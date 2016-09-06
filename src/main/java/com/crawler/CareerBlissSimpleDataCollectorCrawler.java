package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import java.util.Set;

public class CareerBlissSimpleDataCollectorCrawler extends MyCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBlissSimpleDataCollectorCrawler.class);

    public CareerBlissSimpleDataCollectorCrawler() {
        super("https://www.careerbliss.com/", ImmutableList.of("/reviews/?page="), "Careerbliss");
    }

    @Override
    public boolean shouldReadData(String url) {
        System.out.println("Read it: " + url);
        return true;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (href.equals("https://www.careerbliss.com/pg/reviews/plant-technician/#/1752680")) {
            System.out.println("MMMMMMMMMMMMMMMMMMM");
            return true;
        }
        return false;
    }

    @Override
    public void readData(Document doc) {
        System.out.println("Here: " + doc);
        Element reviewElements = doc.getElementsByClass("module-well").first();
        ReviewData reviewData = new ReviewData();
        List<String> datas = reviewData.getDatas();

        Set<String> commentClass = ImmutableSet.of("black");
        System.out.println("Gian: " + reviewElements);
//        datas.add(reviewElements.getElementsByClass("header13").first().text());  //Review Date --> don't know where to get
//
//        String comment = "";
//        Elements allDivs = reviewElements.getElementsByTag("div");
//        for (Element div : allDivs) {
//            // System.out.println("OHHHHHHHHH: " + div.text());
//            if ((div.classNames().size() == 1 && div.classNames().containsAll(commentClass))
//                || ("margin-bottom:10px;".equals(div.attr("style")))) {
//                comment = comment + div.text();
//            }
//        }
//        datas.add(comment);   //Review Comment
//
        myCrawlStat.addReviewData(reviewData);
    }

    public static void main(String[] agr) throws Exception {
        Document doc = Jsoup.parse("<div class=\"module-well\">\n" +
            "        <div class=\"featured-banner\">Requested Review</div>\n" +
            "        <div class=\"job-title header5 black\" style=\"margin-top: 25px;\">SAP Consultant</div>\n" +
            "        <div style=\"margin-top: 10px; margin-bottom: 10px;\"><div class=\"rating \"><span class=\"full\"></span><span class=\"full\"></span><span class=\"full\"></span><span class=\"empty\"></span><span class=\"empty\"></span></div><span class=\"header5 matteblack\" style=\"margin-left: 10px; margin-right:10px;\">3.0</span><span class=\"header13\">Posted last year in Edison, NJ</span></div>\n" +
            "            <div class=\"black\"><strong>Please give us a one liner to describe this review.</strong></div>\n" +
            "            <div style=\"margin-bottom:10px;\"><em>\"Good company but had a 3 month project. Had to step over for a friend during emergency.\"</em></div>\n" +
            "            <div class=\"black\"><strong>Overall review of job.</strong></div>\n" +
            "            <div style=\"margin-bottom:10px;\"><em>\"I love the fact I get to interact with different teams in a BU, work with cross functional teams.\"</em></div>\n" +
            "            <div class=\"black\"><strong>Salary review.</strong></div>\n" +
            "            <div style=\"margin-bottom:10px;\"><em>\"Very less, i do not see any pay hikes. Good company but pay is way too less.\"</em></div>\n" +
            "                        <div class=\"rankings\" style=\"margin-top:15px;\">\n" +
            "                            <div class=\"row-fluid\" style=\"margin-bottom:5px;\"> \n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Person You Work For\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            People You Work With\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Work Setting\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                            </div>\n" +
            "                            <div class=\"row-fluid\" style=\"margin-bottom:5px;\"> \n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Support You Get\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Rewards You Receive\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Growth Opportunities\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                            </div>\n" +
            "                            <div class=\"row-fluid\" style=\"margin-bottom:5px;\"> \n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Company Culture\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                        <span class=\"span3 header9 text-right category\" style=\"line-height: 17px;\">\n" +
            "                            Way You Work\n" +
            "                        </span>\n" +
            "                        <span class=\"span1 header8 value\" style=\"color:#666;\"><span style=\"color: black;\">3</span> / 5</span>\n" +
            "                            </div>\n" +
            "                </div>\n" +
            "    </div>");

        Element reviewElements = doc.getElementsByClass("module-well").first();
        ReviewData reviewData = new ReviewData();
        List<String> datas = reviewData.getDatas();

        Set<String> commentClass = ImmutableSet.of("black");
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
        System.out.println(datas);
    }
}
