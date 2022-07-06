package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 銷售大類映射
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Sale_Classification")
public class SaleClassification extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String customerCode;// 客戶代碼
	private String customerName;// 客戶名稱
	private String classification;// 銷售大類

	@Column(name = "customer_Code")
	public String getCustomerCode() {
		return customerCode;
	}

	@Column(name = "customer_Name")
	public String getCustomerName() {
		return customerName;
	}

	@Column
	public String getClassification() {
		return classification;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

}
