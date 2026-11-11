package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="listings",uniqueConstraints=@UniqueConstraint(columnNames={"seller_id","seller_sku"}))
public class Listing {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="variant_id") private ProductVariant variant;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="seller_id",nullable=false) private Seller seller;
 @Column(name="seller_sku",nullable=false,length=100) private String sellerSku;
 @Column(name="price_minor",nullable=false) private long priceMinor;
 @Column(nullable=false,length=3) private String currency="INR";
 @Column(name="item_condition",nullable=false,length=20) private String itemCondition="NEW";
 @Column(name="listing_status",nullable=false,length=24) private String listingStatus="PENDING_REVIEW";
 @Column(name="shipping_days_min",nullable=false) private short shippingDaysMin=2;
 @Column(name="shipping_days_max",nullable=false) private short shippingDaysMax=7;
 @Column(name="return_window_days",nullable=false) private short returnWindowDays=7;
 @Column(name="warranty_text",length=300) private String warrantyText;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Version @Column(nullable=false) private long version;
 protected Listing(){}
 public Listing(Product product,ProductVariant variant,Seller seller,String sellerSku,long priceMinor,String currency,String itemCondition,short shippingDaysMin,short shippingDaysMax,short returnWindowDays,String warrantyText){this.product=product;this.variant=variant;this.seller=seller;this.sellerSku=sellerSku;this.priceMinor=priceMinor;this.currency=currency;this.itemCondition=itemCondition;this.shippingDaysMin=shippingDaysMin;this.shippingDaysMax=shippingDaysMax;this.returnWindowDays=returnWindowDays;this.warrantyText=warrantyText;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public Product getProduct(){return product;} public ProductVariant getVariant(){return variant;} public Seller getSeller(){return seller;} public String getSellerSku(){return sellerSku;} public long getPriceMinor(){return priceMinor;} public String getCurrency(){return currency;} public String getItemCondition(){return itemCondition;} public String getListingStatus(){return listingStatus;} public short getShippingDaysMin(){return shippingDaysMin;} public short getShippingDaysMax(){return shippingDaysMax;} public short getReturnWindowDays(){return returnWindowDays;} public String getWarrantyText(){return warrantyText;} public void approve(){listingStatus="ACTIVE";} public void pause(){listingStatus="PAUSED";}
}
