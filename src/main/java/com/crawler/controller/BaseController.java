package com.crawler.controller;

import com.crawler.CrawlerConstants;
import com.crawler.model.CrawlData;
import com.crawler.model.ReviewData;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;
import java.util.List;

public class BaseController {
    public static List<ReviewData> getCrawlerData(String baseAddress, Class type) throws Exception{
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CrawlerConstants.ROOT_FOLDER);
        config.setMaxPagesToFetch(CrawlerConstants.NUMBER_OF_CRAWLER);
        config.setPolitenessDelay(10);

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
