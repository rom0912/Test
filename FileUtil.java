import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component
public class FileUtil {

	public static final int BUFFER_SIZE = 4096;
    
	/**
	 * 엑셀파일 가져오기
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getExcelFile(MultipartHttpServletRequest request) throws Exception {
		
		Map<String, MultipartFile> files = request.getFileMap();
		Iterator<Entry<String, MultipartFile>> fileIter = files.entrySet().iterator();
		
		List<Map<String, Object>> listTempFile = new ArrayList<Map<String, Object>>();
		
		InputStream is = null;
		
		try {
			Entry<String, MultipartFile> entry = null;
			MultipartFile multiFile = null;
			
			while (fileIter.hasNext()) {
				entry = fileIter.next();
				multiFile = request.getFile(entry.getKey());
				
				if(multiFile.getSize() > 0) {
					String strOrgFileNm = multiFile.getOriginalFilename();
					is = multiFile.getInputStream();
					
					Map<String, Object> mapFileInfo = new HashMap<String, Object>();
					mapFileInfo.put("strOrgFileNm", strOrgFileNm);
					
					// 파일 확장자에 따른 분기
					if( strOrgFileNm.toLowerCase().endsWith("xls") ) {
						mapFileInfo.put("ext", "xls");
						mapFileInfo.put("objWb", new HSSFWorkbook(is));
					} else if( strOrgFileNm.toLowerCase().endsWith("xlsx") ) {
						mapFileInfo.put("ext", "xlsx");
						mapFileInfo.put("objWb", new XSSFWorkbook(is));
					} else {
						throw new Exception("엑셀 파일이 아닙니다.");
					}
					
					listTempFile.add(mapFileInfo);
				}
			}
			
		} catch (Exception e) {
			throw new Exception("엑셀 파일을 읽는 도중 오류가 발생하였습니다.");
		} finally {
			if(is != null) {
				is.close();
			}
		}
		
		return listTempFile;
	}
	
	/**
	 * 오류 리스트 엑셀 파일 생성
	 * @param request		(필수)	HttpServletRequest
	 * @param response		(필수)	HttpServletResponse
	 * @param listHeader	(필수)	엑셀 파일의 헤더 정보 (n개의 Sheet일 경우를 대비해 List로 받음)
	 * @param mapData		(필수)	오류 데이터 정보 (원본 WorkBook의 Sheet명이 Key, ex. mapData.put("변인", listError) )
	 * @param orgWb			(필수)	원본 WorkBook
	 * @param fileNm		(필수)	다운받을 파일명 (확장자는 xlsx로 하드코딩 이기 때문에 확장자 제외)
	 * @return 오류파일 생성 정보 {
	 *           				"fileNm" 	: "파일명"
	 *         				  , "fileOrgNm" : "저장된 파일명"
	 *         				  , "filePath" 	: "파일 저장경로"
	 *         				  }
	 * @throws Exception
	 */
	public static Map<String, String> createExcelFileByUpload(HttpServletRequest request, HttpServletResponse response,
			List<LinkedHashMap<String, String>> listHeader, Map<String, Object> mapData, Workbook orgWb, String fileNm)
			throws Exception {
		
		if(orgWb.getNumberOfSheets() != listHeader.size()) return null;
		
		SXSSFWorkbook wb = new SXSSFWorkbook();
		List<SXSSFSheet> listSheet = new ArrayList<SXSSFSheet>();
		// original Sheet 별로 오류내역 Sheet 생성
		for (Integer i=0, size=orgWb.getNumberOfSheets(); i < size; i++) {
			listSheet.add(wb.createSheet(orgWb.getSheetAt(i).getSheetName()));
		}
		
		CellStyle styleHd = wb.createCellStyle();    //제목 스타일
	    CellStyle styleBody = wb.createCellStyle();   //내용 스타일

	    Font font = wb.createFont();
	    font.setFontHeightInPoints((short) 11);
	    font.setFontName("맑은 고딕");
	    font.setBold(true);
	    
	    styleHd.setFont(font);
	    styleHd.setAlignment(CellStyle.ALIGN_CENTER);
	    styleHd.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    styleHd.setBorderBottom(CellStyle.BORDER_THIN);
	    styleHd.setBorderTop(CellStyle.BORDER_THIN);
	    styleHd.setBorderLeft(CellStyle.BORDER_THIN); 
	    styleHd.setBorderRight(CellStyle.BORDER_THIN);
	    styleHd.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
	    styleHd.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    styleHd.setBottomBorderColor(HSSFColor.BLACK.index);
	    styleHd.setLeftBorderColor(HSSFColor.BLACK.index);
	    styleHd.setRightBorderColor(HSSFColor.BLACK.index);
	    styleHd.setTopBorderColor(HSSFColor.BLACK.index);
	    
	    styleBody.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    styleBody.setBorderBottom(CellStyle.BORDER_THIN);
	    styleBody.setBorderTop(CellStyle.BORDER_THIN);
	    styleBody.setBorderLeft(CellStyle.BORDER_THIN);
	    styleBody.setBorderRight(CellStyle.BORDER_THIN);
	    styleBody.setBottomBorderColor(HSSFColor.BLACK.index);
	    styleBody.setLeftBorderColor(HSSFColor.BLACK.index);
	    styleBody.setRightBorderColor(HSSFColor.BLACK.index);
	    styleBody.setTopBorderColor(HSSFColor.BLACK.index);
	    
	    CellStyle num = wb.createCellStyle();
	    num.setAlignment(CellStyle.ALIGN_CENTER);
	    num.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    num.setBorderBottom(CellStyle.BORDER_THIN);
	    num.setBorderTop(CellStyle.BORDER_THIN);
	    num.setBorderLeft(CellStyle.BORDER_THIN);
	    num.setBorderRight(CellStyle.BORDER_THIN);
	    num.setBottomBorderColor(HSSFColor.BLACK.index);
	    num.setLeftBorderColor(HSSFColor.BLACK.index);
	    num.setRightBorderColor(HSSFColor.BLACK.index);
	    num.setTopBorderColor(HSSFColor.BLACK.index);
		
	    // header 만들기
	    for (Integer i=0, size=listHeader.size(); i < size; i++) {
	    	SXSSFRow row = listSheet.get(i).createRow(0);
	    	LinkedHashMap<String, String> mapHeader = listHeader.get(i);
	    	if(mapHeader == null) continue;
	    	
	    	List<String> keys = mapHeader.keySet().stream().collect(Collectors.toList());
	    	
	    	for (Integer j=0, cnt=keys.size(); j < cnt; j++) {
	    		SXSSFCell cell = row.createCell(j);
	    		String strHeader = mapHeader.get(keys.get(j));
	    		cell.setCellValue(strHeader);
	    		cell.setCellStyle(styleHd);
	    		listSheet.get(i).setColumnWidth(cell.getColumnIndex(), strHeader.getBytes().length * 500);
	    	}
	    }
		
	    // 데이터 만들기
	    for (Integer i=0, size=orgWb.getNumberOfSheets(); i < size; i++) {
	    	String sheetNm = orgWb.getSheetAt(i).getSheetName();
	    	
	    	LinkedHashMap<String, String> mapHeader = listHeader.get(i);
	    	if(mapHeader == null) continue;
	    	
	    	// original Sheet 이름으로 데이터 리스트를 받아옴.
	    	List<Map<String, Object>> listData = (List<Map<String,Object>>) mapData.get(sheetNm);
	    	
	    	for (Integer j=0, rowCnt=listData.size(); j < rowCnt; j++) {
	    		// row 생성
	    		SXSSFRow row = listSheet.get(i).createRow(j + 1);
	    		
	    		Map<String, Object> data = listData.get(j);
	    		List<String> keys = mapHeader.keySet().stream().collect(Collectors.toList());
		    	
		    	for (Integer k=0, cnt=keys.size(); k < cnt; k++) {
		    		SXSSFCell cell = row.createCell(k);
		    		
		    		String strVal = "";
		    		
		    		if(data.get(keys.get(k)) == null) {
		    			cell.setCellValue(strVal);
		    		} else {
		    			if("java.lang.Double".equals(data.get(keys.get(k)).getClass().getName())) {
			    			Double dVal = (Double) data.get(keys.get(k));
			    			cell.setCellValue(dVal);
			    		} else {
			    			strVal = (String) data.get(keys.get(k));
			    			cell.setCellValue(strVal);
			    		}
		    			
		    			if("No".equals(keys.get(k))) {
		    				cell.setCellStyle(num);
		    			} else {
		    				cell.setCellStyle(styleBody);
		    			}
		    		}
		    		
	    			int intBWidth = strVal.getBytes().length * 500;
		    		int intHWidth = listSheet.get(i).getColumnWidth(cell.getColumnIndex());
		    		if(intHWidth < intBWidth) {
		    			if(intBWidth > 5000) {
		    				listSheet.get(i).setColumnWidth(cell.getColumnIndex(), 5000);
		    			}else {
		    				listSheet.get(i).setColumnWidth(cell.getColumnIndex(), intBWidth);
		    			}
		    		}
		    	}
	    	}
	    } // 데이터 만들기 for문 종료
		
	    // 임시 경로
		String strExportDir = "/xls_export/";
		File dir = new File(strExportDir);
		// 임시 경로이기 때문에 파일을 남기지 않음.
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File[] fileList = dir.listFiles();
		for (int j=0, len=fileList.length; j < len; j++) {
			fileList[j].deleteOnExit();
		}
		
		String strExcelRealFileNm = fileNm+".xlsx";
		
		long time = new Date().getTime();
		String strExcelChgFileNm = fileNm+time+ ".xlsx";
		String path = strExportDir + strExcelChgFileNm;

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(new File(filePathBlackList(path)));
			wb.write(fout);
		} finally {
			wb = null;
			if(fout != null){
				fout.close();
			}
		}
		
		Map<String, String> fileInfo = new HashMap<String, String>();
		
		File file = new File(path);
		if(file.exists()){
			fileInfo.put("fileNm", file.getName());
			fileInfo.put("fileOrgNm", strExcelRealFileNm);
			fileInfo.put("filePath", strExportDir);
		}
	    
		return fileInfo;
	}
	
	/**
	 * filePath secure coding
	 * @param value
	 * @return
	 */
	public static String filePathBlackList(String value) {
		String returnValue = value;
		if (returnValue == null || returnValue.trim().equals("")) {
			return "";
		}

		returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
		returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\

		return returnValue;
	}
	
	/**
	 * 다운로드시 브라우저별 맞는 파일이름을 리턴
	 * @param browser
	 * @param originalFilename
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getDispositionFileName(String browser, String originalFilename) throws UnsupportedEncodingException {
		String dispositionPrefix = "attachment; filename=";
		String encodedFilename = null;
		
		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(originalFilename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Trident")) { // IE11 문자열 깨짐 방지
			encodedFilename = URLEncoder.encode(originalFilename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(originalFilename.getBytes("UTF-8"), "8859_1") + "\"";
			encodedFilename = URLDecoder.decode(encodedFilename);
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(originalFilename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < originalFilename.length(); i++) {
				char c = originalFilename.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		} else if (browser.equals("Safari")) {
			encodedFilename = "\"" + new String(originalFilename.getBytes("UTF-8"), "8859_1") + "\"";
			encodedFilename = URLDecoder.decode(encodedFilename);
		}
		else {
			encodedFilename = "\"" + new String(originalFilename.getBytes("UTF-8"), "8859_1") + "\"";
		}

		return dispositionPrefix + encodedFilename;
	}
	
	/**
	 * 브라우저 정보 가져오기
	 * @param request
	 * @return
	 */
	public static String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header.indexOf("MSIE") > -1) {
			return "MSIE";
		} else if (header.indexOf("Trident") > -1) {   // IE11 문자열 깨짐 방지
			return "Trident";
		} else if (header.indexOf("Chrome") > -1) {
			return "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			return "Opera";
		} else if (header.indexOf("Safari") > -1) {
			return "Safari";
	   }
	   return "Firefox";
	}
	
	/**
	 * 엑셀파일 생성 및 다운 (공통)
	 * @param request
	 * @param response
	 * @param mapHeader
	 * @param listData
	 * @param fileNm
	 * @return
	 * @throws Exception
	 */
	public static void createExcelFileDown(HttpServletRequest request, HttpServletResponse response,
			LinkedHashMap<String, String> mapHeader, List<Map<String, Object>> listData, String sheetNm, String fileNm,
			String fileExt) throws Exception {
		
		SXSSFWorkbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = wb.createSheet(sheetNm);
		
		CellStyle styleHd = wb.createCellStyle();    //제목 스타일
	    CellStyle styleBody = wb.createCellStyle();   //내용 스타일

	    Font font = wb.createFont();
	    font.setFontHeightInPoints((short) 11);
	    font.setFontName("맑은 고딕");
	    font.setBold(true);
	    
	    styleHd.setAlignment(CellStyle.ALIGN_CENTER);
	    styleHd.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    styleHd.setBorderBottom(CellStyle.BORDER_THIN);
	    styleHd.setBorderTop(CellStyle.BORDER_THIN);
	    styleHd.setBorderLeft(CellStyle.BORDER_THIN); 
	    styleHd.setBorderRight(CellStyle.BORDER_THIN);
	    styleHd.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
	    styleHd.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    styleHd.setBottomBorderColor(HSSFColor.BLACK.index);
	    styleHd.setLeftBorderColor(HSSFColor.BLACK.index);
	    styleHd.setRightBorderColor(HSSFColor.BLACK.index);
	    styleHd.setTopBorderColor(HSSFColor.BLACK.index);
	    styleHd.setFont(font);
	    
	    styleBody.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    styleBody.setBorderBottom(CellStyle.BORDER_THIN);
	    styleBody.setBorderTop(CellStyle.BORDER_THIN);
	    styleBody.setBorderLeft(CellStyle.BORDER_THIN);
	    styleBody.setBorderRight(CellStyle.BORDER_THIN);
	    styleBody.setBottomBorderColor(HSSFColor.BLACK.index);
	    styleBody.setLeftBorderColor(HSSFColor.BLACK.index);
	    styleBody.setRightBorderColor(HSSFColor.BLACK.index);
	    styleBody.setTopBorderColor(HSSFColor.BLACK.index);
	    
	    CellStyle num = wb.createCellStyle();
	    num.setAlignment(CellStyle.ALIGN_CENTER);
	    num.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    num.setBorderBottom(CellStyle.BORDER_THIN);
	    num.setBorderTop(CellStyle.BORDER_THIN);
	    num.setBorderLeft(CellStyle.BORDER_THIN);
	    num.setBorderRight(CellStyle.BORDER_THIN);
	    num.setBottomBorderColor(HSSFColor.BLACK.index);
	    num.setLeftBorderColor(HSSFColor.BLACK.index);
	    num.setRightBorderColor(HSSFColor.BLACK.index);
	    num.setTopBorderColor(HSSFColor.BLACK.index);
	    
	    CellStyle number = wb.createCellStyle();
	    number.setAlignment(CellStyle.ALIGN_RIGHT);
	    number.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    number.setBorderBottom(CellStyle.BORDER_THIN);
	    number.setBorderTop(CellStyle.BORDER_THIN);
	    number.setBorderLeft(CellStyle.BORDER_THIN);
	    number.setBorderRight(CellStyle.BORDER_THIN);
	    number.setBottomBorderColor(HSSFColor.BLACK.index);
	    number.setLeftBorderColor(HSSFColor.BLACK.index);
	    number.setRightBorderColor(HSSFColor.BLACK.index);
	    number.setTopBorderColor(HSSFColor.BLACK.index);
	    DataFormat format = wb.createDataFormat();
	    number.setDataFormat(format.getFormat("#,##0"));
	    
	    CellStyle dbNum = wb.createCellStyle();
	    dbNum.setAlignment(CellStyle.ALIGN_RIGHT);
	    dbNum.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    dbNum.setBorderBottom(CellStyle.BORDER_THIN);
	    dbNum.setBorderTop(CellStyle.BORDER_THIN);
	    dbNum.setBorderLeft(CellStyle.BORDER_THIN);
	    dbNum.setBorderRight(CellStyle.BORDER_THIN);
	    dbNum.setBottomBorderColor(HSSFColor.BLACK.index);
	    dbNum.setLeftBorderColor(HSSFColor.BLACK.index);
	    dbNum.setRightBorderColor(HSSFColor.BLACK.index);
	    dbNum.setTopBorderColor(HSSFColor.BLACK.index);
	    DataFormat format2 = wb.createDataFormat();
	    number.setDataFormat(format2.getFormat("#,##0.0##"));
	    
	    CellStyle line = wb.createCellStyle();
	    line.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    line.setBorderBottom(CellStyle.BORDER_THIN);
	    line.setBorderTop(CellStyle.BORDER_THIN);
	    line.setBorderLeft(CellStyle.BORDER_THIN);
	    line.setBorderRight(CellStyle.BORDER_THIN);
	    line.setBottomBorderColor(HSSFColor.BLACK.index);
	    line.setLeftBorderColor(HSSFColor.BLACK.index);
	    line.setRightBorderColor(HSSFColor.BLACK.index);
	    line.setTopBorderColor(HSSFColor.BLACK.index);
	    line.setWrapText(true);
		
	    // header 만들기
	    List<String> keys = mapHeader.keySet().stream().collect(Collectors.toList());
	    SXSSFRow row = sheet.createRow(0);
    	for (Integer j=0, cnt=keys.size(); j < cnt; j++) {
    		SXSSFCell cell = row.createCell(j);
    		String strHeader = mapHeader.get(keys.get(j));
    		cell.setCellValue(strHeader);
    		cell.setCellStyle(styleHd);
    		sheet.setColumnWidth(cell.getColumnIndex(), strHeader.getBytes().length * 500);
    	}
		
	    // 데이터 만들기
    	for (Integer j=0, rowCnt=listData.size(); j < rowCnt; j++) {
    		// row 생성
    		row = sheet.createRow(j + 1);
    		
    		Map<String, Object> data = listData.get(j);
	    	
	    	for (Integer k=0, cnt=keys.size(); k < cnt; k++) {
	    		SXSSFCell cell = row.createCell(k);
	    		
	    		String strVal = "";
	    		if(data.get(keys.get(k)) == null) {
	    			if("idx".equals(keys.get(k))) {
	    				strVal = String.valueOf(j + 1);
	    				cell.setCellStyle(num);
	    			}
	    			
	    			cell.setCellValue(strVal);
	    			
	    		} else {
	    			// 소수
	    			if("java.math.BigDecimal".equals(data.get(keys.get(k)).getClass().getName())) {
	    				cell.setCellStyle(dbNum);
        				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        				BigDecimal dVal = (BigDecimal) data.get(keys.get(k));
		    			cell.setCellValue(dVal.doubleValue());
		    			
		    		// 정수
		    		} else if("java.lang.Integer".equals(data.get(keys.get(k)).getClass().getName())) {
		    			cell.setCellStyle(number);
        				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        				Integer intVal = (Integer) data.get(keys.get(k));
		    			cell.setCellValue(intVal.doubleValue());
		    			
		    		} else {
		    			strVal = (String) data.get(keys.get(k));
		    			cell.setCellValue(strVal);
		    			// 줄바꿈 스타일로 변경
			    		if(strVal.contains("\\n")) {
			    			cell.setCellStyle(line);
			    		} else {
			    			cell.setCellStyle(styleBody);
			    		}
		    		}
	    		}
	    		
    			int intBWidth = strVal.getBytes().length * 500;
	    		int intHWidth = sheet.getColumnWidth(cell.getColumnIndex());
	    		if(intHWidth < intBWidth) {
	    			if(intBWidth > 5000) {
	    				sheet.setColumnWidth(cell.getColumnIndex(), 5000);
	    			}else {
	    				sheet.setColumnWidth(cell.getColumnIndex(), intBWidth);
	    			}
	    		}
	    	}
    	}
	    
    	ByteArrayOutputStream bao = new ByteArrayOutputStream();
	    wb.write(bao);
	    byte[] fileByte = bao.toByteArray();
	     
	    response.setContentType("application/octet-stream");
	    response.setContentLength(fileByte.length);
	    
	    String browser = getBrowser(request);
	    String dispositionFileName = getDispositionFileName(browser, fileNm + "." +fileExt);
        
        response.setHeader("Content-Disposition", dispositionFileName);

        if ("Opera".equals(browser)){
           response.setContentType("application/octet-stream;charset=UTF-8");
        }
    
	    response.setHeader("Content-Transfer-Encoding", "binary");
	    response.getOutputStream().write(fileByte);
	     
	    response.getOutputStream().flush();
	    response.getOutputStream().close();
	}
}
