package com.casekaro.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

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

    // Selectors for the cart drawer
    private static final String CART_DRAWER = "#CartDrawer";
    private static final String CART_ITEMS = ".cart-item";
    private static final String CART_ITEM_NAME = "a[href*='/products/']";
    private static final String CART_ITEM_VARIANT = ".product-option";
    private static final String CART_ITEM_PRICE = ".price";
    private static final String CART_CLOSE_BUTTON = "#CartDrawer button[aria-label='Close']";
    private static final String CART_ICON = "a[href='/cart']";

    public CartPage(Page page) {
        this.page = page;
    }

    /**
     * Get the number of items currently in the cart drawer.
     */
    public int getCartItemCount() {
        page.waitForTimeout(1000);
        // Try to find cart items in the drawer
        List<Locator> items = page.locator(CART_ITEMS).all();
        int visibleCount = 0;
        for (Locator item : items) {
            if (item.isVisible()) {
                visibleCount++;
            }
        }
        return visibleCount;
    }

    /**
     * Get details of all cart items: material, price, and product link.
     * Returns a list of maps with keys: "material", "price", "link".
     */
    public List<Map<String, String>> getCartItemDetails() {
        page.waitForTimeout(1000);
        List<Map<String, String>> itemDetails = new ArrayList<>();
        List<Locator> items = page.locator(CART_ITEMS).all();

        for (Locator item : items) {
            if (!item.isVisible()) continue;

            Map<String, String> details = new LinkedHashMap<>();

            // Get material/variant info
            Locator variantLocator = item.locator(CART_ITEM_VARIANT);
            String material = "N/A";
            if (variantLocator.count() > 0 && variantLocator.first().isVisible()) {
                material = variantLocator.first().textContent().trim();
            }

            // Get price
            Locator priceLocator = item.locator(CART_ITEM_PRICE);
            String price = "N/A";
            if (priceLocator.count() > 0 && priceLocator.first().isVisible()) {
                price = priceLocator.first().textContent().trim();
            }

            // Get product link
            Locator linkLocator = item.locator(CART_ITEM_NAME);
            String link = "N/A";
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
        // Try the close button first
        Locator closeBtn = page.locator(CART_CLOSE_BUTTON);
        if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
            closeBtn.first().click();
        } else {
            // Fallback: press Escape
            page.keyboard().press("Escape");
        }
        page.waitForTimeout(1000);
    }
}
