package com.romy.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.auth.entity.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

	AuthToken findByUserKey(String userKey);
	
	AuthToken findByUserKeyAndRefreshToken(String userKey, String refreshToken);
	
}
