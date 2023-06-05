package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.service.base.UserDetailImpl;
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
    private PoTaskService poTaskService;

    @RequestMapping(value = "index")
    public String index(Model model) {
        try {
            model.addAttribute("roles", poTaskService.index());
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poTaskComplete/index";
    }

    @RequestMapping(value="/list")
    @Log(name = "採購任務-->查看列表")
    public String list(Model model, PageRequest pageRequest,@Log(name = "任務名稱") String name,
                       @Log(name = "任務類型") String type,@Log(name = "任務時間") String date,@Log(name = "用戶角色") String roleCode) {
        try {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName=loginUser.getUsername();
            String userSql=" select sbu,COMMODITY_MAJOR from fit_user where username="+"'"+userName+"'";
            List<Map> maps = poTaskService.listMapBySql(userSql);
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
            List<Map> fileList=poTaskService.listMapBySql(fileId);
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
                    pageRequest.setPageSize(poTaskService.listBySql(sql1).size());
                    Page<Object[]> page = poTaskService.findPageBySql(pageRequest, sql);
                    model.addAttribute("page", page);
                    int index=1;
                    if(pageRequest.getPageNo()>1){
                        index=2;
                    }
                    model.addAttribute("index", index);
                    return "/bi/poTaskComplete/cpo";
                }else{
                    //查询任务明细及增加汇总数
                    model=poTaskService.selectPoTask(model,tableName,locale,pageRequest,id);
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
}