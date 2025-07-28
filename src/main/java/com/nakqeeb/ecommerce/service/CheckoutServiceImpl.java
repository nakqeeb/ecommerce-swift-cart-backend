package com.nakqeeb.ecommerce.service;

import com.nakqeeb.ecommerce.dao.ProductRepository;
import com.nakqeeb.ecommerce.dao.UserRepository;
import com.nakqeeb.ecommerce.dto.*;
import com.nakqeeb.ecommerce.entity.*;
import com.nakqeeb.ecommerce.enums.OrderStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CheckoutServiceImpl(UserRepository userRepository,
                               ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Order order = purchase.getOrder();

        // Generate tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);

        // Set order status to Received
        order.setStatus(OrderStatus.Received);

        // Process order items
        Set<OrderItem> orderItems = purchase.getOrderItems();
        orderItems.forEach(item -> order.add(item));  // This sets the order reference in items

        // Process addresses
        Address billingAddress = purchase.getBillingAddress();
        Address shippingAddress = purchase.getShippingAddress();

        // Set addresses to user (to ensure they're persisted)
        billingAddress.setUser(user);
        shippingAddress.setUser(user);
        user.addAddress(billingAddress);
        user.addAddress(shippingAddress);

        // Set addresses to order
        order.setBillingAddress(billingAddress);
        order.setShippingAddress(shippingAddress);

        // Calculate totals
        calculateOrderTotals(order, orderItems);

        // Set user to order and add to user's orders
        order.setUser(user);
        user.addOrder(order);

        // Save to database
        userRepository.save(user);

        return new PurchaseResponse(orderTrackingNumber);
    }

    @Override
    @Transactional
    public List<OrderResponse> getOrdersForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Get all product IDs from all order items
        Set<Long> productIds = user.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());

        // Fetch products in bulk
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return user.getOrders().stream()
                .map(order -> convertToOrderResponse(order, productMap))
                .collect(Collectors.toList());
    }

    private OrderResponse convertToOrderResponse(Order order, Map<Long, Product> productMap) {
        OrderResponse response = new OrderResponse();
        response.setOrderTrackingNumber(order.getOrderTrackingNumber());
        response.setTotalQuantity(order.getTotalQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setDateCreated(order.getDateCreated());

        // Map order items with product names
        response.setOrderItems(order.getOrderItems().stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setImageUrl(item.getImageUrl());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setProductId(item.getProductId());

            // Get product name from productMap
            Product product = productMap.get(item.getProductId());
            itemResponse.setProductName(product != null ? product.getName() : "Product Not Available");

            return itemResponse;
        }).collect(Collectors.toList()));

        // Map addresses
        response.setBillingAddress(convertToAddressResponse(order.getBillingAddress()));
        response.setShippingAddress(convertToAddressResponse(order.getShippingAddress()));

        return response;
    }

    private AddressResponse convertToAddressResponse(Address address) {
        if (address == null) return null;

        AddressResponse response = new AddressResponse();
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setZipCode(address.getZipCode());
        return response;
    }

    private void associateAddressesWithUser(User user, Purchase purchase) {
        Address billingAddress = purchase.getBillingAddress();
        Address shippingAddress = purchase.getShippingAddress();

        // Set user for addresses
        billingAddress.setUser(user);
        shippingAddress.setUser(user);

        // Add addresses to user
        user.addAddress(billingAddress);
        user.addAddress(shippingAddress);
    }

    private void calculateOrderTotals(Order order, Set<OrderItem> orderItems) {
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            totalQuantity += item.getQuantity();
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        order.setTotalQuantity(totalQuantity);
        order.setTotalPrice(totalPrice);
    }

    private String generateOrderTrackingNumber() {
        return UUID.randomUUID().toString();
    }
}