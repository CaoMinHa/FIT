package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * AP余额（驗收入庫單）
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_AP_Balance_Storage")
public class APBalanceStorage extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String document;// 帳款編號
	private String summons;// 傳票編號
	private String strikeNo;// 沖賬單號
	private String number;// 進貨驗收單/入庫單/退貨單號
	private String category;// 項次
	private String supplier;// 供應商代码
	private String supplierName;// 供應商名稱
	private String supplierType;// 供應商類型
	private String borrowItemCode;// 借方科目代碼
	private String borrowItemDesc;// 借方科目描述
	private String itemCode;// 貸方科目代碼
	private String itemDesc;// 貸方科目描述
	private String currency;// 交易币别
	private String srcUntaxAmount;// 原幣未稅應付金額
	private String currencyUntaxAmount;// 本幣未稅應付金額
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

	@Column(name = "strike_No")
	public String getStrikeNo() {
		return strikeNo;
	}

	@Column(name="no")
	public String getNumber() {
		return number;
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

	@Column(name = "src_Untax_Amount")
	public String getSrcUntaxAmount() {
		return srcUntaxAmount;
	}

	@Column(name = "currency_Untax_Amount")
	public String getCurrencyUntaxAmount() {
		return currencyUntaxAmount;
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

	public void setStrikeNo(String strikeNo) {
		this.strikeNo = strikeNo;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public void setSrcUntaxAmount(String srcUntaxAmount) {
		this.srcUntaxAmount = srcUntaxAmount;
	}

	public void setCurrencyUntaxAmount(String currencyUntaxAmount) {
		this.currencyUntaxAmount = currencyUntaxAmount;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}
