package com.online_shopping.entity.product;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@JsonFilter("CategoryMetadataFieldValuesFilter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValues {

    @EmbeddedId
    private CategoryMetadataFieldKey categoryMetadataFieldKey;

    private String metadataValues;

    @ManyToOne
    @MapsId("categoryMetadataFieldId")
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetadataField categoryMetadataField;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

}
