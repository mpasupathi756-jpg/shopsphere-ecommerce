package com.shopsphere.commerce.api;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CheckoutOrderRequest(@NotNull UUID addressId){}
