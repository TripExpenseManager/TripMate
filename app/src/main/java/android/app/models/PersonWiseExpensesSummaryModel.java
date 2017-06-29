package android.app.models;

/**
 * Created by Sai Krishna on 6/22/2017.
 */

public class PersonWiseExpensesSummaryModel {

    private String name,mobile,email;
    private  Double depositAmountGiven,depositAmountSpent,depositAmountRemaining,personalAmountGiven,personalAmountSpent,personalAmountRemaining,totalAmountGiven,totalAmountSpent,totalAmountRemaining;
    private int admin;
    boolean canRemove;


    public boolean getCanRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getDepositAmountGiven() {
        return depositAmountGiven;
    }

    public void setDepositAmountGiven(Double depositAmountGiven) {
        this.depositAmountGiven = depositAmountGiven;
    }

    public Double getDepositAmountSpent() {
        return depositAmountSpent;
    }

    public void setDepositAmountSpent(Double depositAmountSpent) {
        this.depositAmountSpent = depositAmountSpent;
    }

    public Double getDepositAmountRemaining() {
        return depositAmountRemaining;
    }

    public void setDepositAmountRemaining(Double depositAmountRemaining) {
        this.depositAmountRemaining = depositAmountRemaining;
    }

    public Double getPersonalAmountGiven() {
        return personalAmountGiven;
    }

    public void setPersonalAmountGiven(Double personalAmountGiven) {
        this.personalAmountGiven = personalAmountGiven;
    }

    public Double getPersonalAmountRemaining() {
        return personalAmountRemaining;
    }

    public void setPersonalAmountRemaining(Double personalAmountRemaining) {
        this.personalAmountRemaining = personalAmountRemaining;
    }

    public Double getPersonalAmountSpent() {
        return personalAmountSpent;
    }

    public void setPersonalAmountSpent(Double personalAmountSpent) {
        this.personalAmountSpent = personalAmountSpent;
    }

    public Double getTotalAmountGiven() {
        return totalAmountGiven;
    }

    public void setTotalAmountGiven(Double totalAmountGiven) {
        this.totalAmountGiven = totalAmountGiven;
    }

    public Double getTotalAmountRemaining() {
        return totalAmountRemaining;
    }

    public void setTotalAmountRemaining(Double totalAmountRemaining) {
        this.totalAmountRemaining = totalAmountRemaining;
    }

    public Double getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public void setTotalAmountSpent(Double totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
