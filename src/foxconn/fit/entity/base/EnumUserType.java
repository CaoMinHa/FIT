package foxconn.fit.entity.base;

/**
 * 用户类型
 * @author 陈亮
 */
public enum EnumUserType {
	
	Admin("管理员"),
	HFM("HFM"),
	BI("BI"),
	Budget("预算用户");
	
	private EnumUserType(String name){
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