package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * AP余额（發票）
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_AP_Balance_Invoice")
public class APBalanceInvoice extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String document;// 帳款編號
	private String summons;// 傳票編號
	private String invoice;// 發票號碼
	private String supplier;// 供應商代码
	private String supplierName;// 供應商名稱
	private String supplierType;// 供應商類型
	private String borrowItemCode;// 借方科目代碼
	private String borrowItemDesc;// 借方科目描述
	private String itemCode;// 貸方科目代碼
	private String itemDesc;// 貸方科目描述
	private String currency;// 交易币别
	private String srcAmount;// 原幣含稅應付金額
	private String srcTax;// 原幣稅額
	private String srcUntax;// 原幣未稅額
	private String currencyAmount;// 本幣含稅應付金額
	private String currencyTax;// 本幣稅額
	private String currencyUntax;// 本幣未稅額
	private String exchangeRate;// 重評匯率
	private String currencyExAmount;// 本幣重評含稅應付金額
	private String currencyExTax;// 重評稅額
	private String currencyExUntax;// 重評未稅額
	private String docDate;// 入帳日期
	private String dueDate;// 到期日
	private String aging;// 帳齡
	private String department;// 部門代碼
	private String condition;// 付款條件
	private String summary;// 摘要
	private String note;// 备注

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

	@Column
	public String getSupplier() {
		return supplier;
	}

	@Column(name = "supplier_Name")
	public String getSupplierName() {
		return supplierName;
	}

	@Column(name = "supplier_Type")
	public String getSupplierType() {
		return supplierType;
	}
	
	@Column(name = "borrow_Item_Code")
	public String getBorrowItemCode() {
		return borrowItemCode;
	}

	@Column(name = "borrow_Item_Desc")
	public String getBorrowItemDesc() {
		return borrowItemDesc;
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

	@Column
	public String getAging() {
		return aging;
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

	@Column
	public String getNote() {
		return note;
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

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
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

	public void setAging(String aging) {
		this.aging = aging;
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

	public void setNote(String note) {
		this.note = note;
	}

}
