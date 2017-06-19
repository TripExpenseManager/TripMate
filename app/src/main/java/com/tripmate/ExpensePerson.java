package com.tripmate;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class ExpensePerson {
    private String name;
    private double amountSpent,amountShared,amountDue,amountDeposit;

    public ExpensePerson(String name,double amountDeposit, double amountSpent, double amountShared) {
        this.name = name;
        this.amountSpent = amountSpent;
        this.amountShared = amountShared;
        this.amountDue = amountSpent-amountShared;
        this.amountDeposit = amountDeposit;
    }

    public String getName() {
        return name;
    }

    public ExpensePerson() {
        this.name = name;
        this.amountSpent = 0.0;
        this.amountShared = 0.0;
        this.amountDue = amountSpent-amountShared;
        this.amountDeposit = 0.0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(double amountSpent) {
        this.amountSpent = amountSpent;
    }

    public double getAmountShared() {
        return amountShared;
    }

    public void setAmountShared(double amountShared) {
        this.amountShared = amountShared;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getAmountDeposit() {
        return amountDeposit;
    }

    public void setAmountDeposit(double amountDeposit) {
        this.amountDeposit = amountDeposit;
    }
}
