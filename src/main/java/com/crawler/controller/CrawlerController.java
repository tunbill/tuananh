package com.crawler.controller;

import com.crawler.CareerBliss2DataCollectorCrawler;
import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.CareerBlissMainPageCrawler;
import com.crawler.CrawlerConstants;
import com.crawler.IndeedDataCollectorCrawler;
import com.crawler.IndeedMainPageCrawler;
import com.crawler.Vault2DataCollectorCrawler;
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
        if (args.length > 1) {
            String sitesString = args[1];
            String[] sites = sitesString.split(",");
            if (sites != null && sites.length > 0) {

            }
        }
        String filePath = args[0];
        ExcelRead objExcelFile = new ExcelRead();
        Workbook currentWorkBook = objExcelFile.readExcel(filePath);

        try {

//            Map<String, Integer> sheets = ImmutableMap.of("Current S&P 500", 2, "Former S&P 500", 3);
//            Set<String> companies = readAllCompanies(currentWorkBook, sheets);


            List<Crawler> allCrawlers = new ArrayList<Crawler>();

            allCrawlers.add(new Crawler("http://www.vault.com/search-results/CompanyResultsPage?iID=4094,4095,4096,4097,4098,117634,4100,4101,4103,117630,4105,4106,117629,132250,4107,117633,4108,4109,4119,57949,4111,4112,4113,4114,4117,4118,4120,117631,4122,4123,4124,117635,4126,4127,4128,117632,4129,4130,4132,4133,117933,4134,4135,4137,4138,4140,4141,4142,4143",
                VaultDataCollectorCrawler.class, "Vault", 5000, true));
            allCrawlers.add(new Crawler("https://www.careerbliss.com/index/?pageType=ReviewsByCompanyName&letter=a",
                CareerBlissDataCollectorCrawler.class, "Careerbliss", 5000, true));
            allCrawlers.add(new Crawler("http://www.indeed.com/Best-Places-to-Work",
                IndeedDataCollectorCrawler.class, "Indeed", 5000, true));

            /*
            Vault 2
             */
            allCrawlers.add(new Crawler("http://www.vault.com/search-results/CompanyResultsPage?iID=4094,4095,4096,4097,4098,117634,4100,4101,4103,117630,4105,4106,117629,132250,4107,117633,4108,4109,4119,57949,4111,4112,4113,4114,4117,4118,4120,117631,4122,4123,4124,117635,4126,4127,4128,117632,4129,4130,4132,4133,117933,4134,4135,4137,4138,4140,4141,4142,4143",
                Vault2DataCollectorCrawler.class, "VAULT 2", 5000, false));

            /*
            CAREERBLISS 2
             */
            allCrawlers.add(new Crawler("https://www.careerbliss.com/index/?pageType=ReviewsByCompanyName&letter=a",
                CareerBliss2DataCollectorCrawler.class, "CAREERBLISS 2", 300, false));

            /*
            Just for testing each company
             */
//            allCrawlers.add(new Crawler("http://www.vault.com/company-profiles/tech-consulting/cognizant/employee-reviews?rt=salaries",
//                VaultDataCollectorCrawler.class, "Vault", 10000));
//            allCrawlers.add(new Crawler("http://www.vault.com/company-profiles/tech-consulting/cognizant/employee-reviews?rt=interviews",
//                VaultDataCollectorCrawler.class, "Vault", 10000));
//            allCrawlers.add(new Crawler("http://www.vault.com/company-profiles/tech-consulting/cognizant/employee-reviews",
//                VaultDataCollectorCrawler.class, "Vault", 10000));

            for (Crawler baseCrawler : allCrawlers) {
                List<ReviewData> allReviewDatas = BaseController.getCrawlerData(baseCrawler, filePath);

                //System.out.println("Data total: " + allReviewDatas.size());
            }

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

//    public static Set<String> readAllCompanies(Workbook currentWorkBook, Map<String, Integer> sheetNames) {
//        Set<String> result = new HashSet<String>();
//        for (Map.Entry<String, Integer> sheetName : sheetNames.entrySet()) {
//            Sheet sheet = currentWorkBook.getSheet(sheetName.getKey());
//
//            //Get iterator to all the rows in current sheet
//            Iterator<Row> rowIterator = sheet.iterator();
//    System.out.println("Number of rows: " + sheet.getLastRowNum());
//            while(rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                if (row != null) {
//                    Cell cell = row.getCell(sheetName.getValue());
//                    if (cell != null) {
//                        result.add(cell.getStringCellValue());
//                    }
//                }
//            }
//        }
//        return result;
//    }
}
