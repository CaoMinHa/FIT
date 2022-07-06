package foxconn.fit.entity.bi;

/**
 * @author Yang DaiSheng
 * @program fit
 * @description
 * @create 2021-04-21 14:15
 **/

import javax.persistence.*;
import java.io.Serializable;

import java.util.Date;


@Entity
@Table(name = " FIT_PO_Target_CPO_CD_DTL")
public class PoFlow implements Serializable{

    private static final long serialVersionUID = -6340420848724099356L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String year;
    private String poCenter;
    private String commodityMajor;
    private double noPoTotal;
    private double noCd;
    private double noCpo;
    private double poTotal;
    private double cd;
    private double cpo;
    private String flowUser;
    private String flowUserId;
    private Date flowTime;

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

    public String getPoCenter() {
        return poCenter;
    }

    public void setPoCenter(String poCenter) {
        this.poCenter = poCenter;
    }

    public String getCommodityMajor() {
        return commodityMajor;
    }

    public void setCommodityMajor(String commodityMajor) {
        this.commodityMajor = commodityMajor;
    }

    public double getNoPoTotal() {
        return noPoTotal;
    }

    public void setNoPoTotal(double noPoTotal) {
        this.noPoTotal = noPoTotal;
    }

    public double getNoCd() {
        return noCd;
    }

    public void setNoCd(double noCd) {
        this.noCd = noCd;
    }

    public double getNoCpo() {
        return noCpo;
    }

    public void setNoCpo(double noCpo) {
        this.noCpo = noCpo;
    }

    public double getPoTotal() {
        return poTotal;
    }

    public void setPoTotal(double poTotal) {
        this.poTotal = poTotal;
    }

    public double getCd() {
        return cd;
    }

    public void setCd(double cd) {
        this.cd = cd;
    }

    public double getCpo() {
        return cpo;
    }

    public void setCpo(double cpo) {
        this.cpo = cpo;
    }

    public String getFlowUser() {
        return flowUser;
    }

    public void setFlowUser(String flowUser) {
        this.flowUser = flowUser;
    }

    public String getFlowUserId() {
        return flowUserId;
    }

    public void setFlowUserId(String flowUserId) {
        this.flowUserId = flowUserId;
    }

    public Date getFlowTime() {
        return flowTime;
    }

    public void setFlowTime(Date flowTime) {
        this.flowTime = flowTime;
    }

    @Override
    public String toString() {
        return "PoFlow{" +
                "id='" + id + '\'' +
                ", year='" + year + '\'' +
                ", poCenter='" + poCenter + '\'' +
                ", commodityMajor='" + commodityMajor + '\'' +
                ", noPoTotal=" + noPoTotal +
                ", noCd=" + noCd +
                ", noCpo=" + noCpo +
                ", poTotal=" + poTotal +
                ", cd=" + cd +
                ", cpo=" + cpo +
                ", flowUser='" + flowUser + '\'' +
                ", flowUserId='" + flowUserId + '\'' +
                ", flowTime=" + flowTime +
                '}';
    }
}