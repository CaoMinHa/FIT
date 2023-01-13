package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

/**
 * @author maggao
 */
@Service
public class PoUploadOverdueService {

    @Autowired
    private PoTableDao poTableDao;

    public void updateState(String userId,String type){
        String[] ids = userId.split(",");
        String sql="";
        if("Y".equals(type)){
            sql="delete from FIT_USER_PO_UPLOAD where USER_ID in("+userId+")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            for (String s : ids) {
                sql ="insert into FIT_USER_PO_UPLOAD(USER_ID) values("+s+")";
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            }
        }else{
            sql ="update FIT_USER_PO_UPLOAD set STATE='N' where USER_ID in ("+userId+")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }
    }
    public Model roleList(Model model){
        String sql="select distinct r.code,r.name,r.grade from FIT_PO_AUDIT_ROLE r where \n" +
                "r.code in('CPO','SBUCompetent','PD','MM','SOURCER','KEYUSER','PLACECLASS1','PLACECLASS','TDC','T_MANAGER','CLASS','MANAGER','ADMIN') order by r.grade";
        List<Map> list=poTableDao.listMapBySql(sql);
        model.addAttribute("roles",list);
        return model;
    }
    public String selectSql(String query){
        String sql="select distinct id,username,realname,sbu,commodity_major,state from FIT_USER_PO_UPLOAD_V where 1=1";
        if (StringUtils.isNotEmpty(query)) {
            String[] params = query.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("=")).trim();
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if ("username".equalsIgnoreCase(columnName)) {
                        sql += " and (USERNAME like '%" + columnValue + "%' or  REALNAME like '%" + columnValue + "%')";
                    } else if ("state".equalsIgnoreCase(columnName)) {
                        sql += " and state='" + columnValue + "'";
                    } else {
                        sql += " and "+columnName+" like '%"+columnValue+"%' ";
                    }
                }
            }
        }
        return sql;
    }
}