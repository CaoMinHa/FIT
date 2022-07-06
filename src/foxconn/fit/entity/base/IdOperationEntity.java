package foxconn.fit.entity.base;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * 统一定义operation的entity基类.
 * 
 */
// JPA 基类的标识
@MappedSuperclass
public abstract class IdOperationEntity extends OperationEntity {

	private static final long serialVersionUID = 5142244736135944667L;

	protected String id;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "foxconn.fit.util.MyUUIDGenerator")
	@Column(name = "ID", length = 50, unique = true, nullable = false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "id="+this.id+","+super.toString();
	}

}
