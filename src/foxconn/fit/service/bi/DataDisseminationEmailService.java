package foxconn.fit.service.bi;

import foxconn.fit.dao.ebs.ParameterDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.util.EmailUtil;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataDisseminationEmailService {

    @Autowired
    private PoTableService poTableService;
    @Autowired
    private ParameterDao userDao;

    /**进入首页获取数据更新的月份*/
    public Model index(Model model){
        String sql ="SELECT YEARS||'-'||period as YEAR_MONTH FROM epmexp.cux_pl_default_bi where send_email_flag='Y' and years not in ('2021')  group by YEARS,period order by years||period";
        List<String> list=userDao.listBySql(sql);
        model.addAttribute("plDate",list);
        sql="SELECT PERIOD_YYYYMM as YEAR_MONTH FROM epmebs.ecux_expense_pl_all WHERE attribute8 IS NULL group by PERIOD_YYYYMM order by PERIOD_YYYYMM";
        list=userDao.listBySql(sql);
        model.addAttribute("rawdataDate",list);
        sql="SELECT YEARS||'-'||period as YEAR_MONTH FROM epmexp.cux_bs_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by YEARS||period";
        list=userDao.listBySql(sql);
        model.addAttribute("bsDate",list);
        sql="SELECT YEARS||'-'||period as YEAR_MONTH FROM epmexp.cux_cf_default_bi  where send_email_flag='Y' and years not in ('2021') group by YEARS,period order by YEARS||period";
        list=userDao.listBySql(sql);
        model.addAttribute("cfDate",list);
        return model;
    }

    /**損益表數據更新郵件通知*/
    public AjaxResult plBIEmail(AjaxResult ajaxResult,String date){
        System.out.print("BI平台三表检验是否有同步数据 如果有给有权限的人发送邮件");
        ajaxResult=this.biSendEamil(date,"SY_SBU",ajaxResult);
        if ("fail".equals(ajaxResult.getResult().get("flag"))){
            return ajaxResult;
        }
        ajaxResult=this.biSendEamil(date,"menu_plUnit",ajaxResult);
        return ajaxResult;
    }

    public AjaxResult  biSendEamil(String yearMonth,String type,AjaxResult ajaxResult){
        String typeVal="";
        if(type.equals("menu_plUnit")){
            typeVal="損益單位損益表";
        }else if(type.equals("SY_SBU")){
            typeVal="损益表";
        }
        try {
            String [] period=yearMonth.split("-");
            String date=period[0]+"年";
            String month=period[1];
            if(month.substring(0,1).equalsIgnoreCase("0")){
                month=month.substring(1,2);
            }
            date+=month+"月份";
//            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';"+type+";') > 0  and u.BI_USER in('Maggie','Amber')";
                String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';"+type+";') > 0";
            if("menu_plUnit".equals(type)){
//                sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';menu_plUnit;') > 0 and instr(';'||u.BI_GROUP||';',';SY_SBU;') <= 0 and u.BI_USER in('Maggie','Amber')";
                sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';menu_plUnit;') > 0 and instr(';'||u.BI_GROUP||';',';SY_SBU;') <= 0 ";
            }
            List<Map> emailListC=poTableService.listMapBySql(sqlEmail);
            if(null!=emailListC&&emailListC.size()>0){
                String erro="";
                String title="";
                for (Map map:emailListC) {
                    String str="'"+String.valueOf(map.get("BI_GROUP")).replace(";","','")+"'";
                    sqlEmail="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('BS','TS')";
                    if(type.equals("menu_plUnit")){
                        sqlEmail="select distinct SBU from FIT_USER_RT_EMAIL_AUTHORITY where AUTHORITY in("+str+") and type in('SY','SY_Unit')";
                    }
                    List<String> rtUserEmailAuthorities=poTableService.listBySql(sqlEmail);
                    String content="Dear "+map.get("BI_USERNAME").toString()+"主管：<br></br>&nbsp;&nbsp;&nbsp;";
                    String message=this.message(yearMonth.replace("-",""),map.get("BI_USER").toString().trim(),type);
                    if(null==rtUserEmailAuthorities||rtUserEmailAuthorities.size()==0){
                            title=date+"FIT"+typeVal;
                            content+=date+"FIT"+typeVal+"已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">FIT"+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }else{
                            title=date+rtUserEmailAuthorities.toString()+typeVal;
                            content+=date+rtUserEmailAuthorities.toString()+typeVal+"已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。"+message+"<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">"+rtUserEmailAuthorities.toString()+typeVal+"</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }
                    System.out.print("发送邮件："+map.get("EMAIL").toString()+"****主题："+title+"****内容："+content);
                        if(type.equals("menu_plUnit")){
                            poTableService.updateSql("update epmexp.cux_pl_default_bi set send_email_flag='N' where send_email_flag='Y' and YEARS||'-'||period='"+yearMonth+"'");
                            poTableService.updateSql("update BIDEV.Bi_user_list set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/D.損益單位損益表' where instr(';'||BI_GROUP||';',';menu_plUnit;') > 0  ");
                        }else if(type.equals("SY_SBU")){
                            poTableService.updateSql("update epmexp.cux_pl_default_bi set send_email_flag='N' where send_email_flag='Y' and YEARS||'-'||period='"+yearMonth+"'");
                            poTableService.updateSql("update BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/D.SBU损益表' where instr(';'||BI_GROUP||';',';SY_SBU;') > 0  ");
                        }
                    boolean isSend= EmailUtil.emailsMany(map.get("EMAIL").toString(),title,content);
                    if(!isSend){
                        erro+=map.get("BI_USER").toString()+",";
                    }
                }
                if (erro!="" && !"".equals(erro)) {
                    //未发送成功的邮箱：
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", typeVal+"更新郵件通知發送失敗的有 : " + erro.substring(0,erro.length()-1));
                    System.out.print(erro);
                }
            }
        }catch (Exception e){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", typeVal+"更新郵件通知發送失敗 : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult;
    }

    /**損益表获取郵件正文數值内容*/
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

    /**损益Rawdata数据已發佈郵件更新*/
    public AjaxResult rawdataEmail(AjaxResult ajaxResult,String yearMonth){
        String date=yearMonth;
        try{
            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and u.BI_USER in('F0606248','FIT0447','HAH0016109','F3748959','J5027812','F0541693','Maggie','Arrisa','Amber')";
//            String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and u.BI_USER in('Maggie','Amber')";
            List<Map> emailListC=poTableService.listMapBySql(sqlEmail);
            if(null!=emailListC&&emailListC.size()>0) {
                String erro="";
                if(yearMonth.substring(5,6).equalsIgnoreCase("0")){
                    yearMonth=yearMonth.substring(0,4)+"年"+yearMonth.substring(6,7)+"月份";
                }else{
                    yearMonth=yearMonth.substring(0,4)+"年"+yearMonth.substring(5,7)+"月份";
                }
                String content =yearMonth+"损益Rawdata数据已發佈，請點擊以下鏈接登錄BI平臺進行檢查，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">损益Rawdata</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，系統登錄、數據核對如有問題，請聯系顧問 , 分機 5070-32202 , 郵箱：ambcai@deloitte.com.cn。<br></br><br>Best Regards!";
                poTableService.updateSql("update BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/PL/PL_BasicData/D.損益表基础數據' where BI_USER in('F0606248','FIT0447','HAH0016109','F3748959','J5027812','F0541693','Maggie','Arrisa','Amber')");
                poTableService.updateSql("update epmebs.ecux_expense_pl_all set attribute8='N' where attribute8 IS NULL and PERIOD_YYYYMM='"+date+"'");
                for (Map map : emailListC) {
                    String c ="Dear " + map.get("BI_USERNAME").toString() + "主管：<br></br>&nbsp;&nbsp;&nbsp;"+content;
                    Boolean b= EmailUtil.emailsMany(map.get("EMAIL").toString(), yearMonth+"損益Rawdata數據", c);
                    if (!b){
                        erro+=map.get("BI_USER").toString()+",";
                    }
                }
                if (erro!="" && !"".equals(erro)) {
                    //未发送成功的邮箱：
                    ajaxResult.put("flag", "fail");
                    ajaxResult.put("msg", "Rawdata更新郵件通知發送失敗的有 : " + erro.substring(0,erro.length()-1));
                    System.out.print(erro);
                }
            }
        }catch (Exception e){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "Rawdata更新郵件通知發送失敗 : " + ExceptionUtil.getRootCauseMessage(e));
            e.printStackTrace();
        }
        return ajaxResult;
    }

    /***/
    public AjaxResult bsEmail(AjaxResult ajaxResult,String date){
        System.out.print("BU資產負債表检验是否有同步数据 如果有给有权限的人发送邮件");
        ajaxResult=this.biBuSendEamil(date,"BS",ajaxResult);
        return ajaxResult;
    }

    /***/
    public AjaxResult cfEmail(AjaxResult ajaxResult,String date){
        System.out.print("BU現金流量表檢驗是否有同步数据 如果有给有权限的人发送邮件");
        ajaxResult=this.biBuSendEamil(date,"CF",ajaxResult);
        return ajaxResult;
    }


    public AjaxResult biBuSendEamil(String yearMonth,String type,AjaxResult ajaxResult) {
        try {
            String [] period=yearMonth.split("-");
            String date=period[0]+"年";
            String month=period[1];
            if(month.substring(0,1).equalsIgnoreCase("0")){
                month=month.substring(1,2);
            }
            date+=month+"月份";
//                String sqlEmail="select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and u.BI_USER in('Maggie','Amber') and (instr(';'||u.BI_GROUP||';',';PL_BU;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU1;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU2;') > 0)";
                String sqlEmail = "select BI_USER,BI_USERNAME,EMAIL,BI_GROUP,BI_PORTALPATH from BIDEV.Bi_user_list u where u.EMAIL is not null and instr(';'||u.BI_GROUP||';',';PL_BU;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU1;') > 0 or instr(';'||u.BI_GROUP||';',';PL_BU2;') > 0 ";
                List<Map> emailListC = poTableService.listMapBySql(sqlEmail);
                if (null != emailListC && emailListC.size() > 0) {
                    date += "月份";
                    String erro = "";
                    String title = "";
                    String content = "";
                    if ("BS".equals(type)) {
                        poTableService.updateSql("update epmexp.cux_bs_default_bi set send_email_flag='N' where send_email_flag='Y' and YEARS||'-'||period='"+yearMonth+"'");
                        poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表' where instr(';'||BI_GROUP||';',';PL_BU;') > 0 ");
                        poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表-不含Total' where instr(';'||BI_GROUP||';',';PL_BU1;') > 0 ");
                        poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/BS/D.BU資產負債表-特殊' where instr(';'||BI_GROUP||';',';PL_BU2;') > 0 ");
                        title = date + "BU資產負債表";
                        content += date + "BU資產負債表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">BU資產負債表</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    } else if ("CF".equals(type)) {
                        poTableService.updateSql("update epmexp.cux_cf_default_bi set send_email_flag='N' where send_email_flag='Y' and YEARS||'-'||period='"+yearMonth+"'");
                        poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.BU現金流量表' where instr(';'||BI_GROUP||';',';PL_BU;') > 0 ");
                        poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.BU現金流量表-不含Total' where instr(';'||BI_GROUP||';',';PL_BU1;') > 0 ");
                        poTableService.updateSql("update  BIDEV.Bi_user_list  set BI_PORTALPATH='/shared/FIT-BI Platform v2/01.分析/CF/D.BU現金流量表-特殊' where instr(';'||BI_GROUP||';',';PL_BU2;') > 0 ");
                        title = date + "BU现金流量表";
                        content += date + "BU现金流量表已發佈，請點擊以下鏈接登錄BI平臺進行查看，謝謝。<br></br>&nbsp;&nbsp;&nbsp;<b>Link to:</b>&nbsp;<a href=\"https://bi.one-fit.com/analytics\" style=\"color: blue;\">BU现金流量表</a><br></br>BI平臺登錄賬號及密碼是EIP賬號及密碼，登錄如有問題，請聯系顧問 , 分機 5070-32202 ,  郵箱：ambcai@deloitte.com.cn<br></br><br>Best Regards!";
                    }

                    for (Map map : emailListC) {
                        String contentVal="";
                        contentVal = "Dear " + map.get("BI_USERNAME").toString() + "主管：<br></br>&nbsp;&nbsp;&nbsp;"+content;
                        System.out.print("发送邮件：" + map.get("EMAIL").toString() + "****主题：" + title + "****内容：" + contentVal);
                        boolean isSend = EmailUtil.emailsMany(map.get("EMAIL").toString(), title, contentVal);
                        if (!isSend) {
                            erro += map.get("BI_USER").toString() + ",";
                        }
                        if (erro!="" && !"".equals(erro)) {
                            //未发送成功的邮箱：
                            ajaxResult.put("flag", "fail");
                            ajaxResult.put("msg", "數據更新郵件通知發送失敗的有 : " + erro.substring(0,erro.length()-1));
                            System.out.print(erro);
                        }
                    }
                }
        }catch (Exception e){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg","數據更新郵件通知發送失敗 : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult;
    }
}
