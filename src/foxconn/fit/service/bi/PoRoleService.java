package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoRoleDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoRole;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoRoleService extends BaseService<PoRole> {

    @Autowired
    private PoRoleDao poRoleDao;

    @Autowired
    private InstrumentClassService instrumentClassService;

    @Override
    public BaseDaoHibernate<PoRole> getDao() {
        return poRoleDao;
    }


    private void updateData(String updateSql) {
        System.out.println(updateSql);
        poRoleDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }
    /**數據查詢**/
    public void list(Model model, PageRequest pageRequest, String name){
        String sql="select  ID , NAME  ,remark, create_user, create_time, " +
                " UPDATE_USER, UPDTAE_TIME from FIT_PO_AUDIT_ROLE where DELETED='0'";
        if(!StringUtils.isBlank(name)){
            name="%"+name+"%";
            sql=sql+" and name like "+"'"+name+"'";
        }
        pageRequest.setOrderBy("ID,CODE");
        Page<Object[]> page = poRoleDao.findPageBySql(pageRequest, sql);
        int index=1;
        if(pageRequest.getPageNo()>1){
            index=2;
        }
        model.addAttribute("index", index);
        model.addAttribute("tableName", "FIT_PO_AUDIT_ROLE");
        model.addAttribute("page", page);
    }
    /**數據添加**/
    public void add( String rolename,String flag, String remark,String roleCode,String roleGrade){
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String user=loginUser.getUsername();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String signTimet=df.format(new Date());
        String sql="insert into FIT_PO_AUDIT_ROLE (ID,NAME,FLAG,DELETED ,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,REMARK,CODE,GRADE) values ( ";
        sql=sql+"'"+ UUID.randomUUID().toString()+"',"+"'"+rolename+"',"+"'"+flag+"',"+"'0',"+"'"+user+"',"+"'"+signTimet+"',"+"'"+user+"',"+"'"+signTimet+"',"+
                "'"+remark+"','"+roleCode+"','"+roleGrade+"')";
        this.updateData(sql);
    }
    /**數據刪除**/
    public void delete(String id){
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
        this.updateData(sql);
    }
    /**數據修改**/
    public void update(String updateData){
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
                    updateSql+=columnName+"='"+columnValue+"',";
                }
            }
            updateSql=updateSql+"UPDATE_USER="+"'"+user+"',"+"UPDTAE_TIME="+"'"+signTimet+"'";
            updateSql+=where;
            this.updateData(updateSql);
        }
    }
    /**獲取用戶列表**/
    public void userList(Model model,PageRequest pageRequest,String id,String name,String hasRole,String roleName){
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
        Page<Object[]> page = poRoleDao.findPageBySql(pageRequest, sql+"order by USERNAME,REALNAME,CREATOR,ID,create_time");
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
    }
    /**用戶添加角色**/
    public AjaxResult addRoleUser(AjaxResult ajaxResult,String roleId,String userId){
        String[] ids = userId.split(",");
        String whereSql="";
        for (String s : ids) {
            whereSql+="'"+s+"',";
        }
        whereSql=whereSql.substring(0,whereSql.length()-1);
        String roleSql=" select Name,GRADE from FIT_PO_AUDIT_ROLE where id="+"'"+roleId+"'";
        List<Map> maps = poRoleDao.listMapBySql(roleSql);
        //角色等级
        String grade=maps.get(0).get("GRADE").toString();
        String countSql="  select count(ur.id) as COUNT from FIT_PO_AUDIT_ROLE_USER ur  " +
                "left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id " +
                " where r.grade="+"'"+grade+"' and ur.user_id in ("+whereSql+")";
        List<Map> countMaps = poRoleDao.listMapBySql(countSql);
        if(countMaps!=null&&"1".equals(countMaps.get(0).get("COUNT").toString())){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "处于同一级别的角色，一个用户只能拥有一个");
        }else{
            if(maps!=null&maps.size()==1){
                String name=maps.get(0).get("NAME").toString();
                if(name.indexOf("cpo")!=-1){
                    this.addCpoMenu(roleId,ids);
                }else{
                    for (int i = 0; i < ids.length; i++) {
                        String uuId=UUID.randomUUID().toString();
                        String sql="insert into FIT_PO_AUDIT_ROLE_USER (ID,ROLE_ID,USER_Id) values ( ";
                        sql=sql+"'"+uuId+"',"+"'"+roleId+"',"+"'"+ids[i]+"')";
                        System.out.println(sql);
                        this.updateData(sql);
                    }
                }
            }
            else{
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "未找到對應的角色數據( not found role )");
            }
        }
        return ajaxResult;
    }
    /**用戶取消角色**/
    public AjaxResult removeUserRole(AjaxResult ajaxResult,String roleId,String userId){
        String roleSql=" select Name from FIT_PO_AUDIT_ROLE where id="+"'"+roleId+"'";
        List list = poRoleDao.listBySql(roleSql);
        if(list!=null&list.size()==1){
            String name=list.get(0).toString();
            if(name.indexOf("cpo")!=-1){
                this.rmoveCpoMenu(userId,roleId);
            }else{
                String sql="delete from FIT_PO_AUDIT_ROLE_USER where user_id="+"'"+userId+"'"+" and role_id="+"'"+roleId+"'";
                this.updateData(sql);
            }
        }else{
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "未找到對應的角色數據( not found role )");
        }
        return ajaxResult;
    }
    private void addCpoMenu(String roleId,String[] ids){
        for (int i = 0; i < ids.length; i++) {
            String uuId=UUID.randomUUID().toString();
            String sql="insert into FIT_PO_AUDIT_ROLE_USER (ID,ROLE_ID,USER_Id) values ( ";
            sql=sql+"'"+uuId+"',"+"'"+roleId+"',"+"'"+ids[i]+"')";
            poRoleDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            String updateSql=" update fit_user set menus=menus||',poFlow' where id="+"'"+ids[i]+"'";
            this.updateData(updateSql);
        }
    }
    private void rmoveCpoMenu(String userId,String roleId){
        String sql="delete from FIT_PO_AUDIT_ROLE_USER where user_id="+"'"+userId+"'"+" and role_id="+"'"+roleId+"'";;
        poRoleDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        String updateSql="update fit_user set menus=Replace(menus,',poFlow','') where ID="+"'"+userId+"'";
        this.updateData(updateSql);
    }
    /**为角色增加数据处理权限**/
    public void addPerms(String id,String perms){
        String sql="update FIT_PO_AUDIT_ROLE  set TABLE_PERMS="+"'"+perms+"'"+" where id="+"'"+id+"'";
        this.updateData(sql);
    }
    /**
     通过角色id查询对应权限
     **/
    public AjaxResult findPermsByRoleId(AjaxResult ajaxResult, HttpServletRequest request, String id){
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        List<Map<String,String>> tableList=new ArrayList<Map<String,String>>();
        List<String> tableList1=poRoleDao.listBySql("select table_name from FIT_PO_TABLE a where type in('PO','CPO') order by a.upload_flag desc");
        List<String> perms = poRoleDao.listBySql("select TABLE_PERMS from FIT_PO_AUDIT_ROLE where id="+"'"+id+"'");
        List<Map> poTableList = poRoleDao.listMapBySql("select a.table_name,a.comments,b.role_id,b.buttons_type from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b where a.table_name=b.form_name and type in('PO','CPO') order by a.table_name,b.buttons_type");
        if(perms.get(0)!=null){
            String[] split = perms.get(0).split(",");
            for (Map poTable : poTableList) {
                Map<String, String> map = new HashMap<>();
                map.put("name",poTable.get("TABLE_NAME").toString());
                map.put("comment",instrumentClassService.getByLocale(locale, poTable.get("COMMENTS").toString()));
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
                map.put("comment",instrumentClassService.getByLocale(locale, poTable.get("COMMENTS").toString()));
                map.put("roleId",poTable.get("ROLE_ID").toString());
                map.put("type",poTable.get("BUTTONS_TYPE").toString());
                map.put("flag","0");
                tableList.add(map);
            }
        }
        ajaxResult.put("poTableList", tableList);
        ajaxResult.put("poTableList1",tableList1);
        return ajaxResult;
    }
}