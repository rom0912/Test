package com.romy.file.module.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.file.common.logger.Log;
import com.romy.file.entity.FileList;
import com.romy.file.entity.FileListId;
import com.romy.file.entity.FilePath;
import com.romy.file.repository.FileListRepository;

@Service
public class FileListService {

	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private FileListRepository fileListRepository;
	
	
	/**
	 * 파일 리스트 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFileListByFileId(Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> resultList = new ArrayList<>();
		
		List<String> listFileId = (ArrayList<String>) paramMap.get("fileIds");
		
		if(listFileId != null && listFileId.size() > 0) {
		
			for (String fileId : listFileId) {
				Map<String, Object> mapFile = new HashMap<>();
				List<FileList> fileList = fileListRepository.findByIdFileId(Long.parseLong(fileId));
				
				List<Map<String, Object>> listData = new ArrayList<Map<String,Object>>();
				for (FileList file : fileList) {
					Map<String, Object> data = new HashMap<String, Object>();
					
					data.put("fileId", file.getId().getFileId());
					data.put("seqNo", file.getId().getSeqNo());
					data.put("fileName", file.getFileName());
					data.put("fileSize", file.getFileSize());
					
					listData.add(data);
				}
				
				mapFile.put(fileId, listData);
				
				resultList.add(mapFile);
			}
		}
		
		return resultList;
	}
	
	
	/**
	 * 첨부파일 등록
	 * @param filePath
	 * @param jsonFile
	 * @throws Exception
	 */
	public void saveFileList(FilePath filePath, Map<String, Object> jsonFile) throws Exception {
		
		String fileNm = (String) jsonFile.get("filename");
		String[] fileArr = fileNm.split("\\.");
		UUID randUUID = UUID.randomUUID();
		StringBuilder sbSaveFileNm = new StringBuilder();
		sbSaveFileNm.append(randUUID).append(fileNm);
		
		Integer fileSize = (Integer) jsonFile.get("length");
		
		if(fileSize != null && fileSize > 0) {
			
			try {
				List<Integer> listSource = (ArrayList) jsonFile.get("source");
				
				byte[] fileSource = new byte[listSource.size()];
				for (Integer i=0, len=listSource.size(); i < len; i++) {
					fileSource[i] = (listSource.get(i)).byteValue();
				}
				
				File dir = new File(filePath.getFilePath() + File.separator + String.valueOf(filePath.getFileId()));
				if(!dir.exists()) {
					dir.mkdirs();
				}
				
				File fileInfo = new File(filePath.getFilePath() + File.separator + String.valueOf(filePath.getFileId())
						+ File.separator + sbSaveFileNm.toString());
				FileOutputStream fos = new FileOutputStream(fileInfo);
				fos.write(fileSource);
				fos.close();
				
				Long lCnt = fileListRepository.countByIdFileId(filePath.getFileId());
				
				FileListId id = new FileListId();
				id.setFileId(filePath.getFileId());
				id.setSeqNo(lCnt.intValue() + 1);
				
				FileList file = new FileList();
				file.setId(id);
				file.setFileName((String) jsonFile.get("filename"));
				file.setSaveFileName(sbSaveFileNm.toString());
				file.setFileSize(fileSize);
				file.setFileExt(fileArr[fileArr.length - 1]);
				
				fileListRepository.save(file);
				
			} catch(Exception e) {
				// 오류가 발생할 경우 파일 삭제하기
				File fileInfo = new File(filePath.getFilePath() + File.separator + String.valueOf(filePath.getFileId())
						+ File.separator + sbSaveFileNm.toString());
				
				if(fileInfo.exists()) {
					fileInfo.delete();
				}
				
				Log.Debug(e.getMessage());
				
				throw e;
			}
		}
	}

	/**
	 * 파일 삭제
	 * @param paramMap
	 * @throws Exception
	 */
	public void deleteFile(Map<String, Object> paramMap) throws Exception {
		
		String fileId = (String) paramMap.get("fileId");
		String seqNo = (String) paramMap.get("seqNo");
		
		FileListId id = new FileListId();
		id.setFileId(Long.parseLong(fileId));
		id.setSeqNo(Integer.parseInt(seqNo));
		
		Optional<FileList> optFile = fileListRepository.findById(id);
		if(optFile.isPresent()) {
			String rootPath = filePathService.getFilePathByFileId(fileId);
			
			// 파일 삭제
			File fileInfo = new File(
					rootPath + File.separator + fileId + File.separator + optFile.get().getSaveFileName());
			
			if(fileInfo.exists()) {
				fileInfo.delete();
			}
			
			// 데이터 삭제
			fileListRepository.delete(optFile.get());
		}
	}
	
	/**
	 * 파일 다운로드를 위한 파일 정보를 조회한다.
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileInfo(Map<String, Object> paramMap) throws Exception {
		
		String fileId = (String) paramMap.get("fileId");
		String seqNo = (String) paramMap.get("seqNo");
		
		FileListId id = new FileListId();
		id.setFileId(Long.parseLong(fileId));
		id.setSeqNo(Integer.parseInt(seqNo));
		
		Optional<FileList> optFile = fileListRepository.findById(id);
		if(optFile.isPresent()) {
			String rootPath = filePathService.getFilePathByFileId(fileId);
			
			File fileInfo = new File(
					rootPath + File.separator + fileId + File.separator + optFile.get().getSaveFileName());
			
			if(fileInfo.exists()) {
				return FileUtils.readFileToByteArray(fileInfo);
			}
		}
		
		return null;
	}
	
	/**
	 * fileId에 해당하는 파일 전체 다운로드
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileInfoAll(Map<String, Object> paramMap) throws Exception {
		
		String fileId = (String) paramMap.get("fileId");
		String rootPath = filePathService.getFilePathByFileId(fileId);
		
		UUID randUUID = UUID.randomUUID();
		
		File zipFile = new File(rootPath + File.separator + fileId + "_" +randUUID + ".zip");
		
		FileOutputStream fout = new FileOutputStream(rootPath + File.separator + fileId + "_" +randUUID + ".zip");
		ZipOutputStream zout = new ZipOutputStream(fout);
		
		List<FileList> fileList = fileListRepository.findByIdFileId(Long.parseLong(fileId));
		
		byte[] buffer = new byte[4096];
		
		for (FileList file : fileList) {
			File fileInfo = new File(
					rootPath + File.separator + fileId + File.separator + file.getSaveFileName());
			
			if(fileInfo.exists()) {
				ZipEntry zipEntry = new ZipEntry(file.getFileName());
				zout.putNextEntry(zipEntry);

				FileInputStream fin = new FileInputStream(fileInfo);
				int length;

				while ((length = fin.read(buffer)) > 0) {
					zout.write(buffer, 0, length);
				}

				zout.closeEntry();
				fin.close();
			}
		}
		
		zout.close();
		
		return FileUtils.readFileToByteArray(zipFile);
	}
}
