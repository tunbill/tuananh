package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CareerBliss2DataCollectorCrawler extends MyCrawler {
    private static final Logger logger = LoggerFactory.getLogger(CareerBliss2DataCollectorCrawler.class);
    public static final String INDUSTRY = "Industry";
    public static final String HEAD_QUATER = "HQ";
    public static final String WEBSITE = "WS";

    public CareerBliss2DataCollectorCrawler() {
        super("https://www.careerbliss.com/", ImmutableList.of("/reviews/?page="), "Careerbliss");
    }

    @Override
    public boolean shouldReadData(String url) {
        url = url.toLowerCase();
        if (url.endsWith("/reviews/".toLowerCase())) {
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
        if (href.contains("pageType=ReviewsByCompanyName".toLowerCase())
            || href.endsWith("/reviews/")) {
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

            ReviewData reviewData = new ReviewData();
            List<String> datas = reviewData.getDatas();
            datas.add("");
            datas.add(comName);    //Company Name

            String allDatas = doc.getElementsByClass("display-name").first().getElementsByTag("div").get(3).text();
            Map<String, String> companyInfos = getCompanyInfos(allDatas);

            datas.add(companyInfos.get(INDUSTRY)); //Industry
            datas.add(companyInfos.get(HEAD_QUATER));
            datas.add(companyInfos.get(WEBSITE));
            myCrawlStat.addReviewData(reviewData);
        }
    }

    public static void main(String[] agr) throws Exception {
        Document doc = Jsoup.parse("\n" +
            "\t\t<div class=\"display-name\">\n" +
            "\t\t\t<div>\n" +
            "\t\t\t\t<strong class=\"matteblack name header10\">Procter &amp; Gamble</strong>\n" +
            "\t\t\t\t\t<a href=\"/pg/reviews/\">\n" +
            "\t\t\t\t\t\t<div class=\"rating \"><span class=\"full\"></span><span class=\"full\"></span><span class=\"full\"></span><span class=\"full\"></span><span class=\"empty\"></span></div>\n" +
            "\t\t\t\t\t\t<span class=\"review-count header11\">(76 reviews)</span>\n" +
            "\t\t\t\t\t</a>\n" +
            "\t\t\t\t\t\t\t\t\t<a class=\"btn-sm btn-small btn-tertiary review-link\" href=\"/review-your-employer/\" title=\"Share your company review with the CareerBliss community!\">Submit Review</a>\n" +
            "\t\t\t\t\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<div>\n" +
            "\t\t\t\t\tIndustry: Chemical Manufacturing\n" +
            "\t\t\t\t\t\t\t\t\t\t ·\n" +
            "HQ: Cincinnati, OH\t\t\t\t\t\t\t\t\t\t ·\n" +
            "https://www.pg.com\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\t\t<ul class=\"nav nav-tabs\">\n" +
            "\t\t\t<li class=\"tab tab-overview \"><a href=\"/pg/\"><span class=\"icon\">Overview</span></a></li>\n" +
            "\t\t\t<li class=\"tab tab-job \"><a href=\"/pg/jobs/\"><span class=\"icon\">Jobs</span></a></li>\n" +
            "\t\t\t<li class=\"tab tab-salary \"><a href=\"/pg/salaries/\"><span class=\"icon\">Salaries</span></a></li>\n" +
            "\t\t\t<li class=\"tab tab-review active\"><a href=\"/pg/reviews/\"><span class=\"icon\">Reviews</span></a></li>\n" +
            "\t\t\t<li class=\"tab tab-news \"><a href=\"/pg/news/\"><span class=\"icon\">News</span></a></li>\n" +
            "\t\t\t\n" +
            "\t\t</ul>\n" +
            "\t");

        String allDatas = doc.getElementsByClass("display-name").first().getElementsByTag("div").get(3).text();
            //System.out.println("allDatas: " + getCompanyInfos(allDatas));

        //System.out.println();
    }
    private Map<String, String> getCompanyInfos(String companyInfo) {
        Map<String, String> result = new HashMap<String, String>();
        String[] companyInfos = StringUtils.split(companyInfo, '·');
        for (int i = 0; i < companyInfos.length; i++) {
            String company = companyInfos[i].trim();
            if (company.startsWith(INDUSTRY)) {
                result.put(INDUSTRY, company.substring(9));
            } else if (company.startsWith(HEAD_QUATER)) {
                result.put(HEAD_QUATER, company.substring(3));
            } else {
                result.put(WEBSITE, company);
            }
        }

        return result;
    }
}
