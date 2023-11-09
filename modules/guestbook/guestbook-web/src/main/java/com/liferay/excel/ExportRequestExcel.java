package com.liferay.excel;

import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;


import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExportRequestExcel {
	
	public static void exportExcel(ResourceRequest resourceRequest, ResourceResponse resourceResponse,List<GuestbookEntry> DeleteList, String excelFileName) {
					
		Workbook workbook = null;
		try {
			workbook = generateDeleteRequestexcel(DeleteList);
		} catch (Exception e) {
			((com.liferay.portal.kernel.log.Log) log).error("error in creating workbook: " + e);
		}
		if (Validator.isNotNull(workbook)) {
			
			ByteArrayOutputStream bos = null;
			
			try {
				bos = new ByteArrayOutputStream();
				
				workbook.write(bos);
				byte[] bytes = bos.toByteArray();
				HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);
				HttpServletResponse response = PortalUtil.getHttpServletResponse(resourceResponse);
				String contentType = MimeTypesUtil.getContentType(excelFileName);
				ServletResponseUtil.sendFile(request, response, excelFileName, bytes, contentType);
				
			} catch (Exception e) {
				
				((com.liferay.portal.kernel.log.Log) log).error("error in downloading excel: " + e);
				
			} finally {
				
				try {
					
					if (bos != null) {
						
						bos.close();
					}
				} catch (Exception e) {
					
					((com.liferay.portal.kernel.log.Log) log).error("error in closing output stream: " + e);
				}
			}
		 }
	}
	
	public static Workbook generateDeleteRequestexcel(List<GuestbookEntry> DeleteList) {
		
		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet("KidDeletRequest");
		
		CellStyle headerCellStyle = workbook.createCellStyle();
		Font headFont= (Font) workbook.createFont();
		headerCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headFont);
		headerCellStyle.setWrapText(true);
		
		Row headRow = sheet.createRow(0);
		int headColumnNumber = 0;
		
		createCell(headRow, headColumnNumber++, "Name", headerCellStyle);
		createCell(headRow, headColumnNumber++, "Email Id", headerCellStyle);
		createCell(headRow, headColumnNumber++, "Status", headerCellStyle);
		
		if(Validator.isNotNull(DeleteList)) {
				int i = 1;
				
				for(GuestbookEntry deletelist: DeleteList) {
					try {
						CellStyle defaultCellStyle = workbook.createCellStyle();
						defaultCellStyle.setWrapText(true);
						Row row = sheet.createRow(i);
						int columnNumber = 0;
						
						createCell(row, columnNumber++, deletelist.getName(), defaultCellStyle);
						createCell(row, columnNumber++, deletelist.getEmail(), defaultCellStyle);
						createCell(row, columnNumber++, String.valueOf(deletelist.getCreateDate()), defaultCellStyle);
						
						i++;
						}catch (Exception e) {
						((com.liferay.portal.kernel.log.Log) log).error("Error during creating cells for Delete MIS : " + e);
						}
				}
				for (int columnNumber = 0; columnNumber < headColumnNumber - 1; columnNumber++) {
					if (columnNumber == 0) {
						sheet.setColumnWidth(columnNumber, 8000);
					} else if (columnNumber == 1) {
						sheet.setColumnWidth(columnNumber, 15000);
					} else {
						sheet.setColumnWidth(columnNumber, 6000);
					}
				}
				CellRangeAddress range = new CellRangeAddress(0, 0, 0, headColumnNumber - 1);
				sheet.setAutoFilter(range);
		}
		return workbook;
	}

	private static void createCell(Row row, int cellIndex, String cellValue, CellStyle cellStyle) {
		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}
	
	private static final com.liferay.portal.kernel.log.Log log = LogFactoryUtil.getLog(ExportRequestExcel.class);	

}
