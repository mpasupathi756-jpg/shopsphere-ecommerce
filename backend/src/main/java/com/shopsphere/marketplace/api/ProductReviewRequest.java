package com.shopsphere.marketplace.api;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record ProductReviewRequest(@NotBlank @Size(max=500) String reason){}
