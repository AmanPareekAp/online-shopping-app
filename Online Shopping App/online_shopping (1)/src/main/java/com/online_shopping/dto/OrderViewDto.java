package com.online_shopping.dto;

import com.online_shopping.entity.order.Order;
import com.online_shopping.entity.order.OrderProduct;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
//@JsonFilter("orderViewFilter")
public class OrderViewDto {

    private int id;
    private int totalAmount;
    private Date orderDate;
    private String paymentMethod;
    private String customerAddressAddressLine;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private int customerAddressZipcode;
    private String customerAddressLabel;
    private List<OrderProductViewDto> productList;


    public OrderViewDto(Order order) {

        this.id = order.getId();
        this.totalAmount = order.getAmount();
        this.orderDate = order.getDateCreated();
        this.paymentMethod = order.getPaymentMethod();
        this.customerAddressAddressLine = order.getCustomerAddressAddressLine();
        this.customerAddressCity = order.getCustomerAddressCity();
        this.customerAddressState = order.getCustomerAddressState();
        this.customerAddressCountry = order.getCustomerAddressCountry();
        this.customerAddressZipcode = order.getCustomerAddressZipcode();
        this.customerAddressLabel = order.getCustomerAddressLabel();
        this.productList = initProductList(order.getOrderProductList());

    }

    private List<OrderProductViewDto> initProductList(List<OrderProduct> orderProductList) {

        List<OrderProductViewDto> orderProductViewDtoList = new ArrayList<>();

        for(OrderProduct orderProduct : orderProductList )
            orderProductViewDtoList.add(new OrderProductViewDto(orderProduct,orderProduct.getOrderStatus()));

        return orderProductViewDtoList;
    }

}
