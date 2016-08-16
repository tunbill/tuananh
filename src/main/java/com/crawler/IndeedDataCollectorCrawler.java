package com.crawler;

import com.crawler.model.ReviewData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndeedDataCollectorCrawler extends MyCrawler {
    public IndeedDataCollectorCrawler() {
        super("http://www.indeed.com/cmp/Google/reviews?fcountry=ALL&start=");
    }

    @Override
    public void readData(Document doc) {
        Elements reviewElements = doc.getElementsByClass("cmp-review");
        System.out.println("Number of tag: " + reviewElements.size());
        for (int i=0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);

            Elements titleElement = reviewElement.getElementsByClass("cmp-review-title");
            Elements spanEles = titleElement.get(0).getElementsByTag("span");
            ReviewData reviewData = new ReviewData();
            reviewData.getDatas().add(spanEles.get(0).text());

            myCrawlStat.addReviewData(reviewData);
        }

    }
}
