package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.PoCenterService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/bi/poIntegrationList")
public class PoIntegrationListController extends BaseController {
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private PoCenterService poCenterService;


    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if(String.valueOf(session.getAttribute("detailsTsak")).equalsIgnoreCase("ok")){
                List<String> listSBU=poTableService.listBySql(" select SBU from FIT_PO_SBU_YEAR_CD_SUM where TASK_ID='"+session.getAttribute("taskId")+"'" +
                        "  and rownum=1 ");
                List<String> listYear=poTableService.listBySql(" select year from FIT_PO_SBU_YEAR_CD_SUM where TASK_ID='"+session.getAttribute("taskId")+"'" +
                        "  and rownum=1 ");
                String sbuVal=listSBU.get(0);
                String DateYear=listYear.get(0);
                model.addAttribute("sbuVal",sbuVal);
                model.addAttribute("DateYear",DateYear);
            }
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName = loginUser.getUsername();
            String permSql = "  select listagg(r.TABLE_PERMS, ',') within GROUP(ORDER BY r.id) as TABLE_PERMS  from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " where u.username=" + "'" + userName + "'";
            List<String> perms = poTableService.listBySql(permSql);
            String roleSql = " select distinct r.code  from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " WHERE r.grade='1' and u.username='" + userName + "'";
            String keyUserSql = " select count(1)  from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " WHERE r.code='KEYUSER' and u.username='" + userName + "'";
            List<Map> maps = poTableService.listMapBySql(keyUserSql);
            List<String> roleCode = poTableService.listBySql(roleSql);
            //取值范围 1 采购源 物料大类值集 2 企划员 sbu值集
            List<String> dataRange = new ArrayList<>();
            String hasKey = "0";
            String userSql = "";
            if (maps != null && !"0".equals(maps.get(0).get("COUNT(1)").toString())) {
                hasKey = "1";
            }
            if (null != roleCode && roleCode.size() > 0) {
                String roles = roleCode.get(0) == null ? "" : roleCode.get(0);
                if (roles.equalsIgnoreCase("MM")) {
                    userSql = "select SBU  from fit_user where username='" + userName + "' and sbu is not null " +
                            "union all " +
                            "select listagg(t.NEW_SBU_NAME, ',') within group(order by t.NEW_SBU_NAME) as sbu " +
                            "  from " +
                            "(select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie " +
                            " where exists (select SBU  from fit_user where username='" + userName + "' and sbu is null) " +
                            " ) t ";
                    //select   distinct  SBU from EPMEBS.CUX_SBU_BU_MAPPING order by SBU
//                    select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie order by tie.NEW_SBU_NAME

                } else if (roles.equalsIgnoreCase("SOURCER")) {
                    userSql = " select COMMODITY_MAJOR  from fit_user where username='" + userName + "' and COMMODITY_MAJOR is not null " +
                            "union all " +
                            "select listagg(t.COMMODITY_NAME, ',') within group(order by t.COMMODITY_NAME) as COMMODITY_MAJOR " +
                            "  from " +
                            "(select distinct tie.COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING tie " +
                            " where exists (select COMMODITY_MAJOR  from fit_user where username='" + userName + "' and COMMODITY_MAJOR is null) " +
                            " ) t ";
                }
            }
            if (!userSql.equalsIgnoreCase("")) {
                List<String> ranges = poTableService.listBySql(userSql);
                String[] split = ranges.get(0).split(",");
                dataRange = Arrays.asList(split);
            }
            String whereSql = "";

            if (perms.get(0) != null && perms.get(0).length() != 0) {
                String perm = perms.get(0);
                String[] berSplit = perm.split(",");
                List list = Arrays.asList(berSplit);
                Set set = new HashSet(list);
                String[] split = (String[]) set.toArray(new String[0]);
                for (String s : split) {
                    whereSql += "'" + s + "',";
                }
            }
            whereSql+= "'a'";
//            String uploadSql = " select * from Fit_po_table where TYPE='PO' and Upload_flag='Y' " +
//                    "and table_name in (" + whereSql + ") order by serial";
            String uploadSql = "select a.* from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                    " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=2 and b.role_id in (" + whereSql + ") order by serial";
//            String exportSql = " select * from Fit_po_table where TYPE='PO' and Upload_flag!='Y' order by serial";
            String exportSql = "select a.* from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                    " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=1 and b.role_id in (" + whereSql + ") order by serial";
            String selectSql = "select a.* from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                    " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=3 and b.role_id in (" + whereSql + ") order by serial";

            List<PoTable> poTableListSelect = poTableService.listBySql(selectSql, PoTable.class);
            List<PoTable> poTableList = poTableService.listBySql(uploadSql, PoTable.class);
            List<PoTable> poTableOutList = poTableService.listBySql(exportSql, PoTable.class);


            List<PoTable> tableList = new ArrayList<PoTable>();
            for (PoTable poTable : poTableList) {
                tableList.add(new PoTable(poTable.getTableName(), getByLocale(locale, poTable.getComments())));
            }

            List<PoTable> tableListSelect = new ArrayList<PoTable>();
            for (PoTable poTable : poTableListSelect) {
                tableListSelect.add(new PoTable(poTable.getTableName(), getByLocale(locale, poTable.getComments())));
            }

            List<PoTable> tableOutList = new ArrayList<PoTable>();
            for (PoTable poTable : poTableOutList) {
                if (poTable.getTableName().equalsIgnoreCase("FIT_PO_CD_MONTH_DTL")) {
                    tableOutList.add(new PoTable("FIT_PO_CD_MONTH_DOWN", getByLocale(locale, poTable.getComments())));
                } else if (!poTable.getTableName().equalsIgnoreCase("FIT_PO_CD_MONTH_DOWN")) {
                    tableOutList.add(new PoTable(poTable.getTableName(), getByLocale(locale, poTable.getComments())));
                }
            }

            List<String> commodityList=poTableService.listBySql("select COMMODITY_MAJOR  from BIDEV.v_dm_d_commodity_major order by FUNCTION_NAME");
            List<String> poCenters = poCenterService.findPoCenters();
            session.setAttribute("dataRange", dataRange);
            request.setAttribute("poCenters", poCenters);
            model.addAttribute("commodityList", commodityList);

            model.addAttribute("poTableList", tableList);
            model.addAttribute("poTableOutList", tableOutList);
            model.addAttribute("tableListSelect", tableListSelect);
            model.addAttribute("dataRange", dataRange);
            model.addAttribute("hasKey", hasKey);
            List<String> typeList = poTableService.listBySql("select distinct tablename from FIT_AUDIT_CONSOL_CONFIG order by tablename");
            model.addAttribute("typeList", typeList);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poIntegrationList/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request, String DateYear,
                       String date, String dateEnd, String tableName, String poCenter, String sbuVal,String commodity) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            HttpSession session = request.getSession(false);
            if(String.valueOf(session.getAttribute("detailsTsak")).equalsIgnoreCase("ok")){
                session.setAttribute("detailsTsak","");
            }
            if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
                tableName = "FIT_PO_CD_MONTH_DOWN";
            }
            PoTable poTable = poTableService.get(tableName);

            List<PoColumns> columns = poTable.getColumns();
            for (PoColumns poColumns : columns) {
                poColumns.setComments(getByLocale(locale, poColumns.getComments()));
            }
            String sql = "select ID,";
            String sqlSum = "select '' ID,";
            for (PoColumns column : columns) {
                String columnName = column.getColumnName();
                if (column.getDataType().equalsIgnoreCase("number")) {
                    //改成原样输出
                    sql += columnName + ",";
                    if ("FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
                        switch (columnName) {
                            case "CPO_PRO":
                                sqlSum += "to_char(decode((sum(YEAR_TOTAL)+sum(PO_TARGET_CD)),0,null,sum(PO_TARGET_CD)/(sum(YEAR_TOTAL)+sum(PO_TARGET_CD))*100),9999999999.9999) CPO_PRO,";
                                break;
                            case "PO_TARGET_CPO":
                                sqlSum += "to_char(decode((sum(YEAR_TOTAL)+sum(PO_TARGET_CD)),0,null,sum(PO_TARGET_CD)/(sum(YEAR_TOTAL)+sum(PO_TARGET_CD))*100),9999999999.9999) PO_TARGET_CD,";
                                break;
                            case "ONE_CPO":
                                sqlSum += "to_char(decode((sum(ONE_PO_MONEY)+sum(ONE_CD)),0,null,sum(ONE_CD)/(sum(ONE_PO_MONEY)+sum(ONE_CD))*100),9999999999.9999) ONE_CPO,";
                                break;
                            case "TWO_CPO":
                                sqlSum += "to_char(decode((sum(TWO_PO_MONEY)+sum(TWO_CD)),0,null,sum(TWO_CD)/(sum(TWO_PO_MONEY)+sum(TWO_CD))*100),9999999999.9999) TWO_CPO,";
                                break;
                            case "THREE_CPO":
                                sqlSum += "to_char(decode((sum(THREE_PO_MONEY)+sum(THREE_CD)),0,null,sum(THREE_CD)/(sum(THREE_PO_MONEY)+sum(THREE_CD))*100),9999999999.9999) THREE_CPO,";
                                break;
                            case "FOUR_CPO":
                                sqlSum += "to_char(decode((sum(FOUR_PO_MONEY)+sum(FOUR_CD)),0,null,sum(FOUR_CD)/(sum(FOUR_PO_MONEY)+sum(FOUR_CD))*100),9999999999.9999) FOUR_CPO,";
                                break;
                            case "FIVE_CPO":
                                sqlSum += "to_char(decode((sum(FIVE_PO_MONEY)+sum(FIVE_CD)),0,null,sum(FIVE_CD)/(sum(FIVE_PO_MONEY)+sum(FIVE_CD))*100),9999999999.9999) FIVE_CPO,";
                                break;
                            case "SIX_CPO":
                                sqlSum += "to_char(decode((sum(SIX_PO_MONEY)+sum(SIX_CD)),0,null,sum(SIX_CD)/(sum(SIX_PO_MONEY)+sum(SIX_CD))*100),9999999999.9999) SIX_CPO,";
                                break;
                            case "SEVEN_CPO":
                                sqlSum += "to_char(decode((sum(SEVEN_PO_MONEY)+sum(SEVEN_CD)),0,null,sum(SEVEN_CD)/(sum(SEVEN_PO_MONEY)+sum(SEVEN_CD))*100),9999999999.9999) SEVEN_CPO,";
                                break;
                            case "EIGHT_CPO":
                                sqlSum += "to_char(decode((sum(EIGHT_PO_MONEY)+sum(EIGHT_CD)),0,null,sum(EIGHT_CD)/(sum(EIGHT_PO_MONEY)+sum(EIGHT_CD))*100),9999999999.9999) EIGHT_CPO,";
                                break;
                            case "NINE_CPO":
                                sqlSum += "to_char(decode((sum(NINE_PO_MONEY)+sum(NINE_CD)),0,null,sum(NINE_CD)/(sum(NINE_PO_MONEY)+sum(NINE_CD))*100),9999999999.9999) NINE_CPO,";
                                break;
                            case "TEN_CPO":
                                sqlSum += "to_char(decode((sum(TEN_PO_MONEY)+sum(TEN_CD)),0,null,sum(TEN_CD)/(sum(TEN_PO_MONEY)+sum(TEN_CD))*100),9999999999.9999) TEN_CPO,";
                                break;
                            case "ELEVEN_CPO":
                                sqlSum += "to_char(decode((sum(ELEVEN_PO_MONEY)+sum(ELEVEN_CD)),0,null,sum(ELEVEN_CD)/(sum(ELEVEN_PO_MONEY)+sum(ELEVEN_CD))*100),9999999999.9999) ELEVEN_CPO,";
                                break;
                            case "TWELVE_CPO":
                                sqlSum += "to_char(decode((sum(TWELVE_PO_MONEY)+sum(TWELVE_CD)),0,null,sum(TWELVE_CD)/(sum(TWELVE_PO_MONEY)+sum(TWELVE_CD))*100),9999999999.9999) TWELVE_CPO,";
                                break;
                            case "PO_CPO":
                                sqlSum += "to_char(decode((sum(PO_TOTAL)+sum(PO_CD)),0,null,sum(PO_CD)/(sum(PO_TOTAL)+sum(PO_CD))*100),9999999999.9999) PO_CPO,";
                                break;
                            default:
                                sqlSum += "sum(" + columnName + "),";
                                break;
                        }
                    }else if("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())){
                        switch (columnName) {
                            case "YEAR_CD":
                                sqlSum += " sum(YEAR_CD_AMOUNT)/(sum(YEAR_CD_AMOUNT)+sum(PO_AMOUNT))*100 YEAR_CD ,";
                                break;
                            default:
                                sqlSum += "sum(" + columnName + "),";
                                break;
                        }
                    }else {
                        sqlSum += "sum(" + columnName + "),";
                    }
                } else if (column.getDataType().equalsIgnoreCase("date")) {
                    sql += "to_char(" + columnName + ",'dd/mm/yyyy'),";
                    sqlSum += "'' " + columnName + ",";
                } else {
                    sql += columnName + ",";
                    sqlSum += "'' " + columnName + ",";
                }
            }
            sql = sql.substring(0, sql.length() - 1);
            sql += " from " + poTable.getTableName() + " where 1=1";
            sqlSum = sqlSum.substring(0, sqlSum.length() - 1);
            sqlSum += " from " + poTable.getTableName() + " where 1=1";
            String whereSql = "";
            String orderBy = " order by ";
            if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())) {
                if (StringUtils.isNotEmpty(DateYear)) {
                    whereSql += " and " + columns.get(0).getColumnName() + "='" + DateYear + "'";
                }
                if (StringUtils.isNotEmpty(poCenter)) {
                    whereSql += " and " + columns.get(1).getColumnName() + "='" + poCenter + "'";
                }
            } else {
                if (StringUtils.isNotEmpty(date) && StringUtils.isNotEmpty(dateEnd)) {
                    Date d = DateUtil.parseByYyyy_MM(date);
                    Assert.notNull(d, getLanguage(locale, "年月格式錯誤", "The format of year/month is error"));
                    String[] split = date.split("-");
                    String[] split1 = dateEnd.split("-");
                    String year = split[0];
                    String period = split[1];
                    String period1 = split1[1];
                    if (period.length() < 2) {
                        period = "0" + period;
                    }
                    if (period1.length() < 2) {
                        period1 = "0" + period1;
                    }
                    orderBy += columns.get(1).getColumnName() + ", ";
                    whereSql += " and " + columns.get(0).getColumnName() + " =" + year + " and " + columns.get(1).getColumnName() + ">=" + period + " and " + columns.get(1).getColumnName() + "<=" + period1;
                }
                if (StringUtils.isNotEmpty(poCenter)) {
                    whereSql += " and " + columns.get(2).getColumnName() + "='" + poCenter + "'";
                }
            }
            if ("FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
                whereSql = "";
                whereSql += " and year= " + DateYear;
            }

            if(null!=commodity && !"".equalsIgnoreCase(commodity)){
                String commotityVal="";
                for (int i=0;i<commodity.split(",").length;i++) {
                    commotityVal+="'"+commodity.split(",")[i]+"',";
                }
                if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(poTable.getTableName())||
                        "FIT_PO_Target_CPO_CD_DTL".equalsIgnoreCase(poTable.getTableName())||
                        "FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
                    whereSql += " and COMMODITY_MAJOR in(" + commotityVal.substring(0,commotityVal.length()-1) + ")";
                }else{
                    whereSql += " and COMMODITY in (" + commotityVal.substring(0,commotityVal.length()-1) + ")";
                }
            }
            if (StringUtils.isNotEmpty(sbuVal)) {
                whereSql += " and sbu LIKE " + "'%" + sbuVal + "%'";
            }
            orderBy += " ID,SBU";
            sql = sql+" and flag!='-1' " + whereSql + orderBy;
            sqlSum += whereSql+" and flag!='-1' ";
            System.out.println(sql + "合計：" + sqlSum);

            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            PageRequest pageRequest1 = new PageRequest();
            pageRequest1.setPageNo(1);
            pageRequest1.setPageSize(1);
            Page<Object[]> pages = poTableService.findPageBySql(pageRequest1, sqlSum);
            page.getResult().addAll(pages.getResult());
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", poTable.getTableName());
            model.addAttribute("page", page);
            model.addAttribute("columns", columns);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poIntegrationList/list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String deleteAll(AjaxResult ajaxResult, HttpServletRequest request, String id, String tableName) {
        try {
            if (StringUtils.isNotEmpty(id)) {
                String[] ids = id.split(",");
                String idStr = "";
                for (String s : ids) {
                    idStr = idStr + "'" + s + "',";
                }
                idStr = idStr.substring(0, idStr.length() - 1);
                if ("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)) {
                    tableName = "FIT_PO_CD_MONTH_DOWN";
                } else if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)) {
                    String cpoSql = " select COUNT(*) FROM FIT_PO_Target_CPO_CD_DTL " +
                            " WHERE FLAG='0' AND YEAR IN (SELECT YEAR FROM FIT_PO_SBU_YEAR_CD_SUM " +
                            " WHERE ID IN (" + idStr + "))";
                    List<Map> maps = poTableService.listMapBySql(cpoSql);
                    if (maps != null && !"0".equals(maps.get(0).get("COUNT(*)").toString())) {
                        ajaxResult.put("flag", "fail");
                        ajaxResult.put("msg", "删除的sbu数据所对应的CPO任务已流转，不能删除");
                        return ajaxResult.getJson();
                    }
                }
                String sql = " SELECT COUNT(*) FROM "
                        + tableName + " WHERE ID IN ( "
                        + idStr + ") AND FLAG!='0'";
                List<Map> countMaps = poTableService.listMapBySql(sql);
                if (countMaps != null && !"0".equals(countMaps.get(0).get("COUNT(*)").toString())) {
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "删除数据对应的任务已提交，数据不能删除");
                    return ajaxResult.getJson();
                } else {
                    poTableService.deleteAll(idStr, tableName);
                }
            } else {
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "删除数据异常,请稍后再试");
            }
        } catch (Exception e) {
            logger.error("刪除" + tableName + "数据失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除" + tableName + "失败(delete " + tableName + " Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    @RequestMapping(value = "/selectCommdity")
    @ResponseBody
    public List<String> selectCommdity(HttpServletRequest request, String functionName){
        List<String> commodityList=poTableService.listBySql("select distinct COMMODITY_MAJOR from BIDEV.v_dm_d_commodity_major where FUNCTION_NAME='"+functionName+"' order by COMMODITY_MAJOR");
        return  commodityList;
    }

    /**
     * 页面下载权限把控
     * @param ajaxResult
     * @param tableName
     * @return
     */
    @RequestMapping(value = "/downloadCheck")
    @ResponseBody
    public String downloadCheck(AjaxResult ajaxResult, String tableName) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName = loginUser.getUsername();
            String permSql = "  select listagg(r.TABLE_PERMS, ',') within GROUP(ORDER BY r.id) as TABLE_PERMS  from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " where u.username=" + "'" + userName + "'";
            List<String> perms = poTableService.listBySql(permSql);
            String whereSql="";
            if (perms.get(0) != null && perms.get(0).length() != 0) {
                String perm = perms.get(0);
                String[] berSplit = perm.split(",");
                List list = Arrays.asList(berSplit);
                Set set = new HashSet(list);
                String[] split = (String[]) set.toArray(new String[0]);
                for (String s : split) {
                    whereSql += "'" + s + "',";
                }
            }
            whereSql += "'a'";
            String uploadSql = "select count(1) from FIT_PO_TABLE a,FIT_PO_BUTTON_ROLE b" +
                    " where a.table_name=b.form_name and type in('PO','CPO') and b.BUTTONS_TYPE=2 and b.role_id in ("+whereSql+") and a.TABLE_NAME ='"+tableName+"'";
            List<Map> maps = poTableService.listMapBySql(uploadSql);
            if (maps == null || "0".equals(maps.get(0).get("COUNT(1)").toString())) {
                ajaxResult.put("flag", "fail");
                return ajaxResult.getJson();
            }
        } catch (Exception e) {
            ajaxResult.put("flag", "fail");
        }
        return ajaxResult.getJson();
    }
}
