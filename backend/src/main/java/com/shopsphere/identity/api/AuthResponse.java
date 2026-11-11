package com.shopsphere.identity.api;
public record AuthResponse(String accessToken,String tokenType,long expiresIn,UserProfileResponse user){}
