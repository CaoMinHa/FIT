package foxconn.fit.entity.bi;

import foxconn.fit.entity.hfm.AuditColumns;
import foxconn.fit.entity.hfm.AuditKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FIT_PO_TABLE")
public class PoTable implements Serializable{

	private static final long serialVersionUID = -2275276207234120757L;
	private String tableName;
	private String comments;
	private String type;
	private String uploadFlag;
	private String serial;
	private List<PoColumns> columns = new ArrayList<PoColumns>();
	private List<PoKey> keys = new ArrayList<PoKey>();

	public PoTable() {}

	public PoTable(String tableName, String comments,String uploadFlag) {
		this.tableName = tableName;
		this.comments = comments;
		this.uploadFlag = uploadFlag;
	}
	public PoTable(String tableName, String comments) {
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
	public List<PoColumns> getColumns() {
		return columns;
	}
	@Column
	public String getType() {
		return type;
	}
	@Column
	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "upload_flag")
	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	@OneToMany(mappedBy = "table",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
	@OrderBy(value = "serial asc")
	public List<PoKey> getKeys() {
		return keys;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setColumns(List<PoColumns> columns) {
		this.columns = columns;
	}

	public void setKeys(List<PoKey> keys) {
		this.keys = keys;
	}

}
