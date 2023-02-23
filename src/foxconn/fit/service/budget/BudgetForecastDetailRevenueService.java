package foxconn.fit.service.budget;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.BudgetDetailRevenueDao;
import foxconn.fit.dao.budget.ForecastSalesRevenueDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.budget.BudgetDetailRevenue;
import foxconn.fit.entity.budget.ForecastSalesRevenue;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BudgetForecastDetailRevenueService extends BaseService<BudgetDetailRevenue> {

	@Autowired
	private BudgetDetailRevenueDao budgetDetailRevenueDao;

	@Autowired
	private ForecastSalesRevenueDao forecastSalesRevenueDao;

	@Autowired
	private InstrumentClassService instrumentClassService;

	@Autowired
	private ForecastDetailRevenueService forecastDetailRevenueService;


	@Override
	public BaseDaoHibernate<BudgetDetailRevenue> getDao() {
		return budgetDetailRevenueDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = budgetDetailRevenueDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		//預算應爲測試需要先把年份校驗放開
//		int year=calendar.get(Calendar.YEAR)+1;
		int year=calendar.get(Calendar.YEAR);
		//查看當前用戶是否只有查看下載數據權限
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String roleSql="select count(1) from  fit_user u \n" +
				" left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
				" left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
				" WHERE  u.username='"+loginUser.getUsername()+"' and code='salesQuery' ";
		List<BigDecimal> countList = (List<BigDecimal>)budgetDetailRevenueDao.listBySql(roleSql);
		model.addAttribute("onlyQuery", countList.get(0).intValue()>0 ? "Y" : "N");
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		model.addAttribute("versionList", this.versionVal());
		return model;
	}

	public List<String> versionVal(){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select distinct version  from FIT_BUDGET_DETAIL_REVENUE where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version";
		List<String> versionList=budgetDetailRevenueDao.listBySql(sqlVersion);
		return  versionList;
	}

	/**預算頁面查詢*/
	public String budgetList(String year,String version,String entity){
		String sql="select * from FIT_BUDGET_DETAIL_REVENUE_V where 1=1";
		if (null!=year&&StringUtils.isNotEmpty(year)) {
			sql+=" and YEAR='"+year+"'";
		}
		if (null!=version && StringUtils.isNotEmpty(version)) {
			sql+=" and version='"+version+"'";
		}
		String tarList=instrumentClassService.getBudgetSBUStr();
		String sbu="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS,','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+tarList+")";
		List<Map> sbuMap=budgetDetailRevenueDao.listMapBySql(sbu);
		sql+=instrumentClassService.querySbuSql(entity,sbuMap);
		sql+=" order by year,entity,make_entity,ID";
		return sql;
	}

	/**預測頁面查詢*/
	public String forecastList(String year,String version,String entity){
		String sql="select * from FIT_FORECAST_REVENUE_V1 where 1=1";
		if (null!=year&&StringUtils.isNotEmpty(year)) {
			sql+=" and YEAR='"+year+"'";
		}
		if (null!=version && StringUtils.isNotEmpty(version)) {
			sql+=" and version='"+version+"'";
		}
		String tarList=instrumentClassService.getBudgetSBUStr();
		String sbu="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS,','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and  type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+tarList+")";
		List<Map> sbuMap=budgetDetailRevenueDao.listMapBySql(sbu);
		sql+=instrumentClassService.querySbuSql(entity,sbuMap);
		sql+=" order by year,entity,make_entity,ID";
		return sql;
	}


	/**預算數據上傳*/
	public String uploadBudget(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest) {
		try {
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
//			獲取當前用戶的SBU權限
			List<String> tarList = instrumentClassService.getBudgetSBU();
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
					result.put("msg", instrumentClassService.getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
					return result.getJson();
				}
				Workbook wb = null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb = new HSSFWorkbook(file.getInputStream());
				} else {
					//Excel2007
					wb = new XSSFWorkbook(file.getInputStream());
				}
				wb.close();
				Sheet sheet = wb.getSheetAt(0);
				int COLUMN_NUM = 45;
				String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(21), 0);
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
//預算應爲測試需要先把年份校驗放開
// 				Calendar calendar = Calendar.getInstance();
//				String year = Integer.toString(calendar.get(Calendar.YEAR) + 1);
//				Assert.isTrue(year.substring(2).equals(v_year.substring(2)), instrumentClassService.getLanguage(locale, "僅可上傳明年的預算數據！", "Only next year's budget data can be uploaded"));
				int column = sheet.getRow(2).getLastCellNum();
				if (column < COLUMN_NUM) {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "Excel列数不能小于" + COLUMN_NUM + "，請下載正確的模板上傳數據！", "Number Of Columns Can Not Less Than" + COLUMN_NUM + ",Please download the correct template to upload the data"));
					return result.getJson();
				}
				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum < 4) {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
					return result.getJson();
				}
				List<BudgetDetailRevenue> list = new ArrayList<>();
				/**SBU法人**/List<String> entityList = new ArrayList<>();
				/**SBU**/List<String> sbuList = new ArrayList<>();
				/**次產業**/List<String> industryList = new ArrayList<>();
				/**主營業務*/List<String> mainBusinessList = new ArrayList<>();
				/**3+3**/List<String> threeList = new ArrayList<>();
				/**產品系列**/List<String> productSeriesList = new ArrayList<>();
				/**產品料號**/List<String> productNoList = new ArrayList<>();
				/**賬款客戶**/List<String> loanCustomerList = new ArrayList<>();
				/**最終客戶**/List<String> endCustomerList = new ArrayList<>();
				/**交易類型**/List<String> tradeTypeList = new ArrayList<>();
				/**報告幣種**/List<String> currencyList = new ArrayList<>();
				List<String> entityMakeList=new ArrayList<>();
				String check = "";
				String productChek="";
				for (int i = 3; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(0), i);
					if(row == null||entity.length()<1||"".equals(entity)){
						continue;
					}
					String sql="select distinct PARENT from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"' and ALIAS='"+entity+"'";
					List<String> listSbu=budgetDetailRevenueDao.listBySql(sql);
					sbuList.addAll(listSbu);
					check = instrumentClassService.getDiffrent(listSbu, tarList);
					if (row == null|| !"".equalsIgnoreCase(check.trim()) || check.length() > 0) {
						continue;
					}
					if(ExcelUtil.getCellStringValue(row.getCell(5), i).isEmpty()
							&&ExcelUtil.getCellStringValue(row.getCell(6), i).isEmpty()){
						productChek+=i+"、";
						continue;
					}
					entityList.add(entity);
					entityMakeList.add(ExcelUtil.getCellStringValue(row.getCell(1), i));
					industryList.add(ExcelUtil.getCellStringValue(row.getCell(2), i));
					mainBusinessList.add(ExcelUtil.getCellStringValue(row.getCell(3), i));
					threeList.add(ExcelUtil.getCellStringValue(row.getCell(4), i));
					if(!ExcelUtil.getCellStringValue(row.getCell(5), i).isEmpty()){
						productSeriesList.add(ExcelUtil.getCellStringValue(row.getCell(5), i));
					}
					if(!ExcelUtil.getCellStringValue(row.getCell(6), i).isEmpty()){
						productNoList.add(ExcelUtil.getCellStringValue(row.getCell(6), i));
					}
					endCustomerList.add(ExcelUtil.getCellStringValue(row.getCell(7), i));
					loanCustomerList.add(ExcelUtil.getCellStringValue(row.getCell(8), i));
					tradeTypeList.add(ExcelUtil.getCellStringValue(row.getCell(9), i));
					currencyList.add(ExcelUtil.getCellStringValue(row.getCell(10), i));

					BudgetDetailRevenue budgetDetailRevenue = new BudgetDetailRevenue();
					budgetDetailRevenue.setEntity(entity);
					budgetDetailRevenue.setMakeEntity(ExcelUtil.getCellStringValue(row.getCell(1), i));
					budgetDetailRevenue.setIndustry(ExcelUtil.getCellStringValue(row.getCell(2), i));
					budgetDetailRevenue.setmainBusiness(ExcelUtil.getCellStringValue(row.getCell(3), i));
					budgetDetailRevenue.setThree(ExcelUtil.getCellStringValue(row.getCell(4), i));
					budgetDetailRevenue.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(5), i));
					budgetDetailRevenue.setProductNo(ExcelUtil.getCellStringValue(row.getCell(6), i));
					budgetDetailRevenue.setEndCustomer(ExcelUtil.getCellStringValue(row.getCell(7), i));
					budgetDetailRevenue.setLoanCustomer(ExcelUtil.getCellStringValue(row.getCell(8), i));
					budgetDetailRevenue.setTradeType(ExcelUtil.getCellStringValue(row.getCell(9), i));
					budgetDetailRevenue.setCurrency(ExcelUtil.getCellStringValue(row.getCell(10), i));
					budgetDetailRevenue.setTypeOfAirplane(ExcelUtil.getCellStringValue(row.getCell(11), i));
					budgetDetailRevenue.setPm(ExcelUtil.getCellStringValue(row.getCell(12), i));
					budgetDetailRevenue.setRevenueNextyear(ExcelUtil.getCellStringValue(row.getCell(13), i));
					budgetDetailRevenue.setRevenueTwoyear(ExcelUtil.getCellStringValue(row.getCell(14), i));
					budgetDetailRevenue.setRevenueThreeyear(ExcelUtil.getCellStringValue(row.getCell(15), i));
					budgetDetailRevenue.setRevenueFouryear(ExcelUtil.getCellStringValue(row.getCell(16), i));

					budgetDetailRevenue.setQuantityNextyear(ExcelUtil.getCellStringValue(row.getCell(17), i));
					budgetDetailRevenue.setQuantityTwoyear(ExcelUtil.getCellStringValue(row.getCell(18), i));
					budgetDetailRevenue.setQuantityThreeyear(ExcelUtil.getCellStringValue(row.getCell(19), i));
					budgetDetailRevenue.setQuantityFouryear(ExcelUtil.getCellStringValue(row.getCell(20), i));
					budgetDetailRevenue.setQuantityMonth1(ExcelUtil.getCellStringValue(row.getCell(21), i));
					budgetDetailRevenue.setQuantityMonth2(ExcelUtil.getCellStringValue(row.getCell(22), i));
					budgetDetailRevenue.setQuantityMonth3(ExcelUtil.getCellStringValue(row.getCell(23), i));
					budgetDetailRevenue.setQuantityMonth4(ExcelUtil.getCellStringValue(row.getCell(24), i));
					budgetDetailRevenue.setQuantityMonth5(ExcelUtil.getCellStringValue(row.getCell(25), i));
					budgetDetailRevenue.setQuantityMonth6(ExcelUtil.getCellStringValue(row.getCell(26), i));
					budgetDetailRevenue.setQuantityMonth7(ExcelUtil.getCellStringValue(row.getCell(27), i));
					budgetDetailRevenue.setQuantityMonth8(ExcelUtil.getCellStringValue(row.getCell(28), i));
					budgetDetailRevenue.setQuantityMonth9(ExcelUtil.getCellStringValue(row.getCell(29), i));
					budgetDetailRevenue.setQuantityMonth10(ExcelUtil.getCellStringValue(row.getCell(30), i));
					budgetDetailRevenue.setQuantityMonth11(ExcelUtil.getCellStringValue(row.getCell(31), i));
					budgetDetailRevenue.setQuantityMonth12(ExcelUtil.getCellStringValue(row.getCell(32), i));

					budgetDetailRevenue.setPriceMonth1(ExcelUtil.getCellStringValue(row.getCell(33), i));
					budgetDetailRevenue.setPriceMonth2(ExcelUtil.getCellStringValue(row.getCell(34), i));
					budgetDetailRevenue.setPriceMonth3(ExcelUtil.getCellStringValue(row.getCell(35), i));
					budgetDetailRevenue.setPriceMonth4(ExcelUtil.getCellStringValue(row.getCell(36), i));
					budgetDetailRevenue.setPriceMonth5(ExcelUtil.getCellStringValue(row.getCell(37), i));
					budgetDetailRevenue.setPriceMonth6(ExcelUtil.getCellStringValue(row.getCell(38), i));
					budgetDetailRevenue.setPriceMonth7(ExcelUtil.getCellStringValue(row.getCell(39), i));
					budgetDetailRevenue.setPriceMonth8(ExcelUtil.getCellStringValue(row.getCell(40), i));
					budgetDetailRevenue.setPriceMonth9(ExcelUtil.getCellStringValue(row.getCell(41), i));
					budgetDetailRevenue.setPriceMonth10(ExcelUtil.getCellStringValue(row.getCell(42), i));
					budgetDetailRevenue.setPriceMonth11(ExcelUtil.getCellStringValue(row.getCell(43), i));
					budgetDetailRevenue.setPriceMonth12(ExcelUtil.getCellStringValue(row.getCell(44), i));
					budgetDetailRevenue.setYear(v_year);
					budgetDetailRevenue.setVersion("V00");
					budgetDetailRevenue.setId(UUID.randomUUID().toString());
					budgetDetailRevenue.setCreateName(loginUser.getUsername());
					budgetDetailRevenue.setCreateDate(new Date());
					list.add(budgetDetailRevenue);
				}
				if (!list.isEmpty()) {
					entityMakeList.addAll(entityList);
					result=this.checkMainData(result,entityMakeList,industryList,mainBusinessList,threeList,productSeriesList,productNoList,
							loanCustomerList,endCustomerList,tradeTypeList,currencyList);
					if ("fail".equals(result.getResult().get("flag"))){
						return result.getJson();
					}
					this.saveBatch(list,v_year,loginUser.getUsername());
				} else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
				}
				sbuList=instrumentClassService.removeDuplicate(sbuList);
				check = instrumentClassService.getDiffrent(sbuList, tarList);
				if (!"".equalsIgnoreCase(check.trim()) && check.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下數據未上傳成功，請檢查您是否具備該SBU權限。--------->" + check, "The following data fails to be uploaded. Check whether you have the SBU permission--------->" + check));
				}
				if (!"".equalsIgnoreCase(productChek.trim()) && productChek.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下行數據未上傳成功，產品系列和產品料號必填其一。--------->" + productChek.substring(0,productChek.length()-1), "The following lines of data have not been uploaded successfully, one of the product series and product number is required--------->" + productChek.substring(0,productChek.length()-1)));
				}
			} else {
				result.put("flag", "fail");
				result.put("msg", instrumentClassService.getLanguage(locale, "對不起，未接受到上傳的文件", "Unreceived File"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	/**預測數據上傳*/
	public String uploadForecast(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest) {
		try {
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
//			獲取當前用戶的SBU權限
			List<String> tarList = instrumentClassService.getBudgetSBU();
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
					result.put("msg", instrumentClassService.getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
					return result.getJson();
				}
				Workbook wb = null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb = new HSSFWorkbook(file.getInputStream());
				} else {
					//Excel2007
					wb = new XSSFWorkbook(file.getInputStream());
				}
				wb.close();
				Sheet sheet = wb.getSheetAt(0);
				int COLUMN_NUM = 37;
				String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(13), 0);
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
				Calendar calendar = Calendar.getInstance();
				String year = Integer.toString(calendar.get(Calendar.YEAR));
				Assert.isTrue(year.substring(2).equals(v_year.substring(2)), instrumentClassService.getLanguage(locale, "僅可上傳當前年份的預測數據！", "Only the forecast data of the current year can be uploaded"));
				int column = sheet.getRow(2).getLastCellNum();
				if (column < COLUMN_NUM) {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "Excel列数不能小于" + COLUMN_NUM + "，請下載正確的模板上傳數據！", "Number Of Columns Can Not Less Than" + COLUMN_NUM + ",Please download the correct template to upload the data"));
					return result.getJson();
				}
				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum < 4) {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
					return result.getJson();
				}
				List<ForecastSalesRevenue> list = new ArrayList<>();
				/**SBU法人**/List<String> entityList = new ArrayList<>();
				/**SBU**/List<String> sbuList = new ArrayList<>();
				/**次產業**/List<String> industryList = new ArrayList<>();
				/**主營業務*/List<String> mainBusinessList = new ArrayList<>();
				/**3+3**/List<String> threeList = new ArrayList<>();
				/**產品系列**/List<String> productSeriesList = new ArrayList<>();
				/**產品料號**/List<String> productNoList = new ArrayList<>();
				/**賬款客戶**/List<String> loanCustomerList = new ArrayList<>();
				/**最終客戶**/List<String> endCustomerList = new ArrayList<>();
				/**交易類型**/List<String> tradeTypeList = new ArrayList<>();
				/**報告幣種**/List<String> currencyList = new ArrayList<>();
				List<String> entityMakeList=new ArrayList<>();
				String check = "";
				for (int i = 3; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(0), i);
					if(row == null||entity.length()<1||"".equals(entity)){
						continue;
					}
					String sql="select distinct PARENT from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"' and ALIAS='"+entity+"'";
					List<String> listSbu=budgetDetailRevenueDao.listBySql(sql);
					sbuList.addAll(listSbu);
					check = instrumentClassService.getDiffrent(listSbu, tarList);
					if (row == null|| !"".equalsIgnoreCase(check.trim()) || check.length() > 0) {
						continue;
					}
					entityList.add(entity);
					entityMakeList.add(ExcelUtil.getCellStringValue(row.getCell(1), i));
					industryList.add(ExcelUtil.getCellStringValue(row.getCell(2), i));
					mainBusinessList.add(ExcelUtil.getCellStringValue(row.getCell(3), i));
					threeList.add(ExcelUtil.getCellStringValue(row.getCell(4), i));
					productSeriesList.add(ExcelUtil.getCellStringValue(row.getCell(5), i));
					productNoList.add(ExcelUtil.getCellStringValue(row.getCell(6), i));
					endCustomerList.add(ExcelUtil.getCellStringValue(row.getCell(7), i));
					loanCustomerList.add(ExcelUtil.getCellStringValue(row.getCell(8), i));
					tradeTypeList.add(ExcelUtil.getCellStringValue(row.getCell(9), i));
					currencyList.add(ExcelUtil.getCellStringValue(row.getCell(10), i));

					ForecastSalesRevenue forecastSalesRevenue = new ForecastSalesRevenue();
					forecastSalesRevenue.setEntity(entity);
					forecastSalesRevenue.setMakeEntity(ExcelUtil.getCellStringValue(row.getCell(1), i));
					forecastSalesRevenue.setIndustry(ExcelUtil.getCellStringValue(row.getCell(2), i));
					forecastSalesRevenue.setmainBusiness(ExcelUtil.getCellStringValue(row.getCell(3), i));
					forecastSalesRevenue.setThree(ExcelUtil.getCellStringValue(row.getCell(4), i));
					forecastSalesRevenue.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(5), i));
					forecastSalesRevenue.setProductNo(ExcelUtil.getCellStringValue(row.getCell(6), i));
					forecastSalesRevenue.setEndCustomer(ExcelUtil.getCellStringValue(row.getCell(7), i));
					forecastSalesRevenue.setLoanCustomer(ExcelUtil.getCellStringValue(row.getCell(8), i));
					forecastSalesRevenue.setTradeType(ExcelUtil.getCellStringValue(row.getCell(9), i));
					forecastSalesRevenue.setCurrency(ExcelUtil.getCellStringValue(row.getCell(10), i));
					forecastSalesRevenue.setTypeOfAirplane(ExcelUtil.getCellStringValue(row.getCell(11), i));
					forecastSalesRevenue.setPm(ExcelUtil.getCellStringValue(row.getCell(12), i));

					forecastSalesRevenue.setQuantityMonth1(ExcelUtil.getCellStringValue(row.getCell(13), i));
					forecastSalesRevenue.setQuantityMonth2(ExcelUtil.getCellStringValue(row.getCell(14), i));
					forecastSalesRevenue.setQuantityMonth3(ExcelUtil.getCellStringValue(row.getCell(15), i));
					forecastSalesRevenue.setQuantityMonth4(ExcelUtil.getCellStringValue(row.getCell(16), i));
					forecastSalesRevenue.setQuantityMonth5(ExcelUtil.getCellStringValue(row.getCell(17), i));
					forecastSalesRevenue.setQuantityMonth6(ExcelUtil.getCellStringValue(row.getCell(18), i));
					forecastSalesRevenue.setQuantityMonth7(ExcelUtil.getCellStringValue(row.getCell(19), i));
					forecastSalesRevenue.setQuantityMonth8(ExcelUtil.getCellStringValue(row.getCell(20), i));
					forecastSalesRevenue.setQuantityMonth9(ExcelUtil.getCellStringValue(row.getCell(21), i));
					forecastSalesRevenue.setQuantityMonth10(ExcelUtil.getCellStringValue(row.getCell(22), i));
					forecastSalesRevenue.setQuantityMonth11(ExcelUtil.getCellStringValue(row.getCell(23), i));
					forecastSalesRevenue.setQuantityMonth12(ExcelUtil.getCellStringValue(row.getCell(24), i));

					forecastSalesRevenue.setPriceMonth1(ExcelUtil.getCellStringValue(row.getCell(25), i));
					forecastSalesRevenue.setPriceMonth2(ExcelUtil.getCellStringValue(row.getCell(26), i));
					forecastSalesRevenue.setPriceMonth3(ExcelUtil.getCellStringValue(row.getCell(27), i));
					forecastSalesRevenue.setPriceMonth4(ExcelUtil.getCellStringValue(row.getCell(28), i));
					forecastSalesRevenue.setPriceMonth5(ExcelUtil.getCellStringValue(row.getCell(29), i));
					forecastSalesRevenue.setPriceMonth6(ExcelUtil.getCellStringValue(row.getCell(30), i));
					forecastSalesRevenue.setPriceMonth7(ExcelUtil.getCellStringValue(row.getCell(31), i));
					forecastSalesRevenue.setPriceMonth8(ExcelUtil.getCellStringValue(row.getCell(32), i));
					forecastSalesRevenue.setPriceMonth9(ExcelUtil.getCellStringValue(row.getCell(33), i));
					forecastSalesRevenue.setPriceMonth10(ExcelUtil.getCellStringValue(row.getCell(34), i));
					forecastSalesRevenue.setPriceMonth11(ExcelUtil.getCellStringValue(row.getCell(35), i));
					forecastSalesRevenue.setPriceMonth12(ExcelUtil.getCellStringValue(row.getCell(36), i));
					forecastSalesRevenue.setYear(v_year);
					forecastSalesRevenue.setVersion("V00");
					forecastSalesRevenue.setId(UUID.randomUUID().toString());
					forecastSalesRevenue.setCreateName(loginUser.getUsername());
					forecastSalesRevenue.setCreateDate(new Date());
					list.add(forecastSalesRevenue);
				}
				if (!list.isEmpty()) {
					entityMakeList.addAll(entityList);
					result=this.checkMainData(result,entityMakeList,industryList,mainBusinessList,threeList,productSeriesList,productNoList,
							loanCustomerList,endCustomerList,tradeTypeList,currencyList);
					if ("fail".equals(result.getResult().get("flag"))){
						return result.getJson();
					}
					this.saveBatchForecast(list,v_year,loginUser.getUsername());
				} else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
					return result.getJson();
				}
				check = instrumentClassService.getDiffrent(sbuList, tarList);
				if (!"".equalsIgnoreCase(check.trim()) && check.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下數據未上傳成功，請檢查您是否具備該SBU權限。--------->" + check, "The following data fails to be uploaded. Check whether you have the SBU permission--------->" + check));
				}
			} else {
				result.put("flag", "fail");
				result.put("msg", instrumentClassService.getLanguage(locale, "對不起，未接受到上傳的文件", "Unreceived File"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	private AjaxResult checkMainData(AjaxResult result,List<String> entityMakeList,List<String> industryList,List<String> mainBusinessList,
	List<String> threeList,List<String> productSeriesList,List<String> productNoList,List<String> loanCustomerList,List<String> endCustomerList,
									 List<String> tradeTypeList,List<String> currencyList	 ) throws Exception {
		String check="";
		/**SBU_法人校驗*/
		String sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"' and DIMENSION not in('ABS_A084002')";
		check=this.check(entityMakeList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【SBU_銷售法人】或【SBU_製造法人】在【維度表】没有找到---> " + check);
			return result;
		}
		/**次產業校驗*/
		sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Segment.getCode() +"' and PARENT like 'SE_%'  or DIMENSION='S00' ";
		check=this.check(industryList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【次產業】在【維度表】没有找到---> "+check);
			return result;
		}
		/**主營業務*/
		/**5GAIOT\EV\AUDIO\Type C\Existing*/
		sql="select distinct trim(alias) from fit_dimension where type='Bak2' and PARENT='bak201' ";
		check=this.check(mainBusinessList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【Main Business】在【維度表】没有找到---> "+check);
			return result;
		}
		/**3+3**/
		sql="select distinct trim(alias) from fit_dimension where type='Project'  and PARENT='P_FIT3+3'";
		check=this.check(threeList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【3+3】在【維度表】没有找到---> "+check);
			return result;
		}
		/**產品系列**/
		sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Product.getCode() +"' ";
		check=this.check(productSeriesList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【產品系列】在【維度表】没有找到---> "+check);
			return result;
		}
		/**賬款客戶**/
		sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Customer.getCode() +"'  and PARENT in('Customer_Total','HT_ICP') and  DIMENSION <> 'HT_ICP' ";
		check=this.check(loanCustomerList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【賬款客戶】在【維度表】没有找到---> "+check);
			return result;
		}
		/**最終客戶**/
		sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Combine.getCode() +"' and PARENT in('C_End Customer') ";
		check=this.check(endCustomerList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【最終客戶】在【維度表】没有找到---> "+check);
			return result;
		}
		/**交易類型**/
		sql="select distinct trim(alias) from fit_dimension where type='View' and PARENT in('Int000') and DIMENSION not in('Int005','Int006')";
		check=this.check(tradeTypeList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【交易類型】在【維度表】没有找到---> "+check);
			return result;
		}
		/**報告幣種**/
		sql="select distinct trim(alias) from fit_dimension where type='Currency' and PARENT ='O_Currency'";
		check=this.check(currencyList,sql);
		if (!check.equals("") && check.length() > 0){
			result.put("flag", "fail");
			result.put("msg", "以下【報告幣種】在【維度表】没有找到---> "+check);
			return result;
		}
		/**產品料號*/
		productNoList = instrumentClassService.removeDuplicate(productNoList);
		forecastDetailRevenueService.saveCheckExist(productNoList);
		List<String> partNoList = forecastDetailRevenueService.listBySql("select distinct value from epmods.FIT_CHECK_EXIST c where not exists (select distinct product from (\n" +
				"select distinct trim(alias) as product from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' \n" +
				"union all\n" +
				"select distinct trim(ITEM_CODE) as product from epmods.cux_inv_sbu_item_info_mv\n" +
				") b where b.product=c.value)");
		if (!partNoList.isEmpty()) {
			result.put("flag", "fail");
			result.put("msg", "以下【產品料號】在【產品BCG映射表】没有找到---------> "+Arrays.toString(partNoList.toArray()));
			return result;
		}
		return result;
	}

	/**匹配用戶上傳的主數據list是否在維度表中能找到*/
	public String check(List<String> list,String sql){
		list = instrumentClassService.removeDuplicate(list);
		List<String> checkList = this.listBySql(sql);
		String check = instrumentClassService.getDiffrent(list, checkList);
		return check;
	}

	/**預算保存數據*/
	public void saveBatch(List<BudgetDetailRevenue> list,String year,String userName) throws Exception {
		String sql="delete from FIT_BUDGET_DETAIL_REVENUE where VERSION='V00' and YEAR='"+year+"' and CREATE_NAME ='"+userName+"'";
		budgetDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			budgetDetailRevenueDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				budgetDetailRevenueDao.getHibernateTemplate().flush();
				budgetDetailRevenueDao.getHibernateTemplate().clear();
			}
		}
	}

	/**預測保存數據*/
	public void saveBatchForecast(List<ForecastSalesRevenue> list,String year,String userName) throws Exception {
		String sql="delete from FIT_FORECAST_REVENUE where VERSION='V00' and YEAR='"+year+"' and CREATE_NAME ='"+userName+"'";
		forecastSalesRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			forecastSalesRevenueDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				forecastSalesRevenueDao.getHibernateTemplate().flush();
				forecastSalesRevenueDao.getHibernateTemplate().clear();
			}
		}
	}

	/**下載模板*/
	public Map<String,String>  template(HttpServletRequest request,String type) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			if(type.equals("budget")){
				String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預算表","銷售收入預算表")+".xlsx";
				InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預算表","銷售收入預算表")+".xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(ins);
				Sheet sheet = workBook.getSheetAt(0);
				Calendar calendar = Calendar.getInstance();
				Row row =sheet.getRow(0);
				//預算應爲測試需要先把年份校驗放開
//				int year=calendar.get(Calendar.YEAR);
				int year=calendar.get(Calendar.YEAR)-1;
				row.getCell(13).setCellValue("FY"+ String.valueOf(year+2).substring(2));
				row.getCell(14).setCellValue("FY"+ String.valueOf(year+3).substring(2));
				row.getCell(15).setCellValue("FY"+ String.valueOf(year+4).substring(2));
				row.getCell(16).setCellValue("FY"+ String.valueOf(year+5).substring(2));
				row.getCell(17).setCellValue("FY"+ String.valueOf(year+2).substring(2));
				row.getCell(18).setCellValue("FY"+ String.valueOf(year+3).substring(2));
				row.getCell(19).setCellValue("FY"+ String.valueOf(year+4).substring(2));
				row.getCell(20).setCellValue("FY"+ String.valueOf(year+5).substring(2));
				row.getCell(21).setCellValue("FY"+ String.valueOf(year+1).substring(2));
				row.getCell(33).setCellValue("FY"+ String.valueOf(year+1).substring(2));
				File outFile = new File(filePath);
				OutputStream out = new FileOutputStream(outFile);
				workBook.write(out);
				workBook.close();
				out.flush();
				out.close();
				mapResult.put("file",outFile.getName());
			}else{
				String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表","銷售收入預測表")+".xlsx";
				InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表","銷售收入預測表")+".xlsx");
				XSSFWorkbook workBook = new XSSFWorkbook(ins);
				Sheet sheet = workBook.getSheetAt(0);
				Calendar calendar = Calendar.getInstance();
				Row row =sheet.getRow(0);
				int year=calendar.get(Calendar.YEAR);
				row.getCell(13).setCellValue("FY"+ String.valueOf(year).substring(2));
				File outFile = new File(filePath);
				OutputStream out = new FileOutputStream(outFile);
				workBook.write(out);
				workBook.close();
				out.flush();
				out.close();
				mapResult.put("file",outFile.getName());
			}
			System.gc();
		}catch (Exception e){
			e.printStackTrace();
			mapResult.put("result","N");
			mapResult.put("str",ExceptionUtil.getRootCauseMessage(e));
		}
		return mapResult;
	}

	/**預算下載數據*/
	public Map<String,String>  downloadBudget(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預算表","銷售收入預算表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預算表_下载","銷售收入預算表_下载")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(15).setCellValue(y);
			row.getCell(16).setCellValue("FY"+(year+1));
			row.getCell(17).setCellValue("FY"+(year+2));
			row.getCell(18).setCellValue("FY"+(year+3));
			row.getCell(19).setCellValue("FY"+(year+4));
			row.getCell(20).setCellValue(y);
			row.getCell(21).setCellValue("FY"+(year+1));
			row.getCell(22).setCellValue("FY"+(year+2));
			row.getCell(23).setCellValue("FY"+(year+3));
			row.getCell(24).setCellValue("FY"+(year+4));
			row.getCell(25).setCellValue(y);
			row.getCell(37).setCellValue(y);
			row.getCell(49).setCellValue(y);

			String sql="select * from FIT_BUDGET_DETAIL_REVENUE_V where YEAR='"+y+"'";
			if (null!=version && StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			String sbuVal="";
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+sbuStr+")";
			List<Map> sbuMap=budgetDetailRevenueDao.listMapBySql(sbusql);
			String entityStr=","+entitys+",";
			for (Map map:sbuMap){
				if(entityStr.indexOf(map.get("PARENT").toString())!=-1){
					sbuVal+=" ENTITY like '"+map.get("ALIAS").toString()+"_%' or";
				}
			}
			if(!sbuVal.isEmpty()){
				sql+=" and ("+sbuVal.substring(0,sbuVal.length()-2)+")";
			}
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+="order by entity,PRODUCT_SERIES,id";
			List<Object[]> dataList = budgetDetailRevenueDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 0; i < objects.length; i++) {
						if(i==62){
							break;
						}
						Cell cell = contentRow.createCell(i);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>14 && i<62) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = budgetDetailRevenueDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 0; i < objects.length; i++) {
								if(i==62){
									break;
								}
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>14 && i<62) {
									cell.setCellValue(Double.parseDouble(text));
								} else {
									cell.setCellValue(text);
								}
							}
						}
					}
				}
			}
			File outFile = new File(filePath);
			OutputStream out = new FileOutputStream(outFile);
			workBook.write(out);
			workBook.close();
			out.flush();
			out.close();
			mapResult.put("file",outFile.getName());
			System.gc();
		}catch (Exception e){
			e.printStackTrace();
			mapResult.put("result","N");
			mapResult.put("str",ExceptionUtil.getRootCauseMessage(e));
		}
		return mapResult;
	}

	/**預測下載數據*/
	public Map<String,String>  downloadForecast(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表","銷售收入預測表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表_下载","銷售收入預測表_下载")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			row.getCell(15).setCellValue(y);
			String sql="select * from FIT_FORECAST_REVENUE_V1 where YEAR='"+y+"'";
			if (null!=version && StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			String sbuVal="";
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+sbuStr+")";
			List<Map> sbuMap=forecastSalesRevenueDao.listMapBySql(sbusql);
			String entityStr=","+entitys+",";
			for (Map map:sbuMap){
				if(entityStr.indexOf(map.get("PARENT").toString())!=-1){
					sbuVal+=" ENTITY like '"+map.get("ALIAS").toString()+"_%' or";
				}
			}
			if(!sbuVal.isEmpty()){
				sql+=" and ("+sbuVal.substring(0,sbuVal.length()-2)+")";
			}
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+="order by entity,PRODUCT_SERIES,id";
			List<Object[]> dataList = forecastSalesRevenueDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 0; i < objects.length; i++) {
						if(i==54){
							break;
						}
						Cell cell = contentRow.createCell(i);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if(i==15){
							text = (objects[53] != null ? objects[53].toString() : "");
						}
						if (StringUtils.isNotEmpty(text) && i>14) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = forecastSalesRevenueDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 0; i < objects.length-1; i++) {
								if(i==54){
									break;
								}
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if(i==15){
									text = (objects[53] != null ? objects[53].toString() : "");
								}
								if (StringUtils.isNotEmpty(text) && i>14) {
									cell.setCellValue(Double.parseDouble(text));
								} else {
									cell.setCellValue(text);
								}
							}
						}
					}
				}
			}
			File outFile = new File(filePath);
			OutputStream out = new FileOutputStream(outFile);
			workBook.write(out);
			workBook.close();
			out.flush();
			out.close();
			mapResult.put("file",outFile.getName());
			System.gc();
		}catch (Exception e){
			e.printStackTrace();
			mapResult.put("result","N");
			mapResult.put("str",ExceptionUtil.getRootCauseMessage(e));
		}
		return mapResult;
	}

	/**預算版本控制*/
	public String versionBudget(){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from FIT_BUDGET_DETAIL_REVENUE where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = budgetDetailRevenueDao.listMapBySql(sqlVersion);
		if(null ==maps.get(0).get("VERSION")){
			sqlVersion="No finalizable data detected_沒有檢查到可定版的數據！";
		}else if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into FIT_BUDGET_DETAIL_REVENUE (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,\n" +
				"'"+sqlVersion+"' version,year, \n" +
				"entity,make_entity,segment,main_industry,industry,main_business,three,product_series,product_no,loan_customer,end_customer,type_of_airplane,trade_type,currency,pm,revenue,\n" +
				"revenue_nextyear,revenue_twoyear,revenue_threeyear,revenue_fouryear,quantity,quantity_nextyear,quantity_twoyear,quantity_threeyear,quantity_fouryear,quantity_month1,quantity_month2,\n" +
				"quantity_month3,quantity_month4,quantity_month5,quantity_month6,quantity_month7,quantity_month8,quantity_month9,quantity_month10,quantity_month11,quantity_month12,price_month1,\n" +
				"price_month2,price_month3,price_month4,price_month5,price_month6,price_month7,price_month8,price_month9,price_month10,price_month11,price_month12,revenue_month1,revenue_month2,\n" +
				"revenue_month3, revenue_month4, revenue_month5, revenue_month6,revenue_month7,revenue_month8,revenue_month9, revenue_month10, revenue_month11, revenue_month12,\n" +
				"create_name,create_date, sysdate version_date,'"+loginUser.getUsername()+"' version_name,ou,makeou,currency_transition\n" +
				"  from FIT_BUDGET_DETAIL_REVENUE where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		budgetDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

	/**預測版本控制*/
	public String versionForecast(){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from FIT_FORECAST_REVENUE where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = budgetDetailRevenueDao.listMapBySql(sqlVersion);
		if(null ==maps.get(0).get("VERSION")){
			sqlVersion="No finalizable data detected_沒有檢查到可定版的數據！";
		}else if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into FIT_FORECAST_REVENUE (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,\n" +
				"'"+sqlVersion+"' version,year, \n" +
				"entity,make_entity,segment,main_industry,industry,main_business,three,product_series,product_no,loan_customer,end_customer,type_of_airplane,\n" +
				"trade_type,currency,pm,quantity_month1,quantity_month2,quantity_month3,quantity_month4,quantity_month5,quantity_month6,\n" +
				"quantity_month7,quantity_month8,quantity_month9,quantity_month10,quantity_month11,quantity_month12,price_month1,\n" +
				"price_month2,price_month3,price_month4,price_month5,price_month6,price_month7,price_month8,price_month9,price_month10,price_month11,\n" +
				"price_month12,\n"+
				"create_name,create_date, sysdate version_date,'"+loginUser.getUsername()+"' version_name,ou,makeou,currency_transition\n" +
				"  from FIT_FORECAST_REVENUE where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		budgetDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

	public void deleteForecast(String id) throws Exception {
		forecastSalesRevenueDao.delete(id);
	}

	/**重複校驗**/
	public String doubleCheck(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest) {
		try {
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
					result.put("msg", instrumentClassService.getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
					return result.getJson();
				}
				Workbook wb = null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb = new HSSFWorkbook(file.getInputStream());
				} else {
					//Excel2007
					wb = new XSSFWorkbook(file.getInputStream());
				}
				wb.close();
				Sheet sheet = wb.getSheetAt(0);
				int rowNum = sheet.getPhysicalNumberOfRows();
				if (rowNum < 4) {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
					return result.getJson();
				}
				List<BudgetDetailRevenue> list = new ArrayList<>();
				for (int i = 3; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					BudgetDetailRevenue budgetDetailRevenue = new BudgetDetailRevenue();
					budgetDetailRevenue.setEntity(ExcelUtil.getCellStringValue(row.getCell(0), i));
					budgetDetailRevenue.setIndustry(ExcelUtil.getCellStringValue(row.getCell(2), i));
					budgetDetailRevenue.setmainBusiness(ExcelUtil.getCellStringValue(row.getCell(3), i));
					budgetDetailRevenue.setThree(ExcelUtil.getCellStringValue(row.getCell(4), i));
					budgetDetailRevenue.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(5), i));
					budgetDetailRevenue.setProductNo(ExcelUtil.getCellStringValue(row.getCell(6), i));
					budgetDetailRevenue.setEndCustomer(ExcelUtil.getCellStringValue(row.getCell(7), i));
					budgetDetailRevenue.setLoanCustomer(ExcelUtil.getCellStringValue(row.getCell(8), i));
					budgetDetailRevenue.setTradeType(ExcelUtil.getCellStringValue(row.getCell(9), i));
					budgetDetailRevenue.setCurrency(ExcelUtil.getCellStringValue(row.getCell(10), i));

					budgetDetailRevenue.setPriceMonth1(ExcelUtil.getCellStringValue(row.getCell(33), i));
					budgetDetailRevenue.setPriceMonth2(ExcelUtil.getCellStringValue(row.getCell(34), i));
					budgetDetailRevenue.setPriceMonth3(ExcelUtil.getCellStringValue(row.getCell(35), i));
					budgetDetailRevenue.setPriceMonth4(ExcelUtil.getCellStringValue(row.getCell(36), i));
					budgetDetailRevenue.setPriceMonth5(ExcelUtil.getCellStringValue(row.getCell(37), i));
					budgetDetailRevenue.setPriceMonth6(ExcelUtil.getCellStringValue(row.getCell(38), i));
					budgetDetailRevenue.setPriceMonth7(ExcelUtil.getCellStringValue(row.getCell(39), i));
					budgetDetailRevenue.setPriceMonth8(ExcelUtil.getCellStringValue(row.getCell(40), i));
					budgetDetailRevenue.setPriceMonth9(ExcelUtil.getCellStringValue(row.getCell(41), i));
					budgetDetailRevenue.setPriceMonth10(ExcelUtil.getCellStringValue(row.getCell(42), i));
					budgetDetailRevenue.setPriceMonth11(ExcelUtil.getCellStringValue(row.getCell(43), i));
					budgetDetailRevenue.setPriceMonth12(ExcelUtil.getCellStringValue(row.getCell(44), i));
					budgetDetailRevenue.setYear(ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(13), 0));
					list.add(budgetDetailRevenue);
				}
				result=this.saveDoubleCheck(result,list,locale);

			} else {
				result.put("flag", "fail");
				result.put("msg", instrumentClassService.getLanguage(locale, "對不起，未接受到上傳的文件", "Unreceived File"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	/**重複數據校驗*/
	public AjaxResult saveDoubleCheck(AjaxResult result,List<BudgetDetailRevenue> list,Locale locale){
		String sql="delete from FIT_REVENUE_DOUBLE_CHECK";
		budgetDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			BudgetDetailRevenue d=list.get(i);
			sql="insert into FIT_REVENUE_DOUBLE_CHECK values('"+d.getYear()+"','"+d.getEntity()+"','"+d.getIndustry()+"','"
					+d.getmainBusiness()+"','"+d.getThree()+"','"+d.getProductSeries()+"','"+d.getProductNo()+"','"
					+d.getLoanCustomer()+"','"+d.getEndCustomer()+"','"+d.getCurrency()+"','"+d.getTradeType()+ "','"
					+d.getPriceMonth1()+"','"+d.getPriceMonth2()+"','"+d.getPriceMonth3()+"','"+d.getPriceMonth4()+"','"
					+d.getPriceMonth5()+"','"+d.getPriceMonth6()+"','"+d.getPriceMonth7()+"','"+d.getPriceMonth8()+"','"
					+d.getPriceMonth9()+"','"+d.getPriceMonth10()+"','"+d.getPriceMonth11()+"','"+d.getPriceMonth12()+"')";
			budgetDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		}
		sql="select distinct year,entity,industry,main_business,three,nvl(product_series,' ') product_series,nvl(product_no,' ') product_no,loan_customer,end_customer,\n" +
				"currency,trade_type from(\n" +
				"select * from FIT_REVENUE_DOUBLE_CHECK unpivot(price for month in(price_month1 as '1',price_month2 as '2',price_month3 as '3',price_month4 as '4',price_month5 as '5',price_month6 as '6',price_month7 as '7',\n" +
				"price_month8 as '8',price_month9 as '9',price_month10 as '10',price_month11 as '11',price_month12 as '12'))\n" +
				"ORDER BY year,entity,industry,main_business,three,product_series,product_no,loan_customer,end_customer,\n" +
				"currency,trade_type,month\n" +
				")t Group by year,entity,industry,main_business,three,product_series,product_no,loan_customer,end_customer,\n" +
				"currency,trade_type,month,price Having count(*)>1";
		List<Map> listMap= budgetDetailRevenueDao.listMapBySql(sql);
		if(null==listMap||listMap.size()<1){
			result.put("flag", "fail");
			result.put("msg", instrumentClassService.getLanguage(locale, "沒有重複數據。", "No duplicate data"));
		}else {
			sql="";
			for (Map map:listMap){
				sql+="<p>"+map.get("ENTITY")+","+map.get("INDUSTRY")+","+map.get("MAIN_BUSINESS")+","+map.get("THREE")+","+map.get("PRODUCT_SERIES")+","+map.get("PRODUCT_NO")+","
				+map.get("LOAN_CUSTOMER")+","+map.get("END_CUSTOMER")+","+map.get("CURRENCY");
			}
			result.put("flag", "fail");
			result.put("msg", instrumentClassService.getLanguage(locale, "<font style=\"color:red;font-size:large;\">以下維度數據重複:</font>"+sql, "<font style=\"color:red;\">The following dimension data is repeated--></font>"+sql));
		}
		return result;
	}
}
