package com.romy.auth.module.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.entity.AuthTenant;
import com.romy.auth.repository.AuthTenantRepository;

@Service
public class AuthTenantService {

	@Autowired
	private AuthTenantRepository authTenantRepository;
	
	
	public boolean isExistAuthTenant(String tenantKey) throws Exception {
		
		Boolean bChk = false;
		
		AuthTenant tenant =  authTenantRepository.findByTenantKey(tenantKey);
		if(tenant != null) {
			bChk = true;
		}
		
		return bChk;
	}
	
	/**
	 * 테넌트 생성
	 * @param paramMap
	 * @return
	 */
	public AuthTenant saveAuthTenant(Map<String, Object> paramMap) {
		
		String tenantKey = (String) paramMap.get("tenantKey");
		String tenantName = (String) paramMap.get("tenantName");
		
		AuthTenant tenant = authTenantRepository.findByTenantKey(tenantKey);
		if(tenant == null) {
			tenant = new AuthTenant();
			
			tenant.setTenantKey(tenantKey);
			tenant.setTenantName(tenantName);
			
			tenant = authTenantRepository.save(tenant);
		}
		
		return tenant;
	}
	
	/**
	 * 테넌트 정보 조회
	 * @param tenantKey
	 * @return
	 */
	public AuthTenant getAuthTenantInfoByTenantKey(String tenantKey) {
		
		AuthTenant tenant = authTenantRepository.findByTenantKey(tenantKey);
		if(tenant != null) {
			return tenant;
		}
		
		return null;
	}
	
	/**
	 * 테넌트 삭제
	 * @param tenant
	 */
	public void deleteAuthTenantByTenantKey(AuthTenant tenant) {
		authTenantRepository.delete(tenant);
	}
	
}
