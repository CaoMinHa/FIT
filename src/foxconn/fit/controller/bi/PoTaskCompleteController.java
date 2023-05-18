package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
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
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/bi/poTaskComplete")
public class PoTaskCompleteController extends BaseController {

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
                    " WHERE  u.username="+"'"+userName+"' and r.type='PO' order by r.grade";
            List<Map> roleList = userService.listMapBySql(roleSql);
            pageRequest.setOrderBy("serial");
            pageRequest.setOrderDir("asc");
            model.addAttribute("roles", roleList);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poTaskComplete/index";
    }

    @RequestMapping(value="/list")
    @Log(name = "採購任務-->查看列表")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,@Log(name = "任務名稱") String name,
                       @Log(name = "任務類型") String type,@Log(name = "任務時間") String date,@Log(name = "用戶角色") String roleCode) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName=loginUser.getUsername();
            String userSql=" select sbu,COMMODITY_MAJOR from fit_user where username="+"'"+userName+"'";
            List<Map> maps = userService.listMapBySql(userSql);
            String sbu="";
            String commodityMajor="";
            if(maps!=null&&maps.size()==1){
                sbu=maps.get(0).get("SBU")==null?"":maps.get(0).get("SBU").toString();
                commodityMajor=maps.get(0).get("COMMODITY_MAJOR")==null?"":maps.get(0).get("COMMODITY_MAJOR").toString();
            }
            String sql="select  ID , TYPE ,NAME, FLAG ,remark,CREATE_USER_REAL, create_time, " +
                    " UPDATE_USER_REAL, UPDTAE_TIME from FIT_PO_TASK WHERE flag=3 ";
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
            if(!StringUtils.isBlank(date)){
                date=date+"%";
                sql=sql+" and name like"+"'"+date+"'";
            }
            if("KEYUSER".equalsIgnoreCase(roleCode)){

            }else if("SOURCER".equalsIgnoreCase(roleCode)||"specialSourcer".equalsIgnoreCase(roleCode)){
                sql+=" and CREATE_USER="+"'"+userName+"' and Type in ('FIT_PO_BUDGET_CD_DTL','FIT_ACTUAL_PO_NPRICECD_DTL','FIT_PO_CD_MONTH_DTL') ";
                roleCode="BASE";
            }else if("MM".equalsIgnoreCase(roleCode)){
                sql+=" and CREATE_USER="+"'"+userName+"'  and type='FIT_PO_SBU_YEAR_CD_SUM' and instr(',"+sbu+",',','||SBU||',')>0";
                roleCode="BASE";
            }else if("TDC".equalsIgnoreCase(roleCode)){
                sql+="and type='FIT_PO_Target_CPO_CD_DTL' ";
            }else if("PD".equalsIgnoreCase(roleCode)){
                sql+= "  and type='FIT_PO_SBU_YEAR_CD_SUM' and instr(',"+sbu+",',','||SBU||',')>0 and (flag='1' or AUDIT_ONE='"+userName+"' )";
            }else if("CLASS".equalsIgnoreCase(roleCode)){
                sql+= " and instr(',"+commodityMajor+",',','||COMMODITY_MAJOR||',')>0 and (flag='1' or AUDIT_ONE='"+userName+"') ";
            }else if("specialClass".equalsIgnoreCase(roleCode)){
                sql+= " and instr(',"+sbu+",',','||COMMODITY_MAJOR||',')>0 and (flag='1' or AUDIT_ONE='"+userName+"') ";
            }else if("MANAGER".equalsIgnoreCase(roleCode)){
                sql+= " and instr(',"+commodityMajor+",',','||COMMODITY_MAJOR||',')>0 and (flag='2' or AUDIT_TWO='"+userName+"') ";
            }else if("specialManager".equalsIgnoreCase(roleCode)){
                sql+= " and instr(',"+sbu+",',','||COMMODITY_MAJOR||',')>0 and (flag='2' or AUDIT_TWO='"+userName+"') ";
            }else if("T_MANAGER".equalsIgnoreCase(roleCode)){
                sql+= " and type='FIT_PO_Target_CPO_CD_DTL' and (flag='1' or AUDIT_CPO='"+userName+"') ";
            }else if("PLACECLASS".equalsIgnoreCase(roleCode)){
                sql+=" and type='FIT_PO_Target_CPO_CD_DTL' and (flag='10' or AUDIT_ONE  ='"+userName+"') ";
            }else if("CPO".equalsIgnoreCase(roleCode)){
                sql+=" and type='FIT_PO_Target_CPO_CD_DTL' and (flag='2' or AUDIT_TWO='"+userName+"') ";
            }else{
                sql+=" and 1=0";
            }
            sql+=" order by create_time desc,flag asc";
            System.out.println(sql);
            model.addAttribute("role", roleCode);
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
        return "/bi/poTaskComplete/list";
    }
    @RequestMapping(value="/audit")
    @Log(name = "採購任務-->查看任務詳細信息")
    public String userList(Model model, PageRequest pageRequest,HttpServletRequest request,@Log(name="任務ID") String id,
                           String statusType,String role) {
        try {
            HttpSession session = request.getSession(false);
            session.setAttribute("detailsTsak","N");
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            model.addAttribute("statusType", statusType);
            model.addAttribute("role", role);
            //查询当前任务明细所有的附件
            String fileId="select FILEID,FILENAME from fit_po_task_file where TASKID='"+id+"'";
            List<Map> fileList=poTableService.listMapBySql(fileId);
            model.addAttribute("fileList",fileList);

            String taskSql=" select type,name from FIT_PO_TASK WHERE ID="+"'"+id+"'";
            List<Map> maps = poTaskService.listMapBySql(taskSql);
            taskSql="select task_name,create_user,create_time,remark,flag from " +
                    "epmods.FIT_PO_TASK_LOG where TASK_NAME='"+maps.get(0).get("NAME").toString()+"' order by create_time desc ";
            List<Map> taskLogList = poTaskService.listMapBySql(taskSql);
            model.addAttribute("taskLogList",taskLogList);
            System.out.println(maps);
            if(maps!=null||maps.size()==1){
                String tableName=maps.get(0).get("TYPE").toString();
                model.addAttribute("taskType", tableName);
                model.addAttribute("taskId",id);
                model.addAttribute("taskName", maps.get(0).get("NAME").toString());
                //根据角色判断当前明细所在那个节点
                if(null!= role){
                    if( "TDC".equalsIgnoreCase(role) || "BASE".equalsIgnoreCase(role)|| "SOURCER".equalsIgnoreCase(role)||"specialSourcer".equalsIgnoreCase(role)||"MM".equalsIgnoreCase(role)){
                        model.addAttribute("user", "N");
                    }else if("CLASS".equalsIgnoreCase(role)||"specialClass".equalsIgnoreCase(role) || "T_MANAGER".equalsIgnoreCase(role)||"PD".equalsIgnoreCase(role)){
                        model.addAttribute("user", "C");
                    }else if("CPO".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role)||"specialManager".equalsIgnoreCase(role)){
                        model.addAttribute("user", "Z");
                    }else if("KEYUSER".equalsIgnoreCase(role)){
                        model.addAttribute("user", "K");
                        if("KEYUSER".equalsIgnoreCase(role)&&"FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)){
                            model.addAttribute("keyUser", "TS");
                        }
                    }else if("PLACECLASS".equalsIgnoreCase(role)){
                        model.addAttribute("user", "P");
                    }
                }
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
                    return "/bi/poTaskComplete/cpo";
                }else{
                    //查询任务明细及增加汇总数
                    model=this.selectPoTask(model,tableName,locale,pageRequest,id);
                    return "/bi/poTaskComplete/audit";
                }
            }else{
                logger.error("沒有任務綁定數據:");
            }
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poTaskComplete/audit";
    }

    /**
     * 頁面list數據加載
     */
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
                        case "PO_TARGET_CPO":
                            sqlSum += "to_char(decode((sum(YEAR_TOTAL)+sum(PO_TARGET_CD)),0,null,sum(PO_TARGET_CD)/(sum(YEAR_TOTAL)+sum(PO_TARGET_CD))*100),9999999999.9999) PO_TARGET_CPO,";
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
                            sqlSum += " case when sum(YEAR_CD_AMOUNT) = 0 then 0 else sum(YEAR_CD_AMOUNT)/(sum(YEAR_CD_AMOUNT)+sum(PO_AMOUNT))*100 end YEAR_CD ,";
                            break;
                        default:
                            sqlSum += "sum(" + columnName + "),";
                            break;
                    }
                }else if("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(poTable.getTableName())){
                    switch (columnName) {
                        case "CD_RATIO":
                            sqlSum += " sum(CD_AMOUNT)/(sum(PO_AMOUNT)+sum(CD_AMOUNT))*100 CD_RATIO ,";
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