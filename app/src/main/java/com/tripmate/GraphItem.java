package com.tripmate;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class GraphItem {
    private String name;
    private Double amount;

    public GraphItem(String name, Double amount) {
        this.name = name;
        this.amount = amount;
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

    public GraphItem() {
    }
}

