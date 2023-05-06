package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maggao
 * backlog動態預估
 */
@Entity
@Table(name = "FIT_BACKLOG_DYNAMIC_PREDICTION")
public class RtDynamicPrediction implements Serializable {

	@Id
	private String id;
	/**創建人*/
	@Column(name = "create_name")
	private String createName;
	/**創建時間*/
	@Column(name = "CREATE_TIME")
	private Date createTime;
	private String year;
	private String SBU;
	private String JAN;
	private String FEB;
	private String MAR;
	private String APR;
	private String MAY;
	private String JUN;
	private String JUL;
	private String AUG;
	private String SEP;
	private String OCT;
	private String NOV;
	private String DEC;

	public RtDynamicPrediction() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSBU() {
		return SBU;
	}

	public void setSBU(String SBU) {
		this.SBU = SBU;
	}

	public String getJAN() {
		return JAN;
	}

	public void setJAN(String JAN) {
		this.JAN = JAN;
	}

	public String getFEB() {
		return FEB;
	}

	public void setFEB(String FEB) {
		this.FEB = FEB;
	}

	public String getMAR() {
		return MAR;
	}

	public void setMAR(String MAR) {
		this.MAR = MAR;
	}

	public String getAPR() {
		return APR;
	}

	public void setAPR(String APR) {
		this.APR = APR;
	}

	public String getMAY() {
		return MAY;
	}

	public void setMAY(String MAY) {
		this.MAY = MAY;
	}

	public String getJUN() {
		return JUN;
	}

	public void setJUN(String JUN) {
		this.JUN = JUN;
	}

	public String getJUL() {
		return JUL;
	}

	public void setJUL(String JUL) {
		this.JUL = JUL;
	}

	public String getAUG() {
		return AUG;
	}

	public void setAUG(String AUG) {
		this.AUG = AUG;
	}

	public String getSEP() {
		return SEP;
	}

	public void setSEP(String SEP) {
		this.SEP = SEP;
	}

	public String getOCT() {
		return OCT;
	}

	public void setOCT(String OCT) {
		this.OCT = OCT;
	}

	public String getNOV() {
		return NOV;
	}

	public void setNOV(String NOV) {
		this.NOV = NOV;
	}

	public String getDEC() {
		return DEC;
	}

	public void setDEC(String DEC) {
		this.DEC = DEC;
	}
}