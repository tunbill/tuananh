package com.crawler;

import com.crawler.model.ReviewData;
import com.google.common.collect.ImmutableList;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndeedDataCollectorCrawler extends MyCrawler {
    private static final List<String> rankingKeys = ImmutableList.of("Job Work/Life Balance", "Compensation/Benefits", "Job Security/Advancement", "Management", "Job Culture");
    public IndeedDataCollectorCrawler() {
        super("http://www.indeed.com/", ImmutableList.of("/reviews?fcountry=ALL&start="));
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (!FILTERS.matcher(href).matches() && href.startsWith(prefixPage.toLowerCase()) && hrefChecking(href)) {
            //System.out.println("Should visit from Indeed: " + url.getURL());
            return true;
        }
        return false;
    }

    protected boolean hrefChecking(String href) {
        if (href.endsWith("/Best-Places-to-Work") ||
            href.contains("?i=all-industries&l=all-places&start=") ||
            (href.startsWith(prefixPage + "cmp/") && (2 == StringUtils.countMatches(href, "/"))) ||
            href.endsWith("/reviews") ||
            href.endsWith("?fcountry=ALL".toLowerCase()) ||
            href.contains("/reviews?fcountry=ALL&start=".toLowerCase())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldReadData(String url) {
        url = url.toLowerCase();
        if (url.contains("?fcountry=ALL".toLowerCase())) {
            return true;
        }
        return false;
    }

    @Override
    public void readData(Document doc) {
        String comName = doc.getElementById("cmp-name-and-rating").getElementsByTag("h2").first().text();
        System.out.println("Company: " + comName);
        Elements reviewElements = doc.getElementsByClass("cmp-review");
        for (int i=0; i < reviewElements.size(); i++) {
            Element reviewElement = reviewElements.get(i);

            Elements titleElement = reviewElement.getElementsByClass("cmp-review-title");
            Elements spanEles = titleElement.get(0).getElementsByTag("span");
            ReviewData reviewData = new ReviewData();
            List<String> datas = reviewData.getDatas();
            datas.add("");
            datas.add(comName);    //Company Name
//            datas.add("" + (i+1));  //Revew Number

            //Overall
            datas.add(reviewElement.getElementsByClass("cmp-rating-expandable").first().getElementsByClass("cmp-value-title").first().attr("title"));

            // Job work/ life balance
            // Compensation/ benefits
            // Job security/ advancement
            // Management
            // Job culture
            Map<String, String> rankingValues = getRankingValue(reviewElement.getElementsByClass("cmp-ratings-popup").first());
            for (String rankingKey : rankingKeys) {
                datas.add(rankingValues.get(rankingKey));
            }

            //Review title
            datas.add(titleElement.first().getElementsByAttributeValue("itemprop", "name").text());

            Element subTitleElement = reviewElement.getElementsByClass("cmp-review-subtitle").first();
            // Reviewer position
            datas.add(subTitleElement.getElementsByClass("cmp-reviewer").text());
            // Current vs. former
            String former = subTitleElement.getElementsByClass("cmp-reviewer-job-title").text();
            datas.add(former.substring(former.indexOf('(') + 1, former.indexOf(')')));

            // Reviewer location
            datas.add(subTitleElement.getElementsByClass("cmp-reviewer-job-location").text());
            // Review date
            datas.add(subTitleElement.getElementsByClass("cmp-review-date-created").text());

            Element reviewContentElement = reviewElement.getElementsByClass("cmp-review-content-container").first();
            // Review content
            datas.add(reviewContentElement.getElementsByClass("cmp-review-text").html());
            // Pros
            datas.add(reviewContentElement.getElementsByClass("cmp-review-pro-text").text());
            // Cons
            datas.add(reviewContentElement.getElementsByClass("cmp-review-con-text").text());

            myCrawlStat.addReviewData(reviewData);
        }

    }

    private static Map<String, String> getRankingValue(Element rankingElement) {
        Map<String, String> rankingValues = new HashMap<String, String>();
        String key = null;
        Elements trs = rankingElement.getElementsByTag("tr");
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).getElementsByTag("td");
            key = tds.get(1).text();
            rankingValues.put(key, calculateRate(tds.get(0).getElementsByClass("rating").attr("style")));
        }

        return rankingValues;
    }

    private static String calculateRate(String widthValue) {
        String width = widthValue.substring(6, widthValue.length()-2);
        float rate = Float.parseFloat(width);
        rate = (rate * 5)/86;
        return "" + rate;
    }

    public static void main(String[] agrs) throws Exception {
        Document doc = Jsoup.parse("<div class=\"cmp-review\" data-tn-entitytype=\"reviewId\" data-tn-entityid=\"a62243b3d80c4d49\"><div itemprop=\"itemReviewed\" itemscope=\"\" itemtype=\"http://schema.org/LocalBusiness\"><meta itemprop=\"name\" content=\"Google\"></div><div class=\"cmp-review-heading\"> <div class=\"cmp-ratings\"><span itemprop=\"reviewRating\" itemscope=\"\" itemtype=\"http://schema.org/Rating\"><meta itemprop=\"ratingValue\" content=\"5\"></span><div class=\"cmp-rating-expandable\"><span class=\"cmp-rating-outer\"><span class=\"cmp-rating-inner rating\" style=\"width:86.0px\"><span class=\"cmp-value-title\" title=\"5.0\"></span></span></span><button class=\"cmp-ratings-expand-button\"></button><div class=\"cmp-ratings-popup\"><table class=\"cmp-ratings-expanded\"><tbody><tr><td class=\"cmp-star-cell\"><span class=\"cmp-rating-outer\"><span class=\"cmp-rating-inner rating\" style=\"width:68.8px\"></span></span></td><td>Job Work/Life Balance</td></tr><tr><td class=\"cmp-star-cell\"><span class=\"cmp-rating-outer\"><span class=\"cmp-rating-inner rating\" style=\"width:86.0px\"></span></span></td><td>Compensation/Benefits</td></tr><tr><td class=\"cmp-star-cell\"><span class=\"cmp-rating-outer\"><span class=\"cmp-rating-inner rating\" style=\"width:86.0px\"></span></span></td><td>Job Security/Advancement</td></tr><tr><td class=\"cmp-star-cell\"><span class=\"cmp-rating-outer\"><span class=\"cmp-rating-inner rating\" style=\"width:68.8px\"></span></span></td><td>Management</td></tr><tr><td class=\"cmp-star-cell\"><span class=\"cmp-rating-outer\"><span class=\"cmp-rating-inner rating\" style=\"width:86.0px\"></span></span></td><td>Job Culture</td></tr></tbody></table></div></div></div><div class=\"cmp-review-title\"><span itemprop=\"name\">Missing Key Data</span><span itemprop=\"author\" itemscope=\"\" itemtype=\"http://schema.org/Person\"><meta itemprop=\"name\" content=\"Project Manager\"></span></div><div class=\"clear\"></div><div class=\"cmp-review-subtitle\"><span class=\"cmp-reviewer-job-title\"><span class=\"cmp-reviewer\">Project Manager</span>&nbsp;(Current Employee) – &nbsp;</span><span class=\"cmp-reviewer-job-location\">Palo Alto, CA</span> – <span class=\"cmp-review-date-created\">August 5, 2016</span></div></div><div class=\"cmp-review-content-container\"><div class=\"cmp-review-description\" data-tn-component=\"reviewDescription\"><span class=\"cmp-review-text\" itemprop=\"reviewBody\">We are usually always ahead of the time when it comes to consumer data, but commerce we only have access to 10% which is online, the other 90% is in store from the point of sale. Other companies like IBM are taking an aggressive approach to retrieving this data and we need to stay up to speed. Its good to hear we are in talks with point of sale companies. With access to the other 90% of missing data we can finally close the loop and 9x our revenue in advertising.</span></div><div class=\"cmp-review-pros-cons-content\"><div class=\"cmp-review-pros\"><div class=\"cmp-bold\">Pros</div><div class=\"cmp-review-pro-text\">Flexible hours, great benefits, paid vacation days, etc.</div></div><div class=\"cmp-review-cons\"><div class=\"cmp-bold\">Cons</div><div class=\"cmp-review-con-text\">I think often times we miss the big picture here, we need to interact more with the customer and we need more sales minded people.</div></div><div class=\"clear\"></div></div></div><div class=\"cmp-review-feedback clearfix\" data-reviewid=\"1056530399\"><div class=\"cmp-review-vote-report \"><span class=\"cmp-feedback-question\">Was this review helpful?</span><span class=\"cmp-review-vote\"><a href=\"javascript:void(0);\" name=\"yesFeedbackReview\" data-reviewid=\"1056530399\" rel=\"nofollow\" class=\"cmp-review-feedback-link cmp-simple-button\"><span class=\"cmp-vote-text\" data-reviewid=\"1056530399\"><span id=\"yes_feedback\" class=\"cmp-feedback-link cmp-feedback-upvote\">Yes</span><span class=\"cmp-vote-no-decoration \">-&nbsp;</span><span name=\"upVoteCount\" class=\"cmp-vote-count\" data-reviewid=\"1056530399\">2</span></span></a><a href=\"javascript:void(0);\" name=\"noFeedbackReview\" data-reviewid=\"1056530399\" rel=\"nofollow\" class=\"cmp-review-feedback-link cmp-simple-button\"><span class=\"cmp-vote-text\" data-reviewid=\"1056530399\"><span id=\"no_feedback\" class=\"cmp-feedback-link cmp-feedback-downvote\">No</span><span class=\"cmp-vote-no-decoration hidden\">-&nbsp;</span><span name=\"downVoteCount\" class=\"cmp-vote-count\" data-reviewid=\"1056530399\"></span></span></a></span><span class=\"cmp-review-flag\" data-reviewid=\"a62243b3d80c4d49\">Report</span></div></div></div>");

        //System.out.println(doc.getElementsByClass("cmp-ratings-popup").first());
        Element subTitleElement = doc.getElementsByClass("cmp-review-subtitle").first();
        // Current vs. former
        String former = subTitleElement.getElementsByClass("cmp-reviewer-job-title").text();
        System.out.println(former.substring(former.indexOf('(') + 1, former.indexOf(')')));
       // System.out.println(getRankingValue(doc.getElementsByClass("cmp-ratings-popup").first()));
    }
}
