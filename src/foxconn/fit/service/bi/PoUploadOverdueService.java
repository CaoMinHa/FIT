package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.bi.PoTable;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

/**
 * @author maggao
 */
@Service
public class PoUploadOverdueService {

    @Autowired
    private PoTableDao poTableDao;

    @Autowired
    private InstrumentClassService instrumentClassService;

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

    public void updateState(String updateData){
        String[] params = updateData.split("&");
        String sql="";
        if("Y".equals(params[5].substring(params[5].indexOf("=")+1).trim())){
            sql="delete from FIT_USER_PO_UPLOAD where USER_ID='"+params[0].substring(params[0].indexOf("=")+1).trim()+"'";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            sql ="insert into FIT_USER_PO_UPLOAD(USER_ID,TABLE_NAME,TABLE_CODE,PERIOD_START,PERIOD_END,STATE) " +
                    "values('"+params[0].substring(params[0].indexOf("=")+1).trim()+"','"
                    +params[1].substring(params[1].indexOf("=")+1).trim()+"','"
                    +params[2].substring(params[2].indexOf("=")+1).trim()+"','"
                    +params[3].substring(params[3].indexOf("=")+1).trim()+"','"
                    +params[4].substring(params[4].indexOf("=")+1).trim()+"','"
                    +params[5].substring(params[5].indexOf("=")+1).trim()+"')";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }else{
            sql ="update FIT_USER_PO_UPLOAD set STATE='N',TABLE_NAME='"+params[1].substring(params[1].indexOf("=")+1).trim()+"',TABLE_CODE='"
                    +params[2].substring(params[2].indexOf("=")+1).trim()+"',PERIOD_START='"+
                    params[3].substring(params[3].indexOf("=")+1).trim()+"',PERIOD_END='"+
                    params[4].substring(params[4].indexOf("=")+1).trim()+"' where USER_ID in ('"+params[0].substring(params[0].indexOf("=")+1).trim()+"')";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }
    }

    public Model roleList(Model model){
        String sql="select distinct r.code,r.name,r.grade from FIT_PO_AUDIT_ROLE r where \n" +
                "r.code in('CPO','SBUCompetent','PD','MM','SOURCER','specialSourcer','KEYUSER','PLACECLASS1','PLACECLASS','TDC','T_MANAGER','CLASS','specialClass','MANAGER','specialManager','ADMIN') order by r.grade";
        List<Map> list=poTableDao.listMapBySql(sql);
        model.addAttribute("roles",list);
        return model;
    }
    public String selectSql(String query){
        String sql="select distinct id,username,realname,sbu,commodity_major,table_name,table_code,period_start,period_end,state from FIT_USER_PO_UPLOAD_V where 1=1";
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

    /**
     * 获取採購中心對應物料大類的map
     *
     * @return
     */
    public List<PoTable> selectTabel(Locale locale) {
        String uploadSql = "select * from FIT_PO_TABLE where type='PO' and TABLE_NAME <> 'FIT_PO_BUDGET_CD_DTL'";
        List<PoTable> poTableList = poTableDao.listBySql(uploadSql, PoTable.class);
        List<PoTable> tableList = new ArrayList<>();
        for (PoTable poTable : poTableList) {
            tableList.add(new PoTable(poTable.getTableName(), instrumentClassService.getByLocale(locale, poTable.getComments())));
        }
        return tableList;
    }
}