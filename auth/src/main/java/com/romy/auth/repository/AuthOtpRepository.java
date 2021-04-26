package com.romy.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.auth.entity.AuthOtp;

@Repository
public interface AuthOtpRepository extends JpaRepository<AuthOtp, String> {

	AuthOtp findByUserKey(String userKey);
	
	AuthOtp findByUserKeyAndOtp(String userKey, String otp);
	
}
