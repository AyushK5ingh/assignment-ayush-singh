Feature: CaseKaro Mobile Cover Purchase Flow
  As a user, I want to search for an iPhone 16 Pro case,
  add Hard, Soft, and Glass variants to the cart,
  and validate the cart contents.

  Scenario: Add iPhone 16 Pro case with all 3 material variants to cart and validate
    Given I navigate to the CaseKaro website
    When I click on "Mobile Covers" from the top navigation menu
    Then I should be on the phone cases by model page

    When I search for "Apple" in the phone model search box
    Then I should see a no results message for the search
    And I should NOT see "Samsung" in the suggestions
    And I should NOT see "OnePlus" in the suggestions
    And I should NOT see "Xiaomi" in the suggestions
    And I should NOT see "Vivo" in the suggestions

    When I clear the search box and search for "iPhone 16 Pro"
    Then I should see "iPhone 16 Pro" in the autocomplete suggestions

    When I click on exactly "iPhone 16 Pro" from the suggestions
    Then I should be on the iPhone 16 Pro collection page
    When I click "Choose Options" on the first product card
    Then I should see material variants including "Hard", "Soft", and "Glass"

    When I select "Hard" material and add to cart
    And I close the cart drawer
    And I click "Choose Options" on the first product card
    And I select "Soft" material and add to cart
    And I close the cart drawer
    And I click "Choose Options" on the first product card
    And I select "Glass" material and add to cart

    When I open the cart
    Then the cart should contain 3 items
    And I print all cart item details to console
