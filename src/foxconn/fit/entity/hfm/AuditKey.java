package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

@Entity
@Table(name = "FIT_AUDIT_TABLE_KEY")
public class AuditKey extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String columnName;
	private String dataType;
	private String comments;
	private Integer serial;
	private AuditTable table;
	
	public AuditKey() {}
	
	public AuditKey(String columnName,String dataType, String comments,Integer serial) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.comments = comments;
		this.serial=serial;
	}

	@Column(name = "column_Name")
	public String getColumnName() {
		return columnName;
	}
	
	@Column(name = "data_Type")
	public String getDataType() {
		return dataType;
	}
	
	@Column
	public String getComments() {
		return comments;
	}
	
	@Column
	public Integer getSerial() {
		return serial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "table_Name", referencedColumnName = "table_Name", nullable = false)
	public AuditTable getTable() {
		return table;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void setSerial(Integer serial) {
		this.serial = serial;
	}

	public void setTable(AuditTable table) {
		this.table = table;
	}
}
