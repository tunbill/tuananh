package com.crawler.poi;

import java.io.IOException;

public class MainFile {
    public static void main(String[] args) throws IOException, InterruptedException {

        ExcelRead objExcelFile = new ExcelRead();

        // Prepare the path of excel file

        String filePath = System.getProperty("user.dir") + "/data/";

        System.out.println(filePath);

        // Call read file method of the class to read data

        objExcelFile.readExcel(filePath, "Data.xlsx", "Careerbliss");
        AppendDataInExcel appendData = new AppendDataInExcel();
        //appendData.appendWrite(objExcelFile.getGuru99Workbook(), objExcelFile.getGuru99Sheet(), objExcelFile.getRowCount());
    }
}
