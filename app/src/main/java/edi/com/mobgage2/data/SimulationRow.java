package edi.com.mobgage2.data;

/**
 * Created by Edi on 8/29/2016.
 */
public class SimulationRow {
    private double loanBalance;
    private double annualLoanInterestPerMonth;
    private double payment;
    private double interest;
    private double principal;

    public double getLoanBalance() {
        return loanBalance;
    }

    public void setLoanBalance(double loanBalance) {
        this.loanBalance = loanBalance;
    }

    public double getAnnualLoanInterestPerMonth() {
        return annualLoanInterestPerMonth;
    }

    public void setAnnualLoanInterestPerMonth(double annualLoanInterestPerMonth) {
        this.annualLoanInterestPerMonth = annualLoanInterestPerMonth;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }


    public void addLoanBalance(double num){
        loanBalance += num;
    }

    public void addAnnualLoanInterestPerMonth(double num){
        annualLoanInterestPerMonth += num;
    }

    public void addPayment(double num){
        payment += num;
    }

    public void addInterest(double num){
        interest += num;
    }

    public void addPrincipal(double num){
        principal += num;
    }
}
