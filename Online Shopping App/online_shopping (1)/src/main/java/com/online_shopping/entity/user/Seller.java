package com.online_shopping.entity.user;

import com.online_shopping.entity.product.Product;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Seller extends User{

    @NotEmpty
    private String gst;

    @ElementCollection
    @CollectionTable(name = "seller_contacts",
            joinColumns = @JoinColumn(name = "seller_id", referencedColumnName = "id"))
    private List<Long> contactList;

    @NotEmpty
    private String companyName;

    @NotNull
    @OneToOne(mappedBy = "seller",cascade = CascadeType.ALL)
    Address address;

    @ManyToMany(mappedBy = "sellerList",cascade = CascadeType.ALL)
    List<Product> productList;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
}
