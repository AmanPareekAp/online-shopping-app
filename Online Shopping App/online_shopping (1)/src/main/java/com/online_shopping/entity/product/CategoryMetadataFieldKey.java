package com.online_shopping.entity.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldKey implements Serializable {

    private int categoryMetadataFieldId;
    private int categoryId;

}
