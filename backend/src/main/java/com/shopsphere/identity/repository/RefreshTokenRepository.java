package com.shopsphere.identity.repository;
import com.shopsphere.identity.domain.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,UUID>{
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select token from RefreshToken token join fetch token.user where token.tokenHash = :hash") Optional<RefreshToken> findByHashForUpdate(@Param("hash") String hash);
}
