package com.shopsphere.reviews.api;
import jakarta.validation.constraints.*;
public record ModerationRequest(@NotBlank @Pattern(regexp="PUBLISHED|HIDDEN|REMOVED") String action,@NotBlank @Size(max=500) String reason) {}