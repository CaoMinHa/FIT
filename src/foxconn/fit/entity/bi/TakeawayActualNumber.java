package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 外買外賣收集
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Takeaway_Actual_Number")
public class TakeawayActualNumber extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String period;// 月
	private String SBU;// SBU
	private String amount;// 外賣外賣金額

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getPeriod() {
		return period;
	}

	@Column
	public String getSBU() {
		return SBU;
	}

	@Column
	public String getAmount() {
		return amount;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setSBU(String sBU) {
		SBU = sBU;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

}
