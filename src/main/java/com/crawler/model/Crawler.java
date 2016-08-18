package com.crawler.model;

import com.crawler.MyCrawler;

public class Crawler {
    private final String baseAddress;
    private final Class type;
    private final String sheetName;

    public Crawler(String baseAddress, Class type, String sheetName) {
        this.baseAddress = baseAddress;
        this.type = type;
        this.sheetName = sheetName;
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

}
