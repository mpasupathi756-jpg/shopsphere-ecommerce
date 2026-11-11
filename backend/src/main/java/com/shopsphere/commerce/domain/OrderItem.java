package com.shopsphere.commerce.domain;
import com.shopsphere.marketplace.domain.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="order_items")
public class OrderItem {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="seller_order_id",nullable=false) private SellerOrder sellerOrder;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="listing_id",nullable=false) private Listing listing;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="variant_id") private ProductVariant variant;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="seller_id",nullable=false) private Seller seller;
 @Column(name="store_name_snapshot",nullable=false,length=120) private String storeNameSnapshot;
 @Column(name="product_title_snapshot",nullable=false,length=180) private String productTitleSnapshot;
 @Column(name="seller_sku_snapshot",nullable=false,length=100) private String sellerSkuSnapshot;
 @Column(nullable=false) private int quantity;
 @Column(name="unit_price_minor",nullable=false) private long unitPriceMinor;
 @Column(name="line_total_minor",nullable=false) private long lineTotalMinor;
 @Column(nullable=false,length=3) private String currency;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected OrderItem(){}
 public UUID getId(){return id;} public SellerOrder getSellerOrder(){return sellerOrder;} public Listing getListing(){return listing;} public Product getProduct(){return product;} public ProductVariant getVariant(){return variant;} public Seller getSeller(){return seller;} public String getStoreNameSnapshot(){return storeNameSnapshot;} public String getProductTitleSnapshot(){return productTitleSnapshot;} public String getSellerSkuSnapshot(){return sellerSkuSnapshot;} public int getQuantity(){return quantity;} public long getUnitPriceMinor(){return unitPriceMinor;} public long getLineTotalMinor(){return lineTotalMinor;} public String getCurrency(){return currency;}\n public OrderItem(SellerOrder sellerOrder,Listing listing,int quantity){this.sellerOrder=sellerOrder;this.listing=listing;this.product=listing.getProduct();this.variant=listing.getVariant();this.seller=listing.getSeller();this.storeNameSnapshot=seller.getStoreName();this.productTitleSnapshot=product.getTitle();this.sellerSkuSnapshot=listing.getSellerSku();this.quantity=quantity;this.unitPriceMinor=listing.getPriceMinor();this.lineTotalMinor=Math.multiplyExact(unitPriceMinor,quantity);this.currency=listing.getCurrency();this.createdAt=Instant.now();}
}
