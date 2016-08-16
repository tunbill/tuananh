package com.crawler.controller;

import com.crawler.CareerBlissDataCollectorCrawler;
import com.crawler.IndeedDataCollectorCrawler;
import com.crawler.VaultDataCollectorCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VaultDataCollectorController extends BaseController<VaultDataCollectorCrawler> {
    private static final Logger logger = LoggerFactory.getLogger(VaultDataCollectorController.class);

    public static void main(String[] args) throws Exception {
        saveData(VaultDataCollectorCrawler.class,
            "http://www.vault.com/company-profiles/internet-social-media/google-inc/employee-reviews",
            "/home/quynh/tmp/vault.xlsx");
    }
}
