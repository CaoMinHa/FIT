package foxconn.fit.entity.ebs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 参数表
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_PARAMETER")
public class Parameter implements Serializable {

	private static final long serialVersionUID = 3051557260157953394L;

	private String key;
	private String value;

	@Id
	@Column
	public String getKey() {
		return key;
	}

	@Column
	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
