package com.romy.auth.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AUTH_RSA")
public class AuthRsa {

	@Id
	@Column(name = "SESSION_KEY")
	private String sessionKey;
	
	@Column(name = "RSA_KEY")
	private String rsaKey;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getRsaKey() {
		return rsaKey;
	}

	public void setRsaKey(String rsaKey) {
		this.rsaKey = rsaKey;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}
	
}
