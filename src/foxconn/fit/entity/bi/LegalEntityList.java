package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 管報法人列表
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Legal_Entity_List")
public class LegalEntityList extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 管報法人編碼
	private String legalFullName;// 法人全稱
	private String legalName;// 法人簡稱
	private String currency;// 本位幣
	private String system;// 體系
	private String erpCode;// 對應ERP編碼

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column(name = "legal_Full_Name")
	public String getLegalFullName() {
		return legalFullName;
	}

	@Column(name = "legal_Name")
	public String getLegalName() {
		return legalName;
	}

	@Column
	public String getCurrency() {
		return currency;
	}

	@Column(name = "system")
	public String getSystem() {
		return system;
	}

	@Column(name = "erp_Code")
	public String getErpCode() {
		return erpCode;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setLegalFullName(String legalFullName) {
		this.legalFullName = legalFullName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public void setErpCode(String erpCode) {
		this.erpCode = erpCode;
	}

}
