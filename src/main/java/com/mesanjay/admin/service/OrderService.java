package com.mesanjay.admin.service;


import com.mesanjay.admin.model.Order;
import com.mesanjay.admin.model.ShoppingCart;

import java.util.List;


public interface OrderService {
    Order save(ShoppingCart shoppingCart);

    List<Order> findAll(String username);

    List<Order> findALlOrders();

    Order acceptOrder(Long id);

    void cancelOrder(Long id);
}
