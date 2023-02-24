package foxconn.fit.service.budget;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.ForecastDetailRevenueSrcDao;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.budget.ForecastDetailRevenueSrc;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ForecastDetailRevenueSrcService extends BaseService<ForecastDetailRevenueSrc>{

	@Autowired
	private ForecastDetailRevenueSrcDao forecastDetailRevenueSrcDao;
	
	@Override
	public BaseDaoHibernate<ForecastDetailRevenueSrc> getDao() {
		return forecastDetailRevenueSrcDao;
	}

	public void deleteVersion(String entitys, String year, String scenarios) {
		String sbu="";
		for (String s : entitys.split(",")) {
			sbu+=s+"|";
		}
		sbu=sbu.substring(0,sbu.length()-1);
		Session session = forecastDetailRevenueSrcDao.getSessionFactory().getCurrentSession();
		String deleteSql="delete from FIT_FORECAST_DETAIL_REV_SRC where year='"+year+"' and scenarios='"+scenarios+"' and version='V00' and REGEXP_LIKE(ENTITY,'^("+sbu+")')";
		session.createSQLQuery(deleteSql).executeUpdate();
	}

	public Map<String,String> dimension(HttpServletRequest request) {
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+"FIT_Hyperion Dimension table.xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+"FIT_Hyperion Dimension table.xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);

			/**組織维度表*/
			Sheet sheet = workBook.getSheetAt(1);
			String sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"' and PARENT <> 'FOET' and DIMENSION not in('ABS_A084002')";
			this.selectDimension(sql,sheet);

			/**Entity-for FOIT*/
			sheet = workBook.getSheetAt(2);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Entity.getCode()+"' and PARENT = 'FOET' and DIMENSION not in('ABS_A084002')";
			this.selectDimension(sql,sheet);

			/**次產業*/
			sheet = workBook.getSheetAt(3);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Segment.getCode()+"' and PARENT like 'SE_%' or DIMENSION='S00' ";
			this.selectDimension(sql,sheet);

			/**3+3*/
			sheet = workBook.getSheetAt(4);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='Bak2' and PARENT in('bak201','bak20199') and DIMENSION not in('bak20199')";
			this.selectDimension(sql,sheet);

			/**三大技術*/
			sheet = workBook.getSheetAt(5);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='Project' and PARENT='P_FIT3+3'";
			this.selectDimension(sql,sheet);

			/**產品系列*/
			sheet = workBook.getSheetAt(6);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' ";
			this.selectDimension(sql,sheet);

			/**Product series for FOIT*/
			sheet = workBook.getSheetAt(7);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Product.getCode()+"' and PARENT in('7EB','7EE','7ED','7EA','7EG','7ER','7RD','7RE','7RA','7RC','7RF','7RB','7SB','7SC','7SA','7PF','7PC','7PB','7PG','7PD','7PA','7EU','7RG','7ET')";
			this.selectDimension(sql,sheet);

			/**產品料号维度表*/
			sheet = workBook.getSheetAt(8);
			sql="SELECT distinct SBU,PRODUCT_TYPE_DESC,PRODUCT_FAMILY_DESC,PRODUCT_SERIES_DESC,ITEM_CODE FROM epmods.cux_inv_sbu_item_info_mv t";
			this.selectProduct(sql,sheet);

			/**Product number for FOIT*/
			sheet = workBook.getSheetAt(9);
			sql="SELECT distinct SBU,PRODUCT_TYPE_DESC,PRODUCT_FAMILY_DESC,PRODUCT_SERIES_DESC,ITEM_CODE FROM epmods.cux_inv_sbu_item_info_mv t  WHERE t.sbu = 'FOET' ";
			this.selectProduct(sql,sheet);

			/**最終客戶*/
			sheet = workBook.getSheetAt(10);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Combine.getCode()+"' and PARENT in('C_End Customer') ";
			this.selectDimension(sql,sheet);

			/**賬款客戶*/
			sheet = workBook.getSheetAt(11);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='"+EnumDimensionType.Customer.getCode()+"'  and PARENT in('Customer_Total','HT_ICP') and  DIMENSION <> 'HT_ICP' ";
			this.selectDimension(sql,sheet);

			/**交易類型*/
			sheet = workBook.getSheetAt(12);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='View' and PARENT in('Int000') and DIMENSION not in('Int005','Int006')";
			this.selectDimension(sql,sheet);

			/**交易貨幣*/
			sheet = workBook.getSheetAt(13);
			sql="select distinct DIMENSION,ALIAS from fit_dimension where type='Currency' and PARENT ='O_Currency'";
			this.selectDimension(sql,sheet);

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
	public static String mapValString(Object o){
		if(null == o || o.toString().length()==0){
			return "";
		}
		return o.toString();
	}

	private void selectDimension(String sql,Sheet sheet){
		List<Map> list=forecastDetailRevenueSrcDao.listMapBySql(sql);
		for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow(i+1);
			Map map=list.get(i);
			Cell cell = row.createCell(0);
			cell.setCellValue(mapValString(map.get("DIMENSION")));
			if(null!=map.get("ALIAS")) {
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(mapValString(map.get("ALIAS").toString()));
			}
		}
	}

	private void selectProduct(String sql,Sheet sheet){
		PageRequest pageRequest=new PageRequest();
		pageRequest.setPageSize(10000);
		pageRequest.setPageNo(1);
		pageRequest.setOrderBy("SBU,PRODUCT_TYPE_DESC,PRODUCT_FAMILY_DESC,PRODUCT_SERIES_DESC,ITEM_CODE");
		List<Object[]> dataList = forecastDetailRevenueSrcDao.findPageBySql(pageRequest, sql).getResult();
		int col=1;
		for (Object[] objects : dataList) {
			Row row = sheet.createRow(col++);
			Cell cell = row.createCell(0);
			cell.setCellValue(objects[0].toString());
			Cell cell1 = row.createCell(1);
			cell1.setCellValue(objects[1].toString());
			Cell cell2 = row.createCell(2);
			cell2.setCellValue(objects[2].toString());
			Cell cell3 = row.createCell(3);
			cell3.setCellValue(objects[3].toString());
			Cell cell4 = row.createCell(4);
			cell4.setCellValue(objects[4].toString());
		}
		while (dataList != null && dataList.size() == 10000) {
			pageRequest.setPageNo(pageRequest.getPageNo() + 1);
			dataList = forecastDetailRevenueSrcDao.findPageBySql(pageRequest, sql).getResult();
			if (CollectionUtils.isNotEmpty(dataList)) {
				for (Object[] objects : dataList) {
					Row row = sheet.createRow(col++);
					Cell cell = row.createCell(0);
					cell.setCellValue(objects[0].toString());
					Cell cell1 = row.createCell(1);
					cell1.setCellValue(objects[1].toString());
					Cell cell2 = row.createCell(2);
					cell2.setCellValue(objects[2].toString());
					Cell cell3 = row.createCell(3);
					cell3.setCellValue(objects[3].toString());
					Cell cell4 = row.createCell(4);
					cell4.setCellValue(objects[4].toString());
				}
			}
		}
	}


	public Map<String,String> dimensionOld(HttpServletRequest request) {
		Map<String,String> mapResult=new HashMap<>();
		mapResult.put("result","Y");
		try {
			String realPath = request.getRealPath("");
			String filePath=realPath+"static"+File.separator+"download"+File.separator+"FIT Hyperion Dimension table.xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+"FIT Hyperion Dimension table.xlsx");
			XSSFWorkbook workBook = new XSSFWorkbook(ins);
			Sheet sheet = workBook.getSheetAt(5);
			Sheet sheetFoet = workBook.getSheetAt(6);
//			sheet.shiftRows(2, sheet.getLastRowNum(), -1);
//			sheetFoet.shiftRows(2, sheetFoet.getLastRowNum(), -1);
			String sql="SELECT SBU,PRODUCT_TYPE_DESC,PRODUCT_FAMILY_DESC,PRODUCT_SERIES_DESC,ITEM_CODE FROM epmods.cux_inv_sbu_item_info_mv t";
			List<Map> list=forecastDetailRevenueSrcDao.listMapBySql(sql);
			sql="SELECT SBU,PRODUCT_TYPE_DESC,PRODUCT_FAMILY_DESC,PRODUCT_SERIES_DESC,ITEM_CODE FROM epmods.cux_inv_sbu_item_info_mv t  WHERE t.sbu = 'FOET' ";
			List<Map> listFoet=forecastDetailRevenueSrcDao.listMapBySql(sql);
			for (int i = 0; i < list.size(); i++) {
				Row row = sheet.createRow(i+1);
				Map map=list.get(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(mapValString(map.get("SBU")));
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(mapValString(map.get("PRODUCT_TYPE_DESC").toString()));
				Cell cell2 = row.createCell(2);
				cell2.setCellValue(mapValString(map.get("PRODUCT_FAMILY_DESC").toString()));
				Cell cell3 = row.createCell(3);
				cell3.setCellValue(mapValString(map.get("PRODUCT_SERIES_DESC").toString()));
				Cell cell4 = row.createCell(4);
				cell4.setCellValue(mapValString(map.get("ITEM_CODE").toString()));
			}
			for (int i = 0; i < listFoet.size(); i++) {
				Row row = sheetFoet.createRow(i+1);
				Map map=list.get(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(mapValString(map.get("SBU")));
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(mapValString(map.get("PRODUCT_TYPE_DESC").toString()));
				Cell cell2 = row.createCell(2);
				cell2.setCellValue(mapValString(map.get("PRODUCT_FAMILY_DESC").toString()));
				Cell cell3 = row.createCell(3);
				cell3.setCellValue(mapValString(map.get("PRODUCT_SERIES_DESC").toString()));
				Cell cell4 = row.createCell(4);
				cell4.setCellValue(mapValString(map.get("ITEM_CODE").toString()));
			}
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
}
