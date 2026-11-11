package com.shopsphere.commerce.api;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CartItemRequest(@NotNull UUID listingId,@Min(1) @Max(20) int quantity){}
