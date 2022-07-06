package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.util.ExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RtActualEstimateService {

    @Autowired
    private PoTableDao poTableDao;

    public String selectDataSql(String queryCondition, PoTable poTable, Model model,String saleSorg) {
        List<PoColumns> columns = poTable.getColumns();
        List<PoColumns> columnsList=new ArrayList<>();
        for (PoColumns poColumns : columns) {
                poColumns.setComments(poColumns.getComments());
                columnsList.add(poColumns);
        }
        String sql = "select * from bidev.cux_actual_target_rev_v where 1=1 ";
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            params[params.length-1]="SALES_ORG="+saleSorg;
            for (int i=0;i<params.length;i++) {
                String param=params[i];
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)||"YEAR_MONTH".equals(columnName)) {
                    if(columnName.equals("YEAR_MONTHEND")){continue;}
                    if(columnName.equals("YEAR_MONTH")){
                        String columnValue2 = params[i+1].substring(params[i+1].indexOf("=") + 1).trim();
                        if(null==columnValue||"".equals(columnValue)){
                            columnValue2=columnValue2.replace("-","");
                            if(columnValue2.length()==5){
                                columnValue2=columnValue2.substring(0,4)+"0"+columnValue2.substring(4,5);
                            }
                            sql += " and " + columnName + " <= '"+columnValue2+"'";
                        }else if(null==columnValue2||"".equals(columnValue2)){
                            columnValue=columnValue.replace("-","");
                            if(columnValue.length()==5){
                                columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                            }
                            sql += " and " + columnName + " >= '"+columnValue+"'";
                        }else{
                            columnValue2=columnValue2.replace("-","");
                            if(columnValue2.length()==5){
                                columnValue2=columnValue2.substring(0,4)+"0"+columnValue2.substring(4,5);
                            }
                            columnValue=columnValue.replace("-","");
                            if(columnValue.length()==5){
                                columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                            }
                            sql += " and " + columnName + " between '"+columnValue+"' and '"+columnValue2+"'";
                        }
                    }else{
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
        }
        sql += " order by YEAR_MONTH desc,P_N,CUST_CODE desc";
        model.addAttribute("columns", columnsList);
        return sql;
    }

    public List<Map> selectQuery(HttpServletRequest request){
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='bidev.cux_actual_target_rev_v'  AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<Map> list = poTableDao.listMapBySql(sql);
        List<Map> a=new ArrayList<>();
        for (Map poColumns : list) {
            Map map=new HashMap();
            map.put("key",poColumns.get("COLUMN_NAME"));
            map.put("val",poColumns.get("COMMENTS"));
            a.add(map);
        }
        return a;
    }

    private String getByLocale(Locale locale,String value){
        if (StringUtils.isNotEmpty(value) && value.indexOf("_")>0) {
            if (locale!=null && "en_US".equals(locale.toString())) {
                return value.substring(0,value.lastIndexOf("_"));
            }else{
                return value.substring(value.lastIndexOf("_")+1,value.length());
            }
        }
        return value;
    }

    public String downloadFile(String queryCondition, PoTable poTable, HttpServletRequest request,PageRequest pageRequest,String saleSorg) throws IOException {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        XSSFWorkbook workBook = new XSSFWorkbook();
        String tableName="bidev.cux_actual_target_rev_v";
        XSSFCellStyle titleStyle = workBook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
        XSSFFont font = workBook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        font.setBold(true);
        titleStyle.setFont(font);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workBook);

        List<PoColumns> columns = poTable.getColumns();
        List<Integer> lockSerialList = new ArrayList<Integer>();
        String sql = "select ";
        Sheet sheet = sxssfWorkbook.createSheet(getByLocale(locale, poTable.getComments()));
        sheet.createFreezePane(0, 1, 0, 1);
        Row titleRow = sheet.createRow(0);
        List<Integer> numberList = new ArrayList<Integer>();
        for (int i = 0; i < columns.size(); i++) {
            PoColumns poColumn = columns.get(i);
            String columnName = poColumn.getColumnName();
            String comments = poColumn.getComments();
            if (poColumn.getLocked()) {
                lockSerialList.add(poColumn.getSerial());
            }
            if (poColumn.getDataType().equalsIgnoreCase("number")) {
                sql += "regexp_replace(to_char(" + columnName + ",'FM99999999999999.999999999'),'\\.$',''),";
                numberList.add(i);
            } else if (poColumn.getDataType().equalsIgnoreCase("date")) {
                sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
            } else {
                sql += columnName + ",";
            }

            Cell cell = titleRow.createCell(i);
            cell.setCellValue(comments);
            cell.setCellStyle(titleStyle);
            sheet.setColumnWidth(i, comments.getBytes("GBK").length * 256 + 400);
        }
        String whereSql = "";
        if (StringUtils.isNotEmpty(queryCondition)) {
            whereSql+=" where 1=1 ";
            String[] params = queryCondition.split("&");
            params[params.length-1]="SALES_ORG="+saleSorg;
            for (int i=0;i<params.length;i++) {
                String param=params[i];
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)||"YEAR_MONTH".equals(columnName)) {
                    if(columnName.equals("YEAR_MONTHEND")){continue;}
                    if(columnName.equals("YEAR_MONTH")){
                        String columnValue2 = params[i+1].substring(params[i+1].indexOf("=") + 1).trim();
                        if(null==columnValue||"".equals(columnValue)){
                            columnValue2=columnValue2.replace("-","");
                            if(columnValue2.length()==5){
                                columnValue2=columnValue2.substring(0,4)+"0"+columnValue2.substring(4,5);
                            }
                            whereSql += " and " + columnName + " <= '"+columnValue2+"'";
                        }else if(null==columnValue2||"".equals(columnValue2)){
                            columnValue=columnValue.replace("-","");
                            if(columnValue.length()==5){
                                columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                            }
                            whereSql += " and " + columnName + " >= '"+columnValue+"'";
                        }else{
                            columnValue2=columnValue2.replace("-","");
                            if(columnValue2.length()==5){
                                columnValue2=columnValue2.substring(0,4)+"0"+columnValue2.substring(4,5);
                            }
                            columnValue=columnValue.replace("-","");
                            if(columnValue.length()==5){
                                columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                            }
                            whereSql += " and " + columnName + " between '"+columnValue+"' and '"+columnValue2+"'";
                        }
                    }else{
                        whereSql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
            whereSql += " order by YEAR_MONTH desc";
        }
        sql = sql.substring(0, sql.length() - 1) + " from " + tableName + whereSql;
        System.out.println(sql);
        pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
        pageRequest.setPageNo(1);
        List<Object[]> dataList = poTableDao.findPageBySql(pageRequest, sql).getResult();
        if (CollectionUtils.isNotEmpty(dataList)) {
            int rowIndex = 1;
            for (Object[] objects : dataList) {
                Row contentRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < objects.length; i++) {
                    Cell cell = contentRow.createCell(i);
                    String text = (objects[i] != null ? objects[i].toString() : "");
                    if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
                        cell.setCellValue(Double.parseDouble(text));
                    } else {
                        cell.setCellValue(text);
                    }
                }
            }

            while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
                pageRequest.setPageNo(pageRequest.getPageNo() + 1);
                dataList = poTableDao.findPageBySql(pageRequest, sql).getResult();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Object[] objects : dataList) {
                        Row contentRow = sheet.createRow(rowIndex++);
                        for (int i = 0; i < objects.length; i++) {
                            Cell cell = contentRow.createCell(i);
                            String text = (objects[i] != null ? objects[i].toString() : "");
                            if (StringUtils.isNotEmpty(text) && numberList.contains(i)) {
                                cell.setCellValue(Double.parseDouble(text));
                            } else {
                                cell.setCellValue(text);
                            }
                        }
                    }
                }
            }
        }
        String fileName = getByLocale(locale,poTable.getComments());
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + fileName + ".xlsx");
        OutputStream out = new FileOutputStream(outFile);
        sxssfWorkbook.write(out);
        sxssfWorkbook.close();
        out.flush();
        out.close();
        return outFile.getName();
    }

}