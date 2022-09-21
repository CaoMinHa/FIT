package foxconn.fit.service.budget;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.BudgetProductNoUnitCostDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.budget.BudgetProductNoUnitCost;
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
		int year=calendar.get(Calendar.YEAR)+1;
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		model.addAttribute("versionList", this.versionVal());
		return model;
	}

	public List<String> versionVal(){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select distinct version  from FIT_BUDGET_PRODUCT_UNIT_COST where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version ";
		List<String> versionList=budgetProductNoUnitCostDao.listBySql(sqlVersion);
		return  versionList;
	}

	/**頁面查詢*/
	public String list(String year,String version,String entity){
		String sql="select * from FIT_BUDGET_PRODUCT_UNIT_COST where 1=1";
		if (null!=year&&StringUtils.isNotEmpty(year)) {
			sql+=" and YEAR='"+year+"'";
		}
		if (null!=version && StringUtils.isNotEmpty(version)) {
			sql+=" and version='"+version+"'";
		}

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

		sql+=" order by year,entity,make_entity";
		return sql;
	}

	/**數據上傳*/
	public String upload(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest) {
		try {
//			獲取當前用戶的SBU權限
			List<String> tarList = new ArrayList<String>();
			String corporationCode = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode)) {
				for (String string : corporationCode.split(",")) {
					tarList.add(string);
				}
			}
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
				if(sheet.getSheetName().equals("產品料號單位成本")){
					COLUMN_NUM =220;
					v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(4), 0);
				}else if(sheet.getSheetName().equals("簡易版產品料號單位成本")){
					COLUMN_NUM =70;
					v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(2), 0);
				}else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "請使用模板上傳數據！", "Please use the template to upload data"));
					return result.getJson();
				}
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
				Calendar calendar = Calendar.getInstance();
				String year = Integer.toString(calendar.get(Calendar.YEAR) + 1);
				Assert.isTrue(year.substring(2).equals(v_year.substring(2)), instrumentClassService.getLanguage(locale, "僅可上傳明年的預算數據！", "Only next year's budget data can be uploaded"));
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
				String check = "";
				for (int i = 3; i < rowNum; i++) {
					Row row = sheet.getRow(i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(0), i);
					if(row == null||entity.length()<1||"".equals(entity)){
						continue;
					}
					sbuList.add(entity.split("_")[0]);
					check = instrumentClassService.getDiffrent(sbuList, tarList);
					if (!"".equalsIgnoreCase(check.trim()) && check.length() > 0) {
						continue;
					}
					BudgetProductNoUnitCost budgetProductNoUnitCost = new BudgetProductNoUnitCost();
					entityList.add(entity);
					if(COLUMN_NUM==70){
						tradeTypeList.add(ExcelUtil.getCellStringValue(row.getCell(1), i));
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
					}else if(COLUMN_NUM==220){
						budgetProductNoUnitCost.setMakeEntity(ExcelUtil.getCellStringValue(row.getCell(1), i));
						budgetProductNoUnitCost.setProduct(ExcelUtil.getCellStringValue(row.getCell(2), i));
						budgetProductNoUnitCost.setProductNo(ExcelUtil.getCellStringValue(row.getCell(3), i));

						budgetProductNoUnitCost.setSalesQuantity1(ExcelUtil.getCellStringValue(row.getCell(124), i));
						budgetProductNoUnitCost.setMaterialCost1(ExcelUtil.getCellStringValue(row.getCell(125), i));
						budgetProductNoUnitCost.setLaborCost1(ExcelUtil.getCellStringValue(row.getCell(126),i));
						budgetProductNoUnitCost.setManufactureCost1(ExcelUtil.getCellStringValue(row.getCell(127),i));

						budgetProductNoUnitCost.setSalesQuantity2(ExcelUtil.getCellStringValue(row.getCell(129), i));
						budgetProductNoUnitCost.setMaterialCost2(ExcelUtil.getCellStringValue(row.getCell(130), i));
						budgetProductNoUnitCost.setLaborCost2(ExcelUtil.getCellStringValue(row.getCell(131),i));
						budgetProductNoUnitCost.setManufactureCost2(ExcelUtil.getCellStringValue(row.getCell(132),i));

						budgetProductNoUnitCost.setSalesQuantity3(ExcelUtil.getCellStringValue(row.getCell(134), i));
						budgetProductNoUnitCost.setMaterialCost3(ExcelUtil.getCellStringValue(row.getCell(135), i));
						budgetProductNoUnitCost.setLaborCost3(ExcelUtil.getCellStringValue(row.getCell(136),i));
						budgetProductNoUnitCost.setManufactureCost3(ExcelUtil.getCellStringValue(row.getCell(137),i));

						budgetProductNoUnitCost.setSalesQuantity4(ExcelUtil.getCellStringValue(row.getCell(139), i));
						budgetProductNoUnitCost.setMaterialCost4(ExcelUtil.getCellStringValue(row.getCell(140), i));
						budgetProductNoUnitCost.setLaborCost4(ExcelUtil.getCellStringValue(row.getCell(141),i));
						budgetProductNoUnitCost.setManufactureCost4(ExcelUtil.getCellStringValue(row.getCell(142),i));

						budgetProductNoUnitCost.setSalesQuantity5(ExcelUtil.getCellStringValue(row.getCell(144), i));
						budgetProductNoUnitCost.setMaterialCost5(ExcelUtil.getCellStringValue(row.getCell(145), i));
						budgetProductNoUnitCost.setLaborCost5(ExcelUtil.getCellStringValue(row.getCell(146),i));
						budgetProductNoUnitCost.setManufactureCost5(ExcelUtil.getCellStringValue(row.getCell(147),i));

						budgetProductNoUnitCost.setSalesQuantity6(ExcelUtil.getCellStringValue(row.getCell(149), i));
						budgetProductNoUnitCost.setMaterialCost6(ExcelUtil.getCellStringValue(row.getCell(150), i));
						budgetProductNoUnitCost.setLaborCost6(ExcelUtil.getCellStringValue(row.getCell(151),i));
						budgetProductNoUnitCost.setManufactureCost6(ExcelUtil.getCellStringValue(row.getCell(152),i));

						budgetProductNoUnitCost.setSalesQuantity7(ExcelUtil.getCellStringValue(row.getCell(154), i));
						budgetProductNoUnitCost.setMaterialCost7(ExcelUtil.getCellStringValue(row.getCell(155), i));
						budgetProductNoUnitCost.setLaborCost7(ExcelUtil.getCellStringValue(row.getCell(156),i));
						budgetProductNoUnitCost.setManufactureCost7(ExcelUtil.getCellStringValue(row.getCell(157),i));

						budgetProductNoUnitCost.setSalesQuantity8(ExcelUtil.getCellStringValue(row.getCell(159), i));
						budgetProductNoUnitCost.setMaterialCost8(ExcelUtil.getCellStringValue(row.getCell(160), i));
						budgetProductNoUnitCost.setLaborCost8(ExcelUtil.getCellStringValue(row.getCell(161),i));
						budgetProductNoUnitCost.setManufactureCost8(ExcelUtil.getCellStringValue(row.getCell(162),i));

						budgetProductNoUnitCost.setSalesQuantity9(ExcelUtil.getCellStringValue(row.getCell(164), i));
						budgetProductNoUnitCost.setMaterialCost9(ExcelUtil.getCellStringValue(row.getCell(165), i));
						budgetProductNoUnitCost.setLaborCost9(ExcelUtil.getCellStringValue(row.getCell(166),i));
						budgetProductNoUnitCost.setManufactureCost9(ExcelUtil.getCellStringValue(row.getCell(167),i));

						budgetProductNoUnitCost.setSalesQuantity10(ExcelUtil.getCellStringValue(row.getCell(169), i));
						budgetProductNoUnitCost.setMaterialCost10(ExcelUtil.getCellStringValue(row.getCell(170),i));
						budgetProductNoUnitCost.setLaborCost10(ExcelUtil.getCellStringValue(row.getCell(171),i));
						budgetProductNoUnitCost.setManufactureCost10(ExcelUtil.getCellStringValue(row.getCell(172),i));

						budgetProductNoUnitCost.setSalesQuantity11(ExcelUtil.getCellStringValue(row.getCell(174), i));
						budgetProductNoUnitCost.setMaterialCost11(ExcelUtil.getCellStringValue(row.getCell(175),i));
						budgetProductNoUnitCost.setLaborCost11(ExcelUtil.getCellStringValue(row.getCell(176),i));
						budgetProductNoUnitCost.setManufactureCost11(ExcelUtil.getCellStringValue(row.getCell(177),i));

						budgetProductNoUnitCost.setSalesQuantity12(ExcelUtil.getCellStringValue(row.getCell(179), i));
						budgetProductNoUnitCost.setMaterialCost12(ExcelUtil.getCellStringValue(row.getCell(180),i));
						budgetProductNoUnitCost.setLaborCost12(ExcelUtil.getCellStringValue(row.getCell(181),i));
						budgetProductNoUnitCost.setManufactureCost12(ExcelUtil.getCellStringValue(row.getCell(182),i));

						budgetProductNoUnitCost.setSalesQuantityNextyear(ExcelUtil.getCellStringValue(row.getCell(188), i));
						budgetProductNoUnitCost.setMaterialCostNextyear(ExcelUtil.getCellStringValue(row.getCell(189),i));
						budgetProductNoUnitCost.setLaborCostNextyear(ExcelUtil.getCellStringValue(row.getCell(190),i));
						budgetProductNoUnitCost.setManufactureCostNextyear(ExcelUtil.getCellStringValue(row.getCell(191),i));

						budgetProductNoUnitCost.setSalesQuantityTwoyear(ExcelUtil.getCellStringValue(row.getCell(197), i));
						budgetProductNoUnitCost.setMaterialCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(198),i));
						budgetProductNoUnitCost.setLaborCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(199),i));
						budgetProductNoUnitCost.setManufactureCostTwoyear(ExcelUtil.getCellStringValue(row.getCell(200),i));

						budgetProductNoUnitCost.setSalesQuantityThreeyear(ExcelUtil.getCellStringValue(row.getCell(206), i));
						budgetProductNoUnitCost.setMaterialCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(207),i));
						budgetProductNoUnitCost.setLaborCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(208),i));
						budgetProductNoUnitCost.setManufactureCostThreeyear(ExcelUtil.getCellStringValue(row.getCell(209),i));

						budgetProductNoUnitCost.setSalesQuantityFouryear(ExcelUtil.getCellStringValue(row.getCell(215), i));
						budgetProductNoUnitCost.setMaterialCostFouryear(ExcelUtil.getCellStringValue(row.getCell(216),i));
						budgetProductNoUnitCost.setLaborCostFouryear(ExcelUtil.getCellStringValue(row.getCell(217),i));
						budgetProductNoUnitCost.setManufactureCostFouryear(ExcelUtil.getCellStringValue(row.getCell(218),i));
					}
					budgetProductNoUnitCost.setEntity(ExcelUtil.getCellStringValue(row.getCell(0), i));
					budgetProductNoUnitCost.setYear(v_year);
					budgetProductNoUnitCost.setVersion("V00");
					budgetProductNoUnitCost.setId(UUID.randomUUID().toString());
					UserDetailImpl loginUser = SecurityUtils.getLoginUser();
					budgetProductNoUnitCost.setCreateName(loginUser.getUsername());
					budgetProductNoUnitCost.setCreateDate(new Date());
					list.add(budgetProductNoUnitCost);
				}
				if (!list.isEmpty()) {
				     if(COLUMN_NUM==70){
						 /**SBU_法人校驗*/
						 check=this.check(entityList,EnumDimensionType.Entity.getCode());
						 if (!check.equals("") && check.length() > 0){
							 result.put("flag", "fail");
							 result.put("msg", "以下【SBU_法人】在【維度表】没有找到---> " + check);
							 return result.getJson();
						 }
						 /**交易類型**/
						 check=this.check(tradeTypeList,EnumDimensionType.View.getCode());
						 if (!check.equals("") && check.length() > 0){
							 result.put("flag", "fail");
							 result.put("msg", "以下【交易類型】在【維度表】没有找到---> "+check);
							 return result.getJson();
						 }
				     }
//				     else if(COLUMN_NUM==220){
//
//					 }
					this.saveBatch(list,v_year,instrumentClassService.removeDuplicate(entityList));
				} else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
				}
				check = instrumentClassService.getDiffrent(sbuList, tarList);
				if (!"".equalsIgnoreCase(check.trim()) && check.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下數據未上傳成功，請檢查您是否具備該SBU權限。--------->" + check, "The following data fails to be uploaded. Check whether you have the SBU permission--------->" + check));
				}
			} else {
				result.put("flag", "fail");
				result.put("msg", instrumentClassService.getLanguage(locale, "对不起,未接收到上传的文件", "Unreceived File"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}

	/**check*/
	public String check(List<String> list,String type){
		list = instrumentClassService.removeDuplicate(list);
		List<String> checkList = this.listBySql("select distinct trim(alias) from fit_dimension where type='" + type + "'");
		String check = instrumentClassService.getDiffrent(list, checkList);
		return check;
	}


	/**保存數據*/
	public void saveBatch(List<BudgetProductNoUnitCost> list,String year,List<String> entityList) throws Exception {
		String sql="delete from FIT_BUDGET_PRODUCT_UNIT_COST where VERSION='V00' and YEAR='"+year+"' and ENTITY in(";
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

	/**下載模板*/
	public Map<String,String> template(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"產品料號單位成本","產品料號單位成本")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"產品料號單位成本","產品料號單位成本")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			int year=calendar.get(Calendar.YEAR);
			row.getCell(4).setCellValue("FY"+ String.valueOf(year+1).substring(2));
			row.getCell(184).setCellValue("FY"+ String.valueOf(year+2).substring(2));
			row.getCell(193).setCellValue("FY"+ String.valueOf(year+3).substring(2));
			row.getCell(202).setCellValue("FY"+ String.valueOf(year+4).substring(2));
			row.getCell(211).setCellValue("FY"+ String.valueOf(year+5).substring(2));
			String sql=this.templateVal("FY"+ String.valueOf(year+1).substring(2)) ;
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
				row.createCell(2).setCellValue(map.get("PRODUCT_SERIES").toString());
				row.createCell(3).setCellValue(map.get("PRODUCT_NO").toString());

				row.createCell(4).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(6).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(8).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(10).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(12).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(14).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(16).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(18).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(20).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(22).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(24).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));
				row.createCell(26).setCellValue(Double.parseDouble(map.get("MATERIAL_COST").toString()));

				row.createCell(28).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(30).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(32).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(34).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(36).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(38).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(40).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(42).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(44).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(46).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(48).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));
				row.createCell(50).setCellValue(Double.parseDouble(map.get("LABER_COST").toString()));

				row.createCell(52).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(54).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(56).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(58).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(60).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(62).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(64).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(66).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(68).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(70).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(72).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));
				row.createCell(74).setCellValue(Double.parseDouble(map.get("OVERHEAD_COST").toString()));

				row.createCell(76).setCellFormula("F"+rowNo);
				row.createCell(77).setCellFormula("AD"+rowNo);
				row.createCell(78).setCellFormula("BB"+rowNo);
				row.createCell(79).setCellFormula("SUM(BY"+rowNo+":CA"+rowNo+")");
				row.createCell(80).setCellFormula("H"+rowNo);
				row.createCell(81).setCellFormula("AF"+rowNo);
				row.createCell(82).setCellFormula("BD"+rowNo);
				row.createCell(83).setCellFormula("SUM(CC"+rowNo+":CE"+rowNo+")");
				row.createCell(84).setCellFormula("J"+rowNo);
				row.createCell(85).setCellFormula("AH"+rowNo);
				row.createCell(86).setCellFormula("BF"+rowNo);
				row.createCell(87).setCellFormula("SUM(CG"+rowNo+":CI"+rowNo+")");
				row.createCell(88).setCellFormula("L"+rowNo);
				row.createCell(89).setCellFormula("AJ"+rowNo);
				row.createCell(90).setCellFormula("BH"+rowNo);
				row.createCell(91).setCellFormula("SUM(CK"+rowNo+":CM"+rowNo+")");
				row.createCell(92).setCellFormula("N"+rowNo);
				row.createCell(93).setCellFormula("AL"+rowNo);
				row.createCell(94).setCellFormula("BJ"+rowNo);
				row.createCell(95).setCellFormula("SUM(CO"+rowNo+":CQ"+rowNo+")");
				row.createCell(96).setCellFormula("P"+rowNo);
				row.createCell(97).setCellFormula("AN"+rowNo);
				row.createCell(98).setCellFormula("BL"+rowNo);
				row.createCell(99).setCellFormula("SUM(CS"+rowNo+":CU"+rowNo+")");
				row.createCell(100).setCellFormula("R"+rowNo);
				row.createCell(101).setCellFormula("AP"+rowNo);
				row.createCell(102).setCellFormula("BN"+rowNo);
				row.createCell(103).setCellFormula("SUM(CW"+rowNo+":CY"+rowNo+")");
				row.createCell(104).setCellFormula("T"+rowNo);
				row.createCell(105).setCellFormula("AR"+rowNo);
				row.createCell(106).setCellFormula("BP"+rowNo);
				row.createCell(107).setCellFormula("SUM(DA"+rowNo+":DC"+rowNo+")");
				row.createCell(104).setCellFormula("V"+rowNo);
				row.createCell(105).setCellFormula("AT"+rowNo);
				row.createCell(106).setCellFormula("BR"+rowNo);
				row.createCell(107).setCellFormula("SUM(DE"+rowNo+":DG"+rowNo+")");
				row.createCell(108).setCellFormula("V"+rowNo);
				row.createCell(109).setCellFormula("AT"+rowNo);
				row.createCell(110).setCellFormula("BR"+rowNo);
				row.createCell(111).setCellFormula("SUM(DE"+rowNo+":DG"+rowNo+")");
				row.createCell(112).setCellFormula("X"+rowNo);
				row.createCell(113).setCellFormula("AV"+rowNo);
				row.createCell(114).setCellFormula("BT"+rowNo);
				row.createCell(115).setCellFormula("SUM(DI"+rowNo+":DK"+rowNo+")");
				row.createCell(116).setCellFormula("Z"+rowNo);
				row.createCell(117).setCellFormula("AX"+rowNo);
				row.createCell(118).setCellFormula("BV"+rowNo);
				row.createCell(119).setCellFormula("SUM(DM"+rowNo+":DO"+rowNo+")");
				row.createCell(120).setCellFormula("AB"+rowNo);
				row.createCell(121).setCellFormula("AZ"+rowNo);
				row.createCell(122).setCellFormula("BX"+rowNo);
				row.createCell(123).setCellFormula("SUM(DQ"+rowNo+":DS"+rowNo+")");
				row.createCell(124).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH1").toString()));
				row.createCell(125).setCellFormula("BY"+rowNo+"*$DU"+rowNo);
				row.createCell(126).setCellFormula("BZ"+rowNo+"*$DU"+rowNo);
				row.createCell(127).setCellFormula("CA"+rowNo+"*$DU"+rowNo);
				row.createCell(128).setCellFormula("SUM(DV"+rowNo+":DX"+rowNo+")");
				row.createCell(129).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH2").toString()));
				row.createCell(130).setCellFormula("CC"+rowNo+"*$DZ"+rowNo);
				row.createCell(131).setCellFormula("CD"+rowNo+"*$DZ"+rowNo);
				row.createCell(132).setCellFormula("CE"+rowNo+"*$DZ"+rowNo);
				row.createCell(133).setCellFormula("SUM(EA"+rowNo+":EC"+rowNo+")");
				row.createCell(134).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH3").toString()));
				row.createCell(135).setCellFormula("CG"+rowNo+"*$EE"+rowNo);
				row.createCell(136).setCellFormula("CH"+rowNo+"*$EE"+rowNo);
				row.createCell(137).setCellFormula("CI"+rowNo+"*$EE"+rowNo);
				row.createCell(138).setCellFormula("SUM(EF"+rowNo+":EH"+rowNo+")");
				row.createCell(139).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH4").toString()));
				row.createCell(140).setCellFormula("CK"+rowNo+"*$EJ"+rowNo);
				row.createCell(141).setCellFormula("CL"+rowNo+"*$EJ"+rowNo);
				row.createCell(142).setCellFormula("CM"+rowNo+"*$EJ"+rowNo);
				row.createCell(143).setCellFormula("SUM(EK"+rowNo+":EM"+rowNo+")");
				row.createCell(144).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH5").toString()));
				row.createCell(145).setCellFormula("CO"+rowNo+"*$EO"+rowNo);
				row.createCell(146).setCellFormula("CP"+rowNo+"*$EO"+rowNo);
				row.createCell(147).setCellFormula("CQ"+rowNo+"*$EO"+rowNo);
				row.createCell(148).setCellFormula("SUM(EP"+rowNo+":ER"+rowNo+")");
				row.createCell(149).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH6").toString()));
				row.createCell(150).setCellFormula("CS"+rowNo+"*$ET"+rowNo);
				row.createCell(151).setCellFormula("CT"+rowNo+"*$ET"+rowNo);
				row.createCell(152).setCellFormula("CU"+rowNo+"*$ET"+rowNo);
				row.createCell(153).setCellFormula("SUM(EU"+rowNo+":EW"+rowNo+")");
				row.createCell(154).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH7").toString()));
				row.createCell(155).setCellFormula("CW"+rowNo+"*$EY"+rowNo);
				row.createCell(156).setCellFormula("CX"+rowNo+"*$EY"+rowNo);
				row.createCell(157).setCellFormula("CY"+rowNo+"*$EY"+rowNo);
				row.createCell(158).setCellFormula("SUM(EZ"+rowNo+":FB"+rowNo+")");
				row.createCell(159).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH8").toString()));
				row.createCell(160).setCellFormula("DA"+rowNo+"*$FD"+rowNo);
				row.createCell(161).setCellFormula("DB"+rowNo+"*$FD"+rowNo);
				row.createCell(162).setCellFormula("DC"+rowNo+"*$FD"+rowNo);
				row.createCell(163).setCellFormula("SUM(FE"+rowNo+":FG"+rowNo+")");
				row.createCell(164).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH9").toString()));
				row.createCell(165).setCellFormula("DE"+rowNo+"*$FI"+rowNo);
				row.createCell(166).setCellFormula("DF"+rowNo+"*$FI"+rowNo);
				row.createCell(167).setCellFormula("DG"+rowNo+"*$FI"+rowNo);
				row.createCell(168).setCellFormula("SUM(FJ"+rowNo+":FL"+rowNo+")");
				row.createCell(169).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH10").toString()));
				row.createCell(170).setCellFormula("DI"+rowNo+"*$FN"+rowNo);
				row.createCell(171).setCellFormula("DJ"+rowNo+"*$FN"+rowNo);
				row.createCell(172).setCellFormula("DK"+rowNo+"*$FN"+rowNo);
				row.createCell(173).setCellFormula("SUM(FO"+rowNo+":FQ"+rowNo+")");
				row.createCell(174).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH11").toString()));
				row.createCell(175).setCellFormula("DM"+rowNo+"*$FS"+rowNo);
				row.createCell(176).setCellFormula("DN"+rowNo+"*$FS"+rowNo);
				row.createCell(177).setCellFormula("DO"+rowNo+"*$FS"+rowNo);
				row.createCell(178).setCellFormula("SUM(FT"+rowNo+":FV"+rowNo+")");
				row.createCell(179).setCellValue(Double.parseDouble(map.get("QUANTITY_MONTH12").toString()));
				row.createCell(180).setCellFormula("DQ"+rowNo+"*$FX"+rowNo);
				row.createCell(181).setCellFormula("DR"+rowNo+"*$FX"+rowNo);
				row.createCell(182).setCellFormula("DS"+rowNo+"*$FX"+rowNo);
				row.createCell(183).setCellFormula("SUM(FY"+rowNo+":GA"+rowNo+")");

				row.createCell(187).setCellFormula("SUM(GC"+rowNo+":GE"+rowNo+")");
				row.createCell(188).setCellValue(Double.parseDouble(map.get("QUANTITY_NEXTYEAR").toString()));
				row.createCell(189).setCellFormula("GC"+rowNo+"*$GG"+rowNo);
				row.createCell(190).setCellFormula("GD"+rowNo+"*$GG"+rowNo);
				row.createCell(191).setCellFormula("GE"+rowNo+"*$GG"+rowNo);
				row.createCell(192).setCellFormula("SUM(GH"+rowNo+":GJ"+rowNo+")");

				row.createCell(196).setCellFormula("SUM(GL"+rowNo+":GN"+rowNo+")");
				row.createCell(197).setCellValue(Double.parseDouble(map.get("QUANTITY_TWOYEAR").toString()));
				row.createCell(198).setCellFormula("GL"+rowNo+"*$GP"+rowNo);
				row.createCell(199).setCellFormula("GM"+rowNo+"*$GP"+rowNo);
				row.createCell(200).setCellFormula("GN"+rowNo+"*$GP"+rowNo);
				row.createCell(201).setCellFormula("SUM(GQ"+rowNo+":GS"+rowNo+")");

				row.createCell(205).setCellFormula("SUM(GU"+rowNo+":GW"+rowNo+")");
				row.createCell(206).setCellValue(Double.parseDouble(map.get("QUANTITY_THREEYEAR").toString()));
				row.createCell(207).setCellFormula("GU"+rowNo+"*$GY"+rowNo);
				row.createCell(208).setCellFormula("GV"+rowNo+"*$GY"+rowNo);
				row.createCell(209).setCellFormula("GW"+rowNo+"*$GY"+rowNo);
				row.createCell(210).setCellFormula("SUM(GZ"+rowNo+":HB"+rowNo+")");

				row.createCell(214).setCellFormula("SUM(HD"+rowNo+":HF"+rowNo+")");
				row.createCell(215).setCellValue(Double.parseDouble(map.get("QUANTITY_FOURYEAR").toString()));
				row.createCell(216).setCellFormula("HD"+rowNo+"*$HH"+rowNo);
				row.createCell(217).setCellFormula("HE"+rowNo+"*$HH"+rowNo);
				row.createCell(218).setCellFormula("HF"+rowNo+"*$HH"+rowNo);
				row.createCell(219).setCellFormula("SUM(HI"+rowNo+":HK"+rowNo+")");
				for (int i=0;i<220;i++){
					if(i==5||i==7||i==9||i==11||i==13||i==15||i==17||i==19||i==21||i==23||i==25||i==27||i==29||i==31||i==33 ||
						i==35||i==37||i==39||i==41||i==43||i==45||i==47||i==49||i==51||i==53 ||
							i==55||i==57||i==59||i==61||i==63||i==65||i==67||i==69||i==71||i==73 ||
							i==75||i==184||i==185||i==186||i==193||i==194||i==195||i==202||i==203||i==204||i==211||i==212||i==213){
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

	/**上傳模板填充的數據*/
	public String templateVal(String year){
		String corporationCode = SecurityUtils.getCorporationCode();
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sql="SELECT b.entity,b.make_entity,\n" +
				"                b.product_no,\n" +
				"                b.product_series,\n" +
				"                nvl(t.material_cost,0) material_cost,\n" +
				"                nvl(t.laber_cost,0) laber_cost,\n" +
				"                nvl(t.overhead_cost,0) + nvl(t.outsite_processing_cost,0) overhead_cost, \n" +
				"                nvl(b.quantity_month1,0) quantity_month1,\n" +
				"                nvl(b.quantity_month2,0) quantity_month2,\n" +
				"                nvl(b.quantity_month3,0) quantity_month3,\n" +
				"                nvl(b.quantity_month4,0) quantity_month4,\n" +
				"                nvl(b.quantity_month5,0) quantity_month5,\n" +
				"                nvl(b.quantity_month6,0) quantity_month6,\n" +
				"                nvl(b.quantity_month7,0) quantity_month7,\n" +
				"                nvl(b.quantity_month8,0) quantity_month8,\n" +
				"                nvl(b.quantity_month9,0) quantity_month9,\n" +
				"                nvl(b.quantity_month10,0) quantity_month10,\n" +
				"                nvl(b.quantity_month11,0) quantity_month11,\n" +
				"                nvl(b.quantity_month12,0) quantity_month12,\n" +
				"                nvl(b.quantity,0) quantity,\n" +
				"                nvl(b.quantity_nextyear,0) quantity_nextyear,\n" +
				"                nvl(b.quantity_twoyear,0) quantity_twoyear,\n" +
				"                nvl(b.quantity_threeyear,0) quantity_threeyear,\n" +
				"                nvl(b.quantity_fouryear,0) quantity_fouryear\n" +
				"  FROM epmods.if_ebs_ar_revenue_dtl_cst_v2 t,\n" +
				"       (SELECT a.*\n" +
				"          FROM epmods.fit_budget_detail_revenue a\n" +
				"         WHERE a.version = 'V00'\n" +
				"           AND a.year = 'FY' || (to_char(SYSDATE,'YY') + 1)) b\n" +
				" WHERE t.p_n(+) = b.product_no\n" +
				"   AND t.entity_code(+) = b.ou\n" +
				"   AND t.rn(+)= 1\n" +
				"   and b.year='"+year+"' and b.create_name='"+loginUser.getUsername()+"'";
		if (StringUtils.isNotEmpty(corporationCode)) {
			sql+=" and (";
			for (String string : corporationCode.split(",")) {
				sql+=" b.entity like '"+string+"%' or";
			}
			sql=sql.substring(0,sql.length()-2)+")";
		}else{
			sql=sql+" b.entity=1";
		}
		return sql;
	}

	/**簡易版*/
	public Map<String,String> simplifyTemplate(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"產品料號單位成本預算_簡易模版","產品料號單位成本預算_簡易模版")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"產品料號單位成本預算_簡易模版","產品料號單位成本預算_簡易模版")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			int year=calendar.get(Calendar.YEAR);
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

	/**下載數據*/
	public Map<String,String>  download(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"產品料號單位成本下載","產品料號單位成本下載")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"產品料號單位成本下載","產品料號單位成本下載")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);

			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));

			row.getCell(2).setCellValue(y);
			row.getCell(62).setCellValue("FY"+(year));
			row.getCell(66).setCellValue("FY"+(year+1));
			row.getCell(70).setCellValue("FY"+(year+2));
			row.getCell(74).setCellValue("FY"+(year+3));
			row.getCell(78).setCellValue("FY"+(year+4));

			String sql="select * from FIT_BUDGET_PRODUCT_UNIT_COST where YEAR='"+y+"'";
			if (null!=version && StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			List<String> tarList = new ArrayList<String>();
			String corporationCode = SecurityUtils.getCorporationCode();
			if (StringUtils.isNotEmpty(corporationCode)) {
				for (String string : corporationCode.split(",")) {
					tarList.add(string);
				}
			}
			String sbuVal="";
			for (String sbu : entitys.split(",")) {
				if (tarList.contains(sbu)) {
					sbuVal+=" ENTITY like '"+sbu+"_%' or";
				}
			}
			if(sbuVal.isEmpty()){
				sql+=" and ("+sbuVal.substring(0,sbuVal.length()-2)+")";
			}
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			pageRequest.setOrderBy("year,Id");
			List<Object[]> dataList = budgetProductNoUnitCostDao.findPageBySql(pageRequest, sql).getResult();
			int col=0;
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					col=0;
					for (int i = 3; i < objects.length-7; i++) {
						if(i==65||i==70||i==75||i==80||i==85){
							continue;
						}
						Cell cell = contentRow.createCell(col);
						col++;
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>4 && i<90) {
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
							for (int i = 3; i < objects.length-8; i++) {
								if(i==65||i==70||i==75||i==80||i==85){
									continue;
								}
								Cell cell = contentRow.createCell(col);
								col++;
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>4 && i<90) {
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

    /**
	 * 版本控制
	 */
	public String version(){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from FIT_BUDGET_PRODUCT_UNIT_COST where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = budgetProductNoUnitCostDao.listMapBySql(sqlVersion);
		if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into FIT_BUDGET_PRODUCT_UNIT_COST (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,\n" +
				"'"+sqlVersion+"' version,year, \n" +
				"entity, \n" +
				"product, \n" +
				"sales_quantity1, \n" +
				"material_cost1, \n" +
				"labor_cost1, \n" +
				"manufacture_cost1, \n" +
				"selling_cost1, \n" +
				"sales_quantity2, \n" +
				"material_cost2, \n" +
				"labor_cost2, \n" +
				"manufacture_cost2, \n" +
				"selling_cost2, \n" +
				"sales_quantity3, \n" +
				"material_cost3, \n" +
				"labor_cost3, \n" +
				"manufacture_cost3, \n" +
				"selling_cost3, \n" +
				"sales_quantity4, \n" +
				"material_cost4, \n" +
				"labor_cost4, \n" +
				"manufacture_cost4, \n" +
				"selling_cost4, \n" +
				"sales_quantity5, \n" +
				"material_cost5, \n" +
				"labor_cost5, \n" +
				"manufacture_cost5, \n" +
				"selling_cost5, \n" +
				"sales_quantity6, \n" +
				"material_cost6, \n" +
				"labor_cost6, \n" +
				"manufacture_cost6, \n" +
				"selling_cost6, \n" +
				"sales_quantity7, \n" +
				"material_cost7, \n" +
				"labor_cost7, \n" +
				"manufacture_cost7, \n" +
				"selling_cost7, \n" +
				"sales_quantity8, \n" +
				"material_cost8, \n" +
				"labor_cost8, \n" +
				"manufacture_cost8, \n" +
				"selling_cost8, \n" +
				"sales_quantity9, \n" +
				"material_cost9, \n" +
				"labor_cost9, \n" +
				"manufacture_cost9, \n" +
				"selling_cost9, \n" +
				"sales_quantity10, \n" +
				"material_cost10, \n" +
				"labor_cost10, \n" +
				"manufacture_cost10, \n" +
				"selling_cost10, \n" +
				"sales_quantity11, \n" +
				"material_cost11, \n" +
				"labor_cost11, \n" +
				"manufacture_cost11, \n" +
				"selling_cost11, \n" +
				"sales_quantity12, \n" +
				"material_cost12, \n" +
				"labor_cost12, \n" +
				"manufacture_cost12, \n" +
				"selling_cost12, \n" +
				"sales_quantity_nextyear, \n" +
				"material_cost_nextyear, \n" +
				"labor_cost_nextyear, \n" +
				"manufacture_cost_nextyear, \n" +
				"selling_cost_nextyear, \n" +
				"sales_quantity_twoyear, \n" +
				"material_cost_twoyear, \n" +
				"labor_cost_twoyear, \n" +
				"manufacture_cost_twoyear, \n" +
				"selling_cost_twoyear, \n" +
				"sales_quantity_threeyear, \n" +
				"material_cost_threeyear, \n" +
				"labor_cost_threeyear, \n" +
				"manufacture_cost_threeyear, \n" +
				"selling_cost_threeyear, \n" +
				"sales_quantity_fouryear, \n" +
				"material_cost_fouryear, \n" +
				"labor_cost_fouryear, \n" +
				"manufacture_cost_fouryear, \n" +
				"selling_cost_fouryear, \n" +
				"create_name, \n" +
				"create_date, \n" +
				"sysdate version_date,'"+loginUser.getUsername()+"' version_name,\n" +
				" product_no,trade_type,make_entity from FIT_BUDGET_PRODUCT_UNIT_COST where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		budgetProductNoUnitCostDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

}
