package com.crawler.controller;

import com.crawler.CrawlerConstants;
import com.crawler.model.CrawlData;
import com.crawler.model.Crawler;
import com.crawler.model.ReviewData;
import com.crawler.poi.ExcelRead;
import com.google.common.collect.ImmutableSet;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseController {
    public static List<String> getSimpleData(String urlStr) throws Exception{
        //"https://www.careerbliss.com/Company/GetClickedReview?companyId=98312&reviewId=1752680&oldBootstrap=true"
        String dataHTML = "";
        URL url = new URL(urlStr);
        try (InputStream is = url.openStream();
             JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            dataHTML = obj.getString("html");
        }
        Document doc = Jsoup.parse(dataHTML);

        Element reviewElements = doc.getElementsByClass("module-well").first();
        List<String> datas = new ArrayList<String>();

        Set<String> commentClass = ImmutableSet.of("black");
        datas.add(reviewElements.getElementsByClass("header13").first().text());  //Review Date --> don't know where to get

        String comment = "";
        Elements allDivs = reviewElements.getElementsByTag("div");
        for (Element div : allDivs) {
            if ((div.classNames().size() == 1 && div.classNames().containsAll(commentClass))
                || ("margin-bottom:10px;".equals(div.attr("style")))) {
                comment = comment + div.text();
            }
        }
        datas.add(comment);   //Review Comment

        return datas;
    }
    public static List<ReviewData> getCrawlerData(Crawler crawler, String filePath) throws Exception{
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CrawlerConstants.ROOT_FOLDER);
        config.setMaxPagesToFetch(CrawlerConstants.NUMBER_OF_CRAWLER);
        config.setConnectionTimeout(100000);
        //config.setMaxDepthOfCrawling(1);
        config.setMaxTotalConnections(100);
        config.setPolitenessDelay(crawler.getTimeout());

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(crawler.getBaseAddress());
        controller.setCustomData(filePath);
        controller.start(crawler.getType(), CrawlerConstants.NUMBER_OF_CRAWLER);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        int totalProcessedPages = 0;
        List<ReviewData> allReviewDatas = new ArrayList<ReviewData>();

        int index = 0;
        //Workbook currentWorkBook = objExcelFile.readExcel(filePath);
        for (Object localData : crawlersLocalData) {
            CrawlData stat = (CrawlData) localData;
            totalProcessedPages += stat.getTotalProcessedPages();
            allReviewDatas.addAll(stat.getReviewDatas());
            if (allReviewDatas.size() > 20000) {
                writeToFile(filePath, crawler.getSheetName(), allReviewDatas, index, crawler.isHasIndex());
                index++;
                allReviewDatas = new ArrayList<ReviewData>();
            }
        }

        System.out.println("Aggregated Statistics:");
        System.out.println("\tProcessed Pages: {}" + totalProcessedPages);

        writeToFile(filePath, crawler.getSheetName(), allReviewDatas, ++index, crawler.isHasIndex());
        return allReviewDatas;
    }

    private static void writeToFile(String filePath, String sheetName, List<ReviewData> allReviewDatas, int index, boolean hasIndex) throws Exception{
        ExcelRead objExcelFile = new ExcelRead();
        Workbook currentWorkBook = objExcelFile.readExcel(filePath);
            try {
                Sheet sheet = currentWorkBook.getSheet(sheetName);
                int rowIndex = sheet.getLastRowNum();
                sheet.setForceFormulaRecalculation(true);

                for (int i = 0; i < allReviewDatas.size(); i++) {
                    ReviewData reviewDatas = allReviewDatas.get(i);
                    List<String> reviewData = reviewDatas.getDatas();

                    if (hasIndex) {
                        reviewData.add(3, "" + (rowIndex - 6));
                    }

                    Row row = sheet.createRow(++rowIndex);
                    for (int j = 0; j < reviewData.size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(reviewData.get(j));
                    }
                }

                //System.out.println("Ro la dien tiet: " + getMyController().getCustomData());
                //HSSFFormulaEvaluator.evaluateAllFormulaCells(currentWorkBook);
                FileOutputStream outputStream = new FileOutputStream(new File(sheetName + index + ".xlsx"), true);
                currentWorkBook.write(outputStream);
                System.out.println("Wrote in Excel: " + index);
                outputStream.flush();
                outputStream.close();

            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
    }
}
