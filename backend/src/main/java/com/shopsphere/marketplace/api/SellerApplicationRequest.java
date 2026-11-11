package com.shopsphere.marketplace.api;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record SellerApplicationRequest(@NotBlank @Size(max=180) String legalName,@NotBlank @Size(max=120) String storeName,@NotBlank @Email @Size(max=254) String businessEmail,@NotBlank @Pattern(regexp="^[+0-9() -]{8,24}$") String phone,@NotBlank @Size(max=500) String businessAddress,@Pattern(regexp="^$|^[0-9A-Z]{15}$",message="GSTIN must contain 15 uppercase letters or digits") String gstin){}
