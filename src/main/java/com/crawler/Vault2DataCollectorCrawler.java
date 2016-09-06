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

public class Vault2DataCollectorCrawler extends MyCrawler {
    private static final Map<String, Float> rateKeyValues =  ImmutableMap.of("one", 1F, "two", 2F, "three", 3F, "four", 4F, "five", 5F);

    public Vault2DataCollectorCrawler() {
        //super("http://www.vault.com/company-profiles/", ImmutableList.of("/employee-reviews?rt="));
        super("http://www.vault.com/", ImmutableList.of("/internet-social-media/"), "VAULT 2");
    }
    @Override
    public boolean shouldReadData(String url) {
        url = url.toLowerCase();
        if (url.startsWith("http://www.vault.com/company-profiles/")) {
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
            || (href.contains("/company-profiles/") && href.endsWith(".aspx") && !href.endsWith("submit-employee-review.aspx"))) {
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
            System.out.println("Dumping data: " + myCrawlStat.getTotalProcessedPages());
            dumpMyData();
        }
    }

    @Override
    public void readData(Document doc) {
        String title = doc.title();
        if (title.endsWith("|Company Profile|Vault.com")) {
            String comName = title.substring(0, title.indexOf('|'));

            Element dataElements = doc.getElementById("ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightDivInfo");

            ReviewData reviewData = new ReviewData();
            List<String> datas = reviewData.getDatas();
            datas.add("");
            datas.add(comName);    //Company Name
            datas.add(doc.getElementsByClass("companyInfoCol").first().text()); //com address
            datas.add(doc.getElementsByClass("statsList").first().text()); //Stat list
            datas.add(""); //Stock Symbol _ don't know how to get it

            Element locationElement = doc.getElementById("MajorLocationsDiv");
            if (null != locationElement) {
                datas.add(locationElement.getElementsByTag("ul").first().text()); //Major Office Locations
            } else {
                datas.add("");
            }

            Element otherElement = doc.getElementById("ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_OtherLocationDiv");
            if (null != otherElement) {
                datas.add(otherElement.getElementsByTag("ul").first().text());//Other Locations
            } else {
                datas.add("");
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
        Document doc = Jsoup.parse("<div id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightDivInfo\" class=\"column column3\"> \n" +
            "\t\t\t\n" +
            "    <!-- Display Company Logo -->\n" +
            "    <div class=\"companyLogo\">\n" +
            "        <img id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_CompanyLogoImage\" title=\"Alfred Nickles Bakery Incorporated Company Profile\" src=\"\" alt=\"Alfred Nickles Bakery Incorporated Company Profile\" style=\"display:none;\">\n" +
            "    </div>\n" +
            "    <!--Display the address-->\n" +
            "    <div class=\"companyInfoCol notranslate\">\n" +
            "        <h3 class=\"text\">\n" +
            "            <span id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_CompanyName\">Alfred Nickles Bakery, Inc.</span></h3>\n" +
            "        <p>\n" +
            "            \n" +
            "                    26 Main St N\n" +
            "                \n" +
            "                    <br>\n" +
            "                \n" +
            "                    Navarre, OH 44662-1158\n" +
            "                \n" +
            "                    <br>\n" +
            "                \n" +
            "                    Phone: 1 (330) 879-5635\n" +
            "                \n" +
            "                    <br>\n" +
            "                \n" +
            "                    Fax: 1 (330) 879-5896\n" +
            "                \n" +
            "            <span id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_LineBreakBeforeLink\">\n" +
            "                <br>\n" +
            "            </span>\n" +
            "            \n" +
            "        </p>\n" +
            "    </div>\n" +
            "\n" +
            "    <!--Display the Social Media Links-->\n" +
            "    \n" +
            "\n" +
            "    <!--Display the stats-->\n" +
            "    <h3 class=\"text translate\">\n" +
            "        <span id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_StatsHeader\" class=\"section\">Stats</span></h3>\n" +
            "    <ul class=\"bulletless text statsList\">\n" +
            "        \n" +
            "                <li>\n" +
            "                    Employer Type: Private\n" +
            "                </li>\n" +
            "            \n" +
            "                <li>\n" +
            "                    President: David A. Gardner\n" +
            "                </li>\n" +
            "            \n" +
            "                <li>\n" +
            "                    Chief Finance Officer Vice President Finance And Treas: Mark Sponseller\n" +
            "                </li>\n" +
            "            \n" +
            "                <li>\n" +
            "                    Vice President: Wesley Webber\n" +
            "                </li>\n" +
            "            \n" +
            "    </ul>\n" +
            "\n" +
            "    <!--Display the office locations-->\n" +
            "    <div id=\"MajorLocationsDiv\" class=\"profileSection section translate\">\n" +
            "        <h3 class=\"text\">\n" +
            "            <span id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_MajorLocationHeader\">Major Office Locations</span></h3>\n" +
            "        <ul class=\"bulletless noBottomMargin text\">\n" +
            "            \n" +
            "                    <li style=\"display: list-item;\">\n" +
            "                        Navarre, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "        </ul>\n" +
            "        <a href=\"javascript:void(0)\" id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_ShowMajorLocations\" class=\"showFull\" style=\"display:none;\">\n" +
            "            <span style=\"display: none;\" class=\"showHide\">- Show Less</span>\n" +
            "            <span class=\"showTopic\">+ Show More</span></a>\n" +
            "    </div>\n" +
            "\n" +
            "    <!--Display the \"Other Locations\"-->\n" +
            "    <div id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_OtherLocationDiv\" class=\"profileSection section translate\">\n" +
            "        <h3 class=\"text\"><span id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_OtherLocationHeader\">Other Locations</span></h3>\n" +
            "        <ul class=\"bulletless noBottomMargin text\">\n" +
            "            \n" +
            "                    <li style=\"display: list-item;\">\n" +
            "                        Akron, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: list-item;\">\n" +
            "                        Cambridge, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: list-item;\">\n" +
            "                        Cincinnati, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: list-item;\">\n" +
            "                        Cleveland, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: list-item;\">\n" +
            "                        Columbus, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Dayton, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Fremont, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Girard, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Lima, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Lorain, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Mansfield, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Marion, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Martins Ferry, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Mogadore, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Napoleon, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Orwell, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Toledo, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Washington Court Hou, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Zanesville, OH\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Du Bois, PA\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Ebensburg, PA\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Monroeville, PA\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        New Castle, PA\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Washington, PA\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Fairmont, WV\n" +
            "                    </li>\n" +
            "                \n" +
            "                    <li style=\"display: none;\">\n" +
            "                        Parkersburg, WV\n" +
            "                    </li>\n" +
            "                \n" +
            "        </ul>\n" +
            "        <a href=\"javascript:void(0)\" id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_ShowOtherLocation\" class=\"showFull\">\n" +
            "            <span style=\"display: none;\" class=\"showHide\">- Show Less</span>\n" +
            "            <span style=\"display: inline;\" class=\"showTopic\">+ Show More</span></a>\n" +
            "    </div>\n" +
            "\n" +
            "    <!--Display the Key Finanacial information-->\n" +
            "    <div id=\"ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_KeyFinancialDiv\" class=\"profileSection section translate\" style=\"display:none;\">\n" +
            "        <h3 class=\"text dashedLine\">Key Financials</h3>\n" +
            "        <ul class=\"bulletless text\">\n" +
            "            \n" +
            "        </ul>\n" +
            "    </div>\n" +
            "\n" +
            "\n" +
            "\t\t</div>");

        //System.out.println(doc.getElementsByClass("cmp-ratings-popup").first());
//        Elements reviewElements = doc.getElementsByClass("verticalPadding10");
//        Element reviewElement = reviewElements.first();
//        Elements goldStarsLine = reviewElement.getElementsByClass("section");
//
//        for (int i = 0; i < goldStarsLine.size(); i++) {
//            System.out.println(goldStarsLine.get(i).text());
//        }

        String reviewType = doc.getElementById("ContentPlaceHolderDefault_SectionContent_EntityTabInfo_RightInfo_OtherLocationDiv").getElementsByTag("ul").first().text();
        System.out.println(reviewType);
    }
}
