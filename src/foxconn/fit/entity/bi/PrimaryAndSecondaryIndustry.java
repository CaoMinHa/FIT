package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 主次產業對照檔
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Primary_Secondary_Industry")
public class PrimaryAndSecondaryIndustry extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String BU;
	private String SBU;
	private String pmDirector;// PM主管(敬稱略)
	private String collectionWindow;// 收集窗口(敬稱略)
	private String product;// 產品品號
	private String customerProduct;// 客戶產品品號
	private String accountCustomerCode;// 帳款客戶（代碼）
	private String accountCustomerName;// 帳款客戶（名稱）
	private String accountCustomerGroup;// 帳款客戶（集團）
	private String document;// 對照檔（產品料號+客戶料號+客戶代碼）
	private String majorIndustry;// 主產業
	private String secondaryIndustry;// 次產業
	private String brand;// 品牌or直售
	private String brandCustomerName;// 品牌客戶名稱
	private String customerLevel;// 客戶分級分類
	private String aggregateTime;// 更新匯總時間

	@Column
	public String getBU() {
		return BU;
	}

	@Column
	public String getSBU() {
		return SBU;
	}

	@Column(name = "pm_Director")
	public String getPmDirector() {
		return pmDirector;
	}

	@Column(name = "collection_Window")
	public String getCollectionWindow() {
		return collectionWindow;
	}

	@Column
	public String getProduct() {
		return product;
	}

	@Column(name = "customer_Product")
	public String getCustomerProduct() {
		return customerProduct;
	}

	@Column(name = "account_Customer_Code")
	public String getAccountCustomerCode() {
		return accountCustomerCode;
	}

	@Column(name = "account_Customer_Name")
	public String getAccountCustomerName() {
		return accountCustomerName;
	}

	@Column(name = "account_Customer_Group")
	public String getAccountCustomerGroup() {
		return accountCustomerGroup;
	}

	@Column
	public String getDocument() {
		return document;
	}

	@Column(name = "major_Industry")
	public String getMajorIndustry() {
		return majorIndustry;
	}

	@Column(name = "secondary_Industry")
	public String getSecondaryIndustry() {
		return secondaryIndustry;
	}

	@Column
	public String getBrand() {
		return brand;
	}

	@Column(name = "brand_Customer_Name")
	public String getBrandCustomerName() {
		return brandCustomerName;
	}

	@Column(name = "customer_Level")
	public String getCustomerLevel() {
		return customerLevel;
	}

	@Column(name = "aggregate_Time")
	public String getAggregateTime() {
		return aggregateTime;
	}

	public void setBU(String bU) {
		BU = bU;
	}

	public void setSBU(String sBU) {
		SBU = sBU;
	}

	public void setPmDirector(String pmDirector) {
		this.pmDirector = pmDirector;
	}

	public void setCollectionWindow(String collectionWindow) {
		this.collectionWindow = collectionWindow;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setCustomerProduct(String customerProduct) {
		this.customerProduct = customerProduct;
	}

	public void setAccountCustomerCode(String accountCustomerCode) {
		this.accountCustomerCode = accountCustomerCode;
	}

	public void setAccountCustomerName(String accountCustomerName) {
		this.accountCustomerName = accountCustomerName;
	}

	public void setAccountCustomerGroup(String accountCustomerGroup) {
		this.accountCustomerGroup = accountCustomerGroup;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setMajorIndustry(String majorIndustry) {
		this.majorIndustry = majorIndustry;
	}

	public void setSecondaryIndustry(String secondaryIndustry) {
		this.secondaryIndustry = secondaryIndustry;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setBrandCustomerName(String brandCustomerName) {
		this.brandCustomerName = brandCustomerName;
	}

	public void setCustomerLevel(String customerLevel) {
		this.customerLevel = customerLevel;
	}

	public void setAggregateTime(String aggregateTime) {
		this.aggregateTime = aggregateTime;
	}

}
