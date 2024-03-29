package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
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
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;

/**
 * @author maggao
 */
@Service
public class PlOfflineDataSupplementService {

    @Autowired
    private PoTableDao poTableDao;

    @Autowired
    private InstrumentClassService instrumentClassService;
    /**獲取查詢結果**/
    public void list(PageRequest pageRequest,String queryCondition, Locale locale, Model model,String type) {
        List<Map> map=poTableDao.listMapBySql("select COLUMN_NAME,COMMENTS from fit_po_table_columns where table_name='"+type+"' ORDER BY to_number(SERIAL)");
        List<String> column=new ArrayList<>();
        String sql = "select PL_ID,";
        for (Map m : map) {
            sql+=m.get("COLUMN_NAME").toString()+",";
            column.add(instrumentClassService.getByLocale(locale, m.get("COMMENTS").toString()));
        }
        sql=sql.substring(0,sql.length()-1)+" from epmebs."+type+" where 1=1 ";
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if(columnName.equals("PERIOD")){
                        columnValue=columnValue.replace("-","");
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"-0"+columnValue.substring(4,5);
                        }else{
                            columnValue=columnValue.substring(0,4)+"-"+columnValue.substring(4,6);
                        }
                        sql += " and " + columnName + "='" + columnValue + "'";
                    }else{
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
        }
        sql += "and PERIOD>'2021-12' order by PL_ID desc";
        model.addAttribute("columns", column);
        Page<Object[]> page = poTableDao.findPageBySql(pageRequest, sql);
        int index = 1;
        if (pageRequest.getPageNo() > 1) {
            index = 2;
        }
        model.addAttribute("index", index);
        model.addAttribute("page", page);
        model.addAttribute("tableType", type);
    }

    /**獲取查詢字段**/
    public List<Map> selectQuery(String type,Locale locale ){
        String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='"+type+"' AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
        List<Map> list = poTableDao.listMapBySql(sql);
        for (Map map : list) {
            map.put("COMMENTS",instrumentClassService.getByLocale(locale,map.get("COMMENTS").toString()));
        }
        return list;
    }

    /**獲取下載模板**/
    public File template(XSSFWorkbook workBook,HttpServletRequest request,Locale locale) {
        Sheet sheet = workBook.getSheetAt(1);
        String sql="SELECT ATTRIBUTE1,ATTRIBUTE2,COMPANY_CODE,COMPANY_NAME_CN,COMPANY_NAME_EN FROM epmebs.CUX_EBS_COMPANY_V";
        List<Map> listCompany=poTableDao.listMapBySql(sql);
        int number=1;
        for(Map map:listCompany){
            Row row = sheet.createRow(number);
            row.createCell(0).setCellValue(instrumentClassService.mapValString(map.get("ATTRIBUTE1")));
            row.createCell(1).setCellValue(instrumentClassService.mapValString(map.get("ATTRIBUTE2")));
            row.createCell(2).setCellValue(instrumentClassService.mapValString(map.get("COMPANY_CODE")));
            row.createCell(3).setCellValue(instrumentClassService.mapValString(map.get("COMPANY_NAME_CN")));
            row.createCell(4).setCellValue(instrumentClassService.mapValString(map.get("COMPANY_NAME_EN")));
            number++;
        }
        Sheet sheet1 = workBook.getSheetAt(2);
        sql="SELECT SBU_CODE,SBU FROM epmebs.CUX_FIT_SBU_CODE_V";
        List<Map> listSbu=poTableDao.listMapBySql(sql);
        number=1;
        for(Map map:listSbu){
            Row row = sheet1.createRow(number);
            row.createCell(0).setCellValue(instrumentClassService.mapValString(map.get("SBU_CODE")));
            row.createCell(1).setCellValue(instrumentClassService.mapValString(map.get("SBU")));
            number++;
        }
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download"+File.separator+instrumentClassService.getByLocale(locale,"Template-Offline profit and loss and internal transaction supplement.xlsx_綫下損益表内交模板.xlsx"));
        return outFile;
    }

    /**数据上传**/
    public String upload(Sheet sheet,AjaxResult result, Locale locale,String tableName) throws Exception {
        System.out.print("开始处理数据-------》");
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String roleSql="select count(1) from  fit_user u \n" +
                " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                " WHERE r.code='PLadmin' and  u.username="+"'"+loginUser.getUsername()+"'";
        List<BigDecimal> countList = (List<BigDecimal>)poTableDao.listBySql(roleSql);
        int count = countList.get(0).intValue();
        List<PoColumns> columns = poTableDao.listBySql("select * from fit_po_table_columns where table_name='"+tableName+"' ORDER BY to_number(SERIAL)",PoColumns.class);
        int COLUMN_NUM = columns.size();
        List<List<String>> dataList=new ArrayList<List<String>>();
        String legalPersonCode="";
        List<String> legalPersonCodelist=new ArrayList<>();
        int n;
        String period="";
        for (Row row : sheet) {
            if (row.getRowNum() < 4) {
                Assert.notNull(row, instrumentClassService.getByLocale(locale, "Please use the downloaded template to import data_請使用所下載的模板導入數據！"));
                int columnNum = row.getPhysicalNumberOfCells();
                if (columnNum < 16) {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getByLocale(locale, "The number of columns cannot be less than 16_列數不能小於16,請檢查上傳模板是否正確！"));
                    return result.getJson();
                }
                continue;
            }else if(row.getRowNum()==4){
                period = ExcelUtil.getCellStringValue(row.getCell(0),4);
                if(period.length()<7&&period.indexOf("-")!=4){
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getByLocale(locale, "Please fill in the correct period data such as: 2022-01_請填寫正確期間數據如：2022-01！"));
                    return result.getJson();
                }
                if (count<1){
                    countList = (List<BigDecimal>)poTableDao.listBySql("select count(1) from BIDEV.CUX_PL_DEFAULT_BI_V where YEAR_MONTH='"+period.replace("-","")+"'");
                    count = countList.get(0).intValue();
                    if(count>0){
                        result.put("flag", "fail");
                        result.put("msg", instrumentClassService.getByLocale(locale, "Published income statement data does not allow updates_已發佈的損益表數據不允許更新。"));
                        return result.getJson();
                    }
                }
                if(Integer.parseInt(period.substring(0,4))<2022){
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getByLocale(locale, "Only supports uploading data greater than or equal to 2022_僅支持上傳大於等於2022年的數據"));
                    return result.getJson();
                }
            }
                n = 0;
                List<String> data = new ArrayList<>(COLUMN_NUM);
                while (n < COLUMN_NUM) {
                    if(n==0&&!period.equals(ExcelUtil.getCellStringValue(row.getCell(n),row.getRowNum()))){
                        result.put("flag", "fail");
                        result.put("msg", instrumentClassService.getByLocale(locale, "Please upload the data of the same period_請上傳同期間的數據"));
                        return result.getJson();
                    }
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
                        if(n==1){
                            if (legalPersonCode.indexOf(data.get(1).trim())==-1){
                                legalPersonCode+="'"+value+"',";
                                legalPersonCodelist.add(value);
                            }
                        }
                    }
                    n++;
                }
                dataList.add(data);
            }
        System.out.print("数据集大小："+dataList.size());
        try {
            dataCheck(tableName,period,legalPersonCode);
            String s=saveRtData(dataList,columns,tableName);
            if(s.equals("S")){
                Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
                for (String a:legalPersonCodelist) {
                    CallableStatement cs = c.prepareCall("{call epmebs.ecux_expense_data_pkg.pl_main_manual_all(?,?)}");
                    cs.setInt(1, Integer.parseInt(a));
                    cs.setString(2, period);
                    cs.execute();
                    cs.close();
                }
                c.close();
                return result.getJson();
            }else{
                result.put("flag", "fail");
                result.put("msg", instrumentClassService.getByLocale(locale,s));
                return result.getJson();
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("flag", "fail");
            result.put("msg", instrumentClassService.getByLocale(locale,"fail to upload"+ExceptionUtil.getRootCauseMessage(e)+"_上傳失敗"+ExceptionUtil.getRootCauseMessage(e)));
            return result.getJson();
        }
    }

    /**保存数据**/
    @Transactional(rollbackFor = Exception.class)
    private String saveRtData(List<List<String>> list ,List<PoColumns> columns,String tableName){
        System.out.print("处理数据插入表中");
        String message="S";
        try {
            String columnStr="insert into epmebs."+tableName+"(";
            for (PoColumns column : columns) {
                columnStr += column.getColumnName() + ",";
            }
            String sql="";
            for (List<String> val:list) {
                sql=columnStr.substring(0,columnStr.length()-1)+") values(";
                if(tableName.equals("cux_pl_manual")){
                    for (int i=0;i<11;i++){
                        sql+="'"+val.get(i).trim()+"',";
                    }
                }else if(tableName.equals("cux_pl_fit_inter_manual")){
                    for (int i=0;i<val.size();i++){
                        sql+="'"+val.get(i).trim()+"',";
                    }
                }
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.substring(0,sql.length()-1)+")").executeUpdate();
            }
        }catch (Exception e){
            message="Failed to save"+ExceptionUtil.getRootCauseMessage(e)+"_保存失敗！"+ExceptionUtil.getRootCauseMessage(e);
        }
        return message;
    }

    /**删除历史数据**/
    private void dataCheck(String tableName,String period,String legalPersonCode){
            System.out.print("表："+tableName+"期間："+period+"法人代碼："+legalPersonCode);
            String  deleteStr="delete from epmebs."+tableName+" where PERIOD='"+period+"' and LEGAL_PERSON_CODE in("+legalPersonCode.substring(0, legalPersonCode.length()-1)+")";
            System.out.print("校验通过删除语句："+deleteStr);
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteStr).executeUpdate();
    }

    /**下载数据**/
    public String download(String queryCondition, String  tableName, HttpServletRequest request,PageRequest pageRequest) throws IOException {
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
            String comments = instrumentClassService.getByLocale(locale,poColumn.getComments());
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
                    if(columnName.equals("PERIOD")){
                        columnValue=columnValue.replace("-","");
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"-0"+columnValue.substring(4,5);
                        }else{
                            columnValue=columnValue.substring(0,4)+"-"+columnValue.substring(4,6);
                        }
                        whereSql += " and " + columnName + "='" + columnValue + "'";
                    }else{
                        whereSql+=" and "+columnName+" like '%"+columnValue+"%'";
                    }
                }
            }
            whereSql+= " order by PL_ID";
        }
        sql = sql.substring(0, sql.length() - 1) + " from epmebs." + tableName + whereSql;
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
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator+instrumentClassService.getByLocale(locale,"Template-Offline profit and loss and internal transaction supplement.xlsx_綫下損益表内交模板.xlsx"));
        OutputStream out = new FileOutputStream(outFile);
        sxssfWorkbook.write(out);
        sxssfWorkbook.close();
        out.flush();
        out.close();
        return outFile.getName();
    }

    /**删除数据**/
    public AjaxResult delete(AjaxResult ajaxResult,String no,String tableName) {
        try {
            String[] ids = no.split(",");
            String deleteSql = " delete from epmebs."+tableName+" where PL_ID in (";
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