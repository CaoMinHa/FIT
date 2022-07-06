package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 渠道映射
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Channel")
public class Channel extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人
	private String customerCode;// 客戶代碼
	private String customerShortName;// 客戶簡稱
	private String customerName;// 客戶全名
	private String area;// 地區別
	private String country;// 國別
	private String groups;// 所屬集團
	private String classification1;// 客戶分類-1
	private String classification2;// 客戶分類-2
	private String receiptCondition;// 收款條件
	private String priceCondition;// 價格條件
	private String tradeCustomer;// 交易客戶歸類
	private String document;// 對照檔（法人+客戶代碼）
	private String channel;// 營收渠道

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

	@Column(name = "customer_Name")
	public String getCustomerName() {
		return customerName;
	}

	@Column
	public String getArea() {
		return area;
	}

	@Column
	public String getCountry() {
		return country;
	}

	@Column
	public String getGroups() {
		return groups;
	}

	@Column
	public String getClassification1() {
		return classification1;
	}

	@Column
	public String getClassification2() {
		return classification2;
	}

	@Column(name = "receipt_Condition")
	public String getReceiptCondition() {
		return receiptCondition;
	}

	@Column(name = "price_Condition")
	public String getPriceCondition() {
		return priceCondition;
	}

	@Column(name = "trade_Customer")
	public String getTradeCustomer() {
		return tradeCustomer;
	}

	@Column
	public String getDocument() {
		return document;
	}

	@Column
	public String getChannel() {
		return channel;
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

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public void setClassification1(String classification1) {
		this.classification1 = classification1;
	}

	public void setClassification2(String classification2) {
		this.classification2 = classification2;
	}

	public void setReceiptCondition(String receiptCondition) {
		this.receiptCondition = receiptCondition;
	}

	public void setPriceCondition(String priceCondition) {
		this.priceCondition = priceCondition;
	}

	public void setTradeCustomer(String tradeCustomer) {
		this.tradeCustomer = tradeCustomer;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
