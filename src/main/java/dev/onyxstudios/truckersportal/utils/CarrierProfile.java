package dev.onyxstudios.truckersportal.utils;

import dev.onyxstudios.truckersportal.TruckersPortal;
import org.bson.Document;

import java.io.IOException;

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

    public void saveConfig() {
        TruckersPortal.CONFIG.set("name", this.name);
        TruckersPortal.CONFIG.set("email", this.email);
        TruckersPortal.CONFIG.set("phoneNumber", this.phoneNumber);
        TruckersPortal.CONFIG.set("street", this.street);
        TruckersPortal.CONFIG.set("city", this.city);
        TruckersPortal.CONFIG.set("state", this.state);
        TruckersPortal.CONFIG.set("zipCode", this.zipCode);
        TruckersPortal.CONFIG.set("domain", this.domain);
        TruckersPortal.CONFIG.set("factoring", this.factoringProfile);

        TruckersPortal.CONFIG.set("factoring-name", this.factoringProfile.name);
        TruckersPortal.CONFIG.set("factoring-street", this.factoringProfile.street);
        TruckersPortal.CONFIG.set("factoring-city", this.factoringProfile.city);
        TruckersPortal.CONFIG.set("factoring-state", this.factoringProfile.state);
        TruckersPortal.CONFIG.set("factoring-zipCode", this.factoringProfile.zipCode);

        try {
            TruckersPortal.CONFIG.saveWithComments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
