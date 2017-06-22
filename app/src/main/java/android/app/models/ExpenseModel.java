package android.app.models;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class ExpenseModel {
    String tripId,itemId,itemName,expBy,shareBy,category,date;
    Double amount;
    int amountType,shareByType;
    long dateValue;

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

    public int getShareByType() {
        return shareByType;
    }

    public String getShareBy() {
        return shareBy;
    }

    public void setShareBy(String shareBy) {
        this.shareBy = shareBy;
    }

    public void setShareByType(int shareByType) {
        this.shareByType = shareByType;
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
