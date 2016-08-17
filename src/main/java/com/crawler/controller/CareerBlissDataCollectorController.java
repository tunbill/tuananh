package com.crawler.controller;

import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.CrawlerConstants;
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

public class CareerBlissDataCollectorController extends BaseController<CareerBlissDataCollectorCrawler> {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissDataCollectorController.class);

    public static void main(String[] args) throws Exception {
        saveData(CareerBlissDataCollectorCrawler.class,
            "https://www.careerbliss.com/google/reviews/",
            "Careerbliss");
    }
}
