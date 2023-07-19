package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.RtDynamicPrediction;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;

/**
 * 導入手工版營收毛利
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RtRevenueGrossProfitService{
    @Autowired
    private PoTableDao poTableDao;
    @Autowired
    private InstrumentClassService instrumentClassService;


    /**拼裝查詢sql*/
    public Page<Object[]> selectDataSql(String queryCondition,PageRequest pageRequest) {
        String sql ="select ETL_DAY,YEAR_MONTH,ENTITY_SYSTEM,PRODUCT_CATEGORY,BU,SBU,BM_SBU,SEGMENT,PRODUCT_TYPE,INDUSTRY,SUB_INDUSTRY,STRAGETY,SCREEN_CLOUD,\n" +
                "INNER_DEL_EXT_SALE,CUST_GROUP,CUST_CODE,BRAND_CUST,CUST_AREA,PRODUCT_FAMILY,PRODUCT_SERIES,QUANTITY,TRX_AMT_EXCLUDING_TAX,PROFIT_AMOUNT,USD_NTD_RATE\n" +
                " from BIDEV.if_rev_dtl_manual where 1=1";
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
                    }
                    sql += " and " + columnName + " like '%" + columnValue + "%'";
                }
            }
        }
        sql += " order by YEAR_MONTH desc,id desc";
        Page<Object[]> page = poTableDao.findPageBySql(pageRequest, sql);
        return page;
    }


    /**數據上傳*/
    public String uploadFile(MultipartHttpServletRequest multipartHttpServletRequest, AjaxResult result, Locale locale){
        try {
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            if (mutipartFiles != null && mutipartFiles.size() > 0) {
                MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];
                String suffix = "";
                if (file.getOriginalFilename().lastIndexOf(".") != -1) {
                    suffix = file.getOriginalFilename().substring(
                            file.getOriginalFilename().lastIndexOf(".") + 1,
                            file.getOriginalFilename().length());
                    suffix = suffix.toLowerCase();
                }
                Workbook wb = null;
                if ("xls".equals(suffix)) {
                    //Excel2003
                    wb = new HSSFWorkbook(file.getInputStream());
                } else if ("xlsx".equals(suffix)) {
                    //Excel2007
                    wb = new XSSFWorkbook(file.getInputStream());
                } else {
                    result.put("flag", "fail");
                    result.put("msg", instrumentClassService.getLanguage(locale, "请您上传正确格式的Excel文件", "Error File Formats"));
                    return result.getJson();
                }
                wb.close();
                Sheet sheet = wb.getSheetAt(0);
                int column = sheet.getRow(0).getLastCellNum();
                Assert.isTrue(column == 23, instrumentClassService.getLanguage(locale, "請下載正確的模板上傳數據！", "Please download the correct template to upload the data"));
                int rowNum = sheet.getPhysicalNumberOfRows();
                Assert.isTrue(rowNum > 1, instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
                List<String> list =new ArrayList<String>();
                List<String> listSql =new ArrayList<String>();
                for (int i = 0; i < rowNum; i++) {
                    if (null == sheet.getRow(i+1)) {
                        continue;
                    }
                    String sql="insert into BIDEV.if_rev_dtl_manual(YEAR_MONTH,ENTITY_SYSTEM,PRODUCT_CATEGORY,BU,SBU,BM_SBU,SEGMENT,PRODUCT_TYPE,INDUSTRY,SUB_INDUSTRY,STRAGETY,SCREEN_CLOUD,\n" +
                            "INNER_DEL_EXT_SALE,CUST_GROUP,CUST_CODE,BRAND_CUST,CUST_AREA,PRODUCT_FAMILY,PRODUCT_SERIES,QUANTITY,TRX_AMT_EXCLUDING_TAX,PROFIT_AMOUNT,USD_NTD_RATE) values(";
                    Row row = sheet.getRow(i+1);
                    for (int j=0;j<23;j++){
                        sql+="'"+ExcelUtil.getCellStringValue(row.getCell(j), i)+"',";
                    }
                    list.add(ExcelUtil.getCellStringValue(row.getCell(0), 0));
                    sql=sql.substring(0,sql.length()-1)+")";
                    listSql.add(sql);
                }
                Assert.isTrue(null!=listSql,instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
                this.save(list,listSql);
                return result.getJson();
            }else {
                result.put("flag", "fail");
                result.put("msg", instrumentClassService.getLanguage(locale, "對不起，未接受到上傳的文件", "Unreceived File"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    /**数据保存*/
    public void save(List<String> list,List<String> listSql){
        list=instrumentClassService.removeDuplicate(list);
        String sql="delete BIDEV.if_rev_dtl_manual where YEAR_MONTH in('"+StringUtils.deleteWhitespace(list.toString().substring(1,list.toString().length()-1).replaceAll(",","','"))+"')";
        System.out.println("sql语句"+sql);
        poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        for (String s:listSql) {
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(s).executeUpdate();
        }
    }
    /**数据倒挤*/
    public void diff(String yearMonth) throws Exception {
        yearMonth=yearMonth.replace("-","");
        if(yearMonth.length()==5){
            yearMonth=yearMonth.substring(0,4)+"0"+yearMonth.substring(4,5);
        }
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call epmexp.pbcs_pl_pkg.diff_amt(?)}");
        cs.setString(1, yearMonth);
        cs.execute();
        cs.close();
        c.close();
    }

    /**下載數據*/
    public String downloadFile(String queryCondition,HttpServletRequest request,PageRequest pageRequest) throws IOException {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        String realPath = request.getRealPath("");
        String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"手工營收毛利","Manual revenue gross profit")+".xlsx";
        InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"bi"+File.separator+instrumentClassService.getLanguage(locale,"手工營收毛利","Manual revenue gross profit")+".xlsx");
        XSSFWorkbook workBook = new XSSFWorkbook(ins);
        workBook.removeSheetAt(2);
        workBook.removeSheetAt(1);
        Sheet sheet = workBook.getSheetAt(0);
        String sql = "select ETL_DAY,YEAR_MONTH,ENTITY_SYSTEM,PRODUCT_CATEGORY,BU,SBU,BM_SBU,SEGMENT,PRODUCT_TYPE,INDUSTRY,SUB_INDUSTRY,STRAGETY,SCREEN_CLOUD,\n" +
                "INNER_DEL_EXT_SALE,CUST_GROUP,CUST_CODE,BRAND_CUST,CUST_AREA,PRODUCT_FAMILY,PRODUCT_SERIES,QUANTITY,TRX_AMT_EXCLUDING_TAX,PROFIT_AMOUNT,USD_NTD_RATE from  BIDEV.if_rev_dtl_manual where 1=1";
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
                    }
                    sql += " and " + columnName + " like '%" + columnValue + "%'";
                }
            }
        }
        sql += " order by YEAR_MONTH desc,id desc";
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
                    if (StringUtils.isNotEmpty(text)){
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
                            if (StringUtils.isNotEmpty(text)){
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
        System.gc();
        return outFile.getName();
    }


    /**下載模板*/
    public Map<String,String>  template(HttpServletRequest request,Locale locale) {
        Map<String,String> mapResult=new HashMap<>();
        mapResult.put("result","Y");
        try {
            String realPath = request.getRealPath("");
            String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"手工營收毛利","Manual revenue gross profit")+".xlsx";
            InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"bi"+File.separator+"手工營收毛利.xlsx");
            XSSFWorkbook workBook = new XSSFWorkbook(ins);
            workBook.removeSheetAt(0);
            Sheet sheet = workBook.getSheetAt(1);
            List<Map> list= poTableDao.listMapBySql("select YEARMONTH,RATE_TYPE,FROM_CURRENCY||' -> '||TO_CURRENCY CURRENCY,RATE from epmods.cux_hfm_biee_exrate_v1");
            for (int i=0;i<list.size();i++) {
                Row row=sheet.createRow(i+1);
                Map map=list.get(i);
                row.createCell(0).setCellValue(map.get("YEARMONTH").toString());
                row.createCell(1).setCellValue(map.get("RATE_TYPE").toString());
                row.createCell(2).setCellValue(map.get("CURRENCY").toString());
                row.createCell(3).setCellValue(map.get("RATE").toString());
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