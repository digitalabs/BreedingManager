package org.generationcp.browser.study.util;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.browser.study.TableViewerDatasetTable;
import org.generationcp.commons.util.PoiUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.StudyDataManagerImpl;


public class TableViewerExporter {

    private TableViewerDatasetTable table;
    private TableViewerCellSelectorUtil tableViewerCellSelectorUtil;
    
    public TableViewerExporter(TableViewerDatasetTable table, TableViewerCellSelectorUtil tableViewerCellSelectorUtil) {
    	this.table = table;
    	this.tableViewerCellSelectorUtil = tableViewerCellSelectorUtil;
    }
   
    public FileOutputStream exportToExcel(String filename) throws DatasetExporterException {
        
        //create workbook
        Workbook workbook = new XSSFWorkbook();
        CellStyle cellStyleForObservationSheet = workbook.createCellStyle();
        
        //Create first sheet
        Sheet sheet1 = workbook.createSheet("Sheet 1");
        
        //Prepare data
        ArrayList columnId = new ArrayList<String>();
        ArrayList columnHeaders = new ArrayList<String>();
        Object[] columnHeadersObjectArray = table.getVisibleColumns();
        Object[] columnHeadersStringArray = table.getColumnHeaders();
		for(int x=0;x<columnHeadersObjectArray.length;x++){
			columnId.add(columnHeadersObjectArray[x].toString());
			columnHeaders.add(columnHeadersStringArray[x].toString());
		}
        Object tableItemIds[] = table.getItemIds().toArray();
        
		//Create headers row, and populate with data
        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
        headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(171, 171, 171)));
        Font labelFont = workbook.createFont();
        labelFont.setColor(IndexedColors.BLACK.getIndex());
        labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerStyle.setFont(labelFont);
        
		Row headerRow = sheet1.createRow(0);
		for(int x=0;x<columnHeaders.size();x++){
    	    headerRow.createCell(x).setCellValue(columnHeaders.get(x).toString());
    	    headerRow.getCell(x).setCellStyle(headerStyle);
		}
		
		
		//Traverse through table, and create rows/columns and populate with data
		XSSFColor cellColor = null;
		XSSFCellStyle cellStyle = null;
		
		Row rows[] = new Row[tableItemIds.length];
        for(int y=0;y<tableItemIds.length;y++){
        	rows[y] = sheet1.createRow(y+1);
        	//System.out.println("Item ID: "+tableItemIds[y].toString());
        	for(int x=0;x<columnHeaders.size();x++){
        		rows[y].createCell(x).setCellValue(table.getItem(tableItemIds[y]).getItemProperty(columnId.get(x)).toString());
        		cellColor = tableViewerCellSelectorUtil.getColor(tableItemIds[y].toString(), columnId.get(x).toString());
        		if(cellColor!=null){
        			cellStyle = (XSSFCellStyle) rows[y].getCell(x).getCellStyle();
        			cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        			cellStyle.setFillForegroundColor(cellColor);
        			rows[y].getCell(x).setCellStyle(cellStyle);
        		}
        		//System.out.println("Color: "+tableViewerCellSelectorUtil.getColor(tableItemIds[y].toString(), columnId.get(x).toString()));
        		//System.out.println("ColumnHeader: "+columnHeaders.get(x));
    			//System.out.println(columnId.get(x)+": "+table.getItem(tableItemIds[y]).getItemProperty(columnId.get(x)));
    		}	
        	//System.out.println("");
        }
        
        
        try {
            //write the excel file
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            return fileOutputStream;
        } catch(Exception ex) {
            throw new DatasetExporterException("Error with writing to: " + filename, ex);
        }
    }
    
   
}
