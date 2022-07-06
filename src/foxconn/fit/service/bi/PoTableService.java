package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.dao.bi.PoTaskDao;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoKey;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class PoTableService extends BaseService<PoTable> {

    public static final String UPLOAD_VALIDATE = "UPLOAD_VAL";
    public static final String DATA_VALIDATE = "DATA_VAL";


    @Autowired
    private PoTableDao poTableDao;

    @Autowired
    private PoTaskService poTaskService;

    @Autowired
    private PoTaskDao poTaskDao;

    @Override
    public BaseDaoHibernate<PoTable> getDao() {
        return poTableDao;
    }

    public void savePoTempData(Map<PoTable, List<List<String>>> dataMap, String year, String period, String poCenter) {
        int cnt = 1;
        for (PoTable poTable : dataMap.keySet()) {
            List<PoColumns> columns = poTable.getColumns();
            String tableName = poTable.getTableName() + "_T";
            String deleteSql = "delete from " + tableName;
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();

            List<List<String>> dataList = dataMap.get(poTable);
            for (List<String> data : dataList) {
                String generateType = data.get(0);
                String columnStr = "";
                for (PoColumns column : columns) {
                    columnStr += column.getColumnName() + ",";
                }
                columnStr = columnStr.substring(0, columnStr.length() - 1);
                String valueStr = "'" + generateType + "',";
                for (int i = 1; i < data.size(); i++) {
                    if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                        valueStr += "to_number('" + data.get(i) + "'),";
                    } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                        valueStr += "to_date('" + data.get(i) + "','dd/mm/yyyy'),";
                    } else {
                        valueStr += "'" + data.get(i) + "',";
                    }
                }
                valueStr = valueStr.substring(0, valueStr.length() - 1);
                String insertSql = "insert into " + tableName + "(" + columnStr + ") values(" + valueStr + ")";
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                cnt++;
                if (cnt % 1000 == 0) {
                    poTableDao.getHibernateTemplate().flush();
                    poTableDao.getHibernateTemplate().clear();
                }
            }
        }
    }

    @Transactional
    public String savePoData(Map<PoTable, List<List<String>>> dataMap, String year, String period,
                             List<String> sbu, List<String> commodity,
                             List<String> monthList) {
        int cnt = 1;
        String id = UUID.randomUUID().toString();
        for (PoTable poTable : dataMap.keySet()) {
            List<PoColumns> columns = poTable.getColumns();
            List<PoKey> keys = poTable.getKeys();
            String[] s = poTable.getComments().split("_");
            String name = year + period + "_" + commodity.get(0)+"_" + s[s.length - 1] + "的上传任务";
            String tableName = poTable.getTableName();
            if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)) {
                name = year + "_" + sbu.get(0)+"_" + s[s.length - 1] + "的上传任务";
            }
            if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
                name = year + "_" + commodity.get(0)+"_" + s[s.length - 1] + "的上传任务";
            }
            String count = " select count(id) from fit_po_task where name='" + name + "' and flag not in('0','-1')";
            List<Map> maps = poTaskService.listMapBySql(count);
            if (maps != null) {
                String num = maps.get(0).get("COUNT(ID)").toString();
                if (!"0".equalsIgnoreCase(num)) {
                    return "";
                } else {
                    if (keys != null && keys.size() > 0) {
                    } else {
                        String deleteSql = "";
                        if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())) {
                            for (int i = 0; i < commodity.size(); i++) {
                                deleteSql = "delete from " + poTable.getTableName() +
                                        " where 1=1 and  flag !='-1' " +
                                        " and year ='" + year + "' and sbu='" + sbu.get(i) + "' and COMMODITY_MAJOR='" + commodity.get(i) + "'";
                                System.out.println(deleteSql);
                                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
                            }
                        }
                        else if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(poTable.getTableName())) {
                            for (int i = 0; i < sbu.size(); i++) {
                                deleteSql = "delete from " + poTable.getTableName() +
                                        " where 1=1 and flag !='-1' " +
                                        " and year ='" + year + "' and month='" + monthList.get(i) + "' and sbu='" + sbu.get(i) + "' and COMMODITY_MAJOR='" + commodity.get(i) + "'";
                                System.out.println(deleteSql);
                                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
                            }
                        }
                        else {
                            for (int i = 0; i < sbu.size(); i++) {
                                deleteSql = "delete from " + poTable.getTableName() +
                                        " where 1=1 and flag !='-1' " +
                                        " and year ='" + year + "' and month='" + period +  "' and sbu='" + sbu.get(i) + "' and COMMODITY='" + commodity.get(i) + "'";
                                System.out.println(deleteSql);
                                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
                            }
                        }


                        List<List<String>> dataList = dataMap.get(poTable);
                        for (List<String> data : dataList) {
                            String generateType = data.get(0);
                            String columnStr = "";
                            for (PoColumns column : columns) {
                                columnStr += column.getColumnName() + ",";
                            }
                            columnStr = columnStr + "flag,TASK_ID ";
                            //columnStr=columnStr.substring(0,columnStr.length()-1);
                            String valueStr = "'" + generateType + "',";
                            for (int i = 1; i < data.size(); i++) {
                                if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                                    valueStr += "to_number('" + data.get(i) + "'),";
                                } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                                    valueStr += "to_date('" + data.get(i) + "','dd/mm/yyyy'),";
                                } else {
                                    valueStr += "'" + data.get(i) + "',";
                                }
                            }
                            valueStr = valueStr.substring(0, valueStr.length() - 1);
                            valueStr = valueStr + ",'0'," + "'" + id + "'";
                            String insertSql = "insert into " + poTable.getTableName() + "(" + columnStr + ") values(" + valueStr + ")";
                            System.out.println(insertSql);
                            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                            cnt++;
                            if (cnt % 1000 == 0) {
                                poTableDao.getHibernateTemplate().flush();
                                poTableDao.getHibernateTemplate().clear();
                            }
                        }
                    }
                    //采购删除之前存在的数据
                    String like ="";
                    if("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)){
//                        like=year + "_" + sbu.get(0)+"%";
                        like = year + "_" + sbu.get(0)+"_" + s[s.length - 1] + "的上传任务";
                    }else if("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)){
//                        like= year + "_" + commodity.get(0)+"_%";
                        like = year + "_" + commodity.get(0)+"_" + s[s.length - 1] + "的上传任务";
                    }else{
//                        if("FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(tableName)||"FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(tableName)){
//                        like= year + period + "_" + commodity.get(0)+"%";
//                        like= year + period + "_" + commodity.get(0)+"_%";
                        like=year + period + "_" + commodity.get(0)+"_" + s[s.length - 1] + "的上传任务";
                    }
                    String deleteSql = " delete from FIT_PO_TASK where flag !='-1' and name = '" + like + "' and type=" + "'" + tableName + "'";
                    poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
                    UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                    String user = loginUser.getUsername();
                    List<String> userName= this.listBySql("select realname from FIT_USER where username='"+user+"'");
                    if(null==userName.get(0)){
                        userName.set(0,user);
                    }

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String signTimet = df.format(new Date());
                    String sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,CREATE_USER_REAL,UPDATE_USER_REAL) " +
                            " values ( ";
                    sql = sql + "'" + id + "'," + "'" + tableName + "'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet + "'" +
                            ",'"+userName.get(0)+"','"+userName.get(0)+"')";
                    if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(tableName)
                            || "FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)
                            || "FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(tableName)) {
                        sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,COMMODITY_MAJOR,CREATE_USER_REAL,UPDATE_USER_REAL ) " +
                                " values ( ";
                        sql = sql + "'" + id + "'," + "'" + tableName + "'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet
                                + "','" + commodity.get(0) + "'" + ",'"+userName.get(0)+"','"+userName.get(0)+"')";
                    }
                    if("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)){
                        sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,CREATE_USER_REAL,UPDATE_USER_REAL,SBU) " +
                                " values ( ";
                        sql = sql + "'" + id + "'," + "'" + tableName + "'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet + "'" +
                                ",'"+userName.get(0)+"','"+userName.get(0)+"','"+sbu.get(0)+"')";
                    }
                    poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
                }
            }
        }
        return id;
    }

    /*
       校驗月份展開cd校驗
     */
    public void validateMonth(String taskId) throws Exception {
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{ CALL fit_po_cd_month_down_pkg.main(?,?)}");
        cs.setString(1, taskId);
        //需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效
        cs.registerOutParameter(2, java.sql.Types.VARCHAR);
        cs.execute();
        String message = cs.getString(2);
        cs.close();
        c.close();
        if (StringUtils.isNotEmpty(message)) {
            throw new RuntimeException(message);
        }
    }

    public String validate(String tableName, String year, String period, String entity, String type, Locale locale) throws Exception {
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call CUX_PO_DATA_PKG.validation(?,?,?,?,?,?,?,?)}");
        cs.setString(1, tableName);
        cs.setString(2, type);
        cs.setString(3, locale != null ? locale.toString() : "zh_CN");
        cs.setString(4, year);
        cs.setString(5, period);
        cs.setString(6, entity);
        //需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效
        cs.registerOutParameter(7, java.sql.Types.INTEGER);
        cs.registerOutParameter(8, java.sql.Types.VARCHAR);
        cs.execute();
        int code = cs.getInt(7);
        String message = cs.getString(8);

        cs.close();
        c.close();

        if (code != 0) {
            return message;
        }

        return "";
    }

    public void generateConsolidation(String year, String period, String procedureName) throws Exception {
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call CUX_AUDIT_DATA_PKG." + procedureName.toLowerCase() + "(?,?)}");
        cs.setString(1, year);
        cs.setString(2, period);
        cs.execute();
    }

    public String dataImport(String year, String period, String entity, Locale locale) throws Exception {
        String lang = "CN";
        if (locale != null && locale.toString().equals("en_US")) {
            lang = "EN";
        }
        Connection c = SessionFactoryUtils.getDataSource(poTableDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call CUX_AUDIT_DATA_PKG.main_integration(?,?,?,?,?,?,?)}");
        cs.setString(1, year);
        cs.setString(2, period);
        cs.setString(3, entity);
        cs.setString(4, lang);
        cs.setString(5, "All");
        cs.registerOutParameter(6, java.sql.Types.VARCHAR);
        cs.registerOutParameter(7, java.sql.Types.VARCHAR);
        cs.execute();
        String status = cs.getString(6);
        String message = cs.getString(7);
        cs.close();
        c.close();

        if ("S".equals(status)) {
            message = "";
        }
        return message;
    }


    @Transactional
    public void saveRtData(Map<PoTable, List<List<String>>> dataMap,String year) {
        int cnt = 1;
        for (PoTable poTable : dataMap.keySet()) {
            List<PoColumns> columns = poTable.getColumns();
//            String deleteSql = "";
            String tableName=poTable.getTableName();
//            deleteSql = "delete from " + poTable.getTableName() +
//                    " where 1=1 ";
//            if("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)){
//                deleteSql+=" and year='"+year+"'";
//                System.out.println(deleteSql);
//                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
//            }
            List<List<String>> dataList = dataMap.get(poTable);
            for (List<String> data : dataList) {
                String generateType = data.get(0);
                String columnStr = "";
                for (PoColumns column : columns) {
                    columnStr += column.getColumnName() + ",";
                }
                columnStr=columnStr.substring(0,columnStr.length()-1);
                String valueStr = "'" + generateType + "',";
                String deleteStr=" and year='"+data.get(0)+"'";
                //这里减少创建人 创建时间 更新人 更新时间
                for (int i = 1; i < data.size()-2; i++) {
                    if("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)&&i<14&&i!=1&&i!=2){
                        deleteStr+=" and "+columns.get(i).getColumnName()+"='"+data.get(i)+"'";
                    }
                    if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                        valueStr += "ROUND('" + data.get(i) + "',2),";
                    } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                        valueStr += "to_date('" + data.get(i) + "','dd/mm/yyyy'),";
                    } else {
                        valueStr += "'" + data.get(i) + "',";
                    }
                }
                System.out.println(valueStr);
                if("CUX_RT_SALES_TARGET".equalsIgnoreCase(tableName)){
                    deleteStr="delete "+poTable.getTableName()+" where 1=1 "+deleteStr;
                    poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteStr).executeUpdate();
                }
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String userName = loginUser.getUsername();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet = df.format(new Date());
                valueStr+="'"+userName+"','"+signTimet+"'";
                //valueStr = valueStr.substring(0, valueStr.length() - 1);
                String insertSql = "insert into " + poTable.getTableName() + "(" + columnStr + ") values(" + valueStr + ")";
                System.out.println(insertSql);
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                cnt++;
                if (cnt % 1000 == 0) {
                    poTableDao.getHibernateTemplate().flush();
                    poTableDao.getHibernateTemplate().clear();
                }
            }
        }
    }
    /*
        系统管理员可以删除数据
        每张表任务处于已提交后都不能删除
        特殊 sbu删除规则，当cpo当年度任务新建则不能删除
           by month表删除，连带删除该年度对应sbu的数据

      */
    public void deleteAll(String idStr,String tableName){
        if("FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(tableName)){
            String sql=" delete from FIT_PO_CD_MONTH_DTL where (year,sbu) " +
                    " in (select year,sbu from FIT_PO_CD_MONTH_DOWN where id in ("+idStr+"))";
            System.out.println(sql);
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }
        String deleteSql = " delete from " + tableName + " where id in (";
        deleteSql += idStr + ")";
        System.out.println(deleteSql);
        poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();

    }


    //保存数据
    @Transactional
    public void saveData(Map<PoTable, List<List<String>>> dataMap) {
        int cnt = 1;
        for (PoTable poTable : dataMap.keySet()) {
            List<PoColumns> columns = poTable.getColumns();
            List<List<String>> dataList = dataMap.get(poTable);
            for (List<String> data : dataList) {
                String columnStr = "";
                for (PoColumns column : columns) {
                    columnStr += column.getColumnName() + ",";
                }
                columnStr=columnStr.substring(0,columnStr.length()-1);
                String valueStr = "";
                //这里减少创建人 创建时间 更新人 更新时间
                for (int i = 0; i < data.size(); i++) {
                    if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
                        valueStr += "ROUND('" + data.get(i) + "',2),";
                    } else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
                        valueStr += "to_date('" + data.get(i) + "','dd/mm/yyyy'),";
                    } else {
                        valueStr += "'" + data.get(i) + "',";
                    }
                }
                System.out.println(valueStr);
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String userName = loginUser.getUsername();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet = df.format(new Date());
                valueStr+="'"+userName+"','"+signTimet+"'";
                String insertSql = "insert into " + poTable.getTableName() + "(" + columnStr + ") values(" + valueStr + ")";
                System.out.println(insertSql);
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                cnt++;
                if (cnt % 1000 == 0) {
                    poTableDao.getHibernateTemplate().flush();
                    poTableDao.getHibernateTemplate().clear();
                }
            }
        }
    }
    public void updateSql(String sql){
        poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
    }
}


