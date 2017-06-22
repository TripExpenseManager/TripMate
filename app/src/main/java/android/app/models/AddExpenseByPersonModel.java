package android.app.models;

/**
 * Created by Sai Krishna on 6/22/2017.
 */

public class AddExpenseByPersonModel {

    String name;
    Double amount;
    int namePosition = 0;

    public int getNamePosition() {
        return namePosition;
    }

    public void setNamePosition(int namePosition) {
        this.namePosition = namePosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
