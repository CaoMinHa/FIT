package foxconn.fit.service.ProfitLoss;

import foxconn.fit.dao.ProfitLoss.DataSourceDao;
import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.investment.ProjectBudgetDao;
import foxconn.fit.entity.ProfitLoss.DataSource;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.investment.ProjectBudget;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import foxconn.fit.advice.Log;
/**
 * @author cxj
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DataSourceService extends BaseService<DataSource> {

	@Autowired
	private DataSourceDao dataSourceDao;
	@Autowired
	private InstrumentClassService instrumentClassService;
	@Override
	public BaseDaoHibernate<DataSource> getDao() {
		return dataSourceDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = dataSourceDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		return model;
	}
	
	/**獲取表中版本值*/
	public List<String> versionVal(){
		String sqlVersion="select distinct version_d from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and version_d is not null and data_type='DATA SOURCE' order by version_d";
		List<String> versionList=dataSourceDao.listBySql(sqlVersion);
	
		return  versionList;
	}
	public List<String> entityVal(){
		/*String sqlEntity="select distinct entity from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL'";
		List<String> entityList=dataSourceDao.listBySql(sqlEntity);*/
		String legalCode=SecurityUtils.getLegalCode();
		String targetList="";
		//System.out.println(legalList);
		if (StringUtils.isNotEmpty(legalCode)) {
			for (String string : legalCode.split(",")) {
				List<String> entity = dataSourceDao.listBySql("select distinct entity_name from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL' and entity='"+string+"'");
				targetList+=entity.get(0)+",";
			}
		}
		targetList=targetList.substring(0, targetList.length()-1);
		
		return  Arrays.asList(targetList.split(","));
	}
	/**頁面查詢*/
	public String viewList(String years,String versions,String entitys,String scenarios){
	
		String sql="select * from CUX_BUDGET_EXPENSE_HP_USD where data_type='DATA SOURCE' ";
			if (null!=years&&StringUtils.isNotEmpty(years)) {
			sql+=" and basis_year='"+years+"'";
		}
		if (null!=versions && StringUtils.isNotEmpty(versions)) {
			sql+=" and version_d='"+versions+"'";
		}
		if (null!=scenarios && StringUtils.isNotEmpty(scenarios)) {
			sql+=" and scenario='"+scenarios+"'";
		}
		
		if (null!=entitys && StringUtils.isNotEmpty(entitys)) {
			sql+=" and regexp_substr(ENTITY, '[^_]+', 1, 2, 'i') =(select distinct entity from CUX_BUDGET_HP_PL where entity_name='"+entitys+"')";
			
		}else {
		String tarList=instrumentClassService.getBudgetlegal();
		sql+=" and regexp_substr(ENTITY, '[^_]+', 1, 2, 'i') in ("+tarList+")";
		}
		sql+=" order by basis_year,entity,version_d";
		return sql;
	}
	
	/**下載數據*/
	
	public Map<String,String>  downloadBudget(String entitys,String y,String versions,HttpServletRequest request, PageRequest pageRequest,String scenarios){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"損益表數據源","DataSource")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"損益表數據源","DataSource")+".xlsx");
			String sql="select * from CUX_BUDGET_EXPENSE_SOURCE_V where data_type='DATA SOURCE' and basis_year='"+y+"' and scenario='"+scenarios+"' and version_d='"+versions+"' and legal =(select distinct entity from CUX_BUDGET_HP_PL where entity_name='"+entitys+"')";
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(8).setCellValue(y);
			row.getCell(21).setCellValue("FY"+(year+1));
			row.getCell(22).setCellValue("FY"+(year+2));
			row.getCell(23).setCellValue("FY"+(year+3));
			row.getCell(24).setCellValue("FY"+(year+4));
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+=" order by basis_year,entity";
			List<Object[]> dataList = dataSourceDao.findPageBySql(pageRequest, sql).getResult();
		
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 2;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 0; i < objects.length; i++) {
						if(i==25){
							break;
						}
						Cell cell = contentRow.createCell(i);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>7 && i<25) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = dataSourceDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 0; i < objects.length; i++) {
								if(i==25){
									break;
								}
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>7 && i<25) {
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
