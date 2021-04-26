package com.romy.auth.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AUTH_TENANT")
public class AuthTenant {

	@Id
	@Column(name = "TENANT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tenantId;
	
	@Column(name = "TENANT_NAME")
	private String tenantName;
	
	@Column(name = "TENANT_KEY")
	private String tenantKey;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;
	
	@Column(name = "MOD_DATE")
	private LocalDateTime modDate;

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
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
