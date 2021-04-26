package com.romy.auth.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.ToString;

@Entity
@ToString
public class UserDetail implements UserDetails {

	private static final long serialVersionUID = -4608347932140057654L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;                                                                                         
	
	private String username;
	
	private String password;
	
	private enum UserRole {
	    ROLE_USER,
	    ROLE_ADMIN
	}
	
	private UserRole roles;
	
	@Column
	private String accessToken;
	
	@Column
	private String refreshToken;
	
	@Transient
    private Collection<? extends GrantedAuthority> authorities;
	
    @Transient
    private boolean accountNonExpired = true;
    
    @Transient
    private boolean accountNonLocked = true;
    
    @Transient
    private boolean credentialsNonExpired = true;
    
    @Transient
    private boolean enabled = true;

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public UserRole getRoles() {
		return roles;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRoles(UserRole roles) {
		this.roles = roles;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
