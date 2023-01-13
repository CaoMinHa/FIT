package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.User;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoUploadOverdueService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/bi/poUploadOverdue")
public class PoUploadOverdueController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PoUploadOverdueService poUploadOverdueService;

    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            pageRequest.setOrderBy("serial");
            pageRequest.setOrderDir("asc");
            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
            model=poUploadOverdueService.roleList(model);
            model.addAttribute("attribute", user.getAttribute());
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poUploadOverdue/index";
    }

    @RequestMapping(value="/list")
    public String userList(Model model,PageRequest pageRequest,HttpServletRequest request,String query) {
        try {
            pageRequest.setOrderBy("ID");
            Page<Object[]> page = userService.findPageBySql(pageRequest,poUploadOverdueService.selectSql(query));
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", "FIT_USER");
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poUploadOverdue/list";
    }

    @RequestMapping(value="/allocate")
    @ResponseBody
    @Log(name = "數據上傳逾期處理-->分配/取消")
    public String delete(HttpServletRequest request,AjaxResult ajaxResult,@Log(name = "用戶ID") String userId,@Log(name = "操作") String type){
        try {
            poUploadOverdueService.updateState(userId,type);
        } catch (Exception e) {
            logger.error("删除失败:", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg","操作失敗 : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }
}