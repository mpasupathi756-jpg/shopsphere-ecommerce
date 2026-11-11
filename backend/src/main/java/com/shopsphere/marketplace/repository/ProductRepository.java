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
 @Query("select p from Product p join p.category c left join p.brand b where p.productStatus='ACTIVE' and (:query is null or lower(p.title) like lower(concat('%',:query,'%'))) and (:categorySlug is null or c.slug=:categorySlug) and (:brandSlug is null or b.slug=:brandSlug) and exists (select l.id from Listing l where l.product=p and l.listingStatus='ACTIVE')")
 Page<Product> searchActiveProducts(@Param("query") String query,@Param("categorySlug") String categorySlug,@Param("brandSlug") String brandSlug,Pageable pageable);
}
