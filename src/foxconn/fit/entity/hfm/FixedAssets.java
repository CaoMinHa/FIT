package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 固資
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Fixed_Assets")
public class FixedAssets extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String itemCode;// 科目編號
	private String itemDesc;// 科目描述
	private String beginBalance;// 期初餘額
	private String increaseAmount;// 增添金額
	private String disposeAmount;// 處分金額
	private String transferAmount;// 移轉金額
	private String endAmount;// 期末余額

	@Column(name = "corporation_Code")
	public String getCorporationCode() {
		return corporationCode;
	}

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getPeriod() {
		return period;
	}

	@Column(name = "item_Code")
	public String getItemCode() {
		return itemCode;
	}

	@Column(name = "item_Desc")
	public String getItemDesc() {
		return itemDesc;
	}

	@Column(name = "begin_Balance")
	public String getBeginBalance() {
		return beginBalance;
	}

	@Column(name = "increase_Amount")
	public String getIncreaseAmount() {
		return increaseAmount;
	}

	@Column(name = "dispose_Amount")
	public String getDisposeAmount() {
		return disposeAmount;
	}

	@Column(name = "transfer_Amount")
	public String getTransferAmount() {
		return transferAmount;
	}

	@Column(name = "end_Amount")
	public String getEndAmount() {
		return endAmount;
	}

	public void setCorporationCode(String corporationCode) {
		this.corporationCode = corporationCode;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public void setBeginBalance(String beginBalance) {
		this.beginBalance = beginBalance;
	}

	public void setIncreaseAmount(String increaseAmount) {
		this.increaseAmount = increaseAmount;
	}

	public void setDisposeAmount(String disposeAmount) {
		this.disposeAmount = disposeAmount;
	}

	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}

	public void setEndAmount(String endAmount) {
		this.endAmount = endAmount;
	}

}
