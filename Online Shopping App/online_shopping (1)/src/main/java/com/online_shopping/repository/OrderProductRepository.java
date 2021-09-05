package com.online_shopping.repository;

import com.online_shopping.entity.order.Order;
import com.online_shopping.entity.order.OrderProduct;
import com.online_shopping.entity.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct,Integer> {
    List<OrderProduct> findByOrder(Order order);
    List<OrderProduct> findByProductVariation(ProductVariation productVariation);
}
