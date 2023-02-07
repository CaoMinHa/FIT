package foxconn.fit.service.investment;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.investment.InvestmentBudgetDao;
import foxconn.fit.dao.investment.InvestmentForecastDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.investment.InvestmentBudget;
import foxconn.fit.entity.investment.InvestmentForecast;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvestmentBudgetService extends BaseService<InvestmentBudget> {

	@Autowired
	private InvestmentBudgetDao investmentBudgetDao;
	@Autowired
	private InvestmentForecastDao investmentForecastDao;
	@Autowired
	private InstrumentClassService instrumentClassService;

	@Override
	public BaseDaoHibernate<InvestmentBudget> getDao() {
		return investmentBudgetDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = investmentBudgetDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		//預算應爲測試需要先把年份校驗放開
//		int year=calendar.get(Calendar.YEAR)+1;
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		return model;
	}
	/**獲取表中對應用戶下的版本值*/
	public List<String> versionVal(String budgetTable,String forecastTabel){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select distinct version from "+budgetTable+" where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version";
		List<String> versionList=investmentBudgetDao.listBySql(sqlVersion);
		sqlVersion="select distinct version from "+forecastTabel+" where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version";
		versionList.addAll(investmentForecastDao.listBySql(sqlVersion));
		return versionList;
	}

	/**頁面查詢*/
	public String viewList(String year,String version,String entity,String tableName){
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String userName=loginUser.getUsername();
		String sql="select * from "+tableName+" where CREATE_NAME='"+userName+"'";
		String roleSql="select count(1) from  fit_user u \n" +
				" left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
				" left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
				" WHERE  u.username='"+userName+"' and code='investment' ";
		List<BigDecimal> countList = (List<BigDecimal>)investmentBudgetDao.listBySql(roleSql);
		if(countList.get(0).intValue()>0){
			sql="select * from "+tableName+" where 1=1 ";
		}
		if (null!=year&&StringUtils.isNotEmpty(year)) {
			sql+=" and YEAR='"+year+"'";
		}
		if (null!=version && StringUtils.isNotEmpty(version)) {
			sql+=" and version='"+version+"'";
		}
		String tarList=instrumentClassService.getBudgetSBUStr();
		String sbu="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS,','||PARENT||',' PARENT from FIT_ZR_DIMENSION where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='ZR_Entity' and PARENT in("+tarList+")";
		List<Map> sbuMap=investmentBudgetDao.listMapBySql(sbu);
		sql+=instrumentClassService.querySbuSql(entity,sbuMap);
		sql+=" order by year,entity,ID";
		return sql;
	}

	/**數據上傳*/
	public String uploadBudget(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest,String type) {
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
				Workbook wb = null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb = new HSSFWorkbook(file.getInputStream());
				} else if("xlsx".equals(suffix)) {
					//Excel2007
					wb = new XSSFWorkbook(file.getInputStream());
				}else{
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
					return result.getJson();
				}
				wb.close();
				Sheet sheet = wb.getSheetAt(0);
				String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(9), 0);
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
				//預算應爲測試需要先把年份校驗放開
//				Calendar calendar = Calendar.getInstance();
//				String year = Integer.toString(calendar.get(Calendar.YEAR) + 1);
//				Assert.isTrue(year.substring(2).equals(v_year.substring(2)), instrumentClassService.getLanguage(locale, "僅可上傳明年的預算數據！", "Only next year's budget data can be uploaded"));
				int column = sheet.getRow(1).getLastCellNum();
				Assert.isTrue(column <= 25,instrumentClassService.getLanguage(locale, "Excel列数不能小于" + 25 + "，請下載正確的模板上傳數據！", "Number Of Columns Can Not Less Than" + 25 + ",Please download the correct template to upload the data"));
				int rowNum = sheet.getPhysicalNumberOfRows();
				Assert.isTrue(rowNum > 2,instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
				List list;
				if(type.equals("budget")){
					list = new ArrayList<InvestmentBudget>();
				}else{
					list = new ArrayList<InvestmentForecast>();
				}
				/**投資編號*/List<String> projectList = new ArrayList<>();
				/**設備類別*/List<String> combineList = new ArrayList<>();
				/**SBU_法人*/List<String> entityList = new ArrayList<>();
				/**SBU**/List<String> sbuList = new ArrayList<>();
				/**提出部門*/List<String> departmentList = new ArrayList<>();
				/**提出部門*/List<String> departmentList1 = new ArrayList<>();
				/**Segment*/List<String> bakList = new ArrayList<>();
				/**Main business*/List<String> mainBusinessList= new ArrayList<>();
				/**產業*/List<String> segmentList = new ArrayList<>();
				/**投資類型*/List<String> currencyList = new ArrayList<>();

				String check = "";
				String mianDataChek="";
				String dateChek="";
				for (int i = 2; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					String project=ExcelUtil.getCellStringValue(row.getCell(0), i);
					String combine=ExcelUtil.getCellStringValue(row.getCell(1), i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(2), i);
					String department=ExcelUtil.getCellStringValue(row.getCell(3), i);
					String bak=ExcelUtil.getCellStringValue(row.getCell(4), i);
					String mainBusiness=ExcelUtil.getCellStringValue(row.getCell(5), i);
					String segment=ExcelUtil.getCellStringValue(row.getCell(6), i);
					String view=ExcelUtil.getCellStringValue(row.getCell(7), i);
					String currency=ExcelUtil.getCellStringValue(row.getCell(8), i);
					String receiptDate=ExcelUtil.getCellStringValue(row.getCell(15), i);
					String productLifeCycle=ExcelUtil.getCellStringValue(row.getCell(13), i);
					if(productLifeCycle.isEmpty()||receiptDate.isEmpty()||project.isEmpty()||combine.isEmpty()||entity.isEmpty()||department.isEmpty()||bak.isEmpty()||mainBusiness.isEmpty()||segment.isEmpty()||
							view.isEmpty()||currency.isEmpty()){
						mianDataChek+=(i+1)+",";
						continue;
					}
					if(!receiptDate.isEmpty()||!productLifeCycle.isEmpty()){
						Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
						Matcher isNum = pattern.matcher(productLifeCycle);
						if (!isNum.matches()) {
							dateChek+=(i+1)+",";
							continue;
						}
						pattern = Pattern.compile("[0-9]*");
						if (receiptDate.length()!=6||!pattern.matcher(receiptDate).matches()){
							dateChek+=(i+1)+",";
							continue;
						}
					}
					//跳過沒有SBU權限的數據
					String sql="select distinct PARENT from FIT_ZR_DIMENSION where type='ZR_Entity' and ALIAS='"+entity+"'";
					List<String> listSbu=investmentBudgetDao.listBySql(sql);
					sbuList.addAll(listSbu);
					check = instrumentClassService.getDiffrent(listSbu, tarList);
					if (!"".equalsIgnoreCase(check.trim()) || check.length() > 0) {
						continue;
					}

					projectList.add(project);
					combineList.add(combine);
					entityList.add(entity);
					departmentList.add(department);
					departmentList1.add(department);
					bakList.add(bak);
					mainBusinessList.add(mainBusiness);
					segmentList.add(segment);
					departmentList.add(view);
					currencyList.add(currency);

					if(type.equals("budget")){
						InvestmentBudget investmentBudget = new InvestmentBudget();
						investmentBudget.setInvestmentNo(project);
						investmentBudget.setCategoryEquipment(combine);
						investmentBudget.setEntity(entity);
						investmentBudget.setDepartment(department);
						investmentBudget.setSegment(bak);
						investmentBudget.setMainBusiness(mainBusiness);
						investmentBudget.setIndustry(segment);
						investmentBudget.setUseDepartment(view);
						investmentBudget.setInvestmentType(currency);
						investmentBudget.setYear(v_year);
						investmentBudget.setCreateName(loginUser.getUsername());
						list.add(this.investmentBudget(investmentBudget,row,i));
					}else{
						InvestmentForecast investmentForecast =new InvestmentForecast();
						investmentForecast.setInvestmentNo(project);
						investmentForecast.setCategoryEquipment(combine);
						investmentForecast.setEntity(entity);
						investmentForecast.setDepartment(department);
						investmentForecast.setSegment(bak);
						investmentForecast.setMainBusiness(mainBusiness);
						investmentForecast.setIndustry(segment);
						investmentForecast.setUseDepartment(view);
						investmentForecast.setInvestmentType(currency);
						investmentForecast.setYear(v_year);
						investmentForecast.setCreateName(loginUser.getUsername());
						list.add(this.investmentForecast(investmentForecast,row,i));
					}
				}
				if (!list.isEmpty()) {
					if(!instrumentClassService.removeDuplicate(entityList).isEmpty()){
						checkMianData(projectList,combineList,entityList,departmentList,departmentList1,bakList,mainBusinessList,segmentList,currencyList,loginUser.getUsername());
						if(type.equals("budget")){
							this.saveBatch(list,v_year,loginUser.getUsername());
						}else {
							this.saveBatchForecast(list,v_year,loginUser.getUsername());
						}
						sbuList=instrumentClassService.removeDuplicate(sbuList);
					}
				}else {
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
				}
				check = instrumentClassService.getDiffrent(sbuList, tarList);
				if (!check.trim().isEmpty()) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下數據未上傳成功，請檢查您是否具備該SBU權限。--->" + check, "The following data fails to be uploaded. Check whether you have the SBU permission--->" + check));
				}
				if (!mianDataChek.trim().isEmpty()) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下行數據未上傳成功，主數據、產品生命周期、驗收單年月不可爲空。--->" + mianDataChek.substring(0,mianDataChek.length()-1), "The data of the following row has not been uploaded successfully. Master data, product life cycle, and receipt date cannot be empty--->" + mianDataChek.substring(0,mianDataChek.length()-1)));
				}
				if (!dateChek.trim().isEmpty()) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下行數據未上傳成功，產品生命週期、驗收單年月格式錯誤(示例：4、202301)。--->" + dateChek.substring(0,dateChek.length()-1), "The data of the following row has not been uploaded successfully.Product life cycle, receipt year month format error (example: 4, 202301)--->" + dateChek.substring(0,dateChek.length()-1)));
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

	/**上傳預算保存數據封裝實體類*/
	private InvestmentBudget investmentBudget(InvestmentBudget investmentBudget,Row row,int i) throws Exception {
		investmentBudget.setProjectName(ExcelUtil.getCellStringValue(row.getCell(9), i));
		investmentBudget.setObjectInvestment(ExcelUtil.getCellStringValue(row.getCell(10), i));
		investmentBudget.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(11), i));
		investmentBudget.setQuantityRequired(ExcelUtil.getCellIntegerValue(row.getCell(12), i));
		investmentBudget.setProductLifeCycle(ExcelUtil.getCellStringValue(row.getCell(13), i));
		investmentBudget.setPoPeriod(ExcelUtil.getCellStringValue(row.getCell(14), i));
		investmentBudget.setReceiptDate(ExcelUtil.getCellStringValue(row.getCell(15), i));
		investmentBudget.setDuePeriod(ExcelUtil.getCellStringValue(row.getCell(16), i));
		investmentBudget.setAmountInvestment(ExcelUtil.getCellDoubleValue(row.getCell(17), i));
		investmentBudget.setDescriptionInvestment(ExcelUtil.getCellStringValue(row.getCell(18), i));
		investmentBudget.setRevenue(ExcelUtil.getCellDoubleValue(row.getCell(19), i));
		investmentBudget.setProfit(ExcelUtil.getCellDoubleValue(row.getCell(20), i));
		investmentBudget.setNextRevenue(ExcelUtil.getCellDoubleValue(row.getCell(21), i));
		investmentBudget.setNextProfit(ExcelUtil.getCellDoubleValue(row.getCell(22), i));
		investmentBudget.setAfterRevenue(ExcelUtil.getCellDoubleValue(row.getCell(23), i));
		investmentBudget.setAfterProfit(ExcelUtil.getCellDoubleValue(row.getCell(24), i));
		investmentBudget.setId(UUID.randomUUID().toString());
		investmentBudget.setVersion("V00");
		investmentBudget.setCreateDate(new Date());
		return investmentBudget;
	}
	/**上傳預測保存數據封裝實體類*/
	private InvestmentForecast investmentForecast(InvestmentForecast investmentForecast,Row row,int i) throws Exception {
		investmentForecast.setProjectName(ExcelUtil.getCellStringValue(row.getCell(9), i));
		investmentForecast.setObjectInvestment(ExcelUtil.getCellStringValue(row.getCell(10), i));
		investmentForecast.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(11), i));
		investmentForecast.setQuantityRequired(ExcelUtil.getCellIntegerValue(row.getCell(12), i));
		investmentForecast.setProductLifeCycle(ExcelUtil.getCellStringValue(row.getCell(13), i));
		investmentForecast.setPoPeriod(ExcelUtil.getCellStringValue(row.getCell(14), i));
		investmentForecast.setReceiptDate(ExcelUtil.getCellStringValue(row.getCell(15), i));
		investmentForecast.setDuePeriod(ExcelUtil.getCellStringValue(row.getCell(16), i));
		investmentForecast.setAmountInvestment(ExcelUtil.getCellDoubleValue(row.getCell(17), i));
		investmentForecast.setDescriptionInvestment(ExcelUtil.getCellStringValue(row.getCell(18), i));
		investmentForecast.setRevenue(ExcelUtil.getCellDoubleValue(row.getCell(19), i));
		investmentForecast.setProfit(ExcelUtil.getCellDoubleValue(row.getCell(20), i));
		investmentForecast.setNextRevenue(ExcelUtil.getCellDoubleValue(row.getCell(21), i));
		investmentForecast.setNextProfit(ExcelUtil.getCellDoubleValue(row.getCell(22), i));
		investmentForecast.setAfterRevenue(ExcelUtil.getCellDoubleValue(row.getCell(23), i));
		investmentForecast.setAfterProfit(ExcelUtil.getCellDoubleValue(row.getCell(24), i));
		investmentForecast.setId(UUID.randomUUID().toString());
		investmentForecast.setVersion("V00");
		investmentForecast.setCreateDate(new Date());
		return investmentForecast;
	}
	/**上傳保存數據校驗主數據是否正確*/
	private void checkMianData(List<String> projectList,List<String> combineList,List<String> entityList,List<String> departmentList,List<String> departmentList1,
							   List<String> bakList,List<String> mainBusinessList, List<String> segmentList,List<String> currencyList,String userName){
		String check="";
		/**投資編號*/
		check=this.check(projectList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Project' and DIMENSION not like 'P_CE%'");
		Assert.isTrue("".equals(check),"以下【投資編號】在【維度表】没有找到---> " + check);
		/**設備類別*/
		check=this.check(combineList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Combine'");
		Assert.isTrue("".equals(check),"以下【設備類別】在【維度表】没有找到---> " + check);
		/**SBU_法人*/
		check=this.check(entityList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Entity'");
		Assert.isTrue("".equals(check),"以下【SBU_法人】在【維度表】没有找到---> " + check);
		/**提出部門*/
		List<BigDecimal> countList = (List<BigDecimal>)investmentBudgetDao.listBySql("select count(1) from FIT_USER_DEPARTMENT_MAPPING where USER_CODE='"+userName+"' ");
		if(countList.get(0).intValue()>0){
			check=this.check(departmentList1,"select distinct trim(m.alias) from FIT_USER_DEPARTMENT_MAPPING,FIT_ZR_DIMENSION m where DEPARTMENT_CODE=m.parent and USER_CODE='"+userName+"'");
			Assert.isTrue("".equals(check),"以下【提出部門】沒有上傳權限---> " + check);
		}
		check=this.check(departmentList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Department'");
		Assert.isTrue("".equals(check),"以下【部門】在【維度表】没有找到---> " + check);
		/**Segment*/
		check=this.check(bakList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_bak1'");
		Assert.isTrue("".equals(check),"以下【Segment】在【維度表】没有找到---> " + check);
		/**Main business*/
		check=this.check(mainBusinessList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_bak2' and DIMENSION not in('bak20100','bak20104','bak20105')");
		Assert.isTrue("".equals(check),"以下【Main business】在【維度表】没有找到---> " + check);
		/**產業*/
		check=this.check(segmentList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Segment'");
		Assert.isTrue("".equals(check),"以下【產業】在【維度表】没有找到---> " + check);
		/**投資類型*/
		check=this.check(currencyList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Currency'");
		Assert.isTrue("".equals(check),"以下【投資類型】在【維度表】没有找到---> " + check);
	}
	/**匹配用戶上傳的主數據list是否在維度表中能找到*/
	public String check(List<String> list,String sql){
		list = instrumentClassService.removeDuplicate(list);
		List<String> checkList = this.listBySql(sql);
		String check = instrumentClassService.getDiffrent(list, checkList);
		return check;
	}
	/**預算保存數據*/
	public void saveBatch(List<InvestmentBudget> list,String year,String user) throws Exception {
		String sql="delete from FIT_INVESTMENT_BUDGET where VERSION='V00' and YEAR='"+year+"' and CREATE_NAME ='"+user+"'";
		investmentBudgetDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			investmentBudgetDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				investmentBudgetDao.getHibernateTemplate().flush();
				investmentBudgetDao.getHibernateTemplate().clear();
			}
		}
	}
	/**預測保存數據*/
	public void saveBatchForecast(List<InvestmentForecast> list,String year,String user) throws Exception {
		String sql="delete from FIT_INVESTMENT_FORECAST where VERSION='V00' and YEAR='"+year+"' and CREATE_NAME ='"+user+"'";
		investmentForecastDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			investmentForecastDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				investmentForecastDao.getHibernateTemplate().flush();
				investmentForecastDao.getHibernateTemplate().clear();
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
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"投資預算模板","投資預算模板")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"投資預算模板","投資預算模板")+".xlsx");
			if(type.equals("forecast")){
				filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"投資預測模板","投資預測模板")+".xlsx";
				ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"投資預測模板","投資預測模板")+".xlsx");
			}
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			//預算應爲測試需要先把年份校驗放開
//			int year=calendar.get(Calendar.YEAR);
			int year=calendar.get(Calendar.YEAR)-1;
			row.getCell(9).setCellValue("FY"+ String.valueOf(year+1).substring(2));
			row.getCell(21).setCellValue("FY"+ String.valueOf(year+2).substring(2));
			row.getCell(23).setCellValue("FY"+ String.valueOf(year+3).substring(2));
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
	public Map<String,String>  downloadBudget(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest,String type){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"投資預算表","投資預算表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"投資預算模板","投資預算模板")+".xlsx");
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
			String sql="select * from FIT_INVESTMENT_BUDGET_V where YEAR='"+y+"' ";
			if(type.equals("forecast")){
				filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"投資預測表","投資預測表")+".xlsx";
				ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"投資預測模板","投資預測模板")+".xlsx");
				sql="select * from FIT_INVESTMENT_FORECAST_V where YEAR='"+y+"' ";
			}
			String roleSql="select count(1) from  fit_user u \n" +
					" left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
					" left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
					" WHERE  u.username='"+loginUser.getUsername()+"' and code='investment' ";
			List<BigDecimal> countList = (List<BigDecimal>)investmentBudgetDao.listBySql(roleSql);
			if(countList.get(0).intValue()==0){
				sql+="and CREATE_NAME='"+loginUser.getUsername()+"'";
			}
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(9).setCellValue("FY"+(year));
			row.getCell(21).setCellValue("FY"+(year+1));
			row.getCell(23).setCellValue("FY"+(year+2));
			if (null!=version && StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from FIT_ZR_DIMENSION where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='ZR_Entity'  and PARENT in("+sbuStr+")";
			List<Map> sbuMap=investmentBudgetDao.listMapBySql(sbusql);
			sql+=instrumentClassService.querySbuSql(entitys,sbuMap);
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+="order by investment_no,Id";
			List<Object[]> dataList = investmentBudgetDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 2;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 4; i < objects.length; i++) {
						Cell cell = contentRow.createCell(i-4);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>22|| StringUtils.isNotEmpty(text) && i==21) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = investmentBudgetDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 4; i < objects.length; i++) {
								Cell cell = contentRow.createCell(i-4);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>22|| StringUtils.isNotEmpty(text) && i==21) {
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
	public String versionBudget(String tableName){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from "+tableName+" where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = investmentBudgetDao.listMapBySql(sqlVersion);
		if(null ==maps.get(0).get("VERSION")){
			sqlVersion="No finalizable data detected_沒有檢查到可定版的數據！";
		}else if (maps == null || maps.get(0).get("VERSION").toString().equals("0")) {
			sqlVersion="V1";
		}else{
			int a=Integer.parseInt(maps.get(0).get("VERSION").toString());
			a++;
			sqlVersion="V"+a;
		}
		String sql="insert into "+tableName+" (select\n" +
				"SEQ_BUDGET_DETAIL_REVENUE.NEXTVAL id,create_name,create_date," +
				"investment_no, \n" +
				"category_equipment, \n" +
				"entity, \n" +
				"department, \n" +
				"segment, \n" +
				"main_business, \n" +
				"industry, \n" +
				"object_investment, \n" +
				"use_department, \n" +
				"investment_type, \n" +
				"project_name, \n" +
				"product_series, \n" +
				"quantity_required, \n" +
				"product_life_cycle, \n" +
				"po_period, \n" +
				"receipt_date, \n" +
				"due_period, \n" +
				"amount_investment, \n" +
				"description_investment, \n" +
				"revenue, \n" +
				"profit, \n" +
				"next_revenue, \n" +
				"next_profit, \n" +
				"after_revenue, \n" +
				"after_profit,year," +
				"'"+sqlVersion+"' version, \n" +
				" sysdate version_date,'"+loginUser.getUsername()+"' version_name " +
				"  from "+tableName+" where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		investmentBudgetDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}

	/**預測單條數據刪除*/
	public void deleteForecast(String id) throws Exception {
		investmentForecastDao.delete(id);
	}
	/**維度表下載*/
	public Map<String,String> dimension(HttpServletRequest request) {
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String filePath=request.getRealPath("")+"static"+File.separator+"download"+File.separator+"投資預算維度表.xlsx";
			InputStream ins = new FileInputStream(request.getRealPath("")+"static"+File.separator+"template"+File.separator+"investment"+File.separator+"投資預算維度表.xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			/**投資編號*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Project' and DIMENSION not like 'P_CE%'",workBook.getSheetAt(0));
			/**設備類別*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Combine'",workBook.getSheetAt(1));
			/**SBU_法人*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Entity'",workBook.getSheetAt(2));
			/**提出部門/使用部門*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Department'",workBook.getSheetAt(3));
			/**Segment*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_bak1'",workBook.getSheetAt(4));
			/**Main business*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_bak2' and DIMENSION not in('bak20100','bak20104','bak20105')",workBook.getSheetAt(5));
			/**產業*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Segment'",workBook.getSheetAt(6));
			/**投資類型*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Currency'",workBook.getSheetAt(7));
			File outFile = new File(filePath);
			OutputStream out = new FileOutputStream(outFile);
			workBook.write(out);
			workBook.close();
			out.flush();
			out.close();
			mapResult.put("str",outFile.getName());
			System.gc();
		}catch (Exception e){
			e.printStackTrace();
			mapResult.put("result","N");
			mapResult.put("str",ExceptionUtil.getRootCauseMessage(e));
		}
		return mapResult;
	}
	/**根據sql獲取緯度值*/
	private void selectDimension(String sql,Sheet sheet){
		List<Map> list=investmentBudgetDao.listMapBySql(sql);
		for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow(i+1);
			Map map=list.get(i);
			Cell cell = row.createCell(0);
			cell.setCellValue(instrumentClassService.mapValString(map.get("DIMENSION")));
			if(null!=map.get("ALIAS")) {
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(instrumentClassService.mapValString(map.get("ALIAS").toString()));
			}
		}
	}
}
