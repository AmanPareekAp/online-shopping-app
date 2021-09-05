package com.online_shopping.service.order;

import com.online_shopping.entity.order.Order;
import com.online_shopping.entity.user.Customer;
import com.online_shopping.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Order getLatestOrderForCustomer(Customer customer){

        return null;
    }
}
