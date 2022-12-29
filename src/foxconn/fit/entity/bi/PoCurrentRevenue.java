package foxconn.fit.entity.bi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maggao
 * 非FIT體系當期收入表
 */
@Entity
@Table(name = "FIT_PO_CURRENT_REVENUE")
public class PoCurrentRevenue implements Serializable {

	@Id
	@Column(name = "ID")
	private String id;
	/**創建人*/
	@Column(name = "create_name")
	private String createName;
	/**創建時間*/
	@Column(name = "create_date")
	private Date createDate;
	/** 期間*/
	private String PERIOD;
	private String BU;
	private String SBU;
	/**非FIT體系當期外銷營業收入（M NTD）*/
	@Column(name = "EXPORT_SALES_REVENUE")
	private String exportSalesRevenue;
	/** 非FIT體系當期內銷營業收入（M NTD）*/
	@Column(name = "DOMESTIC_SALES_REVENUE")
	private String domesticSalesRevenue;

	public PoCurrentRevenue() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPERIOD() {
		return PERIOD;
	}

	public void setPERIOD(String PERIOD) {
		this.PERIOD = PERIOD;
	}

	public String getBU() {
		return BU;
	}

	public void setBU(String BU) {
		this.BU = BU;
	}

	public String getSBU() {
		return SBU;
	}

	public void setSBU(String SBU) {
		this.SBU = SBU;
	}

	public String getExportSalesRevenue() {
		return exportSalesRevenue;
	}

	public void setExportSalesRevenue(String exportSalesRevenue) {
		this.exportSalesRevenue = exportSalesRevenue;
	}

	public String getDomesticSalesRevenue() {
		return domesticSalesRevenue;
	}

	public void setDomesticSalesRevenue(String domesticSalesRevenue) {
		this.domesticSalesRevenue = domesticSalesRevenue;
	}

}