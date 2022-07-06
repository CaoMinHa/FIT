package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * AR余额（發票）
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_AR_Balance_Invoice")
public class ARBalanceInvoice extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String document;// 帳款編號
	private String summons;// 傳票編號
	private String invoice;// 發票號碼/銷售折讓單號
	private String invoiceDate;// 發票日期
	private String customer;// 客商代码
	private String customerName;// 客商名稱
	private String customerType;// 客戶類型
	private String itemCode;// 科目代碼
	private String itemDesc;// 科目描述
	private String currency;// 交易币别
	private String srcAmount;// 原幣含稅應收金額
	private String srcTax;// 原幣稅額
	private String srcUntax;// 原幣未稅額
	private String currencyAmount;// 本幣含稅應收金額
	private String currencyTax;// 本幣稅額
	private String currencyUntax;// 本幣未稅額
	private String exchangeRate;// 重評匯率
	private String currencyExAmount;// 本幣重評含稅應收金額
	private String currencyExTax;// 本幣重評稅額
	private String currencyExUntax;// 本幣重評未稅額
	private String docDate;// 入帳日期
	private String dueDate;// 到期日
	private String overdueDays;// 逾期天數
	private String aging;// 發票日帳齡
	private String dueAging;// 到期日賬齡
	private String department;// 部門代碼
	private String condition;// 收款條件
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
	public String getInvoice() {
		return invoice;
	}

	@Column(name = "invoice_Date")
	public String getInvoiceDate() {
		return invoiceDate;
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

	@Column(name = "item_Code")
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

	@Column(name = "src_Amount")
	public String getSrcAmount() {
		return srcAmount;
	}

	@Column(name = "src_Tax")
	public String getSrcTax() {
		return srcTax;
	}

	@Column(name = "src_Untax")
	public String getSrcUntax() {
		return srcUntax;
	}

	@Column(name = "currency_Amount")
	public String getCurrencyAmount() {
		return currencyAmount;
	}

	@Column(name = "currency_Tax")
	public String getCurrencyTax() {
		return currencyTax;
	}

	@Column(name = "currency_Untax")
	public String getCurrencyUntax() {
		return currencyUntax;
	}

	@Column(name = "exchange_Rate")
	public String getExchangeRate() {
		return exchangeRate;
	}

	@Column(name = "currency_ExAmount")
	public String getCurrencyExAmount() {
		return currencyExAmount;
	}

	@Column(name = "currency_ExTax")
	public String getCurrencyExTax() {
		return currencyExTax;
	}

	@Column(name = "currency_ExUntax")
	public String getCurrencyExUntax() {
		return currencyExUntax;
	}

	@Column(name = "doc_Date")
	public String getDocDate() {
		return docDate;
	}

	@Column(name = "due_Date")
	public String getDueDate() {
		return dueDate;
	}

	@Column(name = "overdue_Days")
	public String getOverdueDays() {
		return overdueDays;
	}

	@Column
	public String getAging() {
		return aging;
	}

	@Column(name = "due_Aging")
	public String getDueAging() {
		return dueAging;
	}

	@Column
	public String getDepartment() {
		return department;
	}

	@Column
	public String getCondition() {
		return condition;
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

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setSrcAmount(String srcAmount) {
		this.srcAmount = srcAmount;
	}

	public void setSrcTax(String srcTax) {
		this.srcTax = srcTax;
	}

	public void setSrcUntax(String srcUntax) {
		this.srcUntax = srcUntax;
	}

	public void setCurrencyAmount(String currencyAmount) {
		this.currencyAmount = currencyAmount;
	}

	public void setCurrencyTax(String currencyTax) {
		this.currencyTax = currencyTax;
	}

	public void setCurrencyUntax(String currencyUntax) {
		this.currencyUntax = currencyUntax;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public void setCurrencyExAmount(String currencyExAmount) {
		this.currencyExAmount = currencyExAmount;
	}

	public void setCurrencyExTax(String currencyExTax) {
		this.currencyExTax = currencyExTax;
	}

	public void setCurrencyExUntax(String currencyExUntax) {
		this.currencyExUntax = currencyExUntax;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public void setOverdueDays(String overdueDays) {
		this.overdueDays = overdueDays;
	}

	public void setAging(String aging) {
		this.aging = aging;
	}

	public void setDueAging(String dueAging) {
		this.dueAging = dueAging;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
