package foxconn.fit.entity.bi;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:15
 **/

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "FIT_PO_AUDIT_ROLE")
public class PoRole implements Serializable{

    private static final long serialVersionUID = -6340420848724099111L;

    @Id
    private String id;
    private String name;
    private String flag;
    private String deleted;
    private String createUser;
    private String createId;
    private String updateUser;
    private String updateId;
    private Date createTime;
    private Date updateTime;
    private String remark;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PoRole{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", flag='" + flag + '\'' +
                ", deleted='" + deleted + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createId='" + createId + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", updateId='" + updateId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}