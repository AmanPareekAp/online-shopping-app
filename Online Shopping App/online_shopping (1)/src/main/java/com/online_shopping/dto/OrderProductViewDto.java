package com.online_shopping.dto;

import com.online_shopping.entity.order.OrderProduct;
import com.online_shopping.entity.order.OrderStatus;
import lombok.Data;

@Data
//@JsonFilter("orderProductViewFilter")
public class OrderProductViewDto {

    int id;
    private int quantity;
    private int price;
    private String productVariationMetadata;
    private ProductVariationViewDto productVariation;
    private OrderStatusViewDto orderProductStatus;

    public OrderProductViewDto(OrderProduct orderProduct, OrderStatus orderStatus) {
        this.id=orderProduct.getId();
        this.quantity = orderProduct.getQuantity();
        this.price = orderProduct.getPrice();
        this.productVariationMetadata = orderProduct.getProductVariationMetadata();
        this.productVariation = new ProductVariationViewDto(orderProduct.getProductVariation());
        this.orderProductStatus = new OrderStatusViewDto(orderStatus);
    }
}
