package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.PoFlowService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 作爲采購審核模塊開發
 * @create 2021-04-20 16:04
 **/
@Controller
@RequestMapping("/bi/poFlow")
public class PoFlowController extends BaseController {

    @Autowired
    private PoFlowService poFlowService;


    @RequestMapping(value = "index")
    public String index() {
        return "/bi/poFlow/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String date,String tableName) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            model=poFlowService.list(model,tableName,date,pageRequest);
            model.addAttribute("year", date);
            model.addAttribute("tableName", tableName);
        } catch (Exception e) {
            logger.error("查询SBU年度CD目標核准列表失败:", e);
        }
        return "/bi/poFlow/list";
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @Log(name = "SBU年度CD目標核准表单条更新")
    public String update(AjaxResult result,  String tableName, @Log(name = "更新条件") String updateData){
        try {
            poFlowService.updateData(updateData,tableName);
        } catch (Exception e) {
            logger.error("更新採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    @RequestMapping(value = "updateAll")
    @ResponseBody
    @Log(name = "SBU年度CD目標核准表全部更新")
    public String updateAll(AjaxResult result,String tableName, @Log(name = "更新数据") String updateData){
        try {
            poFlowService.updateDataAll(updateData,tableName);
        } catch (Exception e) {
            logger.error("更新採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }
}