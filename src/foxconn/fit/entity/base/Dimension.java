package foxconn.fit.entity.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 維度表
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_DIMENSION")
public class Dimension extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String dimension;// 維度值
	private String parent;// 父类成员
	private String alias;// 别名表
	private String type;// 維度类型
	private String ouName;

	@Column(name = "ou_name")
	public String getOuName() {
		return ouName;
	}

	public void setOuName(String ouName) {
		this.ouName = ouName;
	}

	public Dimension(){
	}
	
	public Dimension(String dimension, String parent, String alias, String type,String ouName) {
		this.dimension = dimension;
		this.parent = parent;
		this.alias = alias;
		this.type = type;
		this.ouName=ouName;
	}
	public Dimension(String dimension, String parent, String alias, String type) {
		this.dimension = dimension;
		this.parent = parent;
		this.alias = alias;
		this.type = type;
	}

	@Column
	public String getDimension() {
		return dimension;
	}

	@Column
	public String getParent() {
		return parent;
	}

	@Column
	public String getAlias() {
		return alias;
	}

	@Column
	public String getType() {
		return type;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setType(String type) {
		this.type = type;
	}

}
