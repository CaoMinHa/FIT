package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 用戶角色實體
 * @create 2021-05-11 15:49
 **/
@Entity
@Table(name = "FIT_PO_AUDIT_ROLE_USER")
public class PoRoleUser implements Serializable {

    private static final long serialVersionUID = -6340420848724012345L;

    @Id
    private String id;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "USER_ID")
    private String userId;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PoRoleUser{" +
                "roleId='" + roleId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}