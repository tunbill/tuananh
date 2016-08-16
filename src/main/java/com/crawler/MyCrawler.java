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

import java.util.regex.Pattern;

public abstract class MyCrawler extends WebCrawler {
    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
            "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private String prefixPage;

    CrawlData myCrawlStat;

    public MyCrawler(String prefixPage) {
        this.prefixPage = prefixPage;
        myCrawlStat = new CrawlData();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (!FILTERS.matcher(href).matches() && href.startsWith(prefixPage.toLowerCase())) {
            System.out.println("Should visit: " + url.getURL());
            return true;
        }
        return false;
    }

    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        myCrawlStat.incProcessedPages();

        if (page.getParseData() instanceof HtmlParseData) {
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
}
