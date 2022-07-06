package foxconn.fit.entity.hfm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 總賬
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_General_Ledger")
public class GeneralLedger extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String corporationCode;// 法人编码
	private String year;// 年
	private String period;// 期間
	private String itemCode;// 科目編號
	private String itemDesc;// 科目描述
	private String assetsState;// 資產損益別(1.資產負債2.損益)
	private String balanceState;// 正常餘額型態 (1.借餘/2.貸餘)
	private String beginBalance;// 期初餘額
	private String currDebitBalance;// 本期借方發生額
	private String currCreditBalance;// 本期貸方發生額
	private String endBalance;// 期末餘額

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

	@Column(name = "item_code")
	public String getItemCode() {
		return itemCode;
	}

	@Column(name = "item_Desc")
	public String getItemDesc() {
		return itemDesc;
	}

	@Column(name = "assets_State")
	public String getAssetsState() {
		return assetsState;
	}

	@Column(name = "balance_State")
	public String getBalanceState() {
		return balanceState;
	}

	@Column(name = "begin_Balance")
	public String getBeginBalance() {
		return beginBalance;
	}

	@Column(name = "curr_Debit_Balance")
	public String getCurrDebitBalance() {
		return currDebitBalance;
	}

	@Column(name = "curr_Credit_Balance")
	public String getCurrCreditBalance() {
		return currCreditBalance;
	}

	@Column(name = "end_Balance")
	public String getEndBalance() {
		return endBalance;
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

	public void setAssetsState(String assetsState) {
		this.assetsState = assetsState;
	}

	public void setBalanceState(String balanceState) {
		this.balanceState = balanceState;
	}

	public void setBeginBalance(String beginBalance) {
		this.beginBalance = beginBalance;
	}

	public void setCurrDebitBalance(String currDebitBalance) {
		this.currDebitBalance = currDebitBalance;
	}

	public void setCurrCreditBalance(String currCreditBalance) {
		this.currCreditBalance = currCreditBalance;
	}

	public void setEndBalance(String endBalance) {
		this.endBalance = endBalance;
	}

}
