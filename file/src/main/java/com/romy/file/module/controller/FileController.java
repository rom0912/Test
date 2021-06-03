package com.romy.file.module.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.romy.file.common.logger.Log;
import com.romy.file.module.service.FileListService;
import com.romy.file.module.service.FilePathService;

@CrossOrigin
@Controller
public class FileController {

	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private FileListService fileListService;
	
	
	/**
	 * 파일리스트 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			List<Map<String, Object>> list = fileListService.getFileListByFileId(paramMap);
			mv.addObject("message", "OK");
			mv.addObject("fileList", list);
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	
	/**
	 * 파일 업로드
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload")
	public ModelAndView upload(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
	
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			List listFile = filePathService.uploadFile(paramMap);
			mv.addObject("message", "OK");
			mv.addObject("fileList", listFile);
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 파일 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			fileListService.deleteFile(paramMap);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 파일 다운로드
	 * @param req
	 * @param res
	 * @param paramMap
	 * @throws Exception
	 */
	@RequestMapping("/download")
	public @ResponseBody byte[] download(HttpServletRequest req, HttpServletResponse res,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		
		Log.DebugStart();
		
		try {
			byte[] file = fileListService.getFileInfo(paramMap);
			return file;
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * 파일 전체 다운로드 (zip 파일)
	 * @param req
	 * @param res
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/downloadAll")
	public @ResponseBody byte[] downloadAll(HttpServletRequest req, HttpServletResponse res,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		
		Log.DebugStart();
		
		try {
			byte[] file = fileListService.getFileInfoAll(paramMap);
			return file;
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
		
		return null;
	}
}
