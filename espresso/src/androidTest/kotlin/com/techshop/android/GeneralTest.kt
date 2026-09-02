package com.techshop.android

import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * General and Catalog test suite for TechShop Android app (Espresso / Compose Test).
 *
 * Implements the CATALOG test cases defined in test-cases.md against com.techshop.android.
 * Covers positive, negative, and edge test cases including planted bug regressions.
 */
@RunWith(AndroidJUnit4::class)
class GeneralTest : BaseEspressoTest() {

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
        composeTestRule.onNodeWithTag("catalog-title")
            .assertIsDisplayed()
            .assertTextEquals("Products")
    }

    /**
     * TC-CAT-002: Clean truncation of long product names.
     * Category: Edge
     * Bug: BUG-007 (Long product name overflows cell layout without maxLines)
     * Target: Espresso / Appium (Layout & line constraint inspection)
     *
     * Inspects Product 3 (name-p3) to assert that its text layout is constrained
     * to a single line with clean ellipsis truncation.
     */
    @Test
    fun testTcCat002LongProductNameTruncation() {
        login()
        val textLayoutResults = mutableListOf<TextLayoutResult>()
        composeTestRule.onNodeWithTag("name-p3")
            .assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                it(textLayoutResults)
            }

        val layoutResult = textLayoutResults.firstOrNull()
        assertNotNull("Expected TextLayoutResult for name-p3", layoutResult)
        assertEquals(
            "Expected long product title to be constrained to 1 line with truncation (BUG-007)",
            1,
            layoutResult?.lineCount
        )
    }

    /**
     * TC-CAT-003: Out-of-stock badge color & disabled state verification.
     * Category: Edge
     * Bug: BUG-008 (Out-of-stock badge styled in green instead of red)
     * Target: Espresso / Appium (Color attribute & disabled state inspection)
     *
     * Inspects Product 4 (p4) to verify that the "Out of Stock" badge is displayed
     * in Red color and the corresponding "Add" button is disabled.
     */
    @Test
    fun testTcCat003OutOfStockBadgeAndDisabledState() {
        login()

        // 1. Verify Add button is disabled
        composeTestRule.onNodeWithTag("add-p4")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        // 2. Verify badge text
        val badgeNode = composeTestRule.onNodeWithTag("stock-p4")
        badgeNode.assertIsDisplayed().assertTextEquals("Out of Stock")

        // 3. Inspect badge pixel colors to assert Red styling (BUG-008)
        val imageBitmap = badgeNode.captureToImage()
        val pixelMap = imageBitmap.toPixelMap()

        var hasRedPixel = false
        var hasGreenPixel = false

        for (x in 0 until pixelMap.width step 2) {
            for (y in 0 until pixelMap.height step 2) {
                val color = pixelMap[x, y]
                if (color.alpha > 0.5f) {
                    if (color.red > 0.6f && color.green < 0.4f) {
                        hasRedPixel = true
                    }
                    if (color.green > 0.6f && color.red < 0.4f) {
                        hasGreenPixel = true
                    }
                }
            }
        }

        assertTrue(
            "Expected Out of Stock badge to be rendered in Red color (BUG-008: styled in Green)",
            hasRedPixel && !hasGreenPixel
        )
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

        // Verify product items exist and are displayed
        composeTestRule.onNodeWithTag("name-p1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("name-p2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("name-p3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("name-p4").assertIsDisplayed()

        // Verify in-stock Add buttons are enabled
        composeTestRule.onNodeWithTag("add-p1").assertIsDisplayed().assertIsEnabled()
        composeTestRule.onNodeWithTag("add-p2").assertIsDisplayed().assertIsEnabled()
        composeTestRule.onNodeWithTag("add-p3").assertIsDisplayed().assertIsEnabled()

        // Verify out-of-stock Add button is disabled
        composeTestRule.onNodeWithTag("add-p4").assertIsDisplayed().assertIsNotEnabled()
    }
}
