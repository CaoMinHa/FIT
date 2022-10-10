package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author maggao
 */
@Service
public class RtHistoricalDataService{

    @Autowired
    private PoTableDao poTableDao;
    @Autowired
    private InstrumentClassService instrumentClassService;

    @Value("${jdbc-0.proxool.driver-url}")
    String url;
    @Value("${jdbc-0.user}")
    String user;
    @Value("${jdbc-0.password}")
    String password;

    public String selectDataSql(String queryCondition, PoTable poTable, Locale locale, Model model) {
        List<PoColumns> columns = poTable.getColumns();
        List<PoColumns> columnsList=new ArrayList<>();
        for (PoColumns poColumns : columns) {
            if( poColumns.getColumnName().equals("YEAR_MONTH")||
                    poColumns.getColumnName().equals("ENTITY_CODE")|| poColumns.getColumnName().equals("CUST_GROUP")||
                    poColumns.getColumnName().equals("SALE_DEP")||poColumns.getColumnName().equals("SEGMENT")||
                    poColumns.getColumnName().equals("INDUSTRY")||poColumns.getColumnName().equals("SBU")||
                    poColumns.getColumnName().equals("TRADE_TYPE")||poColumns.getColumnName().equals("SHIP_TO_COUNTRY")||
                    poColumns.getColumnName().equals("ACCOUNT_MGR")||poColumns.getColumnName().equals("FIT33")){
                poColumns.setComments(poColumns.getComments());
                columnsList.add(poColumns);
            }
        }
        String sql = "select NO,YEAR_MONTH,ENTITY_CODE,CUST_GROUP,ACCOUNT_MGR,SALE_DEP,SEGMENT,INDUSTRY,FIT33,SBU,TRADE_TYPE,SHIP_TO_COUNTRY from IF_EBS_AR_REVENUE_DTL_MANUAL where 1=1 ";
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if(columnName.equals("YEAR_MONTH")){
                        columnValue=columnValue.replace("-","");
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                        }
                        sql += " and " + columnName + "='" + columnValue + "'";
                    }else{
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
        }

        String[] sbu = SecurityUtils.getSBU();
        if(null!=sbu){
            String sbuSql=" and sbu in (";
            for (String s:sbu) {
                sbuSql+="'"+s+"',";
            }
            sql+=sbuSql.substring(0,sbuSql.length()-1)+")";
        }

        sql += " order by YEAR_MONTH desc,P_N,CUST_CODE,No desc";
        model.addAttribute("columns", columnsList);
        return sql;
    }

    public List<Map> selectQuery(HttpServletRequest request){
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='IF_EBS_AR_REVENUE_DTL_MANUAL' AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<Map> list = poTableDao.listMapBySql(sql);
        List<Map> a=new ArrayList<>();
        for (Map poColumns : list) {
            Map map=new HashMap();
            map.put("key",poColumns.get("COLUMN_NAME"));
            map.put("val",poColumns.get("COMMENTS").toString());
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

    public File template(XSSFWorkbook workBook,PoTable poTable,HttpServletRequest request ) throws UnsupportedEncodingException {
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
        sheet.createFreezePane(0, 1, 0, 1);
        int rowIndex = 0;
        Row row = sheet.createRow(rowIndex++);
        for (int i = 0; i < columns.size(); i++) {
            String comments = columns.get(i).getComments();
            Cell cell = row.createCell(i);
            cell.setCellValue(comments);
            cell.setCellStyle(titleStyle);
            sheet.setColumnWidth(i, comments.getBytes("GBK").length * 256 + 400);
        }
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download/"+getByLocale(locale,poTable.getComments())+".xlsx");
        return outFile;
    }

    public String uploadFile(PoTable poTable,Sheet sheet,AjaxResult result, Locale locale){
        System.out.print("开始处理数据-------》");
        List<PoColumns> columns = poTable.getColumns();
        int COLUMN_NUM = columns.size();
        List<List<String>> dataList=new ArrayList<List<String>>();
        Map<String,List<List<String>>> map=new HashMap<>();
        String yearMonth="";
        BigDecimal revenueUSD = new BigDecimal("0");
        BigDecimal revenueNTD = new BigDecimal("0");
        List<String> listSbu=new ArrayList<>();
        System.out.print("开始装填数据-------》");
        List<String> data;
        String value="";
        int n;
        int number=0;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                Assert.notNull(row, getByLocale(locale, "The title line cannot be empty_第一行為標題行，不允許為空"));
                int columnNum = row.getPhysicalNumberOfCells();
                if (columnNum < COLUMN_NUM) {
                    result.put("flag", "fail");
                    result.put("msg", getByLocale(locale, "The number of columns cannot be less than " + COLUMN_NUM+"_列數不能小於"+ COLUMN_NUM));
                    return result.getJson();
                }
                number++;
                continue;
            }
            n = 0;
            data = new ArrayList<String>(COLUMN_NUM);
            while (n < COLUMN_NUM) {
                if(null==row.getCell(n)){
                    data.add("");
                }else{
                    value = row.getCell(n).getStringCellValue();
                    data.add(value);
                    if(n==32){
                        listSbu.add(value);
                    }
                }
                n++;
            }

            if (yearMonth.indexOf(data.get(1).trim())==-1){
                yearMonth+="'"+data.get(1).trim()+"',";
            }
            if(!data.get(1).trim().equals("202001")&&!data.get(1).trim().equals("202002")&&!data.get(1).trim().equals("202003")
                    &&!data.get(1).trim().equals("202004")&&!data.get(1).trim().equals("202005")&&!data.get(1).trim().equals("202006")){
                if(null != data.get(20).trim()&& !"".equals(data.get(20).trim())){
                    revenueUSD=revenueUSD.add(new BigDecimal(data.get(20).trim()));
                }
                if(null != data.get(76).trim()&& !"".equals(data.get(76).trim())){
                    revenueNTD=revenueNTD.add(new BigDecimal(data.get(76).trim()));
                }
            }
            dataList.add(data);
            if(number%3000==0){
                map.put(String.valueOf(number),dataList);
                dataList=new ArrayList<List<String>>();
            }
            number++;
        }
        map.put(String.valueOf(number),dataList);
        if (!map.isEmpty()) {
            //校验需求类型是否存在
            String  msg=dataCheck(revenueUSD,revenueNTD,yearMonth,listSbu);
            if(!"S".equals(msg)){
                result.put("flag", "fail");
                result.put("msg", getByLocale(locale, msg));
                return result.getJson();
            }
        } else {
            result.put("flag", "fail");
            result.put("msg", getByLocale(locale, "NO valid data row_無有效數據行"));
            return result.getJson();
        }
        System.out.print("数据集大小："+map.size());
        try {
            String s=saveRtData(map,poTable);
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
            result.put("msg", "保存失敗"+ExceptionUtil.getRootCauseMessage(e));
            return result.getJson();
        }
    }

    private String saveRtData(Map<String,List<List<String>>> map,PoTable poTable) throws SQLException, ClassNotFoundException {
        System.out.print("处理数据插入表中");
        String message="S";
        Connection con=null;
        PreparedStatement pst = null;
        /** 測試環境*/
        List<PoColumns> columns = poTable.getColumns();
        String columnStr = "";
        String valStr = "";
        BigDecimal number;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        for (PoColumns column : columns) {
            columnStr += column.getColumnName() + ",";
            valStr += "?,";
        }
        columnStr=columnStr.substring(0,columnStr.length()-1);
        valStr=valStr.substring(0,valStr.length()-1);

        StringBuffer sql = new StringBuffer();
        sql.append("insert into "+poTable.getTableName()+" ("+columnStr+") values ("+valStr+")");
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection(url,user,password);
        // 关闭事务自动提交
        con.setAutoCommit(false);
        pst = con.prepareStatement(sql.toString());
        try {
            for(String key:map.keySet()){
                List<List<String>> dataList= map.get(key);
                for (int j=0;j<dataList.size();j++) {
                    List<String> data =dataList.get(j);
                    String generateType = data.get(0);
                    pst.setString(1,generateType);
                    for (int i = 1; i < data.size(); i++) {
                        String val=data.get(i).trim();
                        if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                            if(null==val||val.length()<1){
                                pst.setInt(i+1,0);
                            }else{
                                number= new BigDecimal(val);
                                pst.setBigDecimal(i+1,number);
                            }
                        } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                            if(null!=val&&!val.equals("")){
                                if(val.indexOf("/")!=-1) {
                                    java.util.Date date = formatter.parse(val);
                                    java.sql.Date formatDate = new java.sql.Date(date.getTime());
                                    pst.setDate(i + 1, formatDate);
                                    continue;
                                }
                                pst.setDate(i+1,getDate(false,Double.parseDouble(val)));
                            }else{
                                pst.setDate(i+1,null);
                            }
                        } else {
                            pst.setString(i+1,val);
                        }
                    }
                    // 把一个SQL命令加入命令列表
                    pst.addBatch();
                }
                pst.executeBatch();
                con.commit();
                System.out.println("------------>3000");
            }
            pst.close();
            con.close();
        }catch (Exception e) {
            try {
                con.rollback();
                pst.close();
                con.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
                message="保存失敗"+ExceptionUtil.getRootCauseMessage(e);
                return message;
            }
            e.printStackTrace();
            message="保存失敗"+ExceptionUtil.getRootCauseMessage(e);
            return message;
        }
        return message;
    }

    public java.sql.Date getDate(boolean b, double value){
        int wholeDays = (int)Math.floor(value);
        int millisecondsInDay = (int)((value - (double)wholeDays) * 8.64E7D + 0.5D);

        Calendar calendar = new GregorianCalendar();

        short startYear = 1900;
        byte dayAdjust = -1;

        if (b) {
            startYear = 1904;
            dayAdjust = 1;
        }else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, millisecondsInDay);

        if(calendar.get(Calendar.MILLISECOND) == 0) {
            calendar.clear(Calendar.MILLISECOND);
        }

        Date date = calendar.getTime();
        java.sql.Date d=new java.sql.Date(date.getTime());
        return d;
    }
    private String dataCheck(BigDecimal revenueUSD,BigDecimal revenueNTD,String yearMonth,List<String> listSbu){
        System.out.println("校验条件"+yearMonth+"revenueNTD："+revenueNTD+"revenueUSD："+revenueUSD+"  sbu:"+listSbu);
        String msg="S";
        if(yearMonth.indexOf("202001")==-1&&yearMonth.indexOf("202002")==-1&&yearMonth.indexOf("202003")==-1
                &&yearMonth.indexOf("202004")==-1&&yearMonth.indexOf("202005")==-1&&yearMonth.indexOf("202006")==-1) {
            String[] date = yearMonth.split(",");
            for (String d : date) {
                String sql = "SELECT sum(AMOUNT_EXCLUDING_TAX_NTD) as amountNtd,sum(AMOUNT_EXCLUDING_TAX_USD) as amountUsd  FROM bidev.if_ebs_ar_revenue_dtl where year_month=" + d + "";
                List<Map> maps = poTableDao.listMapBySql(sql);
                if (maps != null) {
                    BigDecimal amountNtd = new BigDecimal(maps.get(0).get("AMOUNTNTD").toString());
                    BigDecimal amountUsd = new BigDecimal(maps.get(0).get("AMOUNTUSD").toString());
                    if (amountNtd.add(new BigDecimal("1")).compareTo(revenueNTD) != 1 &&
                            amountNtd.subtract(new BigDecimal("1")).compareTo(revenueNTD) != -1) {
                        msg = "Please check if the" + d.substring(1, d.length() - 1) + " Revenue(NTD) is consistent with EBS data_請檢查" + d.substring(1, d.length() - 1) + " Revenue(NTD)與EBS數據是否一致";
                        return msg;
                    } else if (amountUsd.add(new BigDecimal("1")).compareTo(revenueUSD) != 1 &&
                            amountUsd.subtract(new BigDecimal("1")).compareTo(revenueUSD) != -1) {
                        msg = "Please check if the" + d.substring(1, d.length() - 1) + "  Revenue(USD) is consistent with EBS data_請檢查" + d.substring(1, d.length() - 1) + " Revenue(USD)與EBS數據是否一致";
                        return msg;
                    }
                }
            }
        }
        List<String> sbuCount=Arrays.asList(SecurityUtils.getSBU());
        if(instrumentClassService.getDiffrent(listSbu,sbuCount).length()>1){
            msg="("+instrumentClassService.getDiffrent(listSbu,sbuCount)+")No SBU permission, please contact the administrator for maintenance!_("+instrumentClassService.getDiffrent(listSbu,sbuCount)+")沒有SBU權限，請聯係管理員維護!";
            return msg;
        }
        if(msg.equals("S")){
            System.out.print("yearMonth："+yearMonth);
            String  deleteStr="delete from IF_EBS_AR_REVENUE_DTL_MANUAL where  year_month in("+yearMonth.substring(0, yearMonth.length()-1)+")";
            System.out.print("校验通过删除语句："+deleteStr);
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteStr).executeUpdate();
        }
        return msg;
    }

    public String downloadFile(String queryCondition, PoTable poTable, HttpServletRequest request,PageRequest pageRequest) throws IOException {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        XSSFWorkbook workBook = new XSSFWorkbook();
        String tableName="IF_EBS_AR_REVENUE_DTL_MANUAL";
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
        }

        String[] sbu = SecurityUtils.getSBU();
        if(null!=sbu){
            String sbuSql=" and sbu in (";
            for (String s:sbu) {
                sbuSql+="'"+s+"',";
            }
            whereSql+=sbuSql.substring(0,sbuSql.length()-1)+")";
        }
        sql = sql.substring(0, sql.length() - 1) + " from " + tableName + whereSql+" order by NO";
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
                        for (int i = 0; i < objects.length-1; i++) {
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

    public AjaxResult deleteData(AjaxResult ajaxResult,String no) {
        try {
            String[] ids = no.split(",");
            String deleteSql = " delete from IF_EBS_AR_REVENUE_DTL_MANUAL where No in (";
            String whereSql = "";
            for (String s : ids) {
                whereSql = whereSql + "'" + s + "',";
            }
            whereSql = whereSql.substring(0, whereSql.length() - 1);
            deleteSql += whereSql + ")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
        } catch (Exception e) {
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult;
    }
}