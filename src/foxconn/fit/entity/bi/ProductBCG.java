package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import foxconn.fit.entity.base.IdEntity;

/**
 * 產品BCG映射
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_Product_BCG")
public class ProductBCG extends IdEntity {

	private static final long serialVersionUID = -6340420848724099549L;

	private String version;// 版本（年月）
	private String SBU;// SBU
	private String partNo;// partNo_col
	private String productFamily;// productFamily_col
	private String productSeries;// productSeries_col
	private String BCG;// BCG

	@Column
	public String getVersion() {
		return version;
	}

	@Column
	public String getSBU() {
		return SBU;
	}

	@Column(name = "part_No")
	public String getPartNo() {
		return partNo;
	}

	@Column(name = "product_Family")
	public String getProductFamily() {
		return productFamily;
	}

	@Column(name = "product_Series")
	public String getProductSeries() {
		return productSeries;
	}

	@Column
	public String getBCG() {
		return BCG;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setSBU(String sBU) {
		SBU = sBU;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public void setProductFamily(String productFamily) {
		this.productFamily = productFamily;
	}

	public void setProductSeries(String productSeries) {
		this.productSeries = productSeries;
	}

	public void setBCG(String bCG) {
		BCG = bCG;
	}

}
