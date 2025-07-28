// OrderItemResponse.java
package com.nakqeeb.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private String productName;  // Will be populated from Product entity
    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private Long productId;
}