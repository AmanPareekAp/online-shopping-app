package com.online_shopping.entity.user;

import com.online_shopping.entity.order.Order;
import com.online_shopping.entity.product.ProductReview;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Data
public class Customer extends User{

    //Apply validation on contactList
    @ElementCollection
    @CollectionTable(name = "customer_contacts",
            joinColumns = @JoinColumn(name = "customer_id",referencedColumnName = "id"))
    private List<Long> contactList;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Address> addressList;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    List<ProductReview> productReviewList;

    @OneToMany(mappedBy = "customer")
    List<Order> OrderList;

    public List<ProductReview> getProductReviewList() {
        return productReviewList;
    }

    public void setProductReviewList(List<ProductReview> productReviewList) {
        this.productReviewList = productReviewList;
    }
}
