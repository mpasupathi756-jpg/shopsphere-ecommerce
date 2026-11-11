package com.shopsphere.marketplace.api;
import java.util.UUID;
public record CategoryResponse(UUID id,String name,String slug,String description,int displayOrder){}
