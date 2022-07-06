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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class PoEmailService extends BaseService<PoEmailLog> {

    @Autowired
    private PoTaskDao poTaskDao;
    @Autowired
    private PoTableService poTableService;


    /**
     * 发送邮件
     * @param ajaxResult
     * @param emailGroup
     * @param title
     * @param content
     * @return
     */
    public AjaxResult sendEmail(AjaxResult ajaxResult, String emailGroup, String title, String content, List<MultipartFile> list, HttpServletRequest request,String endDate) throws IOException {
        String realPath = request.getRealPath("");
        String user = SecurityUtils.getLoginUser().getUsername();
        List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"'");
        if(null==userName.get(0)){
            userName.set(0,user);
        }
        String[] emailUser=emailGroup.split(",");
        String emailUserVal="";
        for (int i=0;i<emailUser.length;i++){
            emailUserVal+="'"+emailUser[i]+"',";
        }
        String sqlC="select distinct EMAIL_ADDRESS from EPMODS.CUX_PO_EMAIL_GROUP where USER_NAME in ("+emailUserVal.substring(0,emailUserVal.length()-1)+")";
        List<String> emailListC=poTableService.listBySql(sqlC);
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
            Boolean isSend = EmailUtil.emailsMany(emailListC, title,content,fileList);
            if(isSend){
                content=content.replace("</br>","\n");
                content=content.replace("&nbsp;"," ");
                String sql="insert into CUX_PO_EMAIL(CREATED_BY,CREATED_NAME,EMAIL_TITLE,EMAIL_CONTENT,EMAIL_TEAM,FILE_ADDRESS,FILE_NAME,END_DATE) values('"+user
                        +"','"+userName.get(0)+"','"+title+"','"+content+"','"+emailGroup+"','"+Id+File.separator+"','"+fileName.substring(0,fileName.length()-2)+"','"+endDate+"')";
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
    public AjaxResult sendEmail(AjaxResult ajaxResult, String emailGroup, String title, String content,String endDate){
        UserDetailImpl loginUser = SecurityUtils.getLoginUser();
        String user = loginUser.getUsername();
        List<String> userName= poTableService.listBySql("select realname from FIT_USER where username='"+user+"'");
        if(null==userName.get(0)){
            userName.set(0,user);
        }
        String[] emailUser=emailGroup.split(",");
        String emailUserVal="";
        for (int i=0;i<emailUser.length;i++){
            emailUserVal+="'"+emailUser[i]+"',";
        }
        String sqlC="select distinct EMAIL_ADDRESS from EPMODS.CUX_PO_EMAIL_GROUP where USER_NAME in ("+emailUserVal.substring(0,emailUserVal.length()-1)+")";
        List<String> emailListC=poTableService.listBySql(sqlC);
        emailListC=emailListC.stream().distinct().collect(Collectors.toList());
        if(emailListC.size()==0){
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "請聯係管理員維護對應分組人員郵箱(Task Type Fail)");
            return ajaxResult;
        }else {
            content=content.replace("\n","</br>");
            content=content.replace(" ","&nbsp;");
            Boolean isSend = EmailUtil.emailsMany(emailListC, title,content+"</br>&nbsp;&nbsp;<a href=\"http://10.98.5.23:8080/fit/login\" style=\"color: blue;\">接口平臺</a>");
            if(isSend){
                String sql="insert into CUX_PO_EMAIL(CREATED_BY,CREATED_NAME,EMAIL_TITLE,EMAIL_CONTENT,EMAIL_TEAM,END_DATE) values('"+user
                        +"','"+userName.get(0)+"','"+title+"','"+content+"','"+emailGroup+"','"+endDate+"')";
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

    public List<List<String>> selectGroup(List<String> list){
        List<List<String>> groupV=new ArrayList();
        for (int i=0;i<list.size();i++) {
            List<String> listValue=new ArrayList<>();
            listValue.add(list.get(i));
            listValue.addAll(poTableService.listBySql("select USER_NAME from CUX_PO_EMAIL_GROUP where USER_GROUP='"+list.get(i)+"'"));
            groupV.add(listValue);
        }
        return groupV;
    }


    //定时任务发送邮件提醒
    public void sendEmailTiming(String username,String content,String title){
        String sqlC="select distinct EMAIL from EPMODS.fit_user where USERNAME in ("+username+") and type='BI' and EMAIL is not null";
        List<String> emailListC=poTableService.listBySql(sqlC);
        emailListC=emailListC.stream().distinct().collect(Collectors.toList());
        EmailUtil.emailsMany(emailListC,title,content+"</br>&nbsp;&nbsp;<a href=\"http://10.98.5.23:8080/fit/login?task=Y\" style=\"color: blue;\">接口平臺</a>");
    }
}