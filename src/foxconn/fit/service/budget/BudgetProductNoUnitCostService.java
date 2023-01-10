package foxconn.fit.service.budget;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.BudgetProductNoUnitCostDao;
import foxconn.fit.dao.budget.ForecastSalesCostDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.budget.BudgetProductNoUnitCost;
import foxconn.fit.entity.budget.ForecastSalesCost;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
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
public class BudgetProductNoUnitCostService extends BaseService<BudgetProductNoUnitCost> {

	@Autowired
	private BudgetProductNoUnitCostDao budgetProductNoUnitCostDao;

	@Autowired
	private InstrumentClassService instrumentClassService;

	@Autowired
	private ForecastSalesCostDao forecastSalesCostDao;

	@Override
	public BaseDaoHibernate<BudgetProductNoUnitCost> getDao() {
		return budgetProductNoUnitCostDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> sbuList = budgetProductNoUnitCostDao.listBySql("select distinct parent from fit_dimension where type='"+EnumDimensionType.Entity+"' order by parent");
		model.addAttribute("sbuList", sbuList);
		List<String> yearsList = budgetProductNoUnitCostDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		//預算應爲測試需要先把年份校驗放開
//		int year=calendar.get(Calendar.YEAR)+1;
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		model.addAttribute("versionList", this.versionVal());
		return model;
	}

	public List<String> versionVal(){
		Calendar calendar=Calendar.getInstance();
		//預算應爲測試需要先把年份校驗放開
//		int year=calendar.get(Calendar.YEAR)+1;
		int year=calendar.get(Calendar.YEAR);
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select distinct version  from FIT_BUDGET_PRODUCT_UNIT_COST where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version ";
		List<String> versionList=budgetProductNoUnitCostDao.listBySql(sqlVersion);
		return  versionList;
	}

	/**預算頁面查詢*/
	public String dataList(String year,String version,String entity,String tableName){
		String sql="select * from "+tableName+" where 1=1";
		if (null!=year&&StringUtils.isNotEmpty(year)) {
			sql+=" and YEAR='"+year+"'";
		}
		if (null!=version && StringUtils.isNotEmpty(version)) {
			sql+=" and version='"+version+"'";
		}
		String tarList=instrumentClassService.getBudgetSBUStr();
		String sbu="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS,','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+tarList+")";
		List<Map> sbuMap=budgetProductNoUnitCostDao.listMapBySql(sbu);
		sql+=instrumentClassService.querySbuSql(entity,sbuMap);
		sql+=" order by year,entity,make_entity,ID";
		return sql;
	}

	/**預算數據上傳*/
	public String uploadBudget(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest) {
		try {
//			獲取當前用戶的SBU權限
			List<String> tarList =instrumentClassService.getBudgetSBU();
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
				int COLUMN_NUM =0;
				String v_year ="";
				if(sheet.getSheetName().equals("銷售成本預算表")){
					COLUMN_NUM =226;
					v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(10), 0);
				}else if(sheet.getSheetName().equals("簡易版銷售成本預算表")){
					COLUMN_NUM =70;
					v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(2), 0);
				}else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "請使用模板上傳數據！", "Please use the template to upload data"));
					return result.getJson();
				}
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
				//預算應爲測試需要先把年份校驗放開
//				Calendar calendar = Calendar.getInstance();
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
				List<BudgetProductNoUnitCost> list = new ArrayList<>();
				/**SBU法人**/List<String> entityList = new ArrayList<>();
				/**SBU**/List<String> sbuList = new ArrayList<>();
				/**交易類型**/List<String> tradeTypeList = new ArrayList<>();
				UserDetailImpl loginUser = SecurityUtils.getLoginUser();
				String check = "";
				String checkProduct = "";
				String type = "";
				for (int i = 3; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(0), i);
					if(row == null||entity.length()<1||"".equals(entity)){
						continue;
					}
					//跳過沒有SBU權限的數據
					String sql="select distinct PARENT from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"' and ALIAS='"+entity+"'";
					List<String> listSbu=budgetProductNoUnitCostDao.listBySql(sql);
					sbuList.addAll(listSbu);
					check = instrumentClassService.getDiffrent(listSbu, tarList);
					if (row == null|| !"".equalsIgnoreCase(check.trim()) || check.length() > 0) {
						continue;
					}
					BudgetProductNoUnitCost budgetProductNoUnitCost = new BudgetProductNoUnitCost();
					entityList.add(entity);
					if(COLUMN_NUM==70){
						type="1";
						String tradeType=ExcelUtil.getCellStringValue(row.getCell(1), i);
						if(tradeType.isEmpty()){
							tradeTypeList.add("空");
						}else{
							tradeTypeList.add(tradeType);
						}
						budgetProductNoUnitCost.setTradeType(ExcelUtil.getCellStringValue(row.getCell(1), i));
						budgetProductNoUnitCost.setMaterialCost1(ExcelUtil.getCellStringValue(row.getCell(2), i));
						budgetProductNoUnitCost.setLaborCost1(ExcelUtil.getCellStringValue(row.getCell(3),i));
						budgetProductNoUnitCost.setManufactureCost1(ExcelUtil.getCellStringValue(row.getCell(4),i));

						budgetProductNoUnitCost.setMaterialCost2(ExcelUtil.getCellStringValue(row.getCell(6), i));
						budgetProductNoUnitCost.setLaborCost2(ExcelUtil.getCellStringValue(row.getCell(7),i));
						budgetProductNoUnitCost.setManufactureCost2(ExcelUtil.getCellStringValue(row.getCell(8),i));

						budgetProductNoUnitCost.setMaterialCost3(ExcelUtil.getCellStringValue(row.getCell(10), i));
						budgetProductNoUnitCost.setLaborCost3(ExcelUtil.getCellStringValue(row.getCell(11),i));
						budgetProductNoUnitCost.setManufactureCost3(ExcelUtil.getCellStringValue(row.getCell(12),i));

						budgetProductNoUnitCost.setMaterialCost4(ExcelUtil.getCellStringValue(row.getCell(14), i));
						budgetProductNoUnitCost.setLaborCost4(ExcelUtil.getCellStringValue(row.getCell(15),i));
						budgetProductNoUnitCost.setManufactureCost4(ExcelUtil.getCellStringValue(row.getCell(16),i));

						budgetProductNoUnitCost.setMaterialCost5(ExcelUtil.getCellStringValue(row.getCell(18), i));
						budgetProductNoUnitCost.setLaborCost5(ExcelUtil.getCellStringValue(row.getCell(19),i));
						budgetProductNoUnitCost.setManufactureCost5(ExcelUtil.getCellStringValue(row.getCell(20),i));

						budgetProductNoUnitCost.setMaterialCost6(ExcelUtil.getCellStringValue(row.getCell(22), i));
						budgetProductNoUnitCost.setLaborCost6(ExcelUtil.getCellStringValue(row.getCell(23),i));
						budgetProductNoUnitCost.setManufactureCost6(ExcelUtil.getCellStringValue(row.getCell(24),i));

						budgetProductNoUnitCost.setMaterialCost7(ExcelUtil.getCellStringValue(row.getCell(26), i));
						budgetProductNoUnitCost.setLaborCost7(ExcelUtil.getCellStringValue(row.getCell(27),i));
						budgetProductNoUnitCost.setManufactureCost7(ExcelUtil.getCellStringValue(row.getCell(28),i));

						budgetProductNoUnitCost.setMaterialCost8(ExcelUtil.getCellStringValue(row.getCell(30), i));
						budgetProductNoUnitCost.setLaborCost8(ExcelUtil.getCellStringValue(row.getCell(31),i));
						budgetProductNoUnitCost.setManufactureCost8(ExcelUtil.getCellStringValue(row.getCell(32),i));

						budgetProductNoUnitCost.setMaterialCost9(ExcelUtil.getCellStringValue(row.getCell(34), i));
						budgetProductNoUnitCost.setLaborCost9(ExcelUtil.getCellStringValue(row.getCell(35),i));
						budgetProductNoUnitCost.setManufactureCost9(ExcelUtil.getCellStringValue(row.getCell(36),i));

						budgetProductNoUnitCost.setMaterialCost10(ExcelUtil.getCellStringValue(row.getCell(38),i));
						budgetProductNoUnitCost.setLaborCost10(ExcelUtil.getCellStringValue(row.getCell(39),i));
						budgetProductNoUnitCost.setManufactureCost10(ExcelUtil.getCellStringValue(row.getCell(40),i));

						budgetProductNoUnitCost.setMaterialCost11(ExcelUtil.getCellStringValue(row.getCell(42),i));
						budgetProductNoUnitCost.setLaborCost11(ExcelUtil.getCellStringValue(row.getCell(43),i));
						budgetProductNoUnitCost.setManufactureCost11(ExcelUtil.getCellStringValue(row.getCell(44),i));

						budgetProductNoUnitCost.setMaterialCost12(ExcelUtil.getCellStringValue(row.getCell(46),i));
						budgetProductNoUnitCost.setLaborCost12(ExcelUtil.getCellStringValue(row.getCell(47),i));
						budgetProductNoUnitCost.setManufactureCost12(ExcelUtil.getCellStringValue(row.getCell(48),i));

						budgetProductNoUnitCost.setMaterialCostNextyear(ExcelUtil.getCellStringValue(row.getCell(54),i));
						budgetProductNoUnitCost.setLaborCostNextyear(ExcelUtil.getCellStringValue(row.getCell(55),i));
						budgetProductNoUnitCost.setManufactureCostNextyear(ExcelUtil.getCellStringValue(row.getCell(56),i));

						budgetProductNoUnitCost.setMaterialCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(58),i));
						budgetProductNoUnitCost.setLaborCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(59),i));
						budgetProductNoUnitCost.setManufactureCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(60),i));

						budgetProductNoUnitCost.setMaterialCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(62),i));
						budgetProductNoUnitCost.setLaborCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(63),i));
						budgetProductNoUnitCost.setManufactureCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(64),i));

						budgetProductNoUnitCost.setMaterialCostFouryear(ExcelUtil.getCellStringValue(row.getCell(66),i));
						budgetProductNoUnitCost.setLaborCostFouryear(ExcelUtil.getCellStringValue(row.getCell(67),i));
						budgetProductNoUnitCost.setManufactureCostFouryear(ExcelUtil.getCellStringValue(row.getCell(68),i));
					}else if(COLUMN_NUM==226){
						type="2";
						if(ExcelUtil.getCellStringValue(row.getCell(2), i).equals(ExcelUtil.getCellStringValue(row.getCell(3), i))){
							checkProduct+=(i+1)+",";
							continue;
						}
						budgetProductNoUnitCost.setMakeEntity(ExcelUtil.getCellStringValue(row.getCell(1), i));
						budgetProductNoUnitCost.setIndustry(ExcelUtil.getCellStringValue(row.getCell(2), i));
						budgetProductNoUnitCost.setMainBusiness(ExcelUtil.getCellStringValue(row.getCell(3), i));
						budgetProductNoUnitCost.setThree(ExcelUtil.getCellStringValue(row.getCell(4), i));
						budgetProductNoUnitCost.setProduct(ExcelUtil.getCellStringValue(row.getCell(5), i));
						budgetProductNoUnitCost.setProductNo(ExcelUtil.getCellStringValue(row.getCell(6), i));
						budgetProductNoUnitCost.setLoanCustomer(ExcelUtil.getCellStringValue(row.getCell(7), i));
						budgetProductNoUnitCost.setEndCustomer(ExcelUtil.getCellStringValue(row.getCell(8), i));
						budgetProductNoUnitCost.setTradeType(ExcelUtil.getCellStringValue(row.getCell(9), i));

						budgetProductNoUnitCost.setSalesQuantity1(ExcelUtil.getCellStringValue(row.getCell(130), i));
						budgetProductNoUnitCost.setMaterialCost1(ExcelUtil.getCellStringValue(row.getCell(131), i));
						budgetProductNoUnitCost.setLaborCost1(ExcelUtil.getCellStringValue(row.getCell(132),i));
						budgetProductNoUnitCost.setManufactureCost1(ExcelUtil.getCellStringValue(row.getCell(133),i));

						budgetProductNoUnitCost.setSalesQuantity2(ExcelUtil.getCellStringValue(row.getCell(135), i));
						budgetProductNoUnitCost.setMaterialCost2(ExcelUtil.getCellStringValue(row.getCell(136), i));
						budgetProductNoUnitCost.setLaborCost2(ExcelUtil.getCellStringValue(row.getCell(137),i));
						budgetProductNoUnitCost.setManufactureCost2(ExcelUtil.getCellStringValue(row.getCell(138),i));

						budgetProductNoUnitCost.setSalesQuantity3(ExcelUtil.getCellStringValue(row.getCell(140), i));
						budgetProductNoUnitCost.setMaterialCost3(ExcelUtil.getCellStringValue(row.getCell(141), i));
						budgetProductNoUnitCost.setLaborCost3(ExcelUtil.getCellStringValue(row.getCell(142),i));
						budgetProductNoUnitCost.setManufactureCost3(ExcelUtil.getCellStringValue(row.getCell(143),i));

						budgetProductNoUnitCost.setSalesQuantity4(ExcelUtil.getCellStringValue(row.getCell(145), i));
						budgetProductNoUnitCost.setMaterialCost4(ExcelUtil.getCellStringValue(row.getCell(146), i));
						budgetProductNoUnitCost.setLaborCost4(ExcelUtil.getCellStringValue(row.getCell(147),i));
						budgetProductNoUnitCost.setManufactureCost4(ExcelUtil.getCellStringValue(row.getCell(148),i));

						budgetProductNoUnitCost.setSalesQuantity5(ExcelUtil.getCellStringValue(row.getCell(150), i));
						budgetProductNoUnitCost.setMaterialCost5(ExcelUtil.getCellStringValue(row.getCell(151), i));
						budgetProductNoUnitCost.setLaborCost5(ExcelUtil.getCellStringValue(row.getCell(152),i));
						budgetProductNoUnitCost.setManufactureCost5(ExcelUtil.getCellStringValue(row.getCell(153),i));

						budgetProductNoUnitCost.setSalesQuantity6(ExcelUtil.getCellStringValue(row.getCell(155), i));
						budgetProductNoUnitCost.setMaterialCost6(ExcelUtil.getCellStringValue(row.getCell(156), i));
						budgetProductNoUnitCost.setLaborCost6(ExcelUtil.getCellStringValue(row.getCell(157),i));
						budgetProductNoUnitCost.setManufactureCost6(ExcelUtil.getCellStringValue(row.getCell(158),i));

						budgetProductNoUnitCost.setSalesQuantity7(ExcelUtil.getCellStringValue(row.getCell(160), i));
						budgetProductNoUnitCost.setMaterialCost7(ExcelUtil.getCellStringValue(row.getCell(161), i));
						budgetProductNoUnitCost.setLaborCost7(ExcelUtil.getCellStringValue(row.getCell(162),i));
						budgetProductNoUnitCost.setManufactureCost7(ExcelUtil.getCellStringValue(row.getCell(163),i));

						budgetProductNoUnitCost.setSalesQuantity8(ExcelUtil.getCellStringValue(row.getCell(165), i));
						budgetProductNoUnitCost.setMaterialCost8(ExcelUtil.getCellStringValue(row.getCell(166), i));
						budgetProductNoUnitCost.setLaborCost8(ExcelUtil.getCellStringValue(row.getCell(167),i));
						budgetProductNoUnitCost.setManufactureCost8(ExcelUtil.getCellStringValue(row.getCell(168),i));

						budgetProductNoUnitCost.setSalesQuantity9(ExcelUtil.getCellStringValue(row.getCell(170), i));
						budgetProductNoUnitCost.setMaterialCost9(ExcelUtil.getCellStringValue(row.getCell(171), i));
						budgetProductNoUnitCost.setLaborCost9(ExcelUtil.getCellStringValue(row.getCell(172),i));
						budgetProductNoUnitCost.setManufactureCost9(ExcelUtil.getCellStringValue(row.getCell(173),i));

						budgetProductNoUnitCost.setSalesQuantity10(ExcelUtil.getCellStringValue(row.getCell(175), i));
						budgetProductNoUnitCost.setMaterialCost10(ExcelUtil.getCellStringValue(row.getCell(176),i));
						budgetProductNoUnitCost.setLaborCost10(ExcelUtil.getCellStringValue(row.getCell(177),i));
						budgetProductNoUnitCost.setManufactureCost10(ExcelUtil.getCellStringValue(row.getCell(178),i));

						budgetProductNoUnitCost.setSalesQuantity11(ExcelUtil.getCellStringValue(row.getCell(180), i));
						budgetProductNoUnitCost.setMaterialCost11(ExcelUtil.getCellStringValue(row.getCell(181),i));
						budgetProductNoUnitCost.setLaborCost11(ExcelUtil.getCellStringValue(row.getCell(182),i));
						budgetProductNoUnitCost.setManufactureCost11(ExcelUtil.getCellStringValue(row.getCell(183),i));

						budgetProductNoUnitCost.setSalesQuantity12(ExcelUtil.getCellStringValue(row.getCell(185), i));
						budgetProductNoUnitCost.setMaterialCost12(ExcelUtil.getCellStringValue(row.getCell(186),i));
						budgetProductNoUnitCost.setLaborCost12(ExcelUtil.getCellStringValue(row.getCell(187),i));
						budgetProductNoUnitCost.setManufactureCost12(ExcelUtil.getCellStringValue(row.getCell(188),i));

						budgetProductNoUnitCost.setSalesQuantityNextyear(ExcelUtil.getCellStringValue(row.getCell(194), i));
						budgetProductNoUnitCost.setMaterialCostNextyear(ExcelUtil.getCellStringValue(row.getCell(195),i));
						budgetProductNoUnitCost.setLaborCostNextyear(ExcelUtil.getCellStringValue(row.getCell(196),i));
						budgetProductNoUnitCost.setManufactureCostNextyear(ExcelUtil.getCellStringValue(row.getCell(197),i));

						budgetProductNoUnitCost.setSalesQuantityTwoyear(ExcelUtil.getCellStringValue(row.getCell(203), i));
						budgetProductNoUnitCost.setMaterialCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(204),i));
						budgetProductNoUnitCost.setLaborCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(205),i));
						budgetProductNoUnitCost.setManufactureCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(206),i));

						budgetProductNoUnitCost.setSalesQuantityThreeyear(ExcelUtil.getCellStringValue(row.getCell(212), i));
						budgetProductNoUnitCost.setMaterialCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(213),i));
						budgetProductNoUnitCost.setLaborCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(214),i));
						budgetProductNoUnitCost.setManufactureCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(215),i));

						budgetProductNoUnitCost.setSalesQuantityFouryear(ExcelUtil.getCellStringValue(row.getCell(221), i));
						budgetProductNoUnitCost.setMaterialCostFouryear(ExcelUtil.getCellStringValue(row.getCell(222),i));
						budgetProductNoUnitCost.setLaborCostFouryear(ExcelUtil.getCellStringValue(row.getCell(223),i));
						budgetProductNoUnitCost.setManufactureCostFouryear(ExcelUtil.getCellStringValue(row.getCell(224),i));
					}
					budgetProductNoUnitCost.setEntity(ExcelUtil.getCellStringValue(row.getCell(0), i));
					budgetProductNoUnitCost.setYear(v_year);
					budgetProductNoUnitCost.setVersion("V00");
					budgetProductNoUnitCost.setId(UUID.randomUUID().toString());
					budgetProductNoUnitCost.setCreateName(loginUser.getUsername());
					budgetProductNoUnitCost.setCreateDate(new Date());
					list.add(budgetProductNoUnitCost);
				}
				if (!list.isEmpty()) {
				     if(COLUMN_NUM==70){
						 /**SBU_法人校驗*/
						 String sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"'";
						 check=this.check(entityList,sql);
						 if (!check.equals("") && check.length() > 0){
							 result.put("flag", "fail");
							 result.put("msg", "以下【SBU_法人】在【維度表】没有找到---> " + check);
							 return result.getJson();
						 }
						 /**交易類型**/
						 sql="select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.View.getCode()+ "' and PARENT in('Int000') ";
						 check=this.check(tradeTypeList,sql);
						 if (!check.equals("") && check.length() > 0){
							 result.put("flag", "fail");
							 result.put("msg", "以下【交易類型】在【維度表】没有找到---> "+check);
							 return result.getJson();
						 }
				     }else{
						  String msg=this.checkMsgBudget(list,loginUser.getUsername());
						  if(!msg.isEmpty()){
							  result.put("flag", "fail");
							  result.put("msg", "上傳失敗！以下維度數據未在營收明細上傳---> "+msg);
							  return result.getJson();
						  }
					 }
					this.saveBatchBudget(list,v_year,instrumentClassService.removeDuplicate(entityList),type);
				} else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
				}
				if (!"".equalsIgnoreCase(checkProduct.trim()) && checkProduct.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下行數據未上傳成功，產品系列和料號一致請上傳簡化版成本預算模板。--->" + checkProduct.substring(0,checkProduct.length()-1), "The following lines fail to be uploaded.The product series and material number are consistent. Please upload the simplified cost budget template--->" + checkProduct.substring(0,checkProduct.length()-1)));
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

	/**校驗成本能在營收中找到嗎**/
	private String checkMsgBudget(List<BudgetProductNoUnitCost> list,String user){
		String msg="";
		String sql="";
		for (int i = 0; i < list.size(); i++) {
			BudgetProductNoUnitCost budgetProductNoUnitCost=list.get(i);
			sql="select count(1) from FIT_BUDGET_DETAIL_REVENUE where " +
					"ENTITY='"+budgetProductNoUnitCost.getEntity()+"' " +
					"and MAKE_ENTITY='"+budgetProductNoUnitCost.getMakeEntity()+"' and create_name='"+user+"'" +
					"and (PRODUCT_SERIES ='"+budgetProductNoUnitCost.getProduct()+"' " +
					"and PRODUCT_NO='"+budgetProductNoUnitCost.getProductNo()+"') ";
			List<BigDecimal> countList = (List<BigDecimal>)budgetProductNoUnitCostDao.listBySql(sql);
			if(countList.get(0).intValue()==0){
				msg+="<p>"+budgetProductNoUnitCost.getEntity()+","+budgetProductNoUnitCost.getMakeEntity()+","+
						budgetProductNoUnitCost.getProduct()+","+budgetProductNoUnitCost.getProductNo();
			}
		}
		return msg;
	}
	/**校驗成本能在營收中找到嗎**/
	private String checkMsgForecast(List<ForecastSalesCost> list,String user){
		String msg="";
		String sql="";
		for (int i = 0; i < list.size(); i++) {
			ForecastSalesCost forecastSalesCost=list.get(i);
			sql="select count(1) from FIT_FORECAST_REVENUE where " +
					"ENTITY='"+forecastSalesCost.getEntity()+"' " +
					"and MAKE_ENTITY='"+forecastSalesCost.getMakeEntity()+"' and create_name='"+user+"'" +
					"and (PRODUCT_SERIES ='"+forecastSalesCost.getProduct()+"' " +
					"and PRODUCT_NO='"+forecastSalesCost.getProductNo()+"') ";
			List<BigDecimal> countList = (List<BigDecimal>)budgetProductNoUnitCostDao.listBySql(sql);
			if(countList.get(0).intValue()==0){
				msg+="<p>"+forecastSalesCost.getEntity()+","+forecastSalesCost.getMakeEntity()+","+
						forecastSalesCost.getProduct()+","+forecastSalesCost.getProductNo();
			}
		}
		return msg;
	}
	/**預測數據上傳*/
	public String uploadForecast(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest) {
		try {
//			獲取當前用戶的SBU權限
			List<String> tarList =instrumentClassService.getBudgetSBU();
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
				int COLUMN_NUM =0;
				String v_year ="";
				if(sheet.getSheetName().equals("銷售成本預測表")){
					COLUMN_NUM =190;
					v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(10), 0);
				}else if(sheet.getSheetName().equals("簡易版銷售成本預測表")){
					COLUMN_NUM =54;
					v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(2), 0);
				}else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "請使用模板上傳數據！", "Please use the template to upload data"));
					return result.getJson();
				}
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
				Calendar calendar = Calendar.getInstance();
				String year = Integer.toString(calendar.get(Calendar.YEAR));
				Assert.isTrue(year.substring(2).equals(v_year.substring(2)), instrumentClassService.getLanguage(locale, "僅可上傳當前年的預測數據！", "Only forecast data for the current year can be uploaded"));
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
				List<ForecastSalesCost> list = new ArrayList<>();
				/**SBU法人**/List<String> entityList = new ArrayList<>();
				/**SBU**/List<String> sbuList = new ArrayList<>();
				/**交易類型**/List<String> tradeTypeList = new ArrayList<>();
				UserDetailImpl loginUser = SecurityUtils.getLoginUser();
				String check = "";
				String checkProduct = "";
				String type="";
				for (int i = 3; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(0), i);
					if(row == null||entity.length()<1||"".equals(entity)){
						continue;
					}
					//跳過沒有SBU權限的數據
					String sql="select distinct PARENT from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"' and ALIAS='"+entity+"'";
					List<String> listSbu=budgetProductNoUnitCostDao.listBySql(sql);
					sbuList.addAll(listSbu);
					check = instrumentClassService.getDiffrent(listSbu, tarList);
					if (row == null|| !"".equalsIgnoreCase(check.trim()) || check.length() > 0) {
						continue;
					}
					ForecastSalesCost forecastSalesCost = new ForecastSalesCost();
					entityList.add(entity);
					if(COLUMN_NUM==54){
						type="1";
						String tradeType=ExcelUtil.getCellStringValue(row.getCell(1), i);
						if(tradeType.isEmpty()){
							tradeTypeList.add("空");
						}else{
							tradeTypeList.add(tradeType);
						}
						forecastSalesCost.setTradeType(ExcelUtil.getCellStringValue(row.getCell(1), i));

						forecastSalesCost.setMaterialCost1(ExcelUtil.getCellStringValue(row.getCell(2), i));
						forecastSalesCost.setLaborCost1(ExcelUtil.getCellStringValue(row.getCell(3),i));
						forecastSalesCost.setManufactureCost1(ExcelUtil.getCellStringValue(row.getCell(4),i));

						forecastSalesCost.setMaterialCost2(ExcelUtil.getCellStringValue(row.getCell(6), i));
						forecastSalesCost.setLaborCost2(ExcelUtil.getCellStringValue(row.getCell(7),i));
						forecastSalesCost.setManufactureCost2(ExcelUtil.getCellStringValue(row.getCell(8),i));

						forecastSalesCost.setMaterialCost3(ExcelUtil.getCellStringValue(row.getCell(10), i));
						forecastSalesCost.setLaborCost3(ExcelUtil.getCellStringValue(row.getCell(11),i));
						forecastSalesCost.setManufactureCost3(ExcelUtil.getCellStringValue(row.getCell(12),i));

						forecastSalesCost.setMaterialCost4(ExcelUtil.getCellStringValue(row.getCell(14), i));
						forecastSalesCost.setLaborCost4(ExcelUtil.getCellStringValue(row.getCell(15),i));
						forecastSalesCost.setManufactureCost4(ExcelUtil.getCellStringValue(row.getCell(16),i));

						forecastSalesCost.setMaterialCost5(ExcelUtil.getCellStringValue(row.getCell(18), i));
						forecastSalesCost.setLaborCost5(ExcelUtil.getCellStringValue(row.getCell(19),i));
						forecastSalesCost.setManufactureCost5(ExcelUtil.getCellStringValue(row.getCell(20),i));

						forecastSalesCost.setMaterialCost6(ExcelUtil.getCellStringValue(row.getCell(22), i));
						forecastSalesCost.setLaborCost6(ExcelUtil.getCellStringValue(row.getCell(23),i));
						forecastSalesCost.setManufactureCost6(ExcelUtil.getCellStringValue(row.getCell(24),i));

						forecastSalesCost.setMaterialCost7(ExcelUtil.getCellStringValue(row.getCell(26), i));
						forecastSalesCost.setLaborCost7(ExcelUtil.getCellStringValue(row.getCell(27),i));
						forecastSalesCost.setManufactureCost7(ExcelUtil.getCellStringValue(row.getCell(28),i));

						forecastSalesCost.setMaterialCost8(ExcelUtil.getCellStringValue(row.getCell(30), i));
						forecastSalesCost.setLaborCost8(ExcelUtil.getCellStringValue(row.getCell(31),i));
						forecastSalesCost.setManufactureCost8(ExcelUtil.getCellStringValue(row.getCell(32),i));

						forecastSalesCost.setMaterialCost9(ExcelUtil.getCellStringValue(row.getCell(34), i));
						forecastSalesCost.setLaborCost9(ExcelUtil.getCellStringValue(row.getCell(35),i));
						forecastSalesCost.setManufactureCost9(ExcelUtil.getCellStringValue(row.getCell(36),i));

						forecastSalesCost.setMaterialCost10(ExcelUtil.getCellStringValue(row.getCell(38),i));
						forecastSalesCost.setLaborCost10(ExcelUtil.getCellStringValue(row.getCell(39),i));
						forecastSalesCost.setManufactureCost10(ExcelUtil.getCellStringValue(row.getCell(40),i));

						forecastSalesCost.setMaterialCost11(ExcelUtil.getCellStringValue(row.getCell(42),i));
						forecastSalesCost.setLaborCost11(ExcelUtil.getCellStringValue(row.getCell(43),i));
						forecastSalesCost.setManufactureCost11(ExcelUtil.getCellStringValue(row.getCell(44),i));

						forecastSalesCost.setMaterialCost12(ExcelUtil.getCellStringValue(row.getCell(46),i));
						forecastSalesCost.setLaborCost12(ExcelUtil.getCellStringValue(row.getCell(47),i));
						forecastSalesCost.setManufactureCost12(ExcelUtil.getCellStringValue(row.getCell(48),i));
					}else if(COLUMN_NUM==190){
						type="2";
						if(ExcelUtil.getCellStringValue(row.getCell(2), i).equals(ExcelUtil.getCellStringValue(row.getCell(3), i))){
							checkProduct+=(i+1)+",";
							continue;
						}
						forecastSalesCost.setMakeEntity(ExcelUtil.getCellStringValue(row.getCell(1), i));
						forecastSalesCost.setIndustry(ExcelUtil.getCellStringValue(row.getCell(2), i));
						forecastSalesCost.setMainBusiness(ExcelUtil.getCellStringValue(row.getCell(3), i));
						forecastSalesCost.setThree(ExcelUtil.getCellStringValue(row.getCell(4), i));
						forecastSalesCost.setProduct(ExcelUtil.getCellStringValue(row.getCell(5), i));
						forecastSalesCost.setProductNo(ExcelUtil.getCellStringValue(row.getCell(6), i));
						forecastSalesCost.setLoanCustomer(ExcelUtil.getCellStringValue(row.getCell(7), i));
						forecastSalesCost.setEndCustomer(ExcelUtil.getCellStringValue(row.getCell(8), i));
						forecastSalesCost.setTradeType(ExcelUtil.getCellStringValue(row.getCell(9), i));

						forecastSalesCost.setSalesQuantity1(ExcelUtil.getCellStringValue(row.getCell(130), i));
						forecastSalesCost.setMaterialCost1(ExcelUtil.getCellStringValue(row.getCell(131), i));
						forecastSalesCost.setLaborCost1(ExcelUtil.getCellStringValue(row.getCell(132),i));
						forecastSalesCost.setManufactureCost1(ExcelUtil.getCellStringValue(row.getCell(133),i));

						forecastSalesCost.setSalesQuantity2(ExcelUtil.getCellStringValue(row.getCell(135), i));
						forecastSalesCost.setMaterialCost2(ExcelUtil.getCellStringValue(row.getCell(136), i));
						forecastSalesCost.setLaborCost2(ExcelUtil.getCellStringValue(row.getCell(137),i));
						forecastSalesCost.setManufactureCost2(ExcelUtil.getCellStringValue(row.getCell(138),i));

						forecastSalesCost.setSalesQuantity3(ExcelUtil.getCellStringValue(row.getCell(140), i));
						forecastSalesCost.setMaterialCost3(ExcelUtil.getCellStringValue(row.getCell(141), i));
						forecastSalesCost.setLaborCost3(ExcelUtil.getCellStringValue(row.getCell(142),i));
						forecastSalesCost.setManufactureCost3(ExcelUtil.getCellStringValue(row.getCell(143),i));

						forecastSalesCost.setSalesQuantity4(ExcelUtil.getCellStringValue(row.getCell(145), i));
						forecastSalesCost.setMaterialCost4(ExcelUtil.getCellStringValue(row.getCell(146), i));
						forecastSalesCost.setLaborCost4(ExcelUtil.getCellStringValue(row.getCell(147),i));
						forecastSalesCost.setManufactureCost4(ExcelUtil.getCellStringValue(row.getCell(148),i));

						forecastSalesCost.setSalesQuantity5(ExcelUtil.getCellStringValue(row.getCell(150), i));
						forecastSalesCost.setMaterialCost5(ExcelUtil.getCellStringValue(row.getCell(151), i));
						forecastSalesCost.setLaborCost5(ExcelUtil.getCellStringValue(row.getCell(152),i));
						forecastSalesCost.setManufactureCost5(ExcelUtil.getCellStringValue(row.getCell(153),i));

						forecastSalesCost.setSalesQuantity6(ExcelUtil.getCellStringValue(row.getCell(155), i));
						forecastSalesCost.setMaterialCost6(ExcelUtil.getCellStringValue(row.getCell(156), i));
						forecastSalesCost.setLaborCost6(ExcelUtil.getCellStringValue(row.getCell(157),i));
						forecastSalesCost.setManufactureCost6(ExcelUtil.getCellStringValue(row.getCell(158),i));

						forecastSalesCost.setSalesQuantity7(ExcelUtil.getCellStringValue(row.getCell(160), i));
						forecastSalesCost.setMaterialCost7(ExcelUtil.getCellStringValue(row.getCell(161), i));
						forecastSalesCost.setLaborCost7(ExcelUtil.getCellStringValue(row.getCell(162),i));
						forecastSalesCost.setManufactureCost7(ExcelUtil.getCellStringValue(row.getCell(163),i));

						forecastSalesCost.setSalesQuantity8(ExcelUtil.getCellStringValue(row.getCell(165), i));
						forecastSalesCost.setMaterialCost8(ExcelUtil.getCellStringValue(row.getCell(166), i));
						forecastSalesCost.setLaborCost8(ExcelUtil.getCellStringValue(row.getCell(167),i));
						forecastSalesCost.setManufactureCost8(ExcelUtil.getCellStringValue(row.getCell(168),i));

						forecastSalesCost.setSalesQuantity9(ExcelUtil.getCellStringValue(row.getCell(170), i));
						forecastSalesCost.setMaterialCost9(ExcelUtil.getCellStringValue(row.getCell(171), i));
						forecastSalesCost.setLaborCost9(ExcelUtil.getCellStringValue(row.getCell(172),i));
						forecastSalesCost.setManufactureCost9(ExcelUtil.getCellStringValue(row.getCell(173),i));

						forecastSalesCost.setSalesQuantity10(ExcelUtil.getCellStringValue(row.getCell(175), i));
						forecastSalesCost.setMaterialCost10(ExcelUtil.getCellStringValue(row.getCell(176),i));
						forecastSalesCost.setLaborCost10(ExcelUtil.getCellStringValue(row.getCell(177),i));
						forecastSalesCost.setManufactureCost10(ExcelUtil.getCellStringValue(row.getCell(178),i));

						forecastSalesCost.setSalesQuantity11(ExcelUtil.getCellStringValue(row.getCell(180), i));
						forecastSalesCost.setMaterialCost11(ExcelUtil.getCellStringValue(row.getCell(181),i));
						forecastSalesCost.setLaborCost11(ExcelUtil.getCellStringValue(row.getCell(182),i));
						forecastSalesCost.setManufactureCost11(ExcelUtil.getCellStringValue(row.getCell(183),i));

						forecastSalesCost.setSalesQuantity12(ExcelUtil.getCellStringValue(row.getCell(185), i));
						forecastSalesCost.setMaterialCost12(ExcelUtil.getCellStringValue(row.getCell(186),i));
						forecastSalesCost.setLaborCost12(ExcelUtil.getCellStringValue(row.getCell(187),i));
						forecastSalesCost.setManufactureCost12(ExcelUtil.getCellStringValue(row.getCell(188),i));
					}
					forecastSalesCost.setEntity(ExcelUtil.getCellStringValue(row.getCell(0), i));
					forecastSalesCost.setYear(v_year);
					forecastSalesCost.setVersion("V00");
					forecastSalesCost.setId(UUID.randomUUID().toString());
					forecastSalesCost.setCreateName(loginUser.getUsername());
					forecastSalesCost.setCreateDate(new Date());
					list.add(forecastSalesCost);
				}
				if (!list.isEmpty()) {
					if(COLUMN_NUM==54){
						/**SBU_法人校驗*/
						String sql="select distinct trim(alias) from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"'";
						check=this.check(entityList,sql);
						if (!check.equals("") && check.length() > 0){
							result.put("flag", "fail");
							result.put("msg", "以下【SBU_法人】在【維度表】没有找到---> " + check);
							return result.getJson();
						}
						/**交易類型**/
						sql="select distinct trim(alias) from fit_dimension where type='"+EnumDimensionType.View.getCode()+ "' and PARENT in('Int000') ";
						check=this.check(tradeTypeList,sql);
						if (!check.equals("") && check.length() > 0){
							result.put("flag", "fail");
							result.put("msg", "以下【交易類型】在【維度表】没有找到---> "+check);
							return result.getJson();
						}
					}else{
						String msg=this.checkMsgForecast(list,loginUser.getUsername());
						if(!msg.isEmpty()){
							result.put("flag", "fail");
							result.put("msg", "上傳失敗！以下維度數據未在營收明細上傳---> "+msg);
							return result.getJson();
						}
					}
					this.saveBatchForecast(list,v_year,instrumentClassService.removeDuplicate(entityList),type);
				} else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
				}
				if (!"".equalsIgnoreCase(checkProduct.trim()) && checkProduct.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下行數據未上傳成功，產品系列和料號一致請上傳簡化版成本預算模板。--->" + checkProduct.substring(0,checkProduct.length()-1), "The following lines fail to be uploaded.The product series and material number are consistent. Please upload the simplified cost budget template--->" + checkProduct.substring(0,checkProduct.length()-1)));
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

	/**匹配用戶上傳的主數據list是否在維度表中能找到*/
	public String check(List<String> list,String sql){
		list = instrumentClassService.removeDuplicate(list);
		List<String> checkList = this.listBySql(sql);
		String check = instrumentClassService.getDiffrent(list, checkList);
		return check;
	}


	/**預算保存數據*/
	public void saveBatchBudget(List<BudgetProductNoUnitCost> list,String year,List<String> entityList,String type) throws Exception {
		String sql="";
		if(type.equals("1")){
			sql="delete from FIT_BUDGET_PRODUCT_UNIT_COST where VERSION='V00' and YEAR='"+year+"' and PRODUCT_NO is null and ENTITY in(";
		}else{
			sql="delete from FIT_BUDGET_PRODUCT_UNIT_COST where VERSION='V00' and YEAR='"+year+"' and PRODUCT_NO is not null and ENTITY in(";
		}
		for (int i=0;i<entityList.size();i++){
			sql+="'"+entityList.get(i)+"',";
			if ((i + 50) % 1000 == 0) {
				budgetProductNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.substring(0,sql.length()-1)+")").executeUpdate();
				budgetProductNoUnitCostDao.getHibernateTemplate().flush();
				budgetProductNoUnitCostDao.getHibernateTemplate().clear();
			}
		}
		sql=sql.substring(0,sql.length()-1)+")";
		budgetProductNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			budgetProductNoUnitCostDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				budgetProductNoUnitCostDao.getHibernateTemplate().flush();
				budgetProductNoUnitCostDao.getHibernateTemplate().clear();
			}
		}
	}

	/**預測保存數據*/
	public void saveBatchForecast(List<ForecastSalesCost> list,String year,List<String> entityList,String type) throws Exception {
		String sql="";
		if(type.equals("1")){
			sql="delete from FIT_FORECAST_SALES_COST where VERSION='V00' and YEAR='"+year+"' and PRODUCT_NO is null and ENTITY in(";
		}else{
			sql="delete from FIT_FORECAST_SALES_COST where VERSION='V00' and YEAR='"+year+"' and PRODUCT_NO is not null and ENTITY in(";
		}
		for (int i=0;i<entityList.size();i++){
			sql+="'"+entityList.get(i)+"',";
			if ((i + 50) % 1000 == 0) {
				forecastSalesCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.substring(0,sql.length()-1)+")").executeUpdate();
				forecastSalesCostDao.getHibernateTemplate().flush();
				forecastSalesCostDao.getHibernateTemplate().clear();
			}
		}
		sql=sql.substring(0,sql.length()-1)+")";
		forecastSalesCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			forecastSalesCostDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				forecastSalesCostDao.getHibernateTemplate().flush();
				forecastSalesCostDao.getHibernateTemplate().clear();
			}
		}
	}

	/**預算下載模板*/
	public Map<String,String> templateBudget(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預算表","銷售成本預算表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預算表","銷售成本預算表")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			//預算應爲測試需要先把年份校驗放開
//			int year=calendar.get(Calendar.YEAR);
			int year=calendar.get(Calendar.YEAR)-1;
			row.getCell(10).setCellValue("FY"+ String.valueOf(year+1).substring(2));
			row.getCell(190).setCellValue("FY"+ String.valueOf(year+2).substring(2));
			row.getCell(199).setCellValue("FY"+ String.valueOf(year+3).substring(2));
			row.getCell(208).setCellValue("FY"+ String.valueOf(year+4).substring(2));
			row.getCell(217).setCellValue("FY"+ String.valueOf(year+5).substring(2));
			String sql=this.templateVal("FY"+ String.valueOf(year+1).substring(2),"fit_budget_detail_revenue") ;
			List<Map> list=budgetProductNoUnitCostDao.listMapBySql(sql);
			XSSFCellStyle style = workBook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(166,166,166)));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderColor(XSSFCellBorder.BorderSide.LEFT,new XSSFColor(new java.awt.Color(191,191,191)));
			style.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM,new XSSFColor(new java.awt.Color(191,191,191)));
			int rowNo=3;
			for (Map map:list) {
				row=sheet.createRow(rowNo++);
				row.createCell(0).setCellValue(map.get("ENTITY").toString());
				row.createCell(1).setCellValue(map.get("MAKE_ENTITY").toString());
				row.createCell(2).setCellValue(map.get("INDUSTRY").toString());
				row.createCell(3).setCellValue(map.get("MAIN_BUSINESS").toString());
				row.createCell(4).setCellValue(map.get("THREE").toString());
				row.createCell(5).setCellValue(map.get("PRODUCT_SERIES")==null?"":map.get("PRODUCT_SERIES").toString());
				row.createCell(6).setCellValue(map.get("PRODUCT_NO")==null?"":map.get("PRODUCT_NO").toString());
				row.createCell(7).setCellValue(map.get("LOAN_CUSTOMER").toString());
				row.createCell(8).setCellValue(map.get("END_CUSTOMER").toString());
				row.createCell(9).setCellValue(map.get("TRADE_TYPE").toString());

				row.createCell(10).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(12).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(14).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(16).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(18).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(20).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(22).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(24).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(26).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(28).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(30).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(32).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));

				row.createCell(34).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(36).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(38).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(40).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(42).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(44).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(46).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(48).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(50).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(52).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(54).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(56).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));

				row.createCell(58).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(60).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(62).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(64).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(66).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(68).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(70).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(72).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(74).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(76).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(78).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(80).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));

				row.createCell(82).setCellFormula("L"+rowNo);
				row.createCell(83).setCellFormula("AJ"+rowNo);
				row.createCell(84).setCellFormula("BH"+rowNo);
				row.createCell(85).setCellFormula("SUM(CE"+rowNo+":CG"+rowNo+")");
				row.createCell(86).setCellFormula("N"+rowNo);
				row.createCell(87).setCellFormula("AL"+rowNo);
				row.createCell(88).setCellFormula("BJ"+rowNo);
				row.createCell(89).setCellFormula("SUM(CI"+rowNo+":CK"+rowNo+")");
				row.createCell(90).setCellFormula("P"+rowNo);
				row.createCell(91).setCellFormula("AN"+rowNo);
				row.createCell(92).setCellFormula("BL"+rowNo);
				row.createCell(93).setCellFormula("SUM(CM"+rowNo+":CO"+rowNo+")");
				row.createCell(94).setCellFormula("R"+rowNo);
				row.createCell(95).setCellFormula("AP"+rowNo);
				row.createCell(96).setCellFormula("BN"+rowNo);
				row.createCell(97).setCellFormula("SUM(CQ"+rowNo+":CS"+rowNo+")");
				row.createCell(98).setCellFormula("T"+rowNo);
				row.createCell(99).setCellFormula("AR"+rowNo);
				row.createCell(100).setCellFormula("BP"+rowNo);
				row.createCell(101).setCellFormula("SUM(CU"+rowNo+":CW"+rowNo+")");
				row.createCell(102).setCellFormula("V"+rowNo);
				row.createCell(103).setCellFormula("AT"+rowNo);
				row.createCell(104).setCellFormula("BR"+rowNo);
				row.createCell(105).setCellFormula("SUM(CY"+rowNo+":DA"+rowNo+")");
				row.createCell(106).setCellFormula("X"+rowNo);
				row.createCell(107).setCellFormula("AV"+rowNo);
				row.createCell(108).setCellFormula("BT"+rowNo);
				row.createCell(109).setCellFormula("SUM(DC"+rowNo+":DE"+rowNo+")");
				row.createCell(110).setCellFormula("Z"+rowNo);
				row.createCell(111).setCellFormula("AX"+rowNo);
				row.createCell(112).setCellFormula("BV"+rowNo);
				row.createCell(113).setCellFormula("SUM(DG"+rowNo+":DI"+rowNo+")");
				row.createCell(114).setCellFormula("AB"+rowNo);
				row.createCell(115).setCellFormula("AZ"+rowNo);
				row.createCell(116).setCellFormula("BX"+rowNo);
				row.createCell(117).setCellFormula("SUM(DK"+rowNo+":DM"+rowNo+")");
				row.createCell(118).setCellFormula("AD"+rowNo);
				row.createCell(119).setCellFormula("BB"+rowNo);
				row.createCell(120).setCellFormula("BZ"+rowNo);
				row.createCell(121).setCellFormula("SUM(DO"+rowNo+":DQ"+rowNo+")");
				row.createCell(122).setCellFormula("AF"+rowNo);
				row.createCell(123).setCellFormula("BD"+rowNo);
				row.createCell(124).setCellFormula("CB"+rowNo);
				row.createCell(125).setCellFormula("SUM(DS"+rowNo+":DU"+rowNo+")");
				row.createCell(126).setCellFormula("AH"+rowNo);
				row.createCell(127).setCellFormula("BF"+rowNo);
				row.createCell(128).setCellFormula("CD"+rowNo);
				row.createCell(129).setCellFormula("SUM(DW"+rowNo+":DY"+rowNo+")");
				row.createCell(130).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH1").toString()));
				row.createCell(131).setCellFormula("CE"+rowNo+"*$EA"+rowNo);
				row.createCell(132).setCellFormula("CF"+rowNo+"*$EA"+rowNo);
				row.createCell(133).setCellFormula("CG"+rowNo+"*$EA"+rowNo);
				row.createCell(134).setCellFormula("SUM(EB"+rowNo+":ED"+rowNo+")");
				row.createCell(135).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH2").toString()));
				row.createCell(136).setCellFormula("CI"+rowNo+"*$EF"+rowNo);
				row.createCell(137).setCellFormula("CJ"+rowNo+"*$EF"+rowNo);
				row.createCell(138).setCellFormula("CK"+rowNo+"*$EF"+rowNo);
				row.createCell(139).setCellFormula("SUM(EG"+rowNo+":EI"+rowNo+")");
				row.createCell(140).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH3").toString()));
				row.createCell(141).setCellFormula("CM"+rowNo+"*$EK"+rowNo);
				row.createCell(142).setCellFormula("CN"+rowNo+"*$EK"+rowNo);
				row.createCell(143).setCellFormula("CO"+rowNo+"*$EK"+rowNo);
				row.createCell(144).setCellFormula("SUM(EL"+rowNo+":EN"+rowNo+")");
				row.createCell(145).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH4").toString()));
				row.createCell(146).setCellFormula("CQ"+rowNo+"*$EP"+rowNo);
				row.createCell(147).setCellFormula("CR"+rowNo+"*$EP"+rowNo);
				row.createCell(148).setCellFormula("CS"+rowNo+"*$EP"+rowNo);
				row.createCell(149).setCellFormula("SUM(EQ"+rowNo+":ES"+rowNo+")");
				row.createCell(150).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH5").toString()));
				row.createCell(151).setCellFormula("CU"+rowNo+"*$EU"+rowNo);
				row.createCell(152).setCellFormula("CV"+rowNo+"*$EU"+rowNo);
				row.createCell(153).setCellFormula("CW"+rowNo+"*$EU"+rowNo);
				row.createCell(154).setCellFormula("SUM(EV"+rowNo+":EX"+rowNo+")");
				row.createCell(155).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH6").toString()));
				row.createCell(156).setCellFormula("CY"+rowNo+"*$EZ"+rowNo);
				row.createCell(157).setCellFormula("CZ"+rowNo+"*$EZ"+rowNo);
				row.createCell(158).setCellFormula("DA"+rowNo+"*$EZ"+rowNo);
				row.createCell(159).setCellFormula("SUM(FA"+rowNo+":FC"+rowNo+")");
				row.createCell(160).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH7").toString()));
				row.createCell(161).setCellFormula("DC"+rowNo+"*$FE"+rowNo);
				row.createCell(162).setCellFormula("DD"+rowNo+"*$FE"+rowNo);
				row.createCell(163).setCellFormula("DE"+rowNo+"*$FE"+rowNo);
				row.createCell(164).setCellFormula("SUM(FF"+rowNo+":FH"+rowNo+")");
				row.createCell(165).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH8").toString()));
				row.createCell(166).setCellFormula("DG"+rowNo+"*$FJ"+rowNo);
				row.createCell(167).setCellFormula("DH"+rowNo+"*$FJ"+rowNo);
				row.createCell(168).setCellFormula("DI"+rowNo+"*$FJ"+rowNo);
				row.createCell(169).setCellFormula("SUM(FK"+rowNo+":FM"+rowNo+")");
				row.createCell(170).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH9").toString()));
				row.createCell(171).setCellFormula("DK"+rowNo+"*$FO"+rowNo);
				row.createCell(172).setCellFormula("DL"+rowNo+"*$FO"+rowNo);
				row.createCell(173).setCellFormula("DM"+rowNo+"*$FO"+rowNo);
				row.createCell(174).setCellFormula("SUM(FP"+rowNo+":FR"+rowNo+")");
				row.createCell(175).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH10").toString()));
				row.createCell(176).setCellFormula("DO"+rowNo+"*$FT"+rowNo);
				row.createCell(177).setCellFormula("DP"+rowNo+"*$FT"+rowNo);
				row.createCell(178).setCellFormula("DQ"+rowNo+"*$FT"+rowNo);
				row.createCell(179).setCellFormula("SUM(FU"+rowNo+":FW"+rowNo+")");
				row.createCell(180).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH11").toString()));
				row.createCell(181).setCellFormula("DS"+rowNo+"*$FY"+rowNo);
				row.createCell(182).setCellFormula("DT"+rowNo+"*$FY"+rowNo);
				row.createCell(183).setCellFormula("DU"+rowNo+"*$FY"+rowNo);
				row.createCell(184).setCellFormula("SUM(FZ"+rowNo+":GB"+rowNo+")");
				row.createCell(185).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH12").toString()));
				row.createCell(186).setCellFormula("DW"+rowNo+"*$GD"+rowNo);
				row.createCell(187).setCellFormula("DX"+rowNo+"*$GD"+rowNo);
				row.createCell(188).setCellFormula("DY"+rowNo+"*$GD"+rowNo);
				row.createCell(189).setCellFormula("SUM(GE"+rowNo+":GG"+rowNo+")");

				row.createCell(193).setCellFormula("SUM(GI"+rowNo+":GK"+rowNo+")");
				row.createCell(194).setCellValue(Double.parseDouble(map.get("QUANTITY_NEXTYEAR").toString()));
				row.createCell(195).setCellFormula("GI"+rowNo+"*$GM"+rowNo);
				row.createCell(196).setCellFormula("GJ"+rowNo+"*$GM"+rowNo);
				row.createCell(197).setCellFormula("GK"+rowNo+"*$GM"+rowNo);
				row.createCell(198).setCellFormula("SUM(GN"+rowNo+":GP"+rowNo+")");

				row.createCell(202).setCellFormula("SUM(GR"+rowNo+":GT"+rowNo+")");
				row.createCell(203).setCellValue(Double.parseDouble(map.get("QUANTITY_TWOYEAR").toString()));
				row.createCell(204).setCellFormula("GR"+rowNo+"*$GV"+rowNo);
				row.createCell(205).setCellFormula("GS"+rowNo+"*$GV"+rowNo);
				row.createCell(206).setCellFormula("GT"+rowNo+"*$GV"+rowNo);
				row.createCell(207).setCellFormula("SUM(GW"+rowNo+":GY"+rowNo+")");

				row.createCell(211).setCellFormula("SUM(HA"+rowNo+":HC"+rowNo+")");
				row.createCell(212).setCellValue(Double.parseDouble(map.get("QUANTITY_THREEYEAR").toString()));
				row.createCell(213).setCellFormula("HA"+rowNo+"*$HE"+rowNo);
				row.createCell(214).setCellFormula("HB"+rowNo+"*$HE"+rowNo);
				row.createCell(215).setCellFormula("HC"+rowNo+"*$HE"+rowNo);
				row.createCell(216).setCellFormula("SUM(GZ"+rowNo+":HB"+rowNo+")");

				row.createCell(220).setCellFormula("SUM(HJ"+rowNo+":HL"+rowNo+")");
				row.createCell(221).setCellValue(Double.parseDouble(map.get("QUANTITY_FOURYEAR").toString()));
				row.createCell(222).setCellFormula("HJ"+rowNo+"*$HN"+rowNo);
				row.createCell(223).setCellFormula("HK"+rowNo+"*$HN"+rowNo);
				row.createCell(224).setCellFormula("HL"+rowNo+"*$HN"+rowNo);
				row.createCell(225).setCellFormula("SUM(HO"+rowNo+":HQ"+rowNo+")");
				for (int i=0;i<226;i++){
					if(null==row.getCell(i)){
						continue;
					}
					row.getCell(i).setCellStyle(style);
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

	/**預測下載模板*/
	public Map<String,String> templateForecast(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預測表","銷售成本預測表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預測表","銷售成本預測表")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			int year=calendar.get(Calendar.YEAR);
			row.getCell(10).setCellValue("FY"+ String.valueOf(year).substring(2));
			String sql=this.templateVal("FY"+ String.valueOf(year).substring(2),"FIT_FORECAST_REVENUE") ;
			List<Map> list=budgetProductNoUnitCostDao.listMapBySql(sql);
			XSSFCellStyle style = workBook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(166,166,166)));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderColor(XSSFCellBorder.BorderSide.LEFT,new XSSFColor(new java.awt.Color(191,191,191)));
			style.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM,new XSSFColor(new java.awt.Color(191,191,191)));
			XSSFCellStyle lockStyle = workBook.createCellStyle();
			lockStyle.setLocked(true);
			lockStyle.setAlignment(HorizontalAlignment.CENTER);
			lockStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(217, 217, 217)));
			lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			int rowNo=3;
			for (Map map:list) {
				row=sheet.createRow(rowNo++);
				row.createCell(0).setCellValue(map.get("ENTITY").toString());
				row.createCell(1).setCellValue(map.get("MAKE_ENTITY").toString());
				row.createCell(2).setCellValue(map.get("INDUSTRY").toString());
				row.createCell(3).setCellValue(map.get("MAIN_BUSINESS").toString());
				row.createCell(4).setCellValue(map.get("THREE").toString());
				row.createCell(5).setCellValue(map.get("PRODUCT_SERIES")==null?"":map.get("PRODUCT_SERIES").toString());
				row.createCell(6).setCellValue(map.get("PRODUCT_NO")==null?"":map.get("PRODUCT_NO").toString());
				row.createCell(7).setCellValue(map.get("LOAN_CUSTOMER").toString());
				row.createCell(8).setCellValue(map.get("END_CUSTOMER").toString());
				row.createCell(9).setCellValue(map.get("TRADE_TYPE").toString());

				row.createCell(10).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(12).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(14).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(16).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(18).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(20).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(22).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(24).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(26).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(28).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(30).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(32).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));

				row.createCell(34).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(36).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(38).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(40).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(42).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(44).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(46).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(48).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(50).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(52).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(54).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(56).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));

				row.createCell(58).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(60).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(62).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(64).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(66).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(68).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(70).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(72).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(74).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(76).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(78).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(80).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));

				row.createCell(82).setCellFormula("L"+rowNo);
				row.createCell(83).setCellFormula("AJ"+rowNo);
				row.createCell(84).setCellFormula("BH"+rowNo);
				row.createCell(85).setCellFormula("SUM(CE"+rowNo+":CG"+rowNo+")");
				row.createCell(86).setCellFormula("N"+rowNo);
				row.createCell(87).setCellFormula("AL"+rowNo);
				row.createCell(88).setCellFormula("BJ"+rowNo);
				row.createCell(89).setCellFormula("SUM(CI"+rowNo+":CK"+rowNo+")");
				row.createCell(90).setCellFormula("P"+rowNo);
				row.createCell(91).setCellFormula("AN"+rowNo);
				row.createCell(92).setCellFormula("BL"+rowNo);
				row.createCell(93).setCellFormula("SUM(CM"+rowNo+":CO"+rowNo+")");
				row.createCell(94).setCellFormula("R"+rowNo);
				row.createCell(95).setCellFormula("AP"+rowNo);
				row.createCell(96).setCellFormula("BN"+rowNo);
				row.createCell(97).setCellFormula("SUM(CQ"+rowNo+":CS"+rowNo+")");
				row.createCell(98).setCellFormula("T"+rowNo);
				row.createCell(99).setCellFormula("AR"+rowNo);
				row.createCell(100).setCellFormula("BP"+rowNo);
				row.createCell(101).setCellFormula("SUM(CU"+rowNo+":CW"+rowNo+")");
				row.createCell(102).setCellFormula("V"+rowNo);
				row.createCell(103).setCellFormula("AT"+rowNo);
				row.createCell(104).setCellFormula("BR"+rowNo);
				row.createCell(105).setCellFormula("SUM(CY"+rowNo+":DA"+rowNo+")");
				row.createCell(106).setCellFormula("X"+rowNo);
				row.createCell(107).setCellFormula("AV"+rowNo);
				row.createCell(108).setCellFormula("BT"+rowNo);
				row.createCell(109).setCellFormula("SUM(DC"+rowNo+":DE"+rowNo+")");
				row.createCell(110).setCellFormula("Z"+rowNo);
				row.createCell(111).setCellFormula("AX"+rowNo);
				row.createCell(112).setCellFormula("BV"+rowNo);
				row.createCell(113).setCellFormula("SUM(DG"+rowNo+":DI"+rowNo+")");
				row.createCell(114).setCellFormula("AB"+rowNo);
				row.createCell(115).setCellFormula("AZ"+rowNo);
				row.createCell(116).setCellFormula("BX"+rowNo);
				row.createCell(117).setCellFormula("SUM(DK"+rowNo+":DM"+rowNo+")");
				row.createCell(118).setCellFormula("AD"+rowNo);
				row.createCell(119).setCellFormula("BB"+rowNo);
				row.createCell(120).setCellFormula("BZ"+rowNo);
				row.createCell(121).setCellFormula("SUM(DO"+rowNo+":DQ"+rowNo+")");
				row.createCell(122).setCellFormula("AF"+rowNo);
				row.createCell(123).setCellFormula("BD"+rowNo);
				row.createCell(124).setCellFormula("CB"+rowNo);
				row.createCell(125).setCellFormula("SUM(DS"+rowNo+":DU"+rowNo+")");
				row.createCell(126).setCellFormula("AH"+rowNo);
				row.createCell(127).setCellFormula("BF"+rowNo);
				row.createCell(128).setCellFormula("CD"+rowNo);
				row.createCell(129).setCellFormula("SUM(DW"+rowNo+":DY"+rowNo+")");
				row.createCell(130).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH1").toString()));
				row.createCell(131).setCellFormula("CE"+rowNo+"*$EA"+rowNo);
				row.createCell(132).setCellFormula("CF"+rowNo+"*$EA"+rowNo);
				row.createCell(133).setCellFormula("CG"+rowNo+"*$EA"+rowNo);
				row.createCell(134).setCellFormula("SUM(EB"+rowNo+":ED"+rowNo+")");
				row.createCell(135).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH2").toString()));
				row.createCell(136).setCellFormula("CI"+rowNo+"*$EF"+rowNo);
				row.createCell(137).setCellFormula("CJ"+rowNo+"*$EF"+rowNo);
				row.createCell(138).setCellFormula("CK"+rowNo+"*$EF"+rowNo);
				row.createCell(139).setCellFormula("SUM(EG"+rowNo+":EI"+rowNo+")");
				row.createCell(140).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH3").toString()));
				row.createCell(141).setCellFormula("CM"+rowNo+"*$EK"+rowNo);
				row.createCell(142).setCellFormula("CN"+rowNo+"*$EK"+rowNo);
				row.createCell(143).setCellFormula("CO"+rowNo+"*$EK"+rowNo);
				row.createCell(144).setCellFormula("SUM(EL"+rowNo+":EN"+rowNo+")");
				row.createCell(145).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH4").toString()));
				row.createCell(146).setCellFormula("CQ"+rowNo+"*$EP"+rowNo);
				row.createCell(147).setCellFormula("CR"+rowNo+"*$EP"+rowNo);
				row.createCell(148).setCellFormula("CS"+rowNo+"*$EP"+rowNo);
				row.createCell(149).setCellFormula("SUM(EQ"+rowNo+":ES"+rowNo+")");
				row.createCell(150).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH5").toString()));
				row.createCell(151).setCellFormula("CU"+rowNo+"*$EU"+rowNo);
				row.createCell(152).setCellFormula("CV"+rowNo+"*$EU"+rowNo);
				row.createCell(153).setCellFormula("CW"+rowNo+"*$EU"+rowNo);
				row.createCell(154).setCellFormula("SUM(EV"+rowNo+":EX"+rowNo+")");
				row.createCell(155).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH6").toString()));
				row.createCell(156).setCellFormula("CY"+rowNo+"*$EZ"+rowNo);
				row.createCell(157).setCellFormula("CZ"+rowNo+"*$EZ"+rowNo);
				row.createCell(158).setCellFormula("DA"+rowNo+"*$EZ"+rowNo);
				row.createCell(159).setCellFormula("SUM(FA"+rowNo+":FC"+rowNo+")");
				row.createCell(160).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH7").toString()));
				row.createCell(161).setCellFormula("DC"+rowNo+"*$FE"+rowNo);
				row.createCell(162).setCellFormula("DD"+rowNo+"*$FE"+rowNo);
				row.createCell(163).setCellFormula("DE"+rowNo+"*$FE"+rowNo);
				row.createCell(164).setCellFormula("SUM(FF"+rowNo+":FH"+rowNo+")");
				row.createCell(165).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH8").toString()));
				row.createCell(166).setCellFormula("DG"+rowNo+"*$FJ"+rowNo);
				row.createCell(167).setCellFormula("DH"+rowNo+"*$FJ"+rowNo);
				row.createCell(168).setCellFormula("DI"+rowNo+"*$FJ"+rowNo);
				row.createCell(169).setCellFormula("SUM(FK"+rowNo+":FM"+rowNo+")");
				row.createCell(170).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH9").toString()));
				row.createCell(171).setCellFormula("DK"+rowNo+"*$FO"+rowNo);
				row.createCell(172).setCellFormula("DL"+rowNo+"*$FO"+rowNo);
				row.createCell(173).setCellFormula("DM"+rowNo+"*$FO"+rowNo);
				row.createCell(174).setCellFormula("SUM(FP"+rowNo+":FR"+rowNo+")");
				row.createCell(175).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH10").toString()));
				row.createCell(176).setCellFormula("DO"+rowNo+"*$FT"+rowNo);
				row.createCell(177).setCellFormula("DP"+rowNo+"*$FT"+rowNo);
				row.createCell(178).setCellFormula("DQ"+rowNo+"*$FT"+rowNo);
				row.createCell(179).setCellFormula("SUM(FU"+rowNo+":FW"+rowNo+")");
				row.createCell(180).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH11").toString()));
				row.createCell(181).setCellFormula("DS"+rowNo+"*$FY"+rowNo);
				row.createCell(182).setCellFormula("DT"+rowNo+"*$FY"+rowNo);
				row.createCell(183).setCellFormula("DU"+rowNo+"*$FY"+rowNo);
				row.createCell(184).setCellFormula("SUM(FZ"+rowNo+":GB"+rowNo+")");
				row.createCell(185).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH12").toString()));
				row.createCell(186).setCellFormula("DW"+rowNo+"*$GD"+rowNo);
				row.createCell(187).setCellFormula("DX"+rowNo+"*$GD"+rowNo);
				row.createCell(188).setCellFormula("DY"+rowNo+"*$GD"+rowNo);
				row.createCell(189).setCellFormula("SUM(GE"+rowNo+":GG"+rowNo+")");
				for (int i=0;i<190;i++){
					if(null==row.getCell(i)){
						continue;
					}
					row.getCell(i).setCellStyle(style);
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

	/**預算上傳模板填充的數據*/
	public String templateVal(String year,String tableName){
		String corporationCode = SecurityUtils.getCorporationCode();
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sql="SELECT  b.entity,\n" +
				"        b.make_entity,\n" +
				"        b.industry,\n" +
				"        b.main_business,\n" +
				"        b.three,\n" +
				"        b.product_series, \n" +
				"        b.product_no, \n" +
				"        b.loan_customer,\n" +
				"        b.end_customer,\n" +
				"        b.trade_type,\n" +
				"        sum(nvl(t.material_cost,0)) material_cost, \n" +
				"        sum(nvl(t.laber_cost,0)) laber_cost, \n" +
				"        sum(nvl(t.overhead_cost,0)) + sum(nvl(t.outsite_processing_cost,0)) overhead_cost,  \n" +
				"        sum(nvl(b.quantity_month1,0)) quantity_month1, \n" +
				"        sum(nvl(b.quantity_month2,0)) quantity_month2, \n" +
				"        sum(nvl(b.quantity_month3,0)) quantity_month3, \n" +
				"        sum(nvl(b.quantity_month4,0)) quantity_month4, \n" +
				"        sum(nvl(b.quantity_month5,0)) quantity_month5, \n" +
				"        sum(nvl(b.quantity_month6,0)) quantity_month6, \n" +
				"        sum(nvl(b.quantity_month7,0)) quantity_month7, \n" +
				"        sum(nvl(b.quantity_month8,0)) quantity_month8, \n" +
				"        sum(nvl(b.quantity_month9,0)) quantity_month9, \n" +
				"        sum(nvl(b.quantity_month10,0)) quantity_month10, \n" +
				"        sum(nvl(b.quantity_month11,0)) quantity_month11, \n" +
				"        sum(nvl(b.quantity_month12,0)) quantity_month12, \n" +
				"        sum(nvl(b.quantity,0)) quantity, \n" +
				"        sum(nvl(b.quantity_nextyear,0)) quantity_nextyear, \n" +
				"        sum(nvl(b.quantity_twoyear,0)) quantity_twoyear, \n" +
				"        sum(nvl(b.quantity_threeyear,0)) quantity_threeyear, \n" +
				"        sum(nvl(b.quantity_fouryear,0)) quantity_fouryear \n" +
				"FROM epmods.if_ebs_ar_revenue_dtl_cst_v2 t, \n" +
				"     (SELECT a.* \n" +
				"        FROM epmods."+tableName+" a \n" +
				"       WHERE a.version = 'V00' \n" +
				"         AND a.year = 'FY' || (to_char(SYSDATE-30,'YY') + 1)) b \n" +
				"WHERE t.p_n(+) = b.product_no \n" +
				" AND t.entity_code(+) = b.ou \n" +
				" AND t.rn(+)= 1 \n" +
				" and b.product_no!=b.product_series and b.year='"+year+"' and b.create_name='"+loginUser.getUsername()+"'";
		if (StringUtils.isNotEmpty(corporationCode)) {
			sql+=" and (";
			for (String string : corporationCode.split(",")) {
				sql+=" b.entity like '"+string+"%' or";
			}
			sql=sql.substring(0,sql.length()-2)+")";
		}else{
			sql=sql+" b.entity=1";
		}
		sql+=" group by  \n" +
				" b.entity,\n" +
				"        b.make_entity,\n" +
				"        b.industry,\n" +
				"        b.main_business,\n" +
				"        b.three,\n" +
				"        b.product_no, \n" +
				"        b.product_series, \n" +
				"        b.loan_customer,\n" +
				"        b.end_customer,\n" +
				"        b.trade_type";
		return sql;
	}

	/**預算簡易版*/
	public Map<String,String> simplifyTemplateBudget(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預算表_簡易模版","銷售成本預算表_簡易模版")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預算表_簡易模版","銷售成本預算表_簡易模版")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			//預算應爲測試需要先把年份校驗放開
//			int year=calendar.get(Calendar.YEAR);
			int year=calendar.get(Calendar.YEAR)-1;
			row.getCell(2).setCellValue("FY"+ String.valueOf(year+1).substring(2));
			row.getCell(50).setCellValue("FY"+ String.valueOf(year+1).substring(2));
			row.getCell(54).setCellValue("FY"+ String.valueOf(year+2).substring(2));
			row.getCell(58).setCellValue("FY"+ String.valueOf(year+3).substring(2));
			row.getCell(62).setCellValue("FY"+ String.valueOf(year+4).substring(2));
			row.getCell(66).setCellValue("FY"+ String.valueOf(year+5).substring(2));
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


	/**預測簡易版*/
	public Map<String,String> simplifyTemplateForecast(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預測表_簡易模版","銷售成本預測表_簡易模版")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預測表_簡易模版","銷售成本預測表_簡易模版")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			int year=calendar.get(Calendar.YEAR);
			row.getCell(2).setCellValue("FY"+ String.valueOf(year).substring(2));
			row.getCell(50).setCellValue("FY"+ String.valueOf(year).substring(2));
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


	/**預算下載數據*/
	public Map<String,String> downloadBudget(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預算表","銷售成本預算表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預算表_下载","銷售成本預算表_下载")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);

			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));

			row.getCell(10).setCellValue(y);
			row.getCell(70).setCellValue("FY"+(year));
			row.getCell(74).setCellValue("FY"+(year+1));
			row.getCell(78).setCellValue("FY"+(year+2));
			row.getCell(82).setCellValue("FY"+(year+3));
			row.getCell(86).setCellValue("FY"+(year+4));

			String sql="select * from FIT_BUDGET_PRODUCT_UNITCOST_V1 where YEAR='"+y+"'";
			if (null!=version && StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			String sbuVal="";
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+sbuStr+")";
			List<Map> sbuMap=budgetProductNoUnitCostDao.listMapBySql(sbusql);
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
			sql+="order by entity,product,Id";
			List<Object[]> dataList = budgetProductNoUnitCostDao.findPageBySql(pageRequest, sql).getResult();
			int col=0;
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					col=0;
					for (int i = 3; i < objects.length; i++) {
						Cell cell = contentRow.createCell(col);
						col++;
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>12 && i<98) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = budgetProductNoUnitCostDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							col=0;
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 3; i < objects.length-1; i++) {
								Cell cell = contentRow.createCell(col);
								col++;
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>12 && i<98) {
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
	public Map<String,String> downloadForecast(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預測表","銷售成本預測表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售成本預測表_下载","銷售成本預測表_下载")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);

			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(10).setCellValue(y);
			row.getCell(70).setCellValue("FY"+(year));

			String sql="select * from FIT_FORECAST_SALES_COST_V where YEAR='"+y+"'";
			if (null!=version && StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			String sbuVal="";
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+sbuStr+")";
			List<Map> sbuMap=budgetProductNoUnitCostDao.listMapBySql(sbusql);
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
			sql+="order by entity,product,Id";
			List<Object[]> dataList = budgetProductNoUnitCostDao.findPageBySql(pageRequest, sql).getResult();
			int col=0;
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					col=0;
					for (int i = 3; i < objects.length; i++) {
						if(i>76){
							break;
						}
						Cell cell = contentRow.createCell(col);
						col++;
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>12 && i<77) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = budgetProductNoUnitCostDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							col=0;
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 3; i < objects.length; i++) {
								if(i>76){
									break;
								}
								Cell cell = contentRow.createCell(col);
								col++;
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>12 && i<77) {
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
		//預算應爲測試需要先把年份校驗放開
//		int year=calendar.get(Calendar.YEAR)+1;
		int year=calendar.get(Calendar.YEAR);
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from FIT_BUDGET_PRODUCT_UNIT_COST where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = budgetProductNoUnitCostDao.listMapBySql(sqlVersion);
		if(null ==maps.get(0).get("VERSION")){
			sqlVersion="No finalizable data detected_沒有檢查到可定版的數據！";
		}else if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into FIT_BUDGET_PRODUCT_UNIT_COST (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,\n" +
				"'"+sqlVersion+"' version,year, \n" +
				" entity, \n" +
				" product, \n" +
				" sales_quantity1, \n" +
				" material_cost1, \n" +
				" labor_cost1, \n" +
				" manufacture_cost1, \n" +
				" selling_cost1, \n" +
				" sales_quantity2, \n" +
				" material_cost2, \n" +
				" labor_cost2, \n" +
				" manufacture_cost2, \n" +
				" selling_cost2, \n" +
				" sales_quantity3, \n" +
				" material_cost3, \n" +
				" labor_cost3, \n" +
				" manufacture_cost3, \n" +
				" selling_cost3, \n" +
				" sales_quantity4, \n" +
				" material_cost4, \n" +
				" labor_cost4, \n" +
				" manufacture_cost4, \n" +
				" selling_cost4, \n" +
				" sales_quantity5, \n" +
				" material_cost5, \n" +
				" labor_cost5, \n" +
				" manufacture_cost5, \n" +
				" selling_cost5, \n" +
				" sales_quantity6, \n" +
				" material_cost6, \n" +
				" labor_cost6, \n" +
				" manufacture_cost6, \n" +
				" selling_cost6, \n" +
				" sales_quantity7, \n" +
				" material_cost7, \n" +
				" labor_cost7, \n" +
				" manufacture_cost7, \n" +
				" selling_cost7, \n" +
				" sales_quantity8, \n" +
				" material_cost8, \n" +
				" labor_cost8, \n" +
				" manufacture_cost8, \n" +
				" selling_cost8, \n" +
				" sales_quantity9, \n" +
				" material_cost9, \n" +
				" labor_cost9, \n" +
				" manufacture_cost9, \n" +
				" selling_cost9, \n" +
				" sales_quantity10, \n" +
				" material_cost10, \n" +
				" labor_cost10, \n" +
				" manufacture_cost10, \n" +
				" selling_cost10, \n" +
				" sales_quantity11, \n" +
				" material_cost11, \n" +
				" labor_cost11, \n" +
				" manufacture_cost11, \n" +
				" selling_cost11, \n" +
				" sales_quantity12, \n" +
				" material_cost12, \n" +
				" labor_cost12, \n" +
				" manufacture_cost12, \n" +
				" selling_cost12, \n" +
				" sales_quantity, \n" +
				" material_cost, \n" +
				" labor_cost, \n" +
				" manufacture_cost, \n" +
				" selling_cost, \n" +
				" sales_quantity_nextyear, \n" +
				" material_cost_nextyear, \n" +
				" labor_cost_nextyear, \n" +
				" manufacture_cost_nextyear, \n" +
				" selling_cost_nextyear, \n" +
				" sales_quantity_twoyear, \n" +
				" material_cost_twoyear, \n" +
				" labor_cost_twoyear, \n" +
				" manufacture_cost_twoyear, \n" +
				" selling_cost_twoyear, \n" +
				" sales_quantity_threeyear, \n" +
				" material_cost_threeyear, \n" +
				" labor_cost_threeyear, \n" +
				" manufacture_cost_threeyear, \n" +
				" selling_cost_threeyear, \n" +
				" sales_quantity_fouryear, \n" +
				" material_cost_fouryear, \n" +
				" labor_cost_fouryear, \n" +
				" manufacture_cost_fouryear, \n" +
				" selling_cost_fouryear, \n" +
				" create_name, \n" +
				" create_date, \n" +
				"sysdate version_date,'"+loginUser.getUsername()+"' version_name,\n" +
				" product_no, \n" +
				" trade_type, \n" +
				" make_entity, \n" +
				"industry, \n" +
				"main_business, \n" +
				"three, \n" +
				"loan_customer, \n" +
				"end_customer" +
				" from FIT_BUDGET_PRODUCT_UNIT_COST where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		budgetProductNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

	/**預測版本控制*/
	public String versionForecast(){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from FIT_FORECAST_SALES_COST where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = forecastSalesCostDao.listMapBySql(sqlVersion);
		if(null ==maps.get(0).get("VERSION")){
			sqlVersion="No finalizable data detected_沒有檢查到可定版的數據！";
		}else if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into FIT_FORECAST_SALES_COST (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,\n" +
				"'"+sqlVersion+"' version,year, \n" +
				" entity, \n" +
				" product, \n" +
				" sales_quantity1, \n" +
				" material_cost1, \n" +
				" labor_cost1, \n" +
				" manufacture_cost1, \n" +
				" sales_quantity2, \n" +
				" material_cost2, \n" +
				" labor_cost2, \n" +
				" manufacture_cost2, \n" +
				" sales_quantity3, \n" +
				" material_cost3, \n" +
				" labor_cost3, \n" +
				" manufacture_cost3, \n" +
				" sales_quantity4, \n" +
				" material_cost4, \n" +
				" labor_cost4, \n" +
				" manufacture_cost4, \n" +
				" sales_quantity5, \n" +
				" material_cost5, \n" +
				" labor_cost5, \n" +
				" manufacture_cost5, \n" +
				" sales_quantity6, \n" +
				" material_cost6, \n" +
				" labor_cost6, \n" +
				" manufacture_cost6, \n" +
				" sales_quantity7, \n" +
				" material_cost7, \n" +
				" labor_cost7, \n" +
				" manufacture_cost7, \n" +
				" sales_quantity8, \n" +
				" material_cost8, \n" +
				" labor_cost8, \n" +
				" manufacture_cost8, \n" +
				" sales_quantity9, \n" +
				" material_cost9, \n" +
				" labor_cost9, \n" +
				" manufacture_cost9, \n" +
				" sales_quantity10, \n" +
				" material_cost10, \n" +
				" labor_cost10, \n" +
				" manufacture_cost10, \n" +
				" sales_quantity11, \n" +
				" material_cost11, \n" +
				" labor_cost11, \n" +
				" manufacture_cost11, \n" +
				" sales_quantity12, \n" +
				" material_cost12, \n" +
				" labor_cost12, \n" +
				" manufacture_cost12, \n" +
				" create_name, \n" +
				" create_date, \n" +
				"sysdate version_date,'"+loginUser.getUsername()+"' version_name,\n" +
				" product_no, \n" +
				" trade_type, \n" +
				" make_entity, \n" +
				"industry, \n" +
				"main_business, \n" +
				"three, \n" +
				"loan_customer, \n" +
				"end_customer" +
				" from FIT_FORECAST_SALES_COST where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		forecastSalesCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

	public void deleteForecast(String id) throws Exception {
		forecastSalesCostDao.delete(id);
	}
}
