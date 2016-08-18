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

    public Workbook readExcel(String filePath, String fileName) throws IOException, InterruptedException {
        Workbook guru99Workbook = null;

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

        return guru99Workbook;

    }
}
