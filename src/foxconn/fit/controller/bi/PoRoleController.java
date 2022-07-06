package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;

import java.text.SimpleDateFormat;
import java.util.*;

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
            List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();
            propertyFilters.add(new PropertyFilter("EQS_type","PO"));
            propertyFilters.add(new PropertyFilter("EQS_uploadFlag","Y"));
            List<PoTable> poTableList = poTableService.listByHQL(propertyFilters, pageRequest);
            // List<PoTable> poTableList = poTableService.listBySql("SELECT * FROM fit_po_table fp WHERE 1=1 and fp.type='PO' and fp.upload_flag = 'Y' order by fp.serial",PoTable.class);

            List<PoTable> tableList=new ArrayList<PoTable>();
            for (PoTable poTable : poTableList) {
                tableList.add(new PoTable(poTable.getTableName(),getByLocale(locale, poTable.getComments())));
            }
            model.addAttribute("poTableList", tableList);
            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
            model.addAttribute("attribute", user.getAttribute());

        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poRole/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String name) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            String sql="select  ID , NAME ,CODE,GRADE, FLAG ,remark, create_user, create_time, " +
                    " UPDATE_USER, UPDTAE_TIME from FIT_PO_AUDIT_ROLE where DELETED='0'";
            if(!StringUtils.isBlank(name)){
                name="%"+name+"%";
                sql=sql+" and name like "+"'"+name+"'";
            }
            Page<Object[]> page = poRoleService.findPageBySql(pageRequest, sql);
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", "FIT_PO_AUDIT_ROLE");
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poRole/list";
    }
    @RequestMapping(value="/add")
    @ResponseBody
    public String addRole(AjaxResult ajaxResult, HttpServletRequest request,String rolename,String flag,String remark) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String user=loginUser.getUsername();
            String id=UUID.randomUUID().toString();
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet=df.format(new Date());
            String sql="insert into FIT_PO_AUDIT_ROLE (ID,NAME,FLAG,DELETED ,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,REMARK) values ( ";
            sql=sql+"'"+id+"',"+"'"+rolename+"',"+"'"+flag+"',"+"'0',"+"'"+user+"',"+"'"+signTimet+"',"+"'"+user+"',"+"'"+signTimet+"',"+
                    "'"+remark+"'"+")";
           poRoleService.updateData(sql);
        } catch (Exception e) {
            logger.error("新增角色失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "新增角色失败(add role Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    @RequestMapping(value="/delete")
    @ResponseBody
    public String deleteRole(AjaxResult ajaxResult, HttpServletRequest request,String id) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String user=loginUser.getUsername();
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet=df.format(new Date());
            String[] ids = id.split(",");
            String whereSql="";
            for (String s : ids) {
                whereSql=whereSql+"'"+s+"',";
            }
            whereSql=whereSql.substring(0,whereSql.length()-1);
            String sql="UPDATE  FIT_PO_AUDIT_ROLE set DELETED='1',"+"UPDATE_USER="+"'"+user+"',"+"UPDTAE_TIME="+"'"+signTimet+"'"+" where id in ("+whereSql+")";
            poRoleService.updateData(sql);
        } catch (Exception e) {
            logger.error("刪除角色失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除角色失败(delete role Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    @RequestMapping(value = "update")
    @ResponseBody
    public String update(HttpServletRequest request,HttpServletResponse response,AjaxResult result,@Log(name = "更新条件") String updateData){
        try {
            if (StringUtils.isNotEmpty(updateData)) {
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String user=loginUser.getUsername();
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet=df.format(new Date());
                String updateSql="update FIT_PO_AUDIT_ROLE  set ";
                String where="";
                String[] params = updateData.split("&");
                for (String param : params) {
                    String columnName = param.substring(0,param.indexOf("="));
                    String columnValue = param.substring(param.indexOf("=")+1).trim();
                    if ("ID".equalsIgnoreCase(columnName)) {
                        where=" where ID='"+columnValue+"'";
                    }else{
                        if (StringUtils.isNotEmpty(columnValue)) {
                            updateSql+=columnName+"='"+columnValue+"',";
                        }
                    }
                }
                updateSql=updateSql+"UPDATE_USER="+"'"+user+"',"+"UPDTAE_TIME="+"'"+signTimet+"'";
                updateSql+=where;
                poRoleService.updateData(updateSql);

            }
        } catch (Exception e) {
            logger.error("更新採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }
    @RequestMapping(value = "updateUser")
    @ResponseBody
    public String updateUser(HttpServletRequest request,HttpServletResponse response,AjaxResult result,@Log(name = "更新条件") String updateData){
        try {
            if (StringUtils.isNotEmpty(updateData)) {
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String user=loginUser.getUsername();
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet=df.format(new Date());
                String updateSql="update FIT_USER  set  ";
                String where="";
                String[] params = updateData.split("&");
                for (String param : params) {
                    String columnName = param.substring(0,param.indexOf("="));
                    String columnValue = param.substring(param.indexOf("=")+1).trim();
                    if ("ID".equalsIgnoreCase(columnName)) {
                        where=" where ID='"+columnValue+"'";
                    }else{
                        if (StringUtils.isNotEmpty(columnName)) {
                            updateSql+=columnName+"='"+columnValue+"',";
                        }else{

                        }
                    }
                }
                updateSql=updateSql.substring(0,updateSql.length()-1);
                updateSql+=where;
                poRoleService.updateData(updateSql);

            }
        } catch (Exception e) {
            logger.error("更新採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }



    @RequestMapping(value="/userList")
    public String userList(Model model, PageRequest pageRequest,HttpServletRequest request,String id,String name,String hasRole,String roleName) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            String sql="select distinct u.ID , u.USERNAME ,u.REALNAME, u.CREATOR, u.create_time " +
                    " from FIT_USER u  left join FIT_PO_AUDIT_ROLE_USER ur \n" +
                    " on u.id=ur.user_id  " +
                    "  where not exists " +
                    " (select 1 from FIT_PO_AUDIT_ROLE_USER r " +
                    " where u.id = r.user_id and r.role_id ="+ "'"+id+"')"+
                    " and u.USERNAME like "+"'"+"%"+name.trim()+"%'";
            if("1".equals(hasRole)){
                sql="select  distinct u.ID , u.USERNAME ,u.REALNAME, u.CREATOR, u.create_time from FIT_USER u  left join FIT_PO_AUDIT_ROLE_USER ur \n" +
                        " on u.id=ur.user_id where ur.role_id = "+ "'"+id+"'"+
                        " and u.USERNAME like "+"'"+"%"+name.trim()+"%'";
            }
            Page<Object[]> page = poRoleService.findPageBySql(pageRequest, sql);
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", "FIT_USER");
            model.addAttribute("roleId", id);
            model.addAttribute("roleName", roleName);
            model.addAttribute("hasRole", hasRole);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poRole/user";
    }

    //用户只能拥有同一角色等级的唯一
    @RequestMapping(value="/addUserRole")
    @ResponseBody
    public String addRoleUser(AjaxResult ajaxResult, HttpServletRequest request,String roleId,String userId) {
        try {
            String[] ids = userId.split(",");
            String whereSql="";
            for (String s : ids) {
                whereSql+="'"+s+"',";
            }
            whereSql=whereSql.substring(0,whereSql.length()-1);
            String roleSql=" select Name,GRADE from FIT_PO_AUDIT_ROLE where id="+"'"+roleId+"'";
            List<Map> maps = poRoleService.listMapBySql(roleSql);
            //角色等级
            String grade=maps.get(0).get("GRADE").toString();
            String countSql="  select count(ur.id) as COUNT from FIT_PO_AUDIT_ROLE_USER ur  " +
                    "left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id " +
                    " where r.grade="+"'"+grade+"' and ur.user_id in ("+whereSql+")";
            List<Map> countMaps = poRoleService.listMapBySql(countSql);
            if(countMaps!=null&&"1".equals(countMaps.get(0).get("COUNT").toString())){
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "处于同一级别的角色，一个用户只能拥有一个");
            }else{
                if(maps!=null&maps.size()==1){
                    String name=maps.get(0).get("NAME").toString();
                    if(name.indexOf("cpo")!=-1){
                        poRoleUserService.addCpoMenu(roleId,ids);
                    }else{
                        for (int i = 0; i < ids.length; i++) {
                            String uuId=UUID.randomUUID().toString();
                            String sql="insert into FIT_PO_AUDIT_ROLE_USER (ID,ROLE_ID,USER_Id) values ( ";
                            sql=sql+"'"+uuId+"',"+"'"+roleId+"',"+"'"+ids[i]+"')";
                            System.out.println(sql);
                            poRoleUserService.updateData(sql);
                        }
                    }

                }
                else{
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "未找到對應的角色數據( not found role )");
                }
            }

        } catch (Exception e) {
            logger.error("給用戶分配角色失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "給用戶分配角色失敗(delete user role  Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    @RequestMapping(value="/removeUserRole")
    @ResponseBody
    public String removeUserRole(AjaxResult ajaxResult, HttpServletRequest request,String roleId,String userId) {
        try {
                String[] ids = userId.split(",");
                String roleSql=" select Name from FIT_PO_AUDIT_ROLE where id="+"'"+roleId+"'";
                List list = poRoleService.listBySql(roleSql);
                if(list!=null&list.size()==1){
                  String name=list.get(0).toString();
                       if(name.indexOf("cpo")!=-1){
                           poRoleUserService.rmoveCpoMenu(userId,roleId);
                          }else{
                           String sql="delete from FIT_PO_AUDIT_ROLE_USER where user_id="+"'"+userId+"'"+" and role_id="+"'"+roleId+"'";
                           poRoleUserService.updateData(sql);
                          }
                }else{
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "未找到對應的角色數據( not found role )");
                }

        } catch (Exception e) {
            logger.error("給用戶取消角色失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "給用戶取消角色失敗(delete user role  Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    /*
      为角色增加数据处理权限
     */
    @RequestMapping(value="/updatePerms")
    @ResponseBody
    public String addPerms(AjaxResult ajaxResult, HttpServletRequest request,String id,String perms) {
        try {
            String sql="update FIT_PO_AUDIT_ROLE  set TABLE_PERMS="+"'"+perms+"'"+" where id="+"'"+id+"'";
            poRoleService.updateData(sql);
        } catch (Exception e) {
            logger.error("修改角色数据处理权限失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "修改角色数据处理权限失败(update role perms Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    /*
      通过角色id查询对应权限
     */
    @RequestMapping(value="/findPerms")
    @ResponseBody
    public String findPermsByRoleId(AjaxResult ajaxResult, HttpServletRequest request,String id) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        try {
//            PageRequest pageRequest=new PageRequest();
            List<Map<String,String>> tableList=new ArrayList<Map<String,String>>();
            List<String> tableList1=poTableService.listBySql("select table_name from FIT_PO_TABLE a where type in('PO','CPO') order by a.upload_flag desc");
//            List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();
//            propertyFilters.add(new PropertyFilter("EQS_type","PO"));
//            propertyFilters.add(new PropertyFilter("EQS_uploadFlag","Y"));
            List<String> perms = poRoleService.listBySql("select TABLE_PERMS from FIT_PO_AUDIT_ROLE where id="+"'"+id+"'");
            List<Map> poTableList = poTableService.listMapBySql("select a.table_name,a.comments,b.role_id,b.buttons_type from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b where a.table_name=b.form_name and type in('PO','CPO') order by a.table_name,b.buttons_type");
//            List<PoTable> poTableList = poTableService.listByHQL(propertyFilters, pageRequest);
//            PoTable cpo = new PoTable("FIT_PO_Target_CPO_CD_DTL", "採購CD 目標核准表");
//            poTableList.add(cpo);
            if(perms.get(0)!=null){
                    String[] split = perms.get(0).split(",");
                    for (Map poTable : poTableList) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name",poTable.get("TABLE_NAME").toString());
                        map.put("comment",getByLocale(locale, poTable.get("COMMENTS").toString()));
                        map.put("roleId",poTable.get("ROLE_ID").toString());
                        map.put("type",poTable.get("BUTTONS_TYPE").toString());
                        map.put("flag","0");
                        for (int i = 0; i < split.length; i++) {
                            if(split[i].equals(poTable.get("ROLE_ID").toString())){
                                map.put("flag","1");
                                break;
                            }
                        }
                        tableList.add(map);
                    }

                }else{
                    for (Map poTable : poTableList) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name",poTable.get("TABLE_NAME").toString());
                        map.put("comment",getByLocale(locale, poTable.get("COMMENTS").toString()));
                        map.put("roleId",poTable.get("ROLE_ID").toString());
                        map.put("type",poTable.get("BUTTONS_TYPE").toString());
                        map.put("flag","0");
                        tableList.add(map);
                    }
                }
               ajaxResult.put("poTableList", tableList);
               ajaxResult.put("poTableList1",tableList1);
        } catch (Exception e) {
            logger.error("修改角色数据处理权限失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "修改角色数据处理权限失败(update role perms Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }



    /*
    * 用户维护物料大类
    * */

    @RequestMapping(value="/commodityMajor")
    @ResponseBody
    public String userHasCommodityMajor(AjaxResult ajaxResult, HttpServletRequest request,String userId) {
        try {

            String userSql=" select Commodity_Major from FIT_user where id="+"'"+userId+"'";
            List<String>  commodityMajors= poRoleService.listBySql(userSql);
            List<String> commodityList = poTableService.listBySql("select distinct tie.COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING tie order by tie.COMMODITY_NAME");
            List<String> sbuList = poTableService.listBySql("select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie order by tie.NEW_SBU_NAME ");
//            if(commodityMajors!=null&&commodityMajors.size()==1&&commodityMajors.get(0)!=null){
//
//            }
            ajaxResult.put("cList",commodityList);
            ajaxResult.put("sList",sbuList);
        } catch (Exception e) {
            logger.error("給用戶分配角色失敗", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "給用戶分配角色失敗(delete user role  Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }





}