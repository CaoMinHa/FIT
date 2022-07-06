package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * AP已付款
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_AP_Payment")
public class APPayment extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String document;// 帳款編號
	private String summons;// 傳票編號
	private String supplier;// 供應商代码
	private String supplierName;// 供應商名稱
	private String supplierType;// 供應商類型
	private String borrowItemCode;// 借方科目代碼
	private String borrowItemDesc;// 借方科目描述
	private String itemCode;// 貸方科目代碼
	private String itemDesc;// 貸方科目描述
	private String currency;// 交易币别
	private String srcAmount;// 原幣含稅應付金額
	private String srcAlamount;// 原幣含稅已付金額
	private String srcUnamount;// 原幣含稅未付金額
	private String currencyAmount;// 本幣含稅應付金額
	private String currencyAlamount;// 本幣含稅已付金額
	private String currencyUnamount;// 本幣含稅未付金額
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

	@Column(name = "src_Alamount")
	public String getSrcAlamount() {
		return srcAlamount;
	}

	@Column(name = "src_Unamount")
	public String getSrcUnamount() {
		return srcUnamount;
	}

	@Column(name = "currency_Amount")
	public String getCurrencyAmount() {
		return currencyAmount;
	}

	@Column(name = "currency_Alamount")
	public String getCurrencyAlamount() {
		return currencyAlamount;
	}

	@Column(name = "currency_Unamount")
	public String getCurrencyUnamount() {
		return currencyUnamount;
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

	public void setSrcAlamount(String srcAlamount) {
		this.srcAlamount = srcAlamount;
	}

	public void setSrcUnamount(String srcUnamount) {
		this.srcUnamount = srcUnamount;
	}

	public void setCurrencyAmount(String currencyAmount) {
		this.currencyAmount = currencyAmount;
	}

	public void setCurrencyAlamount(String currencyAlamount) {
		this.currencyAlamount = currencyAlamount;
	}

	public void setCurrencyUnamount(String currencyUnamount) {
		this.currencyUnamount = currencyUnamount;
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
