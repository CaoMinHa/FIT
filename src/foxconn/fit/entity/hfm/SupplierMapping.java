package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 供應商映射維護
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Supplier_Mapping")
public class SupplierMapping extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// ERP法人编码
	private String supplierCode;// ERP供應商編碼
	private String supplierDesc;// ERP供應商描述
	private String customerCode;// HFM客戶編碼
	private String customerDesc;// HFM客戶描述

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column(name = "supplier_Code")
	public String getSupplierCode() {
		return supplierCode;
	}

	@Column(name = "supplier_Desc")
	public String getSupplierDesc() {
		return supplierDesc;
	}

	@Column(name = "customer_Code")
	public String getCustomerCode() {
		return customerCode;
	}

	@Column(name = "customer_Desc")
	public String getCustomerDesc() {
		return customerDesc;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public void setSupplierDesc(String supplierDesc) {
		this.supplierDesc = supplierDesc;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public void setCustomerDesc(String customerDesc) {
		this.customerDesc = customerDesc;
	}

}
