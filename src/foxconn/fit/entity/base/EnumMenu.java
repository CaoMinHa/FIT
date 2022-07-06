package foxconn.fit.entity.base;

/**
 * 菜单
 * @author 陈亮
 */
public enum EnumMenu {
	poIntegration("數據上傳"),
	poIntegrationList("報表查詢"),
	poMix ("採購映射"),
	poRole  ("採購角色管理"),
	poTask ("採購任務"),
	poTaskList ("任務列表"),
	poUser("採購用户维护"),
	poEmail("通知窗口"),
	poEmailLog("通知日志"),
	poFlow("採購CD 目標CPO核准表"),
	rtMix("營收映射表"),
	rtIntegration("營收目標"),
	rgpIntegration("營收毛利"),
	revenueDetailActualNumber("營收明細實際數"),
	revenueDetailManual("營收明細手工處理"),
	channel("渠道映射"),
	customerNameSpecification("客戶名稱規範映射"),
	primaryAndSecondaryIndustry("主次產業對照檔"),
	productBCG("產品BCG映射"),
	region("区域映射"),
	saleClassification("銷售大類映射"),
	storeSBU("倉碼&SBU映射"),
	outsourceActualNumber("外包costdown收集"),
	revenueActualNumber("廠區營收收集"),
	takeawayActualNumber("外買外賣收集"),
	sbuMapping("新老SBU對照表"),
	customerMapping("客戶對應表"),
	revenueEstimate("營收預估by產業"),
	revenueTarget("營收目標by產業"),
	entitySBU("SBU組織架構查詢"),
	price("營收價格決定權"),
	legalEntityList("管報法人列表"),
	cct("CCT關聯交易"),
	sbuProfitAndLoss("SBU損益表抽取");


	private EnumMenu(String name){
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