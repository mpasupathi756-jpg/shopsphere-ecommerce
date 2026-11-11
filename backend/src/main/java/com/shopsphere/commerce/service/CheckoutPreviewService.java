package com.shopsphere.commerce.service;
import com.shopsphere.commerce.api.*;
import com.shopsphere.commerce.domain.*;
import com.shopsphere.commerce.repository.*;
import com.shopsphere.marketplace.domain.*;
import com.shopsphere.marketplace.repository.InventoryRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class CheckoutPreviewService {
 private final CustomerAddressRepository addresses; private final CartRepository carts; private final CartItemRepository items; private final InventoryRepository inventory;
 public CheckoutPreviewService(CustomerAddressRepository addresses,CartRepository carts,CartItemRepository items,InventoryRepository inventory){this.addresses=addresses;this.carts=carts;this.items=items;this.inventory=inventory;}
 @Transactional(readOnly=true) public CheckoutPreviewResponse preview(UUID userId,UUID addressId){CustomerAddress address=addresses.findByIdAndUser_Id(addressId,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Shipping address not found"));Cart cart=carts.findByUser_Id(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Your cart is empty"));List<CartItem> cartItems=items.findAllByCart_IdOrderByCreatedAtAsc(cart.getId());if(cartItems.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Your cart is empty");Map<UUID,List<CartItem>> bySeller=new LinkedHashMap<>();for(CartItem item:cartItems){Listing listing=item.getListing();if(!"ACTIVE".equals(listing.getListingStatus())||!"ACTIVE".equals(listing.getProduct().getProductStatus())||!"APPROVED".equals(listing.getSeller().getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"A cart offer is no longer available; refresh your cart");int available=inventory.findByListing_Id(listing.getId()).map(Inventory::getAvailableQty).orElse(0);if(available<item.getQuantity())throw new ResponseStatusException(HttpStatus.CONFLICT,"Stock changed for "+listing.getProduct().getTitle()+"; update your cart");bySeller.computeIfAbsent(listing.getSeller().getId(),key->new ArrayList<>()).add(item);}
 List<SellerCheckoutGroup> groups=new ArrayList<>();long subtotal=0;for(List<CartItem> sellerItems:bySeller.values()){Listing first=sellerItems.get(0).getListing();List<CheckoutPreviewLine> lines=new ArrayList<>();long sellerSubtotal=0;for(CartItem item:sellerItems){Listing listing=item.getListing();long line=Math.multiplyExact(listing.getPriceMinor(),item.getQuantity());sellerSubtotal=Math.addExact(sellerSubtotal,line);lines.add(new CheckoutPreviewLine(listing.getId(),listing.getProduct().getTitle(),listing.getSellerSku(),item.getQuantity(),listing.getPriceMinor(),line,listing.getCurrency()));}subtotal=Math.addExact(subtotal,sellerSubtotal);groups.add(new SellerCheckoutGroup(first.getSeller().getId(),first.getSeller().getStoreName(),lines,sellerSubtotal,0,sellerSubtotal));}
 AddressResponse shipping=new AddressResponse(address.getId(),address.getRecipientName(),address.getPhone(),address.getAddressLine1(),address.getAddressLine2(),address.getCity(),address.getState(),address.getPostalCode(),address.getCountry(),address.isDefaultAddress());return new CheckoutPreviewResponse(shipping,groups,subtotal,0,0,subtotal,"INR",false,"Payment is not configured yet. This is a price and stock preview only: no order is created, no stock is reserved and no payment is collected.");}
}
