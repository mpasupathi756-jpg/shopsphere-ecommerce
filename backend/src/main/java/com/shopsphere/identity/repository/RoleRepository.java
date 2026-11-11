package com.shopsphere.identity.repository;
import com.shopsphere.identity.domain.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoleRepository extends JpaRepository<Role,UUID>{Optional<Role> findByCode(String code);}
