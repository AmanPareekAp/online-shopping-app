package com.online_shopping.repository;

import com.online_shopping.entity.order.OrderProduct;
import com.online_shopping.entity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus,Integer> {
    OrderStatus findByOrderProduct(OrderProduct orderProduct);
}
