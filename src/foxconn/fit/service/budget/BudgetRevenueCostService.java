package foxconn.fit.service.budget;

import foxconn.fit.dao.budget.BudgetDetailRevenueDao;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
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
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maggao
 * 銷貨收入&成本預算表(輸出表)
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BudgetRevenueCostService {

	@Autowired
	private BudgetDetailRevenueDao budgetDetailRevenueDao;

	@Autowired
	private InstrumentClassService instrumentClassService;


	/**頁面初始加載*/
	public Model index(Model model){
		List yearsList = budgetDetailRevenueDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
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
		String sqlVersion="select distinct version from (select distinct version from fit_budget_revenue_cost_v " +
				" where Year='FY"+String.valueOf(year).substring(2)+"' and version<>'V00' union all  " +
				"select distinct version from fit_forecast_revenue_cost_v " +
				" where Year='FY"+String.valueOf(year).substring(2)+"' and version<>'V00' ) order by version ";
		List<String> versionList=budgetDetailRevenueDao.listBySql(sqlVersion);
		return  versionList;
	}

	/**預算頁面查詢*/
	public Page<Object[]> budgetList(PageRequest pageRequest,String year, String version, String entity,String scenarios){
		String sql="select * from fit_budget_revenue_cost_v where 1=1";
		if(scenarios.equals("forecast")){
			sql="select * from fit_forecast_revenue_cost_v where 1=1";
		}
		if (!year.trim().isEmpty()) {
			sql+=" and YEAR='"+year+"'";
		}
		if (!version.trim().isEmpty()) {
			sql+=" and version='"+version+"'";
		}
		String tarList=instrumentClassService.getBudgetSBUStr();
		String sbu="select distinct substr(ALIAS,0,instr(ALIAS,'_')-1) ALIAS,','||PARENT||',' PARENT from fit_dimension where substr(ALIAS,0,instr(ALIAS,'_')-1) is not null and type='" + EnumDimensionType.Entity.getCode() +"' and PARENT in("+tarList+")";
		List<Map> sbuMap=budgetDetailRevenueDao.listMapBySql(sbu);
		sql+=instrumentClassService.querySbuSql(entity,sbuMap);
		sql+=" order by year,entity,make_Entity";
		Page<Object[]> page = budgetDetailRevenueDao.findPageBySql(pageRequest, sql);
		return page;
	}

	/**預算下載數據*/
	public Map<String,String>  download(String scenarios,String entitys,String y,String version,HttpServletRequest request,PageRequest pageRequest){
		Map<String,String> mapResult=new HashMap<>();
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String fileNmae="銷貨收入&成本預算表";
			String sql="select * from fit_budget_revenue_cost_v where YEAR='"+y+"'";
			if(scenarios.equals("forecast")){
				sql="select * from fit_forecast_revenue_cost_v where YEAR='"+y+"'";
				fileNmae="銷貨收入&成本预测表";
			}
			String filePath=realPath+"static"+File.separator+"download"+File.separator+fileNmae+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+fileNmae+".xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(13).setCellValue(y);
			if(!scenarios.equals("forecast")){
				row.getCell(14).setCellValue("FY"+(year+1));
				row.getCell(15).setCellValue("FY"+(year+2));
				row.getCell(16).setCellValue("FY"+(year+3));
				row.getCell(17).setCellValue("FY"+(year+4));
				row.getCell(18).setCellValue(y);
				row.getCell(19).setCellValue("FY"+(year+1));
				row.getCell(20).setCellValue("FY"+(year+2));
				row.getCell(21).setCellValue("FY"+(year+3));
				row.getCell(22).setCellValue("FY"+(year+4));
				row.getCell(23).setCellValue(y);
				row.getCell(24).setCellValue("FY"+(year+1));
				row.getCell(25).setCellValue("FY"+(year+2));
				row.getCell(26).setCellValue("FY"+(year+3));
				row.getCell(27).setCellValue("FY"+(year+4));
				row.getCell(28).setCellValue(y);
				row.getCell(29).setCellValue("FY"+(year+1));
				row.getCell(30).setCellValue("FY"+(year+2));
				row.getCell(31).setCellValue("FY"+(year+3));
				row.getCell(32).setCellValue("FY"+(year+4));
				row.getCell(33).setCellValue(y);
				row.getCell(45).setCellValue(y);
				row.getCell(57).setCellValue(y);
				row.getCell(69).setCellValue(y);
			}
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
			sql+="order by entity,product_series";
			List<Object[]> dataList = budgetDetailRevenueDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 3;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 2; i < objects.length; i++) {
						Cell cell = contentRow.createCell(i-2);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>15&&!text.equals("-")) {
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
							for (int i = 2; i < objects.length; i++) {
								Cell cell = contentRow.createCell(i-2);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>15&&!text.equals("-")) {
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
