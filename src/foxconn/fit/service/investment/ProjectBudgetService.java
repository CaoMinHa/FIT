package foxconn.fit.service.investment;

import foxconn.fit.dao.investment.ProjectBudgetDao;
import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.investment.ProjectForecastDao;
import foxconn.fit.entity.investment.ProjectBudget;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.investment.ProjectForecast;
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
public class ProjectBudgetService extends BaseService<ProjectBudget> {

	@Autowired
	private ProjectBudgetDao projectBudgetDao;
	@Autowired
	private ProjectForecastDao projectForecastDao;
	@Autowired
	private InstrumentClassService instrumentClassService;
	@Override
	public BaseDaoHibernate<ProjectBudget> getDao() {
		return projectBudgetDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = projectBudgetDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
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
		List<String> versionList=projectBudgetDao.listBySql(sqlVersion);
		sqlVersion="select distinct version from "+forecastTabel+" where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"' and version<>'V00' order by version";
		versionList.addAll(projectBudgetDao.listBySql(sqlVersion));
		return  versionList;
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
		List<BigDecimal> countList = (List<BigDecimal>)projectForecastDao.listBySql(roleSql);
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
		List<Map> sbuMap=projectBudgetDao.listMapBySql(sbu);
		sql+=instrumentClassService.querySbuSql(entity,sbuMap);
		sql+=" order by year,entity,ID";
		return sql;
	}

	/**數據上傳*/
	public String uploadBudget(AjaxResult result, Locale locale, MultipartHttpServletRequest multipartHttpServletRequest,String type) {
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
				Workbook wb = null;
				if ("xls".equals(suffix)) {
					//Excel2003
					wb = new HSSFWorkbook(file.getInputStream());
				}else if("xlsx".equals(suffix)) {
					//Excel2007
					wb = new XSSFWorkbook(file.getInputStream());
				}else{
					result.put("flag", "fail");
					result.put("msg", instrumentClassService.getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
					return result.getJson();
				}
				wb.close();
				Sheet sheet = wb.getSheetAt(0);
				String v_year = ExcelUtil.getCellStringValue(sheet.getRow(0).getCell(4), 0);
				Assert.isTrue("FY".equals(v_year.substring(0, 2)), instrumentClassService.getLanguage(locale, "請下載模板上傳數據！", "Please use the template to upload data"));
				Calendar calendar = Calendar.getInstance();
				String year = Integer.toString(calendar.get(Calendar.YEAR) + 1);
				Assert.isTrue(year.substring(2).equals(v_year.substring(2)), instrumentClassService.getLanguage(locale, "僅可上傳明年的預算數據！", "Only next year's budget data can be uploaded"));
				int column = sheet.getRow(1).getLastCellNum();
				Assert.isTrue(column <= 24,instrumentClassService.getLanguage(locale, "Excel列数不能小于" + 24 + "，請下載正確的模板上傳數據！", "Number Of Columns Can Not Less Than" + 24 + ",Please download the correct template to upload the data"));
				int rowNum = sheet.getPhysicalNumberOfRows();
				Assert.isTrue(rowNum >2,instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
				List list;
				if(type.equals("budget")){
					list = new ArrayList<ProjectBudget>();
				}else{
					list = new ArrayList<ProjectForecast>();
				}
				/**專案編號*/List<String> projectList = new ArrayList<>();
				/**SBU_法人*/List<String> entityList = new ArrayList<>();
				/**SBU**/List<String> sbuList = new ArrayList<>();
				/**提出部門*/List<String> departmentList = new ArrayList<>();
				/**產業*/List<String> segmentList = new ArrayList<>();
				UserDetailImpl loginUser = SecurityUtils.getLoginUser();
				String check = "";
				String mianDataChek="";
				for (int i = 2; i < rowNum; i++) {
					if(null==sheet.getRow(i)){
						continue;
					}
					Row row = sheet.getRow(i);
					String project=ExcelUtil.getCellStringValue(row.getCell(0), i);
					String entity=ExcelUtil.getCellStringValue(row.getCell(1), i);
					String department=ExcelUtil.getCellStringValue(row.getCell(2), i);
					String segment=ExcelUtil.getCellStringValue(row.getCell(3), i);
					if(project.isEmpty()||entity.isEmpty()||department.isEmpty()||segment.isEmpty()){
						mianDataChek+=(i+1)+",";
						continue;
					}
					//跳過沒有SBU權限的數據
					String sql="select distinct PARENT from FIT_ZR_DIMENSION where type='ZR_Entity' and ALIAS='"+entity+"'";
					List<String> listSbu=projectBudgetDao.listBySql(sql);
					sbuList.addAll(listSbu);
					check = instrumentClassService.getDiffrent(listSbu, tarList);
					if (!"".equalsIgnoreCase(check.trim()) || check.length() > 0) {
						continue;
					}
					projectList.add(project);
					entityList.add(entity);
					departmentList.add(department);
					segmentList.add(segment);
					if("budget".equals(type)){
						ProjectBudget projectBudget = new ProjectBudget();
						projectBudget.setProjectNumber(project);
						projectBudget.setEntity(entity);
						projectBudget.setDepartment(department);
						projectBudget.setIndustry(segment);
						projectBudget.setYear(v_year);
						projectBudget.setCreateName(loginUser.getUsername());
						list.add(this.projectBudget(projectBudget,row,i));
					}else{
						ProjectForecast projectForecast =new ProjectForecast();
						projectForecast.setProjectNumber(project);
						projectForecast.setEntity(entity);
						projectForecast.setDepartment(department);
						projectForecast.setIndustry(segment);
						projectForecast.setYear(v_year);
						projectForecast.setCreateName(loginUser.getUsername());
						list.add(this.projectForecast(projectForecast,row,i));
					}
				}
				Assert.isTrue(null!=list,instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
				if(!instrumentClassService.removeDuplicate(entityList).isEmpty()){
					checkMianData(projectList,entityList,departmentList,segmentList);
					if(type.equals("budget")){
						this.saveBatch(list,v_year,loginUser.getUsername());
					}else {
						this.saveBatchForecast(list,v_year,loginUser.getUsername());
					}
					sbuList=instrumentClassService.removeDuplicate(sbuList);
				}
				check = instrumentClassService.getDiffrent(sbuList, tarList);
				if (!"".equalsIgnoreCase(check.trim()) && check.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下數據未上傳成功，請檢查您是否具備該SBU權限。--->" + check, "The following data fails to be uploaded. Check whether you have the SBU permission--->" + check));
				}
				if (!"".equalsIgnoreCase(mianDataChek.trim()) && mianDataChek.length() > 0) {
					result.put("msg", instrumentClassService.getLanguage(locale, "以下行數據未上傳成功，主數據不可爲空。--->" + mianDataChek.substring(0,mianDataChek.length()-1), "The following lines fail to be uploaded. Primary data cannot be null--->" + mianDataChek.substring(0,mianDataChek.length()-1)));
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
	private ProjectBudget projectBudget(ProjectBudget projectBudget,Row row,int i) throws Exception {
		projectBudget.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(4), i));
		projectBudget.setProjectName(ExcelUtil.getCellStringValue(row.getCell(5), i));
		projectBudget.setObjectInvestment(ExcelUtil.getCellStringValue(row.getCell(6), i));
		projectBudget.setProjectDescription(ExcelUtil.getCellStringValue(row.getCell(7), i));
		projectBudget.setThree(ExcelUtil.getCellStringValue(row.getCell(8), i));
		projectBudget.setSegment(ExcelUtil.getCellStringValue(row.getCell(9), i));
		projectBudget.setMainBusiness(ExcelUtil.getCellStringValue(row.getCell(10), i));
		projectBudget.setProductLifeCycle(ExcelUtil.getCellStringValue(row.getCell(11), i));
		projectBudget.setStartYear(ExcelUtil.getCellStringValue(row.getCell(12), i));
		projectBudget.setStartMonth(ExcelUtil.getCellStringValue(row.getCell(13), i));
		projectBudget.setEndYear(ExcelUtil.getCellStringValue(row.getCell(14), i));
		projectBudget.setEndMonth(ExcelUtil.getCellStringValue(row.getCell(15), i));
		projectBudget.setExpenditureExpenses(ExcelUtil.getCellDoubleValue(row.getCell(16), i));
		projectBudget.setCapitalExpenditure(ExcelUtil.getCellDoubleValue(row.getCell(17), i));
		projectBudget.setRevenue(ExcelUtil.getCellDoubleValue(row.getCell(18), i));
		projectBudget.setProfit(ExcelUtil.getCellDoubleValue(row.getCell(19), i));
		projectBudget.setNextRevenue(ExcelUtil.getCellDoubleValue(row.getCell(20), i));
		projectBudget.setNextProfit(ExcelUtil.getCellDoubleValue(row.getCell(21), i));
		projectBudget.setAfterRevenue(ExcelUtil.getCellDoubleValue(row.getCell(22), i));
		projectBudget.setAfterProfit(ExcelUtil.getCellDoubleValue(row.getCell(23), i));
		projectBudget.setId(UUID.randomUUID().toString());
		projectBudget.setVersion("V00");
		projectBudget.setCreateDate(new Date());
		return projectBudget;
	}
	/**上傳預測保存數據封裝實體類*/
	private ProjectForecast projectForecast(ProjectForecast projectForecast,Row row,int i) throws Exception {
		projectForecast.setProductSeries(ExcelUtil.getCellStringValue(row.getCell(4), i));
		projectForecast.setProjectName(ExcelUtil.getCellStringValue(row.getCell(5), i));
		projectForecast.setObjectInvestment(ExcelUtil.getCellStringValue(row.getCell(6), i));
		projectForecast.setProjectDescription(ExcelUtil.getCellStringValue(row.getCell(7), i));
		projectForecast.setThree(ExcelUtil.getCellStringValue(row.getCell(8), i));
		projectForecast.setSegment(ExcelUtil.getCellStringValue(row.getCell(9), i));
		projectForecast.setMainBusiness(ExcelUtil.getCellStringValue(row.getCell(10), i));
		projectForecast.setProductLifeCycle(ExcelUtil.getCellStringValue(row.getCell(11), i));
		projectForecast.setStartYear(ExcelUtil.getCellStringValue(row.getCell(12), i));
		projectForecast.setStartMonth(ExcelUtil.getCellStringValue(row.getCell(13), i));
		projectForecast.setEndYear(ExcelUtil.getCellStringValue(row.getCell(14), i));
		projectForecast.setEndMonth(ExcelUtil.getCellStringValue(row.getCell(15), i));
		projectForecast.setExpenditureExpenses(ExcelUtil.getCellDoubleValue(row.getCell(16), i));
		projectForecast.setCapitalExpenditure(ExcelUtil.getCellDoubleValue(row.getCell(17), i));
		projectForecast.setRevenue(ExcelUtil.getCellDoubleValue(row.getCell(18), i));
		projectForecast.setProfit(ExcelUtil.getCellDoubleValue(row.getCell(19), i));
		projectForecast.setNextRevenue(ExcelUtil.getCellDoubleValue(row.getCell(20), i));
		projectForecast.setNextProfit(ExcelUtil.getCellDoubleValue(row.getCell(21), i));
		projectForecast.setAfterRevenue(ExcelUtil.getCellDoubleValue(row.getCell(22), i));
		projectForecast.setAfterProfit(ExcelUtil.getCellDoubleValue(row.getCell(23), i));
		projectForecast.setId(UUID.randomUUID().toString());
		projectForecast.setVersion("V00");
		projectForecast.setCreateDate(new Date());
		return projectForecast;
	}
	/**上傳保存數據校驗主數據是否正確*/
	private void checkMianData(List<String> projectList,List<String> entityList,List<String> departmentList,List<String> segmentList){
		String check="";
		/**專案編號*/
		check=this.check(projectList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Project'");
		Assert.isTrue("".equals(check),"以下【投資編號】在【維度表】没有找到---> " + check);
		/**SBU_法人*/
		check=this.check(entityList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Entity'");
		Assert.isTrue("".equals(check),"以下【SBU_法人】在【維度表】没有找到---> " + check);
		/**提出部門*/
		check=this.check(departmentList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Department'");
		Assert.isTrue("".equals(check),"以下【提出部門】在【維度表】没有找到---> " + check);
		/**產業*/
		check=this.check(segmentList,"select distinct trim(alias) from FIT_ZR_DIMENSION where type='ZR_Segment'");
		Assert.isTrue("".equals(check),"以下【產業】在【維度表】没有找到---> " + check);
	}
	/**匹配用戶上傳的主數據list是否在維度表中能找到*/
	public String check(List<String> list,String sql){
		list = instrumentClassService.removeDuplicate(list);
		List<String> checkList = this.listBySql(sql);
		String check = instrumentClassService.getDiffrent(list, checkList);
		return check;
	}
	/**預算保存數據*/
	public void saveBatch(List<ProjectBudget> list,String year,String user) throws Exception {
		String sql="delete from FIT_PROJECT_BUDGET where VERSION='V00' and YEAR='"+year+"' and CREATE_NAME ='"+user+"'";
		projectBudgetDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			projectBudgetDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				projectBudgetDao.getHibernateTemplate().flush();
				projectBudgetDao.getHibernateTemplate().clear();
			}
		}
	}
	/**預測保存數據*/
	public void saveBatchForecast(List<ProjectForecast> list,String year,String user) throws Exception {
		String sql="delete from FIT_PROJECT_FORECAST where VERSION='V00' and YEAR='"+year+"' and CREATE_NAME ='"+user+"'";
		projectForecastDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		for (int i = 0; i < list.size(); i++) {
			projectForecastDao.save(list.get(i));
			if ((i + 1) % 1000 == 0) {
				projectForecastDao.getHibernateTemplate().flush();
				projectForecastDao.getHibernateTemplate().clear();
			}
		}
	}

	/**下載專案模板*/
	public Map<String,String>  template(HttpServletRequest request,String type) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"專案預算模板","專案預算模板")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"專案預算模板","專案預算模板")+".xlsx");
			if(type.equals("forecast")){
				filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"專案預測模板","專案預測模板")+".xlsx";
				ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"專案預測模板","專案預測模板")+".xlsx");
			}
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Calendar calendar = Calendar.getInstance();
			Row row =sheet.getRow(0);
			int year=calendar.get(Calendar.YEAR);
			row.getCell(4).setCellValue("FY"+ String.valueOf(year+1).substring(2));
			row.getCell(20).setCellValue("FY"+ String.valueOf(year+2).substring(2));
			row.getCell(22).setCellValue("FY"+ String.valueOf(year+3).substring(2));
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
	public Map<String,String>  downloadBudget(String entitys,String y,String version,HttpServletRequest request, PageRequest pageRequest,String type){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"專案預算表","專案預算表")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"專案預算模板","專案預算模板")+".xlsx");
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
			String sql="select * from FIT_PROJECT_BUDGET_V where YEAR='"+y+"' ";
			if(type.equals("forecast")){
				filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"專案預測表","專案預測表")+".xlsx";
				ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"專案預測模板","專案預測模板")+".xlsx");
				sql="select * from FIT_PROJECT_FORECAST_V where YEAR='"+y+"' ";
			}
			String roleSql="select count(1) from  fit_user u \n" +
					" left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
					" left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
					" WHERE  u.username='"+loginUser.getUsername()+"' and code='investment' ";
			List<BigDecimal> countList = (List<BigDecimal>)projectForecastDao.listBySql(roleSql);
			if(countList.get(0).intValue()==0){
				sql+="and CREATE_NAME='"+loginUser.getUsername()+"'";
			}
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(5).setCellValue("FY"+(year));
			row.getCell(20).setCellValue("FY"+(year+1));
			row.getCell(22).setCellValue("FY"+(year+2));
			if (StringUtils.isNotEmpty(version)) {
				sql+=" and VERSION='"+version+"'";
			}
			//獲取當前用戶的SBU權限
			String sbuStr = instrumentClassService.getBudgetSBUStr();
			String sbusql="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS, ','||PARENT||',' PARENT from FIT_ZR_DIMENSION where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='ZR_Entity'  and PARENT in("+sbuStr+")";
			List<Map> sbuMap=projectBudgetDao.listMapBySql(sbusql);
			sql+=instrumentClassService.querySbuSql(entitys,sbuMap);
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			pageRequest.setOrderBy("Id");
			List<Object[]> dataList = projectBudgetDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 2;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 4; i < objects.length; i++) {
						Cell cell = contentRow.createCell(i-4);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>19) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = projectBudgetDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 4; i < objects.length; i++) {
								Cell cell = contentRow.createCell(i-4);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>19) {
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
	/**版本控制*/
	public String versionBudget(String tableName){
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR)+1;
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String sqlVersion="select Max(to_number(substr(version,2))) version  from "+tableName+" where Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"'";
		List<Map> maps = projectBudgetDao.listMapBySql(sqlVersion);
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
				"       project_number,\n" +
				"       entity,\n" +
				"       department,\n" +
				"       industry,\n" +
				"       object_investment,\n" +
				"       product_series,\n" +
				"       project_name,\n" +
				"       project_description,\n" +
				"       three,\n" +
				"       segment,\n" +
				"       main_business,\n" +
				"       product_life_cycle,\n" +
				"       start_year,\n" +
				"       start_month,\n" +
				"       end_year,\n" +
				"       end_month,\n" +
				"       expenditure_expenses,\n" +
				"       capital_expenditure,\n" +
				"       revenue,\n" +
				"       profit,\n" +
				"       next_revenue,\n" +
				"       next_profit,\n" +
				"       after_revenue,\n" +
				"       after_profit,year," +
				"'"+sqlVersion+"' version, \n" +
				" sysdate version_date,'"+loginUser.getUsername()+"' version_name " +
				"  from "+tableName+" where version='V00' and Year='FY"+String.valueOf(year).substring(2)+"' and  CREATE_NAME='"+loginUser.getUsername()+"')";
		projectBudgetDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		return sqlVersion;
	}
	/**預測刪除值*/
	public void deleteForecast(String id) throws Exception {
		projectForecastDao.delete(id);
	}
	/**維度表下載*/
	public Map<String,String> dimension(HttpServletRequest request) {
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String filePath=request.getRealPath("")+"static"+File.separator+"download"+File.separator+"專案預算維度表.xlsx";
			InputStream ins = new FileInputStream(request.getRealPath("")+"static"+File.separator+"template"+File.separator+"investment"+File.separator+"專案預算維度表.xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			/**專案編碼*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Project' and DIMENSION like 'P_CE%'",workBook.getSheetAt(0));
			/**sbu_法人*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Entity'",workBook.getSheetAt(1));
			/**提出部門*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Department'",workBook.getSheetAt(2));
			/**產業*/
			this.selectDimension("select distinct DIMENSION,ALIAS from FIT_ZR_DIMENSION where type='ZR_Segment'",workBook.getSheetAt(3));
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
		List<Map> list=projectBudgetDao.listMapBySql(sql);
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
