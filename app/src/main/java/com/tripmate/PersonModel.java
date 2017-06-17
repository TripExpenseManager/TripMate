package com.tripmate;

/**
 * Created by vinee_000 on 17-06-2017.
 */

public class PersonModel {
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
}
