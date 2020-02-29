package dev.onyxstudios.truckersportal.utils;

public class FactoringProfile {

    public String name;
    public String street, city, state, zipCode;

    public FactoringProfile() {
        this("", "", "", "", "");
    }

    public FactoringProfile(String name, String street, String city, String state, String zipCode) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
