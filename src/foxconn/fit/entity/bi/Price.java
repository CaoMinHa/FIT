package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 營收價格決定權
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Price")
public class Price extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String month;// 月
	private String customer;// 最終客戶
	private String category;// 項目
	private String decision;// 決定權
	private String revenue;// 營收
	private String rate;// 佔比(%)

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getMonth() {
		return month;
	}

	@Column
	public String getCustomer() {
		return customer;
	}

	@Column
	public String getCategory() {
		return category;
	}

	@Column
	public String getDecision() {
		return decision;
	}

	@Column
	public String getRevenue() {
		return revenue;
	}

	@Column
	public String getRate() {
		return rate;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}
