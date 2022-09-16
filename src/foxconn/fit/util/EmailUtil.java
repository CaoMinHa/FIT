package foxconn.fit.util;


import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class EmailUtil
{
    /**
     * 不带附件
     * @param emails
     * @param title
     * @param content
     * @return
     */
    public static Boolean emailsMany(List<String> emails , String title, String content) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "10.98.5.95");
            props.put("mail.smtp.port", "25");    //端口
            props.setProperty("mail.debug", "true");// 开启debug日志，日志更详细
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            Message message = createAttachMailMany(emails,session,title,content);
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static MimeMessage createAttachMailMany(List<String> emails ,Session session,String title,String content)
            throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("fit-bi@mail.foxconn.com"));
        for (String email : emails) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        }
        message.setSubject(title,"UTF-8");
        message.setContent(content,"text/html;charset=UTF-8");
        return message;
    }

    /**
     * 单个用户发送
     * @param email
     * @param title
     * @param content
     * @return
     */
    public static Boolean emailsMany(String email , String title, String content) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "10.98.5.95");
            props.put("mail.smtp.port", "25");    //端口
            props.setProperty("mail.debug", "true");// 开启debug日志，日志更详细
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            Message message = createAttachMailMany(email,session,title,content);
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static MimeMessage createAttachMailMany(String email ,Session session,String title,String content)
            throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("fit-bi@mail.foxconn.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(title,"UTF-8");
        message.setContent(content,"text/html;charset=UTF-8");
        return message;
    }

    /**
     * 带附件发送
     * @param emails
     * @param title
     * @param content
     * @param fileList
     * @return
     */
    public static Boolean emailsMany(List<String> emails , String title, String content, List<File> fileList) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "10.98.5.95");
            props.put("mail.smtp.port", "25");    //端口
            props.setProperty("mail.debug", "true");// 开启debug日志，日志更详细
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            Message message = createAttachMailMany(emails,session,title,content,fileList);
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 带附件发送 CC
     * @param emails
     * @param title
     * @param content
     * @param fileList
     * @return
     */
    public static Boolean emailsManyCC(String email,String emails, String title, String content, List<File> fileList) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "10.98.5.95");
            props.put("mail.smtp.port", "25");    //端口
            props.setProperty("mail.debug", "true");// 开启debug日志，日志更详细
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            Message message = createAttachMailManyCC(email,emails,session,title,content,fileList);
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static MimeMessage createAttachMailMany(List<String> emails , Session session, String title, String contentText, List<File> fileList)
            throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("fit-bi@mail.foxconn.com"));
        for (String email : emails) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        }
        message.setSubject(title,"UTF-8");

        MimeMultipart msgMultipart = new MimeMultipart("mixed");
        message.setContent(msgMultipart);

        MimeBodyPart content = new MimeBodyPart();
        msgMultipart.addBodyPart(content);

        // 8，设置正文格式
        MimeMultipart bodyMultipart = new MimeMultipart("related");
        content.setContent(bodyMultipart);

        // 9，设置正文内容
        MimeBodyPart htmlPart = new MimeBodyPart();
        bodyMultipart.addBodyPart(htmlPart);
        htmlPart.setContent(contentText+"</br>&nbsp;&nbsp;<a href=\"https://itpf-test.one-fit.com/fit/login\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系顧問 , 分機 5070-32202 , 郵箱：emji@deloitte.com.cn。<br></br>Best Regards!", "text/html;charset=UTF-8");
        if (null == fileList || 0 == fileList.size()) {
        }else{
            try {
                for (int i=0;i<fileList.size();i++){
                    if(null != fileList.get(i) && 0 !=fileList.get(i).length() && fileList.get(i).exists()){
                        //设置相关文件
                        MimeBodyPart filePart = new MimeBodyPart();
                        FileDataSource dataSource = new FileDataSource(fileList.get(i));
                        DataHandler dataHandler = new DataHandler(dataSource);
                        // 文件处理
                        filePart.setDataHandler(dataHandler);
                        // 附件名称
                        filePart.setFileName(MimeUtility.encodeText(fileList.get(i).getName(), "utf-8", "B"));
                        // 放入正文（有先后顺序，所以在正文后面）
                        msgMultipart.addBodyPart(filePart);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        message.saveChanges();
        return message;
    }

    public static MimeMessage createAttachMailManyCC(String email ,String emails,Session session, String title, String contentText, List<File> fileList)
            throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("fit-bi@mail.foxconn.com"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emails));
        message.setSubject(title,"UTF-8");
        MimeMultipart msgMultipart = new MimeMultipart("mixed");
        message.setContent(msgMultipart);

        MimeBodyPart content = new MimeBodyPart();
        msgMultipart.addBodyPart(content);

        // 8，设置正文格式
        MimeMultipart bodyMultipart = new MimeMultipart("related");
        content.setContent(bodyMultipart);

        // 9，设置正文内容
        MimeBodyPart htmlPart = new MimeBodyPart();
        bodyMultipart.addBodyPart(htmlPart);
        htmlPart.setContent(contentText+"</br>&nbsp;&nbsp;<a href=\"https://itpf-test.one-fit.com/fit/login\" style=\"color: blue;\">接口平臺</a><br></br>接口平臺登錄賬號是EIP賬號，密碼默認11111111，登錄如有問題，請聯系顧問 , 分機 5070-32202 , 郵箱：emji@deloitte.com.cn。<br></br>Best Regards!", "text/html;charset=UTF-8");
        if (null == fileList || 0 == fileList.size()) {
        }else{
            try {
                for (int i=0;i<fileList.size();i++){
                    if(null != fileList.get(i) && 0 !=fileList.get(i).length() && fileList.get(i).exists()){
                        //设置相关文件
                        MimeBodyPart filePart = new MimeBodyPart();
                        FileDataSource dataSource = new FileDataSource(fileList.get(i));
                        DataHandler dataHandler = new DataHandler(dataSource);
                        // 文件处理
                        filePart.setDataHandler(dataHandler);
                        // 附件名称
                        filePart.setFileName(MimeUtility.encodeText(fileList.get(i).getName(), "utf-8", "B"));
                        // 放入正文（有先后顺序，所以在正文后面）
                        msgMultipart.addBodyPart(filePart);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        message.saveChanges();
        return message;
    }

    public static MimeMessage sendEmail(String email,String addresses , Session session, String title, String contentText) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("fit-bi@mail.foxconn.com"));
        try {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(addresses));
            message.setSubject(title,"UTF-8");
            MimeMultipart msgMultipart = new MimeMultipart("mixed");
            message.setContent(msgMultipart);
            MimeBodyPart content = new MimeBodyPart();
            msgMultipart.addBodyPart(content);
            // 8，设置正文格式
            MimeMultipart bodyMultipart = new MimeMultipart("related");
            content.setContent(bodyMultipart);
            // 9，设置正文内容
            MimeBodyPart htmlPart = new MimeBodyPart();
            bodyMultipart.addBodyPart(htmlPart);
            htmlPart.setContent(contentText, "text/html;charset=UTF-8");
            message.saveChanges();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 抄送
     * @param emails
     * @param title
     * @param content
     * @return
     */
    public static Boolean emailCC(String email,String emails,String title, String content) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "10.98.5.95");
            props.put("mail.smtp.port", "25");    //端口
            props.setProperty("mail.debug", "true");// 开启debug日志，日志更详细
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            Message message = sendEmail(email,emails,session,title,content);
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
