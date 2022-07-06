package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * AR交易额（銷單）
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_AR_Trade_Sale")
public class ARTradeSale extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String document;// 帳款編號
	private String summons;// 傳票編號
	private String sale;// 銷單/出貨單
	private String category;// 項次
	private String customer;// 客商代码
	private String customerName;// 客商名稱
	private String customerType;// 客戶類型
	private String borrowItemCode;// 借方科目代碼
	private String borrowItemDesc;// 借方科目描述
	private String itemCode;// 貸方科目代碼
	private String itemDesc;// 貸方科目描述
	private String currency;// 交易币别
	private String untaxSrcAmount;// 未稅額（原幣）
	private String untaxCurrencyAmount;// 未稅額（本幣）
	private String department;// 部門代碼
	private String summary;// 摘要

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

	@Column
	public String getDocument() {
		return document;
	}

	@Column
	public String getSummons() {
		return summons;
	}

	@Column
	public String getCategory() {
		return category;
	}

	@Column
	public String getSale() {
		return sale;
	}

	@Column
	public String getCustomer() {
		return customer;
	}

	@Column(name = "customer_Name")
	public String getCustomerName() {
		return customerName;
	}

	@Column(name = "customer_Type")
	public String getCustomerType() {
		return customerType;
	}

	@Column(name = "borrow_Item_Code")
	public String getBorrowItemCode() {
		return borrowItemCode;
	}

	@Column(name = "borrow_Item_Desc")
	public String getBorrowItemDesc() {
		return borrowItemDesc;
	}
	
	@Column(name="item_Code")
	public String getItemCode() {
		return itemCode;
	}

	@Column(name = "item_Desc")
	public String getItemDesc() {
		return itemDesc;
	}

	@Column
	public String getCurrency() {
		return currency;
	}

	@Column(name = "untax_Src_Amount")
	public String getUntaxSrcAmount() {
		return untaxSrcAmount;
	}

	@Column(name = "untax_Currency_Amount")
	public String getUntaxCurrencyAmount() {
		return untaxCurrencyAmount;
	}

	@Column
	public String getDepartment() {
		return department;
	}

	@Column
	public String getSummary() {
		return summary;
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

	public void setDocument(String document) {
		this.document = document;
	}

	public void setSummons(String summons) {
		this.summons = summons;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	public void setBorrowItemCode(String borrowItemCode) {
		this.borrowItemCode = borrowItemCode;
	}

	public void setBorrowItemDesc(String borrowItemDesc) {
		this.borrowItemDesc = borrowItemDesc;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setUntaxSrcAmount(String untaxSrcAmount) {
		this.untaxSrcAmount = untaxSrcAmount;
	}

	public void setUntaxCurrencyAmount(String untaxCurrencyAmount) {
		this.untaxCurrencyAmount = untaxCurrencyAmount;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
