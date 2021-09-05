package com.online_shopping.entity.product;

import com.online_shopping.service.product.JsonConverter;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
    private int quantityAvailable;
    private int price;
    private boolean isActive=true;
    @Convert(converter = JsonConverter.class)
    @Transient
    private Map<String, String> metadataMap;
    private String metadataString;
    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

    @Override
    public String toString() {
        return "ProductVariation{" +
                "id=" + id +
                ", product=" + product.getName() +
                ", quantityAvailable=" + quantityAvailable +
                ", price=" + price +
                ", isActive=" + isActive +
                ", metadataString=" + metadataString +
                '}';
    }
}
