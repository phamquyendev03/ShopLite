package com.quyen.shoplite.repository;

import com.quyen.shoplite.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {
    Optional<UserToken> findByRefreshTokenAndRevokedFalse(String refreshToken);
    List<UserToken> findByUser_IdAndRevokedFalse(Integer userId);
}
