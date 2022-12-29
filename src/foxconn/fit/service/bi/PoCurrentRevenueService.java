package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoCurrentRevenueDao;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoCurrentRevenue;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.entity.budget.BudgetDetailRevenue;
import foxconn.fit.entity.investment.DepreExpenBudget;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PoCurrentRevenueService extends BaseService<PoCurrentRevenue> {

    @Autowired
    private PoCurrentRevenueDao poCurrentRevenueDao;
    @Autowired
    private InstrumentClassService instrumentClassService;
    @Override
    public BaseDaoHibernate<PoCurrentRevenue> getDao() {
        return poCurrentRevenueDao;
    }

    /**拼裝查詢sql*/
    public String selectDataSql(String queryCondition) {
        String sql = "select ID,PERIOD,BU,SBU,EXPORT_SALES_REVENUE,DOMESTIC_SALES_REVENUE from FIT_PO_CURRENT_REVENUE where 1=1";
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
        sql += " order by ID desc";
        return sql;
    }
    /**數據上傳*/
    public String uploadFile(MultipartHttpServletRequest multipartHttpServletRequest, AjaxResult result, Locale locale) throws Exception {
        try {
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            List<PoCurrentRevenue> list = new ArrayList();
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
                Assert.isTrue(column <= 5, instrumentClassService.getLanguage(locale, "Excel列数不能小于" + 5 + "，請下載正確的模板上傳數據！", "Number Of Columns Can Not Less Than" + 5 + ",Please download the correct template to upload the data"));
                int rowNum = sheet.getPhysicalNumberOfRows();
                Assert.isTrue(rowNum > 1, instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                for (int i = 1; i < rowNum; i++) {
                    if (null == sheet.getRow(i)) {
                        continue;
                    }
                    Row row = sheet.getRow(i);
                    String period = ExcelUtil.getCellStringValue(row.getCell(0), i);
                    if (period.length() < 7 && period.indexOf("-") != 4) {
                        result.put("flag", "fail");
                        result.put("msg", instrumentClassService.getLanguage(locale, "請填寫正確期間數據如：2022-01！", "Please fill in the correct period data such as: 2022-01"));
                        return result.getJson();
                    }
                    String bu = ExcelUtil.getCellStringValue(row.getCell(1), i);
                    String sbu = ExcelUtil.getCellStringValue(row.getCell(2), i);
                    if (period.isEmpty() || bu.isEmpty() || sbu.isEmpty()) {
                        continue;
                    }
                    PoCurrentRevenue poCurrentRevenue = new PoCurrentRevenue();
                    poCurrentRevenue.setPERIOD(period);
                    poCurrentRevenue.setBU(bu);
                    poCurrentRevenue.setSBU(sbu);
                    poCurrentRevenue.setExportSalesRevenue(ExcelUtil.getCellStringValue(row.getCell(3), i));
                    poCurrentRevenue.setDomesticSalesRevenue(ExcelUtil.getCellStringValue(row.getCell(4), i));
                    poCurrentRevenue.setId(UUID.randomUUID().toString());
                    poCurrentRevenue.setCreateName(loginUser.getUsername());
                    poCurrentRevenue.setCreateDate(new Date());
                    list.add(poCurrentRevenue);
                }
                Assert.isTrue(null!=list,instrumentClassService.getLanguage(locale, "无有效数据行", "Unreceived Valid Row Data"));
                this.saveBatch(list);
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
    /**保存數據*/
    public void saveBatch(List<PoCurrentRevenue> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            PoCurrentRevenue poCurrentRevenue=list.get(i);
            String sql="select count(1) from FIT_PO_CURRENT_REVENUE where PERIOD='"+poCurrentRevenue.getPERIOD()+"' and BU='"+poCurrentRevenue.getBU()+
                    "' and SBU='"+poCurrentRevenue.getSBU()+"'";
            List<BigDecimal> countList = (List<BigDecimal>)poCurrentRevenueDao.listBySql(sql);
            if(countList.get(0).intValue()>0){
                sql="delete from FIT_PO_CURRENT_REVENUE where PERIOD='"+poCurrentRevenue.getPERIOD()+"' and BU='"+poCurrentRevenue.getBU()+
                        "' and SBU='"+poCurrentRevenue.getSBU()+"'";
                poCurrentRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            }
            poCurrentRevenueDao.save(list.get(i));
        }
    }
    /**下載數據*/
    public String downloadFile(String queryCondition,HttpServletRequest request,PageRequest pageRequest) throws IOException {
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

        Sheet sheet = sxssfWorkbook.createSheet("非FIT體系當期收入表");
        sheet.createFreezePane(0, 1, 0, 1);
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("期間");
        titleRow.getCell(0).setCellStyle(titleStyle);
        titleRow.createCell(1).setCellValue("BU");
        titleRow.getCell(1).setCellStyle(titleStyle);
        titleRow.createCell(2).setCellValue("SBU");
        titleRow.getCell(2).setCellStyle(titleStyle);
        titleRow.createCell(3).setCellValue("非FIT體系當期外銷營業收入（M NTD）");
        titleRow.getCell(3).setCellStyle(titleStyle);
        sheet.setColumnWidth(3, "非FIT體系當期外銷營業收入（M NTD）".getBytes("GBK").length*256+400);
        titleRow.createCell(4).setCellValue("非FIT體系當期內銷營業收入（M NTD）");
        titleRow.getCell(4).setCellStyle(titleStyle);
        sheet.setColumnWidth(4, "非FIT體系當期內銷營業收入（M NTD）".getBytes("GBK").length*256+400);
        String sql = "select PERIOD,BU,SBU,EXPORT_SALES_REVENUE,DOMESTIC_SALES_REVENUE from FIT_PO_CURRENT_REVENUE ";
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
            whereSql+= " order by ID";
        }
        sql = sql+ whereSql;
        System.out.println(sql);
        pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
        pageRequest.setPageNo(1);
        List<Object[]> dataList = poCurrentRevenueDao.findPageBySql(pageRequest, sql).getResult();
        if (CollectionUtils.isNotEmpty(dataList)) {
            int rowIndex = 1;
            for (Object[] objects : dataList) {
                Row contentRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < objects.length; i++) {
                    Cell cell = contentRow.createCell(i);
                    String text = (objects[i] != null ? objects[i].toString() : "");
                    if (StringUtils.isNotEmpty(text)&&i>2) {
                        cell.setCellValue(Double.parseDouble(text));
                    } else {
                        cell.setCellValue(text);
                    }
                }
            }

            while (dataList != null && dataList.size() >= ExcelUtil.PAGE_SIZE) {
                pageRequest.setPageNo(pageRequest.getPageNo() + 1);
                dataList = poCurrentRevenueDao.findPageBySql(pageRequest, sql).getResult();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Object[] objects : dataList) {
                        Row contentRow = sheet.createRow(rowIndex++);
                        for (int i = 0; i < objects.length-1; i++) {
                            Cell cell = contentRow.createCell(i);
                            String text = (objects[i] != null ? objects[i].toString() : "");
                            if (StringUtils.isNotEmpty(text) &&i>2) {
                                cell.setCellValue(Double.parseDouble(text));
                            } else {
                                cell.setCellValue(text);
                            }
                        }
                    }
                }
            }
        }
        File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + "非FIT體系當期收入表.xlsx");
        OutputStream out = new FileOutputStream(outFile);
        sxssfWorkbook.write(out);
        sxssfWorkbook.close();
        out.flush();
        out.close();
        return outFile.getName();
    }
    /**刪除數據*/
    public AjaxResult deleteData(AjaxResult ajaxResult,String id) {
        try {
            String[] ids = id.split(",");
            String deleteSql = " delete from FIT_PO_CURRENT_REVENUE where ID in (";
            String whereSql = "";
            for (String s : ids) {
                whereSql = whereSql + "'" + s + "',";
            }
            whereSql = whereSql.substring(0, whereSql.length() - 1);
            deleteSql += whereSql + ")";
            poCurrentRevenueDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
        } catch (Exception e) {
        ajaxResult.put("flag", "fail");
        ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
    return ajaxResult;
    }
    /**下载模板*/
    /**下載模板*/
    public Map<String,String>  template(HttpServletRequest request) {
        Map<String,String> mapResult=new HashMap<>();
        mapResult.put("result","Y");
        try {
            String realPath = request.getRealPath("");
            String filePath=realPath+"static"+File.separator+"download"+File.separator+"非FIT體系當期收入表.xlsx";
            InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"bi"+File.separator+"非FIT體系當期收入表.xlsx");
            XSSFWorkbook workBook = new XSSFWorkbook(ins);
            XSSFCellStyle titleStyle = workBook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
            XSSFFont font = workBook.createFont();
            font.setColor(IndexedColors.WHITE.index);
            font.setBold(true);
            titleStyle.setFont(font);
            Sheet sheet = workBook.createSheet("SBU");
            sheet.createFreezePane(0, 1, 0, 1);
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("BU");
            titleRow.getCell(0).setCellStyle(titleStyle);
            titleRow.createCell(1).setCellValue("SBU");
            titleRow.getCell(1).setCellStyle(titleStyle);

            String sql="select distinct BU_NAME,SBU_NAME from BIDEV.DM_D_ENTITY_SBU where flag='1' order by BU_NAME ";
            List<Map> list= poCurrentRevenueDao.listMapBySql(sql);
            for (int i=0;i<list.size();i++) {
                Map map=list.get(i);
                Row row=sheet.createRow(i+1);
                row.createCell(0).setCellValue(map.get("BU_NAME").toString());
                row.createCell(1).setCellValue(map.get("SBU_NAME").toString());
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