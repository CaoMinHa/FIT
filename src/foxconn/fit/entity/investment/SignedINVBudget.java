package foxconn.fit.entity.investment;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author cxj
 * 签核投資預算信息
 */
@Entity
@Table(name = "CUX_INV_BUDGET_INFO")
public class SignedINVBudget implements Serializable{

	@Id
	@Column(name = "ID")
	private String id;
	/**年份*/
	private String year;
	/**SBU_编码*/
	@Column(name = "sbu_code")
	private String sbuCode;
	/**SBU_名称*/
	@Column(name = "sbu_name")
	private String sbuName;
	/**部門*/
	@Column(name = "department_code")
	private String departmentCode;
	/**部門名称*/
	@Column(name = "department_name")
	private String departmentName;
	/**签核单号*/
	@Column(name = "doc_number")
	private String docNumber;
	/**单号日期*/
	@Column(name = "date_v")
	private String dateV;
	/**明细资料創建人*/
	@Column(name = "create_name")
	private String createName;
	/**签核信息接入時間*/
	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	public SignedINVBudget(){}

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

	public String getSbuCode() {
		return sbuCode;
	}

	public void setSbuCode(String sbuCode) {
		this.sbuCode = sbuCode;
	}

	public String getSbuName() {
		return sbuName;
	}

	public void setSbuName(String sbuName) {
		this.sbuName = sbuName;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDateV() {
		return dateV;
	}

	public void setDateV(String dateV) {
		this.dateV = dateV;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	
}
