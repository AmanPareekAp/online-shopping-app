package com.online_shopping.dto;

import com.online_shopping.entity.user.Customer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class CustomerViewDto {

    private int id;
    private String firstName;
    private String lastName;
    private boolean isActive;

    private List<Long> contactList;

    public CustomerViewDto(Customer customer) {

        this.id=customer.getId();
        this.firstName=customer.getFirstName();
        this.lastName= customer.getLastName();
        this.isActive=customer.isActive();

        contactList=new ArrayList<>(customer.getContactList());
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

    public List<Long> getContactList() {
        return contactList;
    }

    public void setContactList(List<Long> contactList) {
        this.contactList = contactList;
    }
}
