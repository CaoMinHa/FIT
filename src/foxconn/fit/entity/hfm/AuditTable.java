package foxconn.fit.entity.hfm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "FIT_AUDIT_TABLE")
public class AuditTable implements Serializable{

	private static final long serialVersionUID = -2275276207234120757L;
	private String tableName;
	private String comments;
	private List<AuditColumns> columns = new ArrayList<AuditColumns>();
	private List<AuditKey> keys = new ArrayList<AuditKey>();

	public AuditTable() {}

	public AuditTable(String tableName, String comments) {
		this.tableName = tableName;
		this.comments = comments;
	}

	@Id
	@Column(name = "table_Name")
	public String getTableName() {
		return tableName;
	}

	@Column
	public String getComments() {
		return comments;
	}

	@OneToMany(mappedBy = "table",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
	@OrderBy(value = "serial asc")
	public List<AuditColumns> getColumns() {
		return columns;
	}

	@OneToMany(mappedBy = "table",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
	@OrderBy(value = "serial asc")
	public List<AuditKey> getKeys() {
		return keys;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setColumns(List<AuditColumns> columns) {
		this.columns = columns;
	}

	public void setKeys(List<AuditKey> keys) {
		this.keys = keys;
	}

}
