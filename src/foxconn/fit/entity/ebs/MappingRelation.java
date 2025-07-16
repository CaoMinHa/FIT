package foxconn.fit.entity.ebs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import foxconn.fit.entity.base.IdEntity;

/**
 * 映射关系表
 * 
 * @author liangchen cxj update20241009
 *
 */
@Entity
@Table(name = "CUX_DATAMAP")
public class MappingRelation extends IdEntity {
	private static final long serialVersionUID = 3190252124084180360L;

	private String ENTITY;// 公司编码
	private String COST_CENTER;//成本中心
	private String DIMNAME;// 映射类别，科目：ACCOUNT，公司：ENTITY，关联方：ICP，自定义：UD
	private String SRCKEY;// 映射源值
	private String SRCDESC;// HFM映射源值中文名称
	private String SRCDESC_US;// HFM映射源值英文名称
	private String TARGKEY;// HFM映射目标值
	private String TARGDESC;// 映射目标名称
	private String TARGDESC_US;// 映射目标名称
	private String CCT_ACCOUNT;// CCT科目分類
	private String CCT_ACCOUNT_ATT;// CCT科目屬性
	private boolean CHANGESIGN = false;// 是否变号
	private String CATEGORY;// 科目標識
	private Date CREATION_DATE;// 创建日期
	private String CREATED_BY;// 创建用户

	@Column
	public String getENTITY() {
		return ENTITY;
	}
	@Column
	public String getCOST_CENTER() {
		return COST_CENTER;
	}
	@Column
	public String getDIMNAME() {
		return DIMNAME;
	}

	@Column
	public String getSRCKEY() {
		return SRCKEY;
	}

	@Column
	public String getSRCDESC() {
		return SRCDESC;
	}
	
	@Column
	public String getSRCDESC_US() {
		return SRCDESC_US;
	}

	@Column
	public String getTARGKEY() {
		return TARGKEY;
	}

	@Column
	public String getTARGDESC() {
		return TARGDESC;
	}
	
	@Column
	public String getTARGDESC_US() {
		return TARGDESC_US;
	}

	@Column
	public String getCCT_ACCOUNT() {
		return CCT_ACCOUNT;
	}

	@Column
	public String getCCT_ACCOUNT_ATT() {
		return CCT_ACCOUNT_ATT;
	}

	@Column
	@Type(type = "true_false")
	public boolean isCHANGESIGN() {
		return CHANGESIGN;
	}

	@Column
	public String getCATEGORY() {
		return CATEGORY;
	}

	@Column
	public Date getCREATION_DATE() {
		return CREATION_DATE;
	}

	@Column
	public String getCREATED_BY() {
		return CREATED_BY;
	}

	public void setENTITY(String eNTITY) {
		ENTITY = eNTITY;
	}

	public void setCOST_CENTER(String cOST_CENTER) {
		COST_CENTER = cOST_CENTER;
	}

	public void setDIMNAME(String dIMNAME) {
		DIMNAME = dIMNAME;
	}

	public void setSRCKEY(String sRCKEY) {
		SRCKEY = sRCKEY;
	}

	public void setSRCDESC(String sRCDESC) {
		SRCDESC = sRCDESC;
	}
	
	public void setSRCDESC_US(String sRCDESC_US) {
		SRCDESC_US = sRCDESC_US;
	}

	public void setTARGKEY(String tARGKEY) {
		TARGKEY = tARGKEY;
	}

	public void setTARGDESC(String tARGDESC) {
		TARGDESC = tARGDESC;
	}
	
	public void setTARGDESC_US(String tARGDESC_US) {
		TARGDESC_US = tARGDESC_US;
	}

	public void setCCT_ACCOUNT(String cCT_ACCOUNT) {
		CCT_ACCOUNT = cCT_ACCOUNT;
	}

	public void setCCT_ACCOUNT_ATT(String cCT_ACCOUNT_ATT) {
		CCT_ACCOUNT_ATT = cCT_ACCOUNT_ATT;
	}

	public void setCHANGESIGN(boolean cHANGESIGN) {
		CHANGESIGN = cHANGESIGN;
	}

	public void setCATEGORY(String cATEGORY) {
		CATEGORY = cATEGORY;
	}

	public void setCREATION_DATE(Date cREATION_DATE) {
		CREATION_DATE = cREATION_DATE;
	}

	public void setCREATED_BY(String cREATED_BY) {
		CREATED_BY = cREATED_BY;
	}

}
