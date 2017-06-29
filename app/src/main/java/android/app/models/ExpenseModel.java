package android.app.models;

import java.io.Serializable;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class ExpenseModel implements Serializable{
    String tripId,itemId,itemName,expBy,shareBy,category,date;
    Double amount;
    int amountType;
    long dateValue;



    //amount type = 1 refers to deposit money spent
    //amount type = 2 refers to personal money spent

    //below types were removed as adding person in middle of the trip is becoming messy
    //shareByType = 1 refers to shared by all
    //shareByType = 2 refers to shared by some people

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public long getDateValue() {
        return dateValue;
    }

    public void setDateValue(long dateValue) {
        this.dateValue = dateValue;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public int getAmountType() {
        return amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = amountType;
    }


}
