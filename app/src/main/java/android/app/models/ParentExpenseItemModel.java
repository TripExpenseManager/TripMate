package android.app.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;

/**
 * Created by Sai Krishna on 6/24/2017.
 */

public class ParentExpenseItemModel implements Parent<ExpenseModel> {

    String name;
    Double amount = 0.0, percentage = 0.0;

    ArrayList<ExpenseModel> expenseList = new ArrayList<>();

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

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public ArrayList<ExpenseModel> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(ArrayList<ExpenseModel> expenseList) {
        this.expenseList = expenseList;
    }

    @Override
    public ArrayList<ExpenseModel> getChildList() {
        return expenseList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
