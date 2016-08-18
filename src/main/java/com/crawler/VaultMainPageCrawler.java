package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableMap;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VaultMainPageCrawler extends MyCrawler {
    private static final Map<String, Float> rateKeyValues = ImmutableMap.of("one", 1F, "two", 2F, "three", 3F, "four", 4F, "five", 5F);
    private String reviewType = "Company";

    public VaultMainPageCrawler() {
        super("http://www.vault.com/search-results/CompanyResultsPage?iID=4118", "&pg=");
    }

    @Override
    public void readData(Document doc) {
        Elements reviewElements = doc.getElementsByAttributeValueStarting("id", "ContentPlaceHolderDefault_SectionContent_SearchResults_ResultsRepeater_LinkToCompany_");
        for (int i = 0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);

            ReviewData reviewData = new ReviewData();
            List<String> datas = reviewData.getDatas();

            datas.add(reviewElement.text()); //Company Name
            datas.add(reviewElement.attr("href"));    //Company Link
            myCrawlStat.addReviewData(reviewData);
        }

    }
}