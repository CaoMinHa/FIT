package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.RtIntegrationService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@RequestMapping("/bi/rtIntegration")
public class RtIntegrationController extends BaseController {
    @Autowired
    private RtIntegrationService rtIntegrationService;


    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            rtIntegrationService.index(model,request);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/rtIntegration/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request, String tableName,String queryCondition) {
        try {
            String sql =rtIntegrationService.list(model,request,tableName,queryCondition);
            Page<Object[]> page = rtIntegrationService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/rtIntegration/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "營收目標-->上传")
    public String upload(HttpServletRequest request, AjaxResult result, @Log(name = "明细表名称") String[] tableNames) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        try {
            Assert.isTrue(tableNames != null && tableNames.length > 0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            rtIntegrationService.upload(request,result,tableNames);
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }


    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "營收目標-->下载")
    public synchronized String download(HttpServletRequest request,PageRequest pageRequest, AjaxResult result, @Log(name = "明细表名称") String tableNames,
            @Log(name = "条件") String queryCondition) {
        try {
            result=rtIntegrationService.download(request,pageRequest,result,tableNames,queryCondition);
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
     *
     * @param request
     * @param result
     * @param tableNames
     * @return
     */
    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request,AjaxResult result, String tableNames) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            if (tableNames.endsWith(",")) {
                tableNames = tableNames.substring(0, tableNames.length() - 1);
            }
            result=rtIntegrationService.template(request,result,tableNames,locale);
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    @Log(name = "營收目標-->單條刪除")
    public String deleteAll(AjaxResult ajaxResult, String id, @Log(name="表名") String tableName) {
        try {
            rtIntegrationService.delete(id,tableName);
        } catch (Exception e) {
            logger.error("刪除失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }



    @RequestMapping(value = "queryCondition")
    @ResponseBody
    public String queryMasterData(HttpServletRequest request,AjaxResult result,String tableName){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale,"输入表不能为空","Master data can not be null"));
            result=rtIntegrationService.queryMasterData(result,tableName);
        } catch (Exception e) {
            logger.error("查询映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

}
