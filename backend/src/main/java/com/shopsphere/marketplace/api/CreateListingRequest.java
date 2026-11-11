package com.shopsphere.marketplace.api;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.UUID;
public record CreateListingRequest(@NotNull UUID productId,UUID variantId,@NotBlank @Size(max=100) String sellerSku,@Positive long priceMinor,@Pattern(regexp="INR") String currency,@Pattern(regexp="NEW|REFURBISHED|USED") String condition,@Min(0) @Max(60) int shippingDaysMin,@Min(0) @Max(90) int shippingDaysMax,@Min(0) @Max(90) int returnWindowDays,@Size(max=300) String warrantyText,@Min(0) @Max(100000) int availableQuantity){}
