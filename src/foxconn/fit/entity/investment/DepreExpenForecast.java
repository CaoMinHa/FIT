package foxconn.fit.entity.investment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maggao
 * 折舊費用預測(在製）
 */
@Entity
@Table(name = "FIT_DEPRE_EXPEN_FORECAST")
public class DepreExpenForecast implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	/**創建人*/
	@Column(name = "create_name")
	private String createName;
	/**創建時間*/
	@Column(name = "create_date")
	private Date createDate;
	/**SBU_法人*/
	private String entity;
	/**提出部門*/
	private String department;
	/**設備類別*/
	@Column(name = "category_equipment")
	private String categoryEquipment;

	/**1月折舊費用（在製）*/
	private Double jan;
	/**2月折舊費用（在製）*/
	private Double feb;
	/**3月折舊費用（在製）*/
	private Double mar;
	/**4月折舊費用（在製）*/
	private Double apr;
	/**5月折舊費用（在製）*/
	private Double may;
	/**6月折舊費用（在製）*/
	private Double jun;
	/**7月折舊費用（在製）*/
	private Double jul;
	/**8月折舊費用（在製）*/
	private Double aug;
	/**9月折舊費用（在製）*/
	private Double sep;
	/**10月折舊費用（在製）*/
	private Double oct;
	/**11月折舊費用（在製）*/
	private Double nov;
	/**12月折舊費用（在製）*/
	private Double dec;
	private String year;
	/**版本*/
	private String version;
	/**版本創建時間*/
	@Column(name = "version_date")
	private Date versionDate;
	/**版本創建人*/
	@Column(name = "version_name")
	private String versionName;

	public DepreExpenForecast(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCategoryEquipment() {
		return categoryEquipment;
	}

	public void setCategoryEquipment(String categoryEquipment) {
		this.categoryEquipment = categoryEquipment;
	}

	public Double getJan() {
		return jan;
	}

	public void setJan(Double jan) {
		this.jan = jan;
	}

	public Double getFeb() {
		return feb;
	}

	public void setFeb(Double feb) {
		this.feb = feb;
	}

	public Double getMar() {
		return mar;
	}

	public void setMar(Double mar) {
		this.mar = mar;
	}

	public Double getApr() {
		return apr;
	}

	public void setApr(Double apr) {
		this.apr = apr;
	}

	public Double getMay() {
		return may;
	}

	public void setMay(Double may) {
		this.may = may;
	}

	public Double getJun() {
		return jun;
	}

	public void setJun(Double jun) {
		this.jun = jun;
	}

	public Double getJul() {
		return jul;
	}

	public void setJul(Double jul) {
		this.jul = jul;
	}

	public Double getAug() {
		return aug;
	}

	public void setAug(Double aug) {
		this.aug = aug;
	}

	public Double getSep() {
		return sep;
	}

	public void setSep(Double sep) {
		this.sep = sep;
	}

	public Double getOct() {
		return oct;
	}

	public void setOct(Double oct) {
		this.oct = oct;
	}

	public Double getNov() {
		return nov;
	}

	public void setNov(Double nov) {
		this.nov = nov;
	}

	public Double getDec() {
		return dec;
	}

	public void setDec(Double dec) {
		this.dec = dec;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
}
