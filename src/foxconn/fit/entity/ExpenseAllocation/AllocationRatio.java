package foxconn.fit.entity.ExpenseAllocation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author cxj 20241219
 */

@Entity
@Table(name = "CUX_BUDGET_ALLOCATION_RATIO")
public class AllocationRatio implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	
	@Column(name = "BASIS_YEAR")
	private String year;
	
	@Column(name = "SCENARIO")
	private String scenario;
	
	@Column(name = "ENTITY")
	private String entity;
	
	@Column(name = "DEPARTMENT")
	private String department;
	
	@Column(name = "DEPARTMENT_NAME")
	private String departmentName;
	

	@Column(name = "DATA_BUDYEAR")
	private Double dataBudyear;
	@Column(name = "DATA_NEXT1")
	private Double dataNext1;
	@Column(name = "DATA_NEXT2")
	private Double dataNext2;
	@Column(name = "DATA_NEXT3")
	private Double dataNext3;
	@Column(name = "DATA_NEXT4")
	private Double dataNext4;
	@Column(name = "VERSION_DATE")
	private String date;
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	public AllocationRatio() {

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
	
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	public Double getDataBudyear() {
		return dataBudyear;
	}

	public void setDataBudyear(Double dataBudyear) {
		this.dataBudyear = dataBudyear;
	}

	public Double getDataNext1() {
		return dataNext1;
	}

	public void setDataNext1(Double dataNext1) {
		this.dataNext1 = dataNext1;
	}

	public Double getDataNext2() {
		return dataNext2;
	}

	public void setDataNext2(Double dataNext2) {
		this.dataNext2 = dataNext2;
	}

	public Double getDataNext3() {
		return dataNext3;
	}

	public void setDataNext3(Double dataNext3) {
		this.dataNext3 = dataNext3;
	}

	public Double getDataNext4() {
		return dataNext4;
	}

	public void setDataNext4(Double dataNext4) {
		this.dataNext4 = dataNext4;
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
