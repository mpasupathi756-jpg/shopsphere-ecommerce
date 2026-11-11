package com.shopsphere.marketplace.api;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
public record ProductSubmissionRequest(@NotNull UUID categoryId,UUID brandId,@NotBlank @Size(max=180) String title,@NotBlank @Size(max=5000) String description){}
