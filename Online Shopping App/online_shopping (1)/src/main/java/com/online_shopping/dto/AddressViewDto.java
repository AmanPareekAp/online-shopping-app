package com.online_shopping.dto;

import com.online_shopping.entity.user.Address;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AddressViewDto {

    int id;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String label;
    private int zipCode;

    public AddressViewDto(Address address)
    {
        this.id=address.getId();
        this.addressLine=address.getAddressLine();
        this.city=address.getCity();
        this.state=address.getState();
        this.country=address.getCountry();
        this.label=address.getLabel();
        this.zipCode=address.getZipCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
