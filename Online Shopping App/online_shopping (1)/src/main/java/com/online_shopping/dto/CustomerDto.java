package com.online_shopping.dto;

import com.online_shopping.entity.order.Order;
import com.online_shopping.entity.product.ProductReview;
import com.online_shopping.entity.user.Address;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

public class CustomerDto {

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
    @NotEmpty
    String confirmPassword;
    private boolean isActive;

    private List<Long> contactList;
    private List<Address> addressList;

    private List<ProductReview> productReviewList;
    private List<Order> OrderList;


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

    public List<Long> getContactList() {
        return contactList;
    }

    public void setContactList(List<Long> contactList) {
        this.contactList = contactList;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public List<ProductReview> getProductReviewList() {
        return productReviewList;
    }

    public void setProductReviewList(List<ProductReview> productReviewList) {
        this.productReviewList = productReviewList;
    }

    public List<Order> getAOrderList() {
        return OrderList;
    }

    public void setAOrderList(List<Order> OrderList) {
        this.OrderList = OrderList;
    }
}
