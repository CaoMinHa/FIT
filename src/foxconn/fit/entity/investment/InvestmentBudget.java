package foxconn.fit.entity.investment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maggao
 * 投資預算
 */
@Entity
@Table(name = "FIT_INVESTMENT_BUDGET")
public class InvestmentBudget implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	/**創建人*/
	@Column(name = "create_name")
	private String createName;
	/**創建時間*/
	@Column(name = "create_date")
	private Date createDate;
	/**投資編號*/
	@Column(name = "investment_no")
	private String investmentNo;
	/**設備類別*/
	@Column(name = "category_equipment")
	private String categoryEquipment;
	/**SBU_法人*/
	private String entity;
	/**提出部門*/
	private String department;
	/**Segment（下拉選擇）*/
	private String segment;
	/**Main Business（下拉選擇）*/
	@Column(name = "main_business")
	private String mainBusiness;
	/**產業*/
	private String industry;
	/**投資對象（設備）名稱*/
	@Column(name = "object_investment")
	private String objectInvestment;
	/**使用部門*/
	@Column(name = "use_department")
	private String useDepartment;
	/**投資類型*/
	@Column(name = "investment_type")
	private String investmentType;
	/**專案名稱*/
	@Column(name = "project_name")
	private String projectName;
    /**產品系列*/
	@Column(name = "product_series")
	private String productSeries;
	/**需求數量 (或場地面積)*/
	@Column(name = "quantity_required")
	private Integer quantityRequired;
	/**產品生命週期（用於購置設備）*/
	@Column(name = "product_life_cycle")
	private String productLifeCycle;
	/**請購單或模治具執行單年月*/
	@Column(name = "po_period")
	private String poPeriod;
	/**驗收單年月（轉固定資產月份）*/
	@Column(name = "receipt_date")
	private String receiptDate;
	/**結報年月或模治具執行單結案年月*/
	@Column(name = "due_period")
	private String duePeriod;
	/**投資金額*/
	@Column(name = "amount_investment")
	private Double amountInvestment;
	/**投資說明*/
	@Column(name = "description_investment")
	private String descriptionInvestment;
	/**預估收益-營收*/
	private Double revenue;
	/**預估收益-淨利*/
	private Double profit;
	/**預估收益-營收*/
	@Column(name = "next_revenue")
	private Double nextRevenue;
	/**預估收益-淨利*/
	@Column(name = "next_profit")
	private Double nextProfit;
	/**預估收益-營收*/
	@Column(name = "after_revenue")
	private Double afterRevenue;
	/**預估收益-淨利*/
	@Column(name = "after_profit")
	private Double afterProfit;
	private String year;
	/**版本*/
	private String version;
	/**版本創建時間*/
	@Column(name = "version_date")
	private Date versionDate;
	/**版本創建人*/
	@Column(name = "version_name")
	private String versionName;
	/**需求數量 (或場地面積)（明年）*/
	@Column(name = "next_quantity_required")
	private Integer nextQuantityRequired;
	/**投資金額(本位幣)（明年）*/
	@Column(name = "next_amount_investment")
	private Double nextAmountInvestment;
	/**需求數量(或場地面積)（后年）*/
	@Column(name = "after_quantity_required")
	private Integer afterQuantityRequired;
	/**投資金額(本位幣)（后年）*/
	@Column(name = "after_amount_investment")
	private Double afterAmountInvestment;

	public InvestmentBudget(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getInvestmentNo() {
		return investmentNo;
	}

	public void setInvestmentNo(String investmentNo) {
		this.investmentNo = investmentNo;
	}

	public String getCategoryEquipment() {
		return categoryEquipment;
	}

	public void setCategoryEquipment(String categoryEquipment) {
		this.categoryEquipment = categoryEquipment;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getMainBusiness() {
		return mainBusiness;
	}

	public void setMainBusiness(String mainBusiness) {
		this.mainBusiness = mainBusiness;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getObjectInvestment() {
		return objectInvestment;
	}

	public void setObjectInvestment(String objectInvestment) {
		this.objectInvestment = objectInvestment;
	}

	public String getUseDepartment() {
		return useDepartment;
	}

	public void setUseDepartment(String useDepartment) {
		this.useDepartment = useDepartment;
	}

	public String getInvestmentType() {
		return investmentType;
	}

	public void setInvestmentType(String investmentType) {
		this.investmentType = investmentType;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProductSeries() {
		return productSeries;
	}

	public void setProductSeries(String productSeries) {
		this.productSeries = productSeries;
	}

	public Integer getQuantityRequired() {
		return quantityRequired;
	}

	public void setQuantityRequired(Integer quantityRequired) {
		this.quantityRequired = quantityRequired;
	}

	public String getProductLifeCycle() {
		return productLifeCycle;
	}

	public void setProductLifeCycle(String productLifeCycle) {
		this.productLifeCycle = productLifeCycle;
	}

	public String getPoPeriod() {
		return poPeriod;
	}

	public void setPoPeriod(String poPeriod) {
		this.poPeriod = poPeriod;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getDuePeriod() {
		return duePeriod;
	}

	public void setDuePeriod(String duePeriod) {
		this.duePeriod = duePeriod;
	}

	public Double getAmountInvestment() {
		return amountInvestment;
	}

	public void setAmountInvestment(Double amountInvestment) {
		this.amountInvestment = amountInvestment;
	}

	public String getDescriptionInvestment() {
		return descriptionInvestment;
	}

	public void setDescriptionInvestment(String descriptionInvestment) {
		this.descriptionInvestment = descriptionInvestment;
	}

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	public Double getNextRevenue() {
		return nextRevenue;
	}

	public void setNextRevenue(Double nextRevenue) {
		this.nextRevenue = nextRevenue;
	}

	public Double getNextProfit() {
		return nextProfit;
	}

	public void setNextProfit(Double nextProfit) {
		this.nextProfit = nextProfit;
	}

	public Double getAfterRevenue() {
		return afterRevenue;
	}

	public void setAfterRevenue(Double afterRevenue) {
		this.afterRevenue = afterRevenue;
	}

	public Double getAfterProfit() {
		return afterProfit;
	}

	public void setAfterProfit(Double afterProfit) {
		this.afterProfit = afterProfit;
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

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Integer getNextQuantityRequired() {
		return nextQuantityRequired;
	}

	public void setNextQuantityRequired(Integer nextQuantityRequired) {
		this.nextQuantityRequired = nextQuantityRequired;
	}

	public Double getNextAmountInvestment() {
		return nextAmountInvestment;
	}

	public void setNextAmountInvestment(Double nextAmountInvestment) {
		this.nextAmountInvestment = nextAmountInvestment;
	}

	public Integer getAfterQuantityRequired() {
		return afterQuantityRequired;
	}

	public void setAfterQuantityRequired(Integer afterQuantityRequired) {
		this.afterQuantityRequired = afterQuantityRequired;
	}

	public Double getAfterAmountInvestment() {
		return afterAmountInvestment;
	}

	public void setAfterAmountInvestment(Double afterAmountInvestment) {
		this.afterAmountInvestment = afterAmountInvestment;
	}
}
