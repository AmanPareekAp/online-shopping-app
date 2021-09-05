package com.online_shopping.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    private String transitionNotesComments;

    @OneToOne
    @JoinColumn(name = "order_product_id",referencedColumnName = "id")
    private OrderProduct orderProduct;

    private String fromStatus;
    private String toStatus;

}
