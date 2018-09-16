package com.xyt.timebooking.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;

import com.xyt.timebooking.bean.Task;

///TOTO: need to check how we can update work log for each items in spreadsheet??

public class TimeSheetLoadingService {
	private String timeSheetName = "TimeSheet.xls";
	
	private static final String SUMMARY_ROW_NAME = "ºÏ¼Æ";
	
	public List<Task> getDailyWorkDetail(String dateString) {
		System.out.println(System.getProperty("user.dir"));
		
		File spreadSheet = new File(System.getProperty("user.dir") + File.separator + timeSheetName);
		
		try {
			HSSFWorkbook workBook = openSpreadSheet(spreadSheet);
			String sheetName = determineSheetName(dateString);
			String calScope = determineCalScope(dateString);
			HSSFSheet sheet = getSheet(workBook, sheetName);
			return extractWorkDetail(sheet, calScope);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private String determineSheetName(String dateString) {
		return StringUtils.trimToEmpty(dateString).length() > 6 ? StringUtils.trimToEmpty(dateString).substring(0, 6) : "INVALID";
	}
	
	private String determineCalScope(String dateString) {
		return StringUtils.trimToEmpty(dateString).length() == 8 ? StringUtils.trimToEmpty(dateString).substring(6, 8) : "INVALID";
	}
	
	
	private HSSFWorkbook openSpreadSheet(File file) throws FileNotFoundException, IOException {
		if(!file.exists()) {
			System.out.println("file not exist");
			return null;
		} else {
			POIFSFileSystem poiFileSystem = new POIFSFileSystem(new FileInputStream(file));
			return new HSSFWorkbook(poiFileSystem);
		}
	}
	
	
	private List<Task> extractWorkDetail(HSSFSheet sheet, String calScope) throws FileNotFoundException, IOException {
			ArrayList<Task> taskList  = new ArrayList<Task> ();
			HSSFRow row = sheet.getRow(2);
			int columnNum = locateColumnNumForToday(row, calScope);
			int lastRowNum = sheet.getLastRowNum();
			
			for (int i = 3; i < lastRowNum; i++) {
				HSSFRow currentRow = sheet.getRow(i);
				
				if(currentRow.getCell(1) == null) {
					continue;
				}
				Task task = new Task();
				
				HSSFCell projectNameCell = currentRow.getCell(1);
				if(projectNameCell != null) {
					projectNameCell.setCellType(CellType.STRING);
					String projectName = StringUtils.trimToEmpty(projectNameCell.getStringCellValue());
					
					if(StringUtils.isBlank(projectName)) {
						continue;
					} 
					
					if(SUMMARY_ROW_NAME.equals(StringUtils.trimToEmpty(projectNameCell.getStringCellValue()))) {
						break;
					}
					
					task.setProjectName(projectName);
				}
				
				HSSFCell taskNameCell = currentRow.getCell(2);
				if(taskNameCell != null) {
					taskNameCell.setCellType(CellType.STRING);
					String taskName = StringUtils.trimToEmpty(taskNameCell.getStringCellValue());
					
					if(StringUtils.isBlank(taskName)) {
						task.setTaskName(task.getProjectName());
					} else {
						task.setTaskName(taskName);
					}
				}
				
				
				HSSFCell workingHoursCell = currentRow.getCell(columnNum);
				if(workingHoursCell != null) {
					workingHoursCell.setCellType(CellType.NUMERIC);
					double workingHours = workingHoursCell.getNumericCellValue();
					if(workingHours > 0) {
						task.setWorkingHours(new BigDecimal(workingHours).floatValue());
						
						
					HSSFComment comments =	workingHoursCell.getCellComment();
					if(comments != null) {
						HSSFRichTextString commentValue = comments.getString();
						if(commentValue != null && StringUtils.isNotBlank(commentValue.getString())) {
							System.out.println(commentValue.getString());
							task.setWorkLog(commentValue.getString());
						}
					}
						
						
					} else {
						continue;
					}
					
				}
				
				taskList.add(task);
			}
			//print out result
			
			for (Task task : taskList) {
				System.out.println(task.toString());
			}
		
			return taskList;
	}


	private HSSFSheet getSheet(HSSFWorkbook workBook, String sheetName) {
		HSSFSheet sheet = workBook.getSheet(sheetName);
		return sheet;
	}
	
	private int locateColumnNumForToday(HSSFRow row, String calScope) {
		for (int i = 1; i <= row.getLastCellNum(); i++) {
			HSSFCell cell = row.getCell(i);
			
			if(cell != null) {
				cell.setCellType(CellType.STRING);
				String cellValue = cell.getStringCellValue();
				if(calScope.equals(cellValue) || calScope.equals("0" + cellValue)) {
					int columnIndex = cell.getColumnIndex();
					return columnIndex;
				}
			}
		}
		return 0;
	}
	
	public static void main(String[] args) {
		TimeSheetLoadingService service = new TimeSheetLoadingService();
		service.getDailyWorkDetail("20180801");
	}
	
	public void loadWorkingHours() {
		
	}

}
