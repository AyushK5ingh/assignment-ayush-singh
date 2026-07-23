Feature: CaseKaro Mobile Cover Purchase Flow
  As a user, I want to search for an iPhone 16 Pro case,
  add all 3 material variants (Hard, Soft, Glass) to the cart,
  and validate the cart contents.

  Scenario: Add iPhone 16 Pro case with all 3 material variants to cart and validate

    # Step 1: Navigate to website
    Given I navigate to the CaseKaro website

    # Step 2: Click Mobile Covers from top navigation
    When I click on "Mobile Covers" from the top navigation menu
    Then I should be on the phone cases by model page

    # Step 3-4: Search for Apple and validate
    When I search for "Apple" in the phone model search box
    Then I should see Apple or iPhone related suggestions

    # Step 5: Negative validation - other brands should NOT be visible
    Then I should NOT see "Samsung" in the suggestions
    And I should NOT see "OnePlus" in the suggestions
    And I should NOT see "Xiaomi" in the suggestions
    And I should NOT see "Vivo" in the suggestions

    # Step 6: Search for iPhone 16 Pro
    When I clear the search box and search for "iPhone 16 Pro"
    Then I should see "iPhone 16 Pro" in the autocomplete suggestions

    # Step 7: Click on iPhone 16 Pro (not Pro Max)
    When I click on exactly "iPhone 16 Pro" from the suggestions

    # Step 8: Click Choose Options on first product
    Then I should be on the iPhone 16 Pro collection page
    When I click "Choose Options" on the first product card

    # Step 9: Understand the 3 material variants
    Then I should see material variants including "Hard", "Soft", and "Glass"

    # Step 10: Add all 3 materials to cart
    When I select "Hard" material and add to cart
    And I close the cart drawer
    And I click "Choose Options" on the first product card
    And I select "Soft" material and add to cart
    And I close the cart drawer
    And I click "Choose Options" on the first product card
    And I select "Glass" material and add to cart

    # Step 11: Validate cart and print details
    Then the cart should contain 3 items
    And I print all cart item details to console
