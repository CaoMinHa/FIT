package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 廠區營收收集
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Revenue_Actual_Number")
public class RevenueActualNumber extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String period;// 月
	private String factory;// 廠區
	private String BU;// BU
	private String SBU;// SBU
	private String announcementRevenue;// 廠區營收

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getPeriod() {
		return period;
	}

	@Column
	public String getFactory() {
		return factory;
	}

	@Column
	public String getBU() {
		return BU;
	}

	@Column
	public String getSBU() {
		return SBU;
	}

	@Column(name = "announcement_Revenue")
	public String getAnnouncementRevenue() {
		return announcementRevenue;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public void setBU(String bU) {
		BU = bU;
	}

	public void setSBU(String sBU) {
		SBU = sBU;
	}

	public void setAnnouncementRevenue(String announcementRevenue) {
		this.announcementRevenue = announcementRevenue;
	}

}
