package foxconn.fit.entity.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "FIT_USER")
public class User extends IdOperationEntity{

	private static final long serialVersionUID = -7719015548477519917L;
	
	@NotEmpty(message="用户名不能为空")
	private String username;
	private String realname;
	private String password;
	private String corporationCode;//法人编码
	private String entity;//HFM公司编码
	private String ebs;//HFM公司编码
	private String attribute;//HFM用户属性(single-单体用户,group-集团用户)
	private String newPassword;
	private EnumUserType type;
	private String menus;//菜单
	private boolean enable=true;//是否启用
	private String poCenter;//採購中心

	private String SBU;//SBU
	private String EMAIL;//邮箱
	private String commodityMajor;//物料大类 COMMODITY_MAJOR


	public String getSBU() {
		return SBU;
	}

	public void setSBU(String SBU) {
		this.SBU = SBU;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String EMAIL) {
		this.EMAIL = EMAIL;
	}
	@Column(name="commodity_major")
	public String getCommodityMajor() {
		return commodityMajor;
	}

	public void setCommodityMajor(String commodityMajor) {
		this.commodityMajor = commodityMajor;
	}

	@Column(name="username",nullable=false,unique=true,length=20)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name="realname")
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}

	@Column(name="password",nullable=false,length=100)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="corporation_code")
	public String getCorporationCode() {
		return corporationCode;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	@Column(name="po_center")
	public String getPoCenter() {
		return poCenter;
	}

	public void setPoCenter(String poCenter) {
		this.poCenter = poCenter;
	}

	@Column
	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	@Column
	public String getEbs() {
		return ebs;
	}

	public void setEbs(String ebs) {
		this.ebs = ebs;
	}

	@Column
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Transient
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="type")
	public EnumUserType getType() {
		return type;
	}

	public void setType(EnumUserType type) {
		this.type = type;
	}

	@Column
	public String getMenus() {
		return menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	@Column(name="enable")
	@Type(type="true_false")	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
