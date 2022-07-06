package foxconn.fit.service.bi;

import com.monitorjbl.xlsx.StreamingReader;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PmrCommonService {
    @Autowired
    private PoTableDao poTableDao;
    /**
    根據條件拼裝sql語句
     **/
    public String selectDataSql(String queryCondition, Locale locale, Model model, PoTable poTable) {
        List<PoColumns> columns = poTable.getColumns();
        List<String> list = new ArrayList<>();
        String sql = "select ";
        for (PoColumns poColumns : columns) {
           list.add(getByLocale(locale, poColumns.getComments()));
//            if(poColumns.getDataType().equalsIgnoreCase("NUMBER")){
//                sql+="to_char("+poColumns.getColumnName()+",'FM999,999,999,999,999,999.099999999999'),";
//            }else
            if(poColumns.getDataType().equalsIgnoreCase("DATE")){
                sql+="to_char("+poColumns.getColumnName()+",'yyyy/mm/dd hh24:mi:ss'),";
            }else{
                sql+=poColumns.getColumnName()+",";
            }
        }
        sql= sql.substring(0,sql.length()-1)+" from "+poTable.getTableName()+" where 1=1 ";
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if(columnName.equals("YEARS")){
                        columnValue=columnValue.replace("-","");
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                        }
                        sql += " and YEARS||PERIOD='"+columnValue+"'";
                    }else{
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
        }
        sql += " order by CREATE_DATE desc";
        model.addAttribute("columns", list);
        return sql;
    }

    /**
     * 查詢字段
     */
    public List<Map> selectQuery(Locale locale, String tableName){
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='"+tableName+"' AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<Map> list = poTableDao.listMapBySql(sql);
        List<Map> listMap=new ArrayList<>();
        for (Map poColumns : list) {
            Map map=new HashMap();
            map.put("key",poColumns.get("COLUMN_NAME"));
            map.put("val",getByLocale(locale, poColumns.get("COMMENTS").toString()));
            listMap.add(map);
        }
        return listMap;
    }

    /**
     * 下載模板
     */
    public File template(XSSFWorkbook workBook, PoTable poTable, HttpServletRequest request ) throws UnsupportedEncodingException {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        XSSFCellStyle titleStyle = workBook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
        XSSFFont font = workBook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        font.setBold(true);
        titleStyle.setFont(font);
        List<PoColumns> columns = poTable.getColumns();
        Sheet sheet = workBook.createSheet(getByLocale(locale, poTable.getComments()));
        sheet.createFreezePane(0, 3, 0, 3);
        int rowIndex = 0;
        Row row = sheet.createRow(rowIndex++);
        Row row1=sheet.createRow(rowIndex++);
        Row row2=sheet.createRow(rowIndex++);
        for (int i = 0; i < columns.size()-2; i++) {
            String comments =  columns.get(i).getComments().substring(0,columns.get(i).getComments().lastIndexOf("_"));
            String comments1 =  columns.get(i).getComments().substring(columns.get(i).getComments().lastIndexOf("_")+1);
            Cell cell = row.createCell(i);
            cell.setCellValue(comments);
            cell.setCellStyle(titleStyle);
            Cell cell1 = row1.createCell(i);
            cell1.setCellValue(comments1);
            cell1.setCellStyle(titleStyle);
            Cell cell2 = row2.createCell(i);
            cell2.setCellValue(columns.get(i).getExamples());
            cell2.setCellStyle(titleStyle);
            sheet.setColumnWidth(i, comments.getBytes("GBK").length * 256 + 400);
        }
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download/"+getByLocale(locale,poTable.getComments())+".xlsx");
        return outFile;
    }

    /**
     * 上傳數據處理
     */
    public String uploadFile(PoTable poTable, MultipartFile file, AjaxResult result, Locale locale){
        try {
            String suffix = "";
            if (file.getOriginalFilename().lastIndexOf(".") != -1) {
                suffix = file.getOriginalFilename().substring(
                        file.getOriginalFilename().lastIndexOf(".") + 1,
                        file.getOriginalFilename().length());
                suffix = suffix.toLowerCase();
            }
            if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
                result.put("flag", "fail");
                result.put("msg", getByLocale(locale, "請上傳正確格式的Excel文件_The format of excel is error"));
                return result.getJson();
            }
            Workbook wk = StreamingReader.builder()
                    .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                    .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(file.getInputStream());  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
            Sheet sheet = wk.getSheetAt(0);

            System.out.print("开始处理数据-------》");
            List<PoColumns> columns = poTable.getColumns();
            int COLUMN_NUM = columns.size()-2;
            List<List<String>> dataList=new ArrayList<List<String>>();
            String yearMonth="";
            int n;
            for (Row row : sheet) {
                if (row.getRowNum() < 3) {
                    Assert.notNull(row, getByLocale(locale, "Please use the downloaded template to import data_請使用所下載的模板導入數據！"));
                    int columnNum = row.getPhysicalNumberOfCells();
                    if (columnNum < COLUMN_NUM) {
                        result.put("flag", "fail");
                        result.put("msg", getByLocale(locale, "The number of columns cannot be less than " + COLUMN_NUM+"_列數不能小於"+ COLUMN_NUM));
                        return result.getJson();
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
                    }
                    n++;
                }
                if (yearMonth.indexOf(data.get(0).trim()+data.get(1).trim())==-1){
                    if(data.get(1).trim().length()==1){
                        yearMonth+="'"+data.get(0).trim()+"0"+data.get(1).trim()+"',";
                    }else{
                        yearMonth+="'"+data.get(0).trim()+data.get(1).trim()+"',";
                    }
                }
                dataList.add(data);
            }
            String s=saveRtData(dataList,columns,poTable.getTableName(),yearMonth);
            if(s.equals("S")){
                return result.getJson();
            }else{
                result.put("flag", "fail");
                result.put("msg", s);
                return result.getJson();
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("flag", "fail");
            result.put("msg", "保存失敗"+ ExceptionUtil.getRootCauseMessage(e));
            return result.getJson();
        }
    }

    /**
     * 數據保存
     */
    private String saveRtData(List<List<String>> list ,List<PoColumns> columns,String tableName,String yearMonth){
        System.out.print("处理数据插入表中");
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String userName=loginUser.getUsername();
        String message="S";
        try {
            String columnStr="insert into "+tableName+"(ID,";
            for (PoColumns column : columns) {
                if (column.getColumnName().equals("CREATE_DATE")){continue;}
                columnStr += column.getColumnName() + ",";
            }
            String sql="delete from "+tableName+" where YEARS||PERIOD in("+yearMonth.substring(0,yearMonth.length()-1)+")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            sql="";
            for (List<String> val:list) {
                sql=columnStr.substring(0,columnStr.length()-1)+") values('"+UUID.randomUUID().toString()+"',";
                for (int i=0;i<columns.size()-2;i++){
                    if(i==1&&val.get(i).length()==1){
                        sql+="'0"+val.get(i)+"',";
                        continue;
                    }
                    sql+="'"+val.get(i)+"',";
                }
                sql+="'"+userName+"',";
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.substring(0,sql.length()-1)+")").executeUpdate();
            }
        }catch (Exception e){
            message="Failed to save"+ExceptionUtil.getRootCauseMessage(e)+"_保存失敗！"+ExceptionUtil.getRootCauseMessage(e);
        }
        return message;
    }

    /**
     * 切分中英文
     */
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

    /**
     *下載
     */
    public String downloadFile(String queryCondition, PoTable poTable, HttpServletRequest request, PageRequest pageRequest) throws IOException {
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
            if (poColumn.getDataType().equalsIgnoreCase("NUMBER")) {
                sql += "regexp_replace(to_char(" + columnName + ",'FM99999999999999.999999999'),'\\.$',''),";
                numberList.add(i);
            } else if (poColumn.getDataType().equalsIgnoreCase("DATE")) {
                sql += "to_char(" + columnName + ",'yyyy/mm/dd hh24:mi:ss'),";
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
                    if(columnName.equals("YEARS")){
                        columnValue=columnValue.replace("-","");
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                        }
                        whereSql += " and YEARS||PERIOD='"+columnValue+"' ";
                    }else{
                        whereSql+=" and "+columnName+" like '%"+columnValue+"%'";
                    }
                }
            }
            whereSql+= " order by CREATE_DATE desc,ID";
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
