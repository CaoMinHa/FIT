package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@RequestMapping("/bi/poIntegration")
public class PoIntegrationController extends BaseController {

    @Autowired
    private PoTableService poTableService;


    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        model=poTableService.index(model,request);
        return "/bi/poIntegration/index";
    }


    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "採購模块-->上传")
    public String upload(HttpServletRequest request, AjaxResult result, @Log(name = "明细表名称") String tableNamesOut1) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        Assert.isTrue(tableNamesOut1 != null && tableNamesOut1.length() > 0,getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
        HttpSession session = request.getSession();
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        return poTableService.update(session,multipartHttpServletRequest,locale,result,tableNamesOut1);
    }

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "採購模块-->下载")
    public synchronized String download(HttpServletRequest request, PageRequest pageRequest, AjaxResult result,
                                        String dateYear,
                                        String date, String dateEnd, String tableName,String flag,
                                        String poCenter, String sbuVal, String priceControl,String commodity,String founderVal) {
        try {
            result.put("fileName", poTableService.download(request,pageRequest,dateYear,date,dateEnd,tableName,flag,
                    poCenter,sbuVal,priceControl,commodity,founderVal));
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }


    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request, AjaxResult result, String tableName) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
            result.put("fileName", poTableService.template(request,tableName,locale));
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }
}
