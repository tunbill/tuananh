package com.crawler.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crawler.model.ReviewData;
import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WriteToFile {
    public static void writeToExcel(String sheetName, List<ReviewData> datas) throws Exception{
        ExcelRead objExcelFile = new ExcelRead();
        String folderPath = System.getProperty("user.dir") + "/data/";

        System.out.println(folderPath);

        objExcelFile.readExcel(folderPath, "CrawlerData.xlsx", sheetName);
        AppendDataInExcel appendData = new AppendDataInExcel();
        appendData.appendWrite(objExcelFile.getGuru99Workbook(), objExcelFile.getGuru99Sheet(), 7, datas);
    }

}
