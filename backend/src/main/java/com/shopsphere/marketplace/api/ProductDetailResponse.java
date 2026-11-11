package com.shopsphere.marketplace.api;
import java.util.List;
import java.util.UUID;
public record ProductDetailResponse(UUID id,String title,String slug,String description,String categoryName,String brandName,List<ProductAttributeResponse> attributes,List<ProductImageResponse> images,List<ListingOfferResponse> offers,Double averageRating,long reviewCount){}