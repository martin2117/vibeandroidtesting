package com.techshop.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Checkout test suite for TechShop Android app (Espresso / Compose Test).
 *
 * Implements the CHECKOUT test cases defined in test-cases.md against com.techshop.android.
 *
 * NOTE ON BROKEN VS. FIXED BUILDS:
 * On the broken build (`techshop/compose-broken`), BUG-011 renders the "Proceed to Checkout"
 * button a no-op (empty onClick lambda). Consequently, all checkout tests that require reaching
 * the Checkout screen are blocked by BUG-011 and fail at navigation on the broken build.
 * These tests are fully verified to run and pass on the fixed build (`techshop/compose-fixed`).
 */
@RunWith(AndroidJUnit4::class)
class CheckoutTest : BaseEspressoTest() {

    private fun fillValidCheckoutForm(
        firstName: String = "Jane",
        lastName: String = "Doe",
        email: String = "jane.doe@example.com",
        phone: String = "5551234567",
        card: String = "4111222233334444",
        expiry: String = "12/28",
        cvv: String = "123",
    ) {
        composeTestRule.onNodeWithTag("checkout-firstName").performTextReplacement(firstName)
        composeTestRule.onNodeWithTag("checkout-lastName").performTextReplacement(lastName)
        composeTestRule.onNodeWithTag("checkout-email").performTextReplacement(email)
        composeTestRule.onNodeWithTag("checkout-phone").performTextReplacement(phone)
        composeTestRule.onNodeWithTag("checkout-card").performTextReplacement(card)
        composeTestRule.onNodeWithTag("checkout-expiry").performTextReplacement(expiry)
        composeTestRule.onNodeWithTag("checkout-cvv").performTextReplacement(cvv)
    }

    /**
     * TC-CHK-001: End-to-end successful checkout submission.
     * Category: Positive
     * Bug: Covers BUG-013 (Order reference missing on Confirmation screen).
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Fills all valid customer and payment details, taps "Place Order", and asserts
     * transition to Confirmation screen with confirmation title and generated reference ("TS-").
     */
    @Test
    fun testTcChk001SuccessfulCheckoutSubmission() {
        navigateToCheckout("p1")

        fillValidCheckoutForm()
        composeTestRule.onNodeWithTag("checkout-submit").performClick()

        composeTestRule.onNodeWithTag("confirmation-title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmation-order-ref").assertIsDisplayed()
    }

    /**
     * TC-CHK-002: Rejection of empty checkout form submission.
     * Category: Negative
     * Bug: Covers BUG-012 (Empty form submission allowed).
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Leaves all fields blank, taps "Place Order", and asserts error is displayed
     * in checkout-error and confirmation screen is not reached.
     */
    @Test
    fun testTcChk002EmptyFormSubmissionRejection() {
        navigateToCheckout("p1")

        composeTestRule.onNodeWithTag("checkout-submit").performClick()

        composeTestRule.onNodeWithTag("checkout-error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmation-title").assertDoesNotExist()
    }

    /**
     * TC-CHK-003: Rejection of expired credit card date.
     * Category: Negative
     * Bug: Covers BUG-009 (Expired credit card date accepted).
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Enters an expired date ("01/20"), taps "Place Order", and asserts form validation
     * rejects the submission and displays error in checkout-error.
     */
    @Test
    fun testTcChk003ExpiredCardDateRejection() {
        navigateToCheckout("p1")

        fillValidCheckoutForm(expiry = "01/20")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()

        composeTestRule.onNodeWithTag("checkout-error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmation-title").assertDoesNotExist()
    }

    /**
     * TC-CHK-004: Card number 16-digit length & numeric constraint.
     * Category: Edge
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Enters 15 digits (rejected), then corrects to 16 digits (accepted).
     */
    @Test
    fun testTcChk004CardNumberLengthConstraint() {
        navigateToCheckout("p1")

        // 15 digits rejected
        fillValidCheckoutForm(card = "411122223333444")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()
        composeTestRule.onNodeWithTag("checkout-error").assertIsDisplayed()

        // 16 digits accepted
        composeTestRule.onNodeWithTag("checkout-card").performTextReplacement("4111222233334444")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()
        composeTestRule.onNodeWithTag("confirmation-title").assertIsDisplayed()
    }

    /**
     * TC-CHK-005: Phone number 10-digit length constraint.
     * Category: Edge
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Enters 9 digits (rejected), then corrects to 10 digits (accepted).
     */
    @Test
    fun testTcChk005PhoneNumberLengthConstraint() {
        navigateToCheckout("p1")

        // 9 digits rejected
        fillValidCheckoutForm(phone = "555123456")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()
        composeTestRule.onNodeWithTag("checkout-error").assertIsDisplayed()

        // 10 digits accepted
        composeTestRule.onNodeWithTag("checkout-phone").performTextReplacement("5551234567")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()
        composeTestRule.onNodeWithTag("confirmation-title").assertIsDisplayed()
    }

    /**
     * TC-CHK-006: CVV numeric keypad type and 3-digit constraint.
     * Category: Negative / Edge
     * Bug: Covers BUG-010 (CVV uses QWERTY text keyboard).
     * Target: Espresso / Appium (Keyboard type & constraint inspection)
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Verifies 2 digits are rejected and exactly 3 digits are accepted.
     */
    @Test
    fun testTcChk006CvvNumericKeypadAndLengthConstraint() {
        navigateToCheckout("p1")

        // 2 digits rejected
        fillValidCheckoutForm(cvv = "12")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()
        composeTestRule.onNodeWithTag("checkout-error").assertIsDisplayed()

        // 3 digits accepted
        composeTestRule.onNodeWithTag("checkout-cvv").performTextReplacement("123")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()
        composeTestRule.onNodeWithTag("confirmation-title").assertIsDisplayed()
    }

    /**
     * TC-CHK-007: Rejection of invalid email format on checkout.
     * Category: Negative
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Enters invalid email format, taps "Place Order", and asserts error displayed.
     */
    @Test
    fun testTcChk007InvalidEmailFormatRejection() {
        navigateToCheckout("p1")

        fillValidCheckoutForm(email = "invalid-email-address")
        composeTestRule.onNodeWithTag("checkout-submit").performClick()

        composeTestRule.onNodeWithTag("checkout-error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmation-title").assertDoesNotExist()
    }

    /**
     * TC-CHK-008: IME padding and field visibility above soft keyboard.
     * Category: Edge
     * Bug: Covers BUG-017 (Keyboard covers CVV field without imePadding).
     * Target: Espresso / Appium (IME insets & layout inspection)
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Focuses CVV field and asserts the field remains displayed on screen above keyboard.
     */
    @Test
    fun testTcChk008ImePaddingAndFieldVisibility() {
        navigateToCheckout("p1")

        composeTestRule.onNodeWithTag("checkout-cvv")
            .performClick()
            .assertIsDisplayed()
            .assertIsFocused()
    }

    /**
     * TC-CHK-009: Prominent order reference display on Confirmation screen.
     * Category: Positive
     * Bug: Covers BUG-013 (Order reference omitted on Confirmation screen).
     *
     * Blocked on broken build by BUG-011 (Proceed button is a no-op).
     * Verifiable on fixed build.
     *
     * Completes checkout and verifies confirmation screen displays order reference ("TS-XXXXXX").
     */
    @Test
    fun testTcChk009OrderReferenceDisplayOnConfirmation() {
        navigateToCheckout("p1")

        fillValidCheckoutForm()
        composeTestRule.onNodeWithTag("checkout-submit").performClick()

        composeTestRule.onNodeWithTag("confirmation-title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmation-order-ref").assertIsDisplayed()
    }
}
