package foxconn.fit.entity.bi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description 审核任务实体
 * @create 2021-05-14 11:34
 **/
@Entity
@Table(name = "FIT_PO_TASK")
public class PoTask implements Serializable {
    private static final long serialVersionUID = -6340420848724011111L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String type;
    private String name;
    private String flag;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
    private String sbu;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSbu() {
        return sbu;
    }

    public void setSbu(String sbu) {
        this.sbu = sbu;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PoTask{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", flag='" + flag + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", sbu='" + sbu + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}