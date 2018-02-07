package android.app.models;

/**
 * Created by Sai Krishna on 2/5/2018.
 */

public class ExpDialogPersonAndAmountModel {
    String name;
    double amount;
    int shares = 0;

    public ExpDialogPersonAndAmountModel(String name, double amount, int shares) {
        this.name = name;
        this.amount = amount;
        this.shares = shares;
    }

    public ExpDialogPersonAndAmountModel(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getShares() {
        return shares;
    }



    public void setShares(int shares) {
        this.shares = shares;
    }
}
