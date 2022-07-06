package foxconn.fit.controller.budget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumBudgetVersion;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.base.EnumScenarios;
import foxconn.fit.entity.budget.ActualProductNoUnitCost;
import foxconn.fit.entity.budget.ProductNoUnitCost;
import foxconn.fit.service.budget.ProductNoUnitCostService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/budget/productNoUnitCost")
public class ProductNoUnitCostController extends BaseController {
	private static int PAGE_SIZE=2000;
	
	@Autowired
	private ProductNoUnitCostService productNoUnitCostService;

	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		List<String> list = productNoUnitCostService.listBySql("select distinct product as code from FIT_Product_No_Unit_Cost order by product");
		model.addAttribute("codelist", list);
		List<String> yearsList = productNoUnitCostService.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		model.addAttribute("yearsList", yearsList);
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		model.addAttribute("locale", locale.toString());
		return "/budget/productNoUnitCost/index";
	}

	@RequestMapping(value="/delete")
	@ResponseBody
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,Model model,String id){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "删除成功", "Delete Success"));
		try {
			Assert.hasText(id, getLanguage(locale, "ID不能为空", "Id can not be null"));
			productNoUnitCostService.delete(id);
		} catch (Exception e) {
			logger.error("删除失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", getLanguage(locale, "删除失败", "Delete Fail")+ " : "  + ExceptionUtil.getRootCauseMessage(e));
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
			productNoUnitCostService.deleteVersion(entitys,yr,scenarios);
		} catch (Exception e) {
			logger.error("删除失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", getLanguage(locale, "删除失败", "Delete Fail")+ " : "  + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/list")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,String product,String year,String scenarios,String sbu,String version) {
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			model.addAttribute("locale", locale.toString());
			
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			if (StringUtils.isEmpty(version)) {
				version="V1";
			}
			filters.add(new PropertyFilter("EQS_version",version));
			if (StringUtils.isNotEmpty(year)) {
				String yr="20"+year.substring(2);
				filters.add(new PropertyFilter("EQS_year",yr));
			}
			if (StringUtils.isNotEmpty(scenarios)) {
				filters.add(new PropertyFilter("EQS_scenarios",scenarios));
			}
			if (StringUtils.isNotEmpty(product)) {
				filters.add(new PropertyFilter("EQS_product",product));
			}
			if (StringUtils.isNotEmpty(sbu)) {
				filters.add(new PropertyFilter("EQS_sbu",sbu));
			}else{
				String sbuString="";
				String corporationCode = SecurityUtils.getCorporationCode();
				if (StringUtils.isNotEmpty(corporationCode)) {
					for (String string : corporationCode.split(",")) {
						sbuString+=string+",";
					}
				}
				if (sbuString.endsWith(",")) {
					sbuString=sbuString.substring(0,sbuString.length()-1);
				}
				if (sbuString.indexOf(",") > 0) {
					filters.add(new PropertyFilter("OREQS_sbu",sbuString));
				} else {
					filters.add(new PropertyFilter("EQS_sbu",sbuString));
				}
			}
			
			Page<ProductNoUnitCost> page = productNoUnitCostService.findPageByHQL(pageRequest, filters);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询產品料號單位成本列表失败:", e);
		}
		return "/budget/productNoUnitCost/list";
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "產品料號單位成本-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "SBU") String sbu,@Log(name = "年") String year,@Log(name = "期间") String period,@Log(name = "场景") String scenarios) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "上传成功", "Upload Success"));
		try {
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			Assert.hasText(sbu, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			String yr="20"+year.substring(2);
			Date date = DateUtil.parseByYyyy(yr);
			Assert.notNull(date, getLanguage(locale, "年格式错误", "Error Year Formats"));
//			EnumScenarios.valueOf(scenarios);
			if (EnumScenarios.Actual.getCode().equals(scenarios)&&!"SimpleBudget".equals(scenarios)) {
				Assert.hasText(period, getLanguage(locale, "期间不能为空", "Period can not be null"));
			}
			
			List<String> tarList=new ArrayList<String>();
			String corporationCode = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode)) {
				for (String string : corporationCode.split(",")) {
					tarList.add(string);
				}
			}
			Assert.isTrue(tarList.contains(sbu), getLanguage(locale, "错误的SBU", "SBU is error"));
			
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
				Row firstRow = sheet.getRow(0);
				Assert.notNull(firstRow, getLanguage(locale, "第一行为标题行，不允许为空", "The first row can not empty"));
				int column = firstRow.getPhysicalNumberOfCells();
				if (EnumScenarios.Actual.getCode().equals(scenarios)) {
					int COLUMN_NUM=9;
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
					
					List<ActualProductNoUnitCost> list=new ArrayList<ActualProductNoUnitCost>();
					Map<String,String> entityMap=new HashMap<String, String>();
					Map<String,String> productMap=new HashMap<String, String>();
					for (int i = 1; i < rowNum; i++) {
						Row row = sheet.getRow(i);
						
						if (row==null) {
							continue;
						}
						
						int n=0;
						ActualProductNoUnitCost detail=new ActualProductNoUnitCost();
						String entity = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String product = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						if (StringUtils.isEmpty(entity) || StringUtils.isEmpty(product)) {
							continue;
						}
						entityMap.put(entity, entity);
						productMap.put(product, product);
						
						detail.setEntity(entity);
						detail.setProduct(product);
						detail.setMaterialStandardCost(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						list.add(detail);
					}
					
					if (!list.isEmpty()) {
						Collection<String> entityValues = entityMap.values();
						List<String> entityList = productNoUnitCostService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"'");
						Map<String, String> entityCheckMap=new HashMap<String, String>();
						if (entityList!=null && entityList.size()>0) {
							for (String string : entityList) {
								entityCheckMap.put(string, string);
							}
						}
						List<String> notExistList=new ArrayList<String>();
						for (String industry : entityValues) {
							if (!entityCheckMap.containsKey(industry)) {
								notExistList.add(industry);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【SBU_法人】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						Collection<String> productValues = productMap.values();
						productNoUnitCostService.saveCheckExist(productValues);
//						List<String> partNoList = productNoUnitCostService.listBySql("select distinct value from FIT_CHECK_EXIST c where not exists (select distinct product from (select distinct trim(alias) as product from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' union all select distinct trim(partno_col) as product from bidev.if_bd_fitpartno) b where b.product=c.value)");
						List<String> partNoList = productNoUnitCostService.listBySql("select distinct value from epmods.FIT_CHECK_EXIST c where not exists (select distinct ITEM_CODE from epmods.cux_inv_sbu_item_info_mv m where m.item_code=c.value)");
						if (!partNoList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【產品料號】在【產品BCG映射表】没有找到---------> "+Arrays.toString(partNoList.toArray()));
							return result.getJson();
						}
						productNoUnitCostService.saveActual(list,yr,period,sbu);
					}else{
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "無有效數據行", "Unreceived Valid Row Data"));
					}
				}else if (sheet.getSheetName().equals("簡化版-產品料號單位成本")||column==66) {
					String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(2),0);
					Assert.isTrue(year.substring(2).equals(v_year.substring(2)), getLanguage(locale, "當前文檔年份與選擇年份不匹配", "The current document year does not match the selected year"));
					int COLUMN_NUM=66;
					if(column<COLUMN_NUM){
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "Excel列數不能小於"+COLUMN_NUM, "Number Of Columns Can Not Less Than"+COLUMN_NUM));
						return result.getJson();
					}
					int rowNum = sheet.getPhysicalNumberOfRows();
					if (rowNum<4) {
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "檢測到Excel沒有有效行數據", "Row Data Not Empty"));
						return result.getJson();
					}
					List<ProductNoUnitCost> list=new ArrayList<ProductNoUnitCost>();
					Map<String,String> entityMap=new HashMap<String, String>();
					Map<String,String> productMap=new HashMap<String, String>();
					String yearStr=yr;
					for (int i = 3; i < rowNum; i++) {
						String forecastId= UUID.randomUUID().toString();
						Row row = sheet.getRow(i);
						if (row==null) {
							continue;
						}
						int n=0;
						ProductNoUnitCost detail=new ProductNoUnitCost();
						String entity = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String product = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						if (StringUtils.isEmpty(entity) || StringUtils.isEmpty(product)) {
							continue;
						}
						entityMap.put(entity, entity);
						productMap.put(product, product);
						detail.setMaterialCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setEntity(entity);
						detail.setProduct(product);
						detail.setSbu(sbu);
						detail.setYear(yr);
						detail.setScenarios(scenarios);
						detail.setVersion(EnumBudgetVersion.V00.getCode());
						detail.setForecastId(forecastId);
						for (int j = 1; j < 5; j++) {
							int nn=n+4;
							ProductNoUnitCost detailYear=new ProductNoUnitCost();
							detailYear.setEntity(entity);
							detailYear.setProduct(product);
							detailYear.setSbu(sbu);
							String y=String.valueOf(Integer.parseInt(yr)+j);
							if(i==3){
								yearStr+=","+y;
							}
							detailYear.setYear(y);
							detailYear.setScenarios(scenarios);
							detailYear.setVersion(EnumBudgetVersion.V00.getCode());

							detailYear.setMaterialCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManualCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManufactureCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setUnitCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));

							detailYear.setMaterialCost1(detailYear.getMaterialCostYear());
							detailYear.setManualCost1(detailYear.getManualCostYear());
							detailYear.setManufactureCost1(detailYear.getManufactureCostYear());
							detailYear.setUnitCost1(detailYear.getUnitCostYear());
							detailYear.setForecastId(forecastId);
							list.add(detailYear);
							if(nn==n){
								continue;
							}
						}
						list.add(detail);
					}

					if (!list.isEmpty()) {
						Collection<String> entityValues = entityMap.values();
						List<String> entityList = productNoUnitCostService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"'");
						Map<String, String> entityCheckMap=new HashMap<String, String>();
						if (entityList!=null && entityList.size()>0) {
							for (String string : entityList) {
								entityCheckMap.put(string, string);
							}
						}
						List<String> notExistList=new ArrayList<String>();
						String entityString="";
						for (String entity : entityValues) {
							entityString+="'"+entity+"',";
							if (!entityCheckMap.containsKey(entity)) {
								notExistList.add(entity);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【SBU_法人】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						Collection<String> productValues = productMap.values();
						productNoUnitCostService.saveCheckExist(productValues);
//						List<String> partNoList = productNoUnitCostService.listBySql("select distinct value from FIT_CHECK_EXIST c where not exists (select distinct product from (select distinct trim(alias) as product from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' union all select distinct trim(partno_col) as product from bidev.if_bd_fitpartno) b where b.product=c.value)");
						List<String> partNoList = productNoUnitCostService.listBySql("select distinct value from epmods.FIT_CHECK_EXIST c where not exists (select distinct ITEM_CODE from epmods.cux_inv_sbu_item_info_mv m where m.item_code=c.value)");
						if (!partNoList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【產品料號】在【產品BCG映射表】没有找到---------> "+Arrays.toString(partNoList.toArray()));
							return result.getJson();
						}
						entityString=entityString.substring(0,entityString.length()-1);
						String condition="where year in("+yearStr+") and scenarios='"+scenarios+"' and version='"+EnumBudgetVersion.V00.getCode()+"' and entity in ("+entityString+") and product in (select distinct value from FIT_CHECK_EXIST)";
						productNoUnitCostService.saveBatch(list,condition);
					}else{
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
					}
				}
				else{
					String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(2),0);
					Assert.isTrue(year.substring(2).equals(v_year.substring(2)), getLanguage(locale, "當前文檔年份與選擇年份不匹配","The current document year does not match the selected year"));
					int COLUMN_NUM=242;
					if(column<COLUMN_NUM){
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "Excel列數不能小於"+COLUMN_NUM, "Number Of Columns Can Not Less Than"+COLUMN_NUM));
						return result.getJson();
					}

					int rowNum = sheet.getPhysicalNumberOfRows();
					if (rowNum<4) {
						result.put("flag", "fail");
						result.put("msg", getLanguage(locale, "檢測到Excel沒有有效行數據", "Row Data Not Empty"));
						return result.getJson();
					}
					
					List<ProductNoUnitCost> list=new ArrayList<ProductNoUnitCost>();
					Map<String,String> entityMap=new HashMap<String, String>();
					Map<String,String> productMap=new HashMap<String, String>();
					String yearStr=yr;
					for (int i = 3; i < rowNum; i++) {
						String forecastId= UUID.randomUUID().toString();
						Row row = sheet.getRow(i);
						
						if (row==null) {
							continue;
						}
						
						int n=0;
						ProductNoUnitCost detail=new ProductNoUnitCost();
						String entity = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						String product = ExcelUtil.getCellStringValue(row.getCell(n++),i);
						if (StringUtils.isEmpty(entity) || StringUtils.isEmpty(product)) {
							continue;
						}
						entityMap.put(entity, entity);
						productMap.put(product, product);

						detail.setMaterialStandardCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours1(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours1(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours1(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost1(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours2(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours2(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours2(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost2(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours3(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours3(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours3(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost3(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours4(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours4(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours4(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost4(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours5(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours5(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours5(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost5(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours6(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours6(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours6(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost6(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours7(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours7(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours7(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost7(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours8(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours8(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours8(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost8(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours9(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours9(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours9(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost9(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours10(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours10(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours10(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost10(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours11(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours11(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours11(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost11(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						
						detail.setMaterialStandardCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialAdjustCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setMaterialCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setStandardHours12(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setAdjustHours12(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setHours12(ExcelUtil.getCellStringValue(row.getCell(n++),i));
						detail.setManualStandardRate12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualAdjustRate12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualRate12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManualCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureStandardRate12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureAdjustRate12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureRate12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setManufactureCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
						detail.setUnitCost12(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));

						detail.setEntity(entity);
						detail.setProduct(product);
						detail.setSbu(sbu);
						detail.setYear(yr);
						detail.setScenarios(scenarios);
						detail.setVersion(EnumBudgetVersion.V00.getCode());
						detail.setForecastId(forecastId);

						for (int j = 1; j < 5; j++) {
							int nn=n+15;
							ProductNoUnitCost detailYear=new ProductNoUnitCost();
							detailYear.setEntity(entity);
							detailYear.setProduct(product);
							detailYear.setSbu(sbu);
							String y=String.valueOf(Integer.parseInt(yr)+j);
							if(i==3){
								yearStr+=","+y;
							}
							detailYear.setYear(y);
							detailYear.setScenarios(scenarios);
							detailYear.setVersion(EnumBudgetVersion.V00.getCode());
							detailYear.setMaterialStandardCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setMaterialAdjustCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setMaterialCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setStandardHoursYear(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detailYear.setAdjustHoursYear(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detailYear.setHoursYear(ExcelUtil.getCellStringValue(row.getCell(n++),i));
							detailYear.setManualStandardRateYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManualAdjustRateYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManualRateYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManualCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManufactureStandardRateYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManufactureAdjustRateYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManufactureRateYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setManufactureCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setUnitCostYear(ExcelUtil.getCellStringValueNoRounding(row.getCell(n++),i));
							detailYear.setForecastId(forecastId);

							detailYear.setMaterialStandardCost1(detailYear.getMaterialStandardCostYear());
							detailYear.setMaterialAdjustCost1(detailYear.getMaterialAdjustCostYear());
							detailYear.setMaterialCost1(detailYear.getMaterialCostYear());
							detailYear.setStandardHours1(detailYear.getStandardHoursYear());
							detailYear.setAdjustHours1(detailYear.getAdjustHoursYear());
							detailYear.setHours1(detailYear.getHoursYear());
							detailYear.setManualStandardRate1(detailYear.getManualStandardRateYear());
							detailYear.setManualAdjustRate1(detailYear.getManualAdjustRateYear());
							detailYear.setManualRate1(detailYear.getManualRateYear());
							detailYear.setManualCost1(detailYear.getManualCostYear());
							detailYear.setManufactureStandardRate1(detailYear.getManufactureStandardRateYear());
							detailYear.setManufactureAdjustRate1(detailYear.getManufactureAdjustRateYear());
							detailYear.setManufactureRate1(detailYear.getManufactureRateYear());
							detailYear.setManufactureCost1(detailYear.getManufactureCostYear());
							detailYear.setUnitCost1(detailYear.getUnitCostYear());

							list.add(detailYear);
							if(nn==n){
								continue;
							}
						}
						list.add(detail);
					}
					
					if (!list.isEmpty()) {
						Collection<String> entityValues = entityMap.values();
						List<String> entityList = productNoUnitCostService.listBySql("select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"'");
						Map<String, String> entityCheckMap=new HashMap<String, String>();
						if (entityList!=null && entityList.size()>0) {
							for (String string : entityList) {
								entityCheckMap.put(string, string);
							}
						}
						List<String> notExistList=new ArrayList<String>();
						String entityString="";
						for (String entity : entityValues) {
							entityString+="'"+entity+"',";
							if (!entityCheckMap.containsKey(entity)) {
								notExistList.add(entity);
							}
						}
						if (!notExistList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【SBU_法人】在【維度表】没有找到---------> "+Arrays.toString(notExistList.toArray()));
							return result.getJson();
						}
						
						Collection<String> productValues = productMap.values();
						productNoUnitCostService.saveCheckExist(productValues);
//						List<String> partNoList = productNoUnitCostService.listBySql("select distinct value from FIT_CHECK_EXIST c where not exists (select distinct product from (select distinct trim(alias) as product from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' union all select distinct trim(partno_col) as product from bidev.if_bd_fitpartno) b where b.product=c.value)");
						List<String> partNoList = productNoUnitCostService.listBySql("select distinct value from epmods.FIT_CHECK_EXIST c where not exists (select distinct ITEM_CODE from epmods.cux_inv_sbu_item_info_mv m where m.item_code=c.value)");
						if (!partNoList.isEmpty()) {
							result.put("flag", "fail");
							result.put("msg", "以下【產品料號】在【產品BCG映射表】没有找到---------> "+Arrays.toString(partNoList.toArray()));
							return result.getJson();
						}
						
						entityString=entityString.substring(0,entityString.length()-1);
						String condition="where year in("+yearStr+") and scenarios='"+scenarios+"' and version='"+EnumBudgetVersion.V00.getCode()+"' and entity in ("+entityString+") and product in (select distinct value from FIT_CHECK_EXIST)";
						productNoUnitCostService.saveBatch(list,condition);
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

		return result.getJson();
	}
	
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "產品料號單位成本-->下载")
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
			String realPath = request.getRealPath("");
			if (EnumScenarios.Actual.getCode().equals(scenarios)) {
				String sbu="";
				for (String s : entitys.split(",")) {
					sbu+=s+"|";
				}
				sbu=sbu.substring(0,sbu.length()-1);
				
				Assert.hasText(period, getLanguage(locale, "期间不能为空", "Period can not be null"));
				String sql="select entity,product,material_standard_cost"+period+",standard_hours"+period+",manual_Standard_Rate"+period+",manual_Cost"+period+",manufacture_Standard_Rate"+period+",manufacture_Cost"+period+",unit_Cost"+period+" from FIT_Product_No_Unit_Cost where version='"+version+"' and year='"+yr+"' and scenarios='"+EnumScenarios.Actual.getCode()+"' and REGEXP_LIKE(sbu,'^("+sbu+")') order by product";
				List<Object[]> list = productNoUnitCostService.listBySql(sql);
				
				if (CollectionUtils.isNotEmpty(list)) {
					String EXCEL_NAME="產品料號單位成本實際數";
					if ("en_US".equals(locale.toString())) {
						EXCEL_NAME="Unit Cost of Product_No Actual";
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
			}else{
				String EXCEL_NAME="預算(預測)產品料號單位成本";
				if ("en_US".equals(locale.toString())) {
					EXCEL_NAME="Unit Cost of Product_No";
				}
				String sbu="";
				for (String s : entitys.split(",")) {
					sbu+=s+"|";
				}
				sbu=sbu.substring(0,sbu.length()-1);
				
				String sql="select * from FIT_Product_No_Unit_Cost_v where version='"+version+"' and year='"+yr+"' and scenarios='"+scenarios+"' and REGEXP_LIKE(sbu,'^("+sbu+")') order by product";
				List<Map> list = productNoUnitCostService.listMapBySql(sql);
				
				if (CollectionUtils.isNotEmpty(list)) {
					long time = System.currentTimeMillis();
					int totalPages=(int) Math.ceil((double)list.size()/PAGE_SIZE);
					if (totalPages<=1) {
						File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"budget"+File.separator+EXCEL_NAME+".xlsx");
						XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
						workBook.getSheetAt(1).getRow(0).getCell(0).setCellValue(version);
						Sheet sheet = workBook.getSheetAt(0);
						ExcelUtil.addTitleP(sheet.getRow(0),year);
						for (int i = 0; i < list.size(); i++) {
							Map detail = list.get(i);
							Row row = sheet.getRow(i+3);
							ExcelUtil.setCell(detail, row);
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
								Map detail = list.get(i);
								Row row = sheet.getRow((i-begin)+3);
								ExcelUtil.setCell(detail, row);
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

}
