package com.casekaro.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Page Object for the Cart Drawer.
 * Handles cart validation, item counting, and printing cart details to console.
 */
public class CartPage {

    private final Page page;

    private static final String CART_DRAWER = "#CartDrawer";
    private static final String CART_ITEMS = ".cart-item";
    private static final String CART_ITEM_NAME = ".cart-item__name";
    private static final String CART_ITEM_VARIANT = ".product-option dd";
    private static final String CART_ITEM_PRICE = ".cart-item__price-wrapper .price";
    private static final String CART_CLOSE_BUTTON = "#CartDrawer button[aria-label='Close']";

    public CartPage(Page page) {
        this.page = page;
    }

    /**
     * Get the number of items currently in the cart drawer.
     */
    public int getCartItemCount() {
        List<Locator> items = page.locator(CART_ITEMS).all();
        int visibleCount = 0;
        for (Locator item : items) {
            if (item.isVisible()) {
                visibleCount++;
            }
        }
        return visibleCount > 0 ? visibleCount : items.size();
    }

    /**
     * Get details of all cart items: material, price, and product link.
     * Returns a list of maps with keys: "material", "price", "link".
     */
    public List<Map<String, String>> getCartItemDetails() {
        List<Map<String, String>> itemDetails = new ArrayList<>();
        List<Locator> items = page.locator(CART_ITEMS).all();

        for (Locator item : items) {
            if (!item.isVisible())
                continue;

            Map<String, String> details = new LinkedHashMap<>();

            Locator variantLocator = item.locator(CART_ITEM_VARIANT);
            String material = "";
            if (variantLocator.count() > 0 && variantLocator.first().isVisible()) {
                String variantText = variantLocator.first().textContent();
                material = variantText == null ? "" : variantText.trim();
            }

            Locator priceLocator = item.locator(CART_ITEM_PRICE);
            String price = "";
            if (priceLocator.count() > 0 && priceLocator.first().isVisible()) {
                String priceText = priceLocator.first().textContent();
                price = normalizePriceText(priceText);
            }

            Locator linkLocator = item.locator(CART_ITEM_NAME);
            String link = "";
            if (linkLocator.count() > 0) {
                String href = linkLocator.first().getAttribute("href");
                if (href != null) {
                    link = href.startsWith("http") ? href : "https://casekaro.com" + href;
                }
            }

            details.put("material", material);
            details.put("price", price);
            details.put("link", link);

            itemDetails.add(details);
        }

        return itemDetails;
    }

    /**
     * Print all cart item details to the console.
     * Format: Material, Price, Link for each item.
     */
    public void printCartDetails() {
        List<Map<String, String>> items = getCartItemDetails();
        System.out.println("\n===================================");
        System.out.println("         CART ITEM DETAILS         ");
        System.out.println("===================================");

        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = items.get(i);
            System.out.println("\nItem " + (i + 1) + ":");
            System.out.println("  Material : " + item.get("material"));
            System.out.println("  Price    : " + item.get("price"));
            System.out.println("  Link     : " + item.get("link"));
        }

        System.out.println("\n===================================\n");
    }

    /**
     * Close the cart drawer by clicking the close button.
     */
    public void closeCartDrawer() {
        Locator closeBtn = page.locator(CART_CLOSE_BUTTON);
        if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
            closeBtn.first().click();
        }
    }

    /**
     * Open the cart drawer from the cart icon.
     */
    public void openCartDrawer() {
        if (!page.locator(CART_DRAWER).first().isVisible()) {
            Locator cartToggle = page.locator(
                    "a[href*='cart' i], button[aria-label*='cart' i], [aria-controls='CartDrawer'], [data-cart-toggle], [class*='cart']");
            for (Locator candidate : cartToggle.all()) {
                if (candidate.isVisible()) {
                    candidate.click();
                    break;
                }
            }
        }

        page.waitForTimeout(1000);
        page.locator(CART_ITEMS).first().waitFor(new Locator.WaitForOptions()
                .setTimeout(10000)
                .setState(WaitForSelectorState.ATTACHED));
    }

    private String normalizePriceText(String priceText) {
        if (priceText == null) {
            return "";
        }

        String trimmed = priceText.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        return trimmed.replaceFirst("^[^0-9]+", "Rs. ");
    }
}
