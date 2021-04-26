package com.romy.auth.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.romy.auth.common.config.JpaConverterJsonObject;

@Entity
@Table(name="AUTH_USER")
public class AuthUser {

	@Id
	@Column(name = "USER_KEY")
	private String userKey;
	
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "NOTE")
	private String note;
	
	@Column(name = "INIT_LOGIN_YN")
	private String initLoginYn;
	
	@Column(name = "ROCKING_YN")
	private String rockingYn;
	
	@Column(name = "USER_DIV_CD")
	private String userDivCd;
	
	@Column(name = "JOIN_YMD")
	private LocalDate joinYmd;
	
	@Column(name = "PHONE_NO")
	private String phoneNo;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "ADDRESS")
	@Convert(converter = JpaConverterJsonObject.class)
	private Object address;
	
	@Column(name = "TENANT_KEY")
	private String tenantKey;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;
	
	@Column(name = "MOD_DATE", updatable = false, insertable = false)
	private LocalDateTime modDate;

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getInitLoginYn() {
		return initLoginYn;
	}

	public void setInitLoginYn(String initLoginYn) {
		this.initLoginYn = initLoginYn;
	}

	public String getRockingYn() {
		return rockingYn;
	}

	public void setRockingYn(String rockingYn) {
		this.rockingYn = rockingYn;
	}

	public String getUserDivCd() {
		return userDivCd;
	}

	public void setUserDivCd(String userDivCd) {
		this.userDivCd = userDivCd;
	}

	public LocalDate getJoinYmd() {
		return joinYmd;
	}

	public void setJoinYmd(LocalDate joinYmd) {
		this.joinYmd = joinYmd;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Object getAddress() {
		return address;
	}

	public void setAddress(Object address) {
		this.address = address;
	}

	public String getTenantKey() {
		return tenantKey;
	}

	public void setTenantKey(String tenantKey) {
		this.tenantKey = tenantKey;
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
