package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category,UUID>{List<Category> findAllByStatusOrderByDisplayOrderAsc(String status);Optional<Category> findBySlugAndStatus(String slug,String status);}
