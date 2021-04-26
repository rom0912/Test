package com.romy.auth.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="AUTH_TOKEN")
public class AuthToken {

	@Id
	@Column(name = "USER_KEY")
	private String userKey;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "USER_KEY", referencedColumnName = "USER_KEY", insertable = false, updatable = false)
	private AuthUser authUser;
	
	@Column(name = "ACCESS_TOKEN")
	private String accessToken;
	
	@Column(name = "REFRESH_TOKEN")
	private String refreshToken;
	
	@Column(name = "PUSH_TOKEN")
	private String pushToken;
	
	@Column(name = "DEVICE")
	private String device;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;
	
	@Column(name = "MOD_DATE", updatable = false, insertable = false )
	private LocalDateTime modDate;

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public AuthUser getAuthUser() {
		return authUser;
	}

	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getModDate() {
		return modDate;
	}

	public void setModDate(LocalDateTime modDate) {
		this.modDate = modDate;
	}
	
}
