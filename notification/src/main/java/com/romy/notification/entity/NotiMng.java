package com.romy.notification.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.romy.notification.common.config.JpaConverterJsonObject;

@Entity
@Table(name="NOTI_MNG")
public class NotiMng {

	@Id
	@Column(name = "NOTI_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notiId;
	
	@Column(name = "USER_KEY")
	private String userKey;
	
	@Column(name = "MSG_INFO")
	@Convert(converter = JpaConverterJsonObject.class)
	private Object msgInfo;
	
	@Column(name = "READ_YN")
	private String readYn;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;
	
	@Column(name = "MOD_DATE", updatable = false, insertable = false)
	private LocalDateTime modDate;

	public Long getNotiId() {
		return notiId;
	}

	public void setNotiId(Long notiId) {
		this.notiId = notiId;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public Object getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(Object msgInfo) {
		this.msgInfo = msgInfo;
	}

	public String getReadYn() {
		return readYn;
	}

	public void setReadYn(String readYn) {
		this.readYn = readYn;
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
