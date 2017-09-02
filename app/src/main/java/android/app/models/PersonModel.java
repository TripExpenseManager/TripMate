package android.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vinee_000 on 17-06-2017.
 */

public class PersonModel implements Parcelable {
    private String name,mobile="",email="",trip_id;
    private  Double deposit=0.0;
    private int admin=0;

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_id() {
        return trip_id;
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

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
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
        dest.writeDouble(deposit);
        dest.writeInt(admin);

    }
    public static final Parcelable.Creator<PersonModel> CREATOR = new Parcelable.Creator<PersonModel>() {

        @Override
        public PersonModel createFromParcel(Parcel source) {
            PersonModel personModel = new PersonModel();
            personModel.setName(source.readString());
            personModel.setMobile(source.readString());
            personModel.setEmail(source.readString());
            personModel.setDeposit(source.readDouble());
            personModel.setAdmin(source.readInt());
            return  personModel;
        }

        @Override
        public PersonModel[] newArray(int size) {
            return new PersonModel[size];
        }
    };
}
