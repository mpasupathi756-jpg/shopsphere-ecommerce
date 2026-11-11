package com.shopsphere.assistant.api;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
public record ProductComparisonRequest(@NotEmpty @Size(min=2,max=4) List<@Size(min=1,max=160) String> slugs) {}
