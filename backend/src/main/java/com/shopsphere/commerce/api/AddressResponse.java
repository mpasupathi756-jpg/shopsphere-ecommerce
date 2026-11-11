package com.shopsphere.commerce.api;
import java.util.UUID;
public record AddressResponse(UUID id,String recipientName,String phone,String addressLine1,String addressLine2,String city,String state,String postalCode,String country,boolean defaultAddress){}
