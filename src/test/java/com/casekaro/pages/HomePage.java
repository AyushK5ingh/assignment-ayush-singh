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
            page.waitForURL("**/pages/phone-cases-by-model");
            return;
        }

        page.locator("#HeaderMenu-mobile-covers, #HeaderDrawer-mobile-covers").first().click();
        page.waitForURL("**/pages/phone-cases-by-model");
    }

    /**
     * Scroll down to the phone model search section and type a search query.
     */
    public void searchPhoneModel(String model) {
        Locator searchInput = page.locator(MODEL_SEARCH_INPUT);
        searchInput.scrollIntoViewIfNeeded();
        searchInput.click();
        searchInput.fill(model);
        page.locator(SUGGESTION_CONTAINER).waitFor();
        page.waitForTimeout(1000);
    }

    /**
     * Clear the search box and type a new search query.
     */
    public void clearAndSearch(String model) {
        page.locator(MODEL_SEARCH_INPUT).fill("");
        searchPhoneModel(model);
    }

    /**
     * Get all visible suggestion texts from the autocomplete dropdown.
     */
    public List<String> getSuggestionTexts() {
        List<Locator> items = page.locator(SUGGESTION_ITEMS).all();
        List<String> texts = new ArrayList<>();
        for (Locator item : items) {
            if (item.isVisible()) {
                String text = item.textContent();
                if (text != null && !text.trim().isEmpty()) {
                    texts.add(text);
                }
            }
        }
        return texts;
    }

    /**
     * Get the current text shown in the search results container.
     */
    public String getSearchResultsText() {
        String text = page.locator(SUGGESTION_CONTAINER).textContent();
        return text == null ? "" : text.trim();
    }

    /**
     * Check if any suggestion text contains the given brand name
     * (case-insensitive).
     */
    public boolean hasSuggestionContaining(String text) {
        List<String> suggestions = getSuggestionTexts();
        return suggestions.stream()
                .anyMatch(s -> s.toLowerCase().contains(text.toLowerCase()));
    }

    /**
     * Click on a specific suggestion by exact text match.
     * Uses exact matching to avoid clicking "iPhone 16 Pro Max" when targeting
     * "iPhone 16 Pro".
     */
    public void clickExactSuggestion(String exactText) {
        page.locator(SUGGESTION_CONTAINER).waitFor();
        Locator suggestions = page.locator(SUGGESTION_ITEMS);
        List<Locator> items = suggestions.all();

        for (Locator item : items) {
            String itemText = item.textContent();
            if (itemText != null && itemText.trim().equalsIgnoreCase(exactText)) {
                item.click();
                page.locator("button.quick-add__submit:has-text('Choose options')").first().waitFor();
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
