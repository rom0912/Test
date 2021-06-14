package com.romy.log.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOG_LOGIN")
public class LogLogin {
	
	@Id
	@Column(name = "LOG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long logId;
	
	@Column(name = "USER_KEY")
	private String userKey;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "IP")
	private String ip;
	
	@Column(name = "LOGIN_AGENT")
	private String loginAgent;
	
	@Column(name = "TENANT_KEY")
	private String tenantKey;
	
	@Column(name = "LOGIN_DUP_YN")
	private String loginDupYn;
	
	@Column(name = "NOTE")
	private String note;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;
	
	@Column(name = "MOD_DATE", updatable = false, insertable = false)
	private LocalDateTime modDate;

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLoginAgent() {
		return loginAgent;
	}

	public void setLoginAgent(String loginAgent) {
		this.loginAgent = loginAgent;
	}

	public String getTenantKey() {
		return tenantKey;
	}

	public void setTenantKey(String tenantKey) {
		this.tenantKey = tenantKey;
	}

	public String getLoginDupYn() {
		return loginDupYn;
	}

	public void setLoginDupYn(String loginDupYn) {
		this.loginDupYn = loginDupYn;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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
