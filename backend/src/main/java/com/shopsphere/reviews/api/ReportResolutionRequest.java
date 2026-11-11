package com.shopsphere.reviews.api;
import jakarta.validation.constraints.*;
public record ReportResolutionRequest(@NotBlank @Pattern(regexp="DISMISSED|ACTIONED") String action,@Size(max=500) String note) {}