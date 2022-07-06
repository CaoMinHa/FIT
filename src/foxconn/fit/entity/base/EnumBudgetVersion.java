package foxconn.fit.entity.base;

/**
 * 预算版本
 * @author 陈亮
 */
public enum EnumBudgetVersion {
	V00("工作版"),
	V1("年度預算第一版"),
	V2("年度預算第二版"),
	V3("年度預算第三版"),
	W1("W1"),
	W2("W2"),
	W3("W3"),
	W4("W4"),
	W5("W5"),
	W6("W6"),
	W7("W7"),
	W8("W8"),
	W9("W9"),
	W10("W10"),
	W11("W11"),
	W12("W12"),
	W13("W13"),
	W14("W14"),
	W15("W15"),
	W16("W16"),
	W17("W17"),
	W18("W18"),
	W19("W19"),
	W20("W20"),
	W21("W21"),
	W22("W22"),
	W23("W23"),
	W24("W24"),
	W25("W25"),
	W26("W26"),
	W27("W27"),
	W28("W28"),
	W29("W29"),
	W30("W30"),
	W31("W31"),
	W32("W32"),
	W33("W33"),
	W34("W34"),
	W35("W35"),
	W36("W36"),
	W37("W37"),
	W38("W38"),
	W39("W39"),
	W40("W40"),
	W41("W41"),
	W42("W42"),
	W43("W43"),
	W44("W44"),
	W45("W45"),
	W46("W46"),
	W47("W47"),
	W48("W48"),
	W49("W49"),
	W50("W50"),
	W51("W51"),
	W52("W52"),
	W53("W53"),
	W54("W54");
	
	private EnumBudgetVersion(String name){
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