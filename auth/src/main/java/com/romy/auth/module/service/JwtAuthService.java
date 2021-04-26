package com.romy.auth.module.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.romy.auth.common.logger.Log;
import com.romy.auth.entity.AuthUser;
import com.romy.auth.repository.AuthUserRepository;

@Service
public class JwtAuthService implements UserDetailsService {

	@Autowired
	private AuthUserRepository authUserRepository;
	
	@Override
	public UserDetails loadUserByUsername(String userKey) throws UsernameNotFoundException {

		Log.DebugStart();
		
		AuthUser user = authUserRepository.findByUserKey(userKey);
		if(user == null) {
			throw new UsernameNotFoundException(userKey);
		}
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getUserDivCd()));
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getTenantKey()));

		Log.DebugEnd();
		
		return new User(user.getUserKey(), user.getPassword(), grantedAuthorities);
	}

	public AuthUser authenticateByIdAndUserDivCd(String id, String userDivCd) {

		return authUserRepository.findByUserIdAndUserDivCd(id, userDivCd);
	}
	
	public AuthUser authenticateByIdAndTenantKey(String id, String tenantKey) {
		
		return authUserRepository.findByUserIdAndTenantKeyAndUserDivCd(id, tenantKey, "U");
	}
	
}
