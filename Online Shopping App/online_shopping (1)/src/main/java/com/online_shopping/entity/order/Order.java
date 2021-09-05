package com.online_shopping.entity.order;

import com.online_shopping.entity.user.Address;
import com.online_shopping.entity.user.Customer;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;
    private Date dateCreated;
    private String paymentMethod;

    private String customerAddressAddressLine;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private int customerAddressZipcode;
    private String customerAddressLabel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProductList = new ArrayList<>();

    public Order(){}

    public Order(Date dateCreated, String paymentMethod,
                 Address address) {

        this.dateCreated = dateCreated;
        this.paymentMethod = paymentMethod;
        this.customerAddressAddressLine = address.getAddressLine();
        this.customerAddressCity = address.getCity();
        this.customerAddressState = address.getState();
        this.customerAddressCountry = address.getCountry();
        this.customerAddressZipcode = address.getZipCode();
        this.customerAddressLabel = address.getLabel();
    }

}
