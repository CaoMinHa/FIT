package foxconn.fit.controller.bi;

import com.monitorjbl.xlsx.StreamingReader;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.PlMappingService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Controller
@RequestMapping("/bi/plMapping")
public class PlMappingController extends BaseController {

    @Autowired
    private PoTableService poTableService;

    @Autowired
    private PlMappingService mappingService;

    @RequestMapping(value = "index")
    public String index() {
        return "/bi/plMapping/index";
    }

    @RequestMapping(value = "/query")
    @ResponseBody
    public String query(AjaxResult result,HttpServletRequest request,String type){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            List<Map> map=mappingService.selectQuery(type,locale);
            result.put("queryList", map);
        }catch (Exception e){
            logger.error("查询採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }
    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request,String queryCondition,String type) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            String sql = mappingService.selectDataSql(queryCondition,locale,model,type);
            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
            model.addAttribute("tableType", type);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/plMapping/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "三表補錄-->上传")
    public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name = "表名")String tableName) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
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
                if (!"xlsx".equals(suffix)) {
                    result.put("flag", "fail");
                    result.put("msg", getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
                    return result.getJson();
                }
                Workbook wk = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(file.getInputStream());
                Sheet sheet = wk.getSheetAt(0);
                String str =mappingService.uploadFile(sheet,result,locale,tableName);
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
    @Log(name = "三表補錄-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
            @Log(name = "查询条件") String queryCondition,String tableName) {
        try {
            String fileName=mappingService.downloadFile(queryCondition,tableName,request,pageRequest);
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
    public synchronized String template(HttpServletRequest request, HttpServletResponse response, AjaxResult result,String tableName) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
            result =mappingService.template(result,request,tableName);
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public String deleteAll(AjaxResult ajaxResult, HttpServletRequest request, String no,String tableName) {
        ajaxResult= mappingService.deleteData(ajaxResult,no,tableName);
        return ajaxResult.getJson();
    }
}
