package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="product_attributes",uniqueConstraints=@UniqueConstraint(columnNames={"product_id","attribute_key"}))
public class ProductAttribute {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(name="attribute_key",nullable=false,length=80) private String attributeKey;
 @Column(name="display_name",nullable=false,length=100) private String displayName;
 @Column(name="attribute_value",nullable=false,length=500) private String attributeValue;
 @Column(length=30) private String unit;
 @Column(name="sort_order",nullable=false) private int sortOrder;
 protected ProductAttribute(){}
 public UUID getId(){return id;} public String getAttributeKey(){return attributeKey;} public String getDisplayName(){return displayName;} public String getAttributeValue(){return attributeValue;} public String getUnit(){return unit;} public int getSortOrder(){return sortOrder;}
}
