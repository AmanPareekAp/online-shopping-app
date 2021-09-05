package com.online_shopping.entity.product;

import com.online_shopping.entity.user.Seller;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    boolean isCancellable;
    boolean isReturnable;
    private String brand;
    boolean isActive;
    boolean isDeleted;
    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;



    @ManyToMany()
    @JoinTable(name = "seller_product",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "seller_id",referencedColumnName = "id"))
    private List<Seller> sellerList;

    //get it checked
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductReview> productReviewList;

    @OneToMany(mappedBy = "product")
    private List<ProductVariation> productVariationList;

    public Product() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Seller> getSellerList() {
        return sellerList;
    }

    public void setSellerList(List<Seller> sellerList) {
        this.sellerList = sellerList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductReview> getProductReviewList() {
        return productReviewList;
    }

    public void setProductReviewList(List<ProductReview> productReviewList) {
        this.productReviewList = productReviewList;
    }

    public List<ProductVariation> getProductVariationList() {
        return productVariationList;
    }

    public void setProductVariationList(List<ProductVariation> productVariationList) {
        this.productVariationList = productVariationList;
    }

    public boolean isCancellable() {
        return isCancellable;
    }

    public void setCancellable(boolean cancellable) {
        isCancellable = cancellable;
    }

    public boolean isReturnable() {
        return isReturnable;
    }

    public void setReturnable(boolean returnable) {
        isReturnable = returnable;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isCancellable=" + isCancellable +
                ", isReturnable=" + isReturnable +
                ", brand='" + brand + '\'' +
                ", isActive=" + isActive +
                ", category=" + category.getName() +
                '}';
    }
}
