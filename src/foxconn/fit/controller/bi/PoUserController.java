package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.PoUserService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 采购的用户维护请求层
 * @create 2021-06-09 14:57
 **/
@Controller
@RequestMapping("/bi/poUser")
public class PoUserController extends BaseController {

    @Autowired
    private PoUserService poUserService;

    @RequestMapping(value = "index")
    public String index() {
        return "/bi/poUser/index";
    }


    @RequestMapping(value="/list")
    public String userList(Model model, PageRequest pageRequest,String name,String username) {
        try {
            poUserService.list(model, pageRequest, name, username);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poUser/list";
    }

    /**獲取大類及SBU維度值**/
    @RequestMapping(value="/commodityAndSbu")
    @ResponseBody
    public String userHasCommodityMajor(AjaxResult ajaxResult, HttpServletRequest request,String userId) {
        try {
            poUserService.userHasCommodityMajor(ajaxResult, request, userId);
        } catch (Exception e) {
            logger.error("用戶管理維護信息時獲取大類及SBU維度數據失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "用戶更新界面加載失敗: " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @Log(name = "用戶管理-->修改用戶信息")
    public String updateUser(AjaxResult result,@Log(name = "ID") String id, @Log(name = "SBU") String sbu,@Log(name = "郵箱")String email,
                             @Log(name = "大類") String commodity,@Log(name = "真實姓名") String realname){
        try {
            result=poUserService.updateUser(result, id, sbu, email, commodity, realname);
        } catch (Exception e) {
            logger.error("維護用戶失敗", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }
}