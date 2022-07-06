package foxconn.fit.entity.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Planning
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_PLANNING")
public class Planning extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String ACCOUNT;
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
	private String YT;
	private String POINT_OF_VIEW;
	private String DATA_LOAD_CUBE_NAME;

	@Column
	public String getACCOUNT() {
		return ACCOUNT;
	}

	@Column
	public String getJAN() {
		return JAN;
	}

	@Column
	public String getFEB() {
		return FEB;
	}

	@Column
	public String getMAR() {
		return MAR;
	}

	@Column
	public String getAPR() {
		return APR;
	}

	@Column
	public String getMAY() {
		return MAY;
	}

	@Column
	public String getJUN() {
		return JUN;
	}

	@Column
	public String getJUL() {
		return JUL;
	}

	@Column
	public String getAUG() {
		return AUG;
	}

	@Column
	public String getSEP() {
		return SEP;
	}

	@Column
	public String getOCT() {
		return OCT;
	}

	@Column
	public String getNOV() {
		return NOV;
	}

	@Column
	public String getDEC() {
		return DEC;
	}

	@Column
	public String getYT() {
		return YT;
	}

	@Column
	public String getPOINT_OF_VIEW() {
		return POINT_OF_VIEW;
	}

	@Column
	public String getDATA_LOAD_CUBE_NAME() {
		return DATA_LOAD_CUBE_NAME;
	}

	public void setACCOUNT(String aCCOUNT) {
		ACCOUNT = aCCOUNT;
	}

	public void setJAN(String jAN) {
		JAN = jAN;
	}

	public void setFEB(String fEB) {
		FEB = fEB;
	}

	public void setMAR(String mAR) {
		MAR = mAR;
	}

	public void setAPR(String aPR) {
		APR = aPR;
	}

	public void setMAY(String mAY) {
		MAY = mAY;
	}

	public void setJUN(String jUN) {
		JUN = jUN;
	}

	public void setJUL(String jUL) {
		JUL = jUL;
	}

	public void setAUG(String aUG) {
		AUG = aUG;
	}

	public void setSEP(String sEP) {
		SEP = sEP;
	}

	public void setOCT(String oCT) {
		OCT = oCT;
	}

	public void setNOV(String nOV) {
		NOV = nOV;
	}

	public void setDEC(String dEC) {
		DEC = dEC;
	}

	public void setYT(String yT) {
		YT = yT;
	}

	public void setPOINT_OF_VIEW(String pOINT_OF_VIEW) {
		POINT_OF_VIEW = pOINT_OF_VIEW;
	}

	public void setDATA_LOAD_CUBE_NAME(String dATA_LOAD_CUBE_NAME) {
		DATA_LOAD_CUBE_NAME = dATA_LOAD_CUBE_NAME;
	}

}
