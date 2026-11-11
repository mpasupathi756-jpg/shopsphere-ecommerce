package com.shopsphere.reviews.api;
import jakarta.validation.constraints.*;
public record ReviewReportRequest(@NotBlank @Pattern(regexp="SPAM|ABUSE|FAKE|IRRELEVANT|OTHER") String reasonCode,@Size(max=500) String details) {}