package com.shopsphere.commerce.api;
import java.util.UUID;
public record WishlistItemResponse(UUID productId,String title,String slug,String categoryName,long startingPriceMinor,String currency){}
