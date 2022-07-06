package foxconn.fit.entity.budget;

/**
 * 營收明細实际数
 * 
 * @author liangchen
 *
 */
public class ActualDetailRevenue {

	private String entity;// SBU_法人
	private String industry;// 次產業
	private String product;// 產品料號
	private String combine;// 最終客戶
	private String customer;// 賬款客戶
	private String type;// 交易類型
	private String currency;// 交易貨幣
	private String version;// 版本
	private String scenarios;// 場景
	private String year;// 年
	private String quantity;// 銷售數量
	private String averageSalesPrice;// Average Sales Price / 平均單價
	private String revenue;// 營收

	public ActualDetailRevenue(String entity, String industry, String product,
			String combine, String customer, String type, String currency,
			String version, String scenarios, String year, String quantity,
			String averageSalesPrice, String revenue) {
		this.entity = entity;
		this.industry = industry;
		this.product = product;
		this.combine = combine;
		this.customer = customer;
		this.type = type;
		this.currency = currency;
		this.version = version;
		this.scenarios = scenarios;
		this.year = year;
		this.quantity = quantity;
		this.averageSalesPrice = averageSalesPrice;
		this.revenue = revenue;
	}

	public String getEntity() {
		return entity;
	}

	public String getIndustry() {
		return industry;
	}

	public String getProduct() {
		return product;
	}

	public String getCombine() {
		return combine;
	}

	public String getCustomer() {
		return customer;
	}

	public String getType() {
		return type;
	}

	public String getCurrency() {
		return currency;
	}

	public String getVersion() {
		return version;
	}

	public String getScenarios() {
		return scenarios;
	}

	public String getYear() {
		return year;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getAverageSalesPrice() {
		return averageSalesPrice;
	}

	public String getRevenue() {
		return revenue;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setCombine(String combine) {
		this.combine = combine;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setScenarios(String scenarios) {
		this.scenarios = scenarios;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public void setAverageSalesPrice(String averageSalesPrice) {
		this.averageSalesPrice = averageSalesPrice;
	}

	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}

}
