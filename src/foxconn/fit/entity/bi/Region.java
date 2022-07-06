package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 区域映射
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Region")
public class Region extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String code;// 業務代碼
	private String department;// 業務部門
	private String region;// 大區域
	private String judgeRegion;// 營收判定區域

	@Column
	public String getCode() {
		return code;
	}

	@Column
	public String getDepartment() {
		return department;
	}

	@Column
	public String getRegion() {
		return region;
	}

	@Column(name = "judge_Region")
	public String getJudgeRegion() {
		return judgeRegion;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setJudgeRegion(String judgeRegion) {
		this.judgeRegion = judgeRegion;
	}

}
