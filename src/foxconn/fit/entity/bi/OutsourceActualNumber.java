package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 外包costdown收集
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Outsource_Actual_Number")
public class OutsourceActualNumber extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String period;// 月
	private String factory;// 廠區
	private String BU;// BU
	private String SBU;// SBU
	private String outsourceCost;// 外包成本
	private String factoryCost;// 廠內自製成本
	private String revenue;//廠區營收

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

	@Column(name = "outsource_Cost")
	public String getOutsourceCost() {
		return outsourceCost;
	}

	@Column(name = "factory_Cost")
	public String getFactoryCost() {
		return factoryCost;
	}

	@Column
	public String getRevenue() {
		return revenue;
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

	public void setOutsourceCost(String outsourceCost) {
		this.outsourceCost = outsourceCost;
	}

	public void setFactoryCost(String factoryCost) {
		this.factoryCost = factoryCost;
	}

	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}
	
}
