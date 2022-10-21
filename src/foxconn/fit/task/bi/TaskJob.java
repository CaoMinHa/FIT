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
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(rollbackFor = Exception.class)
public class TaskJob {
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private PoEmailService poEmailService;
    @Autowired
    private ParameterDao userDao;

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
            String sql="select * from CUX_PO_EMAIL where CREATION_DATE=(select max(CREATION_DATE) from CUX_PO_EMAIL where end_date is not null)";
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
                    int integer=Integer.parseInt(dateString.substring(0,4))+1;
                    //查找未提交的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('IDS','EMS','ABS','ACE','ASD','AEC','TSC','APS','CW','FAD','IoT','CIDA','Tengyang','TMTS') " +
                            " and tie.NEW_SBU_NAME not in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag in(1,2,3) and a.year='"+integer+"')";
                    //查找拥有角色MM的权限用户
                    String sqlUser="select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='MM' and u.type='BI' and u.sbu is not null";
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
//                            poEmailService.sendEmailTimingCC("'POTest','Amber'","'Emma','Nono'",sqlUser,title);
                            poEmailService.sendEmailTimingCC(username.substring(0,username.length()-1),SBUCompetentUserName.substring(0,SBUCompetentUserName.length()-1),sqlUser,title);
                        }else{
                            sqlUser="親愛的同事：</br>&nbsp;&nbsp;&nbsp;&nbsp;由"+poEmailLog.getCreateName()+"在"+formatter.format(poEmailLog.getCreateDate())
                                    +"發送的\""+poEmailLog.getEmailTitle()+"\"通知，截止完成時間為："+poEmailLog.getEndDate()
                                    +",請合理安排時間，儘快完成數據上傳並告知您的主管完成審核。</br>如已經完成，請忽略該提醒；<br></br>附檔是年度目標CD審批流程，根據需要可查看了解。";
                            title=poEmailLog.getEmailTitle()+" 年度目標CD數據上傳提醒！";
                            System.out.print("開始發送未提交的sbu企劃主管 收件人："+username+" 主題："+title+"  内容："+sqlUser);
                            poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,title);
//                            poEmailService.sendEmailTiming("'POTest','Emma'",sqlUser,title);
                        }
                    }
                    //查找未审核的SBU
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('IDS','EMS','ABS','ACE','ASD','AEC','TSC','APS','CW','FAD','IoT','CIDA','Tengyang','TMTS') " +
                            " and tie.NEW_SBU_NAME  in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=1 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='PD' and u.type='BI' and u.sbu is not null";
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
//                        poEmailService.sendEmailTiming("'Emma'",sqlUser,title);
                    }
                    /**查找未审核的SBU*/
                    sql="select distinct tie.NEW_SBU_NAME from bidev.v_if_sbu_mapping tie where" +
                            "  tie.NEW_SBU_NAME IN ('IDS','EMS','ABS','ACE','ASD','AEC','TSC','APS','CW','FAD','IoT','CIDA','Tengyang','TMTS') " +
                            " and tie.NEW_SBU_NAME in" +
                            "(select distinct a.sbu from FIT_PO_SBU_YEAR_CD_SUM a where flag=2 and a.year='"+integer+"')";
                    sqlUser = "select  u.*  from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur where u.id=ur.user_id and " +
                            "r.id=ur.role_id and r.code='KEYUSER' and u.type='BI' and u.sbu is not null";
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
//                        poEmailService.sendEmailTiming(username.substring(0,username.length()-1),sqlUser,title);
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

    /**
     *数据更新邮件通知
     SELECT send_email_flag  FROM epmexp.cux_cf_default_bi   /shared/FIT-BI Platform v2/01.分析/CF/D.SBU現金流量表
     SELECT send_email_flag  FROM epmexp.cux_pl_default_bi   /shared/FIT-BI Platform v2/01.分析/PL/D.SBU损益表
     SELECT send_email_flag  FROM epmexp.cux_bs_default_bi   /shared/FIT-BI Platform v2/01.分析/BS/D.SBU資產負債表
     @Scheduled(cron = "0 0 1 * * MON-SAT")
     */
//    @Scheduled(cron = "30 6 11 * * MON-SAT")
    public void jobBIEmail(){
        try{
            System.out.print("BI平台三表检验是否有同步数据 如果有给有权限的人发送邮件");
            String sql ="SELECT YEARS,period  FROM epmexp.cux_pl_default_bi where send_email_flag='Y' and years not in ('2021')  group by YEARS,period order by period";
            Boolean b=this.biSendEamil(sql,"SY_SBU");
            Boolean l=this.biSendEamil(sql,"menu_plUnit");
            if(l){
                poTableService.updateSql("update epmexp.cux_pl_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/D.損益單位損益表' where instr(';'||BI_GROUP||';',';menu_plUnit;') > 0  ");
            }
            if(b){
                poTableService.updateSql("update epmexp.cux_pl_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/D.SBU损益表' where instr(';'||BI_GROUP||';',';SY_SBU;') > 0  ");
            }
            //以後正式環境資產負債表只發 ZC_BU 的！
//            sql="SELECT YEARS,period  FROM epmexp.cux_bs_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by period";
//            b=this.biSendEamil(sql,"ZC_SBU");
//            if(b){
//                poTableService.updateSql("update epmexp.cux_bs_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
//                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.SBU資產負債表' where instr(';'||BI_GROUP||';',';ZC_SBU;') > 0  ");
//            }
//            sql="SELECT YEARS,period  FROM epmexp.cux_cf_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by period";
//            b=this.biSendEamil(sql,"CF_SBU");
//            if(b){
//                poTableService.updateSql("update epmexp.cux_cf_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
//                poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.SBU現金流量表' where instr(';'||BI_GROUP||';',';CF_SBU;') > 0  ");
//            }
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

//            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';"+type+";') > 0  and u.BI_USER in('Maggie','Amber')";
            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';"+type+";') > 0";
            if("menu_plUnit".equals(type)){
//                sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';menu_plUnit;') > 0 and instr(';'||u.BI_GROUP||';',';SY_SBU;') <= 0 and u.BI_USER in('Maggie','Amber')";
                sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';menu_plUnit;') > 0 and instr(';'||u.BI_GROUP||';',';SY_SBU;') <= 0 ";
            }
            List<Map> emailListC=poTableService.listMapBySql(sqlEmail);
            String typeVal="";
            if(null!=emailListC&&emailListC.size()>0){
                switch (type){
                    case "menu_plUnit":
                        typeVal="損益單位損益表";
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
                    sqlEmail="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('BS','TS')";
                    if(type.equals("menu_plUnit")){
                        sqlEmail="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('SY','SY_Unit')";
                    }
                    List<String> rtUserEmailAuthorities=poTableService.listBySql(sqlEmail);
                    String content="Dear "+map.get("BI_USERNAME").toString()+"主管：<br></br>&nbsp;&nbsp;&nbsp;";
                    String  message="";
                    if(type=="SY_SBU"||type=="menu_plUnit"){
                        if(listMonth.size()>1){
                            for (int i=0;i<listMonth.size();i++){
                                message+="<br></br>"+listMonth.get(i)+"數據："+this.message(listMonth.get(i),map.get("BI_USER").toString().trim(),type);
                            }
                        }else{
                            message+=this.message(listMonth.get(0),map.get("BI_USER").toString().trim(),type);
                        }
                    }
                    if(null==rtUserEmailAuthorities||rtUserEmailAuthorities.size()==0){
                        if(type.indexOf("PL_BU")!=-1){
                            title=date+"BU資產負債表";
                            content+=date+"BU資產負債表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">FIT"+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                        }else{
                            title=date+"FIT"+typeVal;
                            content+=date+"FIT"+typeVal+"已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">FIT"+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                        }
                    }else{
                        if(type.indexOf("PL_BU")!=-1){
                            title=date+"BU資產負債表";
                            content+=date+"BU資產負債表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">"+rtUserEmailAuthorities.toString()+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                        }else{
                            title=date+rtUserEmailAuthorities.toString()+typeVal;
                            content+=date+rtUserEmailAuthorities.toString()+typeVal+"已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">"+rtUserEmailAuthorities.toString()+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                        }
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

    public String message(String yearMonth,String userCode,String type) throws Exception {
        String message="";
        Connection c = SessionFactoryUtils.getDataSource(userDao.getSessionFactory()).getConnection();
        CallableStatement cs = c.prepareCall("{call cux_bi_pl_pkg.get_pl_data(?,?,?,?,?,?)}");
        cs.setString(1, yearMonth);
        cs.setString(2, userCode);
        cs.setString(3, type);
        cs.registerOutParameter(4, java.sql.Types.VARCHAR);
        cs.registerOutParameter(5, java.sql.Types.VARCHAR);
        cs.registerOutParameter(6, java.sql.Types.VARCHAR);
        cs.execute();
        if(null!=cs.getString(4)&&cs.getString(4).length()>0){
            message="<br></br><span style=\"color: #5bb75b;\">"+cs.getString(4)+"</span>";
            if(!yearMonth.substring(4,6).equals("01")){
                if(null!=cs.getString(5)&&cs.getString(5).length()>0){
                    message+="<br></br><span style=\"color: blue;\">"+cs.getString(5)+"</span>";
                }
            }
            if(Integer.parseInt(yearMonth.substring(0,4)) > 2022){
                if(null!=cs.getString(6)&&cs.getString(6).length()>0){
                    message+="<br></br><span style=\"color: blue;\">"+cs.getString(6)+"</span>";
                }
            }
        }
        cs.close();
        c.close();
        return message;
    }

    /**
     * 检查有没有产品信息未在维度表维护
     * @Scheduled(cron = "0 0 1 * * MON-SAT")
     */
    public void checkProductDimension(){
        try{
            String sql="SELECT distinct PRODUCT_SERIES_code FROM epmods.cux_inv_sbu_item_info_mv where PRODUCT_SERIES_DESC not in (select DIMENSION from FIT_DIMENSION where type='Product')";
            List<String> product=poTableService.listBySql(sql);
            if(null!=product&&product.size()>0){
                String content="Dear 系統管理員：</br>&nbsp;&nbsp;以下維度值還未維護，請儘快行動。<br></br>&nbsp;&nbsp;"+product.toString().substring(1,product.toString().length()-1)+"<br></br>Best Regards!";
                EmailUtil.emailsMany("it-ks-mfg@fit-foxconn.com","預算系統 Product Series 維護",content);
//               maggao@deloitte.com.cn
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 檢查損益單位明細質料
     * SELECT COUNT(1)
     *   FROM ecux_expense_pl_all t
     *  WHERE t.attribute8 IS NULL
     * 这个SQL有记录，就需要发邮件
     * 发送邮件后， 把attribute8 改为N
     * @Scheduled(cron = "0 0 1 * * MON-SAT")
     * @Scheduled(cron = "0 5 13 8 7 MON-SAT")
     * **/
    public void checkPLUpdate(){
        try{
            String sql="SELECT COUNT(1) FROM epmebs.ecux_expense_pl_all t WHERE t.attribute8 IS NULL ";
            List<Map> maps = poTableService.listMapBySql(sql);
            if (maps != null && !"0".equals(maps.get(0).get("COUNT(1)").toString())) {
                sql="SELECT max(t.PERIOD_YYYYMM) as YEAR_MONTH FROM epmebs.ecux_expense_pl_all t WHERE t.attribute8 IS NULL";
                maps = poTableService.listMapBySql(sql);
                String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and u.BI_USER in('F0606248','FIT0447','HAH0016109','F3748959','J5027812','F0541693','Maggie','Arrisa','Amber')";
//                String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and u.BI_USER in('Maggie','Amber')";
                List<Map> emailListC=poTableService.listMapBySql(sqlEmail);
                if(null!=emailListC&&emailListC.size()>0) {
                    String erro="郵件發送失敗：";
                    String yearMonth=maps.get(0).get("YEAR_MONTH").toString();
                    if(yearMonth.substring(5,6).equalsIgnoreCase("0")){
                        yearMonth=yearMonth.substring(0,4)+"年"+yearMonth.substring(6,7)+"月份";
                    }else{
                        yearMonth=yearMonth.substring(0,4)+"年"+yearMonth.substring(5,7)+"月份";
                    }
                    String content =yearMonth+"损益Rawdata数据已發佈，請點擊以下鏈接登錄BI平臺進行檢查，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">损益Rawdata</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，系統登錄、數據核對如有問題，請聯系顧問 , 分機 5070-32202 , 郵箱：ambcai@deloitte.com.cn。<br></br><br>Best Regards!";
                    for (Map map : emailListC) {
                        String c ="Dear " + map.get("BI_USERNAME").toString() + "主管：<br></br>&nbsp;&nbsp;&nbsp;"+content;
                        Boolean b= EmailUtil.emailsMany(map.get("EMAIL").toString(), yearMonth+"損益Rawdata數據", c);
                        if (!b){
                            erro+=map.get("BI_USER").toString()+",";
                        }
                    }
                    System.out.println(erro);
                    poTableService.updateSql("update BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/PL_BasicData/D.損益表基础數據' where BI_USER in('F0606248','FIT0447','HAH0016109','F3748959','J5027812','F0541693','Maggie','Arrisa','Amber')");
                    poTableService.updateSql("update epmebs.ecux_expense_pl_all t set t.attribute8='N' where attribute8 IS NULL");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 三表BU表发布邮件提醒
     * @Scheduled(cron = "0 8 21 15 8 MON-SAT")
     */
    public void plSendEamil(){
        try{
            System.out.print("BU三表检验是否有同步数据 如果有给有权限的人发送邮件");
            String sql="SELECT YEARS,period  FROM epmexp.cux_bs_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by period";
            String sqlCF="SELECT YEARS,period  FROM epmexp.cux_cf_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by period";
            List<Map> list=poTableService.listMapBySql(sql);
            List<Map> list1=poTableService.listMapBySql(sqlCF);
            if(list.isEmpty()&&!list1.isEmpty()){
                Boolean b=this.biBuSendEamil(list1,"CF");
                if(b){
                    poTableService.updateSql("update epmexp.cux_cf_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.BU現金流量表' where instr(';'||BI_GROUP||';',';PL_BU;') > 0 ");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.BU現金流量表-不含Total' where instr(';'||BI_GROUP||';',';PL_BU1;') > 0 ");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.BU現金流量表-特殊' where instr(';'||BI_GROUP||';',';PL_BU2;') > 0 ");
                }
            }else if(!list.isEmpty()&&list1.isEmpty()){
                Boolean b=this.biBuSendEamil(list,"BS");
                if(b){
                    poTableService.updateSql("update epmexp.cux_bs_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表' where instr(';'||BI_GROUP||';',';PL_BU;') > 0 ");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表-不含Total' where instr(';'||BI_GROUP||';',';PL_BU1;') > 0 ");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表-特殊' where instr(';'||BI_GROUP||';',';PL_BU2;') > 0 ");
                }
            }else{
                Boolean b=this.biBuSendEamil(list,"ALL");
                if(b){
                    poTableService.updateSql("update epmexp.cux_bs_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                    poTableService.updateSql("update epmexp.cux_cf_default_bi set send_email_flag='N' where send_email_flag='Y' and years not in ('2021')");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表' where instr(';'||BI_GROUP||';',';PL_BU;') > 0 ");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表-不含Total' where instr(';'||BI_GROUP||';',';PL_BU1;') > 0 ");
                    poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表-特殊' where instr(';'||BI_GROUP||';',';PL_BU2;') > 0 ");
                }
            }
        }catch (Exception e){
            System.out.print("系統定時任務失敗");
            e.printStackTrace();
        }
    }

    public Boolean biBuSendEamil(List<Map> list,String type) {
        Boolean isSend=false;
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
//            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and u.BI_USER in('Maggie','Amber') and (instr(';'||u.BI_GROUP||';',';PL_BU;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU1;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU2;') > 0)";
            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';PL_BU;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU1;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU2;') > 0 ";
            List<Map> emailListC=poTableService.listMapBySql(sqlEmail);
            if(null!=emailListC&&emailListC.size()>0){
                date+="月份";
                String erro="未发送成功的邮箱：";
                String title="";
                String content="";
                for (Map map:emailListC) {
                    content="Dear "+map.get("BI_USERNAME").toString()+"主管：<br></br>&nbsp;&nbsp;&nbsp;";
                    if("BS".equals(type)){
                        title=date+"BU資產負債表";
                        content+=date+"BU資產負債表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">BU資產負債表</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }else if("CF".equals(type)){
                        title=date+"BU现金流量表";
                        content+=date+"BU資產負債表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">BU现金流量表</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }else if("ALL".equals(type)){
                        title=date+"BU資產負債表 & BU现金流量表";
                        content+=date+"BU資產負債表 & BU现金流量表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">BU資產負債表</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
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

    /**
     * 實際非價格CD報表、採購CD手動匯總表
     * 1-8號每天9點發送郵件提醒
     * 8-10號未上傳需要同時增加核准人郵箱
     * 實際採購非價格CD匯總表:FIT_ACTUAL_PO_NPRICECD_DTL
     * 採購CD手動匯總表:FIT_PO_BUDGET_CD_DTL
     * @Scheduled(cron = "30 18 * * * *")
     */
    public void poSendTaskEamil(){
        poEamil("FIT_ACTUAL_PO_NPRICECD_DTL");
        poEamil("FIT_PO_BUDGET_CD_DTL");
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
        content+="<br></br>如已經完成，請忽略該提醒<br></br>&nbsp;&nbsp;<a href=\"https://itpf-test.one-fit.com/fit/login\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系顧問 , 分機 5070-32202 , 郵箱：emji@deloitte.com.cn。<br></br>Best Regards!";
        if(Integer.valueOf(date[2])<=8){
            sql="select distinct u.email from fit_user u,FIT_PO_AUDIT_ROLE r ,FIT_PO_AUDIT_ROLE_USER ur\n" +
                    "where u.id=ur.user_id and r.id=ur.role_id and r.code='SOURCER' and type='BI' and u.COMMODITY_MAJOR is not null\n" +
                    "and u.username not in \n" +
                    "(select t.create_user from fit_po_task t where t.type ='"+type+"' and substr(t.name,0,6)='"+dateStr+"'\n" +
                    "and  instr(','||u.COMMODITY_MAJOR||',',','||commodity_major||',') > 0 )";
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
                    "and  instr(','||u.COMMODITY_MAJOR||',',','||commodity_major||',') > 0 )";
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

//    @Scheduled(cron = "0 13 16 20 9 MON-SAT")
    public void bocklogEamil(){
        String title="FIT_Revenue_and_Backlog_Summary";
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        String signTimet = df.format(new Date());
        String content="Dear主管：<br></br>&nbsp;&nbsp;&nbsp;"+signTimet+"FIT_Revenue_and_Backlog_Summary已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi-test.one-fit.com/analytics\" style=\"color: blue;\">FIT_Revenue_and_Backlog_Summary</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 , 郵箱：emji@deloitte.com.cn<br></br><br>Best Regards!";
        String sql="select EMAIL from BIDEV.Bi_user_list u where u.EMAIL is not null and BI_USER in('Maggie','Emma','HAH0016109','F0606248')";
        List<String> emailListC=poTableService.listBySql(sql);
        if(null!=emailListC&&emailListC.size()>0) {
            EmailUtil.emailsMany(emailListC,title,content);
            poTableService.updateSql("update BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/RT/D.FIT Revenue and Backlog Sum' where BI_USER in('Maggie','Emma')");
        }
    }
}
