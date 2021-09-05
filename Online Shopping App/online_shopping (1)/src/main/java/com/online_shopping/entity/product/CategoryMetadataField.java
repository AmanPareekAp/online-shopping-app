package com.online_shopping.entity.product;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@JsonFilter("CategoryMetadataFieldFilter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(unique = true,nullable = false)
    private String name;

    public CategoryMetadataField(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "categoryMetadataField")
    private List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList;

    @Override
    public String toString() {
        return "CategoryMetadataField{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}