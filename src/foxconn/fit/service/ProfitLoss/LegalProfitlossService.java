package foxconn.fit.service.ProfitLoss;
import foxconn.fit.dao.ProfitLoss.DataSourceDao;
import foxconn.fit.dao.base.BaseDaoHibernate;
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
import foxconn.fit.advice.Log;
/**
 * @author cxj
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LegalProfitlossService extends BaseService<DataSource> {

	@Autowired
	private DataSourceDao legalProfitlossDao;
	@Autowired
	private InstrumentClassService instrumentClassService;
	@Override
	public BaseDaoHibernate<DataSource> getDao() {
		return legalProfitlossDao;
	}

	/**頁面初始加載*/
	public Model index(Model model){
		List<String> yearsList = legalProfitlossDao.listBySql("select distinct dimension from FIT_DIMENSION where type='"+EnumDimensionType.Years.getCode()+"' order by dimension");
		Calendar calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		model.addAttribute("yearVal", "FY"+String.valueOf(year).substring(2));
		model.addAttribute("yearsList", yearsList);
		return model;
	}
	
	/**獲取表中entity值*/
	public List<String> entityVal(){
		/*String sqlEntity="select distinct entity from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL'";
		List<String> entityList=legalProfitlossDao.listBySql(sqlEntity);
		return  entityList;*/
		String legalCode=SecurityUtils.getLegalCode();
		String targetList="";
		//System.out.println(legalList);
		if (StringUtils.isNotEmpty(legalCode)) {
			for (String string : legalCode.split(",")) {
				List<String> entity = legalProfitlossDao.listBySql("select distinct entity_name from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL' and entity='"+string+"'");
				targetList+=entity.get(0)+",";
			}
		}
		targetList=targetList.substring(0, targetList.length()-1);
		
		return  Arrays.asList(targetList.split(","));
	}
	/**獲取表中entity值*/
	public List<String> entityVal1(){
		String sqlEntity="select distinct entity from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL'";
		List<String> entityList=legalProfitlossDao.listBySql(sqlEntity);
		return  entityList;

	}
	/**頁面查詢*/
	public String viewList(String years,String scenarios,String entitys){
		
		String sql="select  *  from CUX_BUDGET_HP_PL where 1=1 ";
			if (null!=years&&StringUtils.isNotEmpty(years)) {
			sql+=" and basis_year='"+years+"'";
		}
		if (null!=scenarios && StringUtils.isNotEmpty(scenarios)) {
			sql+=" and scenario='"+scenarios+"'";
		}
		
		if (null!=entitys && StringUtils.isNotEmpty(entitys) && !entitys.equals("") ) {
			sql+=" and entity=(select distinct entity from CUX_BUDGET_HP_PL where entity_name='"+entitys+"')";
			
		}else {
			String tarList=instrumentClassService.getBudgetlegal();
			sql+=" and entity in ("+tarList+")";
			}
		sql+=" order by basis_year,entity,version_d";
		return sql;
	}
	
	/**頁面查詢*/
	public String viewList1(String entitys){
		
		String sql="select  distinct entity,entity_name from CUX_BUDGET_EXPENSE_HP_USD where data_type='PL' ";
		if (null!=entitys && StringUtils.isNotEmpty(entitys) && !entitys.equals("") ) {
			sql+=" and entity='"+entitys+"'";
			
		}
		sql+=" order by entity";
		return sql;
	}
	
	/**下載數據*/
	
	public Map<String,String>  downloadBudget(String entitys,String y,String versions,HttpServletRequest request, PageRequest pageRequest,String scenarios){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"損益表","ProfitLoss")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"損益表","ProfitLoss")+".xlsx");
			String sql="select * from CUX_BUDGET_HP_PL_V where account='APLP01' and basis_year='"+y+"' and scenario='"+scenarios+"' and version_d='"+versions+"' and entity ='"+entitys+"' ";
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(1).setCellValue(y);
			row.getCell(39).setCellValue("FY"+(year+1));
			row.getCell(40).setCellValue("FY"+(year+2));
			row.getCell(41).setCellValue("FY"+(year+3));
			row.getCell(42).setCellValue("FY"+(year+4));
			pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			pageRequest.setPageNo(1);
			sql+=" order by basis_year,entity";
			List<Object[]> dataList = legalProfitlossDao.findPageBySql(pageRequest, sql).getResult();
		
			if (CollectionUtils.isNotEmpty(dataList)) {
				int rowIndex = 4;
				for (Object[] objects : dataList) {
					Row contentRow = sheet.createRow(rowIndex++);
					for (int i = 1; i < 52; i++) {
						if(i==52){
							break;
						}
						Cell cell = contentRow.createCell(i);
						String text = (objects[i] != null ? objects[i].toString() : "");
						if (StringUtils.isNotEmpty(text) && i>0 && i<52) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							cell.setCellValue(text);
						}
					}
				}

				while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
					pageRequest.setPageNo(pageRequest.getPageNo() + 1);
					dataList = legalProfitlossDao.findPageBySql(pageRequest, sql).getResult();
					if (CollectionUtils.isNotEmpty(dataList)) {
						for (Object[] objects : dataList) {
							Row contentRow = sheet.createRow(rowIndex++);
							for (int i = 1; i < 52; i++) {
								if(i==52){
									break;
								}
								Cell cell = contentRow.createCell(i);
								String text = (objects[i] != null ? objects[i].toString() : "");
								if (StringUtils.isNotEmpty(text) && i>0 && i<52) {
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
	
	/**下載數據*/
	
	public Map<String,String>  downloadBudget(String y,String id,HttpServletRequest request){
		Map<String,String> mapResult=new HashMap<>();
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			mapResult.put("result","Y");
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"損益表","ProfitLoss")+".xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"investment"+File.separator+instrumentClassService.getLanguage(locale,"損益表","ProfitLoss")+".xlsx");
			String sql="select * from CUX_BUDGET_HP_PL_V where basis_year=(select basis_year from CUX_BUDGET_HP_PL where id='"+id+"') and scenario=(select scenario from CUX_BUDGET_HP_PL where id='"+id+"') and version_d=(select version_d from CUX_BUDGET_HP_PL where id='"+id+"') and entity =(select entity from CUX_BUDGET_HP_PL where id='"+id+"')  ";
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(0);
			Row row =sheet.getRow(0);
			int year=Integer.parseInt(y.substring(2));
			row.getCell(1).setCellValue(y);
			row.getCell(40).setCellValue("FY"+(year+1));
			row.getCell(43).setCellValue("FY"+(year+2));
			row.getCell(46).setCellValue("FY"+(year+3));
			row.getCell(49).setCellValue("FY"+(year+4));
			//pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
			//pageRequest.setPageNo(1);
			/*String sql5= sql + " and account='APLP01' order by basis_year,entity";
			String sql6= sql + " and account='APL01' order by basis_year,entity";
			
			Row row5 =sheet.getRow(5);
		    this.selectresult(sql5,workBook.getSheetAt(0),row5); 
		    Row row6 =sheet.getRow(6);
		    this.selectresult(sql6,workBook.getSheetAt(0),row6); */
			String sqlentity="select  *  from CUX_BUDGET_HP_PL where id='"+id+"'";
			this.selectentity(sqlentity,workBook.getSheetAt(0),row);  
			
			
			String sql5=sql+" and account='APLP01' order by basis_year,entity";
			Row row5 =sheet.getRow(5);
			this.selectresult(sql5,workBook.getSheetAt(0),row5);  

			String sql6=sql+" and account='APL01' order by basis_year,entity";
			Row row6 =sheet.getRow(6);
			this.selectresult(sql6,workBook.getSheetAt(0),row6);  
			
			String sql7=sql+" and account='APL02' order by basis_year,entity";
			Row row7 =sheet.getRow(7);
			this.selectresult(sql7,workBook.getSheetAt(0),row7);  

			String sql8=sql+" and account='APL03' order by basis_year,entity";
			Row row8 =sheet.getRow(8);
			this.selectresult(sql8,workBook.getSheetAt(0),row8);  

			String sql9=sql+" and account='APLP02' order by basis_year,entity";
			Row row9 =sheet.getRow(9);
			this.selectresult(sql9,workBook.getSheetAt(0),row9);  

					    
			String sql10=sql+" and account='APL04' order by basis_year,entity";
			Row row10=sheet.getRow(10);
			this.selectresult(sql10,workBook.getSheetAt(0),row10);  

			String sql11=sql+" and account='APL04_01' order by basis_year,entity";
			Row row11=sheet.getRow(11);
		    this.selectresult(sql11,workBook.getSheetAt(0),row11);  


			String sql12=sql+" and account='APL04_02' order by basis_year,entity";
			Row row12=sheet.getRow(12);
			this.selectresult(sql12,workBook.getSheetAt(0),row12);  

			String sql13=sql+" and account='APL04_03' order by basis_year,entity";
			Row row13=sheet.getRow(13);
		    this.selectresult(sql13,workBook.getSheetAt(0),row13);  

			String sql14=sql+" and account='APL05' order by basis_year,entity";
			Row row14=sheet.getRow(14);
		    this.selectresult(sql14,workBook.getSheetAt(0),row14);  

		    String sql15=sql+" and account='APL06' order by basis_year,entity";
		    Row row15=sheet.getRow(15);
		    this.selectresult(sql15,workBook.getSheetAt(0),row15);  

		    String sql16=sql+" and account='APL07' order by basis_year,entity";
		    Row row16=sheet.getRow(16);
		    this.selectresult(sql16,workBook.getSheetAt(0),row16);  

		    String sql17=sql+" and account='APL12' order by basis_year,entity";
		    Row row17=sheet.getRow(17);
		    this.selectresult(sql17,workBook.getSheetAt(0),row17);  

		    String sql18=sql+" and account='APL08' order by basis_year,entity";
		    Row row18=sheet.getRow(18);
		    this.selectresult(sql18,workBook.getSheetAt(0),row18);  

		    String sql19=sql+" and account='APL09' order by basis_year,entity";
		    Row row19=sheet.getRow(19);
		    this.selectresult(sql19,workBook.getSheetAt(0),row19);  

		    String sql20=sql+" and account='APL10' order by basis_year,entity";
		    Row row20=sheet.getRow(20);
		    this.selectresult(sql20,workBook.getSheetAt(0),row20);  

		    String sql21=sql+" and account='APL11' order by basis_year,entity";
		    Row row21=sheet.getRow(21);
		    this.selectresult(sql21,workBook.getSheetAt(0),row21);  

		    String sql22=sql+" and account='APL19' order by basis_year,entity";
		    Row row22=sheet.getRow(22);
		    this.selectresult(sql22,workBook.getSheetAt(0),row22);  		    

		    String sql23=sql+" and account='APLP03' order by basis_year,entity";
			Row row23=sheet.getRow(23);
		    this.selectresult(sql23,workBook.getSheetAt(0),row23);  

		    String sql24=sql+" and account='APLP04' order by basis_year,entity";
		    Row row24=sheet.getRow(24);
		    this.selectresult(sql24,workBook.getSheetAt(0),row24);  

		    String sql25=sql+" and account='APL13' order by basis_year,entity";
		    Row row25=sheet.getRow(25);
		    this.selectresult(sql25,workBook.getSheetAt(0),row25);  

		    String sql26=sql+" and account='APL14' order by basis_year,entity";
		    Row row26=sheet.getRow(26);
		    this.selectresult(sql26,workBook.getSheetAt(0),row26);  

		    String sql27=sql+" and account='APL15' order by basis_year,entity";
		    Row row27=sheet.getRow(27);
		    this.selectresult(sql27,workBook.getSheetAt(0),row27);  

		    String sql28=sql+" and account='APL16' order by basis_year,entity";
		    Row row28=sheet.getRow(28);
		    this.selectresult(sql28,workBook.getSheetAt(0),row28);  

		    String sql29=sql+" and account='APL17' order by basis_year,entity";
		    Row row29=sheet.getRow(29);
		    this.selectresult(sql29,workBook.getSheetAt(0),row29);  

		    String sql30=sql+" and account='APLP05' order by basis_year,entity";
		    Row row30=sheet.getRow(30);
		    this.selectresult(sql30,workBook.getSheetAt(0),row30);  

		    String sql31=sql+" and account='APLP06' order by basis_year,entity";
		    Row row31=sheet.getRow(31);
		    System.out.println(sql31);
		    this.selectresult(sql31,workBook.getSheetAt(0),row31);  

		    String sql32=sql+" and account='APL20' order by basis_year,entity";
		    Row row32=sheet.getRow(32);
		    this.selectresult(sql32,workBook.getSheetAt(0),row32);  

		    String sql33=sql+" and account='APL21' order by basis_year,entity";
		    Row row33=sheet.getRow(33);
		    this.selectresult(sql33,workBook.getSheetAt(0),row33);  

		    String sql34=sql+" and account='APL22' order by basis_year,entity";
		    Row row34=sheet.getRow(34);
		    this.selectresult(sql34,workBook.getSheetAt(0),row34);  

		    String sql35=sql+" and account='APL23' order by basis_year,entity";
		    Row row35=sheet.getRow(35);
		    this.selectresult(sql35,workBook.getSheetAt(0),row35);  


		    String sql36=sql+" and account='APL24' order by basis_year,entity";
		    Row row36=sheet.getRow(36);
		    this.selectresult(sql36,workBook.getSheetAt(0),row36);  

		    String sql37=sql+" and account='APLP07' order by basis_year,entity";
		    Row row37=sheet.getRow(37);
		    this.selectresult(sql37,workBook.getSheetAt(0),row37);  

		    String sql38=sql+" and account='APLP08' order by basis_year,entity";
		    Row row38=sheet.getRow(38);
		    this.selectresult(sql38,workBook.getSheetAt(0),row38);  

		    String sql39=sql+" and account='APLP09' order by basis_year,entity";
		    Row row39=sheet.getRow(39);
		    this.selectresult(sql39,workBook.getSheetAt(0),row39);  

		    String sql40=sql+" and account='APLP10' order by basis_year,entity";
		    Row row40=sheet.getRow(40);
		    this.selectresult(sql40,workBook.getSheetAt(0),row40);  

		    String sql41=sql+" and account='APLP11' order by basis_year,entity";
		    Row row41=sheet.getRow(41);
		    this.selectresult(sql41,workBook.getSheetAt(0),row41);  			

			
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
	private void selectentity(String sql,Sheet sheet,Row row){
		List<Map> list=legalProfitlossDao.listMapBySql(sql);
		for (int i = 0; i < list.size(); i++) {
			Map map=list.get(i);
			row.getCell(0).setCellValue("公司："+instrumentClassService.mapValString(map.get("ENTITY"))+"(公司名稱："+instrumentClassService.mapValString(map.get("ENTITY_NAME"))+")  版本："+instrumentClassService.mapValString(map.get("VERSION_D")));
		}
	}
	private void selectresult(String sql,Sheet sheet,Row row){
		List<Map> list=legalProfitlossDao.listMapBySql(sql);
		for (int i = 0; i < list.size(); i++) {
			Map map=list.get(i);
			
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_JAN")))){
				row.getCell(1).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_JAN"))));
			} else {
				row.getCell(1).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_JAN")))){
				row.getCell(2).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_JAN"))));
			} else {
				row.getCell(2).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_JAN")))){
				row.getCell(3).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_JAN"))));
			} else {
				row.getCell(3).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_FEB")))){
				row.getCell(4).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_FEB"))));
			} else {
				row.getCell(4).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_FEB")))){
				row.getCell(5).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_FEB"))));
			} else {
				row.getCell(5).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_FEB")))){
				row.getCell(6).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_FEB"))));
			} else {
				row.getCell(6).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_MAR")))){
				row.getCell(7).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_MAR"))));
			} else {
				row.getCell(7).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_MAR")))){
				row.getCell(8).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_MAR"))));
			} else {
				row.getCell(8).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_MAR")))){
				row.getCell(9).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_MAR"))));
			} else {
				row.getCell(9).setCellValue(Double.parseDouble("0"));
			}
			
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_APR")))){
				row.getCell(10).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_APR"))));
			} else {
				row.getCell(10).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_APR")))){
				row.getCell(11).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_APR"))));
			} else {
				row.getCell(11).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_APR")))){
				row.getCell(12).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_APR"))));
			} else {
				row.getCell(12).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_MAY")))){
				row.getCell(13).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_MAY"))));
			} else {
				row.getCell(13).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_MAY")))){
				row.getCell(14).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_MAY"))));
			} else {
				row.getCell(14).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_MAY")))){
				row.getCell(15).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_MAY"))));
			} else {
				row.getCell(15).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_JUN")))){
				row.getCell(16).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_JUN"))));
			} else {
				row.getCell(16).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_JUN")))){
				row.getCell(17).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_JUN"))));
			} else {
				row.getCell(17).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_JUN")))){
				row.getCell(18).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_JUN"))));
			} else {
				row.getCell(18).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_JUL")))){
				row.getCell(19).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_JUL"))));
			} else {
				row.getCell(19).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_JUL")))){
				row.getCell(20).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_JUL"))));
			} else {
				row.getCell(20).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_JUL")))){
				row.getCell(21).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_JUL"))));
			} else {
				row.getCell(21).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_AUG")))){
				row.getCell(22).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_AUG"))));
			} else {
				row.getCell(22).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_AUG")))){
				row.getCell(23).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_AUG"))));
			} else {
				row.getCell(23).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_AUG")))){
				row.getCell(24).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_AUG"))));
			} else {
				row.getCell(24).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_SEP")))){
				row.getCell(25).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_SEP"))));
			} else {
				row.getCell(25).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_SEP")))){
				row.getCell(26).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_SEP"))));
			} else {
				row.getCell(26).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_SEP")))){
				row.getCell(27).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_SEP"))));
			} else {
				row.getCell(27).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_OCT")))){
				row.getCell(28).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_OCT"))));
			} else {
				row.getCell(28).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_OCT")))){
				row.getCell(29).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_OCT"))));
			} else {
				row.getCell(29).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_OCT")))){
				row.getCell(30).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_OCT"))));
			} else {
				row.getCell(30).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_NOV")))){
				row.getCell(31).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_NOV"))));
			} else {
				row.getCell(31).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_NOV")))){
				row.getCell(32).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_NOV"))));
			} else {
				row.getCell(32).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_NOV")))){
				row.getCell(33).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_NOV"))));
			} else {
				row.getCell(33).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_DEC")))){
				row.getCell(34).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_DEC"))));
			} else {
				row.getCell(34).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_DEC")))){
				row.getCell(35).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_DEC"))));
			} else {
				row.getCell(35).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_DEC")))){
				row.getCell(36).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_DEC"))));
			} else {
				row.getCell(36).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_YEARTOTAL")))){
				row.getCell(37).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_YEARTOTAL"))));
			} else {
				row.getCell(37).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_YEARTOTAL")))){
				row.getCell(38).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_YEARTOTAL"))));
			} else {
				row.getCell(38).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_YEARTOTAL")))){
				row.getCell(39).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_YEARTOTAL"))));
			} else {
				row.getCell(39).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_NEXT1")))){
				row.getCell(40).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_NEXT1"))));
			} else {
				row.getCell(40).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_NEXT1")))){
				row.getCell(41).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_NEXT1"))));
			} else {
				row.getCell(41).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_NEXT1")))){
				row.getCell(42).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_NEXT1"))));
			} else {
				row.getCell(42).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_NEXT2")))){
				row.getCell(43).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_NEXT2"))));
			} else {
				row.getCell(43).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_NEXT2")))){
				row.getCell(44).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_NEXT2"))));
			} else {
				row.getCell(44).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_NEXT2")))){
				row.getCell(45).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_NEXT2"))));
			} else {
				row.getCell(45).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_NEXT3")))){
				row.getCell(46).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_NEXT3"))));
			} else {
				row.getCell(46).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_NEXT3")))){
				row.getCell(47).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_NEXT3"))));
			} else {
				row.getCell(47).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_NEXT3")))){
				row.getCell(48).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_NEXT3"))));
			} else {
				row.getCell(48).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL1_NEXT4")))){
				row.getCell(49).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL1_NEXT4"))));
			} else {
				row.getCell(49).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL2_NEXT4")))){
				row.getCell(50).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL2_NEXT4"))));
			} else {
				row.getCell(50).setCellValue(Double.parseDouble("0"));
			}
			if (StringUtils.isNotEmpty(instrumentClassService.mapValString(map.get("PL3_NEXT4")))){
				row.getCell(51).setCellValue(Double.parseDouble(instrumentClassService.mapValString(map.get("PL3_NEXT4"))));
			} else {
				row.getCell(51).setCellValue(Double.parseDouble("0"));
			}
		}
	}		
			
			
			
		
}
