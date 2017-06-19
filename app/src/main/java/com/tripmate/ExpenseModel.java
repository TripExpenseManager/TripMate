package com.tripmate;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class ExpenseModel {
    String trip_id,exp_id,name,expBy,shareBy,category,date;
    Double amount;

    public ExpenseModel() {
    }

    public ExpenseModel(String name, String expBy, String shareBy, String category, String date, Double amount) {
        this.name = name;
        this.expBy = expBy;
        this.shareBy = shareBy;
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    public ExpenseModel(String trip_id, String exp_id, String name, String expBy, String shareBy, String category, String date, Double amount) {
        this.trip_id = trip_id;
        this.exp_id = exp_id;
        this.name = name;
        this.expBy = expBy;
        this.shareBy = shareBy;
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getExp_id() {
        return exp_id;
    }

    public void setExp_id(String exp_id) {
        this.exp_id = exp_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpBy() {
        return expBy;
    }

    public void setExpBy(String expBy) {
        this.expBy = expBy;
    }

    public String getShareBy() {
        return shareBy;
    }

    public void setShareBy(String shareBy) {
        this.shareBy = shareBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
