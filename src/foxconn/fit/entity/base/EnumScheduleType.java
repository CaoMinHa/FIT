package foxconn.fit.entity.base;

/**
 * 调度类型
 * @author 陈亮
 */
public enum EnumScheduleType {
	
	GeneralLedger("总账","gl_data_insert"),
	ARReceive("AR已收款","ar_data_insert"),
	ARTradeInvoice("AR交易额（發票）","ar_trans_data_insert"),
	APPayment("AP已付款","ap_data_insert"),
	APTradeInvoice("AP交易额（發票）","ap_trans_data_insert");
	
	private EnumScheduleType(String name,String procedureName){
		this.name = name;
		this.procedureName=procedureName;
	}
	
	private final String name;
	private final String procedureName;

	public String getName() {
		return name;
	}
	
	public String getProcedureName() {
		return procedureName;
	}
	public String getCode(){
		return this.name();
	}
	
}