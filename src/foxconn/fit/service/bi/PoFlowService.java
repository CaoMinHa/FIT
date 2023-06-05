package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoFlowDao;
import foxconn.fit.entity.bi.PoFlow;
import foxconn.fit.service.base.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoFlowService extends BaseService<PoFlow> {

    @Autowired
    private PoFlowDao poFlowDao;
    @Override
    public BaseDaoHibernate<PoFlow> getDao() {
        return poFlowDao;
    }

    /**頁面修改單條數據**/
    public void updateData(String updateData,String tableName) {
        if (StringUtils.isNotEmpty(updateData)) {
            String updateSql = "update " + tableName + " set ";
            String where = "";
            String[] params = updateData.split("&");
            for (String param : params) {
                String columnName = param.substring(0, param.indexOf("="));
                String columnValue = param.substring(param.indexOf("=") + 1).trim();
                if ("ID".equalsIgnoreCase(columnName)) {
                    where = " where ID='" + columnValue + "'";
                } else if ("NO_CPO".equalsIgnoreCase(columnName)) {
                    if (StringUtils.isNotEmpty(columnValue)) {
                        updateSql += columnName + "='" + columnValue + "',";
                    }
                } else if ("CPO".equalsIgnoreCase(columnName)) {
                    if (StringUtils.isNotEmpty(columnValue)) {
                        updateSql += columnName + "='" + columnValue + "',";
                    }
                }
            }
            updateSql = updateSql.substring(0, updateSql.length() - 1);
            updateSql += where;
            System.out.println(updateSql);
            poFlowDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
        }
    }

    /**頁面修改數據**/
    public void updateDataAll(String updateData,String tableName) {
        if (StringUtils.isNotEmpty(updateData)) {
            String[] str=updateData.split(";");
            for (String s:str) {
                String updateSql = "update " + tableName + " set ";
                String where = "";
                String[] params = s.split("&");
                for (String param : params) {
                    String columnName = param.substring(0, param.indexOf("="));
                    String columnValue = param.substring(param.indexOf("=") + 1).trim();
                    if ("ID".equalsIgnoreCase(columnName)) {
                        where = " where ID='" + columnValue + "'";
                    } else if ("NO_CPO".equalsIgnoreCase(columnName)) {
                        if (StringUtils.isNotEmpty(columnValue)) {
                            updateSql += columnName + "='" + columnValue + "',";
                        }
                    } else if ("CPO".equalsIgnoreCase(columnName)) {
                        if (StringUtils.isNotEmpty(columnValue)) {
                            updateSql += columnName + "='" + columnValue + "',";
                        }
                    }
                }
                updateSql = updateSql.substring(0, updateSql.length()- 1)+where;
                System.out.println(updateSql);
                getDao().getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
            }
        }
    }

    private void executeCpo(String year){
        try{
            Connection c = SessionFactoryUtils.getDataSource(poFlowDao.getSessionFactory()).getConnection();
            CallableStatement cs = c.prepareCall("{call fit_po_target_cpo_cd_pkg.main(?)}");
            cs.setString(1, year);
            cs.execute();
            cs.close();
            c.close();
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    /**頁面查詢**/
    public Model list(Model model, String tableName, String  date, PageRequest pageRequest){
        String flagSql="select distinct flag from "+tableName+ " where year='"+date+"'  and flag is null";
        List<String> flags= this.listBySql(flagSql);
        if(flags!=null&&flags.size()>0){
            String flag=flags.get(0);
            if(flag==null){
                this.executeCpo(date);
                System.out.println("實時計算date");
            }
        }
        String sql="select ID,PO_CENTER,COMMODITY_MAJOR,NO_PO_TOTAL, NO_CD ,NO_CPO ,PO_TOTAL,CD,CPO  " +
                "from "+tableName+ " where year="+date+" order by ID,PO_CENTER";

        if("FIT_PO_Target_CPO_CD_DTL".equals(tableName)){
            sql="select ID,PO_CENTER,COMMODITY_MAJOR ,NO_PO_TOTAL,NO_CD_AMOUNT, NO_CD ,NO_CPO,PO_TOTAL ,CD_AMOUNT, CD,CPO from FIT_PO_TARGET_CPO_CD_DTL_V  where year="+date;
        }
        pageRequest.setPageSize(this.listBySql(sql).size());
        Page<Object[]> page = this.findPageBySql(pageRequest, sql);
        int index=0;
        if(pageRequest.getPageNo()>1){
            index=1;
        }

        String countSbu="select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where FLAG='1' order by SBU_NAME";
        String countNotUpload="select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where FLAG='1' " +
                " and SBU_NAME not in" +
                "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where  a.flag=3 and a.year='"+date+"')";
        String countUpload="select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where FLAG='1' and SBU_NAME in" +
                "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where  a.flag=3 and a.year='"+date+"')";
        List <String> countSbulist= this.listBySql(countSbu);
        List <String> countNotUploadlist= this.listBySql(countNotUpload);
        List <String> countUploadList= this.listBySql(countUpload);


        model.addAttribute("countSUM",countSbulist.size());
        model.addAttribute("countNotUploadList",countNotUploadlist);
        model.addAttribute("countUploadList",countUploadList);
        model.addAttribute("countNotUploadNumber",countNotUploadlist.size());

        model.addAttribute("index", index);
        model.addAttribute("total", page.getTotalItems());
        model.addAttribute("page", page);
        return model;
    }
}