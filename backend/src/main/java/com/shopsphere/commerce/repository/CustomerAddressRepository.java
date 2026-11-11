package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.CustomerAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress,UUID>{List<CustomerAddress> findAllByUser_IdOrderByDefaultAddressDescCreatedAtDesc(UUID userId);Optional<CustomerAddress> findByIdAndUser_Id(UUID id,UUID userId);}
