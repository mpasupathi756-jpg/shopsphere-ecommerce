package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Seller;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SellerRepository extends JpaRepository<Seller,UUID>{Optional<Seller> findByUserId(UUID userId);boolean existsByUserId(UUID userId);List<Seller> findAllByStatusOrderBySubmittedAtDesc(String status);}
