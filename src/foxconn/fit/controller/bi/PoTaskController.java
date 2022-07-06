package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.User;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.service.bi.PoTaskService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 作爲審核模塊開發
 * @create 2021-04-20 16:04
 **/
@Controller
@RequestMapping("/bi/poTask")
public class PoTaskController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PoTaskService poTaskService;
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private PoTableDao poTableDao;


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
        return "/bi/poTask/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,HttpServletRequest request,String name,String type,String date,String roleCode) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName=loginUser.getUsername();
            String userSql=" select sbu,email,COMMODITY_MAJOR from fit_user where username="+"'"+userName+"'";
            List<Map> maps = userService.listMapBySql(userSql);
            String sbu="";
            String email="";
            String commodityMajor="";
            if(maps!=null&&maps.size()==1){
                sbu=maps.get(0).get("SBU")==null?"":maps.get(0).get("SBU").toString();
                email=maps.get(0).get("EMAIL")==null?"":maps.get(0).get("EMAIL").toString();
                commodityMajor=maps.get(0).get("COMMODITY_MAJOR")==null?"":maps.get(0).get("COMMODITY_MAJOR").toString();
            }
            String sql="select  ID , TYPE ,NAME, FLAG ,remark,CREATE_USER_REAL, create_time, " +
                    " UPDATE_USER_REAL, UPDTAE_TIME from FIT_PO_TASK WHERE 1=1 ";
            if(!StringUtils.isBlank(name)){
                name="%"+name+"%";
                sql=sql+" and name like "+"'"+name+"'";
            }else if(!StringUtils.isBlank(type)){
                if(type.equalsIgnoreCase("FIT_PO_CD_MONTH_DOWN")){
                    sql=sql+" and type ='FIT_PO_CD_MONTH_DTL' ";
                }else{
                    sql=sql+" and type ="+"'"+type+"'";
                }
            }else if(!StringUtils.isBlank(date)){
                date=date+"%";
                sql=sql+" and name like"+"'"+date+"'";
            }
            //  数据状态：未提交->初審中->終審中->已核准
            //  不同角色人员，自己看到自己对应阶段的数据
            //1 采购员看到自己创建的数据 by 物料大类 Sourcer
            //2 企划 看到自己创建的数据  by SBU  MM
            //3 TDC 看到自己创建的数据  cpo
            //4 课级主管 阶段是初审中 by 物料大类（3表）class
            //5 部级主管 阶段是終審中 by 物料大类（3表）Manager
            //备注 若同一物料大类部门只维护了部级主管，则任务直接提交至部级主管
            //6 TDC部主管 阶段是初审中 by 物料大类（cpo表）class 可修改cpo
            //7 CPO核准 阶段是終審中 by 物料大类（cpo表）Manager 可修改cpo
            //备注 若同一物料大类部门只维护了部级主管，则任务直接提交至部级主管
            //8 系统管理员 所有数据 且阶段是終審中(sbu表)可审核 KeyUser
            if("KEYUSER".equalsIgnoreCase(roleCode)){

            }else if("SOURCER".equalsIgnoreCase(roleCode)){
                sql+=" and CREATE_USER="+"'"+userName+
                        "' and Type in " +
                        " ('FIT_PO_BUDGET_CD_DTL','FIT_ACTUAL_PO_NPRICECD_DTL','FIT_PO_CD_MONTH_DTL') ";
                roleCode="BASE";
            }else if("MM".equalsIgnoreCase(roleCode)){
                sql+=" and CREATE_USER="+"'"+userName+
                        "'  and type='FIT_PO_SBU_YEAR_CD_SUM' and instr(',"+sbu+",',','||SBU||',')>0";
                roleCode="BASE";
            }else if("TDC".equalsIgnoreCase(roleCode)){
                sql+=" and CREATE_USER="+"'"+userName+
                        "' and type='FIT_PO_Target_CPO_CD_DTL' ";
                roleCode="TDC";
            }else if("PD".equalsIgnoreCase(roleCode)){
                sql+= "  and type='FIT_PO_SBU_YEAR_CD_SUM' and instr(',"+sbu+",',','||SBU||',')>0 and flag='1' or AUDIT_ONE='"+userName+"' ";
            }else if("CLASS".equalsIgnoreCase(roleCode)){
                sql+= " and instr(',"+commodityMajor+",',','||COMMODITY_MAJOR||',')>0 and flag='1' or AUDIT_ONE='"+userName+"' ";
            }else if("MANAGER".equalsIgnoreCase(roleCode)){
                sql+= " and instr(',"+commodityMajor+",',','||COMMODITY_MAJOR||',')>0 and flag='2' or AUDIT_TWO='"+userName+"' ";
            }else if("T_MANAGER".equalsIgnoreCase(roleCode)){
                sql+= " and type='FIT_PO_Target_CPO_CD_DTL' and flag='1' or AUDIT_ONE='"+userName+"' ";
            }else if("CPO".equalsIgnoreCase(roleCode)){
                sql+=" and type='FIT_PO_Target_CPO_CD_DTL' and flag='2' or AUDIT_TWO='"+userName+"' ";
            }else{
                sql+=" and 1=0";
            }
            sql+=" order by create_time desc,flag asc";
            System.out.println(sql);
            model.addAttribute("email", email);
            model.addAttribute("sbu", sbu);
            model.addAttribute("commodityMajor", commodityMajor);
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
        return "/bi/poTask/list";
    }
    @RequestMapping(value="/addCpo")
    @ResponseBody
    @Transactional
    public String addRole(AjaxResult ajaxResult, HttpServletRequest request,String year) {
        try {
            ajaxResult=poTaskService.addCpoTask(ajaxResult,year);
        } catch (Exception e) {
            logger.error("新增cpo任务失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "新增cpo任务失败(add Cpo Task Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    @RequestMapping(value="/audit")
    public String userList(Model model, PageRequest pageRequest,HttpServletRequest request,String id,
                           String statusType,String role) {
        try {
            HttpSession session = request.getSession(false);
            session.setAttribute("detailsTsak","N");
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
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
                    if( "TDC".equalsIgnoreCase(role) || "BASE".equalsIgnoreCase(role)|| "SOURCER".equalsIgnoreCase(role)||"MM".equalsIgnoreCase(role)){
                        model.addAttribute("user", "N");
                    }else if("CLASS".equalsIgnoreCase(role) || "T_MANAGER".equalsIgnoreCase(role)||"PD".equalsIgnoreCase(role)){
                        model.addAttribute("user", "C");
                    }else if("CPO".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role)){
                        model.addAttribute("user", "Z");
                    }else if("KEYUSER".equalsIgnoreCase(role)){
                        model.addAttribute("user", "K");
                        if("KEYUSER".equalsIgnoreCase(role)&&"FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(tableName)){
                            model.addAttribute("user", "TS");
                        }
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
                    return "/bi/poTask/cpo";
                }else{
                    //查询任务明细及增加汇总数
                    model=this.selectPoTask(model,tableName,locale,pageRequest,id);
                    return "/bi/poTask/audit";
                }
            }else{
                logger.error("沒有任務綁定數據:");
            }
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/poTask/audit";
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


    /*
     *   submitTask  任务提交
     *   sbu flag=0提交到任務管理員 flag=2
     *   采購數據 flag=0提交到課級flag=1->科級flag=2
     *   若只維護了部級別主管 直接提交到部級別
     *   tdc  flag=0->tdc部門flag=1-》cpo核准 flag=2
     * */
    @RequestMapping(value="/submitTask")
    @ResponseBody
    public String submitTask(AjaxResult ajaxResult, HttpServletRequest request,String id,String taskType,String roleCode) {
        try {
            ajaxResult=poTaskService.submit(ajaxResult,taskType,id,roleCode);
        } catch (Exception e) {
            logger.error("提交任务失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "提交任务失败(sbumit Task Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    /**
     * 初审
     * @param ajaxResult
     * @param request
     * @param id
     * @param taskType
     * @param remark
     * @param status
     * @param roleCode
     * @return
     */
    @RequestMapping(value="/submitOneAudit")
    @ResponseBody
    public String submitOneAudit(AjaxResult ajaxResult, HttpServletRequest request,String id,String taskType,String remark,String status,String roleCode) {
        try {
            ajaxResult=poTaskService.submitOne(ajaxResult,taskType,id,status,remark,roleCode);
        } catch (Exception e) {
            logger.error("初审任务失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "初审任务失败(submit One Audit Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }


    /**
     * 终审
     * @param ajaxResult
     * @param request
     * @param id
     * @param taskType
     * @param remark
     * @param status
     * @return
     */
    @RequestMapping(value="/submitAudit")
    @ResponseBody
    public String submitEndAudit(AjaxResult ajaxResult, HttpServletRequest request,String id,String taskType,String remark,String status) {
        try {
            ajaxResult=poTaskService.submitEnd(ajaxResult,taskType,id,status,remark);
        } catch (Exception e) {
            logger.error("终审任务失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "终审任务失败(submit Audit Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    /**
     *  管理員取消審批任務，所有任務退回未提交 flag=0
     * @param ajaxResult
     * @param request
     * @param id
     * @param taskType
     * @return
     */
    @RequestMapping(value="/cancelAudit")
    @ResponseBody
    public String cancelAudit(AjaxResult ajaxResult, HttpServletRequest request,String id,String taskType) {
        try {
            ajaxResult=poTaskService.cancelAudit(ajaxResult,taskType,id);
        } catch (Exception e) {
            logger.error("取消審批任务失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "取消審批任务失败(cancel Audit Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    /*
          TDC取消審任務，任務數據刪除，且所綁定的業務數據flag=null task_id清空
         */
    @RequestMapping(value="/cancelTask")
    @ResponseBody
    public String cancelTask(AjaxResult ajaxResult, HttpServletRequest request,String id) {
        try {
            ajaxResult=poTaskService.cancelTask(ajaxResult,id);
        } catch (Exception e) {
            logger.error("取消審批任务失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "取消審批任务失败(cancel Audit Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }

    /**
     * 刪除任務附檔
     * @param request
     * @param response
     * @param result
     * @param fileId
     * @return
     */
    @RequestMapping(value = "deleteUrl")
    @ResponseBody
    @Log(name = "任務附檔-->刪除")
    public String deleteUrl(HttpServletRequest request, HttpServletResponse response,
                            AjaxResult result,String fileId){
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "刪除成功", "Delete success"));
        try {
            String realPath = request.getRealPath("");
            //获取存储路径文件夹
            File outFile=new File(realPath+File.separator+"static"+File.separator+"taskFile"+File.separator+fileId);
            if(outFile.isDirectory()) {
                File[] files = outFile.listFiles();
                for (int i = 0; i < files.length; i++) {
                    //删除子文件
                    if (files[i].isFile()) {
                        files[i].delete();
                    }
                }
                outFile.delete();
                String deleteSql="delete from fit_po_task_file where FILEID='"+fileId+"'";
                poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
            }else {
                result.put("msg", getLanguage(locale, "刪除失敗，未找到文件路徑。", "Failed to delete the file because the file path was not found."));
            }
        }catch (Exception e) {
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "任务附档-->上传")
    public String upload(HttpServletRequest request, HttpServletResponse response,
                         AjaxResult result,String taskId) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        File localFile = null;
        try {
            String realPath = request.getRealPath("");
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            if (mutipartFiles != null && mutipartFiles.size() > 0) {
                MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];
                String suffix = "";
                if (file.getOriginalFilename().lastIndexOf(".") != -1) {
                    suffix = file.getOriginalFilename().substring(
                            file.getOriginalFilename().lastIndexOf(".") + 1,
                            file.getOriginalFilename().length());
                    suffix = suffix.toLowerCase();
                }
                if (!"xls".equals(suffix) && !"xlsx".equals(suffix)&& ! "pdf".equals(suffix)) {
                    result.put("flag", "fail");
                    result.put("msg", getLanguage(locale, "請上傳正確格式的Excel文件", "The format of excel is error"));
                    return result.getJson();
                } else {
                    String Id = Long.toString(System.currentTimeMillis());
                    //获取存储路径文件夹
                    File outFile=new File(realPath+File.separator+"static"+File.separator+"taskFile"+File.separator+Id);
                    outFile.mkdirs();
                    //保存到本地
                    localFile = new File(outFile.getAbsolutePath() +File.separator +file.getOriginalFilename());
                    file.transferTo(localFile);
                    UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                    String user = loginUser.getUsername();
                    String insertSql="insert into FIT_PO_TASK_FILE(CREATE_USER,TASKID,FILEURL,FILENAME,FILEID) " +
                            "values ('"+user+"','"+taskId+"','"+Id+File.separator +file.getOriginalFilename()+"','"+
                            file.getOriginalFilename()+"','"+Id+"')";
                    poTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
                    result.put("fileName",file.getOriginalFilename());
                    result.put("fileId",Id);
                }
            } else {
                result.put("flag", "fail");
                result.put("msg", getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
            }
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }
}