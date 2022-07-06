package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * CCT關聯交易
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_CCT")
public class CCT extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String organization;// 組織
	private String item;// 項目
	private String cap;// CAP
	private String rollingForecast;// 滾動預測
	private String vsCAP;// vs CAP
	private String percent;// %
	private String yearRate;// 年度CAP佔比
	private String light;// 燈號
	private String warningInstructions;// 預警說明

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getOrganization() {
		return organization;
	}

	@Column
	public String getItem() {
		return item;
	}

	@Column
	public String getCap() {
		return cap;
	}

	@Column(name = "rolling_Forecast")
	public String getRollingForecast() {
		return rollingForecast;
	}

	@Column
	public String getVsCAP() {
		return vsCAP;
	}

	@Column
	public String getPercent() {
		return percent;
	}

	@Column(name = "year_Rate")
	public String getYearRate() {
		return yearRate;
	}

	@Column
	public String getLight() {
		return light;
	}

	@Column(name = "warning_Instructions")
	public String getWarningInstructions() {
		return warningInstructions;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public void setRollingForecast(String rollingForecast) {
		this.rollingForecast = rollingForecast;
	}

	public void setVsCAP(String vsCAP) {
		this.vsCAP = vsCAP;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public void setYearRate(String yearRate) {
		this.yearRate = yearRate;
	}

	public void setLight(String light) {
		this.light = light;
	}

	public void setWarningInstructions(String warningInstructions) {
		this.warningInstructions = warningInstructions;
	}

}
