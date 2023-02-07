package foxconn.fit.entity.budget;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maggao
 */
@Entity
@Table(name = "FIT_FORECAST_SALES_COST")
public class ForecastSalesCost implements Serializable {
	@Id
	@Column(name = "ID",nullable = true)
	private String id;
	private String year;// 年
	private String version;// 版本
	private String entity;//SBU_法人
	private String product;// 產品系列
	//銷售數量
	@Column(name = "sales_quantity1")
	private String salesQuantity1;
	@Column(name = "sales_quantity2")
	private String salesQuantity2;
	@Column(name = "sales_quantity3")
	private String salesQuantity3;
	@Column(name = "sales_quantity4")
	private String salesQuantity4;
	@Column(name = "sales_quantity5")
	private String salesQuantity5;
	@Column(name = "sales_quantity6")
	private String salesQuantity6;
	@Column(name = "sales_quantity7")
	private String salesQuantity7;
	@Column(name = "sales_quantity8")
	private String salesQuantity8;
	@Column(name = "sales_quantity9")
	private String salesQuantity9;
	@Column(name = "sales_quantity10")
	private String salesQuantity10;
	@Column(name = "sales_quantity11")
	private String salesQuantity11;
	@Column(name = "sales_quantity12")
	private String salesQuantity12;
	//材料成本
	@Column(name = "material_cost1")
	private String materialCost1;
	@Column(name = "material_cost2")
	private String materialCost2;
	@Column(name = "material_cost3")
	private String materialCost3;
	@Column(name = "material_cost4")
	private String materialCost4;
	@Column(name = "material_cost5")
	private String materialCost5;
	@Column(name = "material_cost6")
	private String materialCost6;
	@Column(name = "material_cost7")
	private String materialCost7;
	@Column(name = "material_cost8")
	private String materialCost8;
	@Column(name = "material_cost9")
	private String materialCost9;
	@Column(name = "material_cost10")
	private String materialCost10;
	@Column(name = "material_cost11")
	private String materialCost11;
	@Column(name = "material_cost12")
	private String materialCost12;
	//	人工成本
	@Column(name = "labor_cost1")
	private String laborCost1;
	@Column(name = "labor_cost2")
	private String laborCost2;
	@Column(name = "labor_cost3")
	private String laborCost3;
	@Column(name = "labor_cost4")
	private String laborCost4;
	@Column(name = "labor_cost5")
	private String laborCost5;
	@Column(name = "labor_cost6")
	private String laborCost6;
	@Column(name = "labor_cost7")
	private String laborCost7;
	@Column(name = "labor_cost8")
	private String laborCost8;
	@Column(name = "labor_cost9")
	private String laborCost9;
	@Column(name = "labor_cost10")
	private String laborCost10;
	@Column(name = "labor_cost11")
	private String laborCost11;
	@Column(name = "labor_cost12")
	private String laborCost12;
	//	製造費用
	@Column(name = "manufacture_cost1")
	private String manufactureCost1;
	@Column(name = "manufacture_cost2")
	private String manufactureCost2;
	@Column(name = "manufacture_cost3")
	private String manufactureCost3;
	@Column(name = "manufacture_cost4")
	private String manufactureCost4;
	@Column(name = "manufacture_cost5")
	private String manufactureCost5;
	@Column(name = "manufacture_cost6")
	private String manufactureCost6;
	@Column(name = "manufacture_cost7")
	private String manufactureCost7;
	@Column(name = "manufacture_cost8")
	private String manufactureCost8;
	@Column(name = "manufacture_cost9")
	private String manufactureCost9;
	@Column(name = "manufacture_cost10")
	private String manufactureCost10;
	@Column(name = "manufacture_cost11")
	private String manufactureCost11;
	@Column(name = "manufacture_cost12")
	private String manufactureCost12;
	@Column(name = "create_name")
	private String createName;//數據創建人
	@Column(name = "create_date")
	private Date createDate;//數據創建時間
	@Column(name = "version_name")
	private String versionName;//版本存檔人
	@Column(name = "version_date")
	private Date versionDate;//版本存檔時間
	@Column(name = "product_no")
	private String productNo;// 產品料號
	@Column(name = "make_entity",nullable = false)
	private String makeEntity;// SBU_製造法人
	@Column(name = "trade_type")
	private String tradeType;// 交易類型
	private String industry;// 次產業
	@Column(name = "main_business")
	private String mainBusiness;//MAIN_BUSINESS
	private String three;//3+3
	@Column(name = "loan_customer")
	private String loanCustomer;// 賬款客戶
	@Column(name = "end_customer")
	private String endCustomer;// 最終客戶

	//單位材料成本
	@Column(name = "unit_material1")
	private String unitMaterial1;
	@Column(name = "unit_material2")
	private String unitMaterial2;
	@Column(name = "unit_material3")
	private String unitMaterial3;
	@Column(name = "unit_material4")
	private String unitMaterial4;
	@Column(name = "unit_material5")
	private String unitMaterial5;
	@Column(name = "unit_material6")
	private String unitMaterial6;
	@Column(name = "unit_material7")
	private String unitMaterial7;
	@Column(name = "unit_material8")
	private String unitMaterial8;
	@Column(name = "unit_material9")
	private String unitMaterial9;
	@Column(name = "unit_material10")
	private String unitMaterial10;
	@Column(name = "unit_material11")
	private String unitMaterial11;
	@Column(name = "unit_material12")
	private String unitMaterial12;

	//單位人工成本
	@Column(name = "unit_labor1")
	private String unitLabor1;
	@Column(name = "unit_labor2")
	private String unitLabor2;
	@Column(name = "unit_labor3")
	private String unitLabor3;
	@Column(name = "unit_labor4")
	private String unitLabor4;
	@Column(name = "unit_labor5")
	private String unitLabor5;
	@Column(name = "unit_labor6")
	private String unitLabor6;
	@Column(name = "unit_labor7")
	private String unitLabor7;
	@Column(name = "unit_labor8")
	private String unitLabor8;
	@Column(name = "unit_labor9")
	private String unitLabor9;
	@Column(name = "unit_labor10")
	private String unitLabor10;
	@Column(name = "unit_labor11")
	private String unitLabor11;
	@Column(name = "unit_labor12")
	private String unitLabor12;

	//單位製造費用
	@Column(name = "unit_manufacture1")
	private String unitManufacture1;
	@Column(name = "unit_manufacture2")
	private String unitManufacture2;
	@Column(name = "unit_manufacture3")
	private String unitManufacture3;
	@Column(name = "unit_manufacture4")
	private String unitManufacture4;
	@Column(name = "unit_manufacture5")
	private String unitManufacture5;
	@Column(name = "unit_manufacture6")
	private String unitManufacture6;
	@Column(name = "unit_manufacture7")
	private String unitManufacture7;
	@Column(name = "unit_manufacture8")
	private String unitManufacture8;
	@Column(name = "unit_manufacture9")
	private String unitManufacture9;
	@Column(name = "unit_manufacture10")
	private String unitManufacture10;
	@Column(name = "unit_manufacture11")
	private String unitManufacture11;
	@Column(name = "unit_manufacture12")
	private String unitManufacture12;

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getMainBusiness() {
		return mainBusiness;
	}

	public void setMainBusiness(String mainBusiness) {
		this.mainBusiness = mainBusiness;
	}

	public String getThree() {
		return three;
	}

	public void setThree(String three) {
		this.three = three;
	}

	public String getLoanCustomer() {
		return loanCustomer;
	}

	public void setLoanCustomer(String loanCustomer) {
		this.loanCustomer = loanCustomer;
	}

	public String getEndCustomer() {
		return endCustomer;
	}

	public void setEndCustomer(String endCustomer) {
		this.endCustomer = endCustomer;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getMakeEntity() {
		return makeEntity;
	}

	public void setMakeEntity(String makeEntity) {
		this.makeEntity = makeEntity;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getSalesQuantity1() {
		return salesQuantity1;
	}

	public void setSalesQuantity1(String salesQuantity1) {
		this.salesQuantity1 = salesQuantity1;
	}

	public String getSalesQuantity2() {
		return salesQuantity2;
	}

	public void setSalesQuantity2(String salesQuantity2) {
		this.salesQuantity2 = salesQuantity2;
	}

	public String getSalesQuantity3() {
		return salesQuantity3;
	}

	public void setSalesQuantity3(String salesQuantity3) {
		this.salesQuantity3 = salesQuantity3;
	}

	public String getSalesQuantity4() {
		return salesQuantity4;
	}

	public void setSalesQuantity4(String salesQuantity4) {
		this.salesQuantity4 = salesQuantity4;
	}

	public String getSalesQuantity5() {
		return salesQuantity5;
	}

	public void setSalesQuantity5(String salesQuantity5) {
		this.salesQuantity5 = salesQuantity5;
	}

	public String getSalesQuantity6() {
		return salesQuantity6;
	}

	public void setSalesQuantity6(String salesQuantity6) {
		this.salesQuantity6 = salesQuantity6;
	}

	public String getSalesQuantity7() {
		return salesQuantity7;
	}

	public void setSalesQuantity7(String salesQuantity7) {
		this.salesQuantity7 = salesQuantity7;
	}

	public String getSalesQuantity8() {
		return salesQuantity8;
	}

	public void setSalesQuantity8(String salesQuantity8) {
		this.salesQuantity8 = salesQuantity8;
	}

	public String getSalesQuantity9() {
		return salesQuantity9;
	}

	public void setSalesQuantity9(String salesQuantity9) {
		this.salesQuantity9 = salesQuantity9;
	}

	public String getSalesQuantity10() {
		return salesQuantity10;
	}

	public void setSalesQuantity10(String salesQuantity10) {
		this.salesQuantity10 = salesQuantity10;
	}

	public String getSalesQuantity11() {
		return salesQuantity11;
	}

	public void setSalesQuantity11(String salesQuantity11) {
		this.salesQuantity11 = salesQuantity11;
	}

	public String getSalesQuantity12() {
		return salesQuantity12;
	}

	public void setSalesQuantity12(String salesQuantity12) {
		this.salesQuantity12 = salesQuantity12;
	}

	public String getMaterialCost1() {
		return materialCost1;
	}

	public void setMaterialCost1(String materialCost1) {
		this.materialCost1 = materialCost1;
	}

	public String getMaterialCost2() {
		return materialCost2;
	}

	public void setMaterialCost2(String materialCost2) {
		this.materialCost2 = materialCost2;
	}

	public String getMaterialCost3() {
		return materialCost3;
	}

	public void setMaterialCost3(String materialCost3) {
		this.materialCost3 = materialCost3;
	}

	public String getMaterialCost4() {
		return materialCost4;
	}

	public void setMaterialCost4(String materialCost4) {
		this.materialCost4 = materialCost4;
	}

	public String getMaterialCost5() {
		return materialCost5;
	}

	public void setMaterialCost5(String materialCost5) {
		this.materialCost5 = materialCost5;
	}

	public String getMaterialCost6() {
		return materialCost6;
	}

	public void setMaterialCost6(String materialCost6) {
		this.materialCost6 = materialCost6;
	}

	public String getMaterialCost7() {
		return materialCost7;
	}

	public void setMaterialCost7(String materialCost7) {
		this.materialCost7 = materialCost7;
	}

	public String getMaterialCost8() {
		return materialCost8;
	}

	public void setMaterialCost8(String materialCost8) {
		this.materialCost8 = materialCost8;
	}

	public String getMaterialCost9() {
		return materialCost9;
	}

	public void setMaterialCost9(String materialCost9) {
		this.materialCost9 = materialCost9;
	}

	public String getMaterialCost10() {
		return materialCost10;
	}

	public void setMaterialCost10(String materialCost10) {
		this.materialCost10 = materialCost10;
	}

	public String getMaterialCost11() {
		return materialCost11;
	}

	public void setMaterialCost11(String materialCost11) {
		this.materialCost11 = materialCost11;
	}

	public String getMaterialCost12() {
		return materialCost12;
	}

	public void setMaterialCost12(String materialCost12) {
		this.materialCost12 = materialCost12;
	}

	public String getLaborCost1() {
		return laborCost1;
	}

	public void setLaborCost1(String laborCost1) {
		this.laborCost1 = laborCost1;
	}

	public String getLaborCost2() {
		return laborCost2;
	}

	public void setLaborCost2(String laborCost2) {
		this.laborCost2 = laborCost2;
	}

	public String getLaborCost3() {
		return laborCost3;
	}

	public void setLaborCost3(String laborCost3) {
		this.laborCost3 = laborCost3;
	}

	public String getLaborCost4() {
		return laborCost4;
	}

	public void setLaborCost4(String laborCost4) {
		this.laborCost4 = laborCost4;
	}

	public String getLaborCost5() {
		return laborCost5;
	}

	public void setLaborCost5(String laborCost5) {
		this.laborCost5 = laborCost5;
	}

	public String getLaborCost6() {
		return laborCost6;
	}

	public void setLaborCost6(String laborCost6) {
		this.laborCost6 = laborCost6;
	}

	public String getLaborCost7() {
		return laborCost7;
	}

	public void setLaborCost7(String laborCost7) {
		this.laborCost7 = laborCost7;
	}

	public String getLaborCost8() {
		return laborCost8;
	}

	public void setLaborCost8(String laborCost8) {
		this.laborCost8 = laborCost8;
	}

	public String getLaborCost9() {
		return laborCost9;
	}

	public void setLaborCost9(String laborCost9) {
		this.laborCost9 = laborCost9;
	}

	public String getLaborCost10() {
		return laborCost10;
	}

	public void setLaborCost10(String laborCost10) {
		this.laborCost10 = laborCost10;
	}

	public String getLaborCost11() {
		return laborCost11;
	}

	public void setLaborCost11(String laborCost11) {
		this.laborCost11 = laborCost11;
	}

	public String getLaborCost12() {
		return laborCost12;
	}

	public void setLaborCost12(String laborCost12) {
		this.laborCost12 = laborCost12;
	}

	public String getManufactureCost1() {
		return manufactureCost1;
	}

	public void setManufactureCost1(String manufactureCost1) {
		this.manufactureCost1 = manufactureCost1;
	}

	public String getManufactureCost2() {
		return manufactureCost2;
	}

	public void setManufactureCost2(String manufactureCost2) {
		this.manufactureCost2 = manufactureCost2;
	}

	public String getManufactureCost3() {
		return manufactureCost3;
	}

	public void setManufactureCost3(String manufactureCost3) {
		this.manufactureCost3 = manufactureCost3;
	}

	public String getManufactureCost4() {
		return manufactureCost4;
	}

	public void setManufactureCost4(String manufactureCost4) {
		this.manufactureCost4 = manufactureCost4;
	}

	public String getManufactureCost5() {
		return manufactureCost5;
	}

	public void setManufactureCost5(String manufactureCost5) {
		this.manufactureCost5 = manufactureCost5;
	}

	public String getManufactureCost6() {
		return manufactureCost6;
	}

	public void setManufactureCost6(String manufactureCost6) {
		this.manufactureCost6 = manufactureCost6;
	}

	public String getManufactureCost7() {
		return manufactureCost7;
	}

	public void setManufactureCost7(String manufactureCost7) {
		this.manufactureCost7 = manufactureCost7;
	}

	public String getManufactureCost8() {
		return manufactureCost8;
	}

	public void setManufactureCost8(String manufactureCost8) {
		this.manufactureCost8 = manufactureCost8;
	}

	public String getManufactureCost9() {
		return manufactureCost9;
	}

	public void setManufactureCost9(String manufactureCost9) {
		this.manufactureCost9 = manufactureCost9;
	}

	public String getManufactureCost10() {
		return manufactureCost10;
	}

	public void setManufactureCost10(String manufactureCost10) {
		this.manufactureCost10 = manufactureCost10;
	}

	public String getManufactureCost11() {
		return manufactureCost11;
	}

	public void setManufactureCost11(String manufactureCost11) {
		this.manufactureCost11 = manufactureCost11;
	}

	public String getManufactureCost12() {
		return manufactureCost12;
	}

	public void setManufactureCost12(String manufactureCost12) {
		this.manufactureCost12 = manufactureCost12;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getUnitMaterial1() {
		return unitMaterial1;
	}

	public void setUnitMaterial1(String unitMaterial1) {
		this.unitMaterial1 = unitMaterial1;
	}

	public String getUnitMaterial2() {
		return unitMaterial2;
	}

	public void setUnitMaterial2(String unitMaterial2) {
		this.unitMaterial2 = unitMaterial2;
	}

	public String getUnitMaterial3() {
		return unitMaterial3;
	}

	public void setUnitMaterial3(String unitMaterial3) {
		this.unitMaterial3 = unitMaterial3;
	}

	public String getUnitMaterial4() {
		return unitMaterial4;
	}

	public void setUnitMaterial4(String unitMaterial4) {
		this.unitMaterial4 = unitMaterial4;
	}

	public String getUnitMaterial5() {
		return unitMaterial5;
	}

	public void setUnitMaterial5(String unitMaterial5) {
		this.unitMaterial5 = unitMaterial5;
	}

	public String getUnitMaterial6() {
		return unitMaterial6;
	}

	public void setUnitMaterial6(String unitMaterial6) {
		this.unitMaterial6 = unitMaterial6;
	}

	public String getUnitMaterial7() {
		return unitMaterial7;
	}

	public void setUnitMaterial7(String unitMaterial7) {
		this.unitMaterial7 = unitMaterial7;
	}

	public String getUnitMaterial8() {
		return unitMaterial8;
	}

	public void setUnitMaterial8(String unitMaterial8) {
		this.unitMaterial8 = unitMaterial8;
	}

	public String getUnitMaterial9() {
		return unitMaterial9;
	}

	public void setUnitMaterial9(String unitMaterial9) {
		this.unitMaterial9 = unitMaterial9;
	}

	public String getUnitMaterial10() {
		return unitMaterial10;
	}

	public void setUnitMaterial10(String unitMaterial10) {
		this.unitMaterial10 = unitMaterial10;
	}

	public String getUnitMaterial11() {
		return unitMaterial11;
	}

	public void setUnitMaterial11(String unitMaterial11) {
		this.unitMaterial11 = unitMaterial11;
	}

	public String getUnitMaterial12() {
		return unitMaterial12;
	}

	public void setUnitMaterial12(String unitMaterial12) {
		this.unitMaterial12 = unitMaterial12;
	}

	public String getUnitLabor1() {
		return unitLabor1;
	}

	public void setUnitLabor1(String unitLabor1) {
		this.unitLabor1 = unitLabor1;
	}

	public String getUnitLabor2() {
		return unitLabor2;
	}

	public void setUnitLabor2(String unitLabor2) {
		this.unitLabor2 = unitLabor2;
	}

	public String getUnitLabor3() {
		return unitLabor3;
	}

	public void setUnitLabor3(String unitLabor3) {
		this.unitLabor3 = unitLabor3;
	}

	public String getUnitLabor4() {
		return unitLabor4;
	}

	public void setUnitLabor4(String unitLabor4) {
		this.unitLabor4 = unitLabor4;
	}

	public String getUnitLabor5() {
		return unitLabor5;
	}

	public void setUnitLabor5(String unitLabor5) {
		this.unitLabor5 = unitLabor5;
	}

	public String getUnitLabor6() {
		return unitLabor6;
	}

	public void setUnitLabor6(String unitLabor6) {
		this.unitLabor6 = unitLabor6;
	}

	public String getUnitLabor7() {
		return unitLabor7;
	}

	public void setUnitLabor7(String unitLabor7) {
		this.unitLabor7 = unitLabor7;
	}

	public String getUnitLabor8() {
		return unitLabor8;
	}

	public void setUnitLabor8(String unitLabor8) {
		this.unitLabor8 = unitLabor8;
	}

	public String getUnitLabor9() {
		return unitLabor9;
	}

	public void setUnitLabor9(String unitLabor9) {
		this.unitLabor9 = unitLabor9;
	}

	public String getUnitLabor10() {
		return unitLabor10;
	}

	public void setUnitLabor10(String unitLabor10) {
		this.unitLabor10 = unitLabor10;
	}

	public String getUnitLabor11() {
		return unitLabor11;
	}

	public void setUnitLabor11(String unitLabor11) {
		this.unitLabor11 = unitLabor11;
	}

	public String getUnitLabor12() {
		return unitLabor12;
	}

	public void setUnitLabor12(String unitLabor12) {
		this.unitLabor12 = unitLabor12;
	}

	public String getUnitManufacture1() {
		return unitManufacture1;
	}

	public void setUnitManufacture1(String unitManufacture1) {
		this.unitManufacture1 = unitManufacture1;
	}

	public String getUnitManufacture2() {
		return unitManufacture2;
	}

	public void setUnitManufacture2(String unitManufacture2) {
		this.unitManufacture2 = unitManufacture2;
	}

	public String getUnitManufacture3() {
		return unitManufacture3;
	}

	public void setUnitManufacture3(String unitManufacture3) {
		this.unitManufacture3 = unitManufacture3;
	}

	public String getUnitManufacture4() {
		return unitManufacture4;
	}

	public void setUnitManufacture4(String unitManufacture4) {
		this.unitManufacture4 = unitManufacture4;
	}

	public String getUnitManufacture5() {
		return unitManufacture5;
	}

	public void setUnitManufacture5(String unitManufacture5) {
		this.unitManufacture5 = unitManufacture5;
	}

	public String getUnitManufacture6() {
		return unitManufacture6;
	}

	public void setUnitManufacture6(String unitManufacture6) {
		this.unitManufacture6 = unitManufacture6;
	}

	public String getUnitManufacture7() {
		return unitManufacture7;
	}

	public void setUnitManufacture7(String unitManufacture7) {
		this.unitManufacture7 = unitManufacture7;
	}

	public String getUnitManufacture8() {
		return unitManufacture8;
	}

	public void setUnitManufacture8(String unitManufacture8) {
		this.unitManufacture8 = unitManufacture8;
	}

	public String getUnitManufacture9() {
		return unitManufacture9;
	}

	public void setUnitManufacture9(String unitManufacture9) {
		this.unitManufacture9 = unitManufacture9;
	}

	public String getUnitManufacture10() {
		return unitManufacture10;
	}

	public void setUnitManufacture10(String unitManufacture10) {
		this.unitManufacture10 = unitManufacture10;
	}

	public String getUnitManufacture11() {
		return unitManufacture11;
	}

	public void setUnitManufacture11(String unitManufacture11) {
		this.unitManufacture11 = unitManufacture11;
	}

	public String getUnitManufacture12() {
		return unitManufacture12;
	}

	public void setUnitManufacture12(String unitManufacture12) {
		this.unitManufacture12 = unitManufacture12;
	}

	public ForecastSalesCost(){}
}
