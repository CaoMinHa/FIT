package foxconn.fit.entity.base;

/**
 * 映射类别
 * @author 陈亮
 */
public enum EnumDimensionName {
	
	ACCOUNT("科目"),
	ENTITY("公司"),
	ICP("关联方"),
	UD("自定义");
	
	private EnumDimensionName(String name){
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