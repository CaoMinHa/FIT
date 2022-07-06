package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.User;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.service.bi.PoTaskService;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 全部任務界面
 */
@Controller
@RequestMapping("/bi/poTaskList")
public class PoTaskListController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PoTaskService poTaskService;
    @Autowired
    private PoTableService poTableService;


    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName=loginUser.getUsername();
            String roleSql="select distinct r.code,r.grade,r.name  from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " WHERE  u.username="+"'"+userName+"'"+" order by r.grade";
            List<Map> roleList = userService.listMapBySql(roleSql);
            pageRequest.setOrderBy("serial");
            pageRequest.setOrderDir("asc");
            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
            model.addAttribute("roles", roleList);
            model.addAttribute("attribute", user.getAttribute());

        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poTaskList/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String name,String type,
                       String taskStatus,String QDate ,String QDateEnd) {
        try {
            pageRequest.setPageSize(14);
            String sql="select  ID , TYPE ,NAME, FLAG ,remark,CREATE_USER_REAL, create_time, " +
                    " UPDATE_USER_REAL, UPDTAE_TIME from FIT_PO_TASK WHERE 1=1 ";
            if(!StringUtils.isBlank(name)){
                name="%"+name+"%";
                sql=sql+" and name like "+"'"+name+"'";
            }
            if(!StringUtils.isBlank(type)){
                if(type.equalsIgnoreCase("FIT_PO_CD_MONTH_DOWN")){
                    sql=sql+" and type ='FIT_PO_CD_MONTH_DTL' ";
                }else{
                    sql=sql+" and type ="+"'"+type+"'";
                }
            }
            if(!StringUtils.isBlank(taskStatus)){
                sql=sql+" and flag ='"+taskStatus+"'";
            }
            if(!StringUtils.isBlank(QDate)){
                sql =sql+" and create_time > '"+QDate+"'";
                if(!StringUtils.isBlank(QDateEnd)){
                    sql =sql+" and create_time between '"+QDate+"' and '"+QDateEnd+"'";
                }
            }else if(!StringUtils.isBlank(QDateEnd)){
                sql =sql+" and create_time < '"+QDateEnd+"'";
            }
            sql+=" order by create_time desc,flag asc";
            System.out.println(sql);
            Page<Object[]> page = poTaskService.findPageBySql(pageRequest, sql);
            int index=1;
            if(pageRequest.getPageNo()>1){
                index=2;
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", "FIT_PO_TASK");
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poTaskList/list";
    }

    @RequestMapping(value="/audit")
    public String userList(Model model, PageRequest pageRequest,HttpServletRequest request,String id,
                           String statusType) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            model.addAttribute("statusType", statusType);
            //查询当前任务明细所有的附件
            String fileId="select FILEID,FILENAME from fit_po_task_file where TASKID='"+id+"'";
            List<Map> fileList=poTableService.listMapBySql(fileId);
            model.addAttribute("fileList",fileList);

            String taskSql=" select type,name from FIT_PO_TASK WHERE ID="+"'"+id+"'";
            List<Map> maps = poTaskService.listMapBySql(taskSql);
            taskSql="select task_name,create_user,create_time,remark,flag from " +
                    "epmods.FIT_PO_TASK_LOG where TASK_NAME='"+maps.get(0).get("NAME").toString()+"' order by create_time desc";
            List<Map> taskLogList = poTaskService.listMapBySql(taskSql);
            model.addAttribute("taskLogList",taskLogList);
            System.out.println(maps);
            if(maps!=null||maps.size()==1){
                String tableName=maps.get(0).get("TYPE").toString();
                model.addAttribute("taskType", tableName);
                model.addAttribute("taskId",id);
                model.addAttribute("taskName", maps.get(0).get("NAME").toString());

                if("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(tableName)){
                    tableName="FIT_PO_CD_MONTH_DOWN";
                }
                //区分是否是CPO任务 跳转到不同页面
                if("FIT_PO_Target_CPO_CD_DTL".equalsIgnoreCase(tableName)){
                    String sql="select  ID,PO_CENTER,COMMODITY_MAJOR ,NO_PO_TOTAL,NO_CD_AMOUNT, NO_CD ,NO_CPO,PO_TOTAL ,CD_AMOUNT, CD,CPO ,username ,FLOW_TIME  " +
                            "from FIT_PO_TARGET_CPO_CD_DTL_V where task_id="+"'"+id+"'";
                    System.out.println(sql);
                    String sql1="select ID from FIT_PO_TARGET_CPO_CD_DTL_V where task_id="+"'"+id+"'";
                    pageRequest.setPageSize(poTableService.listBySql(sql1).size());
                    Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
                    model.addAttribute("page", page);
                    int index=1;
                    if(pageRequest.getPageNo()>1){
                        index=2;
                    }
                    model.addAttribute("index", index);
                    return "/bi/poTaskList/cpo";
                }else{
                    //查询任务明细及增加汇总数
                    model=this.selectPoTask(model,tableName,locale,pageRequest,id);
                    return "/bi/poTaskList/audit";
                }
            }else{
                logger.error("沒有任務綁定數據:");
            }
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poTaskList/audit";
    }

//查找任务
    public Model selectPoTask(Model model,String tableName, Locale locale,PageRequest pageRequest,String id) throws Exception {
        PoTable poTable = poTableService.get(tableName);
        List<PoColumns> columns = poTable.getColumns();
        for (PoColumns poColumns : columns) {
            poColumns.setComments(getByLocale(locale, poColumns.getComments()));
        }
        String sql="select ";
        String sqlSum="select ";
        for (PoColumns column : columns) {
            String columnName = column.getColumnName();
            if (column.getDataType().equalsIgnoreCase("number")) {
                sql+=columnName+",";
                if("FIT_PO_CD_MONTH_DOWN".equalsIgnoreCase(poTable.getTableName())) {
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
                }else{
                    sqlSum += "sum(" + columnName + "),";
                }
            }else if (column.getDataType().equalsIgnoreCase("date")) {
                sql+="to_char("+columnName+",'dd/mm/yyyy'),";
                sqlSum+="'' " + columnName + ",";
            }else{
                sql+=columnName+",";
                sqlSum+="'' " + columnName + ",";
            }
        }
        sql=sql.substring(0,sql.length()-1);
        sql+=" from "+poTable.getTableName()+" where 1=1 and task_id="+"'"+id+"'";
        sqlSum =sqlSum.substring(0, sqlSum.length() - 1);
        sqlSum +=" from "+poTable.getTableName()+" where 1=1 and task_id="+"'"+id+"'";
        pageRequest.setOrderBy(columns.get(3).getColumnName()+","+columns.get(1).getColumnName()+","+columns.get(2).getColumnName()+","+columns.get(0).getColumnName());
        pageRequest.setOrderDir("asc,asc,asc,asc");
        Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
        PageRequest pageRequest1=new PageRequest();
        pageRequest1.setPageNo(1);
        pageRequest1.setPageSize(1);
        Page<Object[]> pages = poTableService.findPageBySql(pageRequest1, sqlSum);
        page.getResult().addAll(pages.getResult());
        //查找任务对于的附档
        model.addAttribute("tableName", poTable.getTableName());
        model.addAttribute("page", page);
        model.addAttribute("columns", columns);
        int index=1;
        if(pageRequest.getPageNo()>1){
            index=2;
        }
        model.addAttribute("index", index);
        return model;
    }
}