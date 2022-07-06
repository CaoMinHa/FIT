package foxconn.fit.controller.bi;

import com.monitorjbl.xlsx.StreamingReader;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.service.bi.RtHistoricalDataService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/bi/rtHistoricalData")
public class RtHistoricalDataController extends BaseController {

    @Autowired
    private PoTableService poTableService;

    @Autowired
    private RtHistoricalDataService rtHistoricalDataService;

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            List<Map> list=rtHistoricalDataService.selectQuery(request);
            model.addAttribute("queryList",list);
        }catch (Exception e){
            logger.error("查詢頁面條件結果集失敗！", e);
        }
        return "/bi/rtHistoricalData/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            PoTable poTable = poTableService.get("IF_EBS_AR_REVENUE_DTL_MANUAL");
            String sql = rtHistoricalDataService.selectDataSql(queryCondition,poTable,locale,model);
            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/rtHistoricalData/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "手工版營收歷史數據表-->上传")
    public String upload(HttpServletRequest request, HttpServletResponse response, AjaxResult result) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        String tableName = "IF_EBS_AR_REVENUE_DTL_MANUAL" ;
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
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
                if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
                    result.put("flag", "fail");
                    result.put("msg", getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
                    return result.getJson();
                }
                Workbook wk = StreamingReader.builder()
                        .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                        .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                        .open(file.getInputStream());  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
                Sheet sheet = wk.getSheetAt(0);
                PoTable poTable = poTableService.get(tableName);
                String str =rtHistoricalDataService.uploadFile(poTable,sheet,result,locale);
                return str;
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
    @Log(name = "手工版營收歷史數據表-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
            @Log(name = "查询条件") String queryCondition) {
        try {
            PoTable poTable = poTableService.get("IF_EBS_AR_REVENUE_DTL_MANUAL");
            String fileName=rtHistoricalDataService.downloadFile(queryCondition,poTable,request,pageRequest);
            result.put("fileName",fileName);
            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    /**
     * 下載模板
     */
    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request, HttpServletResponse response, AjaxResult result) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
            XSSFWorkbook workBook = new XSSFWorkbook();
            String tableName = "IF_EBS_AR_REVENUE_DTL_MANUAL";
            PoTable poTable = poTableService.get(tableName);
            File outFile =rtHistoricalDataService.template(workBook,poTable,request);
            OutputStream out = new FileOutputStream(outFile);
            workBook.write(out);
            workBook.close();
            out.flush();
            out.close();
            result.put("fileName", outFile.getName());
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public String deleteAll(AjaxResult ajaxResult, HttpServletRequest request, String no) {
        ajaxResult= rtHistoricalDataService.deleteData(ajaxResult,no);
        return ajaxResult.getJson();
    }
}
