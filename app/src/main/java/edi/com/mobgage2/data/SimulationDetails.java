package edi.com.mobgage2.data;

/**
 * Created by Edi on 8/30/2016.
 */
public class SimulationDetails {
    private double bankIsraelAnnualGrowth;
    private double fixedInterestAnnualGrowth;
    private double indexAnnualGrowth;
    private double capInterest;

    public double getBankIsraelAnnualGrowth() {
        return bankIsraelAnnualGrowth;
    }

    public void setBankIsraelAnnualGrowth(double bankIsraelAnnualGrowth) {
        this.bankIsraelAnnualGrowth = bankIsraelAnnualGrowth;
    }

    public double getFixedInterestAnnualGrowth() {
        return fixedInterestAnnualGrowth;
    }

    public void setFixedInterestAnnualGrowth(double fixedInterestAnnualGrowth) {
        this.fixedInterestAnnualGrowth = fixedInterestAnnualGrowth;
    }

    public double getIndexAnnualGrowth() {
        return indexAnnualGrowth;
    }

    public void setIndexAnnualGrowth(double indexAnnualGrowth) {
        this.indexAnnualGrowth = indexAnnualGrowth;
    }

    public double getCapInterest() {
        return capInterest;
    }

    public void setCapInterest(double capInterest) {
        this.capInterest = capInterest;
    }
}
