package foxconn.fit.entity.bi;


import foxconn.fit.entity.base.IdEntity;
import org.apache.poi.hpsf.Decimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "FIT_ACTUAL_PO_NPRICECD_DTL")
public class ActualPoNPriceCDDetail extends IdEntity {
    private static final long serialVersionUID = -6340420848724099549L;

    private  String scenario ;
    private  String year ;
    private  String period ;
    private  String commodity ;
    private  String vView ;
    private  String bu ;
    private  String sbu ;
    private String lowerToleranceLimit ;
    private  String changeItem ;
    private  String secondSource ;
    private  String competor ;
    private String priceContinueLimit;
    private String lowerContact ;
    private String rebate ;//回扣
    private String punitiveDeduction ;//惩罚性扣款
    private  String freeSample ;//免费送样
    private  String sluggishTreatment;//呆滞处理
    private  String  tradeTerm ;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }
    @Column(name = "v_view")
    public String getvView() {
        return vView;
    }

    public void setvView(String vView) {
        this.vView = vView;
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
    @Column(name = "LOWER_TOLERANCE_LIMIT")
    public String getLowerToleranceLimit() {
        return lowerToleranceLimit;
    }

    public void setLowerToleranceLimit(String lowerToleranceLimit) {
        this.lowerToleranceLimit = lowerToleranceLimit;
    }
    @Column(name = "CHANGE_ITEM")
    public String getChangeItem() {
        return changeItem;
    }

    public void setChangeItem(String changeItem) {
        this.changeItem = changeItem;
    }
    @Column(name = "SECOND_SOURCE")
    public String getSecondSource() {
        return secondSource;
    }

    public void setSecondSource(String secondSource) {
        this.secondSource = secondSource;
    }

    public String getCompetor() {
        return competor;
    }

    public void setCompetor(String competor) {
        this.competor = competor;
    }
    @Column(name = "PRICE_CONTINUE_LIMIT")
    public String getPriceContinueLimit() {
        return priceContinueLimit;
    }

    public void setPriceContinueLimit(String priceContinueLimit) {
        this.priceContinueLimit = priceContinueLimit;
    }
    @Column(name = "LOWER_CONTACT")
    public String getLowerContact() {
        return lowerContact;
    }

    public void setLowerContact(String lowerContact) {
        this.lowerContact = lowerContact;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }
    @Column(name = "PUNITIVE_DEDUCTION")
    public String getPunitiveDeduction() {
        return punitiveDeduction;
    }

    public void setPunitiveDeduction(String punitiveDeduction) {
        this.punitiveDeduction = punitiveDeduction;
    }
    @Column(name = "FREE_SAMPLE")
    public String getFreeSample() {
        return freeSample;
    }

    public void setFreeSample(String freeSample) {
        this.freeSample = freeSample;
    }
    @Column(name = "SLUGGISH_TREATMENT")
    public String getSluggishTreatment() {
        return sluggishTreatment;
    }

    public void setSluggishTreatment(String sluggishTreatment) {
        this.sluggishTreatment = sluggishTreatment;
    }
    @Column(name = "TRADE_TERM")
    public String getTradeTerm() {
        return tradeTerm;
    }

    public void setTradeTerm(String tradeTerm) {
        this.tradeTerm = tradeTerm;
    }
}
