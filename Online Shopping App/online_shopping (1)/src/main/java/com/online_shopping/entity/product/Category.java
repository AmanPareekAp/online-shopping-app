package com.online_shopping.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

   @ManyToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "parent_category_id", referencedColumnName = "id")
   private Category parentCategory;

   @OneToMany
   private List<Category> childCategoryList;

   @OneToMany(mappedBy = "category")
   private List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList;

   //get it checked
   @OneToMany(mappedBy = "category")
   private List<Product> productList;

    public Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + "'" +
                '}';
    }

}
