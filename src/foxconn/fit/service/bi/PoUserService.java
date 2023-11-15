package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
/**
 * @author Yang DaiSheng
 * @program fit
 * @description 采購功能審批流用戶添加角色業務類
 * @create 2021-05-11 15:47
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoUserService{

    @Autowired
    private PoTableDao poTableDao;


    public void updateData(String updateSql) {
        poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }


    public void list(Model model, PageRequest pageRequest, String name, String username){
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
        Page<Object[]> page = poTableDao.findPageBySql(pageRequest, sql);
        int index=1;
        if(pageRequest.getPageNo()>1){
            index=2;
        }
        model.addAttribute("index", index);
        model.addAttribute("tableName", "FIT_USER");
        model.addAttribute("page", page);
    }

    /**獲取大類及SBU維度值**/
    public AjaxResult userHasCommodityMajor(AjaxResult ajaxResult, HttpServletRequest request, String userId){
        String userSql=" select Commodity_Major,SBU,REALNAME,email from FIT_user where id="+"'"+userId+"'";
        List<Map> map= poTableDao.listMapBySql(userSql);
        List<String> commodityList = poTableDao.listBySql("select distinct tie.COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING tie order by tie.COMMODITY_NAME");
        List<String> sbuList = poTableDao.listBySql("select distinct SBU from(\n" +
                "select distinct NEW_SBU_NAME sbu from BIDEV.v_if_sbu_mapping \n" +
                "union all\n" +
                "select distinct SBU from epmexp.cux_pbcs_fit_mapping\n" +
                ")");
        ajaxResult.put("cList",commodityList);
        ajaxResult.put("map",map);
        ajaxResult.put("sList",sbuList);
        return ajaxResult;
    }

    public AjaxResult updateUser(AjaxResult result,String id, String sbu, String email, String commodity,String realname){
        if (StringUtils.isNotEmpty(id)) {
            String updateSql = "update FIT_USER set ";
            updateSql += " sbu=" + "'" + sbu + "',";
            updateSql += " COMMODITY_MAJOR=" + "'" + commodity + "',";
            updateSql += " email=" + "'" + email + "',";
            updateSql += " realname=" + "'" + realname + "',";
            updateSql = updateSql.substring(0, updateSql.length() - 1);
            updateSql += " where id=" + "'" + id + "'";
            this.updateData(updateSql);
        }else{
            result.put("flag", "fail");
            result.put("msg", "維護用戶失敗");
        }
        return result;
    }
}