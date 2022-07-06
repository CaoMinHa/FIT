package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 營收明細實際數
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Revenue_Detail_Actual")
public class RevenueDetailActualNumber extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 管報法人編碼
	private String year;// 立賬年度
	private String period;// 立賬月份
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
	private String productNo;// 產品品號
	private String customerProductNo;// 客戶產品品號
	private String quantity;// 數量
	private String price;// 單價
	private String currency;// 幣別
	private String rate;// 匯率
	private String sourceUntaxAmount;// 未稅金額(原幣)
	private String currencyUntaxAmount;// 未稅金額(本幣)
	private String supplierCode;// 送貨客戶代碼
	private String supplierName;// 送貨客戶名稱
	private String productionUnit;// 生產單位
	private String cd;// CD
	private String invoiceNumber;// 發票NO.
	private String version;// V1-管报 V2-财报

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getPeriod() {
		return period;
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

	@Column(name = "invoice_Number")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	@Column
	public String getVersion() {
		return version;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
