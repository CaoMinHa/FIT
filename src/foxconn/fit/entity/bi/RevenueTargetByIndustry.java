package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 營收目標by產業
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Revenue_Target_Industry")
public class RevenueTargetByIndustry extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String version;// 版本
	private String organization;// 組織
	private String system;// 體系
	private String scene;// 情景
	private String industry;// 主產業
	private String month1;// 1月
	private String month2;// 2月
	private String month3;// 3月
	private String month4;// 4月
	private String month5;// 5月
	private String month6;// 6月
	private String month7;// 7月
	private String month8;// 8月
	private String month9;// 9月
	private String month10;// 10月
	private String month11;// 11月
	private String month12;// 12月

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getVersion() {
		return version;
	}

	@Column
	public String getOrganization() {
		return organization;
	}

	@Column
	public String getSystem() {
		return system;
	}

	@Column
	public String getScene() {
		return scene;
	}

	@Column
	public String getIndustry() {
		return industry;
	}

	@Column
	public String getMonth1() {
		return month1;
	}

	@Column
	public String getMonth2() {
		return month2;
	}

	@Column
	public String getMonth3() {
		return month3;
	}

	@Column
	public String getMonth4() {
		return month4;
	}

	@Column
	public String getMonth5() {
		return month5;
	}

	@Column
	public String getMonth6() {
		return month6;
	}

	@Column
	public String getMonth7() {
		return month7;
	}

	@Column
	public String getMonth8() {
		return month8;
	}

	@Column
	public String getMonth9() {
		return month9;
	}

	@Column
	public String getMonth10() {
		return month10;
	}

	@Column
	public String getMonth11() {
		return month11;
	}

	@Column
	public String getMonth12() {
		return month12;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public void setMonth1(String month1) {
		this.month1 = month1;
	}

	public void setMonth2(String month2) {
		this.month2 = month2;
	}

	public void setMonth3(String month3) {
		this.month3 = month3;
	}

	public void setMonth4(String month4) {
		this.month4 = month4;
	}

	public void setMonth5(String month5) {
		this.month5 = month5;
	}

	public void setMonth6(String month6) {
		this.month6 = month6;
	}

	public void setMonth7(String month7) {
		this.month7 = month7;
	}

	public void setMonth8(String month8) {
		this.month8 = month8;
	}

	public void setMonth9(String month9) {
		this.month9 = month9;
	}

	public void setMonth10(String month10) {
		this.month10 = month10;
	}

	public void setMonth11(String month11) {
		this.month11 = month11;
	}

	public void setMonth12(String month12) {
		this.month12 = month12;
	}

}
