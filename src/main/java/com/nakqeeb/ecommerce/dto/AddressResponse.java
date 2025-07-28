package com.nakqeeb.ecommerce.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}