package foxconn.fit.entity.bi;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 用户邮箱实体
 * @create 2021-05-26 11:16
 **/
public class UserEmail {
    private String name;
    private String password;
    private String mail;

    public UserEmail() {
    }

    public UserEmail(String name, String password, String mail) {
        this.name = name;
        this.password = password;
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "UserEmail{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }

}