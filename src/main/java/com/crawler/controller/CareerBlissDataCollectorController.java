package com.crawler.controller;

import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.model.CrawlData;
import com.crawler.model.ReviewData;
import com.crawler.poi.WriteToFile;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CareerBlissDataCollectorController {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissDataCollectorController.class);

    public static void main(String[] args) throws Exception {
//        if (args.length != 2) {
//            logger.info("Needed parameters: ");
//            logger.info("\t rootFolder (it will contain intermediate crawl data)");
//            logger.info("\t numberOfCralwers (number of concurrent threads)");
//            return;
//        }

        String rootFolder = "/home/quynh/tmp";
        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(rootFolder);
        config.setMaxPagesToFetch(10);
        config.setPolitenessDelay(1000);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://www.careerbliss.com/google/reviews/");
        controller.start(CareerBlissDataCollectorCrawler.class, numberOfCrawlers);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        long totalLinks = 0;
        long totalTextSize = 0;
        int totalProcessedPages = 0;
        List<ReviewData> allReviewDatas = new ArrayList<ReviewData>();
        for (Object localData : crawlersLocalData) {
            CrawlData stat = (CrawlData) localData;
            totalLinks += stat.getTotalLinks();
            totalProcessedPages += stat.getTotalProcessedPages();

            allReviewDatas.addAll(stat.getReviewDatas());
        }

        System.out.println("Aggregated Statistics:");
        System.out.println("\tProcessed Pages: {}" + totalProcessedPages);
        System.out.println("\tTotal Links found: {}" + totalLinks);
        System.out.println("\tTotal Text Size: {}" + totalTextSize);

        WriteToFile.writeToExcel("/home/quynh/tmp/data.xlsx", allReviewDatas);
    }
}
