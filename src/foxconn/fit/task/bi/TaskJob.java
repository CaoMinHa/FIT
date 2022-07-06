package foxconn.fit.task.bi;

import foxconn.fit.dao.ebs.ParameterDao;
import foxconn.fit.entity.base.User;
import foxconn.fit.entity.bi.PoEmailLog;
import foxconn.fit.service.bi.PoEmailService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class TaskJob {
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private PoEmailService poEmailService;
    @Autowired
    private ParameterDao userDao;

/** @Scheduled(cron = "10 * * * * ?")
    @Scheduled(cron = "0 0 8 * * ?" )
    @Scheduled(cron = "0/5 * * * * MON-SAT")**/
    @Scheduled(cron = "0 0 8 * * MON-SAT")
    public void job(){
        try{
            System.out.print("任務截止日當天及前一天上午8點檢查。");
            //查找有截止任务的最新一条邮件记录
            String sql="select * from CUX_PO_EMAIL where end_date is not null order by id desc";
            List<PoEmailLog> list=poTableService.listBySql(sql,PoEmailLog.class);
            if(null!=list&&list.size()>0){
                PoEmailLog poEmailLog=list.get(0);
                Date date=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                //当前时间字符串
                String dateString = formatter.format(date);
                //时间替换成截止时间
                Date date1= formatter.parse(poEmailLog.getEndDate());
                //获取截止时间前一天的字符串
                String predate = formatter.format(new Date(date1.getTime() - (long) 24 * 60 * 60 * 1000));
                System.out.print("定时任务检验：当前时间"+dateString+",任务截止时间："+poEmailLog.getEndDate()+",任务截止时间前一天："+predate);
                //满足截止时间及截止时间前一天与当前申请相等 触发检验
                if(dateString.equals(predate)||dateString.equals(poEmailLog.getEndDate())|| date.getTime()>date1.getTime()){
                    String username="";
                    int integer=Integer.parseInt(dateString.substring(0,4))+1;
                    //查找未提交的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD') " +
                            " and tie.NEW_SBU_NAME not in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag in(1,2,3) and a.year='"+integer+"')";
                    //查找拥有角色MM的权限用户
                    String sqlUser="select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='MM' and u.sbu is not null";
                    username=this.userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        if(date.getTime()>date1.getTime()){
                            sqlUser="親愛的同事：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",系統檢測到您目前尚未完成，已經逾期。請儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            sql=poEmailLog.getEmailTitle()+" 數據上傳逾期通知！";
                        }else{
                            sqlUser="親愛的同事：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            sql=poEmailLog.getEmailTitle()+" 數據上傳提醒！";
                        }
                        System.out.print("開始發送未提交的sbu企劃主管 收件人："+username+" 主題："+sql+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,sql);
                    }
                    //查找未审核的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD') " +
                            " and tie.NEW_SBU_NAME not in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=1 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                        "r.id=ur.role_id and r.code='PD' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        if(date.getTime()>date1.getTime()){
                            sqlUser="尊敬的企劃主管：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",系統檢測到您目前尚未完成，已經逾期。請儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            sql=poEmailLog.getEmailTitle()+" 數據上傳逾期通知！";
                        }else{
                            sqlUser="尊敬的企劃主管：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            sql=poEmailLog.getEmailTitle()+" 數據上傳提醒！";
                        }
                        System.out.print("開始發送未審核的企划主管 收件人："+username+" 主題："+sql+" 數據上傳提醒！"+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,sql);
                    }
                    //查找未审核的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                                "  tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD') " +
                                " and tie.NEW_SBU_NAME not in" +
                                "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=2 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='KEYUSER' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        if(date.getTime()>date1.getTime()){
                            sqlUser="尊敬的采購管理員：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",系統檢測到您目前尚未完成，已經逾期。請儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            sql=poEmailLog.getEmailTitle()+" 數據上傳逾期通知！";
                        }else{
                            sqlUser="尊敬的采購管理員：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒";
                            sql=poEmailLog.getEmailTitle()+" 數據上傳提醒！";
                        }

                        System.out.print("開始發送未審核的采購管理員 收件人："+username+" 主題："+sql+" 數據上傳提醒！"+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,sql);
                    }
                }
            }
        }catch (Exception e){
            System.out.print("系统定时任务失败");
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 0 13 * * MON-SAT")
    public void jobOne(){
        try{
            System.out.print("任務截止日當天下午1點檢查。");
            //查找有截止任务的最新一条邮件记录
            String sql="select * from CUX_PO_EMAIL where end_date is not null order by id  desc";
            List<PoEmailLog> list=poTableService.listBySql(sql,PoEmailLog.class);
            if(null!=list&&list.size()>0){
                PoEmailLog poEmailLog=list.get(0);
                Date date=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                //当前时间字符串
                String dateString = formatter.format(date);
                System.out.print("定时任务检验：当前时间"+dateString+",任务截止时间："+poEmailLog.getEndDate());
                //满足截止时间及截止时间前一天与当前申请相等 触发检验
                if(dateString.equals(poEmailLog.getEndDate())){
                    String username="";
                    int integer=Integer.parseInt(dateString.substring(0,4))+1;
                    //查找未提交的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD')" +
                            " and tie.NEW_SBU_NAME not in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag in(1,2,3) and a.year='"+integer+"')";
                    String sqlUser="select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='MM' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        sqlUser="親愛的同事：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                +"時間發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                +",請合理安排時間，儘快完成數據上傳且提交並告知主管完成審核。</br>如您已經完成，請忽略該提醒";
                        System.out.print("開始發送未提交的sbu企劃主管 收件人："+username+" 主題："+poEmailLog.getEmailTitle()+" 數據上傳提醒！"+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,poEmailLog.getEmailTitle()+" 數據上傳提醒！");
                    }

                    //查找未提交的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD') " +
                            " and tie.NEW_SBU_NAME not in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=1 and a.year='"+integer+"')";
                    sqlUser="select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='PD' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        sqlUser="尊敬的企劃主管：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                +"時間發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                +",請合理安排時間，儘快完成數據上傳並告知主管完成審核。</br>如您已經完成，請忽略該提醒";
                        System.out.print("開始發送未提交的sbu企劃主管 收件人："+username+" 主題："+poEmailLog.getEmailTitle()+" 數據上傳提醒！"+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,poEmailLog.getEmailTitle()+" 數據上傳提醒！");
                    }

                    //查找未审核的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                                "  tie.NEW_SBU_NAME IN ('PSS','ASD','FIAD','ACD','FAS','CRD','EMS','TMTS','IoT','ABS','Tengyang','FAB','TSC','IDS','APS','CW','AEC','CIDA','ACE','OLU','Other','FAD') " +
                                " and tie.NEW_SBU_NAME not in" +
                                "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=2 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='KEYUSER' and u.sbu is not null";
                    username=userEmail(sql,sqlUser);
                    if(null!=username&&username!=""&&username.length()>0){
                        sqlUser="尊敬的采購管理員：</br>&nbsp;&nbsp;由"+poEmailLog.getCreateBy()+"在"+formatter.format(poEmailLog.getCreateDate())
                                +"時間發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                +",請合理安排時間，儘快完成數據上傳並告知主管完成審核。</br>如您已經完成，請忽略該提醒";
                        System.out.print("開始發送未審核的采購管理員 收件人："+username+" 主題："+poEmailLog.getEmailTitle()+" 數據上傳提醒！"+"  内容："+sqlUser);
                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,poEmailLog.getEmailTitle()+" 數據上傳提醒！");
                    }
                }
            }
        }catch (Exception e){
            System.out.print("系统定时任务失败");
            e.printStackTrace();
        }
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
                        username+="'"+listUser.get(j).getUsername()+"',";
                        listUser.remove(j);
                    }
                }
            }
        }
        return username;
    }

    /**
     SELECT send_email_flag  FROM epmexp.cux_cf_default_bi   /shared/FIT-BI Platform v2/01.分析/CF/D.SBU現金流量表
     SELECT send_email_flag  FROM epmexp.cux_pl_default_bi   /shared/FIT-BI Platform v2/01.分析/PL/D.SBU损益表
     SELECT send_email_flag  FROM epmexp.cux_bs_default_bi   /shared/FIT-BI Platform v2/01.分析/BS/D.SBU資產負債表
     发邮件标志,  这个三个表
     */

//    @Scheduled(cron = "30 * * * * ?")
//    @Scheduled(cron = "0 0 7,10,12,15 * * MON-SAT")
//    @Scheduled(cron = "50 * * * * ?")
    @Scheduled(cron = "0 0 1 * * MON-SAT")
    public void jobBIEmail(){
        try{
            System.out.print("BI平台三表检验是否有同步数据 如果有给有权限的人发送邮件");
            String sql ="SELECT YEARS,period  FROM epmexp.cux_pl_default_bi and years not in ('2021') where send_email_flag='Y' group by YEARS,period order by period";
            Boolean b=this.biSendEamil(sql,"SY_SBU");
            // Boolean l=this.biSendEamil(sql,"menu_plUnit");
            //if(b&&l){
            if(b){
                poTableService.updateSql("update epmexp.cux_pl_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/D.SBU损益表' where instr(';'||BI_GROUP||';',';SY_SBU;') > 0  ");
            }
            sql="SELECT YEARS,period  FROM epmexp.cux_bs_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by period";
            b=this.biSendEamil(sql,"ZC_SBU");
            if(b){
                poTableService.updateSql("update epmexp.cux_bs_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.SBU資產負債表' where instr(';'||BI_GROUP||';',';ZC_SBU;') > 0  ");
            }
            sql="SELECT YEARS,period  FROM epmexp.cux_cf_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by period";
            b=this.biSendEamil(sql,"CF_SBU");
            if(b){
                poTableService.updateSql("update epmexp.cux_cf_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.SBU現金流量表' where instr(';'||BI_GROUP||';',';CF_SBU;') > 0  ");
            }
        }catch (Exception e){
            System.out.print("系統定時任務失敗");
            e.printStackTrace();
        }
    }

    public Boolean biSendEamil(String sql,String type) throws Exception {
        //CF_SBU;ZC_SBU;SY_SBU
        Boolean isSend=false;
        List<Map> list=poTableService.listMapBySql(sql);
        List<String> listMonth=new ArrayList<>();
        if(null !=list && list.size()>0){
            String date=String.valueOf(list.get(0).get("YEARS"));
            date+="年";
            for (int i=0;i<list.size();i++) {
                String month=String.valueOf(list.get(i).get("PERIOD"));
                listMonth.add(list.get(0).get("YEARS").toString()+month);
                if(month.substring(0,1).equalsIgnoreCase("0")){
                    month=month.substring(1,2);
                }
                date+=month;
                if(i!=list.size()-1){
                    date+=",";
                }
            }
            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';"+type+";') > 0 ";
            //and u.BI_USER not in('FIT0141','F3205615','TW8004','FIT0447','HAH0016109','TW20385')";
//            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';"+type+";') > 0  and u.BI_USER in('Total','TotalGM','TW20385')";
            List<Map> emailListC=poTableService.listMapBySql(sqlEmail);
            String typeVal="";
            if(null!=emailListC&&emailListC.size()>0){
                switch (type){
                    case "menu_plUnit":
                        typeVal="損益表";
                        break;
                    case "CF_SBU":
                        typeVal="現金流量表";
                        break;
                    case "ZC_SBU":
                        typeVal="資產負債表";
                        break;
                    case "SY_SBU":
                        typeVal="损益表";
                        break;
                }
                date+="月份";
                String erro="未发送成功的邮箱：";
                String title="";
                for (Map map:emailListC) {
                    String str="'"+String.valueOf(map.get("BI_GROUP")).replace(";","','")+"'";
                    sqlEmail="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('"+type.substring(0,2)+"','TS')";
                    if(type.equals("menu_plUnit")){
                        sqlEmail="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('SY','SY_Unit')";
                    }
                    List<String> rtUserEmailAuthorities=poTableService.listBySql(sqlEmail);
                    String content="Dear "+map.get("BI_USERNAME").toString()+"主管：<br></br>&nbsp;&nbsp;&nbsp;";
                    String  message="";
                    if(type=="SY_SBU"){
                        if(listMonth.size()>1){
                            for (int i=0;i<listMonth.size();i++){
                                message+="<br></br>"+listMonth.get(i)+"數據："+this.message(listMonth.get(i),map.get("BI_USER").toString().trim());
                            }
                        }else{
                            message+=this.message(listMonth.get(0),map.get("BI_USER").toString().trim());
                        }
                    }
                    if(null==rtUserEmailAuthorities||rtUserEmailAuthorities.size()==0){
                        title=date+"FIT"+typeVal;
                        content+=date+"FIT"+typeVal+"已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"http://10.98.5.24:9502/analytics\" style=\"color: blue;\">FIT"+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }else{
                        title=date+rtUserEmailAuthorities.toString()+typeVal;
                        content+=date+rtUserEmailAuthorities.toString()+typeVal+"已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"http://10.98.5.24:9502/analytics\" style=\"color: blue;\">"+rtUserEmailAuthorities.toString()+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }
                    System.out.print("发送邮件："+map.get("EMAIL").toString()+"****主题："+title+"****内容："+content);
                    isSend= EmailUtil.emailsMany(map.get("EMAIL").toString(),title,content);
                    if(!isSend){
                        erro+=map.get("BI_USER").toString()+",";
                        isSend=true;
                    }
                }
                System.out.print(erro);
            }
        }
        return isSend;
    }

    public String message(String yearMonth,String userCode) throws Exception {
        String message="";
        Connection c = SessionFactoryUtils.getDataSource(userDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call cux_bi_pl_pkg.get_pl_data(?,?,?,?,?)}");
        cs.setString(1, yearMonth);
        cs.setString(2, userCode);
        cs.registerOutParameter(3, java.sql.Types.VARCHAR);
        cs.registerOutParameter(4, java.sql.Types.VARCHAR);
        cs.registerOutParameter(5, java.sql.Types.VARCHAR);
        cs.execute();
        if(null!=cs.getString(3)&&cs.getString(3).length()>0){
            message="<br></br><span style=\"color: #5bb75b;\">"+cs.getString(3)+"</span>";
            if(!yearMonth.substring(4,6).equals("01")){
                if(null!=cs.getString(4)&&cs.getString(4).length()>0){
                    message+="<br></br><span style=\"color: blue;\">"+cs.getString(4)+"</span>";
                }
            }
            if(Integer.parseInt(yearMonth.substring(0,4)) > 2022){
                if(null!=cs.getString(5)&&cs.getString(5).length()>0){
                    message+="<br></br><span style=\"color: blue;\">"+cs.getString(5)+"</span>";
                 }
            }
        }
        cs.close();
        c.close();
        return message;
    }


    /**
     * 检查有没有产品信息未在维度表维护
     */
//    @Scheduled(cron = "30 * * * * ?")
    @Scheduled(cron = "0 0 1 * * MON-SAT")
    public void checkProductDimension(){
        try{
            String sql="SELECT distinct PRODUCT_SERIES_DESC FROM epmods.cux_inv_sbu_item_info_mv where PRODUCT_SERIES_DESC not in (select DIMENSION from FIT_DIMENSION where type='Product')";
            List<String> product=poTableService.listBySql(sql);
            if(null!=product&&product.size()>0){
                String content="Dear 系統管理員：</br>&nbsp;&nbsp;以下維度值還未維護，請儘快行動。<br></br>&nbsp;&nbsp;"+product.toString().substring(1,product.toString().length()-1)+"<br></br>Best Regards!";
                EmailUtil.emailsMany("maggao@deloitte.com.cn","Produact Series維護",content);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
