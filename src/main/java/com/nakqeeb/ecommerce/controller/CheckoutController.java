package com.nakqeeb.ecommerce.controller;

import com.nakqeeb.ecommerce.dto.OrderResponse;
import com.nakqeeb.ecommerce.dto.Purchase;
import com.nakqeeb.ecommerce.dto.PurchaseResponse;
import com.nakqeeb.ecommerce.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin("http://localhost:4200")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/purchase")
    public PurchaseResponse placeOrder(@RequestBody Purchase purchase) {

        return checkoutService.placeOrder(purchase);
    }

    // In controller
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {

        List<OrderResponse> orders = checkoutService.getOrdersForCurrentUser();
        return ResponseEntity.ok(orders);
    }

}








