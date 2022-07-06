package foxconn.fit.entity.base;

/**
 * 場景
 * @author 陈亮
 */
public enum EnumScenarios {
	
	Budget("預算"),
	Forecast("預測"),
	Actual("實際數");
	
	private EnumScenarios(String name){
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