package android.app.models;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class GraphItemModel {
    private String name;
    private Double amount;
    private Double percentage;

    public GraphItemModel() {
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

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getPercentage() {
        return percentage;
    }
}

