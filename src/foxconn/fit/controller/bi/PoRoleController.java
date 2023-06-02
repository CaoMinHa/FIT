package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.bi.PoRoleService;
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
 * @description 作爲審核模塊開發
 * @create 2021-04-20 16:04
 **/
@Controller
@RequestMapping("/bi/poRole")
public class PoRoleController extends BaseController {

    @Autowired
    private PoRoleService poRoleService;

    @RequestMapping(value = "index")
    public String index() {
        return "/bi/poRole/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,String name) {
        try {
            poRoleService.list(model,pageRequest,name);
        } catch (Exception e) {
            logger.error("查询角色數據失败:", e);
        }
        return "/bi/poRole/list";
    }
    @RequestMapping(value="/add")
    @ResponseBody
    @Log(name="角色管理-->添加")
    public String addRole(AjaxResult ajaxResult,@Log(name = "角色名稱") String rolename,@Log(name = "狀態")String flag,
                          @Log(name = "備注") String remark,@Log(name = "角色代碼") String roleCode,@Log(name = "角色等級") String roleGrade) {
        try {
            poRoleService.add(rolename,flag,remark,roleCode,roleGrade);
        } catch (Exception e) {
            logger.error("新增角色失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "新增角色失败(add role Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    @RequestMapping(value="/delete")
    @ResponseBody
    public String delete(AjaxResult ajaxResult,String id) {
        try {
            poRoleService.delete(id);
        } catch (Exception e) {
            logger.error("刪除角色失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除角色失败(delete role Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    @RequestMapping(value = "update")
    @ResponseBody
    @Log(name="角色管理-->修改")
    public String update(AjaxResult result,@Log(name = "更新条件") String updateData){
        try {
            poRoleService.update(updateData);
        } catch (Exception e) {
            logger.error("更新採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    @RequestMapping(value="/userList")
    public String userList(Model model, PageRequest pageRequest,String id,String name,String hasRole,String roleName) {
        try {
           poRoleService.userList(model,pageRequest,id,name,hasRole,roleName);
        } catch (Exception e) {
            logger.error("角色菜單下查询分配用戶信息失败:", e);
        }
        return "/bi/poRole/user";
    }

    //用户只能拥有同一角色等级的唯一
    @RequestMapping(value="/addUserRole")
    @ResponseBody
    @Log(name = "角色管理-->給用戶分配角色")
    public String addRoleUser(AjaxResult ajaxResult,@Log(name = "角色ID") String roleId,@Log(name = "用戶ID") String userId) {
        try {
            ajaxResult=poRoleService.addRoleUser(ajaxResult,roleId,userId);
        } catch (Exception e) {
            logger.error("給用戶分配角色失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "給用戶分配角色失敗(delete user role  Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    @RequestMapping(value="/removeUserRole")
    @ResponseBody
    @Log(name = "角色管理-->用戶取消角色")
    public String removeUserRole(AjaxResult ajaxResult,@Log(name = "角色ID") String roleId,@Log(name = "用戶ID") String userId) {
        try {
            ajaxResult=poRoleService.removeUserRole(ajaxResult,roleId,userId);
        } catch (Exception e) {
            logger.error("給用戶取消角色失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "給用戶取消角色失敗(delete user role  Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    /**为角色增加数据处理权限**/
    @RequestMapping(value="/updatePerms")
    @ResponseBody
    @Log(name = "角色管理--數據權限操作>")
    public String addPerms(AjaxResult ajaxResult,@Log(name = "id") String id,@Log(name = "權限編號") String perms) {
        try {
           poRoleService.addPerms(id,perms);
        } catch (Exception e) {
            logger.error("修改角色数据处理权限失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "修改角色数据处理权限失败(update role perms Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    /**
      通过角色id查询对应权限
     **/
    @RequestMapping(value="/findPerms")
    @ResponseBody
    public String findPermsByRoleId(AjaxResult ajaxResult, HttpServletRequest request,String id) {
        try {
          ajaxResult=poRoleService.findPermsByRoleId(ajaxResult,request,id);
        } catch (Exception e) {
            logger.error("修改角色数据处理权限失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "修改角色数据处理权限失败(update role perms Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }
}