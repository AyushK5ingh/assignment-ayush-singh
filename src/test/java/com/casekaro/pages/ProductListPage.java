package com.casekaro.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Product Listing/Collection page and quick-add modal.
 * Handles product card interactions, material variant selection, and add-to-cart.
 */
public class ProductListPage {

    private final Page page;

    // Selectors for product grid
    private static final String CHOOSE_OPTIONS_BUTTON = "button:has-text('Choose options')";
    private static final String PRODUCT_CARD_FIRST = ".card-information";

    // Selectors for the quick-add modal
    private static final String QUICK_ADD_MODAL = "[id^='QuickAddInfo-']";
    private static final String MATERIAL_VARIANT_FIELDSET = "fieldset";
    private static final String VARIANT_LABEL = "fieldset label";
    private static final String ADD_TO_CART_BUTTON = "button[type='submit']:has-text('Add to cart')";
    private static final String PRODUCT_PRICE = ".price-item--sale, .price-item--regular";

    public ProductListPage(Page page) {
        this.page = page;
    }

    /**
     * Click "Choose Options" on the first product card.
     */
    public void clickChooseOptionsOnFirstProduct() {
        page.waitForLoadState();
        Locator chooseBtn = page.locator(CHOOSE_OPTIONS_BUTTON).first();
        chooseBtn.scrollIntoViewIfNeeded();
        chooseBtn.click();
        // Wait for the quick-add modal to appear
        page.waitForTimeout(2000);
    }

    /**
     * Get all available material variant names from the quick-add modal.
     */
    public List<String> getMaterialVariants() {
        page.waitForTimeout(1000);
        List<Locator> labels = page.locator(VARIANT_LABEL).all();
        List<String> variants = new ArrayList<>();
        for (Locator label : labels) {
            if (label.isVisible()) {
                String text = label.textContent().trim();
                if (!text.isEmpty()) {
                    variants.add(text);
                }
            }
        }
        return variants;
    }

    /**
     * Select a specific material variant by clicking its label.
     */
    public void selectMaterial(String material) {
        Locator labels = page.locator(VARIANT_LABEL);
        List<Locator> allLabels = labels.all();

        for (Locator label : allLabels) {
            if (label.isVisible() && label.textContent().trim().equalsIgnoreCase(material)) {
                label.click();
                page.waitForTimeout(500);
                return;
            }
        }
        throw new AssertionError("Material variant '" + material + "' not found in the quick-add modal.");
    }

    /**
     * Click the "Add to cart" button in the quick-add modal.
     */
    public void clickAddToCart() {
        Locator addBtn = page.locator(ADD_TO_CART_BUTTON).last();
        addBtn.scrollIntoViewIfNeeded();
        addBtn.click();
        // Wait for cart drawer to open
        page.waitForTimeout(2000);
    }

    /**
     * Select a material variant and add to cart in one step.
     */
    public void selectMaterialAndAddToCart(String material) {
        selectMaterial(material);
        clickAddToCart();
    }

    /**
     * Get the first product name from the listing page.
     */
    public String getFirstProductName() {
        return page.locator(PRODUCT_CARD_FIRST).first().textContent().trim();
    }
}
