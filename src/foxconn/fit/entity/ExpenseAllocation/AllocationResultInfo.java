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
@Table(name = "CUX_BUDGET_ALLOCATION_VERSION")
public class AllocationResultInfo implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	
	@Column(name = "BASIS_YEAR")
	private String year;
	
	@Column(name = "SCENARIO")
	private String scenario;
	
	@Column(name = "VERSION_D")
	private String version;

	@Column(name = "VERSION_DATE")
	private String date;
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	public AllocationResultInfo() {

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