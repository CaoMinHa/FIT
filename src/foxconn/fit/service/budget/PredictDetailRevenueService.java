package foxconn.fit.service.budget;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.PredictDetailRevenueDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.budget.PredictDetailRevenue;
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
import java.util.*;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PredictDetailRevenueService extends BaseService<PredictDetailRevenue> {

	@Autowired
	private PredictDetailRevenueDao predictDetailRevenueDao;

	@Autowired
	private InstrumentClassService instrumentClassService;

	@Autowired
	private ForecastDetailRevenueService forecastDetailRevenueService;


	@Override
	public BaseDaoHibernate<PredictDetailRevenue> getDao() {
		return predictDetailRevenueDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = predictDetailRevenueDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
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
		String sqlVersion="select distinct version  from FIT_PREDICT_DETAIL_REVENUE where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version";
		List<String> versionList=predictDetailRevenueDao.listBySql(sqlVersion);
		return  versionList;
	}

	/**頁面查詢*/
	public String list(String year,String version,String entity){
		String sql="select * from FIT_PREDICT_DETAIL_REVENUE_V where 1=1";
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
				List<PredictDetailRevenue> list = new ArrayList<>();
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
					Row row = sheet.getRow(i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(0), i);
					if(row == null||entity.length()<1||"".equals(entity)){
						continue;
					}
					String sql="select distinct PARENT from fit_dimension where type='" + EnumDimensionType.Entity.getCode() +"' and ALIAS='"+entity+"'";
					List<String> listSbu=predictDetailRevenueDao.listBySql(sql);
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

					PredictDetailRevenue predictDetailRevenue = new PredictDetailRevenue();
					predictDetailRevenue.setEntity(entity);
					predictDetailRevenue.setMakeEntity(ExcelUtil.getCellStringValue(row.getCell(1), i));
					predictDetailRevenue.setIndustry(ExcelUtil.getCellStringValue(row.getCell(2), i));
					predictDetailRevenue.setmainBusiness(ExcelUtil.getCellStringValue(row.getCell(3), i));
					predictDetailRevenue.setThree(ExcelUtil.getCellStringValue(row.getCell(4), i));
					predictDetailRevenue.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(5), i));
					predictDetailRevenue.setProductNo(ExcelUtil.getCellStringValue(row.getCell(6), i));
					predictDetailRevenue.setEndCustomer(ExcelUtil.getCellStringValue(row.getCell(7), i));
					predictDetailRevenue.setLoanCustomer(ExcelUtil.getCellStringValue(row.getCell(8), i));
					predictDetailRevenue.setTradeType("對外銷售");
					predictDetailRevenue.setCurrency("美元");
					predictDetailRevenue.setTypeOfAirplane(ExcelUtil.getCellStringValue(row.getCell(11), i));
					predictDetailRevenue.setPm(ExcelUtil.getCellStringValue(row.getCell(12), i));
					predictDetailRevenue.setRevenueNextyear(ExcelUtil.getCellStringValue(row.getCell(13), i));
					predictDetailRevenue.setRevenueTwoyear(ExcelUtil.getCellStringValue(row.getCell(14), i));
					predictDetailRevenue.setRevenueThreeyear(ExcelUtil.getCellStringValue(row.getCell(15), i));
					predictDetailRevenue.setRevenueFouryear(ExcelUtil.getCellStringValue(row.getCell(16), i));

					predictDetailRevenue.setQuantityNextyear(ExcelUtil.getCellStringValue(row.getCell(17), i));
					predictDetailRevenue.setQuantityTwoyear(ExcelUtil.getCellStringValue(row.getCell(18), i));
					predictDetailRevenue.setQuantityThreeyear(ExcelUtil.getCellStringValue(row.getCell(19), i));
					predictDetailRevenue.setQuantityFouryear(ExcelUtil.getCellStringValue(row.getCell(20), i));
					predictDetailRevenue.setQuantityMonth1(ExcelUtil.getCellStringValue(row.getCell(21), i));
					predictDetailRevenue.setQuantityMonth2(ExcelUtil.getCellStringValue(row.getCell(22), i));
					predictDetailRevenue.setQuantityMonth3(ExcelUtil.getCellStringValue(row.getCell(23), i));
					predictDetailRevenue.setQuantityMonth4(ExcelUtil.getCellStringValue(row.getCell(24), i));
					predictDetailRevenue.setQuantityMonth5(ExcelUtil.getCellStringValue(row.getCell(25), i));
					predictDetailRevenue.setQuantityMonth6(ExcelUtil.getCellStringValue(row.getCell(26), i));
					predictDetailRevenue.setQuantityMonth7(ExcelUtil.getCellStringValue(row.getCell(27), i));
					predictDetailRevenue.setQuantityMonth8(ExcelUtil.getCellStringValue(row.getCell(28), i));
					predictDetailRevenue.setQuantityMonth9(ExcelUtil.getCellStringValue(row.getCell(29), i));
					predictDetailRevenue.setQuantityMonth10(ExcelUtil.getCellStringValue(row.getCell(30), i));
					predictDetailRevenue.setQuantityMonth11(ExcelUtil.getCellStringValue(row.getCell(31), i));
					predictDetailRevenue.setQuantityMonth12(ExcelUtil.getCellStringValue(row.getCell(32), i));

					predictDetailRevenue.setPriceMonth1(ExcelUtil.getCellStringValue(row.getCell(33), i));
					predictDetailRevenue.setPriceMonth2(ExcelUtil.getCellStringValue(row.getCell(34), i));
					predictDetailRevenue.setPriceMonth3(ExcelUtil.getCellStringValue(row.getCell(35), i));
					predictDetailRevenue.setPriceMonth4(ExcelUtil.getCellStringValue(row.getCell(36), i));
					predictDetailRevenue.setPriceMonth5(ExcelUtil.getCellStringValue(row.getCell(37), i));
					predictDetailRevenue.setPriceMonth6(ExcelUtil.getCellStringValue(row.getCell(38), i));
					predictDetailRevenue.setPriceMonth7(ExcelUtil.getCellStringValue(row.getCell(39), i));
					predictDetailRevenue.setPriceMonth8(ExcelUtil.getCellStringValue(row.getCell(40), i));
					predictDetailRevenue.setPriceMonth9(ExcelUtil.getCellStringValue(row.getCell(41), i));
					predictDetailRevenue.setPriceMonth10(ExcelUtil.getCellStringValue(row.getCell(42), i));
					predictDetailRevenue.setPriceMonth11(ExcelUtil.getCellStringValue(row.getCell(43), i));
					predictDetailRevenue.setPriceMonth12(ExcelUtil.getCellStringValue(row.getCell(44), i));
					predictDetailRevenue.setYear(v_year);
					predictDetailRevenue.setVersion("V00");
					predictDetailRevenue.setId(UUID.randomUUID().toString());
					UserDetailImpl loginUser = SecurityUtils.getLoginUser();
					predictDetailRevenue.setCreateName(loginUser.getUsername());
					predictDetailRevenue.setCreateDate(new Date());
					list.add(predictDetailRevenue);
				}
				if (!list.isEmpty()) {
					/**SBU_法人校驗*/
					entityMakeList.addAll(entityList);
					check=this.check(entityMakeList,EnumDimensionType.Entity.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【SBU_銷售法人】或[SBU_製造法人]在【維度表】没有找到---> " + check);
						return result.getJson();
					}
					/**次產業校驗*/
					check=this.check(industryList,EnumDimensionType.Segment.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【次產業】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**主營業務*/
					/**5GAIOT\EV\AUDIO\Type C\Existing*/
					check=this.check(mainBusinessList,EnumDimensionType.Bak2.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【Main Business】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**3+3**/
					check=this.check(threeList,EnumDimensionType.Project.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【3+3】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**產品系列**/
					check=this.check(productSeriesList,EnumDimensionType.Product.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【產品系列】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**賬款客戶**/
					check=this.check(loanCustomerList,EnumDimensionType.Customer.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【賬款客戶】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**最終客戶**/
					check=this.check(endCustomerList,EnumDimensionType.Combine.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【最終客戶】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**交易類型**/
					check=this.check(tradeTypeList,EnumDimensionType.View.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【交易類型】在【維度表】没有找到---> "+check);
						return result.getJson();
					}
					/**報告幣種**/
					check=this.check(currencyList,EnumDimensionType.Currency.getCode());
					if (!check.equals("") && check.length() > 0){
						result.put("flag", "fail");
						result.put("msg", "以下【報告幣種】在【維度表】没有找到---> "+check);
						return result.getJson();
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
						return result.getJson();
					}
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
	public void saveBatch(List<PredictDetailRevenue> list,String year,List<String> entityList) throws Exception {
		String sql="delete from FIT_PREDICT_DETAIL_REVENUE where VERSION='V00' and YEAR='"+year+"' and ENTITY in(";
		for (int i=0;i<entityList.size();i++){
			sql+="'"+entityList.get(i)+"',";
			if ((i + 50) % 1000 == 0) {
				predictDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.substring(0,sql.length()-1)+")").executeUpdate();
				predictDetailRevenueDao.getHibernateTemplate().flush();
				predictDetailRevenueDao.getHibernateTemplate().clear();
			}
		}
		sql=sql.substring(0,sql.length()-1)+")";
		predictDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			predictDetailRevenueDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				predictDetailRevenueDao.getHibernateTemplate().flush();
				predictDetailRevenueDao.getHibernateTemplate().clear();
			}
		}
	}

	/**下載模板*/
	public Map<String,String>  template(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表","銷售收入預測表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表_backlog","銷售收入預測表_backlog")+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			int year=calendar.get(Calendar.YEAR);
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
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表","銷售收入預測表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+instrumentClassService.getLanguage(locale,"銷售收入預測表_下载backlog","銷售收入預測表_下载backlog")+".xlsx");
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

			String sql="select * from FIT_PREDICT_DETAIL_REVENUE_V where YEAR='"+y+"'";
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
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+sbuStr+")";
			List<Map> sbuMap=predictDetailRevenueDao.listMapBySql(sbusql);
			String entityStr=","+entitys+",";
			for (Map map:sbuMap){
				if(entityStr.indexOf(map.get("PARENT").toString())!=-1){
					sbuVal+=" ENTITY like '"+map.get("ALIAS").toString()+"_%' or";
				}
			}
			if(sbuVal.isEmpty()){
				sql+=" and ("+sbuVal.substring(0,sbuVal.length()-2)+")";
			}
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			pageRequest.setOrderBy("year,Id");
			List<Object[]> dataList = predictDetailRevenueDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 0; i < objects.length-10; i++) {
						Cell cell = contentRow.createCell(i);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>14 && i<62) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
							if(i==13){
								cell.setCellValue("USD");
							}
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = predictDetailRevenueDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 0; i < objects.length-11; i++) {
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>14 && i<62) {
									cell.setCellValue(Double.parseDouble(text));
								} else {
									cell.setCellValue(text);
									if(i==13){
										cell.setCellValue("USD");
									}
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
		String sqlVersion="select Max(to_number(substr(version,2))) version  from FIT_PREDICT_DETAIL_REVENUE where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = predictDetailRevenueDao.listMapBySql(sqlVersion);
		if(null ==maps.get(0).get("VERSION")){
			sqlVersion="No finalizable data detected_沒有檢查到可定版的數據！";
		}else if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into FIT_PREDICT_DETAIL_REVENUE (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,\n" +
				"'"+sqlVersion+"' version,year, \n" +
				"entity,make_entity,segment,main_industry,industry,main_business,three,product_series,product_no,loan_customer,end_customer,type_of_airplane,trade_type,currency,pm,revenue,\n" +
				"revenue_nextyear,revenue_twoyear,revenue_threeyear,revenue_fouryear,quantity,quantity_nextyear,quantity_twoyear,quantity_threeyear,quantity_fouryear,quantity_month1,quantity_month2,\n" +
				"quantity_month3,quantity_month4,quantity_month5,quantity_month6,quantity_month7,quantity_month8,quantity_month9,quantity_month10,quantity_month11,quantity_month12,price_month1,\n" +
				"price_month2,price_month3,price_month4,price_month5,price_month6,price_month7,price_month8,price_month9,price_month10,price_month11,price_month12,revenue_month1,revenue_month2,\n" +
				"revenue_month3, revenue_month4, revenue_month5, revenue_month6,revenue_month7,revenue_month8,revenue_month9, revenue_month10, revenue_month11, revenue_month12,\n" +
				"create_name,create_date, sysdate version_date,'"+loginUser.getUsername()+"' version_name\n" +
				"  from FIT_PREDICT_DETAIL_REVENUE where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		predictDetailRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

}
