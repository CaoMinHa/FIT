package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.bi.PmrCommonService;
import foxconn.fit.util.ExceptionUtil;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Controller
@RequestMapping("/bi/pmrCapexActualBudget")
public class PmrCapexActualBudgetController extends BaseController {

    @Autowired
    private PmrCommonService pmrCommonService;

    private static String tableName="PMR_CAPEX_Actual_Budget";

    /**
     * 獲取查詢字段
     */
    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            List<Map> list=pmrCommonService.selectQuery(locale,tableName);
            model.addAttribute("queryList",list);
        }catch (Exception e){
            logger.error("查詢頁面條件結果集失敗！", e);
        }
        return "/bi/pmrCapexActualBudget/index";
    }

    /**
     * 高級查詢 CAPEX Actual vs Budget
     * @param model
     * @param pageRequest
     * @param request
     * @param queryCondition 查詢條件
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            PoTable poTable = pmrCommonService.get(tableName);
            String sql = pmrCommonService.selectDataSql(queryCondition,locale,model,poTable);
            pageRequest.setPageSize(15);
            Page<Object[]> page = pmrCommonService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/pmrCapexActualBudget/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name="CAPEX Actual vs Budget-->上傳")
    public String upload(HttpServletRequest request, AjaxResult result) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            if (mutipartFiles != null && mutipartFiles.size() > 0) {
                MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];
                PoTable poTable = pmrCommonService.get(tableName);
                String str =pmrCommonService.uploadFile(poTable,file,result,locale);
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
    @Log(name="CAPEX Actual vs Budget-->下載")
    public synchronized String download(HttpServletRequest request, PageRequest pageRequest, AjaxResult result,
            @Log(name = "下載條件") String queryCondition) {
        try {
            PoTable poTable = pmrCommonService.get(tableName);
            String fileName=pmrCommonService.downloadFile(queryCondition,poTable,request,pageRequest);
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
    public synchronized String template(HttpServletRequest request, AjaxResult result) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
            XSSFWorkbook workBook = new XSSFWorkbook();
            PoTable poTable = pmrCommonService.get(tableName);
            File outFile =pmrCommonService.template(workBook,poTable,request);
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
}
