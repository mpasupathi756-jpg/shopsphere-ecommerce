package com.shopsphere.commerce.api;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record AddressRequest(@NotBlank @Size(max=120) String recipientName,@NotBlank @Pattern(regexp="^[+0-9() -]{8,24}$") String phone,@NotBlank @Size(max=180) String addressLine1,@Size(max=180) String addressLine2,@NotBlank @Size(max=100) String city,@NotBlank @Size(max=100) String state,@NotBlank @Pattern(regexp="^[A-Za-z0-9 -]{4,12}$") String postalCode,@Size(max=80) String country,boolean defaultAddress){}
