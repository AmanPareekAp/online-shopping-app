package com.online_shopping.entity.order;

import com.online_shopping.entity.product.ProductVariation;
import com.online_shopping.entity.user.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @EmbeddedId
    private CustomerProductVariationKey customerProductVariationKey;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @MapsId("productVariationId")
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;

    private int quantity;
    private boolean isWishlistItem;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

}
