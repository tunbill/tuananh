package com.crawler.poi;

import com.crawler.model.ReviewData;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class AppendDataInExcel {
    public  void appendWrite(Workbook workbook, Sheet sheet, int rowc, List<ReviewData> datas) {
        for (int i = 0; i < datas.size(); i++) {
            ReviewData data = datas.get(i);
            Row row = sheet.createRow(++rowc);
            for (int j = 0; j < data.getDatas().size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data.getDatas().get(j));
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.dir") + "/data/CrawlerData.xlsx"), true);
            workbook.write(outputStream);
            System.out.println("Wrote in Excel");

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}
