package com.romy.file.module.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.file.entity.FilePath;
import com.romy.file.repository.FilePathRepository;

@Service
public class FilePathService {

	@Autowired
	private FilePathRepository filePathRepository;
	
	@Autowired
	private FileListService fileListService;
	
	
	/**
	 * 파일업로드
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List uploadFile(Map<String, Object> paramMap) throws Exception {
		
		List fileList = (ArrayList) paramMap.get("fileList");
		
		// 파일 리스트
		if(fileList != null && fileList.size() > 0) {
			
			for (Integer i=0, size=fileList.size(); i < size; i++) {
				
				Map<String, Object> mapFile = (Map<String, Object>) fileList.get(i);
				
				String fileId = (String) mapFile.get("fileid");
				
				FilePath filePath = null;
				
				// 첨부파일 경로 데이터 생성
				if(fileId == null || "".equals(fileId)) {
					filePath = new FilePath();
					filePath.setFilePath((String) mapFile.get("filepath"));
					filePath = filePathRepository.save(filePath);
				} else {
					Optional<FilePath> path = filePathRepository.findById(Long.parseLong(fileId));
					if(path.isPresent()) {
						filePath = path.get();
					}
				}
				
				// 첨부파일 저장
				fileListService.saveFileList(filePath, mapFile);
				
				mapFile.put("saveFileId", filePath.getFileId());
			}
		}
		
		return fileList;
	}
	
	/**
	 * FileId에 해당하는 Root Path 조회
	 * @param fileId
	 * @return
	 */
	public String getFilePathByFileId(String fileId) {
		
		Optional<FilePath> path = filePathRepository.findById(Long.parseLong(fileId));
		if(path.isPresent()) {
			return path.get().getFilePath();
		}
		
		return "";
	}
}
