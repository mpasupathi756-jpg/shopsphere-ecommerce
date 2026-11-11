package com.shopsphere.reviews.api;
public record ReviewEligibilityResponse(boolean eligible,boolean alreadyReviewed,String message) {}