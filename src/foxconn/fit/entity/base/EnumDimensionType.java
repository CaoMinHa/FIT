package foxconn.fit.entity.base;

/**
 * 维度类型
 * @author 陈亮
 */
public enum EnumDimensionType {
	Combine("最終客戶"),
	Customer("賬款客戶"),
	Entity("SBU"),
	Product("產品序列"),
	Segment("產業"),
	Currency("貨幣"),
	Years("年"),
	Project("项目");
	
	private EnumDimensionType(String name){
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