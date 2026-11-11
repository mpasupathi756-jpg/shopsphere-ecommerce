package com.shopsphere.commerce.api;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CheckoutPreviewRequest(@NotNull UUID addressId){}
