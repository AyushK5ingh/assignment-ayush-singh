package com.casekaro.stepdefs;

import com.casekaro.hooks.Hooks;
import com.casekaro.pages.CartPage;
import com.casekaro.pages.HomePage;
import com.casekaro.pages.ProductListPage;
import com.microsoft.playwright.Page;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for the CaseKaro Mobile Cover Purchase Flow.
 * Maps Gherkin steps to Playwright actions using Page Object Model.
 * No try-catch blocks per assignment requirements.
 */
public class CaseKaroSteps {

    private Page page;
    private HomePage homePage;
    private ProductListPage productListPage;
    private CartPage cartPage;

    private List<String> currentSuggestions;
    private String currentSearchResultsText;

    private void initPages() {
        page = Hooks.getPage();
        homePage = new HomePage(page);
        productListPage = new ProductListPage(page);
        cartPage = new CartPage(page);
    }

    // ==================== GIVEN ====================

    @Given("I navigate to the CaseKaro website")
    public void iNavigateToTheCaseKaroWebsite() {
        initPages();
        homePage.navigateTo("https://casekaro.com/");
        assertNotNull(page, "Page should be initialized");
        assertTrue(page.url().contains("casekaro.com"), "Should be on CaseKaro website");
    }

    // ==================== WHEN ====================

    @When("I click on {string} from the top navigation menu")
    public void iClickOnFromTheTopNavigationMenu(String menuItem) {
        homePage.clickMobileCovers();
    }

    @When("I search for {string} in the phone model search box")
    public void iSearchForInThePhoneModelSearchBox(String searchTerm) {
        homePage.searchPhoneModel(searchTerm);
        currentSuggestions = homePage.getSuggestionTexts();
        currentSearchResultsText = homePage.getSearchResultsText();
    }

    @When("I clear the search box and search for {string}")
    public void iClearTheSearchBoxAndSearchFor(String searchTerm) {
        homePage.clearAndSearch(searchTerm);
        currentSuggestions = homePage.getSuggestionTexts();
        currentSearchResultsText = homePage.getSearchResultsText();
    }

    @When("I click on exactly {string} from the suggestions")
    public void iClickOnExactlyFromTheSuggestions(String modelName) {
        homePage.clickExactSuggestion(modelName);
    }

    @When("I click {string} on the first product card")
    public void iClickOnTheFirstProductCard(String buttonText) {
        productListPage.clickChooseOptionsOnFirstProduct();
    }

    @When("I select {string} material and add to cart")
    public void iSelectMaterialAndAddToCart(String material) {
        productListPage.selectMaterialAndAddToCart(material);
    }

    @When("I close the cart drawer")
    public void iCloseTheCartDrawer() {
        cartPage.closeCartDrawer();
    }

    @When("I open the cart")
    public void iOpenTheCart() {
        cartPage.openCartDrawer();
    }

    // ==================== THEN ====================

    @Then("I should be on the phone cases by model page")
    public void iShouldBeOnThePhoneCasesByModelPage() {
        page.waitForLoadState();
        String currentUrl = page.url();
        assertTrue(currentUrl.contains("phone-cases-by-model"),
                "URL should contain 'phone-cases-by-model'. Actual URL: " + currentUrl);
    }

        @Then("I should see a no results message for the search")
        public void iShouldSeeANoResultsMessageForTheSearch() {
        assertTrue(currentSearchResultsText.toLowerCase().contains("no models found"),
            "Search results should show a no models found message. Actual text: " + currentSearchResultsText);
        assertTrue(currentSuggestions.isEmpty(),
            "Suggestion list should be empty when the search yields no results. Found: " + currentSuggestions);
    }

    @Then("I should NOT see {string} in the suggestions")
    public void iShouldNotSeeInTheSuggestions(String brand) {
        boolean brandFound = currentSuggestions.stream()
                .anyMatch(s -> s.toLowerCase().contains(brand.toLowerCase()));
        assertFalse(brandFound,
                "Suggestions should NOT contain '" + brand + "' models. Found suggestions: " + currentSuggestions);
    }

    @Then("I should see {string} in the autocomplete suggestions")
    public void iShouldSeeInTheAutocompleteSuggestions(String expectedModel) {
        assertFalse(currentSuggestions.isEmpty(),
                "Autocomplete suggestions should not be empty");

        boolean found = currentSuggestions.stream()
                .anyMatch(s -> s.toLowerCase().contains(expectedModel.toLowerCase()));
        assertTrue(found,
                "Autocomplete should contain '" + expectedModel + "'. Found: " + currentSuggestions);
    }

    @Then("I should be on the iPhone 16 Pro collection page")
    public void iShouldBeOnTheIphone16ProCollectionPage() {
        page.waitForLoadState();
        String currentUrl = page.url().toLowerCase();
        assertTrue(currentUrl.contains("iphone-16-pro"),
                "URL should contain 'iphone-16-pro'. Actual URL: " + currentUrl);
        // Ensure we're NOT on iPhone 16 Pro Max page
        assertFalse(currentUrl.contains("iphone-16-pro-max"),
                "URL should NOT contain 'iphone-16-pro-max'. Actual URL: " + currentUrl);
    }

    @Then("I should see material variants including {string}, {string}, and {string}")
    public void iShouldSeeMaterialVariants(String material1, String material2, String material3) {
        List<String> variants = productListPage.getMaterialVariants();
        assertFalse(variants.isEmpty(), "Material variants should not be empty");

        System.out.println("Available material variants: " + variants);

        // Check that required materials exist (case-insensitive partial match since
        // label text may vary)
        boolean hasMaterial1 = variants.stream()
                .anyMatch(v -> v.toLowerCase().contains(material1.toLowerCase()));
        boolean hasMaterial2 = variants.stream()
                .anyMatch(v -> v.toLowerCase().contains(material2.toLowerCase()));
        boolean hasMaterial3 = variants.stream()
                .anyMatch(v -> v.toLowerCase().contains(material3.toLowerCase()));

        assertTrue(hasMaterial1, "Should have '" + material1 + "' variant. Available: " + variants);
        assertTrue(hasMaterial2, "Should have '" + material2 + "' variant. Available: " + variants);
        assertTrue(hasMaterial3, "Should have '" + material3 + "' variant. Available: " + variants);
    }

    @Then("the cart should contain {int} items")
    public void theCartShouldContainItems(int expectedCount) {
        int actualCount = cartPage.getCartItemCount();
        assertEquals(expectedCount, actualCount,
                "Cart should contain " + expectedCount + " items but found " + actualCount);
    }

    @Then("I print all cart item details to console")
    public void iPrintAllCartItemDetailsToConsole() {
        cartPage.printCartDetails();

        List<Map<String, String>> items = cartPage.getCartItemDetails();
        assertFalse(items.isEmpty(), "Cart item details should not be empty");

        for (Map<String, String> item : items) {
            assertFalse(item.get("material").isBlank(), "Material should not be blank");
            assertFalse(item.get("price").isBlank(), "Price should not be blank");
            assertFalse(item.get("link").isBlank(), "Link should not be blank");
        }
    }
}
