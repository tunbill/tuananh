package com.crawler.model;

import com.crawler.MyCrawler;

public class Crawler {
    private final String baseAddress;
    private final Class type;
    private final String sheetName;
    private final int timeout;
    private final boolean hasIndex;

    public Crawler(String baseAddress, Class type, String sheetName, int timeout, boolean hasIndex) {
        this.baseAddress = baseAddress;
        this.type = type;
        this.sheetName = sheetName;
        this.timeout = timeout;
        this.hasIndex = hasIndex;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public Class getType() {
        return type;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isHasIndex() {
        return hasIndex;
    }
}
