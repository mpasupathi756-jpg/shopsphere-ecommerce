package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Listing;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ListingRepository extends JpaRepository<Listing,UUID>{List<Listing> findAllByProduct_IdAndListingStatusOrderByPriceMinorAsc(UUID productId,String listingStatus);Optional<Listing> findByIdAndSeller_Id(UUID id,UUID sellerId);boolean existsBySeller_IdAndSellerSkuIgnoreCase(UUID sellerId,String sellerSku);}
