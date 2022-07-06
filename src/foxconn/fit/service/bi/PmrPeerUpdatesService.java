package foxconn.fit.service.bi;

import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author maggao
 */
@Service
public class PmrPeerUpdatesService {

    @Autowired
    private PoTableDao poTableDao;


    public AjaxResult add(AjaxResult ajaxResult,String dateAdd,String peerUpdates) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName=loginUser.getUsername();
            dateAdd=dateAdd.replace("-","");
            if(dateAdd.length()==5){
                dateAdd=dateAdd.substring(0,4)+"0"+dateAdd.substring(4,5);
            }
            String sql="insert into PMR_PEER_UPDATES(ID,YEARS,PERIOD,PEER_UPDATES,CREATE_NAME) values('"+ UUID.randomUUID()+"','"+
                    dateAdd.substring(0,4)+"','"+dateAdd.substring(4,6)+"','"+peerUpdates+"','"+userName+"')";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }catch (Exception e) {
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
    return ajaxResult;
    }

    public AjaxResult deleteData(AjaxResult ajaxResult,String no) {
        try {
            String[] ids = no.split(",");
            String deleteSql = " delete from PMR_PEER_UPDATES where ID in(";
            for (String s : ids) {
                deleteSql+= "'" + s + "',";
            }
            deleteSql = deleteSql.substring(0,deleteSql.length()-1) + ")";
            poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
        } catch (Exception e) {
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除失敗(delete Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult;
    }
    /**
     根據條件拼裝sql語句
     **/
    public String selectDataSql(String queryCondition, Locale locale, Model model, PoTable poTable) {
        List<PoColumns> columns = poTable.getColumns();
        List<String> list = new ArrayList<>();
        list.add("ID");
        String sql = "select ID,";
        for (PoColumns poColumns : columns) {
            String value=poColumns.getComments();
            if (locale!=null && "en_US".equals(locale.toString())) {
                list.add(value.substring(0,value.lastIndexOf("_")));
            }else{
                list.add(value.substring(value.lastIndexOf("_")+1,value.length()));
            }
            if(poColumns.getDataType().equalsIgnoreCase("DATE")){
                sql+="to_char("+poColumns.getColumnName()+",'yyyy/mm/dd hh24:mi:ss'),";
            }else{
                sql+=poColumns.getColumnName()+",";
            }
        }
        sql= sql.substring(0,sql.length()-1)+" from "+poTable.getTableName()+" where 1=1 ";
        if (StringUtils.isNotEmpty(queryCondition)) {
            String[] params = queryCondition.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if (StringUtils.isNotEmpty(columnValue)) {
                    if(columnName.equals("YEARS")){
                        columnValue=columnValue.replace("-","");
                        if(columnValue.length()==5){
                            columnValue=columnValue.substring(0,4)+"0"+columnValue.substring(4,5);
                        }
                        sql += " and YEARS||PERIOD='"+columnValue+"'";
                    }else{
                        sql += " and " + columnName + " like '%" + columnValue + "%'";
                    }
                }
            }
        }
        sql += " order by CREATE_DATE desc";
        model.addAttribute("columns", list);
        return sql;
    }

}