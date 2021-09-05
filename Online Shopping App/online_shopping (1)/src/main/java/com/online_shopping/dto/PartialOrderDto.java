package com.online_shopping.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class PartialOrderDto {

    @NotEmpty
    List<Integer> productVariationIdList;
    @NotNull
    OrderProductsDto orderProductsDto;

    public List<Integer> getProductVariationIdList() {
        return productVariationIdList;
    }

    public void setProductVariationIdList(List<Integer> productVariationIdList) {
        this.productVariationIdList = productVariationIdList;
    }

    public OrderProductsDto getOrderProductsDto() {
        return orderProductsDto;
    }

    public void setOrderProductsDto(OrderProductsDto orderProductsDto) {
        this.orderProductsDto = orderProductsDto;
    }
}
