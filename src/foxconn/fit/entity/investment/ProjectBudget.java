package foxconn.fit.entity.investment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maggao
 * 專案預算
 */
@Entity
@Table(name = "FIT_PROJECT_BUDGET")
public class ProjectBudget implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	private String year;
	/**專案編號*/
	@Column(name = "PROJECT_NUMBER")
	private String projectNumber;
	/**SBU_法人*/
	private String entity;
	/**提出部門*/
	private String department;
	/**產業*/
	private String industry;
	/**投資對象（設備）名稱*/
	@Column(name = "object_investment")
	private String objectInvestment;
    /**產品系列*/
	@Column(name = "product_series")
	private String productSeries;
	/**專案名稱*/
	@Column(name = "project_name")
	private String projectName;
	/**專案説明*/
	@Column(name = "project_description")
	private String projectDescription;
	/**三大技術（下拉選擇）*/
	private String three;
	/**Segment（下拉選擇）*/
	private String segment;
	/**3+3（下拉選擇）*/
	@Column(name = "main_business")
	private String mainBusiness;
	/**產品生命週期（用於購置設備）*/
	@Column(name = "product_life_cycle")
	private String productLifeCycle;
	/**預計開始年份*/
	@Column(name = "start_year")
	private String startYear;
	/**預計開始月份*/
	@Column(name = "start_month")
	private String startMonth;
	/**預計結束年份*/
	@Column(name = "end_year")
	private String endYear;
	/**預計結束月度*/
	@Column(name = "end_month")
	private String endMonth;
	/**預計費用支出(含人力)*/
	@Column(name = "expenditure_Expenses")
	private Double expenditureExpenses;
	/**預計資本性支出*/
	@Column(name = "capital_Expenditure")
	private Double capitalExpenditure;
	/**預估收益-營收*/
	private Double revenue;
	/**預估收益-淨利*/
	private Double profit;
	/**預估收益-營收（明年）*/
	@Column(name = "next_revenue")
	private Double nextRevenue;
	/**預估收益-淨利（明年）*/
	@Column(name = "next_profit")
	private Double nextProfit;
	/**預估收益-營收（后年）*/
	@Column(name = "after_revenue")
	private Double afterRevenue;
	/**預估收益-淨利（后年）*/
	@Column(name = "after_profit")
	private Double afterProfit;
	/**版本*/
	private String version;
	/**版本創建時間*/
	@Column(name = "version_date")
	private Date versionDate;
	/**版本創建人*/
	@Column(name = "version_name")
	private String versionName;
	/**創建人*/
	@Column(name = "create_name")
	private String createName;
	/**創建時間*/
	@Column(name = "create_date")
	private Date createDate;

	public ProjectBudget() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getProjectNumber() {
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
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

	public String getProductSeries() {
		return productSeries;
	}

	public void setProductSeries(String productSeries) {
		this.productSeries = productSeries;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getThree() {
		return three;
	}

	public void setThree(String three) {
		this.three = three;
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

	public String getProductLifeCycle() {
		return productLifeCycle;
	}

	public void setProductLifeCycle(String productLifeCycle) {
		this.productLifeCycle = productLifeCycle;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public Double getExpenditureExpenses() {
		return expenditureExpenses;
	}

	public void setExpenditureExpenses(Double expenditureExpenses) {
		this.expenditureExpenses = expenditureExpenses;
	}

	public Double getCapitalExpenditure() {
		return capitalExpenditure;
	}

	public void setCapitalExpenditure(Double capitalExpenditure) {
		this.capitalExpenditure = capitalExpenditure;
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
}
