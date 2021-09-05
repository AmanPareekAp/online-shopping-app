package com.online_shopping.dto;

import com.online_shopping.entity.user.Customer;
import com.online_shopping.entity.user.Seller;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class SellerViewDto {

    private int id;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String gst;
    private String companyName;

    private List<Long> contactList;
    private AddressViewDto addressViewDto;

    public SellerViewDto(Seller seller) {

        this.id=seller.getId();
        this.firstName=seller.getFirstName();
        this.lastName=seller.getLastName();
        this.isActive=seller.isActive();
        this.gst=seller.getGst();
        this.companyName=seller.getCompanyName();

        addressViewDto=new AddressViewDto(seller.getAddress());
        contactList=new ArrayList<>(seller.getContactList());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Long> getContactList() {
        return contactList;
    }

    public void setContactList(List<Long> contactList) {
        this.contactList = contactList;
    }

    public AddressViewDto getAddressViewDto() {
        return addressViewDto;
    }

    public void setAddressViewDto(AddressViewDto addressViewDto) {
        this.addressViewDto = addressViewDto;
    }
}
