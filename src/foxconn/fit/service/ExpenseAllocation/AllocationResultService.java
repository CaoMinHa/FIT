package foxconn.fit.service.ExpenseAllocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
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

import foxconn.fit.dao.ExpenseAllocation.AllocationResultDao;
import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.entity.ExpenseAllocation.AllocationResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

/**
 * @author cxj20241220
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AllocationResultService extends BaseService<AllocationResult> {

	@Autowired
	private AllocationResultDao allocationResultDao;
	@Autowired
	private InstrumentClassService instrumentClassService;
	@Override
	public BaseDaoHibernate<AllocationResult> getDao() {
		return allocationResultDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = allocationResultDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		return model;
	}
	
	
	/**獲取表中entity值*/
	public List<String> entityVal1(){
		String sqlEntity="select distinct entity from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL'";
		List<String> entityList=allocationResultDao.listBySql(sqlEntity);
		return  entityList;

	}
	/**頁面查詢*/
	public String viewList(String years,String scenarios,String types){
		
		String sql=" SELECT * FROM  EPMODS.CUX_BUDGET_ALLOCATION_VERSION where 1=1 ";
			if (null!=years&&StringUtils.isNotEmpty(years)) {
			sql+=" and basis_year='"+years+"'";
		}
		if (null!=scenarios && StringUtils.isNotEmpty(scenarios)) {
			sql+=" and scenario='"+scenarios+"'";
		}
		
		if (null!=types && StringUtils.isNotEmpty(types)) {
			sql+=" and data_type='"+types+"'";
		}
		sql+=" order by scenario,basis_year,version_d";
		return sql;
	}
	
		
	/**下載數據*/
	
	public Map<String,String>  downloadBudget(String y,String id,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"分攤結果","AllocationResult")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"分攤結果","AllocationResult")+".xlsx");
			String sql="select * from CUX_BUDGET_ALLOCATION_RESULT_V where basis_year=(select basis_year from CUX_BUDGET_ALLOCATION_VERSION where id='"+id+"') and scenario=(select scenario from CUX_BUDGET_ALLOCATION_VERSION where id='"+id+"') and version_d=(select version_d from CUX_BUDGET_ALLOCATION_VERSION where id='"+id+"') and DATA_TYPE =(select DATA_TYPE from CUX_BUDGET_ALLOCATION_VERSION where id='"+id+"')  ";
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
			sql+=" order by basis_year,entity,department,account,SBU";
			List<Object[]> dataList = allocationResultDao.findPageBySql(pageRequest, sql).getResult();
		
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
					dataList = allocationResultDao.findPageBySql(pageRequest, sql).getResult();
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
