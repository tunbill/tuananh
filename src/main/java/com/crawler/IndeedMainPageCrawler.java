package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndeedMainPageCrawler extends MyCrawler {
    private static final List<String> rankingKeys = ImmutableList.of("Job Work/Life Balance", "Compensation/Benefits", "Job Security/Advancement", "Management", "Job Culture");

    public IndeedMainPageCrawler() {
        super("http://www.indeed.com/Best-Places-to-Work", ImmutableList.of("?i=all-industries&l=all-places&start="));
    }
    @Override
    public boolean shouldReadData(String url) {
        return false;
    }


    @Override
    public void readData(Document doc) {
        Elements companyElements = doc.getElementsByClass("cmp-company-tile-name");
        for (int i=0; i < companyElements.size(); i++) {
            Element companyElement = companyElements.get(i);

            ReviewData reviewData = new ReviewData();
            List<String> datas = reviewData.getDatas();

            datas.add(companyElement.getElementsByTag("h4").first().text()); //Company Name
            datas.add(companyElement.getElementsByTag("a").first().attr("href"));    //Company Link

            myCrawlStat.addReviewData(reviewData);
        }

    }

}
