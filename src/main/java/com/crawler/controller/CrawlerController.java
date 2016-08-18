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
import java.util.List;

public class CrawlerController {
    public static void main(String[] args) throws Exception {
        ExcelRead objExcelFile = new ExcelRead();
        String folderPath = System.getProperty("user.dir") + "/data/";
        Workbook currentWorkBook = objExcelFile.readExcel(folderPath, "Data.xlsx");

        try {
            FileOutputStream outputStream = new FileOutputStream(new File(folderPath + "CrawlerData.xlsx"), true);

            List<Crawler> allCrawlers = new ArrayList<Crawler>();

            //allCrawlers.add(new Crawler("http://www.vault.com/search-results/CompanyResultsPage?iID=4118", VaultMainPageCrawler.class, "Vault"));
            allCrawlers.add(new Crawler("https://www.careerbliss.com/index/?pageType=ReviewsByCompanyName&letter=a", CareerBlissMainPageCrawler.class, "Careerbliss"));
            //allCrawlers.add(new Crawler("http://www.indeed.com/Best-Places-to-Work", IndeedMainPageCrawler.class, "Indeed"));

            for (Crawler baseCrawler : allCrawlers) {
                Sheet sheet = currentWorkBook.getSheet(baseCrawler.getSheetName());
                List<ReviewData> allIndeedCompanies = BaseController.getCrawlerData(baseCrawler.getBaseAddress(), baseCrawler.getType());
                int rowIndex = 7;
                String prefixUrl = "", suffixUrl = "";
                String typeName = baseCrawler.getType().getName();
                Class nextCrawlerPage = null;
                if (typeName.contains("IndeedMainPageCrawler")) {
                    prefixUrl = "http://www.indeed.com";
                    suffixUrl = "/reviews?fcountry=ALL";
                    nextCrawlerPage = IndeedDataCollectorCrawler.class;
                } else if (typeName.contains("CareerBlissMainPageCrawler")) {
                    prefixUrl = "https://www.careerbliss.com";
                    nextCrawlerPage = CareerBlissDataCollectorCrawler.class;
                } else if (typeName.contains("VaultMainPageCrawler")) {
                    prefixUrl = "http://www.vault.com";
                    suffixUrl = "/employee-reviews";
                    nextCrawlerPage = VaultDataCollectorCrawler.class;
                }

                for (ReviewData datas : allIndeedCompanies) {
                    List<String> data = datas.getDatas();
                    System.out.println(data.get(0) + " || " + prefixUrl + data.get(1) + suffixUrl + " || " + nextCrawlerPage);

//                    if (null != nextCrawlerPage) {
//                        List<ReviewData> allReviewDatas = BaseController.getCrawlerData(prefixUrl + data.get(1) + suffixUrl, nextCrawlerPage);
//
//                        for (int i = 0; i < allReviewDatas.size(); i++) {
//                            ReviewData reviewDatas = allReviewDatas.get(i);
//                            List<String> reviewData = reviewDatas.getDatas();
//
//                            reviewData.add(1, "" + (rowIndex - 6));
//                            reviewData.add(1, data.get(0));//Company name
//
//                            Row row = sheet.createRow(++rowIndex);
//                            for (int j = 0; j < reviewData.size(); j++) {
//                                Cell cell = row.createCell(j);
//                                cell.setCellValue(reviewData.get(j));
//                            }
//                        }
//                    }

                }
            }
            currentWorkBook.write(outputStream);
            System.out.println("Wrote in Excel");
            outputStream.flush();
            outputStream.close();
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
}
