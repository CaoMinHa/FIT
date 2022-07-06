package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 客戶映射維護
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_HFM_Customer_Mapping")
public class HFMCustomerMapping extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// ERP法人编码
	private String ERPCode;// ERP客戶編碼
	private String ERPDesc;// ERP客戶描述
	private String HFMCode;// HFM客戶編碼
	private String HFMDesc;// HFM客戶描述

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column(name = "ERP_Code")
	public String getERPCode() {
		return ERPCode;
	}

	@Column(name = "ERP_Desc")
	public String getERPDesc() {
		return ERPDesc;
	}

	@Column(name = "HFM_Code")
	public String getHFMCode() {
		return HFMCode;
	}

	@Column(name = "HFM_Desc")
	public String getHFMDesc() {
		return HFMDesc;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setERPCode(String eRPCode) {
		ERPCode = eRPCode;
	}

	public void setERPDesc(String eRPDesc) {
		ERPDesc = eRPDesc;
	}

	public void setHFMCode(String hFMCode) {
		HFMCode = hFMCode;
	}

	public void setHFMDesc(String hFMDesc) {
		HFMDesc = hFMDesc;
	}

}
