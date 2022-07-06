package foxconn.fit.entity.base;

/**
 * 營收by產業版本
 * @author 陈亮
 */
public enum EnumVersion {
	
	T1("1","五年財測第1版"),
	T2("2","五年財測第2版"),
	T3("3","五年財測第3版"),
	T4("4","五年財測第4版"),
	T5("5","五年財測第5版"),
	T6("6","五年財測第6版"),
	T7("7","五年財測第7版"),
	T8("8","五年財測第8版");
	
	private EnumVersion(String value,String name){
		this.value=value;
		this.name = name;
	}
	
	private final String value;
	private final String name;

	public String getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode(){
		return this.name();
	}
	
}