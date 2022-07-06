package foxconn.fit.entity.budget;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 產品料號單位成本
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Product_No_Unit_Cost")
public class ProductNoUnitCost extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String entity;// SBU_法人
	private String product;// 產品料號
	private String materialStandardCost1;// 單位材料標準成本1月
	private String materialAdjustCost1;// 單位材料調整成本1月
	private String materialCost1;// 單位材料成本1月
	private String standardHours1;// 單位標準工時1月
	private String adjustHours1;// 單位調整工時1月
	private String hours1;// 單位工時1月
	private String manualStandardRate1;// 單位人工標準費率1月
	private String manualAdjustRate1;// 單位人工調整費率1月
	private String manualRate1;// 單位人工費率1月
	private String manualCost1;// 單位人工成本1月
	private String manufactureStandardRate1;// 單位製造標準費率1月
	private String manufactureAdjustRate1;// 單位製造調整費率1月
	private String manufactureRate1;// 單位製造費率1月
	private String manufactureCost1;// 單位製造成本1月
	private String unitCost1;// 單位成本1月

	private String materialStandardCost2;// 單位材料標準成本2月
	private String materialAdjustCost2;// 單位材料調整成本2月
	private String materialCost2;// 單位材料成本2月
	private String standardHours2;// 單位標準工時2月
	private String adjustHours2;// 單位調整工時2月
	private String hours2;// 單位工時2月
	private String manualStandardRate2;// 單位人工標準費率2月
	private String manualAdjustRate2;// 單位人工調整費率2月
	private String manualRate2;// 單位人工費率2月
	private String manualCost2;// 單位人工成本2月
	private String manufactureStandardRate2;// 單位製造標準費率2月
	private String manufactureAdjustRate2;// 單位製造調整費率2月
	private String manufactureRate2;// 單位製造費率2月
	private String manufactureCost2;// 單位製造成本2月
	private String unitCost2;// 單位成本2月

	private String materialStandardCost3;// 單位材料標準成本3月
	private String materialAdjustCost3;// 單位材料調整成本3月
	private String materialCost3;// 單位材料成本3月
	private String standardHours3;// 單位標準工時3月
	private String adjustHours3;// 單位調整工時3月
	private String hours3;// 單位工時3月
	private String manualStandardRate3;// 單位人工標準費率3月
	private String manualAdjustRate3;// 單位人工調整費率3月
	private String manualRate3;// 單位人工費率3月
	private String manualCost3;// 單位人工成本3月
	private String manufactureStandardRate3;// 單位製造標準費率3月
	private String manufactureAdjustRate3;// 單位製造調整費率3月
	private String manufactureRate3;// 單位製造費率3月
	private String manufactureCost3;// 單位製造成本3月
	private String unitCost3;// 單位成本3月

	private String materialStandardCost4;// 單位材料標準成本4月
	private String materialAdjustCost4;// 單位材料調整成本4月
	private String materialCost4;// 單位材料成本4月
	private String standardHours4;// 單位標準工時4月
	private String adjustHours4;// 單位調整工時4月
	private String hours4;// 單位工時4月
	private String manualStandardRate4;// 單位人工標準費率4月
	private String manualAdjustRate4;// 單位人工調整費率4月
	private String manualRate4;// 單位人工費率4月
	private String manualCost4;// 單位人工成本4月
	private String manufactureStandardRate4;// 單位製造標準費率4月
	private String manufactureAdjustRate4;// 單位製造調整費率4月
	private String manufactureRate4;// 單位製造費率4月
	private String manufactureCost4;// 單位製造成本4月
	private String unitCost4;// 單位成本4月

	private String materialStandardCost5;// 單位材料標準成本5月
	private String materialAdjustCost5;// 單位材料調整成本5月
	private String materialCost5;// 單位材料成本5月
	private String standardHours5;// 單位標準工時5月
	private String adjustHours5;// 單位調整工時5月
	private String hours5;// 單位工時5月
	private String manualStandardRate5;// 單位人工標準費率5月
	private String manualAdjustRate5;// 單位人工調整費率5月
	private String manualRate5;// 單位人工費率5月
	private String manualCost5;// 單位人工成本5月
	private String manufactureStandardRate5;// 單位製造標準費率5月
	private String manufactureAdjustRate5;// 單位製造調整費率5月
	private String manufactureRate5;// 單位製造費率5月
	private String manufactureCost5;// 單位製造成本5月
	private String unitCost5;// 單位成本5月

	private String materialStandardCost6;// 單位材料標準成本6月
	private String materialAdjustCost6;// 單位材料調整成本6月
	private String materialCost6;// 單位材料成本6月
	private String standardHours6;// 單位標準工時6月
	private String adjustHours6;// 單位調整工時6月
	private String hours6;// 單位工時6月
	private String manualStandardRate6;// 單位人工標準費率6月
	private String manualAdjustRate6;// 單位人工調整費率6月
	private String manualRate6;// 單位人工費率6月
	private String manualCost6;// 單位人工成本6月
	private String manufactureStandardRate6;// 單位製造標準費率6月
	private String manufactureAdjustRate6;// 單位製造調整費率6月
	private String manufactureRate6;// 單位製造費率6月
	private String manufactureCost6;// 單位製造成本6月
	private String unitCost6;// 單位成本6月

	private String materialStandardCost7;// 單位材料標準成本7月
	private String materialAdjustCost7;// 單位材料調整成本7月
	private String materialCost7;// 單位材料成本7月
	private String standardHours7;// 單位標準工時7月
	private String adjustHours7;// 單位調整工時7月
	private String hours7;// 單位工時7月
	private String manualStandardRate7;// 單位人工標準費率7月
	private String manualAdjustRate7;// 單位人工調整費率7月
	private String manualRate7;// 單位人工費率7月
	private String manualCost7;// 單位人工成本7月
	private String manufactureStandardRate7;// 單位製造標準費率7月
	private String manufactureAdjustRate7;// 單位製造調整費率7月
	private String manufactureRate7;// 單位製造費率7月
	private String manufactureCost7;// 單位製造成本7月
	private String unitCost7;// 單位成本7月

	private String materialStandardCost8;// 單位材料標準成本8月
	private String materialAdjustCost8;// 單位材料調整成本8月
	private String materialCost8;// 單位材料成本8月
	private String standardHours8;// 單位標準工時8月
	private String adjustHours8;// 單位調整工時8月
	private String hours8;// 單位工時8月
	private String manualStandardRate8;// 單位人工標準費率8月
	private String manualAdjustRate8;// 單位人工調整費率8月
	private String manualRate8;// 單位人工費率8月
	private String manualCost8;// 單位人工成本8月
	private String manufactureStandardRate8;// 單位製造標準費率8月
	private String manufactureAdjustRate8;// 單位製造調整費率8月
	private String manufactureRate8;// 單位製造費率8月
	private String manufactureCost8;// 單位製造成本8月
	private String unitCost8;// 單位成本8月

	private String materialStandardCost9;// 單位材料標準成本9月
	private String materialAdjustCost9;// 單位材料調整成本9月
	private String materialCost9;// 單位材料成本9月
	private String standardHours9;// 單位標準工時9月
	private String adjustHours9;// 單位調整工時9月
	private String hours9;// 單位工時9月
	private String manualStandardRate9;// 單位人工標準費率9月
	private String manualAdjustRate9;// 單位人工調整費率9月
	private String manualRate9;// 單位人工費率9月
	private String manualCost9;// 單位人工成本9月
	private String manufactureStandardRate9;// 單位製造標準費率9月
	private String manufactureAdjustRate9;// 單位製造調整費率9月
	private String manufactureRate9;// 單位製造費率9月
	private String manufactureCost9;// 單位製造成本9月
	private String unitCost9;// 單位成本9月

	private String materialStandardCost10;// 單位材料標準成本10月
	private String materialAdjustCost10;// 單位材料調整成本10月
	private String materialCost10;// 單位材料成本10月
	private String standardHours10;// 單位標準工時10月
	private String adjustHours10;// 單位調整工時10月
	private String hours10;// 單位工時10月
	private String manualStandardRate10;// 單位人工標準費率10月
	private String manualAdjustRate10;// 單位人工調整費率10月
	private String manualRate10;// 單位人工費率10月
	private String manualCost10;// 單位人工成本10月
	private String manufactureStandardRate10;// 單位製造標準費率10月
	private String manufactureAdjustRate10;// 單位製造調整費率10月
	private String manufactureRate10;// 單位製造費率10月
	private String manufactureCost10;// 單位製造成本10月
	private String unitCost10;// 單位成本10月

	private String materialStandardCost11;// 單位材料標準成本11月
	private String materialAdjustCost11;// 單位材料調整成本11月
	private String materialCost11;// 單位材料成本11月
	private String standardHours11;// 單位標準工時11月
	private String adjustHours11;// 單位調整工時11月
	private String hours11;// 單位工時11月
	private String manualStandardRate11;// 單位人工標準費率11月
	private String manualAdjustRate11;// 單位人工調整費率11月
	private String manualRate11;// 單位人工費率11月
	private String manualCost11;// 單位人工成本11月
	private String manufactureStandardRate11;// 單位製造標準費率11月
	private String manufactureAdjustRate11;// 單位製造調整費率11月
	private String manufactureRate11;// 單位製造費率11月
	private String manufactureCost11;// 單位製造成本11月
	private String unitCost11;// 單位成本11月

	private String materialStandardCost12;// 單位材料標準成本12月
	private String materialAdjustCost12;// 單位材料調整成本12月
	private String materialCost12;// 單位材料成本12月
	private String standardHours12;// 單位標準工時12月
	private String adjustHours12;// 單位調整工時12月
	private String hours12;// 單位工時12月
	private String manualStandardRate12;// 單位人工標準費率12月
	private String manualAdjustRate12;// 單位人工調整費率12月
	private String manualRate12;// 單位人工費率12月
	private String manualCost12;// 單位人工成本12月
	private String manufactureStandardRate12;// 單位製造標準費率12月
	private String manufactureAdjustRate12;// 單位製造調整費率12月
	private String manufactureRate12;// 單位製造費率12月
	private String manufactureCost12;// 單位製造成本12月
	private String unitCost12;// 單位成本12月

	private String materialStandardCostYear;// 單位材料標準成本全年
	private String materialAdjustCostYear;// 單位材料調整成本全年
	private String materialCostYear;// 單位材料成本全年
	private String standardHoursYear;// 單位標準工時全年
	private String adjustHoursYear;// 單位調整工時全年
	private String hoursYear;// 單位工時全年
	private String manualStandardRateYear;// 單位人工標準費率全年
	private String manualAdjustRateYear;// 單位人工調整費率全年
	private String manualRateYear;// 單位人工費率全年
	private String manualCostYear;// 單位人工成本全年
	private String manufactureStandardRateYear;// 單位製造標準費率全年
	private String manufactureAdjustRateYear;// 單位製造調整費率全年
	private String manufactureRateYear;// 單位製造費率全年
	private String manufactureCostYear;// 單位製造成本全年

	private String sbu;
	private String year;// 年
	private String scenarios;// 場景
	private String version;// 版本
	private String forecastId;//行ID


	@Column(name = "forecast_Id")
	public String getForecastId() {
		return forecastId;
	}

	public void setForecastId(String forecastId) {
		this.forecastId = forecastId;
	}

	public void setMaterialStandardCostYear(String materialStandardCostYear) {
		this.materialStandardCostYear = materialStandardCostYear;
	}

	public void setMaterialAdjustCostYear(String materialAdjustCostYear) {
		this.materialAdjustCostYear = materialAdjustCostYear;
	}

	public void setMaterialCostYear(String materialCostYear) {
		this.materialCostYear = materialCostYear;
	}

	public void setStandardHoursYear(String standardHoursYear) {
		this.standardHoursYear = standardHoursYear;
	}

	public void setAdjustHoursYear(String adjustHoursYear) {
		this.adjustHoursYear = adjustHoursYear;
	}

	public void setHoursYear(String hoursYear) {
		this.hoursYear = hoursYear;
	}

	public void setManualStandardRateYear(String manualStandardRateYear) {
		this.manualStandardRateYear = manualStandardRateYear;
	}

	public void setManualAdjustRateYear(String manualAdjustRateYear) {
		this.manualAdjustRateYear = manualAdjustRateYear;
	}

	public void setManualRateYear(String manualRateYear) {
		this.manualRateYear = manualRateYear;
	}

	public void setManualCostYear(String manualCostYear) {
		this.manualCostYear = manualCostYear;
	}

	public void setManufactureStandardRateYear(String manufactureStandardRateYear) {
		this.manufactureStandardRateYear = manufactureStandardRateYear;
	}

	public void setManufactureAdjustRateYear(String manufactureAdjustRateYear) {
		this.manufactureAdjustRateYear = manufactureAdjustRateYear;
	}

	public void setManufactureRateYear(String manufactureRateYear) {
		this.manufactureRateYear = manufactureRateYear;
	}

	public void setManufactureCostYear(String manufactureCostYear) {
		this.manufactureCostYear = manufactureCostYear;
	}

	public void setUnitCostYear(String unitCostYear) {
		this.unitCostYear = unitCostYear;
	}

	private String unitCostYear;// 單位成本全年



	@Column(name = "material_Standard_Cost_Year")
	public String getMaterialStandardCostYear() {
		return materialStandardCostYear;
	}

	@Column(name = "material_Adjust_Cost_Year")
	public String getMaterialAdjustCostYear() {
		return materialAdjustCostYear;
	}

	@Column(name = "material_Cost_Year")
	public String getMaterialCostYear() {
		return materialCostYear;
	}

	@Column(name = "standard_Hours_Year")
	public String getStandardHoursYear() {
		return standardHoursYear;
	}

	@Column(name = "adjust_Hours_Year")
	public String getAdjustHoursYear() {
		return adjustHoursYear;
	}

	@Column(name = "hours_Year")
	public String getHoursYear() {
		return hoursYear;
	}

	@Column(name = "manual_Standard_Rate_Year")
	public String getManualStandardRateYear() {
		return manualStandardRateYear;
	}

	@Column(name = "manual_Adjust_Rate_Year")
	public String getManualAdjustRateYear() {
		return manualAdjustRateYear;
	}

	@Column(name = "manual_Rate_Year")
	public String getManualRateYear() {
		return manualRateYear;
	}

	@Column(name = "manual_Cost_Year")
	public String getManualCostYear() {
		return manualCostYear;
	}

	@Column(name = "manufacture_Standard_Rate_Year")
	public String getManufactureStandardRateYear() {
		return manufactureStandardRateYear;
	}

	@Column(name = "manufacture_Adjust_Rate_Year")
	public String getManufactureAdjustRateYear() {
		return manufactureAdjustRateYear;
	}

	@Column(name = "manufacture_Rate_Year")
	public String getManufactureRateYear() {
		return manufactureRateYear;
	}

	@Column(name = "manufacture_Cost_Year")
	public String getManufactureCostYear() {
		return manufactureCostYear;
	}

	@Column(name = "unit_Cost_Year")
	public String getUnitCostYear() {
		return unitCostYear;
	}


	@Column
	public String getEntity() {
		return entity;
	}

	@Column
	public String getProduct() {
		return product;
	}

	@Column(name = "material_Standard_Cost1")
	public String getMaterialStandardCost1() {
		return materialStandardCost1;
	}

	@Column(name = "material_Adjust_Cost1")
	public String getMaterialAdjustCost1() {
		return materialAdjustCost1;
	}

	@Column(name = "material_Cost1")
	public String getMaterialCost1() {
		return materialCost1;
	}

	@Column(name = "standard_Hours1")
	public String getStandardHours1() {
		return standardHours1;
	}

	@Column(name = "adjust_Hours1")
	public String getAdjustHours1() {
		return adjustHours1;
	}

	@Column
	public String getHours1() {
		return hours1;
	}

	@Column(name = "manual_Standard_Rate1")
	public String getManualStandardRate1() {
		return manualStandardRate1;
	}

	@Column(name = "manual_Adjust_Rate1")
	public String getManualAdjustRate1() {
		return manualAdjustRate1;
	}

	@Column(name = "manual_Rate1")
	public String getManualRate1() {
		return manualRate1;
	}

	@Column(name = "manual_Cost1")
	public String getManualCost1() {
		return manualCost1;
	}

	@Column(name = "manufacture_Standard_Rate1")
	public String getManufactureStandardRate1() {
		return manufactureStandardRate1;
	}

	@Column(name = "manufacture_Adjust_Rate1")
	public String getManufactureAdjustRate1() {
		return manufactureAdjustRate1;
	}

	@Column(name = "manufacture_Rate1")
	public String getManufactureRate1() {
		return manufactureRate1;
	}

	@Column(name = "manufacture_Cost1")
	public String getManufactureCost1() {
		return manufactureCost1;
	}

	@Column(name = "unit_Cost1")
	public String getUnitCost1() {
		return unitCost1;
	}

	@Column(name = "material_Standard_Cost2")
	public String getMaterialStandardCost2() {
		return materialStandardCost2;
	}

	@Column(name = "material_Adjust_Cost2")
	public String getMaterialAdjustCost2() {
		return materialAdjustCost2;
	}

	@Column(name = "material_Cost2")
	public String getMaterialCost2() {
		return materialCost2;
	}

	@Column(name = "standard_Hours2")
	public String getStandardHours2() {
		return standardHours2;
	}

	@Column(name = "adjust_Hours2")
	public String getAdjustHours2() {
		return adjustHours2;
	}

	@Column
	public String getHours2() {
		return hours2;
	}

	@Column(name = "manual_Standard_Rate2")
	public String getManualStandardRate2() {
		return manualStandardRate2;
	}

	@Column(name = "manual_Adjust_Rate2")
	public String getManualAdjustRate2() {
		return manualAdjustRate2;
	}

	@Column(name = "manual_Rate2")
	public String getManualRate2() {
		return manualRate2;
	}

	@Column(name = "manual_Cost2")
	public String getManualCost2() {
		return manualCost2;
	}

	@Column(name = "manufacture_Standard_Rate2")
	public String getManufactureStandardRate2() {
		return manufactureStandardRate2;
	}

	@Column(name = "manufacture_Adjust_Rate2")
	public String getManufactureAdjustRate2() {
		return manufactureAdjustRate2;
	}

	@Column(name = "manufacture_Rate2")
	public String getManufactureRate2() {
		return manufactureRate2;
	}

	@Column(name = "manufacture_Cost2")
	public String getManufactureCost2() {
		return manufactureCost2;
	}

	@Column(name = "unit_Cost2")
	public String getUnitCost2() {
		return unitCost2;
	}

	@Column(name = "material_Standard_Cost3")
	public String getMaterialStandardCost3() {
		return materialStandardCost3;
	}

	@Column(name = "material_Adjust_Cost3")
	public String getMaterialAdjustCost3() {
		return materialAdjustCost3;
	}

	@Column(name = "material_Cost3")
	public String getMaterialCost3() {
		return materialCost3;
	}

	@Column(name = "standard_Hours3")
	public String getStandardHours3() {
		return standardHours3;
	}

	@Column(name = "adjust_Hours3")
	public String getAdjustHours3() {
		return adjustHours3;
	}

	@Column
	public String getHours3() {
		return hours3;
	}

	@Column(name = "manual_Standard_Rate3")
	public String getManualStandardRate3() {
		return manualStandardRate3;
	}

	@Column(name = "manual_Adjust_Rate3")
	public String getManualAdjustRate3() {
		return manualAdjustRate3;
	}

	@Column(name = "manual_Rate3")
	public String getManualRate3() {
		return manualRate3;
	}

	@Column(name = "manual_Cost3")
	public String getManualCost3() {
		return manualCost3;
	}

	@Column(name = "manufacture_Standard_Rate3")
	public String getManufactureStandardRate3() {
		return manufactureStandardRate3;
	}

	@Column(name = "manufacture_Adjust_Rate3")
	public String getManufactureAdjustRate3() {
		return manufactureAdjustRate3;
	}

	@Column(name = "manufacture_Rate3")
	public String getManufactureRate3() {
		return manufactureRate3;
	}

	@Column(name = "manufacture_Cost3")
	public String getManufactureCost3() {
		return manufactureCost3;
	}

	@Column(name = "unit_Cost3")
	public String getUnitCost3() {
		return unitCost3;
	}

	@Column(name = "material_Standard_Cost4")
	public String getMaterialStandardCost4() {
		return materialStandardCost4;
	}

	@Column(name = "material_Adjust_Cost4")
	public String getMaterialAdjustCost4() {
		return materialAdjustCost4;
	}

	@Column(name = "material_Cost4")
	public String getMaterialCost4() {
		return materialCost4;
	}

	@Column(name = "standard_Hours4")
	public String getStandardHours4() {
		return standardHours4;
	}

	@Column(name = "adjust_Hours4")
	public String getAdjustHours4() {
		return adjustHours4;
	}

	@Column
	public String getHours4() {
		return hours4;
	}

	@Column(name = "manual_Standard_Rate4")
	public String getManualStandardRate4() {
		return manualStandardRate4;
	}

	@Column(name = "manual_Adjust_Rate4")
	public String getManualAdjustRate4() {
		return manualAdjustRate4;
	}

	@Column(name = "manual_Rate4")
	public String getManualRate4() {
		return manualRate4;
	}

	@Column(name = "manual_Cost4")
	public String getManualCost4() {
		return manualCost4;
	}

	@Column(name = "manufacture_Standard_Rate4")
	public String getManufactureStandardRate4() {
		return manufactureStandardRate4;
	}

	@Column(name = "manufacture_Adjust_Rate4")
	public String getManufactureAdjustRate4() {
		return manufactureAdjustRate4;
	}

	@Column(name = "manufacture_Rate4")
	public String getManufactureRate4() {
		return manufactureRate4;
	}

	@Column(name = "manufacture_Cost4")
	public String getManufactureCost4() {
		return manufactureCost4;
	}

	@Column(name = "unit_Cost4")
	public String getUnitCost4() {
		return unitCost4;
	}

	@Column(name = "material_Standard_Cost5")
	public String getMaterialStandardCost5() {
		return materialStandardCost5;
	}

	@Column(name = "material_Adjust_Cost5")
	public String getMaterialAdjustCost5() {
		return materialAdjustCost5;
	}

	@Column(name = "material_Cost5")
	public String getMaterialCost5() {
		return materialCost5;
	}

	@Column(name = "standard_Hours5")
	public String getStandardHours5() {
		return standardHours5;
	}

	@Column(name = "adjust_Hours5")
	public String getAdjustHours5() {
		return adjustHours5;
	}

	@Column
	public String getHours5() {
		return hours5;
	}

	@Column(name = "manual_Standard_Rate5")
	public String getManualStandardRate5() {
		return manualStandardRate5;
	}

	@Column(name = "manual_Adjust_Rate5")
	public String getManualAdjustRate5() {
		return manualAdjustRate5;
	}

	@Column(name = "manual_Rate5")
	public String getManualRate5() {
		return manualRate5;
	}

	@Column(name = "manual_Cost5")
	public String getManualCost5() {
		return manualCost5;
	}

	@Column(name = "manufacture_Standard_Rate5")
	public String getManufactureStandardRate5() {
		return manufactureStandardRate5;
	}

	@Column(name = "manufacture_Adjust_Rate5")
	public String getManufactureAdjustRate5() {
		return manufactureAdjustRate5;
	}

	@Column(name = "manufacture_Rate5")
	public String getManufactureRate5() {
		return manufactureRate5;
	}

	@Column(name = "manufacture_Cost5")
	public String getManufactureCost5() {
		return manufactureCost5;
	}

	@Column(name = "unit_Cost5")
	public String getUnitCost5() {
		return unitCost5;
	}

	@Column(name = "material_Standard_Cost6")
	public String getMaterialStandardCost6() {
		return materialStandardCost6;
	}

	@Column(name = "material_Adjust_Cost6")
	public String getMaterialAdjustCost6() {
		return materialAdjustCost6;
	}

	@Column(name = "material_Cost6")
	public String getMaterialCost6() {
		return materialCost6;
	}

	@Column(name = "standard_Hours6")
	public String getStandardHours6() {
		return standardHours6;
	}

	@Column(name = "adjust_Hours6")
	public String getAdjustHours6() {
		return adjustHours6;
	}

	@Column
	public String getHours6() {
		return hours6;
	}

	@Column(name = "manual_Standard_Rate6")
	public String getManualStandardRate6() {
		return manualStandardRate6;
	}

	@Column(name = "manual_Adjust_Rate6")
	public String getManualAdjustRate6() {
		return manualAdjustRate6;
	}

	@Column(name = "manual_Rate6")
	public String getManualRate6() {
		return manualRate6;
	}

	@Column(name = "manual_Cost6")
	public String getManualCost6() {
		return manualCost6;
	}

	@Column(name = "manufacture_Standard_Rate6")
	public String getManufactureStandardRate6() {
		return manufactureStandardRate6;
	}

	@Column(name = "manufacture_Adjust_Rate6")
	public String getManufactureAdjustRate6() {
		return manufactureAdjustRate6;
	}

	@Column(name = "manufacture_Rate6")
	public String getManufactureRate6() {
		return manufactureRate6;
	}

	@Column(name = "manufacture_Cost6")
	public String getManufactureCost6() {
		return manufactureCost6;
	}

	@Column(name = "unit_Cost6")
	public String getUnitCost6() {
		return unitCost6;
	}

	@Column(name = "material_Standard_Cost7")
	public String getMaterialStandardCost7() {
		return materialStandardCost7;
	}

	@Column(name = "material_Adjust_Cost7")
	public String getMaterialAdjustCost7() {
		return materialAdjustCost7;
	}

	@Column(name = "material_Cost7")
	public String getMaterialCost7() {
		return materialCost7;
	}

	@Column(name = "standard_Hours7")
	public String getStandardHours7() {
		return standardHours7;
	}

	@Column(name = "adjust_Hours7")
	public String getAdjustHours7() {
		return adjustHours7;
	}

	@Column
	public String getHours7() {
		return hours7;
	}

	@Column(name = "manual_Standard_Rate7")
	public String getManualStandardRate7() {
		return manualStandardRate7;
	}

	@Column(name = "manual_Adjust_Rate7")
	public String getManualAdjustRate7() {
		return manualAdjustRate7;
	}

	@Column(name = "manual_Rate7")
	public String getManualRate7() {
		return manualRate7;
	}

	@Column(name = "manual_Cost7")
	public String getManualCost7() {
		return manualCost7;
	}

	@Column(name = "manufacture_Standard_Rate7")
	public String getManufactureStandardRate7() {
		return manufactureStandardRate7;
	}

	@Column(name = "manufacture_Adjust_Rate7")
	public String getManufactureAdjustRate7() {
		return manufactureAdjustRate7;
	}

	@Column(name = "manufacture_Rate7")
	public String getManufactureRate7() {
		return manufactureRate7;
	}

	@Column(name = "manufacture_Cost7")
	public String getManufactureCost7() {
		return manufactureCost7;
	}

	@Column(name = "unit_Cost7")
	public String getUnitCost7() {
		return unitCost7;
	}

	@Column(name = "material_Standard_Cost8")
	public String getMaterialStandardCost8() {
		return materialStandardCost8;
	}

	@Column(name = "material_Adjust_Cost8")
	public String getMaterialAdjustCost8() {
		return materialAdjustCost8;
	}

	@Column(name = "material_Cost8")
	public String getMaterialCost8() {
		return materialCost8;
	}

	@Column(name = "standard_Hours8")
	public String getStandardHours8() {
		return standardHours8;
	}

	@Column(name = "adjust_Hours8")
	public String getAdjustHours8() {
		return adjustHours8;
	}

	@Column
	public String getHours8() {
		return hours8;
	}

	@Column(name = "manual_Standard_Rate8")
	public String getManualStandardRate8() {
		return manualStandardRate8;
	}

	@Column(name = "manual_Adjust_Rate8")
	public String getManualAdjustRate8() {
		return manualAdjustRate8;
	}

	@Column(name = "manual_Rate8")
	public String getManualRate8() {
		return manualRate8;
	}

	@Column(name = "manual_Cost8")
	public String getManualCost8() {
		return manualCost8;
	}

	@Column(name = "manufacture_Standard_Rate8")
	public String getManufactureStandardRate8() {
		return manufactureStandardRate8;
	}

	@Column(name = "manufacture_Adjust_Rate8")
	public String getManufactureAdjustRate8() {
		return manufactureAdjustRate8;
	}

	@Column(name = "manufacture_Rate8")
	public String getManufactureRate8() {
		return manufactureRate8;
	}

	@Column(name = "manufacture_Cost8")
	public String getManufactureCost8() {
		return manufactureCost8;
	}

	@Column(name = "unit_Cost8")
	public String getUnitCost8() {
		return unitCost8;
	}

	@Column(name = "material_Standard_Cost9")
	public String getMaterialStandardCost9() {
		return materialStandardCost9;
	}

	@Column(name = "material_Adjust_Cost9")
	public String getMaterialAdjustCost9() {
		return materialAdjustCost9;
	}

	@Column(name = "material_Cost9")
	public String getMaterialCost9() {
		return materialCost9;
	}

	@Column(name = "standard_Hours9")
	public String getStandardHours9() {
		return standardHours9;
	}

	@Column(name = "adjust_Hours9")
	public String getAdjustHours9() {
		return adjustHours9;
	}

	@Column
	public String getHours9() {
		return hours9;
	}

	@Column(name = "manual_Standard_Rate9")
	public String getManualStandardRate9() {
		return manualStandardRate9;
	}

	@Column(name = "manual_Adjust_Rate9")
	public String getManualAdjustRate9() {
		return manualAdjustRate9;
	}

	@Column(name = "manual_Rate9")
	public String getManualRate9() {
		return manualRate9;
	}

	@Column(name = "manual_Cost9")
	public String getManualCost9() {
		return manualCost9;
	}

	@Column(name = "manufacture_Standard_Rate9")
	public String getManufactureStandardRate9() {
		return manufactureStandardRate9;
	}

	@Column(name = "manufacture_Adjust_Rate9")
	public String getManufactureAdjustRate9() {
		return manufactureAdjustRate9;
	}

	@Column(name = "manufacture_Rate9")
	public String getManufactureRate9() {
		return manufactureRate9;
	}

	@Column(name = "manufacture_Cost9")
	public String getManufactureCost9() {
		return manufactureCost9;
	}

	@Column(name = "unit_Cost9")
	public String getUnitCost9() {
		return unitCost9;
	}

	@Column(name = "material_Standard_Cost10")
	public String getMaterialStandardCost10() {
		return materialStandardCost10;
	}

	@Column(name = "material_Adjust_Cost10")
	public String getMaterialAdjustCost10() {
		return materialAdjustCost10;
	}

	@Column(name = "material_Cost10")
	public String getMaterialCost10() {
		return materialCost10;
	}

	@Column(name = "standard_Hours10")
	public String getStandardHours10() {
		return standardHours10;
	}

	@Column(name = "adjust_Hours10")
	public String getAdjustHours10() {
		return adjustHours10;
	}

	@Column
	public String getHours10() {
		return hours10;
	}

	@Column(name = "manual_Standard_Rate10")
	public String getManualStandardRate10() {
		return manualStandardRate10;
	}

	@Column(name = "manual_Adjust_Rate10")
	public String getManualAdjustRate10() {
		return manualAdjustRate10;
	}

	@Column(name = "manual_Rate10")
	public String getManualRate10() {
		return manualRate10;
	}

	@Column(name = "manual_Cost10")
	public String getManualCost10() {
		return manualCost10;
	}

	@Column(name = "manufacture_Standard_Rate10")
	public String getManufactureStandardRate10() {
		return manufactureStandardRate10;
	}

	@Column(name = "manufacture_Adjust_Rate10")
	public String getManufactureAdjustRate10() {
		return manufactureAdjustRate10;
	}

	@Column(name = "manufacture_Rate10")
	public String getManufactureRate10() {
		return manufactureRate10;
	}

	@Column(name = "manufacture_Cost10")
	public String getManufactureCost10() {
		return manufactureCost10;
	}

	@Column(name = "unit_Cost10")
	public String getUnitCost10() {
		return unitCost10;
	}

	@Column(name = "material_Standard_Cost11")
	public String getMaterialStandardCost11() {
		return materialStandardCost11;
	}

	@Column(name = "material_Adjust_Cost11")
	public String getMaterialAdjustCost11() {
		return materialAdjustCost11;
	}

	@Column(name = "material_Cost11")
	public String getMaterialCost11() {
		return materialCost11;
	}

	@Column(name = "standard_Hours11")
	public String getStandardHours11() {
		return standardHours11;
	}

	@Column(name = "adjust_Hours11")
	public String getAdjustHours11() {
		return adjustHours11;
	}

	@Column
	public String getHours11() {
		return hours11;
	}

	@Column(name = "manual_Standard_Rate11")
	public String getManualStandardRate11() {
		return manualStandardRate11;
	}

	@Column(name = "manual_Adjust_Rate11")
	public String getManualAdjustRate11() {
		return manualAdjustRate11;
	}

	@Column(name = "manual_Rate11")
	public String getManualRate11() {
		return manualRate11;
	}

	@Column(name = "manual_Cost11")
	public String getManualCost11() {
		return manualCost11;
	}

	@Column(name = "manufacture_Standard_Rate11")
	public String getManufactureStandardRate11() {
		return manufactureStandardRate11;
	}

	@Column(name = "manufacture_Adjust_Rate11")
	public String getManufactureAdjustRate11() {
		return manufactureAdjustRate11;
	}

	@Column(name = "manufacture_Rate11")
	public String getManufactureRate11() {
		return manufactureRate11;
	}

	@Column(name = "manufacture_Cost11")
	public String getManufactureCost11() {
		return manufactureCost11;
	}

	@Column(name = "unit_Cost11")
	public String getUnitCost11() {
		return unitCost11;
	}

	@Column(name = "material_Standard_Cost12")
	public String getMaterialStandardCost12() {
		return materialStandardCost12;
	}

	@Column(name = "material_Adjust_Cost12")
	public String getMaterialAdjustCost12() {
		return materialAdjustCost12;
	}

	@Column(name = "material_Cost12")
	public String getMaterialCost12() {
		return materialCost12;
	}

	@Column(name = "standard_Hours12")
	public String getStandardHours12() {
		return standardHours12;
	}

	@Column(name = "adjust_Hours12")
	public String getAdjustHours12() {
		return adjustHours12;
	}

	@Column
	public String getHours12() {
		return hours12;
	}

	@Column(name = "manual_Standard_Rate12")
	public String getManualStandardRate12() {
		return manualStandardRate12;
	}

	@Column(name = "manual_Adjust_Rate12")
	public String getManualAdjustRate12() {
		return manualAdjustRate12;
	}

	@Column(name = "manual_Rate12")
	public String getManualRate12() {
		return manualRate12;
	}

	@Column(name = "manual_Cost12")
	public String getManualCost12() {
		return manualCost12;
	}

	@Column(name = "manufacture_Standard_Rate12")
	public String getManufactureStandardRate12() {
		return manufactureStandardRate12;
	}

	@Column(name = "manufacture_Adjust_Rate12")
	public String getManufactureAdjustRate12() {
		return manufactureAdjustRate12;
	}

	@Column(name = "manufacture_Rate12")
	public String getManufactureRate12() {
		return manufactureRate12;
	}

	@Column(name = "manufacture_Cost12")
	public String getManufactureCost12() {
		return manufactureCost12;
	}

	@Column(name = "unit_Cost12")
	public String getUnitCost12() {
		return unitCost12;
	}

	@Column
	public String getSbu() {
		return sbu;
	}

	@Column
	public String getYear() {
		return year;
	}

	@Column
	public String getScenarios() {
		return scenarios;
	}

	@Column
	public String getVersion() {
		return version;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setMaterialStandardCost1(String materialStandardCost1) {
		this.materialStandardCost1 = materialStandardCost1;
	}

	public void setMaterialAdjustCost1(String materialAdjustCost1) {
		this.materialAdjustCost1 = materialAdjustCost1;
	}

	public void setMaterialCost1(String materialCost1) {
		this.materialCost1 = materialCost1;
	}

	public void setStandardHours1(String standardHours1) {
		this.standardHours1 = standardHours1;
	}

	public void setAdjustHours1(String adjustHours1) {
		this.adjustHours1 = adjustHours1;
	}

	public void setHours1(String hours1) {
		this.hours1 = hours1;
	}

	public void setManualStandardRate1(String manualStandardRate1) {
		this.manualStandardRate1 = manualStandardRate1;
	}

	public void setManualAdjustRate1(String manualAdjustRate1) {
		this.manualAdjustRate1 = manualAdjustRate1;
	}

	public void setManualRate1(String manualRate1) {
		this.manualRate1 = manualRate1;
	}

	public void setManualCost1(String manualCost1) {
		this.manualCost1 = manualCost1;
	}

	public void setManufactureStandardRate1(String manufactureStandardRate1) {
		this.manufactureStandardRate1 = manufactureStandardRate1;
	}

	public void setManufactureAdjustRate1(String manufactureAdjustRate1) {
		this.manufactureAdjustRate1 = manufactureAdjustRate1;
	}

	public void setManufactureRate1(String manufactureRate1) {
		this.manufactureRate1 = manufactureRate1;
	}

	public void setManufactureCost1(String manufactureCost1) {
		this.manufactureCost1 = manufactureCost1;
	}

	public void setUnitCost1(String unitCost1) {
		this.unitCost1 = unitCost1;
	}

	public void setMaterialStandardCost2(String materialStandardCost2) {
		this.materialStandardCost2 = materialStandardCost2;
	}

	public void setMaterialAdjustCost2(String materialAdjustCost2) {
		this.materialAdjustCost2 = materialAdjustCost2;
	}

	public void setMaterialCost2(String materialCost2) {
		this.materialCost2 = materialCost2;
	}

	public void setStandardHours2(String standardHours2) {
		this.standardHours2 = standardHours2;
	}

	public void setAdjustHours2(String adjustHours2) {
		this.adjustHours2 = adjustHours2;
	}

	public void setHours2(String hours2) {
		this.hours2 = hours2;
	}

	public void setManualStandardRate2(String manualStandardRate2) {
		this.manualStandardRate2 = manualStandardRate2;
	}

	public void setManualAdjustRate2(String manualAdjustRate2) {
		this.manualAdjustRate2 = manualAdjustRate2;
	}

	public void setManualRate2(String manualRate2) {
		this.manualRate2 = manualRate2;
	}

	public void setManualCost2(String manualCost2) {
		this.manualCost2 = manualCost2;
	}

	public void setManufactureStandardRate2(String manufactureStandardRate2) {
		this.manufactureStandardRate2 = manufactureStandardRate2;
	}

	public void setManufactureAdjustRate2(String manufactureAdjustRate2) {
		this.manufactureAdjustRate2 = manufactureAdjustRate2;
	}

	public void setManufactureRate2(String manufactureRate2) {
		this.manufactureRate2 = manufactureRate2;
	}

	public void setManufactureCost2(String manufactureCost2) {
		this.manufactureCost2 = manufactureCost2;
	}

	public void setUnitCost2(String unitCost2) {
		this.unitCost2 = unitCost2;
	}

	public void setMaterialStandardCost3(String materialStandardCost3) {
		this.materialStandardCost3 = materialStandardCost3;
	}

	public void setMaterialAdjustCost3(String materialAdjustCost3) {
		this.materialAdjustCost3 = materialAdjustCost3;
	}

	public void setMaterialCost3(String materialCost3) {
		this.materialCost3 = materialCost3;
	}

	public void setStandardHours3(String standardHours3) {
		this.standardHours3 = standardHours3;
	}

	public void setAdjustHours3(String adjustHours3) {
		this.adjustHours3 = adjustHours3;
	}

	public void setHours3(String hours3) {
		this.hours3 = hours3;
	}

	public void setManualStandardRate3(String manualStandardRate3) {
		this.manualStandardRate3 = manualStandardRate3;
	}

	public void setManualAdjustRate3(String manualAdjustRate3) {
		this.manualAdjustRate3 = manualAdjustRate3;
	}

	public void setManualRate3(String manualRate3) {
		this.manualRate3 = manualRate3;
	}

	public void setManualCost3(String manualCost3) {
		this.manualCost3 = manualCost3;
	}

	public void setManufactureStandardRate3(String manufactureStandardRate3) {
		this.manufactureStandardRate3 = manufactureStandardRate3;
	}

	public void setManufactureAdjustRate3(String manufactureAdjustRate3) {
		this.manufactureAdjustRate3 = manufactureAdjustRate3;
	}

	public void setManufactureRate3(String manufactureRate3) {
		this.manufactureRate3 = manufactureRate3;
	}

	public void setManufactureCost3(String manufactureCost3) {
		this.manufactureCost3 = manufactureCost3;
	}

	public void setUnitCost3(String unitCost3) {
		this.unitCost3 = unitCost3;
	}

	public void setMaterialStandardCost4(String materialStandardCost4) {
		this.materialStandardCost4 = materialStandardCost4;
	}

	public void setMaterialAdjustCost4(String materialAdjustCost4) {
		this.materialAdjustCost4 = materialAdjustCost4;
	}

	public void setMaterialCost4(String materialCost4) {
		this.materialCost4 = materialCost4;
	}

	public void setStandardHours4(String standardHours4) {
		this.standardHours4 = standardHours4;
	}

	public void setAdjustHours4(String adjustHours4) {
		this.adjustHours4 = adjustHours4;
	}

	public void setHours4(String hours4) {
		this.hours4 = hours4;
	}

	public void setManualStandardRate4(String manualStandardRate4) {
		this.manualStandardRate4 = manualStandardRate4;
	}

	public void setManualAdjustRate4(String manualAdjustRate4) {
		this.manualAdjustRate4 = manualAdjustRate4;
	}

	public void setManualRate4(String manualRate4) {
		this.manualRate4 = manualRate4;
	}

	public void setManualCost4(String manualCost4) {
		this.manualCost4 = manualCost4;
	}

	public void setManufactureStandardRate4(String manufactureStandardRate4) {
		this.manufactureStandardRate4 = manufactureStandardRate4;
	}

	public void setManufactureAdjustRate4(String manufactureAdjustRate4) {
		this.manufactureAdjustRate4 = manufactureAdjustRate4;
	}

	public void setManufactureRate4(String manufactureRate4) {
		this.manufactureRate4 = manufactureRate4;
	}

	public void setManufactureCost4(String manufactureCost4) {
		this.manufactureCost4 = manufactureCost4;
	}

	public void setUnitCost4(String unitCost4) {
		this.unitCost4 = unitCost4;
	}

	public void setMaterialStandardCost5(String materialStandardCost5) {
		this.materialStandardCost5 = materialStandardCost5;
	}

	public void setMaterialAdjustCost5(String materialAdjustCost5) {
		this.materialAdjustCost5 = materialAdjustCost5;
	}

	public void setMaterialCost5(String materialCost5) {
		this.materialCost5 = materialCost5;
	}

	public void setStandardHours5(String standardHours5) {
		this.standardHours5 = standardHours5;
	}

	public void setAdjustHours5(String adjustHours5) {
		this.adjustHours5 = adjustHours5;
	}

	public void setHours5(String hours5) {
		this.hours5 = hours5;
	}

	public void setManualStandardRate5(String manualStandardRate5) {
		this.manualStandardRate5 = manualStandardRate5;
	}

	public void setManualAdjustRate5(String manualAdjustRate5) {
		this.manualAdjustRate5 = manualAdjustRate5;
	}

	public void setManualRate5(String manualRate5) {
		this.manualRate5 = manualRate5;
	}

	public void setManualCost5(String manualCost5) {
		this.manualCost5 = manualCost5;
	}

	public void setManufactureStandardRate5(String manufactureStandardRate5) {
		this.manufactureStandardRate5 = manufactureStandardRate5;
	}

	public void setManufactureAdjustRate5(String manufactureAdjustRate5) {
		this.manufactureAdjustRate5 = manufactureAdjustRate5;
	}

	public void setManufactureRate5(String manufactureRate5) {
		this.manufactureRate5 = manufactureRate5;
	}

	public void setManufactureCost5(String manufactureCost5) {
		this.manufactureCost5 = manufactureCost5;
	}

	public void setUnitCost5(String unitCost5) {
		this.unitCost5 = unitCost5;
	}

	public void setMaterialStandardCost6(String materialStandardCost6) {
		this.materialStandardCost6 = materialStandardCost6;
	}

	public void setMaterialAdjustCost6(String materialAdjustCost6) {
		this.materialAdjustCost6 = materialAdjustCost6;
	}

	public void setMaterialCost6(String materialCost6) {
		this.materialCost6 = materialCost6;
	}

	public void setStandardHours6(String standardHours6) {
		this.standardHours6 = standardHours6;
	}

	public void setAdjustHours6(String adjustHours6) {
		this.adjustHours6 = adjustHours6;
	}

	public void setHours6(String hours6) {
		this.hours6 = hours6;
	}

	public void setManualStandardRate6(String manualStandardRate6) {
		this.manualStandardRate6 = manualStandardRate6;
	}

	public void setManualAdjustRate6(String manualAdjustRate6) {
		this.manualAdjustRate6 = manualAdjustRate6;
	}

	public void setManualRate6(String manualRate6) {
		this.manualRate6 = manualRate6;
	}

	public void setManualCost6(String manualCost6) {
		this.manualCost6 = manualCost6;
	}

	public void setManufactureStandardRate6(String manufactureStandardRate6) {
		this.manufactureStandardRate6 = manufactureStandardRate6;
	}

	public void setManufactureAdjustRate6(String manufactureAdjustRate6) {
		this.manufactureAdjustRate6 = manufactureAdjustRate6;
	}

	public void setManufactureRate6(String manufactureRate6) {
		this.manufactureRate6 = manufactureRate6;
	}

	public void setManufactureCost6(String manufactureCost6) {
		this.manufactureCost6 = manufactureCost6;
	}

	public void setUnitCost6(String unitCost6) {
		this.unitCost6 = unitCost6;
	}

	public void setMaterialStandardCost7(String materialStandardCost7) {
		this.materialStandardCost7 = materialStandardCost7;
	}

	public void setMaterialAdjustCost7(String materialAdjustCost7) {
		this.materialAdjustCost7 = materialAdjustCost7;
	}

	public void setMaterialCost7(String materialCost7) {
		this.materialCost7 = materialCost7;
	}

	public void setStandardHours7(String standardHours7) {
		this.standardHours7 = standardHours7;
	}

	public void setAdjustHours7(String adjustHours7) {
		this.adjustHours7 = adjustHours7;
	}

	public void setHours7(String hours7) {
		this.hours7 = hours7;
	}

	public void setManualStandardRate7(String manualStandardRate7) {
		this.manualStandardRate7 = manualStandardRate7;
	}

	public void setManualAdjustRate7(String manualAdjustRate7) {
		this.manualAdjustRate7 = manualAdjustRate7;
	}

	public void setManualRate7(String manualRate7) {
		this.manualRate7 = manualRate7;
	}

	public void setManualCost7(String manualCost7) {
		this.manualCost7 = manualCost7;
	}

	public void setManufactureStandardRate7(String manufactureStandardRate7) {
		this.manufactureStandardRate7 = manufactureStandardRate7;
	}

	public void setManufactureAdjustRate7(String manufactureAdjustRate7) {
		this.manufactureAdjustRate7 = manufactureAdjustRate7;
	}

	public void setManufactureRate7(String manufactureRate7) {
		this.manufactureRate7 = manufactureRate7;
	}

	public void setManufactureCost7(String manufactureCost7) {
		this.manufactureCost7 = manufactureCost7;
	}

	public void setUnitCost7(String unitCost7) {
		this.unitCost7 = unitCost7;
	}

	public void setMaterialStandardCost8(String materialStandardCost8) {
		this.materialStandardCost8 = materialStandardCost8;
	}

	public void setMaterialAdjustCost8(String materialAdjustCost8) {
		this.materialAdjustCost8 = materialAdjustCost8;
	}

	public void setMaterialCost8(String materialCost8) {
		this.materialCost8 = materialCost8;
	}

	public void setStandardHours8(String standardHours8) {
		this.standardHours8 = standardHours8;
	}

	public void setAdjustHours8(String adjustHours8) {
		this.adjustHours8 = adjustHours8;
	}

	public void setHours8(String hours8) {
		this.hours8 = hours8;
	}

	public void setManualStandardRate8(String manualStandardRate8) {
		this.manualStandardRate8 = manualStandardRate8;
	}

	public void setManualAdjustRate8(String manualAdjustRate8) {
		this.manualAdjustRate8 = manualAdjustRate8;
	}

	public void setManualRate8(String manualRate8) {
		this.manualRate8 = manualRate8;
	}

	public void setManualCost8(String manualCost8) {
		this.manualCost8 = manualCost8;
	}

	public void setManufactureStandardRate8(String manufactureStandardRate8) {
		this.manufactureStandardRate8 = manufactureStandardRate8;
	}

	public void setManufactureAdjustRate8(String manufactureAdjustRate8) {
		this.manufactureAdjustRate8 = manufactureAdjustRate8;
	}

	public void setManufactureRate8(String manufactureRate8) {
		this.manufactureRate8 = manufactureRate8;
	}

	public void setManufactureCost8(String manufactureCost8) {
		this.manufactureCost8 = manufactureCost8;
	}

	public void setUnitCost8(String unitCost8) {
		this.unitCost8 = unitCost8;
	}

	public void setMaterialStandardCost9(String materialStandardCost9) {
		this.materialStandardCost9 = materialStandardCost9;
	}

	public void setMaterialAdjustCost9(String materialAdjustCost9) {
		this.materialAdjustCost9 = materialAdjustCost9;
	}

	public void setMaterialCost9(String materialCost9) {
		this.materialCost9 = materialCost9;
	}

	public void setStandardHours9(String standardHours9) {
		this.standardHours9 = standardHours9;
	}

	public void setAdjustHours9(String adjustHours9) {
		this.adjustHours9 = adjustHours9;
	}

	public void setHours9(String hours9) {
		this.hours9 = hours9;
	}

	public void setManualStandardRate9(String manualStandardRate9) {
		this.manualStandardRate9 = manualStandardRate9;
	}

	public void setManualAdjustRate9(String manualAdjustRate9) {
		this.manualAdjustRate9 = manualAdjustRate9;
	}

	public void setManualRate9(String manualRate9) {
		this.manualRate9 = manualRate9;
	}

	public void setManualCost9(String manualCost9) {
		this.manualCost9 = manualCost9;
	}

	public void setManufactureStandardRate9(String manufactureStandardRate9) {
		this.manufactureStandardRate9 = manufactureStandardRate9;
	}

	public void setManufactureAdjustRate9(String manufactureAdjustRate9) {
		this.manufactureAdjustRate9 = manufactureAdjustRate9;
	}

	public void setManufactureRate9(String manufactureRate9) {
		this.manufactureRate9 = manufactureRate9;
	}

	public void setManufactureCost9(String manufactureCost9) {
		this.manufactureCost9 = manufactureCost9;
	}

	public void setUnitCost9(String unitCost9) {
		this.unitCost9 = unitCost9;
	}

	public void setMaterialStandardCost10(String materialStandardCost10) {
		this.materialStandardCost10 = materialStandardCost10;
	}

	public void setMaterialAdjustCost10(String materialAdjustCost10) {
		this.materialAdjustCost10 = materialAdjustCost10;
	}

	public void setMaterialCost10(String materialCost10) {
		this.materialCost10 = materialCost10;
	}

	public void setStandardHours10(String standardHours10) {
		this.standardHours10 = standardHours10;
	}

	public void setAdjustHours10(String adjustHours10) {
		this.adjustHours10 = adjustHours10;
	}

	public void setHours10(String hours10) {
		this.hours10 = hours10;
	}

	public void setManualStandardRate10(String manualStandardRate10) {
		this.manualStandardRate10 = manualStandardRate10;
	}

	public void setManualAdjustRate10(String manualAdjustRate10) {
		this.manualAdjustRate10 = manualAdjustRate10;
	}

	public void setManualRate10(String manualRate10) {
		this.manualRate10 = manualRate10;
	}

	public void setManualCost10(String manualCost10) {
		this.manualCost10 = manualCost10;
	}

	public void setManufactureStandardRate10(String manufactureStandardRate10) {
		this.manufactureStandardRate10 = manufactureStandardRate10;
	}

	public void setManufactureAdjustRate10(String manufactureAdjustRate10) {
		this.manufactureAdjustRate10 = manufactureAdjustRate10;
	}

	public void setManufactureRate10(String manufactureRate10) {
		this.manufactureRate10 = manufactureRate10;
	}

	public void setManufactureCost10(String manufactureCost10) {
		this.manufactureCost10 = manufactureCost10;
	}

	public void setUnitCost10(String unitCost10) {
		this.unitCost10 = unitCost10;
	}

	public void setMaterialStandardCost11(String materialStandardCost11) {
		this.materialStandardCost11 = materialStandardCost11;
	}

	public void setMaterialAdjustCost11(String materialAdjustCost11) {
		this.materialAdjustCost11 = materialAdjustCost11;
	}

	public void setMaterialCost11(String materialCost11) {
		this.materialCost11 = materialCost11;
	}

	public void setStandardHours11(String standardHours11) {
		this.standardHours11 = standardHours11;
	}

	public void setAdjustHours11(String adjustHours11) {
		this.adjustHours11 = adjustHours11;
	}

	public void setHours11(String hours11) {
		this.hours11 = hours11;
	}

	public void setManualStandardRate11(String manualStandardRate11) {
		this.manualStandardRate11 = manualStandardRate11;
	}

	public void setManualAdjustRate11(String manualAdjustRate11) {
		this.manualAdjustRate11 = manualAdjustRate11;
	}

	public void setManualRate11(String manualRate11) {
		this.manualRate11 = manualRate11;
	}

	public void setManualCost11(String manualCost11) {
		this.manualCost11 = manualCost11;
	}

	public void setManufactureStandardRate11(String manufactureStandardRate11) {
		this.manufactureStandardRate11 = manufactureStandardRate11;
	}

	public void setManufactureAdjustRate11(String manufactureAdjustRate11) {
		this.manufactureAdjustRate11 = manufactureAdjustRate11;
	}

	public void setManufactureRate11(String manufactureRate11) {
		this.manufactureRate11 = manufactureRate11;
	}

	public void setManufactureCost11(String manufactureCost11) {
		this.manufactureCost11 = manufactureCost11;
	}

	public void setUnitCost11(String unitCost11) {
		this.unitCost11 = unitCost11;
	}

	public void setMaterialStandardCost12(String materialStandardCost12) {
		this.materialStandardCost12 = materialStandardCost12;
	}

	public void setMaterialAdjustCost12(String materialAdjustCost12) {
		this.materialAdjustCost12 = materialAdjustCost12;
	}

	public void setMaterialCost12(String materialCost12) {
		this.materialCost12 = materialCost12;
	}

	public void setStandardHours12(String standardHours12) {
		this.standardHours12 = standardHours12;
	}

	public void setAdjustHours12(String adjustHours12) {
		this.adjustHours12 = adjustHours12;
	}

	public void setHours12(String hours12) {
		this.hours12 = hours12;
	}

	public void setManualStandardRate12(String manualStandardRate12) {
		this.manualStandardRate12 = manualStandardRate12;
	}

	public void setManualAdjustRate12(String manualAdjustRate12) {
		this.manualAdjustRate12 = manualAdjustRate12;
	}

	public void setManualRate12(String manualRate12) {
		this.manualRate12 = manualRate12;
	}

	public void setManualCost12(String manualCost12) {
		this.manualCost12 = manualCost12;
	}

	public void setManufactureStandardRate12(String manufactureStandardRate12) {
		this.manufactureStandardRate12 = manufactureStandardRate12;
	}

	public void setManufactureAdjustRate12(String manufactureAdjustRate12) {
		this.manufactureAdjustRate12 = manufactureAdjustRate12;
	}

	public void setManufactureRate12(String manufactureRate12) {
		this.manufactureRate12 = manufactureRate12;
	}

	public void setManufactureCost12(String manufactureCost12) {
		this.manufactureCost12 = manufactureCost12;
	}

	public void setUnitCost12(String unitCost12) {
		this.unitCost12 = unitCost12;
	}

	public void setSbu(String sbu) {
		this.sbu = sbu;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setScenarios(String scenarios) {
		this.scenarios = scenarios;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
