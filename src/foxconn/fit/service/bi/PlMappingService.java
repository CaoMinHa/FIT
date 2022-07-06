package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
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
import org.springframework.util.Assert;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PlMappingService {

    @Autowired
    private PoTableDao poTableDao;

    public String selectDataSql(String queryCondition, Locale locale, Model model,String type) {
        List<Map> map=poTableDao.listMapBySql("select COLUMN_NAME,COMMENTS from fit_po_table_columns where table_name='"+type+"' ORDER BY to_number(SERIAL)");
        List<String> column=new ArrayList<>();
        String sql = "select ";
        if(type.equals("epmebs.CUX_FIT_SBU_CODE")){
            sql+="SBU_CODE as code,";
        }else if(type.equals("epmebs.CUX_BI_COMPANY_CODE")){
            sql+="COMPANY_CODE  as code,";
        }
        for (Map m : map) {
            sql+=m.get("COLUMN_NAME").toString()+",";
            column.add(getByLocale(locale, m.get("COMMENTS").toString()));
        }
        sql=sql.substring(0,sql.length()-1)+" from "+type+" where 1=1 ";
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                }
            }
        }
        sql+="order by code";
        model.addAttribute("columns", column);
        return sql;
    }

    public List<Map> selectQuery(String type,Locale locale ){
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='"+type+"' AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<Map> list = poTableDao.listMapBySql(sql);
        for (Map map : list) {
            map.put("COMMENTS",getByLocale(locale,map.get("COMMENTS").toString()));
        }
        return list;
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

    public AjaxResult template(AjaxResult result,HttpServletRequest request ,String tableName) throws IOException {
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
        List<PoColumns> columns = poTableDao.listBySql("select * from fit_po_table_columns where table_name='"+tableName+"' ORDER BY to_number(SERIAL)",PoColumns.class);
        Sheet sheet;
        if(tableName.equals("epmebs.CUX_BI_COMPANY_CODE")){
            sheet = workBook.createSheet(getByLocale(locale,"Entity Code (FIT system)_Entity編碼（FIT體系）"));
        }else {
            //if(tableName.equals("epmebs.CUX_FIT_SBU_CODE"))
            sheet = workBook.createSheet(getByLocale(locale, "SBU Code_SBU代碼"));
        }
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            PoColumns poColumn = columns.get(i);
            String comments = poColumn.getComments();
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(getByLocale(locale,comments));
            cell.setCellStyle(titleStyle);
            sheet.setColumnWidth(i, comments.getBytes("GBK").length * 256 + 400);
        }
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download/三表映射表上傳模板.xlsx");
        OutputStream out = new FileOutputStream(outFile);
        workBook.write(out);
        workBook.close();
        out.flush();
        out.close();
        result.put("fileName", outFile.getName());
        return result;
    }

    public String uploadFile(Sheet sheet,AjaxResult result, Locale locale,String tableName) throws Exception {
        System.out.print("开始处理数据-------》");
        List<PoColumns> columns = poTableDao.listBySql("select * from fit_po_table_columns where table_name='"+tableName+"' ORDER BY to_number(SERIAL)",PoColumns.class);
        int COLUMN_NUM = columns.size();
        List<List<String>> dataList=new ArrayList<List<String>>();
        String code="";
        int n;
        for (Row row : sheet) {
            if (row.getRowNum() < 1) {
                Assert.notNull(row, getByLocale(locale, "Please use the downloaded template to import data_請使用所下載的模板導入數據！"));
                int columnNum = row.getPhysicalNumberOfCells();
                if(tableName.equals("epmebs.CUX_BI_COMPANY_CODE")){
                    if (columnNum < 4) {
                        result.put("flag", "fail");
                        result.put("msg", getByLocale(locale, "The number of columns cannot be less than 4_列數不能小於4"));
                        return result.getJson();
                    }
                }else if(tableName.equals("epmebs.CUX_FIT_SBU_CODE")){
                    if (columnNum < 2) {
                        result.put("flag", "fail");
                        result.put("msg", getByLocale(locale, "The number of columns cannot be less than 2_列數不能小於2"));
                        return result.getJson();
                    }
                }

                continue;
            }
                n = 0;
                List<String> data = new ArrayList<>(COLUMN_NUM);
                while (n < COLUMN_NUM) {
                    if(null==row.getCell(n)){
                        data.add("");
                    }else{
                        String value = ExcelUtil.getCellStringValue(row.getCell(n),row.getRowNum());
                        if (StringUtils.isNotEmpty(value)) {
                            value = value.replaceAll("'", "''");
                            data.add(value);
                        } else {
                            data.add("");
                        }
                        if(tableName.equals("epmebs.CUX_BI_COMPANY_CODE")&& n==2){
                            code+="'"+value+"',";
                        }else if(tableName.equals("epmebs.CUX_FIT_SBU_CODE") && n==1){
                            code+="'"+value+"',";
                        }
                    }
                    n++;
                }
                dataList.add(data);
            }
        System.out.print("数据集大小："+dataList.size());
        try {
            dataCheck(tableName,code);
            String s=saveRtData(dataList,columns,tableName);
            if(s.equals("S")){
                return result.getJson();
            }else{
                result.put("flag", "fail");
                result.put("msg", getByLocale(locale,s));
                return result.getJson();
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("flag", "fail");
            result.put("msg", getByLocale(locale,"fail to upload"+ExceptionUtil.getRootCauseMessage(e)+"_上傳失敗"+ExceptionUtil.getRootCauseMessage(e)));
            return result.getJson();
        }
    }

    private String saveRtData(List<List<String>> list ,List<PoColumns> columns,String tableName){
        System.out.print("处理数据插入表中");
        String message="S";
        try {
            String columnStr="insert into "+tableName+"(";
            for (PoColumns column : columns) {
                columnStr += column.getColumnName() + ",";
            }
            String sql="";
            for (List<String> val:list) {
                sql=columnStr.substring(0,columnStr.length()-1)+") values(";
                    for (int i=0;i<val.size();i++){
                        sql+="'"+val.get(i)+"',";
                    }
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.substring(0,sql.length()-1)+")").executeUpdate();
            }
        }catch (Exception e){
            message="Failed to save"+ExceptionUtil.getRootCauseMessage(e)+"_保存失敗！"+ExceptionUtil.getRootCauseMessage(e);
        }
        return message;
    }


    private void dataCheck(String tableName,String code){
            System.out.print("表："+tableName+"代碼："+code);
            String  deleteStr="delete from "+tableName+" where ";
            if(tableName.equals("epmebs.CUX_BI_COMPANY_CODE")){
                deleteStr+=" COMPANY_CODE in ("+code.substring(0,code.length()-1)+")";
            }else if(tableName.equals("epmebs.CUX_FIT_SBU_CODE")){
                deleteStr+=" SBU_CODE in ("+code.substring(0,code.length()-1)+")";
            }
            System.out.print("校验通过删除语句："+deleteStr);
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteStr).executeUpdate();
    }

    public String downloadFile(String queryCondition, String  tableName, HttpServletRequest request,PageRequest pageRequest) throws IOException {
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

        List<PoColumns> columns = poTableDao.listBySql("select * from fit_po_table_columns where table_name='"+tableName+"' ORDER BY to_number(SERIAL)",PoColumns.class);
        List<Integer> numberList = new ArrayList<Integer>();
        Sheet sheet = sxssfWorkbook.createSheet("補錄模板");
        sheet.createFreezePane(0, 1, 0, 1);
        Row titleRow = sheet.createRow(0);
        String sql = "select ";
        for (int i = 0; i < columns.size(); i++) {
            PoColumns poColumn = columns.get(i);
            String columnName = poColumn.getColumnName();
            String comments = getByLocale(locale,poColumn.getComments());
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
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                        }
                        whereSql += " and " + columnName + "='" + columnValue + "'";
                    }else{
                        whereSql+=" and "+columnName+" like '%"+columnValue+"%'";
                    }
                }
            }
            whereSql+= " order by PL_ID";
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
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + "綫下損益表内交模板.xlsx");
        OutputStream out = new FileOutputStream(outFile);
        sxssfWorkbook.write(out);
        sxssfWorkbook.close();
        out.flush();
        out.close();
        return outFile.getName();
    }

    public AjaxResult deleteData(AjaxResult ajaxResult,String no,String tableName) {
        try {
            String[] ids = no.split(",");
            String deleteSql = " delete from "+tableName+" where";
            if(tableName.equals("epmebs.CUX_FIT_SBU_CODE")){
                deleteSql+=" SBU_CODE in(";
            }else if(tableName.equals("epmebs.CUX_BI_COMPANY_CODE")){
                deleteSql+=" COMPANY_CODE in(";
            }
            for (String s : ids) {
                deleteSql+= "'" + s + "',";
            }
            deleteSql = deleteSql.substring(0,deleteSql.length()-1) + ")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
        } catch (Exception e) {
        ajaxResult.put("flag", "fail");
        ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
    return ajaxResult;
    }


}