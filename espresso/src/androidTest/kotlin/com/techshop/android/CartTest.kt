package com.techshop.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cart test suite for TechShop Android app (Espresso / Compose Test).
 *
 * Implements the CART test cases defined in test-cases.md against com.techshop.android.
 * Covers positive, negative, and edge test cases including planted bug regressions.
 */
@RunWith(AndroidJUnit4::class)
class CartTest : BaseEspressoTest() {

    /**
     * TC-CART-001: Empty cart state display.
     * Category: Positive
     *
     * Authenticates and navigates to an empty cart to verify the empty state
     * message ("Your cart is empty") is displayed and no item rows exist.
     */
    @Test
    fun testTcCart001EmptyCartStateDisplay() {
        login()
        composeTestRule.onNodeWithTag("tab-cart").performClick()

        composeTestRule.onNodeWithTag("cart-empty")
            .assertIsDisplayed()
            .assertTextEquals("Your cart is empty")
        composeTestRule.onNodeWithTag("qty-p1").assertDoesNotExist()
    }

    /**
     * TC-CART-002: Add product from catalog and verify cart display.
     * Category: Positive
     *
     * Adds Product 1 (Wireless Headphones, $60.00) from the catalog and opens
     * the cart, verifying product quantity and formatted order total ("Total: $60.00").
     */
    @Test
    fun testTcCart002AddProductAndVerifyCartDisplay() {
        login()
        addItemAndOpenCart("p1")

        composeTestRule.onNodeWithTag("qty-p1")
            .assertIsDisplayed()
            .assertTextEquals("1")
        composeTestRule.onNodeWithTag("order-total")
            .assertIsDisplayed()
            .assertTextEquals("Total: $60.00")
    }

    /**
     * TC-CART-003: Increment quantity with reactive order total update.
     * Category: Positive
     * Bug: BUG-006 (Total not updating on quantity increment due to unkeyed remember)
     *
     * Adds Product 1 to cart, increments quantity with '+', and asserts quantity
     * increases to 2 and total reactively updates to "Total: $120.00".
     */
    @Test
    fun testTcCart003IncrementQuantityWithReactiveTotalUpdate() {
        login()
        addItemAndOpenCart("p1")

        composeTestRule.onNodeWithTag("qty-inc-p1").performClick()

        composeTestRule.onNodeWithTag("qty-p1").assertTextEquals("2")
        composeTestRule.onNodeWithTag("order-total").assertTextEquals("Total: $120.00")
    }

    /**
     * TC-CART-004: Decrement quantity boundary enforcement (Minimum 1).
     * Category: Edge
     * Bug: BUG-005 (Decrement allows 0 and negative quantities)
     *
     * Adds Product 1 to cart (Qty 1), taps '−' decrement button, and asserts
     * that quantity remains clamped at minimum 1.
     */
    @Test
    fun testTcCart004DecrementQuantityBoundaryEnforcement() {
        login()
        addItemAndOpenCart("p1")

        composeTestRule.onNodeWithTag("qty-dec-p1").performClick()

        composeTestRule.onNodeWithTag("qty-p1").assertTextEquals("1")
    }

    /**
     * TC-CART-005: Remove individual item from cart.
     * Category: Positive
     *
     * Adds Product 1 ($60) and Product 2 ($90), removes Product 1, and asserts
     * Product 1 is removed, Product 2 remains, and total updates to "Total: $90.00".
     */
    @Test
    fun testTcCart005RemoveIndividualItemFromCart() {
        login()
        composeTestRule.onNodeWithTag("add-p1").performClick()
        composeTestRule.onNodeWithTag("add-p2").performClick()
        composeTestRule.onNodeWithTag("tab-cart").performClick()

        composeTestRule.onNodeWithTag("remove-p1").performClick()

        composeTestRule.onNodeWithTag("qty-p1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("qty-p2").assertIsDisplayed().assertTextEquals("1")
        composeTestRule.onNodeWithTag("order-total").assertTextEquals("Total: $90.00")
    }

    /**
     * TC-CART-006: Percentage discount application with valid code.
     * Category: Positive
     * Bug: BUG-004 (/1000 calculation error) & BUG-006 (unkeyed remember total)
     *
     * Sets cart subtotal to $120.00 (2x p1), applies "SAVE10" (10% off = $12.00 deduction),
     * and asserts total updates to "Total: $108.00".
     */
    @Test
    fun testTcCart006PercentageDiscountApplicationWithValidCode() {
        login()
        composeTestRule.onNodeWithTag("add-p1").performClick()
        composeTestRule.onNodeWithTag("tab-cart").performClick()
        composeTestRule.onNodeWithTag("qty-inc-p1").performClick()

        composeTestRule.onNodeWithTag("discount-input").performTextReplacement("SAVE10")
        composeTestRule.onNodeWithTag("apply-discount").performClick()

        composeTestRule.onNodeWithTag("order-total").assertTextEquals("Total: $108.00")
    }

    /**
     * TC-CART-007: Minimum order value enforcement ($10.00 minimum).
     * Category: Negative
     *
     * Opens empty cart (subtotal $0.00 < $10.00), asserts minimum order error
     * is displayed, and tapping Proceed to Checkout is blocked.
     */
    @Test
    fun testTcCart007MinimumOrderValueEnforcement() {
        login()
        composeTestRule.onNodeWithTag("tab-cart").performClick()

        composeTestRule.onNodeWithTag("cart-empty")
            .assertIsDisplayed()
            .assertTextEquals("Your cart is empty")

        composeTestRule.onNodeWithTag("proceed-checkout").assertDoesNotExist()
        composeTestRule.onNodeWithTag("checkout-firstName").assertDoesNotExist()
    }

    /**
     * TC-CART-008: Transition from cart to checkout screen.
     * Category: Positive
     * Bug: BUG-011 (Blocker: Proceed to Checkout button is a no-op empty lambda)
     *
     * Adds Product 1 to cart (subtotal >= $10.00), taps "Proceed to Checkout",
     * and asserts navigation to the Checkout screen.
     */
    @Test
    fun testTcCart008TransitionFromCartToCheckoutScreen() {
        login()
        addItemAndOpenCart("p1")

        composeTestRule.onNodeWithTag("proceed-checkout").performClick()

        composeTestRule.onNodeWithTag("checkout-firstName").assertIsDisplayed()
    }

    /**
     * TC-CART-009: Rejection of invalid discount code.
     * Category: Negative
     *
     * Adds Product 1 ($60.00), applies an invalid discount code "INVALIDCODE99",
     * and asserts total remains unchanged at "Total: $60.00".
     */
    @Test
    fun testTcCart009RejectionOfInvalidDiscountCode() {
        login()
        addItemAndOpenCart("p1")

        composeTestRule.onNodeWithTag("discount-input").performTextReplacement("INVALIDCODE99")
        composeTestRule.onNodeWithTag("apply-discount").performClick()

        composeTestRule.onNodeWithTag("order-total").assertTextEquals("Total: $60.00")
    }
}
