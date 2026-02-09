package com.example.final_project_android;

public class Restaurant_class {

    private String res_name;

    private String Email;

    private String Business_id;

    private String address;

    private  String password;

    public Restaurant_class(){}

    public Restaurant_class(String res_name, String email, String business_id, String address, String password) {
        this.res_name = res_name;
        Email = email;
        Business_id = business_id;
        this.address = address;
        this.password = password;
    }

    public String getRes_name() {
        return res_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBusiness_id() {
        return Business_id;
    }

    public void setBusiness_id(String business_id) {
        Business_id = business_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
