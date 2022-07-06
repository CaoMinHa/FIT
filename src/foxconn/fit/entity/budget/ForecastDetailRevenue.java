package foxconn.fit.entity.budget;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 營收明細
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_FORECAST_DETAIL_REVENUE")
public class ForecastDetailRevenue extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String entity;// SBU_法人
	private String makeEntity;// SBU_法人
	private String industry;// 次產業
	private String product;// 產品料號
	private String combine;// 最終客戶
	private String customer;// 賬款客戶
	private String type;// 交易類型
	private String currency;// 交易貨幣
	private String activity;// 戰術
	private String version;// 版本
	private String scenarios;// 場景
	private String year;// 年
	private String industryDemandTrend;// Industry Demand Trend
	private String industryDemandTrendServed;// Industry Demand Trend-Served
	private String componentUsage;// Component Usage/ 用量
	private String averageSalesPrice;// Average Sales Price / 平均單價
	private String totalAvailableMarket;// Total Available Market / TAM
	private String servedAvailableMarket;// Served Available Market / SAM
	private String allocation;// Allocation
	private String revenue;// 營收
	private String quantity;// 銷售數量
	private String quantityMonth1;// 銷售數量1月
	private String quantityMonth2;// 銷售數量2月
	private String quantityMonth3;// 銷售數量3月
	private String quantityMonth4;// 銷售數量4月
	private String quantityMonth5;// 銷售數量5月
	private String quantityMonth6;// 銷售數量6月
	private String quantityMonth7;// 銷售數量7月
	private String quantityMonth8;// 銷售數量8月
	private String quantityMonth9;// 銷售數量9月
	private String quantityMonth10;// 銷售數量10月
	private String quantityMonth11;// 銷售數量11月
	private String quantityMonth12;// 銷售數量12月
	private String priceMonth1;// 单价1月
	private String priceMonth2;// 单价2月
	private String priceMonth3;// 单价3月
	private String priceMonth4;// 单价4月
	private String priceMonth5;// 单价5月
	private String priceMonth6;// 单价6月
	private String priceMonth7;// 单价7月
	private String priceMonth8;// 单价8月
	private String priceMonth9;// 单价9月
	private String priceMonth10;// 单价10月
	private String priceMonth11;// 单价11月
	private String priceMonth12;// 单价12月
	private String revenueMonth1;// 營收1月
	private String revenueMonth2;// 營收2月
	private String revenueMonth3;// 營收3月
	private String revenueMonth4;// 營收4月
	private String revenueMonth5;// 營收5月
	private String revenueMonth6;// 營收6月
	private String revenueMonth7;// 營收7月
	private String revenueMonth8;// 營收8月
	private String revenueMonth9;// 營收9月
	private String revenueMonth10;// 營收10月
	private String revenueMonth11;// 營收11月
	private String revenueMonth12;// 營收12月
	private int forecastId;//行ID

	@Column(name = "forecast_Id")
	public int getForecastId() {
		return forecastId;
	}

	public void setForecastId(int forecastId) {
		this.forecastId = forecastId;
	}

	public ForecastDetailRevenue() {
	}

	public ForecastDetailRevenue(String entity,String makeEntity, String industry,
			String product, String combine, String customer, String type,
			String currency, String activity, String version, String scenarios,
			String year, String industryDemandTrend,
			String industryDemandTrendServed, String componentUsage,
			String averageSalesPrice, String totalAvailableMarket,
			String servedAvailableMarket, String allocation, String revenue,
			String quantity, String quantityMonth1, String quantityMonth2,
			String quantityMonth3, String quantityMonth4,
			String quantityMonth5, String quantityMonth6,
			String quantityMonth7, String quantityMonth8,
			String quantityMonth9, String quantityMonth10,
			String quantityMonth11, String quantityMonth12, String priceMonth1,
			String priceMonth2, String priceMonth3, String priceMonth4,
			String priceMonth5, String priceMonth6, String priceMonth7,
			String priceMonth8, String priceMonth9, String priceMonth10,
			String priceMonth11, String priceMonth12, String revenueMonth1,
			String revenueMonth2, String revenueMonth3, String revenueMonth4,
			String revenueMonth5, String revenueMonth6, String revenueMonth7,
			String revenueMonth8, String revenueMonth9, String revenueMonth10,
			String revenueMonth11, String revenueMonth12,
			int forecastId) {
		this.entity = entity;
		this.makeEntity = makeEntity;
		this.industry = industry;
		this.product = product;
		this.combine = combine;
		this.customer = customer;
		this.type = type;
		this.currency = currency;
		this.activity = activity;
		this.version = version;
		this.scenarios = scenarios;
		this.year = year;
		this.industryDemandTrend = industryDemandTrend;
		this.industryDemandTrendServed = industryDemandTrendServed;
		this.componentUsage = componentUsage;
		this.averageSalesPrice = averageSalesPrice;
		this.totalAvailableMarket = totalAvailableMarket;
		this.servedAvailableMarket = servedAvailableMarket;
		this.allocation = allocation;
		this.revenue = revenue;
		this.quantity = quantity;
		this.quantityMonth1 = quantityMonth1;
		this.quantityMonth2 = quantityMonth2;
		this.quantityMonth3 = quantityMonth3;
		this.quantityMonth4 = quantityMonth4;
		this.quantityMonth5 = quantityMonth5;
		this.quantityMonth6 = quantityMonth6;
		this.quantityMonth7 = quantityMonth7;
		this.quantityMonth8 = quantityMonth8;
		this.quantityMonth9 = quantityMonth9;
		this.quantityMonth10 = quantityMonth10;
		this.quantityMonth11 = quantityMonth11;
		this.quantityMonth12 = quantityMonth12;
		this.priceMonth1 = priceMonth1;
		this.priceMonth2 = priceMonth2;
		this.priceMonth3 = priceMonth3;
		this.priceMonth4 = priceMonth4;
		this.priceMonth5 = priceMonth5;
		this.priceMonth6 = priceMonth6;
		this.priceMonth7 = priceMonth7;
		this.priceMonth8 = priceMonth8;
		this.priceMonth9 = priceMonth9;
		this.priceMonth10 = priceMonth10;
		this.priceMonth11 = priceMonth11;
		this.priceMonth12 = priceMonth12;
		this.revenueMonth1 = revenueMonth1;
		this.revenueMonth2 = revenueMonth2;
		this.revenueMonth3 = revenueMonth3;
		this.revenueMonth4 = revenueMonth4;
		this.revenueMonth5 = revenueMonth5;
		this.revenueMonth6 = revenueMonth6;
		this.revenueMonth7 = revenueMonth7;
		this.revenueMonth8 = revenueMonth8;
		this.revenueMonth9 = revenueMonth9;
		this.revenueMonth10 = revenueMonth10;
		this.revenueMonth11 = revenueMonth11;
		this.revenueMonth12 = revenueMonth12;
		this.forecastId = forecastId;
	}

	public ForecastDetailRevenue(String entity,String makeEntity, String industry,
								 String product, String combine, String customer, String type,
								 String currency, String activity, String version, String scenarios,
								 String year, String industryDemandTrend,
								 String industryDemandTrendServed, String componentUsage,
								 String averageSalesPrice, String totalAvailableMarket,
								 String servedAvailableMarket, String allocation, String revenue,
								 String quantity,int forecastId) {
		this.entity = entity;
		this.makeEntity = makeEntity;
		this.industry = industry;
		this.product = product;
		this.combine = combine;
		this.customer = customer;
		this.type = type;
		this.currency = currency;
		this.activity = activity;
		this.version = version;
		this.scenarios = scenarios;
		this.year = year;
		this.industryDemandTrend = industryDemandTrend;
		this.industryDemandTrendServed = industryDemandTrendServed;
		this.componentUsage = componentUsage;
		this.averageSalesPrice = averageSalesPrice;
		this.totalAvailableMarket = totalAvailableMarket;
		this.servedAvailableMarket = servedAvailableMarket;
		this.allocation = allocation;
		this.revenue = revenue;
		this.quantity = quantity;
		this.forecastId = forecastId;
	}

	@Column
	public String getEntity() {
		return entity;
	}

	@Column(name = "make_entity")
	public String getMakeEntity() {
		return makeEntity;
	}

	@Column
	public String getIndustry() {
		return industry;
	}

	@Column
	public String getProduct() {
		return product;
	}

	@Column
	public String getCombine() {
		return combine;
	}

	@Column
	public String getCustomer() {
		return customer;
	}

	@Column
	public String getType() {
		return type;
	}

	@Column
	public String getCurrency() {
		return currency;
	}

	@Column
	public String getActivity() {
		return activity;
	}

	@Column
	public String getVersion() {
		return version;
	}

	@Column
	public String getScenarios() {
		return scenarios;
	}

	@Column
	public String getYear() {
		return year;
	}

	@Column(name = "industry_Demand_Trend")
	public String getIndustryDemandTrend() {
		return industryDemandTrend;
	}

	@Column(name = "industry_Demand_Trend_Served")
	public String getIndustryDemandTrendServed() {
		return industryDemandTrendServed;
	}

	@Column(name = "component_Usage")
	public String getComponentUsage() {
		return componentUsage;
	}

	@Column(name = "average_Sales_Price")
	public String getAverageSalesPrice() {
		return averageSalesPrice;
	}

	@Column(name = "total_Available_Market")
	public String getTotalAvailableMarket() {
		return totalAvailableMarket;
	}

	@Column(name = "served_Available_Market")
	public String getServedAvailableMarket() {
		return servedAvailableMarket;
	}

	@Column
	public String getAllocation() {
		return allocation;
	}

	@Column
	public String getRevenue() {
		return revenue;
	}

	@Column
	public String getQuantity() {
		return quantity;
	}

	@Column(name = "quantity_Month1")
	public String getQuantityMonth1() {
		return quantityMonth1;
	}

	@Column(name = "quantity_Month2")
	public String getQuantityMonth2() {
		return quantityMonth2;
	}

	@Column(name = "quantity_Month3")
	public String getQuantityMonth3() {
		return quantityMonth3;
	}

	@Column(name = "quantity_Month4")
	public String getQuantityMonth4() {
		return quantityMonth4;
	}

	@Column(name = "quantity_Month5")
	public String getQuantityMonth5() {
		return quantityMonth5;
	}

	@Column(name = "quantity_Month6")
	public String getQuantityMonth6() {
		return quantityMonth6;
	}

	@Column(name = "quantity_Month7")
	public String getQuantityMonth7() {
		return quantityMonth7;
	}

	@Column(name = "quantity_Month8")
	public String getQuantityMonth8() {
		return quantityMonth8;
	}

	@Column(name = "quantity_Month9")
	public String getQuantityMonth9() {
		return quantityMonth9;
	}

	@Column(name = "quantity_Month10")
	public String getQuantityMonth10() {
		return quantityMonth10;
	}

	@Column(name = "quantity_Month11")
	public String getQuantityMonth11() {
		return quantityMonth11;
	}

	@Column(name = "quantity_Month12")
	public String getQuantityMonth12() {
		return quantityMonth12;
	}

	@Column(name = "price_Month1")
	public String getPriceMonth1() {
		return priceMonth1;
	}

	@Column(name = "price_Month2")
	public String getPriceMonth2() {
		return priceMonth2;
	}

	@Column(name = "price_Month3")
	public String getPriceMonth3() {
		return priceMonth3;
	}

	@Column(name = "price_Month4")
	public String getPriceMonth4() {
		return priceMonth4;
	}

	@Column(name = "price_Month5")
	public String getPriceMonth5() {
		return priceMonth5;
	}

	@Column(name = "price_Month6")
	public String getPriceMonth6() {
		return priceMonth6;
	}

	@Column(name = "price_Month7")
	public String getPriceMonth7() {
		return priceMonth7;
	}

	@Column(name = "price_Month8")
	public String getPriceMonth8() {
		return priceMonth8;
	}

	@Column(name = "price_Month9")
	public String getPriceMonth9() {
		return priceMonth9;
	}

	@Column(name = "price_Month10")
	public String getPriceMonth10() {
		return priceMonth10;
	}

	@Column(name = "price_Month11")
	public String getPriceMonth11() {
		return priceMonth11;
	}

	@Column(name = "price_Month12")
	public String getPriceMonth12() {
		return priceMonth12;
	}

	@Column(name = "revenue_Month1")
	public String getRevenueMonth1() {
		return revenueMonth1;
	}

	@Column(name = "revenue_Month2")
	public String getRevenueMonth2() {
		return revenueMonth2;
	}

	@Column(name = "revenue_Month3")
	public String getRevenueMonth3() {
		return revenueMonth3;
	}

	@Column(name = "revenue_Month4")
	public String getRevenueMonth4() {
		return revenueMonth4;
	}

	@Column(name = "revenue_Month5")
	public String getRevenueMonth5() {
		return revenueMonth5;
	}

	@Column(name = "revenue_Month6")
	public String getRevenueMonth6() {
		return revenueMonth6;
	}

	@Column(name = "revenue_Month7")
	public String getRevenueMonth7() {
		return revenueMonth7;
	}

	@Column(name = "revenue_Month8")
	public String getRevenueMonth8() {
		return revenueMonth8;
	}

	@Column(name = "revenue_Month9")
	public String getRevenueMonth9() {
		return revenueMonth9;
	}

	@Column(name = "revenue_Month10")
	public String getRevenueMonth10() {
		return revenueMonth10;
	}

	@Column(name = "revenue_Month11")
	public String getRevenueMonth11() {
		return revenueMonth11;
	}

	@Column(name = "revenue_Month12")
	public String getRevenueMonth12() {
		return revenueMonth12;
	}


	public void setEntity(String entity) {
		this.entity = entity;
	}
	public void setMakeEntity(String makeEntity) {
		this.makeEntity = makeEntity;
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

	public void setActivity(String activity) {
		this.activity = activity;
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

	public void setIndustryDemandTrend(String industryDemandTrend) {
		this.industryDemandTrend = industryDemandTrend;
	}

	public void setIndustryDemandTrendServed(String industryDemandTrendServed) {
		this.industryDemandTrendServed = industryDemandTrendServed;
	}

	public void setComponentUsage(String componentUsage) {
		this.componentUsage = componentUsage;
	}

	public void setAverageSalesPrice(String averageSalesPrice) {
		this.averageSalesPrice = averageSalesPrice;
	}

	public void setTotalAvailableMarket(String totalAvailableMarket) {
		this.totalAvailableMarket = totalAvailableMarket;
	}

	public void setServedAvailableMarket(String servedAvailableMarket) {
		this.servedAvailableMarket = servedAvailableMarket;
	}

	public void setAllocation(String allocation) {
		this.allocation = allocation;
	}

	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public void setQuantityMonth1(String quantityMonth1) {
		this.quantityMonth1 = quantityMonth1;
	}

	public void setQuantityMonth2(String quantityMonth2) {
		this.quantityMonth2 = quantityMonth2;
	}

	public void setQuantityMonth3(String quantityMonth3) {
		this.quantityMonth3 = quantityMonth3;
	}

	public void setQuantityMonth4(String quantityMonth4) {
		this.quantityMonth4 = quantityMonth4;
	}

	public void setQuantityMonth5(String quantityMonth5) {
		this.quantityMonth5 = quantityMonth5;
	}

	public void setQuantityMonth6(String quantityMonth6) {
		this.quantityMonth6 = quantityMonth6;
	}

	public void setQuantityMonth7(String quantityMonth7) {
		this.quantityMonth7 = quantityMonth7;
	}

	public void setQuantityMonth8(String quantityMonth8) {
		this.quantityMonth8 = quantityMonth8;
	}

	public void setQuantityMonth9(String quantityMonth9) {
		this.quantityMonth9 = quantityMonth9;
	}

	public void setQuantityMonth10(String quantityMonth10) {
		this.quantityMonth10 = quantityMonth10;
	}

	public void setQuantityMonth11(String quantityMonth11) {
		this.quantityMonth11 = quantityMonth11;
	}

	public void setQuantityMonth12(String quantityMonth12) {
		this.quantityMonth12 = quantityMonth12;
	}

	public void setPriceMonth1(String priceMonth1) {
		this.priceMonth1 = priceMonth1;
	}

	public void setPriceMonth2(String priceMonth2) {
		this.priceMonth2 = priceMonth2;
	}

	public void setPriceMonth3(String priceMonth3) {
		this.priceMonth3 = priceMonth3;
	}

	public void setPriceMonth4(String priceMonth4) {
		this.priceMonth4 = priceMonth4;
	}

	public void setPriceMonth5(String priceMonth5) {
		this.priceMonth5 = priceMonth5;
	}

	public void setPriceMonth6(String priceMonth6) {
		this.priceMonth6 = priceMonth6;
	}

	public void setPriceMonth7(String priceMonth7) {
		this.priceMonth7 = priceMonth7;
	}

	public void setPriceMonth8(String priceMonth8) {
		this.priceMonth8 = priceMonth8;
	}

	public void setPriceMonth9(String priceMonth9) {
		this.priceMonth9 = priceMonth9;
	}

	public void setPriceMonth10(String priceMonth10) {
		this.priceMonth10 = priceMonth10;
	}

	public void setPriceMonth11(String priceMonth11) {
		this.priceMonth11 = priceMonth11;
	}

	public void setPriceMonth12(String priceMonth12) {
		this.priceMonth12 = priceMonth12;
	}

	public void setRevenueMonth1(String revenueMonth1) {
		this.revenueMonth1 = revenueMonth1;
	}

	public void setRevenueMonth2(String revenueMonth2) {
		this.revenueMonth2 = revenueMonth2;
	}

	public void setRevenueMonth3(String revenueMonth3) {
		this.revenueMonth3 = revenueMonth3;
	}

	public void setRevenueMonth4(String revenueMonth4) {
		this.revenueMonth4 = revenueMonth4;
	}

	public void setRevenueMonth5(String revenueMonth5) {
		this.revenueMonth5 = revenueMonth5;
	}

	public void setRevenueMonth6(String revenueMonth6) {
		this.revenueMonth6 = revenueMonth6;
	}

	public void setRevenueMonth7(String revenueMonth7) {
		this.revenueMonth7 = revenueMonth7;
	}

	public void setRevenueMonth8(String revenueMonth8) {
		this.revenueMonth8 = revenueMonth8;
	}

	public void setRevenueMonth9(String revenueMonth9) {
		this.revenueMonth9 = revenueMonth9;
	}

	public void setRevenueMonth10(String revenueMonth10) {
		this.revenueMonth10 = revenueMonth10;
	}

	public void setRevenueMonth11(String revenueMonth11) {
		this.revenueMonth11 = revenueMonth11;
	}

	public void setRevenueMonth12(String revenueMonth12) {
		this.revenueMonth12 = revenueMonth12;
	}

}
