package foxconn.fit.entity.ProfitLoss;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CUX_BUDGET_HP_PL")
public class LegalProfitloss implements Serializable{
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
	
	@Column(name = "VERSION_DATE")
	private String date;

	
	public LegalProfitloss() {

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
	public void setEntity_name(String entityName) {
		this.entityName = entityName;
	}		
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
