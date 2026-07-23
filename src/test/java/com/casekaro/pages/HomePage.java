package com.casekaro.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the CaseKaro homepage and "Phone Cases by Model" page.
 * Handles top navigation, phone model search, and autocomplete suggestions.
 */
public class HomePage {

    private final Page page;

    // Selectors
    private static final String MOBILE_COVERS_NAV_LINK = "a[href*='phone-cases-by-model']:visible";
    private static final String MOBILE_COVERS_DRAWER_LINK = "#HeaderDrawer a[href*='phone-cases-by-model']";
    private static final String MODEL_SEARCH_INPUT = "#modelSearch";
    private static final String SUGGESTION_CONTAINER = "#searchResults";
    private static final String SUGGESTION_ITEMS = "#searchResults a";

    public HomePage(Page page) {
        this.page = page;
    }

    /**
     * Navigate to the CaseKaro website.
     */
    public void navigateTo(String url) {
        page.navigate(url);
        page.waitForLoadState();
    }

    /**
     * Click on "Mobile Covers" link in the top navigation menu.
     */
    public void clickMobileCovers() {
        Locator navByName = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Mobile Covers"));
        if (navByName.count() > 0) {
            navByName.first().click();
            page.waitForLoadState();
            return;
        }

        Locator visibleNavLink = page.locator(MOBILE_COVERS_NAV_LINK).first();
        if (visibleNavLink.count() > 0) {
            visibleNavLink.click();
            page.waitForLoadState();
            return;
        }

        // Fallback for mobile view where link is inside the header drawer.
        page.locator("summary.header__icon--menu").first().click();
        page.locator(MOBILE_COVERS_DRAWER_LINK).first().click();
        page.waitForLoadState();
    }

    /**
     * Scroll down to the phone model search section and type a search query.
     */
    public void searchPhoneModel(String model) {
        Locator searchInput = page.locator(MODEL_SEARCH_INPUT);
        searchInput.scrollIntoViewIfNeeded();
        searchInput.fill(model);
        // Wait for suggestions to appear
        page.waitForTimeout(1500);
    }

    /**
     * Clear the search box and type a new search query.
     */
    public void clearAndSearch(String model) {
        Locator searchInput = page.locator(MODEL_SEARCH_INPUT);
        searchInput.scrollIntoViewIfNeeded();
        searchInput.fill("");
        searchInput.fill(model);
        // Wait for suggestions to appear
        page.waitForTimeout(1500);
    }

    /**
     * Get all visible suggestion texts from the autocomplete dropdown.
     */
    public List<String> getSuggestionTexts() {
        page.locator(SUGGESTION_CONTAINER).waitFor();
        List<Locator> items = page.locator(SUGGESTION_ITEMS).all();
        List<String> texts = new ArrayList<>();
        for (Locator item : items) {
            if (item.isVisible()) {
                String text = item.textContent().trim();
                if (!text.isEmpty()) {
                    texts.add(text);
                }
            }
        }
        return texts;
    }

    /**
     * Check if any suggestion text contains the given brand name (case-insensitive).
     */
    public boolean hasSuggestionContaining(String text) {
        List<String> suggestions = getSuggestionTexts();
        return suggestions.stream()
                .anyMatch(s -> s.toLowerCase().contains(text.toLowerCase()));
    }

    /**
     * Click on a specific suggestion by exact text match.
     * Uses exact matching to avoid clicking "iPhone 16 Pro Max" when targeting "iPhone 16 Pro".
     */
    public void clickExactSuggestion(String exactText) {
        page.locator(SUGGESTION_CONTAINER).waitFor();
        Locator suggestions = page.locator(SUGGESTION_ITEMS);
        List<Locator> items = suggestions.all();

        for (Locator item : items) {
            String itemText = item.textContent().trim();
            if (itemText.equalsIgnoreCase(exactText)) {
                item.click();
                page.waitForLoadState();
                return;
            }
        }

        // If no exact match found, throw assertion error
        throw new AssertionError("Suggestion with exact text '" + exactText + "' not found in dropdown.");
    }

    /**
     * Get the current page URL.
     */
    public String getCurrentUrl() {
        return page.url();
    }
}
