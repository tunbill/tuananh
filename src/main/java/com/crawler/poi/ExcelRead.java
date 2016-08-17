package com.crawler.poi;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelRead {
    private int passRowCount = 0;
    private int  rowCount = 0;

    Sheet guru99Sheet;
    Workbook guru99Workbook = null;

    public void readExcel(String filePath, String fileName, String sheetName) throws IOException, InterruptedException {

        // Create a object of File class to open xlsx file
        System.out.println(filePath + fileName);

        File file = new File(filePath + fileName);

        // Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);

        // Find the file extension by spliting file name in substring and
        // getting only extension name

        String fileExtensionName = fileName.substring(fileName.indexOf("."));

        if (fileExtensionName.equals(".xlsx")) {
            guru99Workbook = new XSSFWorkbook(inputStream);
        }
        else if (fileExtensionName.equals(".xls")) {
            guru99Workbook = new HSSFWorkbook(inputStream);
        }

        // Read sheet inside the workbook by its name

        guru99Sheet = guru99Workbook.getSheet(sheetName);
        System.out.println("getFirstRowNum: " + guru99Sheet.getFirstRowNum());

        rowCount = (guru99Sheet.getLastRowNum()) - (guru99Sheet.getFirstRowNum());
        System.out.println("rowcount: " + rowCount);

        setRowCount(rowCount);
    }
    public  void setRowCount(int rc){
        passRowCount =rc;
    }
    public  int getRowCount(){
        return passRowCount;
    }

    public void setGuru99Workbook(Workbook guru99Workbook) {
        this.guru99Workbook = guru99Workbook;
    }

    public void setGuru99Sheet(Sheet guru99Sheet) {
        this.guru99Sheet = guru99Sheet;
    }

    public Sheet getGuru99Sheet() {
        return guru99Sheet;
    }

    public Workbook getGuru99Workbook() {
        return guru99Workbook;
    }
}
