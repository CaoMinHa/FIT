package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 營收明細手工處理
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Revenue_Detail_Manual")
public class RevenueDetailManual extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private int serialNumber;// 序號
	private String year;// 立賬年度
	private String quarter;// 立賬季度
	private String period;// 立賬月份
	private String legalCode;// 管報法人編碼
	private String corporationCode;// 法人簡稱
	private String customerCode;// 客戶代碼
	private String customerName;// 客戶名稱
	private String department;// 業務部門
	private String category;// 類別
	private String invoiceItem;// 發票項次
	private String invoiceNo;// 發票號碼
	private String invoiceDate;// 發票日期
	private String invoiceSignDate;// 發票立賬日期
	private String saleItem;// 銷單項次
	private String saleNo;// 銷單號碼
	private String saleDate;// 銷貨日期
	private String storeNo;// 倉碼
	private String sbu;// SBU
	private String productNo;// 產品品號
	private String customerProductNo;// 客戶產品品號
	private String quantity;// 數量
	private String price;// 單價
	private String currency;// 幣別
	private String rate;// 匯率
	private String sourceUntaxAmount;// 未稅金額(原幣)
	private String currencyUntaxAmount;// 未稅金額(本幣)
	private String currentyRate;// 財報匯率(USD)
	private String monthRevenueAmount;// 營收未稅金額(USD)
	private String monthRate;// 財報匯率(NTD)
	private String monthRevenueRate;// 營收未稅金額(NTD)
	private String supplierCode;// 送貨客戶代碼
	private String supplierName;// 送貨客戶名稱
	private String productionUnit;// 生產單位
	private String cd;// CD
	private String saleCategory;// 銷售大類
	private String customerInfo;// 對照檔（產品料號+客戶料號+客戶代碼）
	private String segment;// 部門別
	private String leadingIndustry1;// 主產業1
	private String leadingIndustry2;// 主產業2
	private String leadingIndustry3;// 主產業3
	private String leadingIndustry4;// 主產業4
	private String leadingIndustry5;// 主產業5
	private String secondaryIndustry;// 次產業
	private String isUnique;// 主產業是否唯一
	private String simpleSpecification;// 客戶簡稱規範
	private String fullSpecification;// 客戶全稱規範
	private String groupSpecification;// 客戶集團規範
	private String grade;// 客戶分級分類
	private String area;// 區域
	private String channel;// 渠道
	private String BCG;// 產品BCG
	private String strategy;// 策略
	private String invoiceNumber;// 發票NO.
	private String version;// V1-管报 V2-财报

	@Column(name = "serial_Number")
	public int getSerialNumber() {
		return serialNumber;
	}

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getQuarter() {
		return quarter;
	}

	@Column
	public String getPeriod() {
		return period;
	}

	@Column(name = "legal_Code")
	public String getLegalCode() {
		return legalCode;
	}

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column(name = "customer_Code")
	public String getCustomerCode() {
		return customerCode;
	}

	@Column(name = "customer_Name")
	public String getCustomerName() {
		return customerName;
	}

	@Column
	public String getDepartment() {
		return department;
	}

	@Column
	public String getCategory() {
		return category;
	}

	@Column(name = "invoice_Item")
	public String getInvoiceItem() {
		return invoiceItem;
	}

	@Column(name = "invoice_No")
	public String getInvoiceNo() {
		return invoiceNo;
	}

	@Column(name = "invoice_Date")
	public String getInvoiceDate() {
		return invoiceDate;
	}

	@Column(name = "invoice_Sign_Date")
	public String getInvoiceSignDate() {
		return invoiceSignDate;
	}

	@Column(name = "sale_Item")
	public String getSaleItem() {
		return saleItem;
	}

	@Column(name = "sale_No")
	public String getSaleNo() {
		return saleNo;
	}

	@Column(name = "sale_Date")
	public String getSaleDate() {
		return saleDate;
	}

	@Column(name = "store_No")
	public String getStoreNo() {
		return storeNo;
	}

	@Column
	public String getSbu() {
		return sbu;
	}

	@Column(name = "product_No")
	public String getProductNo() {
		return productNo;
	}

	@Column(name = "customer_Product_No")
	public String getCustomerProductNo() {
		return customerProductNo;
	}

	@Column
	public String getQuantity() {
		return quantity;
	}

	@Column
	public String getPrice() {
		return price;
	}

	@Column
	public String getCurrency() {
		return currency;
	}

	@Column
	public String getRate() {
		return rate;
	}

	@Column(name = "source_Untax_Amount")
	public String getSourceUntaxAmount() {
		return sourceUntaxAmount;
	}

	@Column(name = "currency_Untax_Amount")
	public String getCurrencyUntaxAmount() {
		return currencyUntaxAmount;
	}

	@Column(name = "currenty_Rate")
	public String getCurrentyRate() {
		return currentyRate;
	}

	@Column(name = "month_Revenue_Amount")
	public String getMonthRevenueAmount() {
		return monthRevenueAmount;
	}

	@Column(name = "month_Rate")
	public String getMonthRate() {
		return monthRate;
	}

	@Column(name = "month_Revenue_Rate")
	public String getMonthRevenueRate() {
		return monthRevenueRate;
	}

	@Column(name = "supplier_Code")
	public String getSupplierCode() {
		return supplierCode;
	}

	@Column(name = "supplier_Name")
	public String getSupplierName() {
		return supplierName;
	}

	@Column(name = "production_Unit")
	public String getProductionUnit() {
		return productionUnit;
	}

	@Column
	public String getCd() {
		return cd;
	}

	@Column(name = "sale_Category")
	public String getSaleCategory() {
		return saleCategory;
	}

	@Column(name = "customer_Info")
	public String getCustomerInfo() {
		return customerInfo;
	}

	@Column
	public String getSegment() {
		return segment;
	}

	@Column(name = "leading_Industry1")
	public String getLeadingIndustry1() {
		return leadingIndustry1;
	}

	@Column(name = "leading_Industry2")
	public String getLeadingIndustry2() {
		return leadingIndustry2;
	}

	@Column(name = "leading_Industry3")
	public String getLeadingIndustry3() {
		return leadingIndustry3;
	}

	@Column(name = "leading_Industry4")
	public String getLeadingIndustry4() {
		return leadingIndustry4;
	}

	@Column(name = "leading_Industry5")
	public String getLeadingIndustry5() {
		return leadingIndustry5;
	}

	@Column(name = "secondary_Industry")
	public String getSecondaryIndustry() {
		return secondaryIndustry;
	}

	@Column(name = "is_Unique")
	public String getIsUnique() {
		return isUnique;
	}

	@Column(name = "simple_Specification")
	public String getSimpleSpecification() {
		return simpleSpecification;
	}

	@Column(name = "full_Specification")
	public String getFullSpecification() {
		return fullSpecification;
	}

	@Column(name = "group_Specification")
	public String getGroupSpecification() {
		return groupSpecification;
	}

	@Column
	public String getGrade() {
		return grade;
	}

	@Column
	public String getArea() {
		return area;
	}

	@Column
	public String getChannel() {
		return channel;
	}

	@Column
	public String getBCG() {
		return BCG;
	}

	@Column
	public String getStrategy() {
		return strategy;
	}

	@Column(name = "invoice_Number")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	@Column
	public String getVersion() {
		return version;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setLegalCode(String legalCode) {
		this.legalCode = legalCode;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setInvoiceItem(String invoiceItem) {
		this.invoiceItem = invoiceItem;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public void setInvoiceSignDate(String invoiceSignDate) {
		this.invoiceSignDate = invoiceSignDate;
	}

	public void setSaleItem(String saleItem) {
		this.saleItem = saleItem;
	}

	public void setSaleNo(String saleNo) {
		this.saleNo = saleNo;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}

	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}

	public void setSbu(String sbu) {
		this.sbu = sbu;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public void setCustomerProductNo(String customerProductNo) {
		this.customerProductNo = customerProductNo;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public void setSourceUntaxAmount(String sourceUntaxAmount) {
		this.sourceUntaxAmount = sourceUntaxAmount;
	}

	public void setCurrencyUntaxAmount(String currencyUntaxAmount) {
		this.currencyUntaxAmount = currencyUntaxAmount;
	}

	public void setCurrentyRate(String currentyRate) {
		this.currentyRate = currentyRate;
	}

	public void setMonthRevenueAmount(String monthRevenueAmount) {
		this.monthRevenueAmount = monthRevenueAmount;
	}

	public void setMonthRate(String monthRate) {
		this.monthRate = monthRate;
	}

	public void setMonthRevenueRate(String monthRevenueRate) {
		this.monthRevenueRate = monthRevenueRate;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public void setProductionUnit(String productionUnit) {
		this.productionUnit = productionUnit;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public void setSaleCategory(String saleCategory) {
		this.saleCategory = saleCategory;
	}

	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public void setLeadingIndustry1(String leadingIndustry1) {
		this.leadingIndustry1 = leadingIndustry1;
	}

	public void setLeadingIndustry2(String leadingIndustry2) {
		this.leadingIndustry2 = leadingIndustry2;
	}

	public void setLeadingIndustry3(String leadingIndustry3) {
		this.leadingIndustry3 = leadingIndustry3;
	}

	public void setLeadingIndustry4(String leadingIndustry4) {
		this.leadingIndustry4 = leadingIndustry4;
	}

	public void setLeadingIndustry5(String leadingIndustry5) {
		this.leadingIndustry5 = leadingIndustry5;
	}

	public void setSecondaryIndustry(String secondaryIndustry) {
		this.secondaryIndustry = secondaryIndustry;
	}

	public void setIsUnique(String isUnique) {
		this.isUnique = isUnique;
	}

	public void setSimpleSpecification(String simpleSpecification) {
		this.simpleSpecification = simpleSpecification;
	}

	public void setFullSpecification(String fullSpecification) {
		this.fullSpecification = fullSpecification;
	}

	public void setGroupSpecification(String groupSpecification) {
		this.groupSpecification = groupSpecification;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setBCG(String bCG) {
		BCG = bCG;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
