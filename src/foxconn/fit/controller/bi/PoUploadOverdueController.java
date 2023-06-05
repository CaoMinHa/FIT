package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoUploadOverdueService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@RequestMapping("/bi/poUploadOverdue")
public class PoUploadOverdueController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PoUploadOverdueService poUploadOverdueService;

    @RequestMapping(value = "index")
    public String index(Model model) {
        try {
            model=poUploadOverdueService.roleList(model);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poUploadOverdue/index";
    }

    @RequestMapping(value="/list")
    public String userList(Model model,PageRequest pageRequest,HttpServletRequest request,String query) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            pageRequest.setOrderBy("ID");
            Page<Object[]> page = userService.findPageBySql(pageRequest,poUploadOverdueService.selectSql(query));
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", "FIT_USER");
            model.addAttribute("page", page);
            model.addAttribute("tableList", poUploadOverdueService.selectTabel(locale));
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poUploadOverdue/list";
    }

    @RequestMapping(value="/allAllocate")
    @ResponseBody
    @Log(name = "數據上傳逾期處理-->多個取消")
    public String allAllocate(AjaxResult ajaxResult,@Log(name = "用戶ID") String userId,@Log(name = "操作") String type){
        try {
            poUploadOverdueService.updateState(userId,type);
        } catch (Exception e) {
            logger.error("分配失敗:", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg","操作失敗 : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    @RequestMapping(value="/allocate")
    @ResponseBody
    @Log(name = "數據上傳逾期處理-->單個分配/取消")
    public String allocate(AjaxResult ajaxResult,@Log(name = "分配詳情") String updateData){
        try {
            poUploadOverdueService.updateState(updateData);
        } catch (Exception e) {
            logger.error("分配失敗:", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg","操作失敗 : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }
}