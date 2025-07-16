package foxconn.fit.entity.ebs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 科目余额表
 * 
 * @author CXJ
 *
 */
@Entity
@Table(name = "CUX_SSI_BAL")
public class SSIReconciliation extends IdEntity {

	private static final long serialVersionUID = 3190252124084180360L;

	private String YEAR;// 年份
	private String PERIOD;// 月份
	private String ENTITY_CODE;// 公司编码
	private String ENTITY_NAME;// 公司名称
	private String ACCOUNT_CODE;// 科目编码
	private String ACCOUNT_NAME;// 科目名称
	private String LC;// 本币币种
	private double LC_EBAL;// 本币期末余额
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
	public String getLC() {
		return LC;
	}

	@Column
	public double getLC_EBAL() {
		return LC_EBAL;
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
	
	public void setLC(String lC) {
		LC = lC;
	}


	public void setLC_EBAL(double lC_EBAL) {
		LC_EBAL = lC_EBAL;
	}


	public void setCREATION_DATE(Date cREATION_DATE) {
		CREATION_DATE = cREATION_DATE;
	}

	public void setCREATED_BY(String cREATED_BY) {
		CREATED_BY = cREATED_BY;
	}

}
