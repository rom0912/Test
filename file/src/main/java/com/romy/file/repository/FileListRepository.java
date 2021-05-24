package com.romy.file.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.file.entity.FileList;
import com.romy.file.entity.FileListId;

@Repository
public interface FileListRepository extends JpaRepository<FileList, FileListId> {

	Long countByIdFileId(Long fileId);
	
	List<FileList> findByIdFileId(Long fileId);
	
}
