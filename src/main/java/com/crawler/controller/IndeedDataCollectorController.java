package com.crawler.controller;

import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.IndeedDataCollectorCrawler;
import com.crawler.model.CrawlData;
import com.crawler.model.ReviewData;
import com.crawler.poi.WriteToFile;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IndeedDataCollectorController extends BaseController<IndeedDataCollectorCrawler> {
    private static final Logger logger = LoggerFactory.getLogger(IndeedDataCollectorController.class);

    public static void main(String[] args) throws Exception {
        saveData(IndeedDataCollectorCrawler.class,
            "http://www.indeed.com/cmp/Google/reviews?fcountry=ALL",
            "Indeed");
    }
}
