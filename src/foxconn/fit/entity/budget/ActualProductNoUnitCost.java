package foxconn.fit.entity.budget;

/**
 * 实际数產品料號單位成本
 * 
 * @author liangchen
 *
 */
public class ActualProductNoUnitCost {
	private String entity;// SBU_法人
	private String product;// 產品料號
	private String materialStandardCost;// 單位材料標準成本
	private String standardHours;// 單位標準工時
	private String manualStandardRate;// 單位人工標準費率
	private String manualCost;// 單位人工成本
	private String manufactureStandardRate;// 單位製造標準費率
	private String manufactureCost;// 單位製造成本
	private String unitCost;// 單位成本

	public String getEntity() {
		return entity;
	}

	public String getProduct() {
		return product;
	}

	public String getMaterialStandardCost() {
		return materialStandardCost;
	}

	public String getStandardHours() {
		return standardHours;
	}

	public String getManualStandardRate() {
		return manualStandardRate;
	}

	public String getManualCost() {
		return manualCost;
	}

	public String getManufactureStandardRate() {
		return manufactureStandardRate;
	}

	public String getManufactureCost() {
		return manufactureCost;
	}

	public String getUnitCost() {
		return unitCost;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setMaterialStandardCost(String materialStandardCost) {
		this.materialStandardCost = materialStandardCost;
	}

	public void setStandardHours(String standardHours) {
		this.standardHours = standardHours;
	}

	public void setManualStandardRate(String manualStandardRate) {
		this.manualStandardRate = manualStandardRate;
	}

	public void setManualCost(String manualCost) {
		this.manualCost = manualCost;
	}

	public void setManufactureStandardRate(String manufactureStandardRate) {
		this.manufactureStandardRate = manufactureStandardRate;
	}

	public void setManufactureCost(String manufactureCost) {
		this.manufactureCost = manufactureCost;
	}

	public void setUnitCost(String unitCost) {
		this.unitCost = unitCost;
	}

}
