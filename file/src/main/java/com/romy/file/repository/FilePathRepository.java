package com.romy.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romy.file.entity.FilePath;

@Repository
public interface FilePathRepository extends JpaRepository<FilePath, Long> {

}
