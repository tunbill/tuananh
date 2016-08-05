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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WriteToFile {
    public static void writeToExcel(String filePath, List<ReviewData> datas) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            // index from 0,0... cell A1 is cell(0,0)
            for (int i = 0; i < datas.size(); i++) {
                ReviewData data = datas.get(i);
                HSSFRow row = worksheet.createRow(i);
                for (int j = 0; j < data.getDatas().size(); j++) {
                    HSSFCell cellA1 = row.createCell(j);
                    cellA1.setCellValue(data.getDatas().get(j));
                    HSSFCellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
                    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    cellA1.setCellStyle(cellStyle);
                }
            }

            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
