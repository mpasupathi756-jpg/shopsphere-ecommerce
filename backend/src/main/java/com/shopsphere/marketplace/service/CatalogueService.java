package com.shopsphere.marketplace.service;
import com.shopsphere.marketplace.api.*;
import com.shopsphere.marketplace.domain.*;
import com.shopsphere.marketplace.repository.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class CatalogueService {
 private final CategoryRepository categories; private final ProductRepository products; private final ListingRepository listings; private final InventoryRepository inventory; private final ProductAttributeRepository attributes; private final ProductImageRepository images;
 public CatalogueService(CategoryRepository categories,ProductRepository products,ListingRepository listings,InventoryRepository inventory,ProductAttributeRepository attributes,ProductImageRepository images){this.categories=categories;this.products=products;this.listings=listings;this.inventory=inventory;this.attributes=attributes;this.images=images;}
 @Transactional(readOnly=true) public List<CategoryResponse> categories(){return categories.findAllByStatusOrderByDisplayOrderAsc("ACTIVE").stream().map(c->new CategoryResponse(c.getId(),c.getName(),c.getSlug(),c.getDescription(),c.getDisplayOrder())).toList();}
 @Transactional(readOnly=true) public Page<ProductCardResponse> search(String query,String categorySlug,String brandSlug,int page,int size){if(page<0||size<1||size>24)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page must be non-negative and page size must be between 1 and 24");String q=blankToNull(query),category=blankToNull(categorySlug),brand=blankToNull(brandSlug);return products.searchActiveProducts(q,category,brand,PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"))).map(this::card);}
 @Transactional(readOnly=true) public ProductDetailResponse detail(String slug){Product product=products.findBySlugAndProductStatus(slug,"ACTIVE").orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));List<ProductAttributeResponse> attrs=attributes.findAllByProduct_IdOrderBySortOrderAsc(product.getId()).stream().map(a->new ProductAttributeResponse(a.getAttributeKey(),a.getDisplayName(),a.getAttributeValue(),a.getUnit())).toList();List<ProductImageResponse> pics=images.findAllByProduct_IdOrderBySortOrderAsc(product.getId()).stream().map(i->new ProductImageResponse(i.getStorageKey(),i.getAltText(),i.isPrimary())).toList();List<ListingOfferResponse> offers=listings.findAllByProduct_IdAndListingStatusOrderByPriceMinorAsc(product.getId(),"ACTIVE").stream().map(this::offer).toList();return new ProductDetailResponse(product.getId(),product.getTitle(),product.getSlug(),product.getDescription(),product.getCategory().getName(),product.getBrand()==null?null:product.getBrand().getName(),attrs,pics,offers);}
 private ProductCardResponse card(Product product){List<Listing> active=listings.findAllByProduct_IdAndListingStatusOrderByPriceMinorAsc(product.getId(),"ACTIVE");Listing lowest=active.get(0);List<ProductImage> productImages=images.findAllByProduct_IdOrderBySortOrderAsc(product.getId());ProductImage primary=productImages.stream().filter(ProductImage::isPrimary).findFirst().orElse(productImages.isEmpty()?null:productImages.get(0));int sellerCount=(int)active.stream().map(l->l.getSeller().getId()).distinct().count();return new ProductCardResponse(product.getId(),product.getTitle(),product.getSlug(),product.getCategory().getName(),product.getBrand()==null?null:product.getBrand().getName(),lowest.getPriceMinor(),lowest.getCurrency(),sellerCount,primary==null?null:primary.getStorageKey());}
 private ListingOfferResponse offer(Listing listing){int qty=inventory.findByListing_Id(listing.getId()).map(Inventory::getAvailableQty).orElse(0);return new ListingOfferResponse(listing.getId(),listing.getSeller().getId(),listing.getSeller().getStoreName(),listing.getSellerSku(),listing.getPriceMinor(),listing.getCurrency(),listing.getItemCondition(),qty,listing.getShippingDaysMin(),listing.getShippingDaysMax(),listing.getReturnWindowDays(),listing.getWarrantyText());}
 private String blankToNull(String value){return value==null||value.isBlank()?null:value.trim();}
}
