package com.shopsphere.reviews.api;
import jakarta.validation.constraints.*;
public record ReviewRequest(@Min(1) @Max(5) int rating,@NotBlank @Size(max=120) String title,@NotBlank @Size(max=3000) String body) {}