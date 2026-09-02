package com.techshop.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Automator Cart Test Suite for TechShop Android app.
 *
 * Implements the CART test cases defined in test-cases.md against com.techshop.android.
 * Covers positive, negative, and edge test cases including planted bug regressions.
 * Every interaction uses explicit synchronization via findAndWait and By.res selectors.
 */
@RunWith(AndroidJUnit4::class)
class CartTest : BaseUiAutomatorTest() {

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

        val cartTab = findAndWait(byRes("tab-cart"))
        assertNotNull("Cart tab 'tab-cart' must be accessible", cartTab)
        cartTab!!.click()

        val emptyState = findAndWait(byRes("cart-empty"))
        assertNotNull("Empty cart message 'cart-empty' must be displayed", emptyState)
        assertEquals("Your cart is empty", emptyState!!.text)

        val productRow = findAndWait(byRes("qty-p1"), 1000L)
        assertNull("No product rows should exist in an empty cart", productRow)
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

        val qtyField = findAndWait(byRes("qty-p1"))
        assertNotNull("Quantity for p1 'qty-p1' must be displayed in cart", qtyField)
        assertEquals("1", qtyField!!.text)

        val orderTotal = findAndWait(byRes("order-total"))
        assertNotNull("Order total 'order-total' must be displayed", orderTotal)
        assertEquals("Total: $60.00", orderTotal!!.text)
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

        val incButton = findAndWait(byRes("qty-inc-p1"))
        assertNotNull("Increment button 'qty-inc-p1' must be present", incButton)
        incButton!!.click()

        val qtyField = findAndWait(byRes("qty-p1"))
        assertNotNull("Quantity for p1 must be displayed", qtyField)
        assertEquals("2", qtyField!!.text)

        val orderTotal = findAndWait(byRes("order-total"))
        assertNotNull("Order total must be displayed", orderTotal)
        assertEquals("Total: $120.00", orderTotal!!.text)
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

        val decButton = findAndWait(byRes("qty-dec-p1"))
        assertNotNull("Decrement button 'qty-dec-p1' must be present", decButton)
        decButton!!.click()

        val qtyField = findAndWait(byRes("qty-p1"))
        assertNotNull("Quantity for p1 must be displayed", qtyField)
        assertEquals("Quantity must remain clamped at minimum 1 (BUG-005)", "1", qtyField!!.text)
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

        val addP1 = findAndWait(byRes("add-p1"))
        assertNotNull("Add button for p1 must be present", addP1)
        addP1!!.click()

        val addP2 = findAndWait(byRes("add-p2"))
        assertNotNull("Add button for p2 must be present", addP2)
        addP2!!.click()

        val cartTab = findAndWait(byRes("tab-cart"))
        assertNotNull("Cart tab must be present", cartTab)
        cartTab!!.click()

        val removeP1 = findAndWait(byRes("remove-p1"))
        assertNotNull("Remove button for p1 must be present", removeP1)
        removeP1!!.click()

        val qtyP1 = findAndWait(byRes("qty-p1"), 1000L)
        assertNull("Product 1 must be removed from the cart", qtyP1)

        val qtyP2 = findAndWait(byRes("qty-p2"))
        assertNotNull("Product 2 must remain in the cart", qtyP2)
        assertEquals("1", qtyP2!!.text)

        val orderTotal = findAndWait(byRes("order-total"))
        assertNotNull("Order total must be displayed", orderTotal)
        assertEquals("Total: $90.00", orderTotal!!.text)
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

        val addP1 = findAndWait(byRes("add-p1"))
        assertNotNull("Add button for p1 must be present", addP1)
        addP1!!.click()

        val cartTab = findAndWait(byRes("tab-cart"))
        assertNotNull("Cart tab must be present", cartTab)
        cartTab!!.click()

        val incP1 = findAndWait(byRes("qty-inc-p1"))
        assertNotNull("Increment button for p1 must be present", incP1)
        incP1!!.click()

        val discountInput = findAndWait(byRes("discount-input"))
        assertNotNull("Discount input field must be present", discountInput)
        discountInput!!.text = "SAVE10"

        val applyDiscount = findAndWait(byRes("apply-discount"))
        assertNotNull("Apply discount button must be present", applyDiscount)
        applyDiscount!!.click()

        val orderTotal = findAndWait(byRes("order-total"))
        assertNotNull("Order total must be displayed", orderTotal)
        assertEquals("Total: $108.00", orderTotal!!.text)
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

        val cartTab = findAndWait(byRes("tab-cart"))
        assertNotNull("Cart tab must be present", cartTab)
        cartTab!!.click()

        val minOrderError = findAndWait(byRes("min-order-error"))
        assertNotNull("Minimum order error 'min-order-error' must be displayed", minOrderError)
        assertEquals("Minimum order value is $10.00", minOrderError!!.text)

        val proceedCheckout = findAndWait(byRes("proceed-checkout"))
        assertNotNull("Proceed to Checkout button must be present", proceedCheckout)
        proceedCheckout!!.click()

        val checkoutFirstName = findAndWait(byRes("checkout-firstName"), 1000L)
        assertNull("Checkout screen should not open when subtotal is below minimum", checkoutFirstName)
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

        val proceedCheckout = findAndWait(byRes("proceed-checkout"))
        assertNotNull("Proceed to Checkout button must be present", proceedCheckout)
        proceedCheckout!!.click()

        val checkoutFirstName = findAndWait(byRes("checkout-firstName"), 2000L)
        assertNotNull("Checkout form field 'checkout-firstName' must be displayed (BUG-011)", checkoutFirstName)
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

        val discountInput = findAndWait(byRes("discount-input"))
        assertNotNull("Discount input field must be present", discountInput)
        discountInput!!.text = "INVALIDCODE99"

        val applyDiscount = findAndWait(byRes("apply-discount"))
        assertNotNull("Apply discount button must be present", applyDiscount)
        applyDiscount!!.click()

        val orderTotal = findAndWait(byRes("order-total"))
        assertNotNull("Order total must be displayed", orderTotal)
        assertEquals("Total: $60.00", orderTotal!!.text)
    }
}
