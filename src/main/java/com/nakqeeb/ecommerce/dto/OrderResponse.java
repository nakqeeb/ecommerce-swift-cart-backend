// OrderResponse.java
package com.nakqeeb.ecommerce.dto;

import com.nakqeeb.ecommerce.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderResponse {
    private String orderTrackingNumber;
    private int totalQuantity;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Date dateCreated;
    private List<OrderItemResponse> orderItems;
    private AddressResponse billingAddress;
    private AddressResponse shippingAddress;
}