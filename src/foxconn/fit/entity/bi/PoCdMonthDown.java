package foxconn.fit.entity.bi;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 采購CDby月份展開表
 */
@Entity
@Table(name = "FIT_PO_CD_MONTH_DOWN")
public class PoCdMonthDown implements Serializable {
    private static final long serialVersionUID = -6340420848724011111L;

    private String id;
    private String year;
    private String commodityMajor;
    private String bu;
    private String sbu;

    public PoCdMonthDown(){}

    @Id
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

    @Column(name = "commodity_major")
    public String getCommodityMajor() {
        return commodityMajor;
    }

    public void setCommodityMajor(String commodityMajor) {
        this.commodityMajor = commodityMajor;
    }

    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }

    public String getSbu() {
        return sbu;
    }

    public void setSbu(String sbu) {
        this.sbu = sbu;
    }
}