package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 客戶對應表
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Customer_Mapping")
public class CustomerMapping extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String customer;// RFQ&SAMPLE&CCMS_客戶
	private String customerGroup;// 對應經管客戶集團

	@Column
	public String getCustomer() {
		return customer;
	}

	@Column(name="customer_Group")
	public String getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}

}
