package dev.onyxstudios.truckersportal.utils;

import org.bson.Document;

public class CarrierProfile {

    public String name, email, phoneNumber;
    public String street, city, state, zipCode;
    public String domain;
    public boolean factoring;
    public FactoringProfile factoringProfile;

    public CarrierProfile(String name, String email, String phoneNumber, String street, String city, String state, String zipCode, String domain, boolean factoring, FactoringProfile factoringProfile) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.domain = domain;
        this.factoring = factoring;
        this.factoringProfile = factoringProfile;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("name", this.name);
        document.append("email", this.email);
        document.append("phoneNumber", this.phoneNumber);
        document.append("street", this.street);
        document.append("city", this.city);
        document.append("state", this.state);
        document.append("zipCode", this.zipCode);
        document.append("factoring", this.factoring);

        document.append("factoringName", factoringProfile.name);
        document.append("factoringStreet", factoringProfile.street);
        document.append("factoringCity", factoringProfile.city);
        document.append("factoringState", factoringProfile.state);
        document.append("factoringZip", factoringProfile.zipCode);

        return document;
    }
}
