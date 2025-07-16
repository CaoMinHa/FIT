package foxconn.fit.entity.ExpenseAllocation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author cxj 20241220
 */
@Entity
@Table(name = "CUX_BUDGET_ALLOCATION_RESULT")
public class AllocationResult implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	
	@Column(name = "BASIS_YEAR")
	private String year;
	
	@Column(name = "SCENARIO")
	private String scenario;
	
	@Column(name = "VERSION_D")
	private String version;
	/**法人*/
	private String entity;
	
	@Column(name = "ENTITY_NAME")
	private String entityName;
	
	private String combine;
	private String currency;
	private String department;
	
	@Column(name = "DEPARTMENT_NAME")
	private String departmentName;
	
	private String bak2;
	private String bak1;
	private String project;
	
	@Column(name = "VIEW_D")
	private String view;
	@Column(name = "VIEW_NAME")
	private String viewName;
	
	private String account;
	@Column(name = "ACCOUNT_NAME")
	private String accountName;
	
	private String SBU;
	
	@Column(name = "AMOUNT_JAN")
	private Double amountJan;
	@Column(name = "AMOUNT_FEB")
	private Double amountFeb;
	@Column(name = "AMOUNT_MAR")
	private Double amountMar;
	@Column(name = "AMOUNT_APR")
	private Double amountApr;
	@Column(name = "AMOUNT_MAY")
	private Double amountMay;
	@Column(name = "AMOUNT_JUN")
	private Double amountJun;
	@Column(name = "AMOUNT_JUL")
	private Double amountJul;
	@Column(name = "AMOUNT_AUG")
	private Double amountAug;
	@Column(name = "AMOUNT_SEP")
	private Double amountSep;
	@Column(name = "AMOUNT_OCT")
	private Double amountOct;
	@Column(name = "AMOUNT_NOV")
	private Double amountNov;
	@Column(name = "AMOUNT_DEC")
	private Double amountDec;
	@Column(name = "AMOUNT_NEXT1")
	private Double amountNext1;
	@Column(name = "AMOUNT_NEXT2")
	private Double amountNext2;
	@Column(name = "AMOUNT_NEXT3")
	private Double amountNext3;
	@Column(name = "AMOUNT_NEXT4")
	private Double amountNext4;
	@Column(name = "VERSION_DATE")
	private String date;
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	public AllocationResult() {

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

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
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

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getCombine() {
		return combine;
	}

	public void setCombine(String combine) {
		this.combine = combine;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getBak2() {
		return bak2;
	}

	public void setBak2(String bak2) {
		this.bak2 = bak2;
	}

	public String getBak1() {
		return bak1;
	}

	public void setBak1(String bak1) {
		this.bak1 = bak1;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getSBU() {
		return SBU;
	}

	public void setSBU(String sBU) {
		SBU = sBU;
	}

	public Double getAmountJan() {
		return amountJan;
	}

	public void setAmountJan(Double amountJan) {
		this.amountJan = amountJan;
	}

	public Double getAmountFeb() {
		return amountFeb;
	}

	public void setAmountFeb(Double amountFeb) {
		this.amountFeb = amountFeb;
	}

	public Double getAmountMar() {
		return amountMar;
	}

	public void setAmountMar(Double amountMar) {
		this.amountMar = amountMar;
	}

	public Double getAmountApr() {
		return amountApr;
	}

	public void setAmountApr(Double amountApr) {
		this.amountApr = amountApr;
	}

	public Double getAmountMay() {
		return amountMay;
	}

	public void setAmountMay(Double amountMay) {
		this.amountMay = amountMay;
	}

	public Double getAmountJun() {
		return amountJun;
	}

	public void setAmountJun(Double amountJun) {
		this.amountJun = amountJun;
	}

	public Double getAmountJul() {
		return amountJul;
	}

	public void setAmountJul(Double amountJul) {
		this.amountJul = amountJul;
	}

	public Double getAmountAug() {
		return amountAug;
	}

	public void setAmountAug(Double amountAug) {
		this.amountAug = amountAug;
	}

	public Double getAmountSep() {
		return amountSep;
	}

	public void setAmountSep(Double amountSep) {
		this.amountSep = amountSep;
	}

	public Double getAmountOct() {
		return amountOct;
	}

	public void setAmountOct(Double amountOct) {
		this.amountOct = amountOct;
	}

	public Double getAmountNov() {
		return amountNov;
	}

	public void setAmountNov(Double amountNov) {
		this.amountNov = amountNov;
	}

	public Double getAmountDec() {
		return amountDec;
	}

	public void setAmountDec(Double amountDec) {
		this.amountDec = amountDec;
	}

	public Double getAmountNext1() {
		return amountNext1;
	}

	public void setAmountNext1(Double amountNext1) {
		this.amountNext1 = amountNext1;
	}

	public Double getAmountNext2() {
		return amountNext2;
	}

	public void setAmountNext2(Double amountNext2) {
		this.amountNext2 = amountNext2;
	}

	public Double getAmountNext3() {
		return amountNext3;
	}

	public void setAmountNext3(Double amountNext3) {
		this.amountNext3 = amountNext3;
	}

	public Double getAmountNext4() {
		return amountNext4;
	}

	public void setAmountNext4(Double amountNext4) {
		this.amountNext4 = amountNext4;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
}
