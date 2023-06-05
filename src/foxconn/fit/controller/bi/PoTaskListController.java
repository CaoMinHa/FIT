package foxconn.fit.controller.bi;

import foxconn.fit.controller.BaseController;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.service.bi.PoTaskService;
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
    private PoTaskService poTaskService;
    @Autowired
    private PoTableService poTableService;


    @RequestMapping(value = "index")
    public String index(Model model) {
        try {
            model.addAttribute("roles", poTaskService.index());
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/poTaskList/index";
    }
    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest,String name,String type,
                       String taskStatus,String QDate ,String QDateEnd) {
        try {
            pageRequest.setPageSize(14);
            String sql="select  ID , TYPE ,NAME, FLAG ,remark,CREATE_USER_REAL, create_time, " +
                    " UPDATE_USER_REAL, UPDTAE_TIME from FIT_PO_TASK WHERE 1=1 and flag>-1";
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
                    model=poTaskService.selectPoTask(model,tableName,locale,pageRequest,id);
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


}