package foxconn.fit.entity.ebs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 科目余额表
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "CUX_BAL")
public class Reconciliation extends IdEntity {

	private static final long serialVersionUID = 3190252124084180360L;

	private String YEAR;// 年份
	private String PERIOD;// 月份
	private String ENTITY_CODE;// 公司编码
	private String ENTITY_NAME;// 公司名称
	private String ACCOUNT_CODE;// 科目编码
	private String ACCOUNT_NAME;// 科目名称
	private String CUST_CODE;// 客商编码
	private String CUST_NAME;// 客商名称
	private String ISICP;// 是否关联方
	private String SOURSYS;// 数据来源
	private String LC;// 本币币种
	private double LC_BBAL;// 本币期初余额
	private double LC_DR;// 本币借方累计
	private double LC_CR;// 本币贷方累计
	private double LC_EBAL;// 本币期末余额
	private String TC;// 交易币种
	private double TC_BBAL;// 交易币期初余额
	private double TC_DR;// 交易币借方累计
	private double TC_CR;// 交易币贷方累计
	private double TC_EBAL;// 交易币期末余额
	private Date CREATION_DATE;// 创建日期
	private String CREATED_BY;// 创建用户

	@Column
	public String getYEAR() {
		return YEAR;
	}

	@Column
	public String getPERIOD() {
		return PERIOD;
	}

	@Column
	public String getENTITY_CODE() {
		return ENTITY_CODE;
	}

	@Column
	public String getENTITY_NAME() {
		return ENTITY_NAME;
	}

	@Column
	public String getACCOUNT_CODE() {
		return ACCOUNT_CODE;
	}

	@Column
	public String getACCOUNT_NAME() {
		return ACCOUNT_NAME;
	}

	@Column
	public String getCUST_CODE() {
		return CUST_CODE;
	}

	@Column
	public String getCUST_NAME() {
		return CUST_NAME;
	}

	@Column
	public String getISICP() {
		return ISICP;
	}

	@Column
	public String getSOURSYS() {
		return SOURSYS;
	}

	@Column
	public String getLC() {
		return LC;
	}

	@Column
	public double getLC_BBAL() {
		return LC_BBAL;
	}

	@Column
	public double getLC_DR() {
		return LC_DR;
	}

	@Column
	public double getLC_CR() {
		return LC_CR;
	}

	@Column
	public double getLC_EBAL() {
		return LC_EBAL;
	}

	@Column
	public String getTC() {
		return TC;
	}

	@Column
	public double getTC_BBAL() {
		return TC_BBAL;
	}

	@Column
	public double getTC_DR() {
		return TC_DR;
	}

	@Column
	public double getTC_CR() {
		return TC_CR;
	}

	@Column
	public double getTC_EBAL() {
		return TC_EBAL;
	}

	@Column
	public Date getCREATION_DATE() {
		return CREATION_DATE;
	}

	@Column
	public String getCREATED_BY() {
		return CREATED_BY;
	}

	public void setYEAR(String yEAR) {
		YEAR = yEAR;
	}

	public void setPERIOD(String pERIOD) {
		PERIOD = pERIOD;
	}

	public void setENTITY_CODE(String eNTITY_CODE) {
		ENTITY_CODE = eNTITY_CODE;
	}

	public void setENTITY_NAME(String eNTITY_NAME) {
		ENTITY_NAME = eNTITY_NAME;
	}

	public void setACCOUNT_CODE(String aCCOUNT_CODE) {
		ACCOUNT_CODE = aCCOUNT_CODE;
	}

	public void setACCOUNT_NAME(String aCCOUNT_NAME) {
		ACCOUNT_NAME = aCCOUNT_NAME;
	}

	public void setCUST_CODE(String cUST_CODE) {
		CUST_CODE = cUST_CODE;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public void setISICP(String iSICP) {
		ISICP = iSICP;
	}

	public void setSOURSYS(String sOURSYS) {
		SOURSYS = sOURSYS;
	}

	public void setLC(String lC) {
		LC = lC;
	}

	public void setLC_BBAL(double lC_BBAL) {
		LC_BBAL = lC_BBAL;
	}

	public void setLC_DR(double lC_DR) {
		LC_DR = lC_DR;
	}

	public void setLC_CR(double lC_CR) {
		LC_CR = lC_CR;
	}

	public void setLC_EBAL(double lC_EBAL) {
		LC_EBAL = lC_EBAL;
	}

	public void setTC(String tC) {
		TC = tC;
	}

	public void setTC_BBAL(double tC_BBAL) {
		TC_BBAL = tC_BBAL;
	}

	public void setTC_DR(double tC_DR) {
		TC_DR = tC_DR;
	}

	public void setTC_CR(double tC_CR) {
		TC_CR = tC_CR;
	}

	public void setTC_EBAL(double tC_EBAL) {
		TC_EBAL = tC_EBAL;
	}

	public void setCREATION_DATE(Date cREATION_DATE) {
		CREATION_DATE = cREATION_DATE;
	}

	public void setCREATED_BY(String cREATED_BY) {
		CREATED_BY = cREATED_BY;
	}

}
