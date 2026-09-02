package com.techshop.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Automator General and Catalog Test Suite for TechShop Android app.
 *
 * Implements the CATALOG test cases defined in test-cases.md against com.techshop.android.
 * Covers positive, negative, and edge test cases including planted bug regressions.
 * Every interaction uses explicit synchronization via findAndWait and By.res selectors.
 */
@RunWith(AndroidJUnit4::class)
class GeneralTest : BaseUiAutomatorTest() {

    /**
     * TC-CAT-001: Catalog navigation header title verification.
     * Category: Positive
     * Bug: BUG-014 (Catalog title displays "Untitled" instead of "Products")
     *
     * Authenticates and verifies the top navigation header on the Catalog screen
     * displays "Products".
     */
    @Test
    fun testTcCat001CatalogTitleVerification() {
        login()

        val catalogTitle = findAndWait(byRes("catalog-title"))
        assertNotNull("Catalog title 'catalog-title' must be displayed", catalogTitle)
        assertEquals("Catalog header title must be 'Products' (BUG-014)", "Products", catalogTitle!!.text)
    }

    /**
     * TC-CAT-002: Clean truncation of long product names.
     * Category: Edge
     * Bug: BUG-007 (Long product name overflows cell layout without maxLines)
     * Target Framework: Espresso / Appium (Deferred from UI Automator)
     *
     * NOTE: As documented in skills/test-authoring.md, UI Automator is a black-box tool
     * that cannot inspect text line counts, layout bounds overflow, or ellipsis truncation
     * semantics. Inspection of line count constraints (BUG-007) is deferred to Espresso / Appium.
     *
     * This test documents the black-box limitation and validates presence of Product 3 title.
     */
    @Test
    fun testTcCat002LongProductNameTruncation() {
        login()

        val product3Name = findAndWait(byRes("name-p3"))
        assertNotNull(
            "Product 3 title 'name-p3' must be present. Note: TextLayout line count and " +
                "truncation validation (BUG-007) is deferred to Espresso / Appium.",
            product3Name
        )
    }

    /**
     * TC-CAT-003: Out-of-stock badge color & disabled state verification.
     * Category: Edge
     * Bug: BUG-008 (Out-of-stock badge styled in green instead of red)
     * Target Framework: Espresso / Appium (Deferred color inspection from UI Automator)
     *
     * Inspects Product 4 (p4) to verify that the "Out of Stock" badge is displayed
     * and the corresponding "Add" button is disabled (enabled = false).
     *
     * NOTE: Asserting rendered RGB badge color (BUG-008: styled in Green instead of Red)
     * is deferred to Espresso (captureToImage) and Appium.
     */
    @Test
    fun testTcCat003OutOfStockBadgeAndDisabledState() {
        login()

        // 1. Verify Add button is disabled
        val addP4 = findAndWait(byRes("add-p4"))
        assertNotNull("Add button for p4 must be present", addP4)
        assertFalse("Out-of-stock product Add button must be disabled", addP4!!.isEnabled)

        // 2. Verify badge text presence
        val stockBadge = findAndWait(byRes("stock-p4"))
        assertNotNull("Out of Stock badge 'stock-p4' must be displayed", stockBadge)
        assertEquals("Out of Stock", stockBadge!!.text)
    }

    /**
     * TC-CAT-004: Browse catalog products list and items display.
     * Category: Positive
     *
     * Browses the product catalog to assert product names, prices, and Add buttons
     * are properly rendered for all products (p1 through p4).
     */
    @Test
    fun testTcCat004BrowseCatalogProductsList() {
        login()

        // Verify product title elements exist and are displayed
        val nameP1 = findAndWait(byRes("name-p1"))
        val nameP2 = findAndWait(byRes("name-p2"))
        val nameP3 = findAndWait(byRes("name-p3"))
        val nameP4 = findAndWait(byRes("name-p4"))

        assertNotNull("Product 1 title 'name-p1' must be displayed", nameP1)
        assertNotNull("Product 2 title 'name-p2' must be displayed", nameP2)
        assertNotNull("Product 3 title 'name-p3' must be displayed", nameP3)
        assertNotNull("Product 4 title 'name-p4' must be displayed", nameP4)

        // Verify in-stock Add buttons are enabled
        val addP1 = findAndWait(byRes("add-p1"))
        val addP2 = findAndWait(byRes("add-p2"))
        val addP3 = findAndWait(byRes("add-p3"))

        assertNotNull("Add button 'add-p1' must be present", addP1)
        assertTrue("Add button for p1 must be enabled", addP1!!.isEnabled)

        assertNotNull("Add button 'add-p2' must be present", addP2)
        assertTrue("Add button for p2 must be enabled", addP2!!.isEnabled)

        assertNotNull("Add button 'add-p3' must be present", addP3)
        assertTrue("Add button for p3 must be enabled", addP3!!.isEnabled)

        // Verify out-of-stock Add button is disabled
        val addP4 = findAndWait(byRes("add-p4"))
        assertNotNull("Add button 'add-p4' must be present", addP4)
        assertFalse("Add button for out-of-stock p4 must be disabled", addP4!!.isEnabled)
    }
}
