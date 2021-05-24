package com.romy.file.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FileListId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Column(name = "SEQ_NO")
	private Integer seqNo;
	
	public FileListId() {
		
	}
	
	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}
}
