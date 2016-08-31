package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CareerBlissDataCollectorCrawler extends MyCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissDataCollectorCrawler.class);
    private static final Map<String, Float> rateKeyValues = ImmutableMap.of("full", 1F, "quarter", 0.25F, "half", 0.5F, "three-quarters", 0.75F, "empty", 0F);
    private static final List<String> rankingKeys = ImmutableList.of("Person You Work For", "People You Work With", "Work Setting", "Support You Get",
        "Rewards You Receive", "Growth Opportunities", "Company Culture", "Way You Work");

    public CareerBlissDataCollectorCrawler() {
        super("https://www.careerbliss.com/", ImmutableList.of("/reviews/?page="));
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
        if (href.contains("pageType=ReviewsByCompanyName".toLowerCase()) || href.endsWith("/reviews/") || href.contains("/reviews/?page=")) {
            return true;
        }
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
                ReviewData reviewData = new ReviewData();
                List<String> datas = reviewData.getDatas();
                datas.add("");
                datas.add(comName);    //Company Name
                //datas.add("" + (i+1));  //Revew Number
                datas.add(reviewElement.getElementsByClass("job-title").text());            //Review Position
                datas.add(getRatingValue(reviewElement.getElementsByClass("rating-container")));        //Review rating
                datas.add(reviewElement.getElementsByClass("header13").text());   //Review Location
                datas.add("");  //Review Date --> don't know where to get
                datas.add(reviewElement.getElementsByClass("comments").text());   //Review Comment

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
        Document doc = Jsoup.parse("<div class=\"company-reviews\">\n" +
            "            <a class=\"job-title header5 twocentChromeExt\" href=\"https://www.careerbliss.com/google/reviews/strategist/#446028\" data-reviewid=\"446028\" data-company=\"Google\" data-jobtitle=\"Strategist\" data-companyid=\"289\" data-jobtitleid=\"18052\">Strategist</a>\n" +
            "            <div class=\"rating-container\">\n" +
            "<div class=\"rating large-star\"><span class=\"full\"></span><span class=\"full\"></span><span class=\"half\"></span><span class=\"empty\"></span><span class=\"empty\"></span></div>                <span class=\"header13\">\n" +
            "                        in Mountain View, CA\n" +
            "                </span>\n" +
            "            </div>\n" +
            "<p class=\"black\" style=\"margin-bottom:0;\"><strong>What do you like about working at Google?</strong></p>                    <p class=\"comments foggy\">\"I like the food, flexibility, smart people I work with, and transparent culture.\"</p>\n" +
            "<p class=\"black\" style=\"margin-bottom:0;\"><strong>Do you have any tips for others interviewing with this company?</strong></p>                    <p class=\"comments foggy\">\"Be prepared for the initial enthusiasm to soon subside, and be relegated to a job that you're probably not interested in.\"</p>\n" +
            "<p class=\"black\" style=\"margin-bottom:0;\"><strong>What don't you like about working at Google?</strong></p>                    <p class=\"comments foggy\">\"I dislike the monotony of the job, no clear career path, getting stuck, managers seeming oblivious and ineffective, and too much ambiguity and being left up to chance.\"</p>\n" +
            "<p class=\"black\" style=\"margin-bottom:0;\"><strong>What suggestions do you have for management?</strong></p>                    <p class=\"comments foggy\">\"I would suggest less red tape, faster-moving decisions, and more insight into why certain decisions are made.\"</p>\n" +
            "            \n" +
            "            <div class=\"rankings\">\n" +
            "                        <div class=\"row-fluid\"> \n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Person You Work For\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">3</span> / 5</span>\n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            People You Work With\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">3</span> / 5</span>\n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Work Setting\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">3</span> / 5</span>\n" +
            "                        </div>\n" +
            "                        <div class=\"row-fluid\"> \n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Support You Get\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">2</span> / 5</span>\n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Rewards You Receive\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">3</span> / 5</span>\n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Growth Opportunities\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">2</span> / 5</span>\n" +
            "                        </div>\n" +
            "                        <div class=\"row-fluid\"> \n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Company Culture\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">2</span> / 5</span>\n" +
            "                    <span class=\"span3 header9 text-right category\">\n" +
            "                            Way You Work\n" +
            "                    </span>\n" +
            "                    <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color:black;\" class=\"foggy\">2</span> / 5</span>\n" +
            "                        </div>\n" +
            "\t\t    </div>   \n" +
            "        </div>");

        Elements fullEles = doc.getElementsByClass("row-fluid");
            System.out.println("Buc minh: " + getRankingValue(fullEles));
        //System.out.println();
    }
}
