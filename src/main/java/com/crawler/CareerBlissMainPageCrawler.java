package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CareerBlissMainPageCrawler extends MyCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissMainPageCrawler.class);

    public CareerBlissMainPageCrawler() {
        super("https://www.careerbliss.com/index/?", ImmutableList.of("pageType=ReviewsByCompanyName"));
    }
    @Override
    public boolean shouldReadData(String url) {
        return false;
    }


    @Override
    public void readData(Document doc) {
        Elements reviewElements = doc.getElementsByClass("browse-list");
        for (int i=0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);

            Elements liElements = reviewElement.getElementsByTag("li");
            for (int j = 0; j < liElements.size(); j++) {
                Element hrefEle = liElements.get(j).getElementsByTag("a").first();
                ReviewData reviewData = new ReviewData();
                List<String> datas = reviewData.getDatas();
                datas.add(hrefEle.text()); //Company Name
                datas.add(hrefEle.attr("href"));    //Company Link

                myCrawlStat.addReviewData(reviewData);

            }
        }
    }

}
