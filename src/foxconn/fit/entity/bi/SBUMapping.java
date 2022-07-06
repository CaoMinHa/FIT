package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 新老SBU對照表
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_SBU_Mapping")
public class SBUMapping extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String year;// 年
	private String oldSBUName;// 老SBU名稱
	private String newSBUName;// 新SBU名稱
	private String changeDesc;// 變更說明

	@Column
	public String getYear() {
		return year;
	}

	@Column(name = "old_SBU_Name")
	public String getOldSBUName() {
		return oldSBUName;
	}

	@Column(name = "new_SBU_Name")
	public String getNewSBUName() {
		return newSBUName;
	}

	@Column(name = "change_Desc")
	public String getChangeDesc() {
		return changeDesc;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setOldSBUName(String oldSBUName) {
		this.oldSBUName = oldSBUName;
	}

	public void setNewSBUName(String newSBUName) {
		this.newSBUName = newSBUName;
	}

	public void setChangeDesc(String changeDesc) {
		this.changeDesc = changeDesc;
	}

}
