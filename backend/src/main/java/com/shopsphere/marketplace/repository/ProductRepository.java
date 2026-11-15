package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ProductRepository extends JpaRepository<Product,UUID>{
 Optional<Product> findBySlugAndProductStatus(String slug,String productStatus);
 Optional<Product> findByIdAndProductStatus(UUID id,String productStatus);
 java.util.List<Product> findAllByCreatedBy_IdOrderByCreatedAtDesc(UUID userId);
 java.util.List<Product> findAllByProductStatusOrderByCreatedAtDesc(String productStatus);

 // Pass empty strings for omitted filters. Non-null string parameters avoid PostgreSQL
 // inferring an untyped NULL parameter as bytea in predicates such as lower(:query).
 @Query("select p from Product p join p.category c left join p.brand b where p.productStatus='ACTIVE' and (:query = '' or lower(p.title) like lower(concat('%',:query,'%'))) and (:categorySlug = '' or c.slug=:categorySlug) and (:brandSlug = '' or b.slug=:brandSlug) and exists (select l.id from Listing l where l.product=p and l.listingStatus='ACTIVE')")
 Page<Product> searchActiveProducts(@Param("query") String query,@Param("categorySlug") String categorySlug,@Param("brandSlug") String brandSlug,Pageable pageable);
}
