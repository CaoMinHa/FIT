package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 總賬科目映射維護
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_COA_MAPPING")
public class CoaMapping extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// ERP法人編碼
	private String erpItemCode;// ERP科目編碼
	private String erpItemDesc;// ERP科目描述
	private String hfmItemCode;// HFM科目編碼
	private String hfmItemDesc;// HFM科目描述
	private String symbol;// 符號轉換

	@Column(name = "SOURCE_CORPORATE_CODE")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column(name = "SOURCE_ACCOUNT_CODE")
	public String getErpItemCode() {
		return erpItemCode;
	}

	@Column(name = "SOURCE_ACCOUNT_NAME")
	public String getErpItemDesc() {
		return erpItemDesc;
	}

	@Column(name = "TARGET_ACCOUNT_CODE")
	public String getHfmItemCode() {
		return hfmItemCode;
	}

	@Column(name = "TARGET_ACCOUNT_NAME")
	public String getHfmItemDesc() {
		return hfmItemDesc;
	}

	@Column(name="ATTRIBUTE1")
	public String getSymbol() {
		return symbol;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setErpItemCode(String erpItemCode) {
		this.erpItemCode = erpItemCode;
	}

	public void setErpItemDesc(String erpItemDesc) {
		this.erpItemDesc = erpItemDesc;
	}

	public void setHfmItemCode(String hfmItemCode) {
		this.hfmItemCode = hfmItemCode;
	}

	public void setHfmItemDesc(String hfmItemDesc) {
		this.hfmItemDesc = hfmItemDesc;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
