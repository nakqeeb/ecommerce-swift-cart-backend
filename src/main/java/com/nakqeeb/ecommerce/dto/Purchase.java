// Purchase.java
package com.nakqeeb.ecommerce.dto;

import com.nakqeeb.ecommerce.entity.Address;
import com.nakqeeb.ecommerce.entity.Order;
import com.nakqeeb.ecommerce.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class Purchase {
    private Order order;
    private Set<OrderItem> orderItems;
    private Address billingAddress;
    private Address shippingAddress;
}