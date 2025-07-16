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

import foxconn.fit.dao.ExpenseAllocation.AllocationRatioDao;
import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.entity.ExpenseAllocation.AllocationRatio;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
/**
 * @author cxj20241219
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AllocationRatioService extends BaseService<AllocationRatio> {

	@Autowired
	private AllocationRatioDao allocationRatioDao;
	@Autowired
	private InstrumentClassService instrumentClassService;
	@Override
	public BaseDaoHibernate<AllocationRatio> getDao() {
		return allocationRatioDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = allocationRatioDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		return model;
	}
	
	/**頁面查詢*/
	public String viewList(String years,String types,String scenarios){
	
		String sql="select * from CUX_BUDGET_ALLOCATION_RATIO where 1=1 ";
		if (null!=years&&StringUtils.isNotEmpty(years)) {
			sql+=" and basis_year='"+years+"'";
		}
		if (null!=types && StringUtils.isNotEmpty(types)) {
			sql+=" and data_type='"+types+"'";
		}
		if (null!=scenarios && StringUtils.isNotEmpty(scenarios)) {
			sql+=" and scenario='"+scenarios+"'";
		}
		sql+=" order by basis_year,department,entity";
		return sql;
	}
	
	/**下載數據*/
	
	public Map<String,String>  downloadBudget(String y,String types,HttpServletRequest request, PageRequest pageRequest,String scenarios){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"分攤比例","AllocationRatio")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"分攤比例","AllocationRatio")+".xlsx");
			String sql="select version_date,department,department_name,entity,data_type,data_budyear,data_next1,data_next2,data_next3,data_next4 from CUX_BUDGET_ALLOCATION_RATIO where  basis_year='"+y+"' and scenario='"+scenarios+"' and data_type='"+types+"' ";
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(5).setCellValue(y);
			row.getCell(6).setCellValue("FY"+(year+1));
			row.getCell(7).setCellValue("FY"+(year+2));
			row.getCell(8).setCellValue("FY"+(year+3));
			row.getCell(9).setCellValue("FY"+(year+4));
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+=" order by basis_year,department,entity";
			List<Object[]> dataList = allocationRatioDao.findPageBySql(pageRequest, sql).getResult();
		
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 2;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 0; i < objects.length; i++) {
						if(i==10){
							break;
						}
						Cell cell = contentRow.createCell(i);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>4 && i<10) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = allocationRatioDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 0; i < objects.length; i++) {
								if(i==10){
									break;
								}
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>4 && i<10) {
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
