package com.example.final_project_android;

public class inspector_class {

    private  String Full_name;

    private  String Email;

    private  String Company_name;

    private  String ID;

    private  String Licence_number;

    private  String Password;

    public inspector_class(){}

    public inspector_class(String full_name, String email, String company_name, String ID, String licence_number, String password) {
        Full_name = full_name;
        Email = email;
        Company_name = company_name;
        this.ID = ID;
        Licence_number = licence_number;
        Password = password;
    }

    public String getFull_name() {
        return Full_name;
    }

    public void setFull_name(String full_name) {
        Full_name = full_name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCompany_name() {
        return Company_name;
    }

    public void setCompany_name(String company_name) {
        Company_name = company_name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLicence_number() {
        return Licence_number;
    }

    public void setLicence_number(String licence_number) {
        Licence_number = licence_number;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
