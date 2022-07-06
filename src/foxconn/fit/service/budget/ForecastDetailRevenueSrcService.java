package foxconn.fit.service.budget;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.budget.ForecastDetailRevenueSrcDao;
import foxconn.fit.entity.budget.ForecastDetailRevenueSrc;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			String filePath=realPath+"static"+File.separator+"download"+File.separator+"FIT_PBCS Dimension table.xlsx";
			InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"budget"+File.separator+"FIT_PBCS Dimension table.xlsx");
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
	public static String mapValString(Object o){
		if(null == o || o.toString().length()==0){
			return "";
		}
		return o.toString();
	}
}
