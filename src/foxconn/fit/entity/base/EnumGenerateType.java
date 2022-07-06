package foxconn.fit.entity.base;

/**
 * 明细记录类型
 * @author 陈亮
 */
public enum EnumGenerateType {
	A("全自动"),
	M("全手工"),
	AM("半自动");
	
	private EnumGenerateType(String name){
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