package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
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

    public VaultDataCollectorCrawler() {
        //super("http://www.vault.com/company-profiles/", ImmutableList.of("/employee-reviews?rt="));
        super("http://www.vault.com/", ImmutableList.of("/internet-social-media/"), "Vault");
    }
    @Override
    public boolean shouldReadData(String url) {
        url = url.toLowerCase();
        if (url.startsWith("http://www.vault.com/company-profiles/") && hrefCheckingDrawData(url)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (!FILTERS.matcher(href).matches() && href.startsWith(prefixPage.toLowerCase()) && hrefChecking(href)) {
            System.out.println("Should visit from vault: " + url.getURL());
            return true;
        }
        return false;
    }

    protected boolean hrefChecking(String href) {
        if (href.contains("search-results/CompanyResultsPage".toLowerCase())
            || (href.contains("/company-profiles/") && href.endsWith(".aspx"))
            || hrefCheckingDrawData(href)) {
            return true;
        }
        return false;
    }

    protected boolean hrefCheckingDrawData(String href) {
        if ((href.contains("rt=salaries") && !href.contains("&str="))
            || (href.contains("rt=interviews") && !href.contains("&str="))
            || (href.endsWith("/employee-reviews"))
            || (href.contains("/employee-reviews?pg="))) {
            return true;
        }
        return false;
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("Visited: {}", url);

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
        String title = doc.title();
        if (title.endsWith("|Employee Reviews|Vault.com") && !title.startsWith("Submit a Review")) {
            String reviewType = doc.getElementsByClass("reviewsNav").first().getElementsByAttributeValue("class", "active").first().ownText();
            String comName = title.substring(0, title.indexOf('|'));
            System.out.println(comName + " || " + doc.location());
            Elements reviewElements = doc.getElementsByClass("verticalPadding10");
            for (int i=0; i < reviewElements.size(); i++) {
                Element reviewElement = reviewElements.get(i);

                Elements titleElement = reviewElement.getElementsByTag("h2");
                ReviewData reviewData = new ReviewData();

                List<String> datas = reviewData.getDatas();
                datas.add("");
                datas.add(comName);    //Company Name
                datas.add("");
                //datas.add("" + (i+1));  //Revew Number

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
        Document doc = Jsoup.parse("<div class=\"column column8\">\n" +
            "                <ul class=\"reviewsNav\">\n" +
            "                    <li>\n" +
            "                        <h2 class=\"medium\">Employee Reviews</h2>\n" +
            "                        <h3 class=\"text\"><a id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_SubmitAnonEmployeeRev\" href=\"/company-profiles/employee-reviews/submit-employee-review.aspx\"><i class=\"fa fa-pencil-square-o\"></i> Submit a Review</a></h3>\n" +
            "                    </li>\n" +
            "\n" +
            "                    \n" +
            "                    <li id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_CompanySubset_LI\" class=\"active\">\n" +
            "                        <span>410</span><br>\n" +
            "                        Company\n" +
            "                    </li>\n" +
            "\n" +
            "                    <li id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_InterviewSubset_LILink\">\n" +
            "                        <a id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_InterviewSubsetLink\" href=\"/company-profiles/tech-consulting/cognizant/employee-reviews?rt=interviews\">\n" +
            "                            <span>177</span><br>\n" +
            "                            Interviews\n" +
            "                        </a>\n" +
            "                    </li>\n" +
            "                    \n" +
            "\n" +
            "                    <li id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_SalariesSubset_LILink\">\n" +
            "                        <a id=\"ContentPlaceHolderDefault_SectionContent_SecondRowContent_SalariesSubsetLink\" href=\"/company-profiles/tech-consulting/cognizant/employee-reviews?rt=salaries\">\n" +
            "                            <span>322</span><br>\n" +
            "                            Salaries\n" +
            "                        </a>\n" +
            "                    </li>\n" +
            "                    \n" +
            "                    \n" +
            "                </ul>\n" +
            "            </div>");

        //System.out.println(doc.getElementsByClass("cmp-ratings-popup").first());
//        Elements reviewElements = doc.getElementsByClass("verticalPadding10");
//        Element reviewElement = reviewElements.first();
//        Elements goldStarsLine = reviewElement.getElementsByClass("section");
//
//        for (int i = 0; i < goldStarsLine.size(); i++) {
//            System.out.println(goldStarsLine.get(i).text());
//        }

        String reviewType = doc.getElementsByClass("reviewsNav").first().getElementsByAttributeValue("class", "active").first().ownText();
        System.out.println(reviewType);
    }
}
