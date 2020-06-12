package com.example.taxirider.Model;

public class Rider {
    private String name, email, phone, pass,sex, year;

    public Rider () {
    }
    public Rider(String name, String email, String phone, String pass, String sex, String year) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pass = pass;
        this.sex = sex;
        this.year = year;
    }

    public String getName () {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone (String phone) {
        this.phone = phone;
    }

    public String getPass () {
        return pass;
    }

    public void setPass (String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "Rider{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", pass='" + pass + '\'' +
                ", sex='" + sex + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
