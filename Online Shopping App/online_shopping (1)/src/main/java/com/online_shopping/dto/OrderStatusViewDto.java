package com.online_shopping.dto;

import com.online_shopping.entity.order.OrderStatus;
import lombok.Data;

@Data
//@JsonFilter("orderStatusViewFilter")
public class OrderStatusViewDto {

    private String transitionNotesComments="No Additional comments";
    private String fromStatus;
    private String toStatus;

    public OrderStatusViewDto(OrderStatus orderStatus) {
        if(transitionNotesComments!=null && transitionNotesComments.length()>0)
            this.transitionNotesComments = orderStatus.getTransitionNotesComments();

        this.fromStatus = orderStatus.getFromStatus();
        this.toStatus = orderStatus.getToStatus();
    }
}
