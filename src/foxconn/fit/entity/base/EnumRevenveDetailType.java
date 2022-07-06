package foxconn.fit.entity.base;

/**
 * 营收明细类型
 * @author 陈亮
 */
public enum EnumRevenveDetailType {
	
	v1("financial_report"),
	v2("management_report");
	
	private EnumRevenveDetailType(String name){
		this.name = name;
	}
	
	private final String name;

	public String getName() {
		return name;
	}
	
	public String getCode(){
		return this.name();
	}
	
}