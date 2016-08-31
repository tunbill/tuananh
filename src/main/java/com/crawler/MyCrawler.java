package com.crawler;

import com.crawler.model.CrawlData;
import com.crawler.model.ReviewData;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Pattern;

public abstract class MyCrawler extends WebCrawler {
    protected static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
            "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    protected String prefixPage;
    protected List<String> urlContainString;

    CrawlData myCrawlStat;

    public MyCrawler(String prefixPage, List<String> urlContainString) {
        this.prefixPage = prefixPage;
        this.urlContainString = urlContainString;
        myCrawlStat = new CrawlData();
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("Visited: {}", url);
        myCrawlStat.incProcessedPages();

        if (shouldReadData(url) && (page.getParseData() instanceof HtmlParseData)) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();

            Document doc = Jsoup.parse(parseData.getHtml());

            readData(doc);
        }
        // We dump this crawler statistics after processing every 50 pages
        if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
            System.out.println("Buc minh lam y: " + myCrawlStat.getTotalProcessedPages());
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

    public abstract void readData(Document doc);

    public abstract boolean shouldReadData(String url);
}
