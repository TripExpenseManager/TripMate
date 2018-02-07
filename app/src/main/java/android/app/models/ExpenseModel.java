package android.app.models;

import java.io.Serializable;

/**
 * Created by vinee_000 on 19-06-2017.
 */

public class ExpenseModel implements Serializable{
    String tripId,itemId,itemName,expBy,shareBy,category,date,currency;
    Double amount,currencyConversionRate;
    int amountType,expByType,shareByType;
    long dateValue;

    //only for persons wise expense list generation
    String personNameForGeneration = "";
    Double amountByPersonForGeneration = 0.0;



    //amount type = 1 refers to deposit money spent
    //amount type = 2 refers to personal money spent

    //below types were removed as adding person in middle of the trip is becoming messy
    //shareByType = 1 refers to shared by all
    //shareByType = 2 refers to shared by some people

    //Now again added due to some extra feaatures
    //shareByType = 1 refers to shared equally
    //shareByType = 2 refers to shared unequally
    //shareByType = 2 refers to shared by shares

    //expByType = 1 refers exp by single person
    //expByType = 1 refers exp by multiple persons


    public ExpenseModel(ExpenseModel model) {
        this.tripId = model.tripId;
        this.itemId = model.itemId;
        this.itemName = model.itemName;
        this.expBy = model.expBy;
        this.shareBy = model.shareBy;
        this.category = model.category;
        this.date = model.date;
        this.currency = model.currency;
        this.amount = model.amount;
        this.currencyConversionRate = model.currencyConversionRate;
        this.amountType = model.amountType;
        this.expByType = model.expByType;
        this.shareByType = model.shareByType;
        this.dateValue = model.dateValue;
        this.personNameForGeneration = model.personNameForGeneration;
        this.amountByPersonForGeneration = model.amountByPersonForGeneration;
    }

    public ExpenseModel(){

    }

    public String getPersonNameForGeneration() {
        return personNameForGeneration;
    }

    public void setPersonNameForGeneration(String personNameForGeneration) {
        this.personNameForGeneration = personNameForGeneration;
    }

    public Double getAmountByPersonForGeneration() {
        return amountByPersonForGeneration;
    }

    public void setAmountByPersonForGeneration(Double amountByPersonForGeneration) {
        this.amountByPersonForGeneration = amountByPersonForGeneration;
    }

    public Double getCurrencyConversionRate() {
        return currencyConversionRate;
    }


    public void setCurrencyConversionRate(Double currencyConversionRate) {
        this.currencyConversionRate = currencyConversionRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getExpByType() {
        return expByType;
    }

    public void setExpByType(int expByType) {
        this.expByType = expByType;
    }

    public void setShareByType(int shareByType) {
        this.shareByType = shareByType;
    }

    public int getShareByType() {
        return shareByType;
    }


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
