package com.crawler.controller;

import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.CrawlerConstants;
import com.crawler.IndeedDataCollectorCrawler;
import com.crawler.VaultDataCollectorCrawler;
import com.crawler.model.CrawlData;
import com.crawler.model.Crawler;
import com.crawler.model.ReviewData;
import com.crawler.poi.ExcelRead;
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

        List<Crawler> allCrawlers = new ArrayList<Crawler>();
        allCrawlers.add(new Crawler("http://www.vault.com/company-profiles/internet-social-media/google-inc/employee-reviews", VaultDataCollectorCrawler.class, "Vault"));
        allCrawlers.add(new Crawler("https://www.careerbliss.com/google/reviews/", CareerBlissDataCollectorCrawler.class, "Careerbliss"));
        allCrawlers.add(new Crawler("http://www.indeed.com/cmp/Google/reviews?fcountry=ALL", IndeedDataCollectorCrawler.class, "Indeed"));

        try {
            FileOutputStream outputStream = new FileOutputStream(new File(folderPath + "CrawlerData.xlsx"), true);

            for (Crawler crawler : allCrawlers) {
                List<ReviewData> allReviewDatas = getCrawlerData(crawler.getBaseAddress(), crawler.getType());
                Sheet sheet = currentWorkBook.getSheet(crawler.getSheetName());
                int rowc = 7;

                for (int i = 0; i < allReviewDatas.size(); i++) {
                    ReviewData data = allReviewDatas.get(i);
                    Row row = sheet.createRow(++rowc);
                    for (int j = 0; j < data.getDatas().size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(data.getDatas().get(j));
                    }
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

    private static List<ReviewData> getCrawlerData(String baseAddress, Class type) throws Exception{
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CrawlerConstants.ROOT_FOLDER);
        config.setMaxPagesToFetch(CrawlerConstants.NUMBER_OF_CRAWLER);
        config.setPolitenessDelay(1000);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(baseAddress);
        controller.start(type, CrawlerConstants.NUMBER_OF_CRAWLER);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        int totalProcessedPages = 0;
        List<ReviewData> allReviewDatas = new ArrayList<ReviewData>();
        for (Object localData : crawlersLocalData) {
            CrawlData stat = (CrawlData) localData;
            totalProcessedPages += stat.getTotalProcessedPages();

            allReviewDatas.addAll(stat.getReviewDatas());
        }

        System.out.println("Aggregated Statistics:");
        System.out.println("\tProcessed Pages: {}" + totalProcessedPages);

        return allReviewDatas;
    }
}
