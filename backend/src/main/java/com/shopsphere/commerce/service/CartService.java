package com.shopsphere.commerce.service;
import com.shopsphere.commerce.api.*;
import com.shopsphere.commerce.domain.*;
import com.shopsphere.commerce.repository.*;
import com.shopsphere.identity.repository.UserAccountRepository;
import com.shopsphere.marketplace.domain.*;
import com.shopsphere.marketplace.repository.*;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class CartService {
 private final CartRepository carts; private final CartItemRepository items; private final UserAccountRepository users; private final ListingRepository listings; private final InventoryRepository inventory;
 public CartService(CartRepository carts,CartItemRepository items,UserAccountRepository users,ListingRepository listings,InventoryRepository inventory){this.carts=carts;this.items=items;this.users=users;this.listings=listings;this.inventory=inventory;}
 @Transactional(readOnly=true) public CartResponse get(UUID userId){Cart cart=carts.findByUser_Id(userId).orElse(null);if(cart==null)return new CartResponse(List.of(),0,0,"INR");return response(cart);}
 @Transactional public CartResponse add(UUID userId,CartItemRequest request){Listing listing=validListing(request.listingId());Inventory stock=inventory.findByListing_Id(listing.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.CONFLICT,"Inventory is not available"));Cart cart=getOrCreate(userId);CartItem existing=items.findByCart_IdAndListing_Id(cart.getId(),listing.getId()).orElse(null);int next=(existing==null?0:existing.getQuantity())+request.quantity();if(next>20)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Maximum quantity per cart line is 20");if(next>stock.getAvailableQty())throw new ResponseStatusException(HttpStatus.CONFLICT,"Requested quantity exceeds available stock");if(existing==null)items.save(new CartItem(cart,listing,next));else existing.setQuantity(next);return response(cart);}
 @Transactional public CartResponse update(UUID userId,UUID itemId,int quantity){CartItem item=items.findByIdAndCart_User_Id(itemId,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cart item not found"));Listing listing=validListing(item.getListing().getId());int available=inventory.findByListing_Id(listing.getId()).map(Inventory::getAvailableQty).orElse(0);if(quantity>available)throw new ResponseStatusException(HttpStatus.CONFLICT,"Requested quantity exceeds available stock");item.setQuantity(quantity);return response(item.getCart());}
 @Transactional public CartResponse remove(UUID userId,UUID itemId){CartItem item=items.findByIdAndCart_User_Id(itemId,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cart item not found"));Cart cart=item.getCart();items.delete(item);return response(cart);}
 private Cart getOrCreate(UUID userId){return carts.findByUser_Id(userId).orElseGet(()->carts.save(new Cart(users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found")))));}
 private Listing validListing(UUID id){Listing listing=listings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Listing not found"));if(!"ACTIVE".equals(listing.getListingStatus())||!"ACTIVE".equals(listing.getProduct().getProductStatus())||!"APPROVED".equals(listing.getSeller().getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"This seller offer is no longer available");return listing;}
 private CartResponse response(Cart cart){List<CartItemResponse> rows=items.findAllByCart_IdOrderByCreatedAtAsc(cart.getId()).stream().map(this::toResponse).toList();long subtotal=rows.stream().mapToLong(CartItemResponse::lineTotalMinor).reduce(0,Math::addExact);int count=rows.stream().mapToInt(CartItemResponse::quantity).sum();return new CartResponse(rows,count,subtotal,"INR");}
 private CartItemResponse toResponse(CartItem item){Listing listing=item.getListing();int available=inventory.findByListing_Id(listing.getId()).map(Inventory::getAvailableQty).orElse(0);return new CartItemResponse(item.getId(),listing.getId(),listing.getProduct().getId(),listing.getProduct().getTitle(),listing.getProduct().getSlug(),listing.getSeller().getStoreName(),listing.getPriceMinor(),listing.getCurrency(),item.getQuantity(),available,Math.multiplyExact(listing.getPriceMinor(),item.getQuantity()));}
}
