package foxconn.fit.entity.base;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * 统一定义ID的entity基类.
 * 
 */
// JPA 基类的标识
@MappedSuperclass
public abstract class IdEntity implements Serializable{

	private static final long serialVersionUID = -5538596785609361901L;
	
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
