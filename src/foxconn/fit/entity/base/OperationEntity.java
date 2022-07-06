package foxconn.fit.entity.base;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 统一定义operation的entity基类.
 * 
 */
//JPA 基类的标识
@MappedSuperclass
public abstract class OperationEntity implements java.io.Serializable {

	private static final long serialVersionUID = 5142244736135944667L;

	//创建人
	protected String creator;
	
	//创建时间
	protected Date createTime;
	
	//更新人
	protected String updator;
	
	//更新时间
	protected Date updateTime;
	
	@Column(name="creator",length=50)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="updator",length=50)
	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	
}
