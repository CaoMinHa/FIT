package foxconn.fit.entity.budget;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author maggao
 */
@Entity
@Table(name = "FIT_FORECAST_REVENUE")
public class ForecastSalesRevenue {

	private static final long serialVersionUID = -6340420848724099549L;
	@Id
	@Column(name = "ID",nullable = true)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String year;// 年
	private String version;// 年
	private String entity;//SBU_銷售法人
	@Column(name = "make_entity",nullable = false)
	private String makeEntity;// SBU_製造法人
	private String segment;//SEGMENT
	@Column(name = "main_industry")
	private String mainIndustry;// 主產業
	private String industry;// 次產業
	@Column(name = "main_business",nullable = false)
	private String mainBusiness;//3+3
	private String three;//三大技術
	@Column(name = "product_series")
	private String productSeries;//產品系列
	@Column(name = "product_no")
	private String productNo;// 產品料號
	@Column(name = "loan_customer")
	private String loanCustomer;// 賬款客戶
	@Column(name = "end_customer")
	private String endCustomer;// 最終客戶
	@Column(name = "type_of_airplane")
	private String typeOfAirplane;// 機種
	@Column(name = "trade_type")
	private String tradeType;// 交易類型
	private String currency;// 報告幣種
	private String pm;// PM
	@Column(name = "quantity_Month1")
	private String quantityMonth1;// 銷售數量1月
	@Column(name = "quantity_Month2")
	private String quantityMonth2;// 銷售數量2月
	@Column(name = "quantity_Month3")
	private String quantityMonth3;// 銷售數量3月
	@Column(name = "quantity_Month4")
	private String quantityMonth4;// 銷售數量4月
	@Column(name = "quantity_Month5")
	private String quantityMonth5;// 銷售數量5月
	@Column(name = "quantity_Month6")
	private String quantityMonth6;// 銷售數量6月
	@Column(name = "quantity_Month7")
	private String quantityMonth7;// 銷售數量7月
	@Column(name = "quantity_Month8")
	private String quantityMonth8;// 銷售數量8月
	@Column(name = "quantity_Month9")
	private String quantityMonth9;// 銷售數量9月
	@Column(name = "quantity_Month10")
	private String quantityMonth10;// 銷售數量10月
	@Column(name = "quantity_Month11")
	private String quantityMonth11;// 銷售數量11月
	@Column(name = "quantity_Month12")
	private String quantityMonth12;// 銷售數量12月
	@Column(name = "price_Month1")
	private String priceMonth1;// 单价1月
	@Column(name = "price_Month2")
	private String priceMonth2;// 单价2月
	@Column(name = "price_Month3")
	private String priceMonth3;// 单价3月
	@Column(name = "price_Month4")
	private String priceMonth4;// 单价4月
	@Column(name = "price_Month5")
	private String priceMonth5;// 单价5月
	@Column(name = "price_Month6")
	private String priceMonth6;// 单价6月
	@Column(name = "price_Month7")
	private String priceMonth7;// 单价7月
	@Column(name = "price_Month8")
	private String priceMonth8;// 单价8月
	@Column(name = "price_Month9")
	private String priceMonth9;// 单价9月
	@Column(name = "price_Month10")
	private String priceMonth10;// 单价10月
	@Column(name = "price_Month11")
	private String priceMonth11;// 单价11月
	@Column(name = "price_Month12")
	private String priceMonth12;// 单价12月
	@Column(name = "create_name")
	private String createName;//數據創建人
	@Column(name = "create_date")
	private Date createDate;//數據創建時間
	@Column(name = "version_name")
	private String versionName;//版本存檔人
	@Column(name = "version_date")
	private Date versionDate;//版本存檔時間
	private String OU;//銷售法人 OU Code
	private String MAKEOU;//製造法人OU Code
	@Column(name = "currency_transition")
	private String currencyTransition;// 轉換幣種

	public String getCurrencyTransition() {
		return currencyTransition;
	}

	public void setCurrencyTransition(String currencyTransition) {
		this.currencyTransition = currencyTransition;
	}

	public ForecastSalesRevenue(String entity, String makeEntity, String segment, String mainIndustry, String industry, String mainBusiness, String three, String productSeries, String productNo, String loanCustomer, String endCustomer, String typeOfAirplane, String tradeType, String currency, String pm, String quantityMonth1, String quantityMonth2, String quantityMonth3, String quantityMonth4, String quantityMonth5, String quantityMonth6, String quantityMonth7, String quantityMonth8, String quantityMonth9, String quantityMonth10, String quantityMonth11, String quantityMonth12, String priceMonth1, String priceMonth2, String priceMonth3, String priceMonth4, String priceMonth5, String priceMonth6, String priceMonth7, String priceMonth8, String priceMonth9, String priceMonth10, String priceMonth11, String priceMonth12, String createName) {
		this.entity = entity;
		this.makeEntity = makeEntity;
		this.segment = segment;
		this.mainIndustry = mainIndustry;
		this.industry = industry;
		this.mainBusiness = mainBusiness;
		this.three = three;
		this.productSeries = productSeries;
		this.productNo = productNo;
		this.loanCustomer = loanCustomer;
		this.endCustomer = endCustomer;
		this.typeOfAirplane = typeOfAirplane;
		this.tradeType = tradeType;
		this.currency = currency;
		this.pm = pm;
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
		this.createName = createName;
	}

	public ForecastSalesRevenue(String year, String version, String entity, String makeEntity, String segment, String mainIndustry, String industry, String mainBusiness, String three, String productSeries, String productNo, String loanCustomer, String endCustomer, String typeOfAirplane, String tradeType, String currency, String pm, String quantityMonth1, String quantityMonth2, String quantityMonth3, String quantityMonth4, String quantityMonth5, String quantityMonth6, String quantityMonth7, String quantityMonth8, String quantityMonth9, String quantityMonth10, String quantityMonth11, String quantityMonth12, String priceMonth1, String priceMonth2, String priceMonth3, String priceMonth4, String priceMonth5, String priceMonth6, String priceMonth7, String priceMonth8, String priceMonth9, String priceMonth10, String priceMonth11, String priceMonth12) {
		this.year = year;
		this.version = version;
		this.entity = entity;
		this.makeEntity = makeEntity;
		this.segment = segment;
		this.mainIndustry = mainIndustry;
		this.industry = industry;
		this.mainBusiness = mainBusiness;
		this.three = three;
		this.productSeries = productSeries;
		this.productNo = productNo;
		this.loanCustomer = loanCustomer;
		this.endCustomer = endCustomer;
		this.typeOfAirplane = typeOfAirplane;
		this.tradeType = tradeType;
		this.currency = currency;
		this.pm = pm;
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
	}

	@Override
	public String toString() {
		return "BudgetDetailRevenue{" +
				"year='" + year + '\'' +
				", version='" + version + '\'' +
				", entity='" + entity + '\'' +
				", makeEntity='" + makeEntity + '\'' +
				", segment='" + segment + '\'' +
				", mainIndustry='" + mainIndustry + '\'' +
				", industry='" + industry + '\'' +
				", mainBusiness='" + mainBusiness + '\'' +
				", three='" + three + '\'' +
				", productSeries='" + productSeries + '\'' +
				", productNo='" + productNo + '\'' +
				", loanCustomer='" + loanCustomer + '\'' +
				", endCustomer='" + endCustomer + '\'' +
				", typeOfAirplane='" + typeOfAirplane + '\'' +
				", tradeType='" + tradeType + '\'' +
				", currency='" + currency + '\'' +
				", pm='" + pm + '\'' +
				", quantityMonth1='" + quantityMonth1 + '\'' +
				", quantityMonth2='" + quantityMonth2 + '\'' +
				", quantityMonth3='" + quantityMonth3 + '\'' +
				", quantityMonth4='" + quantityMonth4 + '\'' +
				", quantityMonth5='" + quantityMonth5 + '\'' +
				", quantityMonth6='" + quantityMonth6 + '\'' +
				", quantityMonth7='" + quantityMonth7 + '\'' +
				", quantityMonth8='" + quantityMonth8 + '\'' +
				", quantityMonth9='" + quantityMonth9 + '\'' +
				", quantityMonth10='" + quantityMonth10 + '\'' +
				", quantityMonth11='" + quantityMonth11 + '\'' +
				", quantityMonth12='" + quantityMonth12 + '\'' +
				", priceMonth1='" + priceMonth1 + '\'' +
				", priceMonth2='" + priceMonth2 + '\'' +
				", priceMonth3='" + priceMonth3 + '\'' +
				", priceMonth4='" + priceMonth4 + '\'' +
				", priceMonth5='" + priceMonth5 + '\'' +
				", priceMonth6='" + priceMonth6 + '\'' +
				", priceMonth7='" + priceMonth7 + '\'' +
				", priceMonth8='" + priceMonth8 + '\'' +
				", priceMonth9='" + priceMonth9 + '\'' +
				", priceMonth10='" + priceMonth10 + '\'' +
				", priceMonth11='" + priceMonth11 + '\'' +
				", priceMonth12='" + priceMonth12 + '\'' +
				", createName='" + createName + '\'' +
				", createDate=" + createDate +
				", versionName='" + versionName + '\'' +
				", versionDate=" + versionDate +
				'}';
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getMakeEntity() {
		return makeEntity;
	}

	public void setMakeEntity(String makeEntity) {
		this.makeEntity = makeEntity;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getMainIndustry() {
		return mainIndustry;
	}

	public void setMainIndustry(String mainIndustry) {
		this.mainIndustry = mainIndustry;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getmainBusiness() {
		return mainBusiness;
	}

	public void setmainBusiness(String mainBusiness) {
		this.mainBusiness = mainBusiness;
	}

	public String getThree() {
		return three;
	}

	public void setThree(String three) {
		this.three = three;
	}

	public String getProductSeries() {
		return productSeries;
	}

	public void setProductSeries(String productSeries) {
		this.productSeries = productSeries;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getLoanCustomer() {
		return loanCustomer;
	}

	public void setLoanCustomer(String loanCustomer) {
		this.loanCustomer = loanCustomer;
	}

	public String getEndCustomer() {
		return endCustomer;
	}

	public void setEndCustomer(String endCustomer) {
		this.endCustomer = endCustomer;
	}

	public String getTypeOfAirplane() {
		return typeOfAirplane;
	}

	public void setTypeOfAirplane(String typeOfAirplane) {
		this.typeOfAirplane = typeOfAirplane;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPm() {
		return pm;
	}

	public void setPm(String pm) {
		this.pm = pm;
	}

	public String getQuantityMonth1() {
		return quantityMonth1;
	}

	public void setQuantityMonth1(String quantityMonth1) {
		this.quantityMonth1 = quantityMonth1;
	}

	public String getQuantityMonth2() {
		return quantityMonth2;
	}

	public void setQuantityMonth2(String quantityMonth2) {
		this.quantityMonth2 = quantityMonth2;
	}

	public String getQuantityMonth3() {
		return quantityMonth3;
	}

	public void setQuantityMonth3(String quantityMonth3) {
		this.quantityMonth3 = quantityMonth3;
	}

	public String getQuantityMonth4() {
		return quantityMonth4;
	}

	public void setQuantityMonth4(String quantityMonth4) {
		this.quantityMonth4 = quantityMonth4;
	}

	public String getQuantityMonth5() {
		return quantityMonth5;
	}

	public void setQuantityMonth5(String quantityMonth5) {
		this.quantityMonth5 = quantityMonth5;
	}

	public String getQuantityMonth6() {
		return quantityMonth6;
	}

	public void setQuantityMonth6(String quantityMonth6) {
		this.quantityMonth6 = quantityMonth6;
	}

	public String getQuantityMonth7() {
		return quantityMonth7;
	}

	public void setQuantityMonth7(String quantityMonth7) {
		this.quantityMonth7 = quantityMonth7;
	}

	public String getQuantityMonth8() {
		return quantityMonth8;
	}

	public void setQuantityMonth8(String quantityMonth8) {
		this.quantityMonth8 = quantityMonth8;
	}

	public String getQuantityMonth9() {
		return quantityMonth9;
	}

	public void setQuantityMonth9(String quantityMonth9) {
		this.quantityMonth9 = quantityMonth9;
	}

	public String getQuantityMonth10() {
		return quantityMonth10;
	}

	public void setQuantityMonth10(String quantityMonth10) {
		this.quantityMonth10 = quantityMonth10;
	}

	public String getQuantityMonth11() {
		return quantityMonth11;
	}

	public void setQuantityMonth11(String quantityMonth11) {
		this.quantityMonth11 = quantityMonth11;
	}

	public String getQuantityMonth12() {
		return quantityMonth12;
	}

	public void setQuantityMonth12(String quantityMonth12) {
		this.quantityMonth12 = quantityMonth12;
	}

	public String getPriceMonth1() {
		return priceMonth1;
	}

	public void setPriceMonth1(String priceMonth1) {
		this.priceMonth1 = priceMonth1;
	}

	public String getPriceMonth2() {
		return priceMonth2;
	}

	public void setPriceMonth2(String priceMonth2) {
		this.priceMonth2 = priceMonth2;
	}

	public String getPriceMonth3() {
		return priceMonth3;
	}

	public void setPriceMonth3(String priceMonth3) {
		this.priceMonth3 = priceMonth3;
	}

	public String getPriceMonth4() {
		return priceMonth4;
	}

	public void setPriceMonth4(String priceMonth4) {
		this.priceMonth4 = priceMonth4;
	}

	public String getPriceMonth5() {
		return priceMonth5;
	}

	public void setPriceMonth5(String priceMonth5) {
		this.priceMonth5 = priceMonth5;
	}

	public String getPriceMonth6() {
		return priceMonth6;
	}

	public void setPriceMonth6(String priceMonth6) {
		this.priceMonth6 = priceMonth6;
	}

	public String getPriceMonth7() {
		return priceMonth7;
	}

	public void setPriceMonth7(String priceMonth7) {
		this.priceMonth7 = priceMonth7;
	}

	public String getPriceMonth8() {
		return priceMonth8;
	}

	public void setPriceMonth8(String priceMonth8) {
		this.priceMonth8 = priceMonth8;
	}

	public String getPriceMonth9() {
		return priceMonth9;
	}

	public void setPriceMonth9(String priceMonth9) {
		this.priceMonth9 = priceMonth9;
	}

	public String getPriceMonth10() {
		return priceMonth10;
	}

	public void setPriceMonth10(String priceMonth10) {
		this.priceMonth10 = priceMonth10;
	}

	public String getPriceMonth11() {
		return priceMonth11;
	}

	public void setPriceMonth11(String priceMonth11) {
		this.priceMonth11 = priceMonth11;
	}

	public String getPriceMonth12() {
		return priceMonth12;
	}

	public void setPriceMonth12(String priceMonth12) {
		this.priceMonth12 = priceMonth12;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getOU() {
		return OU;
	}

	public void setOU(String OU) {
		this.OU = OU;
	}

	public String getMAKEOU() {
		return MAKEOU;
	}

	public void setMAKEOU(String MAKEOU) {
		this.MAKEOU = MAKEOU;
	}

	public ForecastSalesRevenue() {
	}

}
