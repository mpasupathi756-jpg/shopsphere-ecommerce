package com.shopsphere.commerce.service;
import com.shopsphere.commerce.api.WishlistItemResponse;
import com.shopsphere.commerce.domain.Wishlist;
import com.shopsphere.commerce.domain.WishlistItem;
import com.shopsphere.commerce.repository.WishlistItemRepository;
import com.shopsphere.commerce.repository.WishlistRepository;
import com.shopsphere.identity.repository.UserAccountRepository;
import com.shopsphere.marketplace.domain.Listing;
import com.shopsphere.marketplace.domain.Product;
import com.shopsphere.marketplace.repository.ListingRepository;
import com.shopsphere.marketplace.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class WishlistService {
 private final WishlistRepository wishlists; private final WishlistItemRepository items; private final UserAccountRepository users; private final ProductRepository products; private final ListingRepository listings;
 public WishlistService(WishlistRepository wishlists,WishlistItemRepository items,UserAccountRepository users,ProductRepository products,ListingRepository listings){this.wishlists=wishlists;this.items=items;this.users=users;this.products=products;this.listings=listings;}
 @Transactional(readOnly=true) public List<WishlistItemResponse> get(UUID userId){Wishlist wishlist=wishlists.findByUser_Id(userId).orElse(null);if(wishlist==null)return List.of();return items.findAllByWishlist_IdOrderByCreatedAtDesc(wishlist.getId()).stream().map(this::toResponse).toList();}
 @Transactional public List<WishlistItemResponse> add(UUID userId,UUID productId){Product product=products.findByIdAndProductStatus(productId,"ACTIVE").orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Active product not found"));Wishlist wishlist=wishlists.findByUser_Id(userId).orElseGet(()->wishlists.save(new Wishlist(users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found")))));if(items.findByWishlist_IdAndProduct_Id(wishlist.getId(),productId).isEmpty())items.save(new WishlistItem(wishlist,product));return get(userId);}
 @Transactional public List<WishlistItemResponse> remove(UUID userId,UUID productId){Wishlist wishlist=wishlists.findByUser_Id(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Wishlist not found"));items.deleteByWishlist_IdAndProduct_Id(wishlist.getId(),productId);return get(userId);}
 private WishlistItemResponse toResponse(WishlistItem item){Product product=item.getProduct();List<Listing> offers=listings.findAllByProduct_IdAndListingStatusOrderByPriceMinorAsc(product.getId(),"ACTIVE");long price=offers.isEmpty()?0:offers.get(0).getPriceMinor();String currency=offers.isEmpty()?"INR":offers.get(0).getCurrency();return new WishlistItemResponse(product.getId(),product.getTitle(),product.getSlug(),product.getCategory().getName(),price,currency);}
}
