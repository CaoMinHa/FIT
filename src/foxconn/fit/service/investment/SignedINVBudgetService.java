package foxconn.fit.service.investment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.investment.SignedINVBudgetDao;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.investment.SignedINVBudget;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

/**
 * @author cxj20250211
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SignedINVBudgetService extends BaseService<SignedINVBudget> {

	@Autowired
	private SignedINVBudgetDao signedINVBudgetDao;
	@Autowired
	private InstrumentClassService instrumentClassService;

	@Override
	public BaseDaoHibernate<SignedINVBudget> getDao() {
		return signedINVBudgetDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = signedINVBudgetDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		return model;
	}
	/**頁面查詢*/
	public String viewList(String year,String entity,String tableName){
		UserDetailImpl loginUser = SecurityUtils.getLoginUser();
		String userName=loginUser.getUsername();
		String sql="select * from "+tableName+" where CREATE_NAME='"+userName+"'";
		String roleSql="select count(1) from  fit_user u \n" +
				" left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
				" left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
				" WHERE  u.username='"+userName+"' and code='investment' ";
		List<BigDecimal> countList = (List<BigDecimal>)signedINVBudgetDao.listBySql(roleSql);
		if(countList.get(0).intValue()>0){
			sql="select * from "+tableName+" where 1=1 ";
		}
		if (null!=year&&StringUtils.isNotEmpty(year)) {
			sql+=" and YEAR='"+year+"'";
		}
		if (null!=entity && StringUtils.isNotEmpty(entity) && !entity.equals("") ) {
			String tarList=instrumentClassService.querySbuSql2(entity);
			sql+=" and SBU_CODE in ("+tarList+")";
			
		}else {
			String tarList=instrumentClassService.getBudgetSBUStr();
			sql+=" and SBU_CODE in ("+tarList+")";
			}
		sql+=" order by year,sbu_code,date_v";
		return sql;
	}

/**下載數據*/
	
	public Map<String,String>  downloadBudget(String y,String id,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"投資預算表","Investment budget")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"投資預算模板","Investment budget")+".xlsx");
			String sql="select * from CUX_INV_BUDGET_INFO_V where year=(select year from CUX_INV_BUDGET_INFO where id='"+id+"')  and doc_number=(select doc_number from CUX_INV_BUDGET_INFO where id='"+id+"')";
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			//row.getCell(8).setCellValue(y);
			row.getCell(9).setCellValue("FY"+(year));
			
			row.getCell(21).setCellValue("FY"+(year+1));
			row.getCell(23).setCellValue("FY"+(year+2));
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+=" order by investment_no,Id";
			List<Object[]> dataList = signedINVBudgetDao.findPageBySql(pageRequest, sql).getResult();
			String sqlentity="select  *  from CUX_INV_BUDGET_INFO where id='"+id+"'";
			this.selectDocnumber(sqlentity,workBook.getSheetAt(0),row); 
			
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 2;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 5; i < objects.length; i++) {
						Cell cell = contentRow.createCell(i-5);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>23|| StringUtils.isNotEmpty(text) && i==22 ) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = signedINVBudgetDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 5; i < objects.length; i++) {
								Cell cell = contentRow.createCell(i-5);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>23|| StringUtils.isNotEmpty(text) && i==22 ) {
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
	/**根據sql獲取值*/
	private void selectDocnumber(String sql,Sheet sheet,Row row){
		List<Map> list=signedINVBudgetDao.listMapBySql(sql);
		for (int i = 0; i < list.size(); i++) {
			Map map=list.get(i);
			//row.getCell(0).setCellValue("簽核單號："+instrumentClassService.mapValString(map.get("doc_number"))+" 申請日期："+instrumentClassService.mapValString(map.get("date_v")));
			row.getCell(0).setCellValue("單位："+instrumentClassService.mapValString(map.get("SBU_NAME"))+"(簽核單號："+instrumentClassService.mapValString(map.get("DOC_NUMBER"))+")  日期："+instrumentClassService.mapValString(map.get("DATE_V")));

		}
	}
	/**下載數據*/
	public Map<String,String>  downloadBudget(String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest,String type){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"投資預算表","Investment budget")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"投資預算模板","Investment budget")+".xlsx");
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
			List<BigDecimal> countList = (List<BigDecimal>)signedINVBudgetDao.listBySql(roleSql);
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
			List<Map> sbuMap=signedINVBudgetDao.listMapBySql(sbusql);
			sql+=instrumentClassService.querySbuSql(entitys,sbuMap);
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+="order by investment_no,Id";
			List<Object[]> dataList = signedINVBudgetDao.findPageBySql(pageRequest, sql).getResult();
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
					dataList = signedINVBudgetDao.findPageBySql(pageRequest, sql).getResult();
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

	
	
}
