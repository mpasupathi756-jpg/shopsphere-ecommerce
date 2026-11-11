package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="inventory")
public class Inventory {
 @Id @UuidGenerator private UUID id;
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="listing_id",nullable=false,unique=true) private Listing listing;
 @Column(name="available_qty",nullable=false) private int availableQty;
 @Column(name="reserved_qty",nullable=false) private int reservedQty;
 @Version @Column(nullable=false) private long version;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Inventory(){}
 public Inventory(Listing listing,int availableQty){this.listing=listing;this.availableQty=availableQty;this.reservedQty=0;this.updatedAt=Instant.now();}
 public UUID getId(){return id;} public Listing getListing(){return listing;} public int getAvailableQty(){return availableQty;} public int getReservedQty(){return reservedQty;}
 public void adjustAvailable(int delta){if(availableQty+delta<0)throw new IllegalArgumentException("Available inventory cannot be negative");availableQty+=delta;updatedAt=Instant.now();}
 public void reserve(int quantity){if(quantity<=0||availableQty<quantity)throw new IllegalArgumentException("Insufficient available stock");availableQty-=quantity;reservedQty+=quantity;updatedAt=Instant.now();}
 public void release(int quantity){if(quantity<=0||reservedQty<quantity)throw new IllegalArgumentException("Invalid stock reservation release");reservedQty-=quantity;availableQty+=quantity;updatedAt=Instant.now();}
 public void completeReservation(int quantity){if(quantity<=0||reservedQty<quantity)throw new IllegalArgumentException("Invalid stock reservation completion");reservedQty-=quantity;updatedAt=Instant.now();}
}
