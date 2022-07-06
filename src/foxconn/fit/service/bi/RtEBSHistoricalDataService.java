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
public class RtEBSHistoricalDataService {

    @Autowired
    private PoTableDao poTableDao;

    public String selectDataSql(String queryCondition, String columns, Locale locale, Model model) {
        String sql = "select "+columns+" from if_ebs_ar_revenue_dtl where 1=1 ";
        List<String> columnsList=poColumnsList(locale,columns);
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if(columnName.equals("YEAR_MONTH")){
                        columnValue=columnValue.replace("-","");
                        sql += " and YEAR='" + columnValue.substring(0,4) + "'";
                        if(columnValue.length()==5){
                            sql+=" and MONTH='0"+columnValue.substring(4,5)+"'";
                        }else {
                            sql+=" and MONTH='"+columnValue.substring(4,6)+"'";
                        }
                    }else{
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
        }
        sql += " order by YEAR||MONTH desc,P_N,CUST_CODE desc";
        model.addAttribute("columns", columnsList);
        return sql;
    }

    private  List<String> poColumnsList(Locale locale,String columns){
        String[] c= columns.split(",");
        columns="";
        for (String column : c) {
            columns+="'"+column+"',";
        }
        List<PoColumns> columnsList= poTableDao.listBySql("select * from fit_po_table_columns where TABLE_NAME in ('if_ebs_ar_revenue_dtl') and COLUMN_NAME in("+columns.substring(0,columns.length()-1)+") order by serial ",PoColumns.class);
        List<String> list=new ArrayList<>();
        for (PoColumns poColumns : columnsList) {
            list.add(getByLocale(locale, poColumns.getComments()));
        }
        return list;
    }
    public List<Map> selectQuery(HttpServletRequest request){
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='if_ebs_ar_revenue_dtl'  AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<Map> list = poTableDao.listMapBySql(sql);
        List<Map> a=new ArrayList<>();
        for (Map poColumns : list) {
            Map map=new HashMap();
            map.put("key",poColumns.get("COLUMN_NAME"));
            map.put("val",getByLocale(locale,poColumns.get("COMMENTS").toString()));
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

    public String downloadFile(String queryCondition, PoTable poTable, HttpServletRequest request,PageRequest pageRequest,String columns) throws IOException {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFCellStyle titleStyle = workBook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
        XSSFFont font = workBook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        font.setBold(true);
        titleStyle.setFont(font);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workBook);

        String[] c= columns.split(",");
        columns="";
        for (String column : c) {
            columns+="'"+column+"',";
        }
        List<PoColumns> columnsList= poTableDao.listBySql("select * from fit_po_table_columns where TABLE_NAME in ('if_ebs_ar_revenue_dtl') and COLUMN_NAME in("+columns.substring(0,columns.length()-1)+") order by serial ",PoColumns.class);
        List<Integer> lockSerialList = new ArrayList<Integer>();
        String sql = "select ";
        Sheet sheet = sxssfWorkbook.createSheet(getByLocale(locale, poTable.getComments()));
        sheet.createFreezePane(0, 1, 0, 1);
        Row titleRow = sheet.createRow(0);
        List<Integer> numberList = new ArrayList<Integer>();
        for (int i = 0; i < columnsList.size(); i++) {
            PoColumns poColumn = columnsList.get(i);
            String columnName = poColumn.getColumnName();
            String comments = getByLocale(locale,poColumn.getComments());
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
            for (String param : params) {
                String columnName = param.substring(0,param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=")+1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if(columnName.equals("YEAR_MONTH")){
                        columnValue=columnValue.replace("-","");
                        whereSql += " and YEAR='" + columnValue.substring(0,4) + "'";
                        if(columnValue.length()==5){
                            whereSql+=" and MONTH='0"+columnValue.substring(4,5)+"'";
                        }else {
                            whereSql+=" and MONTH='"+columnValue.substring(4,6)+"'";
                        }
                    }else{
                        whereSql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
            whereSql+=" order by YEAR||MONTH desc,P_N,CUST_CODE,SALE_ORDER,INVOICE_NO desc";
        }
        sql = sql.substring(0, sql.length() - 1) + " from " + poTable.getTableName() + whereSql;
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
                    if (StringUtils.isNotEmpty(text)&&text.matches("^-?\\d+(\\.\\d+)?$")) {
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
                        for (int i = 0; i < objects.length-1; i++) {
                            Cell cell = contentRow.createCell(i);
                            String text = (objects[i] != null ? objects[i].toString() : "");
                            if (StringUtils.isNotEmpty(text)&&text.matches("^-?\\d+(\\.\\d+)?$")) {
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