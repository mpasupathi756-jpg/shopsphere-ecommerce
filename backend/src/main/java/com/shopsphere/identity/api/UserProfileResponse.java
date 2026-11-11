package com.shopsphere.identity.api;
import java.util.List;
import java.util.UUID;
public record UserProfileResponse(UUID id,String email,String displayName,List<String> roles,boolean emailVerified){}
