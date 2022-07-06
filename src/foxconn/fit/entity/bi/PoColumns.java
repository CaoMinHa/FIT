package foxconn.fit.entity.bi;

import foxconn.fit.entity.base.IdEntity;
import foxconn.fit.entity.hfm.AuditTable;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "FIT_PO_TABLE_COLUMNS")
public class PoColumns extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String columnName;
	private String dataType;
	private String comments;
	private String examples;
	private Integer serial;
	private boolean nullable=true;//是否可为空
	private boolean locked=false;//是否锁定
	private PoTable table;

	public PoColumns() {}

	public PoColumns(String columnName, String dataType, String comments,String examples, Integer serial, boolean nullable) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.comments = comments;
		this.examples = examples;
		this.serial = serial;
		this.nullable=nullable;
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
	public String getExamples() {
		return examples;
	}

	@Column
	public Integer getSerial() {
		return serial;
	}

	@Column
	@Type(type="true_false")
	public boolean getNullable() {
		return nullable;
	}

	@Column
	@Type(type="true_false")
	public boolean getLocked() {
		return locked;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "table_Name", referencedColumnName = "table_Name", nullable = false)
	public PoTable getTable() {
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

	public void setExamples(String examples) {this.examples = examples;}

	public void setSerial(Integer serial) {
		this.serial = serial;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setTable(PoTable table) {
		this.table = table;
	}

}
