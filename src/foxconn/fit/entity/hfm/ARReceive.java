package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * AR已收款
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_AR_Receive")
public class ARReceive extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String document;// 帳款編號
	private String summons;// 傳票編號
	private String customer;// 客商代码
	private String customerName;// 客商名稱
	private String customerType;// 客戶類型
	private String itemCode;// 科目代碼
	private String itemDesc;// 科目描述
	private String currency;// 交易币别
	private String srcAmount;// 原幣含稅應收金額
	private String srcAlamount;// 原幣含稅已收金額
	private String srcUnamount;// 原幣含稅未收金額
	private String currencyAmount;// 本幣含稅應收金額
	private String currencyAlamount;// 本幣含稅已收金額
	private String currencyUnamount;// 本幣含稅未收金額

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

}
