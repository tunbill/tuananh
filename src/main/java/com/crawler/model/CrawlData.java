package com.crawler.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawlData {
    private long totalLinks;
    private int totalProcessedPages;
    private List<ReviewData> reviewDatas = new ArrayList<ReviewData>();

    public long getTotalLinks() {
        return totalLinks;
    }

    public void setTotalLinks(long totalLinks) {
        this.totalLinks = totalLinks;
    }

    public void incTotalLinks(int count) {
        this.totalLinks += count;
    }

    public int getTotalProcessedPages() {
        return totalProcessedPages;
    }

    public void setTotalProcessedPages(int totalProcessedPages) {
        this.totalProcessedPages = totalProcessedPages;
    }

    public void incProcessedPages() {
        this.totalProcessedPages++;
    }

    public void addReviewData(ReviewData reviewData) {
        reviewDatas.add(reviewData);
    }

    public List<ReviewData> getReviewDatas() {
        return reviewDatas;
    }

    public void setReviewDatas(List<ReviewData> reviewDatas) {
        this.reviewDatas = reviewDatas;
    }

}
