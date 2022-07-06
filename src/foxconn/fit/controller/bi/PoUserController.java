package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.User;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoRoleService;
import foxconn.fit.service.bi.PoRoleUserService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private UserService userService;
    @Autowired
    private PoRoleService poRoleService;
    @Autowired
    private PoRoleUserService poRoleUserService;
    @Autowired
    private PoTableService poTableService;


    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            pageRequest.setOrderBy("serial");
            pageRequest.setOrderDir("asc");
            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
            model.addAttribute("attribute", user.getAttribute());

        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poUser/index";
    }


    @RequestMapping(value="/list")
    public String userList(Model model, PageRequest pageRequest,HttpServletRequest request,String name,String username) {
        try {
            String sql="select distinct ID , USERNAME ,REALNAME, ENABLE ,sbu,COMMODITY_MAJOR,email,TYPE,CREATOR,create_time " +
                    " from FIT_USER where 1=1 ";
            if(!StringUtils.isBlank(name)){
                name="%"+name+"%";
                sql=sql+" and USERNAME like "+"'"+name.trim()+"'";
            }
            if(!StringUtils.isBlank(username)){
                username="%"+username+"%";
                sql=sql+" and REALNAME like "+"'"+username.trim()+"'";
            }
            sql+=" order by REALNAME";
            System.out.println(sql);
            Page<Object[]> page = poRoleService.findPageBySql(pageRequest, sql);
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
        return "/bi/poUser/list";
    }

    /*
     * 用户维护物料大类
     * */

    @RequestMapping(value="/commodityAndSbu")
    @ResponseBody
    public String userHasCommodityMajor(AjaxResult ajaxResult, HttpServletRequest request,String userId) {
        try {

            String userSql=" select Commodity_Major,SBU,REALNAME,email from FIT_user where id="+"'"+userId+"'";
            List<Map> map= poRoleService.listMapBySql(userSql);
            List<String> commodityList = poTableService.listBySql("select distinct tie.COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING tie order by tie.COMMODITY_NAME");
//            List<String> sbuList = poTableService.listBySql("select   distinct  SBU from EPMEBS.CUX_SBU_BU_MAPPING order by SBU ");
            List<String> sbuList = poTableService.listBySql("select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie order by tie.NEW_SBU_NAME ");
            ajaxResult.put("cList",commodityList);
            ajaxResult.put("map",map);
            ajaxResult.put("sList",sbuList);
        } catch (Exception e) {
            logger.error("給用戶分配角色失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "給用戶分配角色失敗(delete user role  Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public String updateUser(HttpServletRequest request,
                             HttpServletResponse response,
                             AjaxResult result,String id,
                             String sbu,
                             String email,
                             String commodity,
                             String realname){
        try {
            if (StringUtils.isNotEmpty(id)) {
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String user = loginUser.getUsername();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet = df.format(new Date());
                String updateSql = "update FIT_USER set ";
                updateSql += " sbu=" + "'" + sbu + "',";
                updateSql += " COMMODITY_MAJOR=" + "'" + commodity + "',";
                updateSql += " email=" + "'" + email + "',";
                updateSql += " realname=" + "'" + realname + "',";
                updateSql = updateSql.substring(0, updateSql.length() - 1);
                updateSql += " where id=" + "'" + id + "'";
                poRoleService.updateData(updateSql);
            }else{
                result.put("flag", "fail");
                result.put("msg", "維護用戶失敗");
            }
        } catch (Exception e) {
            logger.error("維護用戶失敗", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }
}