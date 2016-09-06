package com.crawler;

import com.crawler.controller.BaseController;
import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.bouncycastle.jce.provider.symmetric.Grain128.Base;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CareerBlissDataCollectorCrawler extends MyCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissDataCollectorCrawler.class);
    private static final Map<String, Float> rateKeyValues = ImmutableMap.of("full", 1F, "quarter", 0.25F, "half", 0.5F, "three-quarters", 0.75F, "empty", 0F);
    private static final List<String> rankingKeys = ImmutableList.of("Person You Work For", "People You Work With", "Work Setting", "Support You Get",
        "Rewards You Receive", "Growth Opportunities", "Company Culture", "Way You Work");

    public CareerBlissDataCollectorCrawler() {
        super("https://www.careerbliss.com/", ImmutableList.of("/reviews/?page="), "Careerbliss");
    }

    @Override
    public boolean shouldReadData(String url) {
        url = url.toLowerCase();
        if (url.contains("/reviews/".toLowerCase()) || url.contains("/reviews/?page=")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (!FILTERS.matcher(href).matches() && href.startsWith(prefixPage.toLowerCase()) && hrefChecking(href)) {
            //System.out.println("Should visit from CareerBliss: " + url.getURL());
            return true;
        }
        return false;
    }

    protected boolean hrefChecking(String href) {
//        if (href.contains("pageType=ReviewsByCompanyName".toLowerCase())
//            || href.endsWith("/reviews/") || href.contains("/reviews/?page=")) {
//            return true;
//        }
        return false;
    }

    @Override
    public void readData(Document doc) {
        String pageTitle = doc.title();
        if (pageTitle.endsWith("Reviews | CareerBliss") || pageTitle.contains("Reviews Page")) {
            String comName = doc.getElementsByClass("company-info-header").first().getElementsByTag("strong").first().text();
            System.out.println("Ha ha: " + comName);
            Elements reviewElements = doc.getElementsByClass("company-reviews");
            for (int i=0; i < reviewElements.size(); i++) {
                Element reviewElement = reviewElements.get(i);
                Element subUrlEle = reviewElement.getElementsByTag("a").first();
                String subUrl = "https://www.careerbliss.com/Company/GetClickedReview?companyId=" + subUrlEle.attr("data-companyid")+ "&reviewId=" + subUrlEle.attr("data-reviewid") +"&oldBootstrap=true";
                List<String> dateAndComent  = null;
                try {
                    dateAndComent = BaseController.getSimpleData(subUrl);
                } catch (Exception e){}

                ReviewData reviewData = new ReviewData();
                List<String> datas = reviewData.getDatas();
                datas.add("");
                datas.add(comName);    //Company Name
                datas.add("");
                //datas.add("" + (i+1));  //Revew Number
                datas.add(reviewElement.getElementsByClass("job-title").text());            //Review Position
                datas.add(getRatingValue(reviewElement.getElementsByClass("rating-container")));        //Review rating
                datas.add(reviewElement.getElementsByClass("header13").text());   //Review Location
                if (dateAndComent != null) {
                    datas.addAll(dateAndComent);
                } else {
                    datas.add("");  //Review Date --> don't know where to get
                    datas.add(reviewElement.getElementsByClass("comments").text());   //Review Comment
                }
                //Person you work for
                //People you work with
                //Work setting
                //Support you get
                //Rewards you receive
                //Growth opportunities
                //Company culture
                //Way you work
                Map<String, String> rankingValues = getRankingValue(reviewElement.getElementsByClass("row-fluid"));
                for (String rankingKey : rankingKeys) {
                    datas.add(rankingValues.get(rankingKey));
                }
                myCrawlStat.addReviewData(reviewData);
            }
        }
    }

    private static String getRatingValue(Elements ratingElement) {
        float rateValue = 0.0f;
        for (Map.Entry<String, Float> entry : rateKeyValues.entrySet())
        {
            Elements rateEles = ratingElement.get(0).getElementsByClass(entry.getKey());
            rateValue = rateValue + rateEles.size()*entry.getValue();
        }
        return "" + rateValue;
    }

    private static Map<String, String> getRankingValue(Elements rankingElement) {
        Map<String, String> rankingValues = new HashMap<String, String>();
        String key = null;
        for (int i = 0; i < rankingElement.size(); i++) {
            Elements spanEles = rankingElement.get(i).getAllElements();
            for (int j = 0; j < spanEles.size(); j++) {
                Element spanElement = spanEles.get(j);
                if (spanElement.className().contains("header9")) {
                    key = spanElement.text();
                } else if (spanElement.className().contains("foggy")) {
                    rankingValues.put(key, spanElement.text());
                }
            }
        }

        return rankingValues;
    }
    public static void main(String[] agr) throws Exception {
        String dataHTML = "";
        URL url = new URL("https://www.careerbliss.com/Company/GetClickedReview?companyId=98312&reviewId=1752680&oldBootstrap=true");
        try (InputStream is = url.openStream();
             JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            dataHTML = obj.getString("html");
        }
        Document doc = Jsoup.parse(dataHTML);

        Element reviewElements = doc.getElementsByClass("module-well").first();
        ReviewData reviewData = new ReviewData();
        List<String> datas = reviewData.getDatas();

        Set<String> commentClass = ImmutableSet.of("black");
        System.out.println("Gian: " + reviewElements);
        datas.add(reviewElements.getElementsByClass("header13").first().text());  //Review Date --> don't know where to get

        String comment = "";
        Elements allDivs = reviewElements.getElementsByTag("div");
        for (Element div : allDivs) {
            // System.out.println("OHHHHHHHHH: " + div.text());
            if ((div.classNames().size() == 1 && div.classNames().containsAll(commentClass))
                || ("margin-bottom:10px;".equals(div.attr("style")))) {
                comment = comment + div.text();
            }
        }
        datas.add(comment);   //Review Comment

        System.out.println("Datas: " + datas);
    }
}
