package com.crawler;

import com.crawler.model.CrawlData;
import com.crawler.model.ReviewData;
import com.crawler.poi.WriteToFile;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CareerBlissDataCollectorCrawler extends MyCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissDataCollectorCrawler.class);

    public CareerBlissDataCollectorCrawler() {
        super("https://www.careerbliss.com/google/reviews/");
    }

    @Override
    public void readData(Document doc) {
        Elements reviewElements = doc.getElementsByClass("company-reviews");
        for (int i=0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);
            ReviewData reviewData = new ReviewData();
            reviewData.getDatas().add(reviewElement.getElementsByClass("comments").text());

            myCrawlStat.addReviewData(reviewData);
        }
    }
}
