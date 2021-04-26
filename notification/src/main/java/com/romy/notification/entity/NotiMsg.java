package com.romy.notification.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="NOTI_MSG")
public class NotiMsg {

	@Id
	@Column(name = "MSG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long msgId;
	
	@Column(name = "MSG_TYPE_CD")
	private String msgTypeCd;
	
	@Column(name = "TITLE")
	private String title;
	
	@Lob
	@Column(name = "MESSAGE")
	private String message;
	
	@Column(name = "SENDER")
	private String sender;
	
	@Column(name = "MODULE_NAME")
	private String moduleName;
	
	@Column(name = "SEND_DATE")
	private LocalDateTime sendDate;
	
	@Column(name = "NOTE")
	private String note;
	
	@Column(name = "CREATE_DATE", updatable = false, insertable = false)
	private LocalDateTime createDate;
	
	@Column(name = "MOD_DATE", updatable = false, insertable = false)
	private LocalDateTime modDate;

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public String getMsgTypeCd() {
		return msgTypeCd;
	}

	public void setMsgTypeCd(String msgTypeCd) {
		this.msgTypeCd = msgTypeCd;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
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
