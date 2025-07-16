package foxconn.fit.entity.budget;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author CXJ
 * 料號對接
 */
@Entity
@Table(name = "CUX_BUDGET_PRODUCT_BASIC_DATA")
public class  BudgetProductBasicData implements Serializable{
	@Id
	@Column(name = "ID")
	private String id;
	/**料號*/
	@Column(name = "PRODUCT_NO")
	private String productNO;
	/**創建時間*/
	@Column(name = "CREATION_DATE")
	private String createDate;

	public BudgetProductBasicData() {

	}
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getproductNO() {
		return productNO;
	}

	public void setproductNO(String productNO) {
		this.productNO = productNO;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
}
	