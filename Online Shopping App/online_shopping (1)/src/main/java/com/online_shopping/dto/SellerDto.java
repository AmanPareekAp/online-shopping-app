package com.online_shopping.dto;

import com.online_shopping.entity.product.Product;
import com.online_shopping.entity.user.Address;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class SellerDto {

    @Email
    private String email;

    @NotEmpty
    private String firstName;
    private String middleName;
    private String lastName;
    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" +
            "(?=.*[@#$%^&+=!])" + ".{8,15}$")
    String password;
    String confirmPassword;
    private boolean isActive;


    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")
    @NotEmpty
    private String gst;

    private List<Long> contactList;

    @NotEmpty
    private String companyName;

    @NotNull
    Address address;

    Product product;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    public List<Long> getContactList() {
        return contactList;
    }

    public void setContactList(List<Long> contactList) {
        this.contactList = contactList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
