package com.shopsphere.commerce.api;
import java.util.List;
public record CartResponse(List<CartItemResponse> items,int itemCount,long subtotalMinor,String currency){}
