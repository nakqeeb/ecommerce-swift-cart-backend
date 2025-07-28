package com.nakqeeb.ecommerce.service;

import com.nakqeeb.ecommerce.dto.OrderResponse;
import com.nakqeeb.ecommerce.dto.Purchase;
import com.nakqeeb.ecommerce.dto.PurchaseResponse;

import java.util.List;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);

    List<OrderResponse> getOrdersForCurrentUser();
}