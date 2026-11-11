package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="product_variants")
public class ProductVariant {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(name="variant_name",nullable=false,length=120) private String variantName;
 @JdbcTypeCode(SqlTypes.JSON) @Column(name="attributes_json",nullable=false,columnDefinition="jsonb") private Map<String,String> attributes;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected ProductVariant(){}
 public UUID getId(){return id;} public Product getProduct(){return product;} public String getVariantName(){return variantName;} public Map<String,String> getAttributes(){return attributes;}
}
