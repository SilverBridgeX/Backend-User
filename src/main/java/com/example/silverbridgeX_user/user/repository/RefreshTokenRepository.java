package com.example.silverbridgeX_user.user.repository;

import com.example.silverbridgeX_user.user.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
