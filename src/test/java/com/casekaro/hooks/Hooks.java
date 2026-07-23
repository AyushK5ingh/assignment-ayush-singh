package com.casekaro.hooks;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Cucumber Hooks for Playwright browser lifecycle management.
 * Sets up and tears down the browser before/after each scenario.
 */
public class Hooks {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    @Before
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500));
        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1440, 900));
        page = context.newPage();
        String devMode = System.getProperty("playwright.dev", System.getenv("PLAYWRIGHT_DEV"));
        if (devMode != null && devMode.equalsIgnoreCase("true")) {
            System.out.println("Playwright dev-mode enabled - pausing to open Inspector...");
            page.pause();
        }
    }

    @After
    public void tearDown() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Provides the Playwright Page instance to step definitions.
     */
    public static Page getPage() {
        return page;
    }
}
