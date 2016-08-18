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

public class VaultDataCollectorCrawler extends MyCrawler {
    private static final Map<String, Float> rateKeyValues =  ImmutableMap.of("one", 1F, "two", 2F, "three", 3F, "four", 4F, "five", 5F);
    private String reviewType = "Company";

    public VaultDataCollectorCrawler() {
        super("http://www.vault.com/company-profiles/", "/employee-reviews?rt=");
        //rateKeyValues.put("one-half", 0.5F);
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (!FILTERS.matcher(href).matches() && href.startsWith(prefixPage.toLowerCase())  && href.contains(urlContainString.toLowerCase()) && !href.contains("&")) {
            System.out.println("Should visit from vault: " + url.getURL());
            return true;
        }
        return false;
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("Visited: {}", url);

        if (url.contains("salaries")) {
            reviewType = "Salary";
        } else if (url.contains("interviews")) {
            reviewType = "Interview";
        }
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

    @Override
    public void readData(Document doc) {
        Elements reviewElements = doc.getElementsByClass("verticalPadding10");
        for (int i=0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);

            Elements titleElement = reviewElement.getElementsByTag("h2");
            ReviewData reviewData = new ReviewData();

            List<String> datas = reviewData.getDatas();
            datas.add("");
            datas.add("Google");    //Company Name
            datas.add("" + (i+1));  //Revew Number

            //Review type
            datas.add(reviewType);
            // Review title
            datas.add(titleElement.first().text());
            // Rating
            Element goldStarsLine = reviewElement.getElementsByClass("goldStars").first();
            datas.add(getRate(goldStarsLine));

            String nextP = reviewElement.getElementsByTag("p").get(1).text();
            // Current vs. former
            datas.add(nextP.substring(nextP.indexOf("|") + 1));
            // Review date
            datas.add(nextP.substring(0, nextP.indexOf("|")));
            // Uppers
            // Downers
            // Comments
            Elements sections = reviewElement.getElementsByClass("section");

            for (int j = 0; j < sections.size(); j++) {
                datas.add(sections.get(j).text());
            }

            myCrawlStat.addReviewData(reviewData);
        }

    }

    private static String getRate(Element goldStarsLine) {
        Set<String> rateKey = goldStarsLine.classNames();
        float rate = 0.0F;
        for (String key : rateKey) {
            if (rateKeyValues.containsKey(key)) {
                rate = rate + rateKeyValues.get(key);
            }

        }
        return "" + rate;
    }

    public static void main(String[] agrs) throws Exception {
        Document doc = Jsoup.parse("<div class=\"column column8 verticalPadding10\">\n" +
            "                    <h2 class=\"large normalWeight\">\n" +
            "                        Internship</h2>\n" +
            "                    <!-- <span id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_DisplayReviewItemsRepeater_UGCbrTag_1\"><br /></span> -->\n" +
            "\n" +
            "                    <p class=\"goldStarsLine\">\n" +
            "                        <span id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_DisplayReviewItemsRepeater_SetStarUI_1\" class=\"goldStars four\">\n" +
            "                            4.0\n" +
            "                            of 5 stars\n" +
            "                        </span>\n" +
            "                    </p>\n" +
            "                    <p>\n" +
            "                        <span class=\"text normalWeight\">\n" +
            "                            \n" +
            "                                    November 2015\n" +
            "                                |\n" +
            "                                    <span class=\"darkGrey bold\">FORMER EMPLOYEE</span>\n" +
            "                                \n" +
            "                            <span id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_DisplayReviewItemsRepeater_VerEmployeeDiv_1\" style=\"display:none;\"></span>\n" +
            "                    </span></p>\n" +
            "                            <h3 id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_DisplayReviewItemsRepeater_UGCStringComments_1_PopTitle_0\" class=\"medium\">Uppers</h3>\n" +
            "                            \n" +
            "                            \n" +
            "                            <p class=\"section\">\n" +
            "                                Smartest people I've ever met<br>Great culture and got to work on things that matter\n" +
            "                            </p>\n" +
            "                        \n" +
            "                            <h3 id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_DisplayReviewItemsRepeater_UGCStringComments_1_PopTitle_1\" class=\"medium\">Downers</h3>\n" +
            "                            \n" +
            "                            \n" +
            "                            <p class=\"section\">\n" +
            "                                Not a lot of interaction with senior executives.<br>Burocracy is a downer\n" +
            "                            </p>\n" +
            "                        \n" +
            "                            <h3 id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_DisplayReviewItemsRepeater_UGCStringComments_1_PopTitle_2\" class=\"medium\">Comments</h3>\n" +
            "                            \n" +
            "                            \n" +
            "                            <p class=\"section\">\n" +
            "                                If you like working with smart people, at one of the most important companies in the world, give it a shot\n" +
            "                            </p>\n" +
            "                        \n" +
            "                </div>");

        //System.out.println(doc.getElementsByClass("cmp-ratings-popup").first());
        Elements reviewElements = doc.getElementsByClass("verticalPadding10");
        Element reviewElement = reviewElements.first();
        Elements goldStarsLine = reviewElement.getElementsByClass("section");

        for (int i = 0; i < goldStarsLine.size(); i++) {
            System.out.println(goldStarsLine.get(i).text());
        }
    }
}
