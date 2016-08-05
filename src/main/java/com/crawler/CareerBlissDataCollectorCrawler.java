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

public class CareerBlissDataCollectorCrawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissDataCollectorCrawler.class);

    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
            "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    public static final String PAGING_PREFIX = "https://www.careerbliss.com/google/reviews/";

    CrawlData myCrawlStat;

    public CareerBlissDataCollectorCrawler() {
        myCrawlStat = new CrawlData();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith(PAGING_PREFIX);
    }

    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        myCrawlStat.incProcessedPages();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();

            Document doc = Jsoup.parse(parseData.getHtml());

            Elements reviewElements = doc.getElementsByClass("company-reviews");
            for (int i=0; i < reviewElements.size(); i++) {
                Element reviewElement = reviewElements.get(i);
                ReviewData reviewData = new ReviewData();
                reviewData.getDatas().add(reviewElement.getElementsByClass("comments").text());

                myCrawlStat.addReviewData(reviewData);
            }
            Set<WebURL> links = parseData.getOutgoingUrls();
//            myCrawlStat.incTotalLinks(links.size());
//            for (WebURL webURL : links) {
//                if (webURL.getURL().startsWith(PAGING_PREFIX)) {
//                    myCrawlStat.addToLinkPages(webURL.getURL());
//                }
//            }
//                System.out.println("QuynhTest: " + links);
        }
        // We dump this crawler statistics after processing every 50 pages
        if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
            dumpMyData();
        }
    }

    /**
     * This function is called by controller to get the local data of this crawler when job is finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpMyData();
    }

    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
    }
}
