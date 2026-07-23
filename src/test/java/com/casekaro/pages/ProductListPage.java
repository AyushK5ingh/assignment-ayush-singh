package com.casekaro.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Product Listing/Collection page and quick-add modal.
 * Handles product card interactions, material variant selection, and add-to-cart.
 */
public class ProductListPage {

    private final Page page;

    private static final String QUICK_ADD_MODAL = "[id^='QuickAddInfo-']";
    private static final String CHOOSE_OPTIONS_BUTTON = "button.quick-add__submit:has-text('Choose options')";
    private static final String VARIANT_LABEL = QUICK_ADD_MODAL + " fieldset label";
    private static final String ADD_TO_CART_BUTTON = QUICK_ADD_MODAL + " button.product-form__submit";

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
        visibleQuickAddModal().waitFor();
    }

    /**
     * Get all available material variant names from the quick-add modal.
     */
    public List<String> getMaterialVariants() {
        Locator modal = visibleQuickAddModal();
        List<Locator> labels = modal.locator("fieldset label").all();
        List<String> variants = new ArrayList<>();
        for (Locator label : labels) {
            if (label.isVisible()) {
                String text = normalizeVariantLabel(label.textContent());
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
        Locator modal = visibleQuickAddModal();
        List<Locator> allLabels = modal.locator("fieldset label").all();

        for (Locator label : allLabels) {
            if (label.isVisible() && normalizeVariantLabel(label.textContent()).equalsIgnoreCase(material)) {
                label.click();
                return;
            }
        }
        throw new AssertionError("Material variant '" + material + "' not found in the quick-add modal.");
    }

    /**
     * Click the "Add to cart" button in the quick-add modal.
     */
    public void clickAddToCart() {
        Locator addBtn = visibleQuickAddModal().locator("button.product-form__submit");
        addBtn.scrollIntoViewIfNeeded();
        addBtn.click();
        page.locator("#CartDrawer").waitFor();
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
        return page.locator(".card-information").first().textContent().trim();
    }

    private String normalizeVariantLabel(String rawText) {
        if (rawText == null) {
            return "";
        }

        String[] lines = rawText.trim().split("\\R");
        return lines.length == 0 ? "" : lines[0].trim();
    }

    private Locator visibleQuickAddModal() {
        return page.locator(QUICK_ADD_MODAL + ":visible").first();
    }
}
