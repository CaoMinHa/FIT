package foxconn.fit.task.bi;

import foxconn.fit.entity.base.User;
import foxconn.fit.entity.bi.PoEmailLog;
import foxconn.fit.service.bi.PoEmailService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = Exception.class)
public class TaskJob {
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private PoEmailService poEmailService;

    @Value("${accessUrl}")
    String accessUrl;

    /**
     * 根據SBU VOC收集的數據給相關未完成任務的用戶發送跟催郵件
     * 任務截止日當天及前一天上午8點檢查
     * @Scheduled(cron = "0 0 8 * * MON-SAT")
     */
    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void job(){
        try{
            System.out.print("任務截止日當天及前一天上午8點檢查。");
            //查找有截止任务的最新一条邮件记录
            String sql="select * from CUX_PO_EMAIL where CREATION_DATE=(select max(CREATION_DATE) from CUX_PO_EMAIL where end_date is not null and EMAIL_YEAR is not null)";
            List<PoEmailLog> list=poTableService.listBySql(sql,PoEmailLog.class);
            if(null!=list&&list.size()>0){
                PoEmailLog poEmailLog=list.get(0);
                Date date=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                //当前时间字符串
                String dateString = formatter.format(date);
                //时间替换成截止时间
                Date date1= formatter.parse(poEmailLog.getEndDate());
                //获取截止时间前一天及前两天的字符串
                String predate = formatter.format(new Date(date1.getTime() - (long) 24 * 60 * 60 * 1000));
                String predateTwo = formatter.format(new Date(date1.getTime() - (long) 48 * 60 * 60 * 1000));
                date1=new Date(date1.getTime() + (long) 24 * 60 * 60 * 1000);
                //满足截止时间及截止时间前一天与当前申请相等 触发检验
                if(dateString.equals(predate)||dateString.equals(poEmailLog.getEndDate())||dateString.equals(predateTwo)||date.getTime()>date1.getTime()){
                    String username="";
                    int integer=Integer.parseInt(poEmailLog.getEmailYear());
//                    LocalDate localDate=LocalDate.now();
//                    int integer=Integer.parseInt(localDate.minusDays(60).toString().substring(0,4))+1;
//                    int integer=Integer.parseInt(dateString.substring(0,4))+1;
                    //查找未提交的SBU
                    sql="select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where flag='1' " +
                            " and SBU_NAME not in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag in(1,2,3) and a.year='"+integer+"')";
                    //查找拥有角色MM的权限用户
                    String sqlUser="select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code in('MM','TDC') and u.type='BI' and u.sbu is not null";
                    //查找拥有角色SBUCompetent和企划主管的权限用户
                    String sqlSBUCompetent="select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code in('SBUCompetent','PD') and u.type='BI' and u.sbu is not null ";
                    username=this.userEmail(sql,sqlUser);
                    String SBUCompetentUserName="";
                    SBUCompetentUserName=this.userEmail(sql,sqlSBUCompetent);
                    String title=poEmailLog.getEmailTitle()+" 年度目標CD上傳逾期通知！";
                    if(null!=username&&username!=""&&username.length()>0){
                        if(date.getTime()>date1.getTime()){
                            sqlUser="親愛的同事：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",系統檢測到您目前尚未完成，已經逾期。請儘快與各Commodity溝通並上傳溝通後數據，同時告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            poEmailService.sendEmailTimingCC(username.substring(0,username.length()-1),SBUCompetentUserName.substring(0,SBUCompetentUserName.length()-1),sqlUser,title);
                        }else{
                            sqlUser="親愛的同事：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒；<br></br>附檔是年度目標CD審批流程，根據需要可查看了解。";
                            title=poEmailLog.getEmailTitle()+" 年度目標CD數據上傳提醒！";
                            System.out.print("開始發送未提交的sbu企劃主管 收件人："+username+" 主題："+title+"  内容："+sqlUser);
                            poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,title);
                        }
                    }
                    //查找未审核的SBU
                    sql="select distinct SBU_NAME from BIDEV.DM_D_ENTITY_SBU where flag='1' and SBU_NAME in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=1 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code in('PD','TDC') and u.type='BI' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        if(date.getTime()>date1.getTime()){
                            sqlUser="尊敬的企劃主管：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",系統檢測到您目前尚未完成，已經逾期。請儘快完成審批。</br>如已經完成，請忽略該提醒；<br></br>附檔是年度目標CD審批流程，根據需要可查看了解。";
                        }else{
                            sqlUser="尊敬的企劃主管：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，請儘快完成審批。</br>如已經完成，請忽略該提醒；<br></br>附檔是年度目標CD審批流程，根據需要可查看了解。";
                            title=poEmailLog.getEmailTitle()+" 年度目標CD數據上傳提醒！";
                        }
                        System.out.print("開始發送未審核的企划主管 收件人："+username+" 主題："+title+""+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,title);
                    }
                    /**查找未审核的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('IDS','EMS','ABS','ACE','ASD','AEC','TSC','APS','CW','FAD','IoT','CIDA','Tengyang','TMTS') " +
                            " and tie.NEW_SBU_NAME in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=2 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code in('KEYUSER','TDC') and u.type='BI' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        if(date.getTime()>date1.getTime()){
                            sqlUser="尊敬的采購管理員：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",系統檢測到您目前尚未完成，已經逾期。請儘快完成審批。</br>如已經完成，請忽略該提醒；<br></br>附檔是年度目標CD審批流程，根據需要可查看了解。";
                        }else{
                            sqlUser="尊敬的采購管理員：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，請儘快完成審批。</br>如已經完成，請忽略該提醒；<br></br>附檔是年度目標CD審批流程，根據需要可查看了解。";
                            title=poEmailLog.getEmailTitle()+" 年度目標CD數據上傳提醒！";
                        }
                        System.out.print("開始發送未審核的采購管理員 收件人："+username+" 主題："+title+" 數據上傳提醒！"+"  内容："+sqlUser);
//                        poEmailService.sendEmailTiming("'Emma'",sqlUser,title);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,title);
                    }
                     ***/
                }
            }
        }catch (Exception e){
            System.out.print("系统定时任务失败");
            e.printStackTrace();
        }
    }

    /**
     * 检查有没有产品信息未在维度表维护
     * @Scheduled(cron = "0 0 1 * * MON-SAT")
     */
    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void checkProductDimension(){
        try{
            String sql="SELECT distinct PRODUCT_SERIES_code FROM epmods.cux_inv_sbu_item_info_mv where PRODUCT_SERIES_DESC not in (select DIMENSION from FIT_DIMENSION where type='Product') and PRODUCT_SERIES_CODE not in('EWAA','3CAB','EVAE','5CD')";
            List<String> product=poTableService.listBySql(sql);
            if(null!=product&&product.size()>0){
                String content="Dear 系統管理員：</br>&nbsp;&nbsp;以下維度值還未維護，請儘快行動。<br></br>&nbsp;&nbsp;"+product.toString().substring(1,product.toString().length()-1)+"<br></br>Best Regards!";
                EmailUtil.emailsMany("it-ks-mfg@fit-foxconn.com","預算系統 Product Series 維護",content);
                EmailUtil.emailsMany("ambcai@deloitte.com.cn","預算系統 Product Series 維護",content);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 實際非價格CD報表、採購CD手動匯總表
     * 1-8號每天9點發送郵件提醒
     * 8-10號未上傳需要同時增加核准人郵箱
     * 實際採購非價格CD匯總表:FIT_ACTUAL_PO_NPRICECD_DTL
     * 採購CD手動匯總表:FIT_PO_BUDGET_CD_DTL
     * @Scheduled(cron = "30 18 * * * *")
     */
    @Scheduled(cron = "0 0 9 * * MON-SAT")
    public void poSendTaskEamil(){
        poEamil("FIT_ACTUAL_PO_NPRICECD_DTL");
        poEamil("FIT_PO_BUDGET_CD_DTL");
    }

    /**每天推送 +預測S營收實際ummary 發佈數據**/
//    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void bocklog(){
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        //当前时间字符串
        String dateString = formatter.format(date);
        String title="FIT 營收實際+預測Summary(截止日:"+dateString+")";
        String content="";
        //獲取bocklog表單有權限的用戶
//        String userList="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||BI_GROUP||';',';RT_Backlog;')> 0";
        String userList="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||BI_GROUP||';',';RT_Backlog;')> 0 and BI_USER in('admin')";
        List<Map> userMap=poTableService.listMapBySql(userList);
        if(null!=userMap&&userMap.size()>0){
            for (Map map:userMap) {
                content="Dear "+map.get("BI_USERNAME").toString()+"主管：<br></br>&nbsp;&nbsp;&nbsp;"+df.format(date);
                String years="";
                String quarter="";
                String str="'"+String.valueOf(map.get("BI_GROUP")).replace(";","','")+"'";
                String userSBU="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('BS','TS')";
                List<String> userAuthorities=poTableService.listBySql(userSBU);
                if(null!=userAuthorities&&userAuthorities.size()>0){
                    content+=userAuthorities.toString();
                    str="";
                    for (String s:userAuthorities) {
                        str+="'"+s+"',";
                    }
                    String userNumber="SELECT t.YEAR_MONTH,t.C_TYPE," +
                            "case when sum(t.REVENUE_ALL)=0 then '---' else to_char(sum(t.REVENUE_ALL),'999,999,999,999') end REVENUE_ALL,\n" +
                            "case when sum(t.REVENUE_HIS)=0 then '---' else to_char(sum(t.REVENUE_HIS),'999,999,999,999') end REVENUE_HIS ,\n" +
                            "case when sum(t.REVENUE_MISC)=0 then '---' else to_char(sum(t.REVENUE_MISC),'999,999,999,999') end REVENUE_MISC ,\n" +
                            "case when sum(t.REVENUE_MOVE)=0 then '---' else to_char(sum(t.REVENUE_MOVE),'999,999,999,999') end REVENUE_MOVE ,\n" +
                            "case when sum(t.TRADE_ORDER)=0 then '---' else to_char(sum(t.TRADE_ORDER),'999,999,999,999') end TRADE_ORDER ,\n" +
                            "case when sum(t.DMT)=0 then '---' else to_char(sum(t.DMT),'999,999,999,999') end DMT " +
                            "FROM epmebs.cux_actual_rev_blog_v t WHERE SBU in("+str.substring(0,str.length()-1)+")  group by t.YEAR_MONTH,t.c_type";
                    List<Map> numberList=poTableService.listMapBySql(userNumber);
                    if(null!=numberList&&numberList.size()>1){
                        for (Map m:numberList) {
                            if(m.get("C_TYPE").toString().equals("month")){
                                quarter="<span style=\"color: blue;font-size:15px;\">幣別：MUSD</span><br></br>" +
                                        "<span style=\"color: blue;font-size:12px;\">本月（截止至"+dateString+"）</span><br></br>" +
                                        "<span style=\"color: #5bb75b;\">Net Revenue All</span>:"+m.get("REVENUE_ALL").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">歷史實際營收</span>:"+m.get("REVENUE_HIS").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">DMT</span>:"+m.get("DMT").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Trade Order</span>:"+m.get("TRADE_ORDER").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Move order)</span>:"+m.get("REVENUE_MOVE").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Misc income)</span>:"+m.get("REVENUE_MISC").toString()+"；<br></br>"
                                ;
                            }else if(m.get("C_TYPE").toString().equals("quarter")){
                                years="<span style=\"color: blue;font-size:12px;\">"+m.get("YEAR_MONTH").toString()+"</span><br></br>" +
                                        "<span style=\"color: #5bb75b;\">Net Revenue All</span>:"+m.get("REVENUE_ALL").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">歷史實際營收</span>:"+m.get("REVENUE_HIS").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">DMT</span>:"+m.get("DMT").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Trade Order</span>:"+m.get("TRADE_ORDER").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Move order)</span>:"+m.get("REVENUE_MOVE").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Misc income)</span>:"+m.get("REVENUE_MISC").toString()+"；<br></br>"
                                ;
                            }
                        }
                    }
                }else{
                    content+="FIT";
                    String userNumber="SELECT t.YEAR_MONTH,t.C_TYPE," +
                            "case when sum(t.REVENUE_ALL)=0 then '---' else to_char(sum(t.REVENUE_ALL),'999,999,999,999') end REVENUE_ALL,\n" +
                            "case when sum(t.REVENUE_HIS)=0 then '---' else to_char(sum(t.REVENUE_HIS),'999,999,999,999') end REVENUE_HIS ,\n" +
                            "case when sum(t.REVENUE_MISC)=0 then '---' else to_char(sum(t.REVENUE_MISC),'999,999,999,999') end REVENUE_MISC ,\n" +
                            "case when sum(t.REVENUE_MOVE)=0 then '---' else to_char(sum(t.REVENUE_MOVE),'999,999,999,999') end REVENUE_MOVE ,\n" +
                            "case when sum(t.TRADE_ORDER)=0 then '---' else to_char(sum(t.TRADE_ORDER),'999,999,999,999') end TRADE_ORDER ,\n" +
                            "case when sum(t.DMT)=0 then '---' else to_char(sum(t.DMT),'999,999,999,999') end DMT " +
                            "FROM epmebs.cux_actual_rev_blog_v t WHERE SBU in('FIT')  group by t.YEAR_MONTH,t.c_type";
                    List<Map> numberList=poTableService.listMapBySql(userNumber);
                    if(null!=numberList&&numberList.size()>1){
                        for (Map m:numberList) {
                            if(m.get("C_TYPE").toString().equals("month")){
                                quarter="<span style=\"color: blue;font-size:15px;\">幣別：MUSD</span><br></br>" +
                                        "<span style=\"color: blue;font-size:12px;\">本月（截止至"+dateString+"）</span><br></br>" +
                                        "<span style=\"color: #5bb75b;\">Net Revenue All</span>:"+m.get("REVENUE_ALL").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">歷史實際營收</span>:"+m.get("REVENUE_HIS").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">DMT</span>:"+m.get("DMT").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Trade Order</span>:"+m.get("TRADE_ORDER").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Move order)</span>:"+m.get("REVENUE_MOVE").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Misc income)</span>:"+m.get("REVENUE_MISC").toString()+"；<br></br>"
                                ;
                            }else if(m.get("C_TYPE").toString().equals("quarter")){
                                years="<span style=\"color: blue;font-size:15px;\">"+m.get("YEAR_MONTH").toString()+"</span><br></br>" +
                                        "<span style=\"color: #5bb75b;\">Net Revenue All</span>:"+m.get("REVENUE_ALL").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">歷史實際營收</span>:"+m.get("REVENUE_HIS").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">DMT</span>:"+m.get("DMT").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Trade Order</span>:"+m.get("TRADE_ORDER").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Move order)</span>:"+m.get("REVENUE_MOVE").toString()+"；<br></br>"+
                                        "<span style=\"color: #5bb75b;\">Revenue-(Misc income)</span>:"+m.get("REVENUE_MISC").toString()+"；<br></br>"
                                ;
                            }
                        }
                    }
                }
                content+="營收實際+預測Summary已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>"+quarter+years+"&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">FIT_Revenue_and_Backlog_Summary</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問，郵箱：brian.pr.chen@fit-foxconn.com<br></br><br>Best Regards!";
                EmailUtil.emailsMany(map.get("EMAIL").toString(),title,content);
            }
        }
        poTableService.updateSql("update BIDEV.Bi_user_list set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/RT/D.FIT Revenue and Backlog Sum' where instr(';'||BI_GROUP||';',';RT_Backlog;')> 0 ");
    }

    /**每天固定1点更改还原采购逾期处理的特殊分配*/
    @Scheduled(cron = "0 0 1 * * MON-SAT")
    public void poUpload(){
        poTableService.updateSql("update FIT_USER_PO_UPLOAD set STATE='N' where STATE='Y'");
    }

    public String userEmail(String sql,String sqlUser){
        String username="";
        List <String> countNotUploadlist= poTableService.listBySql(sql);
        if(null!=countNotUploadlist&&countNotUploadlist.size()>0) {
            //查找拥有角色 的权限用户
            List<User> listUser = poTableService.listBySql(sqlUser, User.class);
            for (int i = 0; i < countNotUploadlist.size(); i++) {
                for (int j = 0; j < listUser.size(); j++) {
                    //当前循环sbu
                    sql = "," + countNotUploadlist.get(i) + ",";
                    //用户权限
                    sqlUser = "," + listUser.get(j).getSBU() + ",";
                    //如果此字符串中没有这样的字符，则返回 -1
                    if (sqlUser.indexOf(sql) != -1) {
                        if (username.indexOf("'"+listUser.get(j).getUsername()+"'") == -1) {
                            username+="'"+listUser.get(j).getUsername()+"',";
                        }
                    }
                }
            }
        }
        if(username.isEmpty()){
            username=",";
        }
        return username;
    }
    public void poEamil(String type){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String[] date =formatter.format(new Date()).split("-");
        String dateStr=date[0]+date[1];
        if (date[1].equals("01")||date[1].equals("1")){
            dateStr= (Integer.valueOf(date[0]) - 1) +"12";
        }
//        date[2]= "9";
        String content="";
        String title="";
        String sql="";
        if("FIT_ACTUAL_PO_NPRICECD_DTL".equals(type)){
            content+="《實際採購非價格CD匯總表》";
            title+="《實際採購非價格CD匯總表》數據上傳提醒";
        }else{
            content+="《採購CD手動匯總表》";
            title+="《採購CD手動匯總表》數據上傳提醒";
        }
        if(Integer.valueOf(date[2])<9){
            content="尊敬的用戶:<br></br>&nbsp;&nbsp;請于規定時間内（1號至8號）上傳"+content+"上月數據。<br></br>（提醒：若截止8號您還未上傳表單，超时提醒郵件將抄送至您的審核人，還請及時上傳。）";
        }else{
            content="尊敬的用戶:<br></br>&nbsp;&nbsp;<font style=\"color: red;\">【您已超时！10號將關閉"+content+"上月數據的上傳】</font>請立即采取行動！";
        }
        content+="<br></br>如已經完成，請忽略該提醒<br></br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系顧問，郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!";
        if(Integer.valueOf(date[2])<=8){
            sql="select distinct u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur\n" +
                    "where u.id=ur.user_id and r.id=ur.role_id and r.code='SOURCER' and r.type='BI' and u.COMMODITY_MAJOR is not null\n" +
                    "and u.username not in \n" +
                    "(select t.create_user from fit_po_task t where t.type ='"+type+"' and substr(t.name,0,6)='"+dateStr+"'\n" +
                    "and  instr(','||u.COMMODITY_MAJOR||',',','||commodity_major||',') > 0 ) and u.email is not null ";
            List<String> emailListC=poTableService.listBySql(sql);
            if(null!=emailListC&&emailListC.size()>0) {
                emailListC=emailListC.stream().distinct().collect(Collectors.toList());
                EmailUtil.emailsMany(emailListC,title,content);
            }
        }else if(Integer.valueOf(date[2])>8&&Integer.valueOf(date[2])<11){
            sql="select distinct u.username,u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur\n" +
                    "where u.id=ur.user_id and r.id=ur.role_id and r.code='SOURCER' and type='BI' and u.COMMODITY_MAJOR is not null\n" +
                    "and u.username not in \n" +
                    "(select t.create_user from fit_po_task t where t.type ='"+type+"' and substr(t.name,0,6)='"+dateStr+"'\n" +
                    "and  instr(','||u.COMMODITY_MAJOR||',',','||commodity_major||',') > 0 ) and u.email is not null ";
            List<Map> userList=poTableService.listMapBySql(sql);
            String error="發送失敗郵箱：";
            List<String> emailList=new ArrayList<>();
            if(null!=userList&&userList.size()>0){
                for (Map user:userList) {
                    sql="select distinct COMMODITY_MAJOR from BIDEV.v_dm_d_commodity_major where COMMODITY_MAJOR not in(\n" +
                            "select t.commodity_major from fit_po_task t\n" +
                            "where t.type ='"+type+"' and substr(t.name,0,6)='"+dateStr+"'\n" +
                            ") and instr(','||(select u.commodity_major from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur \n" +
                            "where u.id=ur.user_id and r.id=ur.role_id and r.code='SOURCER' and type='BI' and u.username='"+user.get("USERNAME").toString()+"' and COMMODITY_MAJOR is not null)||',',','||COMMODITY_MAJOR||',')> 0";
                    List<String> commodity=poTableService.listBySql(sql);
                    if(null!=commodity.get(0)&&commodity.get(0).length()>0){
                        String[] commodityStr=commodity.get(0).split(",");
                        for (String c:commodityStr) {
                            sql="select distinct u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and r.id=ur.role_id and r.code='CLASS' and type='BI' and u.COMMODITY_MAJOR is not null\n" +
                                    "and instr(','||u.commodity_major||',',','||'"+c+"'||',')> 0";
                            emailList.addAll(poTableService.listBySql(sql));
                        }
                    }
                    sql="";
                    for (String e:emailList) {
                        sql=sql+e+",";
                    }
                    Boolean b=EmailUtil.emailCC(user.get("EMAIL").toString(),sql.substring(0,sql.length()-1),title,content);
                    if(!b){
                        error+=user.get("EMAIL").toString()+",";
                    }
                }
            }
            System.out.print(error);
        }
    }

}
