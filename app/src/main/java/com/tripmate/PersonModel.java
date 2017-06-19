package com.tripmate;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vinee_000 on 17-06-2017.
 */

public class PersonModel implements Parcelable {
    private String name,mobile,email,deposit,admin;

    public PersonModel() {
    }

    public PersonModel(String name, String mobile, String email, String deposit, String admin) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.deposit = deposit;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String details(){
        return "Name: "+name + " Mobile: " + mobile + " Deposit: " + deposit + " Email: "+email ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(deposit);
        dest.writeString(admin);

    }
    public static final Parcelable.Creator<PersonModel> CREATOR = new Parcelable.Creator<PersonModel>() {

        @Override
        public PersonModel createFromParcel(Parcel source) {
            PersonModel personModel = new PersonModel();
            personModel.setName(source.readString());
            personModel.setMobile(source.readString());
            personModel.setEmail(source.readString());
            personModel.setDeposit(source.readString());
            personModel.setAdmin(source.readString());
            return  personModel;
        }

        @Override
        public PersonModel[] newArray(int size) {
            return new PersonModel[size];
        }
    };
}
