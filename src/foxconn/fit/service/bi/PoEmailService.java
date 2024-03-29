package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoTaskDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoEmailLog;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.EmailUtil;
import foxconn.fit.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class PoEmailService extends BaseService<PoEmailLog> {

    @Autowired
    private PoTaskDao poTaskDao;

    @Value("${accessUrl}")
    String accessUrl;
    /**
     * 发送邮件
     * @param ajaxResult
     * @param emailGroup
     * @param title
     * @param content
     * @return
     */
    public AjaxResult sendEmail(AjaxResult ajaxResult, String emailGroup, String title,String year,String content, List<MultipartFile> list, HttpServletRequest request,String endDate) throws IOException {
        if(endDate.length()==9){
            endDate=endDate.substring(0,4)+"-0"+endDate.substring(5,9);
        }
        String realPath = request.getRealPath("");
        String user = SecurityUtils.getLoginUser().getUsername();
        List<String> userName= poTaskDao.listBySql("select realname from FIT_USER where username='"+user+"'");
        if(null==userName.get(0)){
            userName.set(0,user);
        }
        String[] emailUser=emailGroup.split(",");
        String emailUserVal="";
        for (int i=0;i<emailUser.length;i++){
            emailUserVal+="'"+emailUser[i]+"',";
        }
        String sqlC="select distinct EMAIL from EPMODS.fit_user where EMAIL is not null and (REALNAME in ("+emailUserVal.substring(0,emailUserVal.length()-1)+") or USERNAME in ("+emailUserVal.substring(0,emailUserVal.length()-1)+"))";
        List<String> emailListC=poTaskDao.listBySql(sqlC);
        emailListC=emailListC.stream().distinct().collect(Collectors.toList());
        if(emailListC.size()==0){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "請聯係管理員維護對應分組人員郵箱(Task Type Fail)");
            return ajaxResult;
        }else {
            List<File> fileList=new ArrayList<>();
            String Id = Long.toString(System.currentTimeMillis());
            String fileName="";
            File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+Id);
            for (int i=0;i<list.size();i++){
                MultipartFile file = list.get(i);
                File toFile = null;
                if (file.equals("") || file.getSize() <= 0) {
                    file = null;
                } else {
                    //获取存储路径文件夹
                    outFile.mkdirs();
                    //保存到本地
                    toFile = new File(outFile.getAbsolutePath() +File.separator +file.getOriginalFilename());
                    file.transferTo(toFile);
                    fileName+=file.getOriginalFilename()+"**";
                }
                fileList.add(toFile);
            }
            content=content.replace("\n","</br>");
            content=content.replace(" ","&nbsp;");
            Boolean isSend = EmailUtil.emailsMany(emailListC, title,content+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!",fileList);
            if(isSend){
                content=content.replace("</br>","\n");
                content=content.replace("&nbsp;"," ");
                String sql="insert into CUX_PO_EMAIL(CREATED_BY,CREATED_NAME,EMAIL_TITLE,EMAIL_YEAR,EMAIL_CONTENT,EMAIL_TEAM,FILE_ADDRESS,FILE_NAME,END_DATE) values('"+user
                        +"','"+userName.get(0)+"','"+title+"','"+year+"','"+content+"','"+emailGroup+"','"+Id+File.separator+"','"+fileName.substring(0,fileName.length()-2)+"','"+endDate+"')";
                poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            }else{
                //获取存储路径文件夹
                if(outFile.isDirectory()) {
                    File[] files = outFile.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        //删除子文件
                        if (files[i].isFile()) {
                            files[i].delete();
                        }
                    }
                }
                outFile.delete();
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                return ajaxResult;
            }
        }
        return ajaxResult;
    }
    public AjaxResult sendEmail(AjaxResult ajaxResult, String emailGroup, String title,String year, String content,String endDate){
        if(endDate.length()==9){
            endDate=endDate.substring(0,4)+"-0"+endDate.substring(5,9);
        }
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String user = loginUser.getUsername();
        List<String> userName= poTaskDao.listBySql("select realname from FIT_USER where username='"+user+"'");
        if(null==userName.get(0)){
            userName.set(0,user);
        }
        String[] emailUser=emailGroup.split(",");
        String emailUserVal="";
        for (int i=0;i<emailUser.length;i++){
            emailUserVal+="'"+emailUser[i]+"',";
        }
        String sqlC="select distinct EMAIL from EPMODS.fit_user where EMAIL is not null and (REALNAME in ("+emailUserVal.substring(0,emailUserVal.length()-1)+") or USERNAME in ("+emailUserVal.substring(0,emailUserVal.length()-1)+"))";
        List<String> emailListC=poTaskDao.listBySql(sqlC);
        emailListC=emailListC.stream().distinct().collect(Collectors.toList());
        if(emailListC.size()==0){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "請聯係管理員維護對應分組人員郵箱(Task Type Fail)");
            return ajaxResult;
        }else {
            content=content.replace("\n","</br>");
            content=content.replace(" ","&nbsp;");
            Boolean isSend = EmailUtil.emailsMany(emailListC, title,content+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!");
            if(isSend){
               content=content.replaceAll("'","''");
                String sql="insert into CUX_PO_EMAIL(CREATED_BY,CREATED_NAME,EMAIL_TITLE,EMAIL_YEAR,EMAIL_CONTENT,EMAIL_TEAM,END_DATE) values('"+user
                        +"','"+userName.get(0)+"','"+title+"','"+year+"','"+content+"','"+emailGroup+"','"+endDate+"')";
                poTaskDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
            }else{
                ajaxResult.put("flag", "fail");
                ajaxResult.put("msg", "郵件發送失敗 (Task Type Fail)");
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    @Override
    public BaseDaoHibernate<PoEmailLog> getDao() {
        return null;
    }

    private List<List<String>> selectGroup(List<String> list){
        List<List<String>> groupV=new ArrayList();
        for (int i=0;i<list.size();i++) {
            List<String> listValue=new ArrayList<>();
            listValue.add(list.get(i));
            listValue.addAll(poTaskDao.listBySql("select distinct decode(REALNAME,null,USERNAME,REALNAME) from fit_user u inner join FIT_PO_AUDIT_ROLE_USER ur on u.id=ur.user_id inner join FIT_PO_AUDIT_ROLE r on ur.role_id=r.id  \n" +
                    "and r.name='"+list.get(i)+"'"));
            groupV.add(listValue);
        }
        return groupV;
    }

    public void index(Model model){
        List<String> list=poTaskDao.listBySql("select distinct name from FIT_PO_AUDIT_ROLE where code in('PLACECLASS1','MANAGER','specialManager','PD','PLACECLASS','T_MANAGER','TDC','CPO','MM','SBUCompetent') order by name");
        List<String> yearList=poTaskDao.listBySql("select distinct ID_YEAR from BIDEV.DM_D_TIME_YEAR order by ID_YEAR");
        List<List<String>> listGroup=this.selectGroup(list);
        model.addAttribute("EmailUserTeam",list);
        model.addAttribute("yearList",yearList);
        model.addAttribute("yearDate", Calendar.getInstance().get(Calendar.YEAR));
        model.addAttribute("listGroup",listGroup);
    }

    //定时任务发送邮件提醒
    public void sendEmailTiming(String username,String content,String title){
        if(username.length()>1){
            String sqlC="select distinct EMAIL from EPMODS.fit_user where USERNAME in ("+username+") and type='BI' and EMAIL is not null";
            List<File> list=new ArrayList<>();
            File file = new File("D:"+File.separator+"JAVA"+File.separator+"apache-tomcat-8.0.50"+File.separator+"webapps"+File.separator+"fit"+File.separator+"static"+File.separator+"template"+File.separator+"po"+File.separator+"FIT_VOC_年度目標CD審批流程_v1.0.pdf");
            list.add(file);
            List<String> emailListC=poTaskDao.listBySql(sqlC);
            emailListC=emailListC.stream().distinct().collect(Collectors.toList());
            EmailUtil.emailsMany(emailListC,title,content+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!",list);
        }
    }
    //定时任务发送邮件提醒CC
    public void sendEmailTimingCC(String username,String ccUserName,String content,String title){
        String sql="select distinct EMAIL from EPMODS.fit_user where USERNAME in ("+username+") and type='BI' and EMAIL is not null";
        List<File> list=new ArrayList<>();
        File file = new File("D:"+File.separator+"JAVA"+File.separator+"apache-tomcat-8.0.50"+File.separator+"webapps"+File.separator+"fit"+File.separator+"static"+File.separator+"template"+File.separator+"po"+File.separator+"FIT_VOC_年度目標CD審批流程_v1.0.pdf");
        list.add(file);
        List<String> emailList=poTaskDao.listBySql(sql);
        emailList=emailList.stream().distinct().collect(Collectors.toList());
        sql="select distinct EMAIL from EPMODS.fit_user where USERNAME in ("+ccUserName+") and type='BI' and EMAIL is not null";
        List<String> emailListCC=poTaskDao.listBySql(sql);
        String emailVal="";
        for (String e:emailList) {
            emailVal+=e+",";
        }
        String emailValCC="";
        for (String e:emailListCC) {
            emailValCC+=e+",";
        }
        EmailUtil.emailsManyCC(emailVal.substring(0,emailVal.length()-1),emailValCC.substring(0,emailValCC.length()-1),title,content+"</br>&nbsp;&nbsp;<a href=\""+accessUrl+"\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系郵箱：brian.pr.chen@fit-foxconn.com。<br></br>Best Regards!",list);
    }
}