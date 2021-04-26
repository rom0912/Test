package com.romy.auth.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.auth.entity.AuthUser;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, String> {

	AuthUser findByUserKey(String userKey);
	
	AuthUser findByUserIdAndUserDivCd(String id, String userDivCd);
	
	AuthUser findByUserIdAndTenantKey(String id, String tenantKey);
	
	AuthUser findByUserIdAndTenantKeyAndUserDivCd(String id, String tenantKey, String userDivCd);
	
	AuthUser findByUserIdAndPasswordAndTenantKey(String id, String password, String tenantKey);
	
	AuthUser findByUserKeyAndTenantKey(String userKey, String tenantKey);
	
	List<AuthUser> findByUserDivCdAndTenantKeyIn(String userDivCd, Collection<String> tenantKeys);
	
	AuthUser findByUserDivCdAndTenantKey(String userDivCd, String tenantKey);
	
	List<AuthUser> findByTenantKey(String tenantKey);
	
}
