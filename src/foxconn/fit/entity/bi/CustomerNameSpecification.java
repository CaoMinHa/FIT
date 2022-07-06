package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 客戶名稱規範映射
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Customer_Name_Specify")
public class CustomerNameSpecification extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人
	private String customerCode;// 客戶代碼
	private String customerShortName;// 客戶簡稱
	private String specificationShortName;// 客戶簡稱（規范）
	private String specificationFullName;// 客戶全稱（規范）
	private String specificationGroupName;// 客戶集團（規范）

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column(name = "customer_Code")
	public String getCustomerCode() {
		return customerCode;
	}

	@Column(name = "customer_Short_Name")
	public String getCustomerShortName() {
		return customerShortName;
	}

	@Column(name = "specification_Short_Name")
	public String getSpecificationShortName() {
		return specificationShortName;
	}

	@Column(name = "specification_Full_Name")
	public String getSpecificationFullName() {
		return specificationFullName;
	}

	@Column(name = "specification_Group_Name")
	public String getSpecificationGroupName() {
		return specificationGroupName;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public void setCustomerShortName(String customerShortName) {
		this.customerShortName = customerShortName;
	}

	public void setSpecificationShortName(String specificationShortName) {
		this.specificationShortName = specificationShortName;
	}

	public void setSpecificationFullName(String specificationFullName) {
		this.specificationFullName = specificationFullName;
	}

	public void setSpecificationGroupName(String specificationGroupName) {
		this.specificationGroupName = specificationGroupName;
	}

}
