package com.shopsphere.marketplace.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="inventory_movements")
public class InventoryMovement {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="inventory_id",nullable=false) private Inventory inventory;
 @Column(name="movement_type",nullable=false,length=24) private String movementType;
 @Column(name="quantity_delta",nullable=false) private int quantityDelta;
 @Column(name="reference_type",length=40) private String referenceType;
 @Column(name="reference_id") private UUID referenceId;
 @Column(length=300) private String note;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by_user_id") private UserAccount createdBy;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected InventoryMovement(){}
 public InventoryMovement(Inventory inventory,String movementType,int quantityDelta,String note,UserAccount createdBy){this.inventory=inventory;this.movementType=movementType;this.quantityDelta=quantityDelta;this.note=note;this.createdBy=createdBy;this.createdAt=Instant.now();}
}
