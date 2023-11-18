package com.example.SunbaseData.model;

public class Customer {
    public String first_name;
    public String last_name;
    public String        street;
    public String address;
    public String city;

    public Customer(String first_name, String last_name, String street, String address, String city, String state, String email, String phone) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.street = street;
        this.address = address;
        this.city = city;
        this.state = state;
        this.email = email;
        this.phone = phone;
    }

    public String state;
    public String email;
    public String phone;

}
