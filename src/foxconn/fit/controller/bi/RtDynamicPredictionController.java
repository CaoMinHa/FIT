package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.RtDynamicPredictionService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Controller
@RequestMapping("/bi/rtDynamicPrediction")
public class RtDynamicPredictionController extends BaseController {
    
    @Autowired
    private RtDynamicPredictionService rtDynamicPredictionService;

    @RequestMapping(value = "index")
    public String index() {
        return "/bi/rtDynamicPrediction/index";
    }

    @RequestMapping(value = "/list")
    @Log(name="動態預估-->查詢")
    public String list(Model model, PageRequest pageRequest,@Log(name = "條件") String queryCondition) {
        try {
            String sql = rtDynamicPredictionService.selectDataSql(queryCondition);
            Page<Object[]> page = rtDynamicPredictionService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查詢數據失敗(Failed to query data)", e);
        }
        return "/bi/rtDynamicPrediction/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "動態預估-->上传")
    public String upload(HttpServletRequest request, AjaxResult result,@Log(name = "上传年份") String year) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                String str =rtDynamicPredictionService.uploadFile(multipartHttpServletRequest,result,locale,year);
                return str;
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "動態預估-->下载")
    public synchronized String download(HttpServletRequest request, PageRequest pageRequest, AjaxResult result,
            @Log(name = "查询条件") String queryCondition) {
        try {
            String fileName=rtDynamicPredictionService.downloadFile(queryCondition,request,pageRequest);
            result.put("fileName",fileName);
            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }


    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request, AjaxResult result) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        Map<String,String> map=rtDynamicPredictionService.template(request,locale);
        if(map.get("result").equals("Y")){
            result.put("fileName", map.get("file"));
        }else{
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
        }
        return result.getJson();
    }

}
