package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.RtDynamicPredictionDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumDimensionType;
import foxconn.fit.entity.bi.RtDynamicPrediction;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * backlog動態預估
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RtDynamicPredictionService extends BaseService<RtDynamicPrediction> {

    @Autowired
    private RtDynamicPredictionDao rtDynamicPredictionDao;
    @Autowired
    private InstrumentClassService instrumentClassService;
    @Override
    public BaseDaoHibernate<RtDynamicPrediction> getDao() {
        return rtDynamicPredictionDao;
    }

    /**拼裝查詢sql*/
    public String selectDataSql(String queryCondition) {
        String sql = "select year,sbu,jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec from FIT_BACKLOG_DYNAMIC_PREDICTION where 1=1";
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
        sql += " order by year,CREATE_TIME,id desc";
        return sql;
    }


    /**數據上傳*/
    public String uploadFile(MultipartHttpServletRequest multipartHttpServletRequest, AjaxResult result, Locale locale,String year) {
        try {
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            List<RtDynamicPrediction> list = new ArrayList();
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
                Assert.isTrue(column == 13, instrumentClassService.getLanguage(locale, "請下載正確的模板上傳數據！", "Please download the correct template to upload the data"));
                int rowNum = sheet.getPhysicalNumberOfRows();
                Assert.isTrue(rowNum > 0, instrumentClassService.getLanguage(locale, "检测到Excel没有行数据", "Row Data Not Empty"));
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                for (int i = 0; i < rowNum; i++) {
                    if (null == sheet.getRow(i+1)) {
                        continue;
                    }
                    Row row = sheet.getRow(i+1);
                    String sbu = ExcelUtil.getCellStringValue(row.getCell(0), i);
                    if (sbu.isEmpty()) {
                        continue;
                    }
                    RtDynamicPrediction rtDynamicPrediction = new RtDynamicPrediction();
                    rtDynamicPrediction.setYear(year);
                    rtDynamicPrediction.setSBU(sbu);
                    rtDynamicPrediction.setJAN(ExcelUtil.getCellStringValue(row.getCell(1), i));
                    rtDynamicPrediction.setFEB(ExcelUtil.getCellStringValue(row.getCell(2), i));
                    rtDynamicPrediction.setMAR(ExcelUtil.getCellStringValue(row.getCell(3), i));
                    rtDynamicPrediction.setAPR(ExcelUtil.getCellStringValue(row.getCell(4), i));
                    rtDynamicPrediction.setMAY(ExcelUtil.getCellStringValue(row.getCell(5), i));
                    rtDynamicPrediction.setJUN(ExcelUtil.getCellStringValue(row.getCell(6), i));
                    rtDynamicPrediction.setJUL(ExcelUtil.getCellStringValue(row.getCell(7), i));
                    rtDynamicPrediction.setAUG(ExcelUtil.getCellStringValue(row.getCell(8), i));
                    rtDynamicPrediction.setSEP(ExcelUtil.getCellStringValue(row.getCell(9), i));
                    rtDynamicPrediction.setOCT(ExcelUtil.getCellStringValue(row.getCell(10), i));
                    rtDynamicPrediction.setNOV(ExcelUtil.getCellStringValue(row.getCell(11), i));
                    rtDynamicPrediction.setDEC(ExcelUtil.getCellStringValue(row.getCell(12), i));
                    rtDynamicPrediction.setId(UUID.randomUUID().toString());
                    rtDynamicPrediction.setCreateName(loginUser.getUsername());
                    rtDynamicPrediction.setCreateTime(new Date());
                    list.add(rtDynamicPrediction);
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
    public void saveBatch(List<RtDynamicPrediction> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            RtDynamicPrediction rtDynamicPrediction=list.get(i);
            String sql="delete from FIT_BACKLOG_DYNAMIC_PREDICTION where year='"+rtDynamicPrediction.getYear()+"' and SBU='"+rtDynamicPrediction.getSBU()+"'";
            rtDynamicPredictionDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            rtDynamicPredictionDao.save(list.get(i));
        }
    }
    /**下載數據*/
    public String downloadFile(String queryCondition,HttpServletRequest request,PageRequest pageRequest) throws IOException {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        String realPath = request.getRealPath("");
        String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"動態預估","Dynamic Prediction")+".xlsx";
        InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"bi"+File.separator+"動態預估.xlsx");
        XSSFWorkbook workBook = new XSSFWorkbook(ins);
        workBook.removeSheetAt(1);
        Sheet sheet = workBook.getSheetAt(0);
        String sql = "select year,sbu,jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec from FIT_BACKLOG_DYNAMIC_PREDICTION where 1=1";
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
        sql += " order by year,CREATE_TIME,id desc";
        System.out.println(sql);
        pageRequest.setPageSize(ExcelUtil.PAGE_SIZE);
        pageRequest.setPageNo(1);
        List<Object[]> dataList = rtDynamicPredictionDao.findPageBySql(pageRequest, sql).getResult();
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
                dataList = rtDynamicPredictionDao.findPageBySql(pageRequest, sql).getResult();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Object[] objects : dataList) {
                        Row contentRow = sheet.createRow(rowIndex++);
                        for (int i = 0; i < objects.length-1; i++) {
                            Cell cell = contentRow.createCell(i);
                            String text = (objects[i] != null ? objects[i].toString() : "");
                            if (StringUtils.isNotEmpty(text) &&i>1) {
                                cell.setCellValue(Double.parseDouble(text));
                            } else {
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
            String filePath=realPath+"static"+File.separator+"download"+File.separator+instrumentClassService.getLanguage(locale,"動態預估","Dynamic Prediction")+".xlsx";
            InputStream ins = new FileInputStream(realPath+"static"+File.separator+"template"+File.separator+"bi"+File.separator+"動態預估.xlsx");
            XSSFWorkbook workBook = new XSSFWorkbook(ins);
            XSSFCellStyle titleStyle = workBook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
            XSSFFont font = workBook.createFont();
            font.setColor(IndexedColors.WHITE.index);
            font.setBold(true);
            titleStyle.setFont(font);
            workBook.removeSheetAt(0);
            Sheet sheet = workBook.createSheet("SBU");
            sheet.createFreezePane(0, 1, 0, 1);
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("SBU");
            titleRow.getCell(0).setCellStyle(titleStyle);

            List sbuList = rtDynamicPredictionDao.listBySql("select distinct parent from fit_dimension where type='"+ EnumDimensionType.Entity+"' order by parent");
            for (int i=0;i<sbuList.size();i++) {
                Row row=sheet.createRow(i+1);
                row.createCell(0).setCellValue(sbuList.get(i).toString());
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