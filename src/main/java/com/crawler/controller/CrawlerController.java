package com.crawler.controller;

import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.CareerBlissMainPageCrawler;
import com.crawler.CrawlerConstants;
import com.crawler.IndeedDataCollectorCrawler;
import com.crawler.IndeedMainPageCrawler;
import com.crawler.VaultDataCollectorCrawler;
import com.crawler.VaultMainPageCrawler;
import com.crawler.model.CrawlData;
import com.crawler.model.Crawler;
import com.crawler.model.ReviewData;
import com.crawler.poi.ExcelRead;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CrawlerController {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Needed parameters: ");
            System.out.println("\t tempalteFilePath (it will contain the full path to template file)");
            return;
        }
        ExcelRead objExcelFile = new ExcelRead();
        Workbook currentWorkBook = objExcelFile.readExcel(args[0]);

        try {

            Map<String, Integer> sheets = ImmutableMap.of("Current S&P 500", 2, "Former S&P 500", 3);
            Set<String> companies = readAllCompanies(currentWorkBook, sheets);

//            FileOutputStream outputStream = new FileOutputStream(new File("CrawlerData.xlsx"), true);
//
//            List<Crawler> allCrawlers = new ArrayList<Crawler>();
//
//            allCrawlers.add(new Crawler("http://www.vault.com/search-results/CompanyResultsPage?iID=4118", VaultDataCollectorCrawler.class, "Vault"));
//            allCrawlers.add(new Crawler("https://www.careerbliss.com/index/?pageType=ReviewsByCompanyName&letter=a", CareerBlissDataCollectorCrawler.class, "Careerbliss"));
//
//           // allCrawlers.add(new Crawler("http://www.indeed.com/Best-Places-to-Work", IndeedDataCollectorCrawler.class, "Indeed"));
//
//            for (Crawler baseCrawler : allCrawlers) {
//                Sheet sheet = currentWorkBook.getSheet(baseCrawler.getSheetName());
//                List<ReviewData> allReviewDatas = BaseController.getCrawlerData(baseCrawler.getBaseAddress(), baseCrawler.getType());
//                int rowIndex = 7;
//                System.out.println("BUC MINH NHA: " + allReviewDatas.size());
//                for (int i = 0; i < allReviewDatas.size(); i++) {
//                    ReviewData reviewDatas = allReviewDatas.get(i);
//                    List<String> reviewData = reviewDatas.getDatas();
//
//                    reviewData.add(2, "" + (rowIndex - 6));
//
//                    Row row = sheet.createRow(++rowIndex);
//                    for (int j = 0; j < reviewData.size(); j++) {
//                        Cell cell = row.createCell(j);
//                        cell.setCellValue(reviewData.get(j));
//                    }
//                }
//
//            }
//            currentWorkBook.write(outputStream);
//            System.out.println("Wrote in Excel");
//            outputStream.flush();
//            outputStream.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    private static List<ReviewData> testDatas() {
//            allCrawlers.add(new Crawler("http://www.vault.com/company-profiles/internet-social-media/google-inc/employee-reviews", VaultDataCollectorCrawler.class, "Vault"));
//            allCrawlers.add(new Crawler("https://www.careerbliss.com/google/reviews/", CareerBlissDataCollectorCrawler.class, "Careerbliss"));
//            allCrawlers.add(new Crawler("http://www.indeed.com/cmp/Google/reviews?fcountry=ALL", IndeedDataCollectorCrawler.class, "Indeed"));
        List<ReviewData> result = new ArrayList<ReviewData>();
        ReviewData data = new ReviewData();
        data.setDatas(ImmutableList.of("Southwest Airlines", "/cmp/Southwest-Airlines"));
        result.add(data);
        data = new ReviewData();
        data.setDatas(ImmutableList.of("Colgate-Palmolive", "/cmp/Colgate--palmolive"));
        result.add(data);
        return result;
    }

    public static Set<String> readAllCompanies(Workbook currentWorkBook, Map<String, Integer> sheetNames) {
        Set<String> result = new HashSet<String>();
        for (Map.Entry<String, Integer> sheetName : sheetNames.entrySet()) {
            Sheet sheet = currentWorkBook.getSheet(sheetName.getKey());

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
    System.out.println("Number of rows: " + sheet.getLastRowNum());
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row != null) {
                    Cell cell = row.getCell(sheetName.getValue());
                    if (cell != null) {
                        result.add(cell.getStringCellValue());
                    }
                }
            }
        }
        return result;
    }
}
