package com.romy.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.auth.entity.AuthTenant;

@Repository
public interface AuthTenantRepository extends JpaRepository<AuthTenant, Long> {

	AuthTenant findByTenantKey(String tenantKey);
	
}
