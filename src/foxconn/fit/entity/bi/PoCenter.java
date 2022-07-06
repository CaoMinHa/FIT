package foxconn.fit.entity.bi;
import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-22 16:51
 **/
@Entity
@Table(name = " FIT_PO_Target_CPO_CD_DTL")
public class PoCenter implements Serializable{

    private static final long serialVersionUID = -6340420848724099567L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String ID;

    @Column(name = "FUNCTION_NAME")
    private String PoCenter;

    @Column(name = "COMMODITY_NAME")
    private String commodity;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPoCenter() {
        return PoCenter;
    }

    public void setPoCenter(String poCenter) {
        PoCenter = poCenter;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    @Override
    public String toString() {
        return "PoCenter{" +
                "ID='" + ID + '\'' +
                ", PoCenter='" + PoCenter + '\'' +
                ", commodity='" + commodity + '\'' +
                '}';
    }
}