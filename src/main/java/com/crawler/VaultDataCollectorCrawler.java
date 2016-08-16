package com.crawler;

import com.crawler.model.ReviewData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VaultDataCollectorCrawler extends MyCrawler {
    public VaultDataCollectorCrawler() {
        super("http://www.vault.com/company-profiles/internet-social-media/google-inc/employee-reviews");
    }

    @Override
    public void readData(Document doc) {
        Elements reviewElements = doc.getElementsByClass("verticalPadding10");
        System.out.println("Number of tag: " + reviewElements.size());
        for (int i=0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);

            Elements titleElement = reviewElement.getElementsByTag("h2");
            ReviewData reviewData = new ReviewData();
            reviewData.getDatas().add(titleElement.get(0).text());

            myCrawlStat.addReviewData(reviewData);
        }

    }
}
