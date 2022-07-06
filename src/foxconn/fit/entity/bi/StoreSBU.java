package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 倉碼&SBU映射
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Store_SBU")
public class StoreSBU extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String code;// 倉碼
	private String SBU;// SBU

	@Column
	public String getCode() {
		return code;
	}

	@Column
	public String getSBU() {
		return SBU;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setSBU(String sBU) {
		SBU = sBU;
	}

}
