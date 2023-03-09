package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.RgpIntegrationService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequestMapping("/bi/rgpIntegration")
public class RgpIntegrationController extends BaseController {
    @Autowired
    private RgpIntegrationService rgpIntegrationService;


    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            List<PoTable> tableList = new ArrayList<PoTable>();
            for (PoTable poTable : rgpIntegrationService.index()) {
                tableList.add(new PoTable(poTable.getTableName(),getByLocale(locale, poTable.getComments()),poTable.getUploadFlag()));
            }
            model.addAttribute("poTableList", tableList);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/rgpIntegration/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request, String tableName,String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            PoTable poTable = rgpIntegrationService.get(tableName);
            List<PoColumns> columns = poTable.getColumns();
            Page<Object[]> page = rgpIntegrationService.findPageBySql(pageRequest, rgpIntegrationService.list(columns,poTable,queryCondition,locale));
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            if("N".equals(poTable.getUploadFlag())){
                model.addAttribute("hidden", 1);
            }else{
                model.addAttribute("hidden", 2);
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", poTable.getTableName());
            model.addAttribute("page", page);
            model.addAttribute("columns", columns);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/rgpIntegration/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "营收毛利-->上传")
    public String upload(HttpServletRequest request, AjaxResult result, @Log(name = "明细表名称") String[] tableNames) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        try {
            Assert.isTrue(tableNames != null && tableNames.length > 0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            Map<PoTable, List<List<String>>> dataMap = new HashMap<PoTable, List<List<String>>>();
            if (mutipartFiles != null && mutipartFiles.size() > 0) {
                //文件基础格式校验
                result=rgpIntegrationService.uploadExcelCheck(mutipartFiles,request,result,tableNames);
                Map<Object,Object> map =result.getResult();
                if(map.get("flag").equals("success")){
                    String tableName = tableNames[0];
                    PoTable poTable = rgpIntegrationService.get(tableName);
                    List<List<String>> dataList= (List<List<String>>) map.get(tableName);
                    if (!dataList.isEmpty()) {
                        //上传数据校验
                    List<String> msg=rgpIntegrationService.outSourcingMaterialCheck(dataList);
                        if(!"成功".equals(msg.get(0))){
                            result.put("flag", "fail");
                            result.put("msg", getLanguage(locale, msg.get(0),msg.get(1)));
                            return result.getJson();
                        }else{
                            dataMap.put(poTable, dataList);
                            rgpIntegrationService.saveData(dataMap,msg.get(1));
                        }
                    } else {
                        result.put("flag", "fail");
                        result.put("msg", getLanguage(locale, "sheet無有效數據行", "The sheet has no valid data row"));
                        return result.getJson();
                    }
                }else{
                    return result.getJson();
                }
            } else {
                result.put("flag", "fail");
                result.put("msg", getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
            }
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "营收毛利-->下载")
    public synchronized String download(HttpServletRequest request, PageRequest pageRequest, AjaxResult result, @Log(name = "明细表名称") String tableNames,
            @Log(name = "查询条件") String queryCondition,String checkedVal) {
        OutputStream out=null;
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            XSSFWorkbook workBook = new XSSFWorkbook();
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workBook);
            rgpIntegrationService.downloadExcel(checkedVal,tableNames,locale,sxssfWorkbook,workBook,queryCondition,pageRequest);
            String fileName = tableNames;
            String tableNameSql = "select * from fit_po_table";
            List<PoTable> list = rgpIntegrationService.listBySql(tableNameSql, PoTable.class);
            for (int i = 0; i < list.size(); i++) {
                if (fileName.equalsIgnoreCase(list.get(i).getTableName())) {
                    fileName = list.get(i).getComments().split("_")[1];
                    break;
                }
            }
            File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + fileName + ".xlsx");
            out = new FileOutputStream(outFile);
            sxssfWorkbook.write(out);
            sxssfWorkbook.close();
            result.put("fileName", outFile.getName());
            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }finally {
            try {
                out.flush();
                out.close();
            }catch (Exception e){
                logger.error("营收毛利下載息失败", e);
            }
        }
        return result.getJson();
    }

    /**
     * 下載模板
     * @param request
     * @param result
     * @param tableNames
     * @return
     */
    @RequestMapping(value = "template")
    @ResponseBody
    @Log(name = "营收毛利下載模板")
    public synchronized String template(HttpServletRequest request, AjaxResult result, @Log(name="表名") String tableNames) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        OutputStream out=null;
        try {
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            if (tableNames.endsWith(",")) {
                tableNames = tableNames.substring(0, tableNames.length() - 1);
            }
            XSSFWorkbook workBook = new XSSFWorkbook();
            //单元列锁定
            XSSFCellStyle lockStyle = workBook.createCellStyle();
            lockStyle.setLocked(true);
            lockStyle.setAlignment(HorizontalAlignment.CENTER);
            lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            lockStyle.setFillForegroundColor(IndexedColors.WHITE.index);
            lockStyle.setBorderBottom(BorderStyle.THIN);
            lockStyle.setBorderRight(BorderStyle.THIN);
            lockStyle.setBorderLeft(BorderStyle.THIN);
            lockStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM,new XSSFColor(new java.awt.Color(212, 212, 212)));
            lockStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT,new XSSFColor(new java.awt.Color(212, 212, 212)));
            lockStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT,new XSSFColor(new java.awt.Color(212, 212, 212)));
            //	单元格不锁定的样式
            XSSFCellStyle unlockstyle = workBook.createCellStyle();
            unlockstyle.setLocked(false);

            String fileName =tableNames;
            PoTable poTable = rgpIntegrationService.get(tableNames);
            List<PoColumns> columns = poTable.getColumns();
            XSSFSheet sheet = workBook.createSheet(getByLocale(locale, poTable.getComments()));
            //处理基础表样
            rgpIntegrationService.getSheet(sheet,columns,locale,workBook);
            //统一先去掉表锁定
            for (short i = 0; i < columns.size();i++) {
                sheet.setDefaultColumnStyle(i, unlockstyle);
            }
            //根据字段锁定对应列
            //外购原料输入表
//            if ("CUX_RGP_OUTSOURCING_MATERIAL".equalsIgnoreCase(tableNames)) {
//                sheet.setDefaultColumnStyle(7,lockStyle);
//                sheet.setDefaultColumnStyle(8,lockStyle);
//            }
            //獲取實際表名
            List<PoTable> list = rgpIntegrationService.listBySql("select * from fit_po_table", PoTable.class);
            for (int i = 0; i < list.size(); i++) {
                if (fileName.equalsIgnoreCase(list.get(i).getTableName())) {
                    fileName = list.get(i).getComments().split("_")[1];
                    break;
                }
            }
            File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download/" + fileName + ".xlsx");
            out = new FileOutputStream(outFile);
            workBook.write(out);
            workBook.close();
            result.put("fileName", outFile.getName());
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }finally {
            try {
                out.flush();
                out.close();
            }catch (Exception e){
                logger.error("营收毛利下載模板失敗",e);
            }
        }
        return result.getJson();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    @Log(name="营收毛利刪除")
    public String deleteAll(AjaxResult ajaxResult, @Log(name = "id") String id,@Log(name = "表名") String tableName) {
        try {
            rgpIntegrationService.delete(id,tableName);
        } catch (Exception e) {
            logger.error("刪除" + tableName + "数据失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除数据失败(delete data Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }



    @RequestMapping(value = "queryCondition")
    @ResponseBody
    @Log(name = "营收毛利切換表獲取相應查詢條件")
    public String queryMasterData(HttpServletRequest request,AjaxResult result,@Log(name="表名") String tableName){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale,"输入表不能为空","Master data can not be null"));
            List<List<String>> a=rgpIntegrationService.queryMasterData(tableName,locale);
            result.put("queryList", a);
        } catch (Exception e) {
            logger.error("营收毛利獲取表查詢條件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    @RequestMapping(value = "calculate")
    @ResponseBody
    @Log(name = "营收毛利計算")
    public String calculate(HttpServletRequest request,AjaxResult result,@Log(name="期間") String period){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(period, getLanguage(locale,"期間不能为空","Period data can not be null"));
            String msg=rgpIntegrationService.calculate(period);
            result.put("msg", msg);
        } catch (Exception e) {
            logger.error("营收毛利獲取表查詢條件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }
}
