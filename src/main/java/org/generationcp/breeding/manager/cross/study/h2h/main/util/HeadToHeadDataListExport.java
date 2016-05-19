
package org.generationcp.breeding.manager.cross.study.h2h.main.util;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.breeding.manager.cross.study.h2h.main.ResultsComponent;
import org.generationcp.breeding.manager.cross.study.h2h.main.pojos.ResultsData;
import org.generationcp.breeding.manager.cross.study.h2h.main.pojos.TraitForComparison;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class HeadToHeadDataListExport {

	private static final String LABEL_STYLE = "labelStyle";
	private static final String HEADING_STYLE = "headingStyle";
	private static final String HEADING_MERGED_STYLE = "headingMergedStyle";
	private static final String NUMERIC_STYLE = "numericStyle";
	private static final String NUMERIC_DOUBLE_STYLE = "numericDoubleStyle";

	public HeadToHeadDataListExport() {
		// empty constructor
	}

	private HashMap<String, CellStyle> createStyles(HSSFWorkbook wb) {
		HashMap<String, CellStyle> styles = new HashMap<String, CellStyle>();

		// set cell style for labels in the description sheet
		CellStyle labelStyle = wb.createCellStyle();
		labelStyle.setFillForegroundColor(IndexedColors.BROWN.getIndex());
		labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font labelFont = wb.createFont();
		labelFont.setColor(IndexedColors.WHITE.getIndex());
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		labelStyle.setFont(labelFont);
		styles.put(HeadToHeadDataListExport.LABEL_STYLE, labelStyle);

		// set cell style for headings in the description sheet
		CellStyle headingStyle = wb.createCellStyle();
		headingStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
		headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font headingFont = wb.createFont();
		headingFont.setColor(IndexedColors.WHITE.getIndex());
		headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(headingFont);
		styles.put(HeadToHeadDataListExport.HEADING_STYLE, headingStyle);

		CellStyle headingMergedStyle = wb.createCellStyle();
		headingMergedStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
		headingMergedStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headingMergedStyle.setAlignment(CellStyle.ALIGN_CENTER);
		Font headingMergedFont = wb.createFont();
		headingMergedFont.setColor(IndexedColors.WHITE.getIndex());
		headingMergedFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingMergedStyle.setFont(headingMergedFont);
		styles.put(HeadToHeadDataListExport.HEADING_MERGED_STYLE, headingMergedStyle);

		// set cell style for numeric values (left alignment)
		CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		numericStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		styles.put(HeadToHeadDataListExport.NUMERIC_STYLE, numericStyle);

		CellStyle numericDoubleStyle = wb.createCellStyle();
		numericDoubleStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		numericDoubleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
		styles.put(HeadToHeadDataListExport.NUMERIC_DOUBLE_STYLE, numericDoubleStyle);

		return styles;
	}

	public FileOutputStream exportHeadToHeadDataListExcel(String filename, List<ResultsData> resultDataList,
			Set<TraitForComparison> traitsIterator, String[] columnIdData, Map<String, String> columnIdDataMsgMap)
			throws HeadToHeadDataListExportException {

		HSSFWorkbook wb = new HSSFWorkbook();
		HashMap<String, CellStyle> sheetStyles = this.createStyles(wb);
		HSSFSheet sheet = wb.createSheet("Data List");

		int cellIndex = 0;
		int startDataRowIndex = 0;

		HSSFRow headerColSpan = sheet.createRow(startDataRowIndex++);
		HSSFRow header = sheet.createRow(startDataRowIndex++);

		Cell cell = header.createCell(cellIndex++);
		cell.setCellValue("Test Entry");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
		cell = header.createCell(cellIndex++);
		cell.setCellValue("Standard Entry");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));

		for (TraitForComparison traitForCompare : traitsIterator) {
			if (traitForCompare.isDisplay()) {
				int startCol = cellIndex;
				int endCol = cellIndex;
				Cell cellHeader = headerColSpan.createCell(cellIndex);
				cellHeader.setCellValue(traitForCompare.getTraitInfo().getName());
				cellHeader.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_MERGED_STYLE));

				for (int k = 0; k < columnIdData.length; k++) {
					String colId = columnIdData[k];
					String msg = columnIdDataMsgMap.get(colId);
					cell = header.createCell(cellIndex++);
					cell.setCellValue(msg);
					cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
				}

				endCol = cellIndex;
				sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
						0, // last row (0-based)
						startCol, // first column (0-based)
						endCol - 1 // last column (0-based)
				));
			}
		}

		for (ResultsData resData : resultDataList) {
			// we iterate and permutate against the list
			cellIndex = 0;
			HSSFRow rowData = sheet.createRow(startDataRowIndex++);
			String testEntryName = resData.getGid1Name();
			String standardEntryName = resData.getGid2Name();

			rowData.createCell(cellIndex++).setCellValue(testEntryName);
			rowData.createCell(cellIndex++).setCellValue(standardEntryName);

			for (TraitForComparison traitForCompare : traitsIterator) {
				if (traitForCompare.isDisplay()) {
					for (String colId : columnIdData) {
						String traitColId = traitForCompare.getTraitInfo().getName() + colId;
						String numVal = resData.getTraitDataMap().get(traitColId);
						if (numVal == null) {
							numVal = "0";
						}
						numVal = numVal.replaceAll(",", "");

						cell = rowData.createCell(cellIndex++);

						if ("-".equalsIgnoreCase(numVal)) {
							cell.setCellValue(numVal);
						} else {

							cell.setCellValue(Double.parseDouble(numVal));
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							if (colId.equalsIgnoreCase(ResultsComponent.NUM_OF_ENV_COLUMN_ID)
									|| colId.equalsIgnoreCase(ResultsComponent.NUM_SUP_COLUMN_ID)) {
								cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.NUMERIC_STYLE));
							} else {
								cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.NUMERIC_DOUBLE_STYLE));
							}
						}
					}
				}
			}
		}

		for (int ctr = 0; ctr < cellIndex; ctr++) {
			sheet.autoSizeColumn(ctr);
		}

		try {
			// write the excel file
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (Exception ex) {
			throw new HeadToHeadDataListExportException("Error with writing to: " + filename, ex);
		}
	}
}