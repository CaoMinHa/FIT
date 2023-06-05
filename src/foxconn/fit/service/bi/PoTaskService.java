package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoTaskDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoCdMonthDown;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.entity.bi.PoTask;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.EmailUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PoTaskService extends BaseService<PoTask> {

    @Autowired
    private PoTaskDao poTaskDao;
    @Autowired
    private PoRoleService roRoleService;
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private InstrumentClassService instrumentClassService;

    @Value("${accessUrl}")
    String accessUrl;


    @Override
    public BaseDaoHibernate<PoTask> getDao() {
        return poTaskDao;
    }


    public void updateData(String updateSql) {
        poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
    }

    public List<Map> index(){
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String userName=loginUser.getUsername();
        String roleSql="select distinct r.code,r.grade,r.name  from  fit_user u \n" +
                " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                " WHERE  u.username="+"'"+userName+"' and r.type='PO' order by r.grade";
        List<Map> roleList = this.listMapBySql(roleSql);
        return roleList;
    }

    /**
     * CPO目标任务提交
     */
    public AjaxResult addCpoTask(AjaxResult ajaxResult,String year) {
        String flagSql="select  COUNT(*)  " + "from FIT_PO_Target_CPO_CD_DTL where year='"+year+"' and flag is not null ";
        List<Map> countMaps = poTableService.listMapBySql(flagSql);
        if(countMaps!=null&&"0".equals(countMaps.get(0).get("COUNT(*)").toString())){
            String taskId = UUID.randomUUID().toString();
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String user = loginUser.getUsername();
            List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"' and type='BI'");
            if(null==userName.get(0)){
                userName.set(0,user);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet = df.format(new Date());
            String updateSql = "update epmods.FIT_PO_TARGET_CPO_CD_DTL set flag='0',TASK_ID='" + taskId + "'," +
                    " FLOW_USER='" + user + "', FLOW_TIME='" + signTimet + "',USERNAME='"+userName.get(0)+"' WHERE YEAR='"+ year+"' ";
            String like = year + "%";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
            String deleteSql = " delete from FIT_PO_TASK where name like " + "'" + like + "'" + " and type='FIT_PO_Target_CPO_CD_DTL'";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
            String name = year + "_SBU年度CD目標核准表";
            String sql = " insert into FIT_PO_TASK (ID,TYPE,NAME,FLAG,CREATE_USER,CREATE_TIME,UPDATE_USER,UPDTAE_TIME,CREATE_USER_REAL,UPDATE_USER_REAL) " +
                    " values ( ";
            sql = sql + "'" + taskId + "'," + "'FIT_PO_Target_CPO_CD_DTL'," + "'" + name + "'," + "'0'," + "'" + user + "'," + "'" + signTimet + "'," + "'" + user + "'," + "'" + signTimet + "'" +
                    ",'"+userName.get(0)+"','"+userName.get(0)+"')";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
        }else{
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "任務已創建，不要重複提交 ");
        }
        return ajaxResult;
    }

    /**
      提交任務 三表 sbu cpo
      查詢對應的任務辦理人 用戶名+郵箱 並發送郵件
      type 任務數據
      cm 物料大類
     */

    public AjaxResult submit(AjaxResult ajaxResult,String type,String taskId,String roleCode,String remark) throws Exception {
        String taskName = "select NAME from fit_po_task where id='" + taskId + "'";
        List<String> taskList = roRoleService.listBySql(taskName);
        String[] task= taskList.get(0).split("_");
        String title=task[1]+"_"+task[0]+"採購BI平臺簽核通知，請勿回復";

        String sql="";
        String msg="";
        String flag="1";
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String username=loginUser.getRealname()==null?loginUser.getUsername():loginUser.getRealname();
        //表示三表是否跳了審核步驟，其中0代表每跳，1代表跳了
        String step="";
        List<String> emailList=new ArrayList<>();
        if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(type)|| "FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(type)|| "FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(type)) {
            if("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(type)){
                String update="update FIT_PO_CD_MONTH_DTL set flag='0' where TASK_ID='"+taskId+"'";
                //修改数据库触发down表数据重新产生
                this.updateData(update);
                ajaxResult=this.checkCDObjectiveSummaryStatus(ajaxResult,taskId);
                Map<Object, Object> map=ajaxResult.getResult();
                if(map.get("flag").equals("fail")){
                    return ajaxResult;
                }
            }
            sql = " select distinct u.email from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " WHERE  r.code='CLASS' and u.type='BI'  and u.email is not null and instr(','||u.COMMODITY_MAJOR||',', (select ','||COMMODITY_MAJOR||',' from FIT_PO_TASK" +
                    " WHERE id='"+taskId+"')) > 0";
            if(roleCode.equalsIgnoreCase("specialSourcer")){
                sql = " select distinct u.email from  fit_user u \n" +
                        " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                        " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                        " WHERE  r.code='"+replaceRole(roleCode,"1")+"' and u.type='BI'  and u.email is not null and instr(','||u.sbu||',', (select ','||COMMODITY_MAJOR||',' from FIT_PO_TASK" +
                        " WHERE id='"+taskId+"')) > 0";
            }
            List<String> classMaps = roRoleService.listBySql(sql);
            if (classMaps.size() == 0) {
                sql = " select distinct u.email from  fit_user u \n" +
                        " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                        " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                        " WHERE  r.code='MANAGER' and u.type='BI'  and u.email is not null and instr(','||u.COMMODITY_MAJOR||',', " +
                        "(select ','||COMMODITY_MAJOR||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
                if(roleCode.equalsIgnoreCase("specialSourcer")){
                    sql = " select distinct u.email from  fit_user u \n" +
                            " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                            " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                            " WHERE  r.code='specialManager' and u.type='BI'  and u.email is not null and instr(','||u.sbu||',', (select ','||COMMODITY_MAJOR||',' from FIT_PO_TASK" +
                            " WHERE id='"+taskId+"')) > 0";
                }
                List<String> managers = roRoleService.listBySql(sql);
                flag="2";
                step="1";
                emailList=managers.stream().distinct().collect(Collectors.toList());
                msg="尊敬的主管:</br>&nbsp;&nbsp;採購CD核准任務請審核!";
            } else {
                emailList=classMaps.stream().distinct().collect(Collectors.toList());
                msg="尊敬的主管:</br>&nbsp;&nbsp;採購CD核准任務請審核!";
            }
        } else if ("FIT_PO_SBU_YEAR_CD_SUM".equals(type)) {
            sql = " select distinct  u.email from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " WHERE  r.code='PD' and u.type='BI' and u.email is not null and instr(','||u.SBU||',', " +
                    "(select ','||SBU||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
            List<String> keyUser = roRoleService.listBySql(sql);
            emailList=keyUser.stream().distinct().collect(Collectors.toList());
            msg="尊敬的主管:</br>&nbsp;&nbsp;"+username+"已在接口平臺提交"+task[1]+"_"+task[0]+" SBU採購CD目標，請及時完成初審!";
            title=task[1]+"_"+task[0]+"採購CD目標待簽核，請勿回復";
        } else if ("FIT_PO_Target_CPO_CD_DTL".equals(type)) {
            sql = " select distinct u.email from  fit_user u \n" +
                    " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    " WHERE  r.code='T_MANAGER' and u.type='BI' and u.email is not null";
            List<String> tManager = roRoleService.listBySql(sql);
            emailList=tManager.stream().distinct().collect(Collectors.toList());
            msg="尊敬的主管:</br>&nbsp;&nbsp;SBU年度CD目標核准表請審核!";
            title=task[0]+"年"+task[1]+"採購BI平臺簽核通知，請勿回復";
        }else{
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "任務類型出錯(Task Type Fail)");
            return ajaxResult;
        }
        if(emailList.size()==0){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "請聯係管理員維護對應崗位的郵箱(Task Type Fail)");
            return ajaxResult;
        }else {
            String emailVal="";
            for (String e:emailList) {
                emailVal=emailVal+e+",";
            }
            Boolean isSend = EmailUtil.emailCC(emailVal.substring(0,emailVal.length()-1),loginUser.getEmail(), title,msg+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+taskId+"&statusType="+flag+"&roleCode="+replaceRole(roleCode,"1")+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
            if(isSend){
                uploadTaskFlag(taskId,flag,type,remark,step,"T");
            }else{
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    /**
     * 提交采購CDby月份展開表任務的時候校驗是否SBU年度目標匯總表對應維度任務是否審批完
     * @param ajaxResult
     * @param taskId
     * @return
     */
    public  AjaxResult checkCDObjectiveSummaryStatus(AjaxResult ajaxResult,String taskId){
        String sql="select * from FIT_PO_CD_MONTH_DOWN where TASK_ID='"+taskId+"'";
        List<PoCdMonthDown> list=poTableService.listBySql(sql,PoCdMonthDown.class);
        for(int i=0;i<list.size();i++){
            PoCdMonthDown poCdMonthDown=list.get(i);
            String sqlCounrt="select COUNT(1) from FIT_PO_SBU_YEAR_CD_SUM where FLAG='3' and YEAR='"+poCdMonthDown.getYear()+"' and " +
                    "SBU='"+poCdMonthDown.getSbu()+"' and COMMODITY_MAJOR='"+poCdMonthDown.getCommodityMajor()+"'";
            List<Map> maps = poTableService.listMapBySql(sqlCounrt);
            if (maps == null || "0".equals(maps.get(0).get("COUNT(1)").toString())) {
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg","經系統檢驗\""+poCdMonthDown.getYear()+"年+"+poCdMonthDown.getSbu()+"+"
                        +poCdMonthDown.getCommodityMajor()+"\"的數據在\"SBU年度CD目標匯總表\"中沒有找到對應已審批的數據！");
                return ajaxResult;
            }
        }
        try {
           String msg=poTableService.validateMonth(taskId);
            if(!msg.equals("Y")) {
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", msg+ "配置的CD比例過低,請重新維護上傳");
            }
        }catch (Exception e){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "數據庫運行錯誤，請聯係管理員");
            return ajaxResult;
        }
        return ajaxResult;
    }
    /**
      初審 三表 cpo
      查詢對應的任務辦理人 用戶名+郵箱 並發送郵件
      type 任務數據
      cm 物料大類
     */
    public AjaxResult submitOne(AjaxResult ajaxResult,String type,String taskId,String status,String reamrk,String roleCode) {
        String taskName = "select NAME from fit_po_task where id='" + taskId + "'";
        List<String> taskList = roRoleService.listBySql(taskName);
        String[] task= taskList.get(0).split("_");
        String title=task[1]+"_"+task[0]+"採購BI平臺簽核通知，請勿回復";
        String sql = "";
        String msg="";
        String flag="2";
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        List<String> emailList=new ArrayList<>();
        String sqlC="select distinct email from fit_user where username=(select CREATE_USER from FIT_PO_TASK WHERE id='"+taskId+"') and type='BI' and email is not null";
        List<String> emailListC=roRoleService.listBySql(sqlC);
        String emailCC=emailListC.get(0);
        if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(type)|| "FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(type)|| "FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(type)) {
                if(!"0".equals(status)){
                    //退回
                    sql=sqlC;
                    msg="亲爱的同事:</br>&nbsp;&nbsp;採購CD核准任務已退回請處理!";
                    flag="-1";
                }else{
                    sql = " select distinct u.email from  fit_user u \n" +
                            " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                            " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                            " WHERE  r.code='MANAGER' and u.type='BI' and u.email is not null and instr(','||u.COMMODITY_MAJOR||',', " +
                            "(select ','||COMMODITY_MAJOR||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
                    if(roleCode.equalsIgnoreCase("specialClass")){
                        sql = " select distinct u.email from  fit_user u \n" +
                                " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                                " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                                " WHERE  r.code='"+replaceRole(roleCode,"1")+"' and u.type='BI'  and u.email is not null and instr(','||u.sbu||',', " +
                                "(select ','||COMMODITY_MAJOR||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
                    }
                    msg="尊敬的主管:</br>&nbsp;&nbsp;採購CD核准任務請審核!";
                }
                List<String> managers = roRoleService.listBySql(sql);
                emailList=managers.stream().distinct().collect(Collectors.toList());
        } else if ("FIT_PO_Target_CPO_CD_DTL".equals(type)) {
            String user = loginUser.getUsername();
            List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"' and type='BI'");
            if(null==userName.get(0)){
                userName.set(0,user);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet = df.format(new Date());
            String updateSql = " update FIT_PO_Target_CPO_CD_DTL set" +
                    " FLOW_USER=" + "'" + user + "'," + " FLOW_TIME=" + "'" + signTimet + "'" +", USERNAME='"+userName.get(0)+"'"+
                    " WHERE task_id= '" + taskId+"'";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
            if(!"0".equals(status)){
                //退回
                sql=sqlC;
                msg="亲爱的同事:</br>&nbsp;&nbsp;採購CD 目標CPO核准任務已退回請處理!";
                flag="-1";
            }else{
                sql = " select distinct u.email from  fit_user u \n" +
                        " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                        " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                        " WHERE  r.code='CPO' and u.type='BI' and u.email is not null";
                msg="尊敬的主管:</br>&nbsp;&nbsp;採購CD 目標CPO核准任務請審核!";
            }
            List<String> tManager = roRoleService.listBySql(sql);
            emailList=tManager.stream().distinct().collect(Collectors.toList());
            title=task[0]+"年"+task[1]+"採購BI平臺簽核通知，請勿回復";
        }else if ("FIT_PO_SBU_YEAR_CD_SUM".equals(type)) {
            if(!"0".equals(status)){
                //退回
                sql=sqlC;
                msg="亲爱的同事:</br>&nbsp;&nbsp;SBU年度CD目標核准任務已退回請處理!";
                flag="-1";
            }else{
                sql = " select distinct u.email from  fit_user u \n" +
                        " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                        " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                        " WHERE  r.code in('KEYUSER') and u.type='BI' and u.email is not null and instr(','||u.SBU||',', " +
                        "(select ','||SBU||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
                String sqlSbu = " select distinct u.email from  fit_user u \n" +
                        " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                        " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                        " WHERE  r.code in('SBUCompetent') and u.type='BI' and u.email is not null and instr(','||u.SBU||',', " +
                        "(select ','||SBU||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
                List<String> tManager = roRoleService.listBySql(sqlSbu);
                emailCC=emailCC+",";
                for (String e:tManager) {
                    emailCC=emailCC+e+",";
                }
                String username=loginUser.getRealname()==null?loginUser.getUsername():loginUser.getRealname();
                msg="尊敬的主管:</br>&nbsp;&nbsp;"+username+"已在接口平臺完成"+task[1]+"_"+task[0]+" SBU採購CD目標初級審批，請及時完成終審!";
            }
            List<String> tManager = roRoleService.listBySql(sql);
            emailList=tManager.stream().distinct().collect(Collectors.toList());
            title=task[1]+"_"+task[0]+"採購CD目標待簽核，請勿回復";
        }else{
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "任務類型出錯(Task Type Fail)");
            return ajaxResult;
        }
        if(emailList.size()==0&&emailListC.size()==0){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "請聯係管理員維護對應崗位的郵箱(Task Type Fail)");
            return ajaxResult;
        }else {
            if(flag.equalsIgnoreCase("-1")){
               roleCode=replaceRole("",taskId);
            }else if(flag.equalsIgnoreCase("2")){
               roleCode=replaceRole(roleCode,"1");
            }
            String emailVal="";
            for (String e:emailList) {
                emailVal=emailVal+e+",";
            }
            Boolean isSend = EmailUtil.emailCC(emailVal,emailCC, title,msg+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+taskId+"&statusType="+flag+"&roleCode="+roleCode+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
            if(isSend){
                uploadTaskFlag(taskId,flag,type,reamrk,"","C");
            }else{
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    /**
     * CPO目标任务初級審批
     */
    public AjaxResult CPOAudit(AjaxResult ajaxResult,String type,String taskId,String status,String reamrk,String roleCode) {
        String taskName = "select NAME from fit_po_task where id='" + taskId + "'";
        List<String> taskList = roRoleService.listBySql(taskName);
        String title=taskList.get(0)+"採購BI平臺簽核通知，請勿回復";

        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        List<String> emailList=new ArrayList<>();
        String sqlC="select distinct email from fit_user where username=(select CREATE_USER from FIT_PO_TASK WHERE id='"+taskId+"') and type='BI' and email is not null";
        List<String> emailListC=roRoleService.listBySql(sqlC);
        String emailCC=loginUser.getEmail()+","+emailListC.get(0);

            String user = loginUser.getUsername();
            List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"' and type='BI'");
            if(null==userName.get(0)){
                userName.set(0,user);
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet = df.format(new Date());
            String updateSql = " update FIT_PO_Target_CPO_CD_DTL set" +
                    " FLOW_USER=" + "'" + user + "'," + " FLOW_TIME=" + "'" + signTimet + "'" +", USERNAME='"+userName.get(0)+"'"+
                    " WHERE task_id= '" + taskId+"'";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
            String sql="";
            String flag="";
            String msg="";
            if(!"0".equals(status)){
                //退回
                msg="亲爱的同事:</br>&nbsp;&nbsp;採購CD 目標CPO核准任務已退回請處理!";
                flag="-1";
            }else{
                flag="10";
                sql = " select distinct u.email from  fit_user u \n" +
                        " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                        " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                        " WHERE  r.code='PLACECLASS' and u.type='BI' and u.email is not null";
                msg="尊敬的主管:</br>&nbsp;&nbsp;採購CD 目標CPO核准任務請審核!";
                List<String> tManager = roRoleService.listBySql(sql);
                emailList=tManager.stream().distinct().collect(Collectors.toList());
            }
        if(emailList.size()==0&&emailListC.size()==0){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "請聯係管理員維護對應崗位的郵箱(Task Type Fail)");
            return ajaxResult;
        }else {
            if(flag.equalsIgnoreCase("-1")){
                roleCode=replaceRole("",taskId);
            }else if(flag.equalsIgnoreCase("10")){
                roleCode=replaceRole(roleCode,"1");
            }
            String emailVal="";
            for (String e:emailList) {
                emailVal=emailVal+e+",";
            }
            Boolean isSend = EmailUtil.emailCC(emailVal,emailCC, title,msg+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+taskId+"&statusType="+flag+"&roleCode="+roleCode+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
            if(isSend){
                uploadTaskFlag(taskId,flag,type,reamrk,"","E");
            }else{
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    /**
     終審 三表 cpo
     查詢對應的任務辦理人 用戶名+郵箱 並發送郵件
     type 任務數據
     cm 物料大類
    */
    public AjaxResult submitEnd(AjaxResult ajaxResult,String type,String taskId,String status,String reamrk) {
        String msg="亲爱的同事:</br>&nbsp;&nbsp;";
        String flag="3";
        String sqlC="select distinct email from fit_user where username=(select CREATE_USER from FIT_PO_TASK WHERE id='"+taskId+"')  and type='BI' and email is not null";
        List<String> emailListC=roRoleService.listBySql(sqlC);
        emailListC=emailListC.stream().distinct().collect(Collectors.toList());
        String taskName = "select NAME,CREATE_USER_REAL from fit_po_task where id='" + taskId + "'";
        List<Map> taskList = roRoleService.listMapBySql(taskName);
        String[] task= taskList.get(0).get("NAME").toString().split("_");
        msg+=task[0]+"_"+task[1]+" ";
        if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(type)){
            msg+="採購CD手動匯總";
        }else if("FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(type)){
            msg+="實際採購非價格CD匯總";
        }else if("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(type)) {
            msg+="採購CDby月份";
        } else if ("FIT_PO_SBU_YEAR_CD_SUM".equals(type)) {
            msg+="SBU年度CD目標匯總";
        }else if ("FIT_PO_Target_CPO_CD_DTL".equals(type)) {
            msg+="SBU年度CD目標核准表";
        }
        if("0".equals(status)){
            msg+="任務終審已通過！";
        }else {
            msg+= "任務終審退回，請及時處理！";
            flag="-1";
        }
            if(emailListC.size()==0){
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "請聯係管理員維護對應崗位的郵箱(Task Type Fail)");
                return ajaxResult;
            }else {
                String title=task[1]+"_"+task[0]+"採購BI平臺簽核通知，請勿回復";
                Boolean isSend=false;
                if ("FIT_PO_SBU_YEAR_CD_SUM".equals(type)) {
                    title=task[0]+"_"+task[1]+"採購CD目標簽核通知，請勿回復";
                    String sqlSbu = " select distinct u.email from  fit_user u \n" +
                            " left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                            " left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                            " WHERE  r.code in('SBUCompetent') and u.type='BI' and u.email is not null and instr(','||u.SBU||',', " +
                            "(select ','||SBU||',' from FIT_PO_TASK WHERE id='"+taskId+"')) > 0";
                    List<String> tManager = roRoleService.listBySql(sqlSbu);
                    String emailCC="";
                    for (String e:tManager) {
                        emailCC=emailCC+e+",";
                    }
                    String email="";
                    for (String e:emailListC) {
                        email=email+e+",";
                    }
                    isSend = EmailUtil.emailCC(email,emailCC, title,msg+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+taskId+"&statusType="+flag+"&roleCode="+replaceRole("",taskId)+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
                }else {
                    isSend=EmailUtil.emailsMany(emailListC,title,msg+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+taskId+"&statusType="+flag+"&roleCode="+replaceRole("",taskId)+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
                }
                if(isSend){
                    if("0".equals(status)){
                        uploadTaskFlag(taskId,"3",type,reamrk,"","Z");
                        if ("FIT_PO_SBU_YEAR_CD_SUM".equalsIgnoreCase(type)) {
                            String sql = "select distinct u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and r.id=ur.role_id \n" +
                                    "and u.type='BI'and EMAIL is not null and r.code in ('CLASS','specialClass','ADMIN','PLACECLASS1','MANAGER','specialManager','PLACECLASS','T_MANAGER','TDC')  and COMMODITY_MAJOR is not null";
                            List<String> emailList = roRoleService.listBySql(sql);
                            emailList = emailList.stream().distinct().collect(Collectors.toList());
                            List<Map> maps = poTaskDao.listMapBySql("select * from(select REMARK from FIT_PO_TASK_LOG where TASK_NAME='"+taskList.get(0).get("NAME").toString()+"' and FLAG='-3' order by CREATE_TIME desc) where rownum=1 ");
                            if (maps != null && maps.size()>0 && null!=maps.get(0).get("REMARK")) {
                                msg = "尊敬的主管:</br> &nbsp;&nbsp;" + taskList.get(0).get("CREATE_USER_REAL").toString() + "對" + task[0] + "_" + task[1] + "年度SBU CD目標數據有作更新："+maps.get(0).get("REMARK").toString()+"，請盡快登陸系統進行確認，如有問題請及時與該SBU溝通,謝謝。";
                            }else {
                                msg = "尊敬的主管:</br> &nbsp;&nbsp;" + taskList.get(0).get("CREATE_USER_REAL").toString() + "已經完成" + task[0] + "_" + task[1] + "年度SBU CD目標數據，請盡快登陸系統進行確認，如有問題請及時與該SBU溝通,謝謝。";
                            }
                            Boolean isSends = EmailUtil.emailsMany(emailList, task[0] + "_" + task[1] + " SBU年度VOC", msg + "</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+taskId+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
                            if (!isSends) {
                                ajaxResult.put("flag", "fail");
                                ajaxResult.put("msg", "審核通過郵件通知發送失敗 (Failed to send the audit notification by email)");
                                return ajaxResult;
                            }
                        }else if ("FIT_PO_Target_CPO_CD_DTL".equalsIgnoreCase(type)){
                            String sql = "select distinct u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and r.id=ur.role_id \n" +
                                    "and u.type='BI'and EMAIL is not null and r.code in ('CLASS','specialClass','PLACECLASS1','PLACECLASS','T_MANAGER','TDC','ADMIN','MANAGER','specialManager') ";
                            List<String> emailList = roRoleService.listBySql(sql);
                            emailList = emailList.stream().distinct().collect(Collectors.toList());
                            msg="尊敬的主管:</br> &nbsp;&nbsp;"+taskList.get(0).get("NAME").toString()+"已呈核准, 請知悉并在 BI 接口平臺并上傳By月目標, 以及具體AR的開展。";
                            Boolean isSends = EmailUtil.emailsMany(emailList,task[0]+"年"+task[1]+"簽核通知，請勿回復",msg+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
                            if(!isSends){
                                ajaxResult.put("flag", "fail");
                                ajaxResult.put("msg", "審核通過郵件通知發送失敗 (Failed to send the audit notification by email)");
                                return ajaxResult;
                            }
                        }
                    }else{
                        uploadTaskFlag(taskId,"-1",type,reamrk,"","Z");
                    }
                }else{
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                    return ajaxResult;
                }
            }
        return ajaxResult;
    }

    /**
      提交任务,完善任务的SBU
    */
    public void uploadTaskFlag(String id, String flag, String tableName,String remark,String step,String checkStu) {
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String user = loginUser.getUsername();
            List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"' and type='BI'");
            if(null==userName.get(0)){
                userName.set(0,user);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet = df.format(new Date());
            String taskSql = " update FIT_PO_TASK set flag=" + "'" + flag + "'," + "UPDTAE_TIME =" + "'" + signTimet + "'," + " UPDATE_USER="
                    + "'" + user +"', UPDATE_USER_REAL ='"+userName.get(0)+"',";
            if("C".equalsIgnoreCase(checkStu)){
                taskSql+="AUDIT_ONE='"+user+"',";
            }else if("Z".equalsIgnoreCase(checkStu)){
                taskSql+="AUDIT_TWO='"+user+"',";
            }else if("E".equalsIgnoreCase(checkStu)){
                taskSql+="AUDIT_CPO='"+user+"',";
            }
            if (!StringUtils.isBlank(remark)) {
                taskSql += "remark=" + "'" + remark + "',";
            }
            if (!StringUtils.isBlank(step)) {
                taskSql += "step=" + "'" + step + "',";
            }
            taskSql=taskSql.substring(0,taskSql.length()-1);
            taskSql+=" where id='"+id+"'";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(taskSql).executeUpdate();
            String updateSql = " update " + tableName + " set flag=" + "'" + flag + "'" + " where task_id=" + "'" + id + "'";
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
            taskSql="select NAME from FIT_PO_TASK where id='"+id+"'";
            List<String> name= poTaskDao.listBySql(taskSql);
            if(null!=name&&name.size()>0){
                taskSql="insert into epmods.FIT_PO_TASK_LOG(task_name,create_user,create_time,remark,flag) values('"+name.get(0)+"','"+userName.get(0)+"','"+signTimet+"','"+remark+"','"+flag+"')";
                poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(taskSql).executeUpdate();
            }
    }

    /**
       取消任務審批流程
       1 當前任務數據的節點退回為0
         任務綁定的業務數據節點都退回為0
       2 cpo 待商議
     */

    public  AjaxResult cancelAudit(AjaxResult ajaxResult,String type,String id,String remark){
        if ("FIT_PO_BUDGET_CD_DTL".equalsIgnoreCase(type)||
                "FIT_ACTUAL_PO_NPRICECD_DTL".equalsIgnoreCase(type)||
                "FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(type)
                ||"FIT_PO_SBU_YEAR_CD_SUM".equals(type)||"FIT_PO_Target_CPO_CD_DTL".equals(type)) {
            if("FIT_PO_CD_MONTH_DTL".equalsIgnoreCase(type)){
                type="FIT_PO_CD_MONTH_DOWN";
            }
            List<Map> taskList = roRoleService.listMapBySql("select NAME,CREATE_USER_REAL from fit_po_task where id='" + id + "'");
            String[] task= taskList.get(0).get("NAME").toString().split("_");
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String user = loginUser.getUsername();
            List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"' and type='BI'");
            if(null==userName.get(0)){
                userName.set(0,user);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTimet = df.format(new Date());

            String sql=" update "+type+" set flag='0' where task_id='"+id+"'";
            String taskSql="update fit_po_task set flag='0' ,UPDTAE_TIME =" + "'" + signTimet + "'," + " UPDATE_USER="
                    + "'" + user +"', UPDATE_USER_REAL ='"+userName.get(0)+"'";
            if (!StringUtils.isBlank(remark)) {
                taskSql += ",remark=" + "'" + remark + "'";
            }
            taskSql+="where id='"+id+"'";
            String sqlC="select distinct email from fit_user where username=(select CREATE_USER from FIT_PO_TASK WHERE id='"+id+"') and type='BI' and email is not null";
            List<String> emailListC=roRoleService.listBySql(sqlC);
            emailListC=emailListC.stream().distinct().collect(Collectors.toList());

            if(emailListC.size()==0){
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "請聯係管理員維護對應崗位的郵箱(Task Type Fail)");
                return ajaxResult;
            }else {
                Boolean isSend = EmailUtil.emailsMany(emailListC, taskList.get(0).get("NAME").toString()+"採購BI平臺簽核通知，請勿回復","亲爱的同事:</br>&nbsp;&nbsp;任務管理員取消審批，請及時處理！</br>&nbsp;&nbsp;<a href=\""+accessUrl+"?taskId="+id+"&statusType=0&roleCode="+replaceRole("",id)+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
                if(!isSend){
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                    return ajaxResult;
                }else {
                    if ("FIT_PO_Target_CPO_CD_DTL".equalsIgnoreCase(type)) {
                        String userList = "select distinct u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and r.id=ur.role_id \n" +
                                "and u.type='BI'and EMAIL is not null and r.code in ('PLACECLASS1','PLACECLASS','T_MANAGER','TDC','ADMIN','MANAGER','specialManager') ";
                        List<String> emailList = roRoleService.listBySql(userList);
                        emailList = emailList.stream().distinct().collect(Collectors.toList());
                        String msg = "尊敬的主管:</br> &nbsp;&nbsp;" + task[0]+"年"+ "SBU年度CD目標有更新, 需重新呈核, 請及時關注目標變更！";
                        Boolean isSends = EmailUtil.emailsMany(emailList, task[0]+"年"+task[1] + "簽核通知，請勿回復", msg + "</br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
                        if (!isSends) {
                            ajaxResult.put("flag", "fail");
                            ajaxResult.put("msg", "審核驳回郵件通知發送失敗 (Failed to send the audit notification by email)");
                            return ajaxResult;
                        }
                    }
                }
            }
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(taskSql).executeUpdate();

            taskSql="select NAME from FIT_PO_TASK where id='"+id+"'";
            List<String> name= poTaskDao.listBySql(taskSql);
            if(null!=name&&name.size()>0){
                taskSql="insert into epmods.FIT_PO_TASK_LOG(task_name,create_user,create_time,remark,flag) values('"+name.get(0)+"','"+userName.get(0)+"','"+signTimet+"','"+remark+"','-3')";
                poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(taskSql).executeUpdate();
            }
            return ajaxResult;
        } else{
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "任務類型出錯(Task Type Fail)");
            return ajaxResult;
        }

    }


    /**
       取消任務
       cpo任務數據清空
       綁定數據的flag+taskId清空

     */
    public  AjaxResult cancelTask(AjaxResult ajaxResult,String id){
        try{
            List<String> list=poTaskDao.listBySql("select type from fit_po_task where id='"+id+"'");
            if (list.size()==1) {
                UserDetailImpl loginUser = SecurityUtils.getLoginUser();
                String user = loginUser.getUsername();
                List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"' and type='BI'");
                if(null==userName.get(0)){
                    userName.set(0,user);
                }
                String taskSql="select NAME from FIT_PO_TASK where id='"+id+"'";
                List<String> name= poTaskDao.listBySql(taskSql);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String signTimet = df.format(new Date());
                if(null!=name&&name.size()>0){
                    taskSql="insert into epmods.FIT_PO_TASK_LOG(task_name,create_user,create_time,remark,flag) values('"+name.get(0)+"','"+userName.get(0)+"','"+signTimet+"','用戶取消任務','-2')";
                    poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(taskSql).executeUpdate();
                }

                String deleteSql= "delete from fit_po_task where flag not in('-1','-3') and id='"+id+"'";
                poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
                if(!list.get(0).equalsIgnoreCase("FIT_PO_Target_CPO_CD_DTL")){
                    String deleteSqlSJY= "delete from "+list.get(0)+" where TASK_ID='"+id+"'";
                    poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSqlSJY).executeUpdate();
                }else{
                    String updateSql=" update FIT_PO_Target_CPO_CD_DTL set flag=null, task_id=null where task_id='"+id+"'";
                    poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
                }
            }else {
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "取消任務失敗(Mission Cancel Failed)");
            }

        }catch (Exception e){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "取消任務失敗(Mission Cancel Failed) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult;

    }

    public String replaceRole(String roleCode,String taskId){
        if(null!=taskId&&taskId.length()>2){
            roleCode="";
            String roleCodeSql=" select  r.code username from fit_user  u \n" +
                    "  left join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id \n" +
                    "  left join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id\n" +
                    "  where username=(select CREATE_USER from FIT_PO_TASK WHERE id='"+taskId+"')  and u.type='BI' and r.grade='1'";
            List<String> list=roRoleService.listBySql(roleCodeSql);
            if(null!=list&&list.size()>0){
                return list.get(0);
            }
        }else if (null!=taskId&&taskId.equalsIgnoreCase("1")) {
            switch (roleCode) {
                case "MM":
                    roleCode = "PD";
                    break;
                case "PD":
                    roleCode = "KEYUSER";
                    break;
                case "TDC":
                    roleCode = "T_MANAGER";
                    break;
                case "T_MANAGER":
                    roleCode = "PLACECLASS";
                    break;
                case "PLACECLASS":
                    roleCode = "CPO";
                    break;
                case "SOURCER":
                    roleCode = "CLASS";
                    break;
                case "specialSourcer":
                    roleCode = "specialClass";
                    break;
                case "CLASS":
                    roleCode = "MANAGER";
                    break;
                case "specialClass":
                    roleCode = "specialManager";
                    break;
                default:
                    roleCode = "";
                    break;
            }
        }
        return roleCode;
    }

    //查找任务
    public Model selectPoTask(Model model, String tableName, Locale locale, PageRequest pageRequest, String id) throws Exception {
        PoTable poTable = poTableService.get(tableName);
        List<PoColumns> columns = poTable.getColumns();
        for (PoColumns poColumns : columns) {
            poColumns.setComments(instrumentClassService.getByLocale(locale, poColumns.getComments()));
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