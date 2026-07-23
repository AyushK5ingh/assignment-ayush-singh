package com.casekaro.hooks;

import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

public class PlaywrightBootstrapTest {

    @Test
    public void downloadBrowsers() {
        Playwright playwright = Playwright.create();
        System.out.println("Playwright instance created - browsers should be installed if missing.");
        playwright.close();
    }
}
