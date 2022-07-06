package foxconn.fit.controller.budget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import foxconn.fit.entity.base.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.budget.ActualDetailRevenue;
import foxconn.fit.entity.budget.ForecastDetailRevenue;
import foxconn.fit.entity.budget.ForecastDetailRevenueSrc;
import foxconn.fit.service.budget.ForecastDetailRevenueService;
import foxconn.fit.service.budget.ForecastDetailRevenueSrcService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/budget/forecastDetailRevenue")
public class ForecastDetailRevenueController extends BaseController {

	private static int PAGE_SIZE=2000;
	
	@Autowired
	private ForecastDetailRevenueService forecastDetailRevenueService;
	
	@Autowired
	private ForecastDetailRevenueSrcService forecastDetailRevenueSrcService;

	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		List<String> yearsList = forecastDetailRevenueService.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		model.addAttribute("yearsList", yearsList);
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		model.addAttribute("locale", locale.toString());
		return "/budget/forecastDetailRevenue/index";
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,Model model,String id){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "删除成功", "Delete Success"));
		try {
			Assert.hasText(id, getLanguage(locale, "ID不能为空", "Id can not be null"));
			forecastDetailRevenueSrcService.delete(id);
		} catch (Exception e) {
			logger.error("删除失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", getLanguage(locale, "删除失败", "Delete Fail")+ " : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/deleteVersion")
	@ResponseBody
	public String deleteVersion(HttpServletRequest request,AjaxResult ajaxResult,Model model,@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "场景") String scenarios){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "删除成功", "Delete Success"));
		try {
			EnumScenarios.valueOf(scenarios);
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			String yr="20"+year.substring(2);
			Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Date date = DateUtil.parseByYyyy(yr);
			Assert.notNull(date, getLanguage(locale, "年格式错误", "Error Year Formats"));
			if (entitys.endsWith(",")) {
				entitys=entitys.substring(0, entitys.length()-1);
			}
			forecastDetailRevenueSrcService.deleteVersion(entitys,yr,scenarios);
		} catch (Exception e) {
			logger.error("删除失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", getLanguage(locale, "删除失败", "Delete Fail")+ " : "  + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}

	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String entity,String year,String scenarios,String version) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			model.addAttribute("locale", locale.toString());
			String sql="select * from FIT_FORECAST_DETAIL_REV_SRC where 1=1";
			if (StringUtils.isNotEmpty(year)) {
				String yr="20"+year.substring(2);
				Date date = DateUtil.parseByYyyy(yr);
				Assert.notNull(date, getLanguage(locale, "年格式错误", "Error Year Formats"));
				sql+=" and YEAR='"+yr+"'";
			}
			if (StringUtils.isNotEmpty(scenarios)) {
				sql+=" and SCENARIOS='"+scenarios+"'";
			}
			if (StringUtils.isEmpty(version)) {
				version="V1";
			}
			sql+=" and version='"+version+"'";
			
			List<String> tarList=new ArrayList<String>();
			String corporationCode = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode)) {
				for (String sbu : corporationCode.split(",")) {
					tarList.add(sbu);
				}
			}
			
			if (StringUtils.isNotEmpty(entity) && tarList.contains(entity)) {
				sql+=" and ENTITY like '"+entity+"%'";
			}else{
				if (!tarList.isEmpty()) {
					sql+=" and (";
					for (int i = 0; i < tarList.size(); i++) {
						String sbu=tarList.get(i);
						if (i==0) {
							sql+="ENTITY like '"+sbu+"%'";
						}else{
							sql+=" or ENTITY like '"+sbu+"%'";
						}
					}
					sql+=")";
				}
			}
			
			sql+=" order by entity,industry,product,combine,customer,type,activity,scenarios,year";
			Page<Object[]> page = forecastDetailRevenueSrcService.findPageBySql(pageRequest, sql, ForecastDetailRevenueSrc.class);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询預算(預測)營收明細列表失败:", e);
		}
		return "/budget/forecastDetailRevenue/list";
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "預算(預測)營收明細-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "SBU") String[] entitys,@Log(name = "年") String year,@Log(name = "期间") String period,@Log(name = "场景") String scenarios) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "上传成功", "Upload Success"));
		String noInsertEntity="";
		try {
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			String yr="20"+year.substring(2);
			Assert.isTrue(entitys!=null && entitys.length>0, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Date date = DateUtil.parseByYyyy(yr);
			Assert.notNull(date, getLanguage(locale, "年格式错误", "Error Year Formats"));
			EnumScenarios.valueOf(scenarios);
			
			List<String> tarList=new ArrayList<String>();
			String corporationCode = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode)) {
				for (String string : corporationCode.split(",")) {
					tarList.add(string);
				}
			}
			
			List<String> asteriskList=new ArrayList<String>();
			for (String sbu : entitys) {
				if (tarList.contains(sbu)) {
					asteriskList.add(sbu);
				}
			}
			
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
			if (mutipartFiles != null && mutipartFiles.size() > 0) {
				MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];

				String suffix = "";
				if (file.getOriginalFilename().lastIndexOf(".") != -1) {
					suffix = file.getOriginalFilename().substring(
							file.getOriginalFilename().lastIndexOf(".") + 1,
							file.getOriginalFilename().length());
					suffix = suffix.toLowerCase();
				}
				if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
					result.put("flag", "fail");
					result.put("msg", getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
					return result.getJson();
				}

				Workbook wb=null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb=new HSSFWorkbook(file.getInputStream());
				}else {
					//Excel2007
					wb=new XSSFWorkbook(file.getInputStream());
				}
				wb.close();
				
				String version = wb.getSheetAt(1).getRow(0).getCell(0).getStringCellValue();
				Assert.isTrue(EnumBudgetVersion.V00.getCode().equals(version), getLanguage(locale, "版本号错误，只能上传V00版本数据", "The version is error"));
				Sheet sheet = wb.getSheetAt(0);
				if (EnumScenarios.Actual.getCode().equals(scenarios)) {
					int COLUMN_NUM=10;
					Row firstRow = sheet.getRow(0);
					Assert.notNull(firstRow, getLanguage(locale, "第一行为标题行，不允许为空", "The first row can not empty"));
					int column = firstRow.getPhysicalNumberOfCells();
					if(column!=COLUMN_NUM){
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "场景与模板不匹配", "Scenarios do not match the template"));
						return result.getJson();
					}
					int rowNum = sheet.getPhysicalNumberOfRows();
					if (rowNum<2) {
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
						return result.getJson();
					}
					
					List<ActualDetailRevenue> list=new ArrayList<ActualDetailRevenue>();
					Map<String,String> entityMap=new HashMap<String, String>();
					Map<String,String> industryMap=new HashMap<String, String>();
					Map<String,String> productMap=new HashMap<String, String>();
					Map<String,String> combineMap=new HashMap<String, String>();
					Map<String,String> customerMap=new HashMap<String, String>();
					for (int i = 1; i < rowNum; i++) {
						Row row = sheet.getRow(i);
						
						if (row==null) {
							continue;
						}
						
						String entity = ExcelUtil.getCellStringValue(row.getCell(0),i);
						if (StringUtils.isNotEmpty(entity) && startWithByList(entity,asteriskList)) {
							String industry = ExcelUtil.getCellStringValue(row.getCell(1),i);
							String product = ExcelUtil.getCellStringValue(row.getCell(2),i);
							String combine = ExcelUtil.getCellStringValue(row.getCell(3),i);
							String customer = ExcelUtil.getCellStringValue(row.getCell(4),i);
							entityMap.put(entity, entity);
							industryMap.put(industry, industry);
							productMap.put(product, product);
							combineMap.put(combine, combine);
							customerMap.put(customer, customer);
							
							String type = ExcelUtil.getCellStringValue(row.getCell(5),i);
							String currency = ExcelUtil.getCellStringValue(row.getCell(6),i);
							String quantity = ExcelUtil.getCellStringValue(row.getCell(7),i);
							String averageSalesPrice = ExcelUtil.getCellStringValueNoRounding(row.getCell(8),i);
							String revenue = ExcelUtil.getCellStringValueNoRounding(row.getCell(9),i);
							
							ActualDetailRevenue actualDetailRevenue=new ActualDetailRevenue(entity,industry,product,combine,customer,type,currency,EnumBudgetVersion.V00.getCode(),scenarios,yr,quantity,averageSalesPrice,revenue);
							list.add(actualDetailRevenue);
						}
					}
					
					if (!list.isEmpty()) {
						Collection<String> values = entityMap.values();
						List<String> entityList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"'");
						Map<String, String> entityCheckMap=new HashMap<String, String>();
						if (entityList!=null && entityList.size()>0) {
							for (String string : entityList) {
								entityCheckMap.put(string, string);
							}
						}
						List<String> notExistList=new ArrayList<String>();
						for (String entity : values) {
							if (!entityCheckMap.containsKey(entity)) {
								notExistList.add(entity);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【SBU_法人】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						values = industryMap.values();
						List<String> industryList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Segment.getCode()+"'");
						Map<String, String> industryCheckMap=new HashMap<String, String>();
						if (industryList!=null && industryList.size()>0) {
							for (String string : industryList) {
								industryCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String industry : values) {
							if (!industryCheckMap.containsKey(industry)) {
								notExistList.add(industry);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【次產業】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						values = combineMap.values();
						List<String> combineList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Combine.getCode()+"'");
						Map<String, String> combineCheckMap=new HashMap<String, String>();
						if (combineList!=null && combineList.size()>0) {
							for (String string : combineList) {
								combineCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String combine : values) {
							if (!combineCheckMap.containsKey(combine)) {
								notExistList.add(combine);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【最終客戶】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						values = customerMap.values();
						List<String> customerList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Customer.getCode()+"'");
						Map<String, String> customerCheckMap=new HashMap<String, String>();
						if (customerList!=null && customerList.size()>0) {
							for (String string : customerList) {
								customerCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String customer : values) {
							if (!customerCheckMap.containsKey(customer)) {
								notExistList.add(customer);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【賬款客戶】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						//耗时较长，放最后校验
						values = productMap.values();
						forecastDetailRevenueService.saveCheckExist(values);
//						List<String> partNoList = forecastDetailRevenueService.listBySql("select distinct value from FIT_CHECK_EXIST c where not exists (select distinct product from (select distinct trim(alias) as product from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' union all select distinct trim(partno_col) as product from bidev.if_bd_fitpartno) b where b.product=c.value)");
						List<String> partNoList = forecastDetailRevenueService.listBySql("select distinct value from epmods.FIT_CHECK_EXIST c where not exists (select distinct ITEM_CODE from epmods.cux_inv_sbu_item_info_mv m where m.item_code=c.value)");
						if (!partNoList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【產品料號】在【產品BCG映射表】没有找到---------> "+Arrays.toString(partNoList.toArray()));
							return result.getJson();
						}
						
						forecastDetailRevenueService.saveActual(list,period);
					}else{
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
					}
				}else{
					int COLUMN_NUM=199;
					String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(11),0);
					Assert.isTrue(year.substring(2).equals(v_year.substring(2)), getLanguage(locale, "第1行11列值與選擇的年份不匹配！", "The value of row 1,column 11 do not match the selected year"));
					Row thirdRow = sheet.getRow(2);
					int column = thirdRow.getPhysicalNumberOfCells();
					if(column<COLUMN_NUM){
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "Excel列数不能小于"+COLUMN_NUM, "Number Of Columns Can Not Less Than"+COLUMN_NUM));
						return result.getJson();
					}

					int rowNum = sheet.getPhysicalNumberOfRows();
					if (rowNum<4) {
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
						return result.getJson();
					}
					
					List<ForecastDetailRevenue> list=new ArrayList<ForecastDetailRevenue>();
					Map<String,String> entityMap=new HashMap<String, String>();
					Map<String,String> industryMap=new HashMap<String, String>();
					Map<String,String> productMap=new HashMap<String, String>();
					Map<String,String> combineMap=new HashMap<String, String>();
					Map<String,String> customerMap=new HashMap<String, String>();
					Map<String,String> activityMap=new HashMap<String, String>();
					for (int i = 3; i < rowNum; i++) {
						Row row = sheet.getRow(i);
						if (row==null) {
							continue;
						}
						String entity = ExcelUtil.getCellStringValue(row.getCell(0),i);
						if (StringUtils.isNotEmpty(entity) && startWithByList(entity,asteriskList)) {
							String sql="select epmods.fit_forecast_detail_s.nextval as forecastId from dual";
							List<Map> forecastIdMap = forecastDetailRevenueService.listMapBySql(sql);
							int forecastId=Integer.parseInt(forecastIdMap.get(0).get("FORECASTID").toString());
							String makeEntity = ExcelUtil.getCellStringValue(row.getCell(1),i);
							String industry = ExcelUtil.getCellStringValue(row.getCell(2),i);
							String product = ExcelUtil.getCellStringValue(row.getCell(3),i);
							String combine = ExcelUtil.getCellStringValue(row.getCell(4),i);
							String customer = ExcelUtil.getCellStringValue(row.getCell(5),i);
							entityMap.put(entity, entity);
							entityMap.put(makeEntity, makeEntity);
							industryMap.put(industry, industry);
							productMap.put(product, product);
							combineMap.put(combine, combine);
							customerMap.put(customer, customer);
							/**
							 * 交易類型判斷邏輯：
							 * 1. 外售：儅“賬款客戶”為外部客戶（不是FIT體系内法人）
							 * 2. 跨法人跨SBU内交: 儅“賬款客戶”為内部客戶（FIT體系内法人）且“SBU_銷售法人”和“賬款客戶”的法人不同
							 * 3. 同法人跨SBU内交：儅“賬款客戶”為内部客戶（FIT體系内法人）且“SBU_銷售法人”和“賬款客戶”的法人相同
							 */
							String type = ExcelUtil.getCellStringValue(row.getCell(6),i);
							if(!"外售".equals(type)&&!"回銷".equals(type)&&!"同法人跨SBU轉撥".equals(type)
									&&!"跨法人同SBU內交".equals(type)&&!"跨法人跨SBU內交".equals(type)){
								result.put("flag", "fail");
								result.put("msg", "交易類型: "+type+" 不存在!(The transaction type does not exist)");
								return result.getJson();
							}
//							sql="SELECT DIMENSION FROM fit_dimension d WHERE d.type = 'Customer' and ALIAS='"+customer+"'";
//							List<String> dimension=forecastDetailRevenueService.listBySql(sql);
//							if(null!=dimension && dimension.size()>0){
//								sql="select count(1) FROM EPMEBS.cux_cust_rp_mapping ccr WHERE " +
//										"ccr.cust_code='"+dimension.get(0)+"' ";
//								List<Map> maps = forecastDetailRevenueService.listMapBySql(sql);
//								if (maps == null || "0".equals(maps.get(0).get("COUNT(1)").toString())) {
//									type="外售";
//								}
//							}


							String currency = ExcelUtil.getCellStringValue(row.getCell(7),i);
							String activity = ExcelUtil.getCellStringValue(row.getCell(8),i);
							activityMap.put(activity, activity);
							String industryDemandTrend3 = ExcelUtil.getCellStringValue(row.getCell(11),i);
							String industryDemandTrend31 = ExcelUtil.getCellStringValue(row.getCell(12),i);
							String industryDemandTrend32 = ExcelUtil.getCellStringValue(row.getCell(13),i);
							String industryDemandTrend33 = ExcelUtil.getCellStringValue(row.getCell(14),i);
							String industryDemandTrend34 = ExcelUtil.getCellStringValue(row.getCell(15),i);

							String industryDemandTrendServed3 = ExcelUtil.getCellStringValue(row.getCell(18),i);
							String industryDemandTrendServed31 = ExcelUtil.getCellStringValue(row.getCell(19),i);
							String industryDemandTrendServed32 = ExcelUtil.getCellStringValue(row.getCell(20),i);
							String industryDemandTrendServed33 = ExcelUtil.getCellStringValue(row.getCell(21),i);
							String industryDemandTrendServed34 = ExcelUtil.getCellStringValue(row.getCell(22),i);

							String componentUsage3 = ExcelUtil.getCellStringValue(row.getCell(25),i);
							String componentUsage31 = ExcelUtil.getCellStringValue(row.getCell(26),i);
							String componentUsage32 = ExcelUtil.getCellStringValue(row.getCell(27),i);
							String componentUsage33 = ExcelUtil.getCellStringValue(row.getCell(28),i);
							String componentUsage34 = ExcelUtil.getCellStringValue(row.getCell(29),i);

							String averageSalesPrice3 = ExcelUtil.getCellStringValueNoRounding(row.getCell(32),i);
							String averageSalesPrice31 = ExcelUtil.getCellStringValueNoRounding(row.getCell(33),i);
							String averageSalesPrice32 = ExcelUtil.getCellStringValueNoRounding(row.getCell(34),i);
							String averageSalesPrice33 = ExcelUtil.getCellStringValueNoRounding(row.getCell(35),i);
							String averageSalesPrice34 = ExcelUtil.getCellStringValueNoRounding(row.getCell(36),i);

							String totalAvailableMarket3 = ExcelUtil.getCellStringValueNoRounding(row.getCell(39),i);
							String totalAvailableMarket31 = ExcelUtil.getCellStringValueNoRounding(row.getCell(40),i);
							String totalAvailableMarket32 = ExcelUtil.getCellStringValueNoRounding(row.getCell(41),i);
							String totalAvailableMarket33 = ExcelUtil.getCellStringValueNoRounding(row.getCell(42),i);
							String totalAvailableMarket34 = ExcelUtil.getCellStringValueNoRounding(row.getCell(43),i);

							String servedAvailableMarket3 = ExcelUtil.getCellStringValueNoRounding(row.getCell(46),i);
							String servedAvailableMarket31 = ExcelUtil.getCellStringValueNoRounding(row.getCell(47),i);
							String servedAvailableMarket32 = ExcelUtil.getCellStringValueNoRounding(row.getCell(48),i);
							String servedAvailableMarket33 = ExcelUtil.getCellStringValueNoRounding(row.getCell(49),i);
							String servedAvailableMarket34 = ExcelUtil.getCellStringValueNoRounding(row.getCell(50),i);

							String allocation3 = ExcelUtil.getCellStringValue(row.getCell(53),i);
							String allocation31 = ExcelUtil.getCellStringValue(row.getCell(54),i);
							String allocation32 = ExcelUtil.getCellStringValue(row.getCell(55),i);
							String allocation33 = ExcelUtil.getCellStringValue(row.getCell(56),i);
							String allocation34 = ExcelUtil.getCellStringValue(row.getCell(57),i);

							String revenue3 = ExcelUtil.getCellStringValueNoRounding(row.getCell(60),i);
							String revenue31 = ExcelUtil.getCellStringValueNoRounding(row.getCell(61),i);
							String revenue32 = ExcelUtil.getCellStringValueNoRounding(row.getCell(62),i);
							String revenue33 = ExcelUtil.getCellStringValueNoRounding(row.getCell(63),i);
							String revenue34 = ExcelUtil.getCellStringValueNoRounding(row.getCell(64),i);

							String quantity3 = ExcelUtil.getCellStringValue(row.getCell(67),i);
							String quantity31 = ExcelUtil.getCellStringValue(row.getCell(68),i);
							String quantity32 = ExcelUtil.getCellStringValue(row.getCell(69),i);
							String quantity33 = ExcelUtil.getCellStringValue(row.getCell(70),i);
							String quantity34 = ExcelUtil.getCellStringValue(row.getCell(71),i);

							String quantity3_1 = ExcelUtil.getCellStringValue(row.getCell(98),i);
							String quantity3_2 = ExcelUtil.getCellStringValue(row.getCell(99),i);
							String quantity3_3 = ExcelUtil.getCellStringValue(row.getCell(100),i);
							String quantity3_4 = ExcelUtil.getCellStringValue(row.getCell(101),i);
							String quantity3_5 = ExcelUtil.getCellStringValue(row.getCell(102),i);
							String quantity3_6 = ExcelUtil.getCellStringValue(row.getCell(103),i);
							String quantity3_7 = ExcelUtil.getCellStringValue(row.getCell(104),i);
							String quantity3_8 = ExcelUtil.getCellStringValue(row.getCell(105),i);
							String quantity3_9 = ExcelUtil.getCellStringValue(row.getCell(106),i);
							String quantity3_10 = ExcelUtil.getCellStringValue(row.getCell(107),i);
							String quantity3_11 = ExcelUtil.getCellStringValue(row.getCell(108),i);
							String quantity3_12 = ExcelUtil.getCellStringValue(row.getCell(109),i);
							
							String price3_1 = ExcelUtil.getCellStringValueNoRounding(row.getCell(139),i);
							String price3_2 = ExcelUtil.getCellStringValueNoRounding(row.getCell(140),i);
							String price3_3 = ExcelUtil.getCellStringValueNoRounding(row.getCell(141),i);
							String price3_4 = ExcelUtil.getCellStringValueNoRounding(row.getCell(142),i);
							String price3_5 = ExcelUtil.getCellStringValueNoRounding(row.getCell(143),i);
							String price3_6 = ExcelUtil.getCellStringValueNoRounding(row.getCell(144),i);
							String price3_7 = ExcelUtil.getCellStringValueNoRounding(row.getCell(145),i);
							String price3_8 = ExcelUtil.getCellStringValueNoRounding(row.getCell(146),i);
							String price3_9 = ExcelUtil.getCellStringValueNoRounding(row.getCell(147),i);
							String price3_10 = ExcelUtil.getCellStringValueNoRounding(row.getCell(148),i);
							String price3_11 = ExcelUtil.getCellStringValueNoRounding(row.getCell(149),i);
							String price3_12 = ExcelUtil.getCellStringValueNoRounding(row.getCell(150),i);
							
							String revenue3_1 = ExcelUtil.getCellStringValueNoRounding(row.getCell(181),i);
							String revenue3_2 = ExcelUtil.getCellStringValueNoRounding(row.getCell(182),i);
							String revenue3_3 = ExcelUtil.getCellStringValueNoRounding(row.getCell(183),i);
							String revenue3_4 = ExcelUtil.getCellStringValueNoRounding(row.getCell(184),i);
							String revenue3_5 = ExcelUtil.getCellStringValueNoRounding(row.getCell(185),i);
							String revenue3_6 = ExcelUtil.getCellStringValueNoRounding(row.getCell(186),i);
							String revenue3_7 = ExcelUtil.getCellStringValueNoRounding(row.getCell(187),i);
							String revenue3_8 = ExcelUtil.getCellStringValueNoRounding(row.getCell(188),i);
							String revenue3_9 = ExcelUtil.getCellStringValueNoRounding(row.getCell(189),i);
							String revenue3_10 = ExcelUtil.getCellStringValueNoRounding(row.getCell(190),i);
							String revenue3_11 = ExcelUtil.getCellStringValueNoRounding(row.getCell(191),i);
							String revenue3_12 = ExcelUtil.getCellStringValueNoRounding(row.getCell(192),i);
							
							ForecastDetailRevenue forecast3=new ForecastDetailRevenue(entity,makeEntity,industry,product,combine,customer,type,currency,activity,EnumBudgetVersion.V00.getCode(),scenarios,yr,
									industryDemandTrend3,industryDemandTrendServed3,componentUsage3,averageSalesPrice3,totalAvailableMarket3,servedAvailableMarket3,
									allocation3,revenue3,quantity3,quantity3_1,quantity3_2,quantity3_3,quantity3_4,quantity3_5,quantity3_6,quantity3_7,
									quantity3_8,quantity3_9,quantity3_10,quantity3_11,quantity3_12,price3_1,price3_2,price3_3,price3_4,price3_5,price3_6,price3_7,
									price3_8,price3_9,price3_10,price3_11,price3_12,revenue3_1,revenue3_2,revenue3_3,revenue3_4,revenue3_5,
									revenue3_6,revenue3_7,revenue3_8,revenue3_9,revenue3_10,revenue3_11,revenue3_12,forecastId);
							ForecastDetailRevenue forecast31=new ForecastDetailRevenue(entity,makeEntity,industry,product,combine,customer,type,currency,activity,EnumBudgetVersion.V00.getCode(),scenarios,String.valueOf(Integer.parseInt(yr)+1),
									industryDemandTrend31,industryDemandTrendServed31,componentUsage31,averageSalesPrice31,totalAvailableMarket31,servedAvailableMarket31,
									allocation31,revenue31,quantity31,forecastId);
							ForecastDetailRevenue forecast32=new ForecastDetailRevenue(entity,makeEntity,industry,product,combine,customer,type,currency,activity,EnumBudgetVersion.V00.getCode(),scenarios,String.valueOf(Integer.parseInt(yr)+2),
									industryDemandTrend32,industryDemandTrendServed32,componentUsage32,averageSalesPrice32,totalAvailableMarket32,servedAvailableMarket32,
									allocation32,revenue32,quantity32,forecastId);
							ForecastDetailRevenue forecast33=new ForecastDetailRevenue(entity,makeEntity,industry,product,combine,customer,type,currency,activity,EnumBudgetVersion.V00.getCode(),scenarios,String.valueOf(Integer.parseInt(yr)+3),
									industryDemandTrend33,industryDemandTrendServed33,componentUsage33,averageSalesPrice33,totalAvailableMarket33,servedAvailableMarket33,
									allocation33,revenue33,quantity33,forecastId);
							ForecastDetailRevenue forecast34=new ForecastDetailRevenue(entity,makeEntity,industry,product,combine,customer,type,currency,activity,EnumBudgetVersion.V00.getCode(),scenarios,String.valueOf(Integer.parseInt(yr)+4),
									industryDemandTrend34,industryDemandTrendServed34,componentUsage34,averageSalesPrice34,totalAvailableMarket34,servedAvailableMarket34,
									allocation34,revenue34,quantity34,forecastId);
							
							list.add(forecast3);
							list.add(forecast31);
							list.add(forecast32);
							list.add(forecast33);
							list.add(forecast34);
						}else {
							noInsertEntity+=entity+" ";
						}
					}
					
					if (!list.isEmpty()) {
						Collection<String> values = entityMap.values();
						List<String> entityList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"'");
						Map<String, String> entityCheckMap=new HashMap<String, String>();
						if (entityList!=null && entityList.size()>0) {
							for (String string : entityList) {
								entityCheckMap.put(string, string);
							}
						}
						List<String> notExistList=new ArrayList<String>();
						for (String entity : values) {
							if (!entityCheckMap.containsKey(entity)) {
								notExistList.add(entity);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【SBU_法人】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						values = industryMap.values();
						List<String> industryList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Segment.getCode()+"'");
						Map<String, String> industryCheckMap=new HashMap<String, String>();
						if (industryList!=null && industryList.size()>0) {
							for (String string : industryList) {
								industryCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String industry : values) {
							if (!industryCheckMap.containsKey(industry)) {
								notExistList.add(industry);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【次產業】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						values = combineMap.values();
						List<String> combineList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Combine.getCode()+"'");
						Map<String, String> combineCheckMap=new HashMap<String, String>();
						if (combineList!=null && combineList.size()>0) {
							for (String string : combineList) {
								combineCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String combine : values) {
							if (!combineCheckMap.containsKey(combine)) {
								notExistList.add(combine);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【最終客戶】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						values = customerMap.values();
						List<String> customerList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Customer.getCode()+"'");
						Map<String, String> customerCheckMap=new HashMap<String, String>();
						if (customerList!=null && customerList.size()>0) {
							for (String string : customerList) {
								customerCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String customer : values) {
							if (!customerCheckMap.containsKey(customer)&&!"".equalsIgnoreCase(customer)&&customer.length()>0) {
								notExistList.add(customer);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【賬款客戶】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						values = activityMap.values();
						List<String> activityList = forecastDetailRevenueService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Project.getCode()+"'");
						Map<String, String> activityCheckMap=new HashMap<String, String>();
						if (activityList!=null && activityList.size()>0) {
							for (String string : activityList) {
								activityCheckMap.put(string, string);
							}
						}
						notExistList.clear();
						for (String activity : values) {
							if (!activityCheckMap.containsKey(activity)) {
								notExistList.add(activity);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【戰術】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						//耗时较长，放最后校验
						values = productMap.values();
						forecastDetailRevenueService.saveCheckExist(values);
//						List<String> partNoList = forecastDetailRevenueService.listBySql("select distinct value from FIT_CHECK_EXIST c where not exists (select distinct product from (select distinct trim(alias) as product from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' union all select distinct trim(partno_col) as product from bidev.if_bd_fitpartno) b where b.product=c.value)");
						List<String> partNoList = forecastDetailRevenueService.listBySql("select distinct value from epmods.FIT_CHECK_EXIST c where not exists (select distinct ITEM_CODE from epmods.cux_inv_sbu_item_info_mv m where m.item_code=c.value)");
						if (!partNoList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【產品料號】在【產品BCG映射表】没有找到---------> "+Arrays.toString(partNoList.toArray()));
							return result.getJson();
						}
						
						forecastDetailRevenueService.saveBatch(list);
						forecastDetailRevenueService.dataTransfer();
					}else{
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
					}
				}
				
			} else {
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "对不起,未接收到上传的文件", "Unreceived File"));
			}
		} catch (Exception e) {
			logger.error("保存文件失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		if(null!=noInsertEntity&&!"".equalsIgnoreCase(noInsertEntity.trim())){
			result.put("msg", getLanguage(locale, "以下數據未上傳成功，請檢查您是否具備該SBU權限。--------->"+noInsertEntity, "Upload Success"));
		}

		return result.getJson();
	}
	
	private static boolean startWithByList(String sbu,List<String> list){
		for (String prefix : list) {
			if (sbu.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}
	
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "預算(預測)營收明細-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "期间") String period,@Log(name = "场景") String scenarios,@Log(name = "版本") String version){
		try {
			EnumBudgetVersion.valueOf(version);
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			String yr="20"+year.substring(2);
			Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Date date = DateUtil.parseByYyyy(yr);
			Assert.notNull(date, getLanguage(locale, "年格式错误", "Error Year Formats"));
			EnumScenarios.valueOf(scenarios);
			if (entitys.endsWith(",")) {
				entitys=entitys.substring(0, entitys.length()-1);
			}
			
			List<String> tarList=new ArrayList<String>();
			String corporationCode = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode)) {
				for (String string : corporationCode.split(",")) {
					tarList.add(string);
				}
			}
			
			List<String> asteriskList=new ArrayList<String>();
			for (String sbu : entitys.split(",")) {
				if (tarList.contains(sbu)) {
					asteriskList.add(sbu);
				}
			}
			String realPath = request.getRealPath("");
			if (EnumScenarios.Actual.getCode().equals(scenarios)) {
				String entity="";
				for (String e : asteriskList) {
					entity+=e+"|";
				}
				entity=entity.substring(0,entity.length()-1);
				
				Assert.hasText(period, getLanguage(locale, "期间不能为空", "Period can not be null"));
				String sql="select ENTITY,INDUSTRY,PRODUCT,COMBINE,CUSTOMER,TYPE,CURRENCY,QUANTITY_MONTH"+period+",PRICE_MONTH"+period+",REVENUE_MONTH"+period+" from FIT_FORECAST_DETAIL_REV_SRC where version='"+version+"' and year='"+yr+"' and scenarios='"+EnumScenarios.Actual.getCode()+"' and REGEXP_LIKE(ENTITY,'^("+entity+")') order by ENTITY";
				List<Object[]> list = forecastDetailRevenueService.listBySql(sql);
				
				if (CollectionUtils.isNotEmpty(list)) {
					String EXCEL_NAME="營收明細實際數";
					if ("en_US".equals(locale.toString())) {
						EXCEL_NAME="Sales Detail Actual";
					}
					long time = System.currentTimeMillis();
					int totalPages=(int) Math.ceil((double)list.size()/PAGE_SIZE);
					if (totalPages<=1) {
						File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"budget"+File.separator+EXCEL_NAME+".xlsx");
						XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
						workBook.getSheetAt(1).getRow(0).getCell(0).setCellValue(version);
						Sheet sheet = workBook.getSheetAt(0);
						
						for (int i = 0; i < list.size(); i++) {
							Object[] detail = list.get(i);
							Row row = sheet.getRow(i+1);
							row.getCell(0).setCellValue(detail[0]==null?"":detail[0].toString());
							row.getCell(1).setCellValue(detail[1]==null?"":detail[1].toString());
							row.getCell(2).setCellValue(detail[2]==null?"":detail[2].toString());
							row.getCell(3).setCellValue(detail[3]==null?"":detail[3].toString());
							row.getCell(4).setCellValue(detail[4]==null?"":detail[4].toString());
							row.getCell(5).setCellValue(detail[5]==null?"":detail[5].toString());
							row.getCell(6).setCellValue(detail[6]==null?"":detail[6].toString());
							row.getCell(7).setCellValue(detail[7]==null?"":detail[7].toString());
							row.getCell(8).setCellValue(detail[8]==null?"":detail[8].toString());
							row.getCell(9).setCellValue(detail[8]==null?"":detail[9].toString());
						}
						sheet.setForceFormulaRecalculation(true);
						File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".xlsx");
						OutputStream out = new FileOutputStream(outFile);
						workBook.write(out);
						workBook.close();
						out.flush();
						out.close();
						String templateName=version+"_"+EXCEL_NAME+".xlsx";
						result.put("fileName", outFile.getName());
						result.put("templateName", templateName);
					}else {
						List<String> filePaths=new ArrayList<String>();
						for (int p = 0; p < totalPages; p++) {
							int begin=p*PAGE_SIZE;
							int end=(p+1)*PAGE_SIZE;
							if (end>list.size()) {
								end=list.size();
							}
							File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"budget"+File.separator+EXCEL_NAME+".xlsx");
							XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
							workBook.getSheetAt(1).getRow(0).getCell(0).setCellValue(version);
							Sheet sheet = workBook.getSheetAt(0);
							
							for (int i = begin; i < end; i++) {
								Object[] detail = list.get(i);
								Row row = sheet.getRow((i-begin)+1);
								row.getCell(0).setCellValue(detail[0]==null?"":detail[0].toString());
								row.getCell(1).setCellValue(detail[1]==null?"":detail[1].toString());
								row.getCell(2).setCellValue(detail[2]==null?"":detail[2].toString());
								row.getCell(3).setCellValue(detail[3]==null?"":detail[3].toString());
								row.getCell(4).setCellValue(detail[4]==null?"":detail[4].toString());
								row.getCell(5).setCellValue(detail[5]==null?"":detail[5].toString());
								row.getCell(6).setCellValue(detail[6]==null?"":detail[6].toString());
								row.getCell(7).setCellValue(detail[7]==null?"":detail[7].toString());
								row.getCell(8).setCellValue(detail[8]==null?"":detail[8].toString());
								row.getCell(9).setCellValue(detail[8]==null?"":detail[9].toString());
							}
							sheet.setForceFormulaRecalculation(true);
							String filename=realPath+File.separator+"static"+File.separator+"download"+File.separator+(p+1)+"_"+time+".xlsx";
							File outFile=new File(filename);
							filePaths.add(outFile.getAbsolutePath());
							OutputStream out = new FileOutputStream(outFile);
							workBook.write(out);
							workBook.close();
							out.flush();
							out.close();
						}
						System.gc();
						
						File zipFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".zip");
						ZipOutputStream zipOutputStream=new ZipOutputStream(new FileOutputStream(zipFile));
						for (int i = 0; i < filePaths.size(); i++) {
							File file=new File(filePaths.get(i));
							byte[] buf = new byte[1024*4];  
							int len = 0; 
							FileInputStream fis=new FileInputStream(file);
							ZipEntry entry=new ZipEntry(EXCEL_NAME+(i+1)+".xlsx");
							zipOutputStream.putNextEntry(entry);
							while((len=fis.read(buf)) >0){  
								zipOutputStream.write(buf, 0, len);  
							} 
							zipOutputStream.closeEntry();
							fis.close();
							file.delete();
						}
						zipOutputStream.flush();
						zipOutputStream.close();
						String templateName=version+"_"+EXCEL_NAME+".zip";
						result.put("fileName", zipFile.getName());
						result.put("templateName", templateName);
					}
					System.gc();
				}else {
					result.put("flag", "fail");
					result.put("msg", getLanguage(locale, "没有查询到可下载的数据", "No data can be downloaded"));
				}
			}else {
				String EXCEL_NAME="預算(預測)營收明細";
				if ("en_US".equals(locale.toString())) {
					EXCEL_NAME="Sales Detail";
				}

				String sql="select * from FIT_FORECAST_DETAIL_REV_SRC_V where YEAR in ('20"+year.substring(2)+"') and SCENARIOS='"+scenarios+"' and version='"+version+"'";
				if (!asteriskList.isEmpty()) {
					sql+=" and (";
					for (int i = 0; i < asteriskList.size(); i++) {
						String sbu=asteriskList.get(i);
						if (i==0) {
							sql+="ENTITY like '"+sbu+"%'";
						}else{
							sql+=" or ENTITY like '"+sbu+"%'";
						}
					}
					sql+=")";
				}
				sql+=" order by entity,MAKE_ENTITY,industry,product,combine,customer,type,currency,activity,scenarios,year";
				List<Map> list = forecastDetailRevenueService.listMapBySql(sql);
				
				if (CollectionUtils.isNotEmpty(list)) {
					long time = System.currentTimeMillis();
					int totalPages=(int) Math.ceil((double)list.size()/PAGE_SIZE);
					if (totalPages<=1) {
						File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"budget"+File.separator+EXCEL_NAME+".xlsx");
						XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
						workBook.getSheetAt(1).getRow(0).getCell(0).setCellValue(version);
						Sheet sheet = workBook.getSheetAt(0);
						
						ExcelUtil.addTitle(sheet.getRow(0),year);
						for (int i = 0; i < list.size(); i++) {
							Map detail = list.get(i);
							Row row = sheet.getRow(i+3);
							ExcelUtil.createForecastDetailRevenueCell(detail, row);
						}
						sheet.setForceFormulaRecalculation(true);
						File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".xlsx");
						OutputStream out = new FileOutputStream(outFile);
						workBook.write(out);
						workBook.close();
						out.flush();
						out.close();
						String templateName=version+"_"+EXCEL_NAME+".xlsx";
						result.put("fileName", outFile.getName());
						result.put("templateName", templateName);
					}else {
						List<String> filePaths=new ArrayList<String>();
						for (int p = 0; p < totalPages; p++) {
							int begin=p*PAGE_SIZE;
							int end=(p+1)*PAGE_SIZE;
							if (end>list.size()) {
								end=list.size();
							}
							File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"budget"+File.separator+EXCEL_NAME+".xlsx");
							XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
							workBook.getSheetAt(1).getRow(0).getCell(0).setCellValue(version);
							Sheet sheet = workBook.getSheetAt(0);
							
							ExcelUtil.addTitle(sheet.getRow(0),year);
							for (int i = begin; i < end; i++) {
								Map detail = list.get(i);
								Row row = sheet.getRow((i-begin)+3);
								ExcelUtil.createForecastDetailRevenueCell(detail, row);
							}
							sheet.setForceFormulaRecalculation(true);
							String filename=realPath+File.separator+"static"+File.separator+"download"+File.separator+(p+1)+"_"+time+".xlsx";
							File outFile=new File(filename);
							filePaths.add(outFile.getAbsolutePath());
							OutputStream out = new FileOutputStream(outFile);
							workBook.write(out);
							workBook.close();
							out.flush();
							out.close();
						}
						System.gc();
						
						File zipFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".zip");
						ZipOutputStream zipOutputStream=new ZipOutputStream(new FileOutputStream(zipFile));
						for (int i = 0; i < filePaths.size(); i++) {
							File file=new File(filePaths.get(i));
							byte[] buf = new byte[1024*4];  
							int len = 0; 
							FileInputStream fis=new FileInputStream(file);
							ZipEntry entry=new ZipEntry(EXCEL_NAME+(i+1)+".xlsx");
							zipOutputStream.putNextEntry(entry);
							while((len=fis.read(buf)) >0){  
								zipOutputStream.write(buf, 0, len);  
							} 
							zipOutputStream.closeEntry();
							fis.close();
							file.delete();
						}
						zipOutputStream.flush();
						zipOutputStream.close();
						String templateName=version+"_"+EXCEL_NAME+".zip";
						result.put("fileName", zipFile.getName());
						result.put("templateName", templateName);
					}
					System.gc();
				}else {
					result.put("flag", "fail");
					result.put("msg", getLanguage(locale, "没有查询到可下载的数据", "No data can be downloaded"));
				}
			}
			
			
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}

	/**
	 * 下載維度表
	 */
	@RequestMapping(value = "dimension")
	@ResponseBody
	public synchronized String dimension(HttpServletRequest request, HttpServletResponse response, AjaxResult result) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		 Map<String,String> map=forecastDetailRevenueSrcService.dimension(request);
			if(map.get("result")=="Y"){
				result.put("fileName", map.get("str"));
			}else{
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
			}
		return result.getJson();
	}
}
