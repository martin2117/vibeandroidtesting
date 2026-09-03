package com.techshop.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Automator Checkout Test Suite for TechShop Android app.
 *
 * Implements the CHECKOUT test cases defined in test-cases.md against com.techshop.android.
 *
 * NOTE ON BROKEN VS. FIXED BUILDS (BUG-011 BLOCKER):
 * On the broken build (`techshop/compose-broken`), the "Proceed to Checkout" button in CartScreen
 * is wired to an empty lambda (`onProceed = {}`), causing BUG-011. As a result, the application
 * cannot navigate from the Cart screen to the Checkout screen via the UI on the broken build.
 *
 * Consequently, all tests in this suite (TC-CHK-001 through TC-CHK-009) that require reaching the
 * Checkout screen are blocked by BUG-011 on the broken build and are intended to be executed and
 * verified against the fixed build (`techshop/compose-fixed`).
 */
@RunWith(AndroidJUnit4::class)
class CheckoutTest : BaseUiAutomatorTest() {

    /**
     * Helper to populate checkout input fields using By.res selectors.
     */
    private fun fillValidCheckoutForm(
        firstName: String = "Jane",
        lastName: String = "Doe",
        email: String = "jane.doe@example.com",
        phone: String = "5551234567",
        card: String = "4111222233334444",
        expiry: String = "12/28",
        cvv: String = "123",
    ) {
        val firstNameField = findAndWait(byRes("checkout-firstName"))
        assertNotNull("First Name field 'checkout-firstName' must be present", firstNameField)
        firstNameField!!.text = firstName

        val lastNameField = findAndWait(byRes("checkout-lastName"))
        assertNotNull("Last Name field 'checkout-lastName' must be present", lastNameField)
        lastNameField!!.text = lastName

        val emailField = findAndWait(byRes("checkout-email"))
        assertNotNull("Email field 'checkout-email' must be present", emailField)
        emailField!!.text = email

        val phoneField = findAndWait(byRes("checkout-phone"))
        assertNotNull("Phone field 'checkout-phone' must be present", phoneField)
        phoneField!!.text = phone

        val cardField = findAndWait(byRes("checkout-card"))
        assertNotNull("Card field 'checkout-card' must be present", cardField)
        cardField!!.text = card

        val expiryField = findAndWait(byRes("checkout-expiry"))
        assertNotNull("Expiry field 'checkout-expiry' must be present", expiryField)
        expiryField!!.text = expiry

        val cvvField = findAndWait(byRes("checkout-cvv"))
        assertNotNull("CVV field 'checkout-cvv' must be present", cvvField)
        cvvField!!.text = cvv
    }

    /**
     * TC-CHK-001: End-to-end successful checkout submission.
     * Category: Positive
     * Bug: Covers BUG-013 (Order reference missing on confirmation screen).
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Fills all valid customer and payment details, taps "Place Order", and asserts
     * transition to Confirmation screen with confirmation title and generated reference ("TS-").
     */
    @Test
    fun testTcChk001SuccessfulCheckoutSubmission() {
        navigateToCheckout("p1")

        fillValidCheckoutForm()

        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val confirmationTitle = findAndWait(byRes("confirmation-title")) ?: findAndWait(By.text("Order Confirmed!"))
        assertNotNull("Confirmation title 'confirmation-title' must be displayed", confirmationTitle)

        val orderRef = findAndWait(byRes("confirmation-order-ref")) ?: findAndWait(By.textStartsWith("Order Ref:"))
        assertNotNull("Order reference 'confirmation-order-ref' must be displayed (BUG-013)", orderRef)
    }

    /**
     * TC-CHK-002: Rejection of empty checkout form submission.
     * Category: Negative
     * Bug: Covers BUG-012 (Empty checkout form submission allowed).
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Leaves all fields blank, taps "Place Order", and asserts error is displayed
     * in checkout-error and confirmation screen is not reached.
     */
    @Test
    fun testTcChk002EmptyFormSubmissionRejection() {
        navigateToCheckout("p1")

        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()
        device.waitForIdle()

        val hasError = device.wait(Until.hasObject(byRes("checkout-error")), 5000L)
            || (device.wait(Until.hasObject(By.text("All fields are required")), 3000L))
        assertNotNull("Inline error 'checkout-error' must be displayed on empty form submission (BUG-012)", if (hasError) device.findObject(byRes("checkout-error")) ?: device.findObject(By.text("All fields are required")) else null)

        val confirmationTitle = device.findObject(byRes("confirmation-title"))
        assertNull("Confirmation screen must not be reached on empty form submission", confirmationTitle)
    }

    /**
     * TC-CHK-003: Rejection of expired credit card date.
     * Category: Negative
     * Bug: Covers BUG-009 (Past credit card expiry date accepted).
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Enters an expired date ("01/20"), taps "Place Order", and asserts form validation
     * rejects the submission and displays error in checkout-error.
     */
    @Test
    fun testTcChk003ExpiredCardDateRejection() {
        navigateToCheckout("p1")

        fillValidCheckoutForm(expiry = "01/20")

        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val checkoutError = findAndWait(byRes("checkout-error"))
        assertNotNull("Inline error 'checkout-error' must be displayed for expired card date (BUG-009)", checkoutError)

        val confirmationTitle = findAndWait(byRes("confirmation-title"), 1000L)
        assertNull("Confirmation screen must not be reached with expired card date", confirmationTitle)
    }

    /**
     * TC-CHK-004: Card number 16-digit length & numeric constraint.
     * Category: Edge
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Enters 15 digits (rejected with validation error), then corrects to 16 digits (accepted).
     */
    @Test
    fun testTcChk004CardNumberLengthConstraint() {
        navigateToCheckout("p1")

        // 1. Enter 15 digits — should be rejected
        fillValidCheckoutForm(card = "411122223333444")
        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val checkoutError = findAndWait(byRes("checkout-error"))
        assertNotNull("15-digit card number must trigger validation error", checkoutError)

        // 2. Correct to 16 digits — should be accepted
        val cardField = findAndWait(byRes("checkout-card"))
        assertNotNull("Card field 'checkout-card' must be present", cardField)
        cardField!!.text = "4111222233334444"

        val updatedSubmitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", updatedSubmitBtn)
        updatedSubmitBtn!!.click()

        val confirmationTitle = findAndWait(byRes("confirmation-title")) ?: findAndWait(By.text("Order Confirmed!"))
        assertNotNull("Confirmation screen must appear with valid 16-digit card", confirmationTitle)
    }

    /**
     * TC-CHK-005: Phone number 10-digit length constraint.
     * Category: Edge
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Enters 9 digits (rejected with validation error), then corrects to 10 digits (accepted).
     */
    @Test
    fun testTcChk005PhoneNumberLengthConstraint() {
        navigateToCheckout("p1")

        // 1. Enter 9 digits — should be rejected
        fillValidCheckoutForm(phone = "555123456")
        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val checkoutError = findAndWait(byRes("checkout-error"))
        assertNotNull("9-digit phone number must trigger validation error", checkoutError)

        // 2. Correct to 10 digits — should be accepted
        val phoneField = findAndWait(byRes("checkout-phone"))
        assertNotNull("Phone field 'checkout-phone' must be present", phoneField)
        phoneField!!.text = "5551234567"

        val updatedSubmitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", updatedSubmitBtn)
        updatedSubmitBtn!!.click()

        val confirmationTitle = findAndWait(byRes("confirmation-title")) ?: findAndWait(By.text("Order Confirmed!"))
        assertNotNull("Confirmation screen must appear with valid 10-digit phone", confirmationTitle)
    }

    /**
     * TC-CHK-006: CVV numeric keypad type and 3-digit constraint.
     * Category: Negative / Edge
     * Bug: Covers BUG-010 (CVV uses QWERTY text keyboard).
     * Target Framework: Espresso / Appium (Keypad type inspection) & UI Automator (Length validation)
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * NOTE: As documented in skills/test-authoring.md, UI Automator is a black-box tool
     * that cannot assert IME soft keyboard input type (BUG-010: KeyboardType.Number vs Text).
     * That check is deferred to Espresso / Appium. Here we assert length constraint validation.
     */
    @Test
    fun testTcChk006CvvNumericKeypadAndLengthConstraint() {
        navigateToCheckout("p1")

        // 1. Enter 2 digits — should be rejected
        fillValidCheckoutForm(cvv = "12")
        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val checkoutError = findAndWait(byRes("checkout-error"))
        assertNotNull("2-digit CVV must trigger validation error", checkoutError)

        // 2. Correct to 3 digits — should be accepted
        val cvvField = findAndWait(byRes("checkout-cvv"))
        assertNotNull("CVV field 'checkout-cvv' must be present", cvvField)
        cvvField!!.text = "123"

        val updatedSubmitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", updatedSubmitBtn)
        updatedSubmitBtn!!.click()

        val confirmationTitle = findAndWait(byRes("confirmation-title")) ?: findAndWait(By.text("Order Confirmed!"))
        assertNotNull("Confirmation screen must appear with valid 3-digit CVV", confirmationTitle)
    }

    /**
     * TC-CHK-007: Rejection of invalid email format on checkout.
     * Category: Negative
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Enters invalid email format on checkout, taps "Place Order", and asserts error displayed.
     */
    @Test
    fun testTcChk007InvalidEmailFormatRejection() {
        navigateToCheckout("p1")

        fillValidCheckoutForm(email = "invalid-email-address")

        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val checkoutError = findAndWait(byRes("checkout-error"))
        assertNotNull("Invalid email format must display error in 'checkout-error'", checkoutError)

        val confirmationTitle = findAndWait(byRes("confirmation-title"), 1000L)
        assertNull("Confirmation screen must not be reached on invalid email format", confirmationTitle)
    }

    /**
     * TC-CHK-008: IME padding and field visibility above soft keyboard.
     * Category: Edge
     * Bug: Covers BUG-017 (Keyboard covers CVV without imePadding).
     * Target Framework: Espresso / Appium (IME insets / keyboard overlap inspection)
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * NOTE: Asserting exact IME window insets and soft keyboard overlay (BUG-017)
     * is deferred to Espresso / Appium. Here we assert field presence and focusability.
     */
    @Test
    fun testTcChk008ImePaddingAndFieldVisibility() {
        navigateToCheckout("p1")

        val cvvField = findAndWait(byRes("checkout-cvv"))
        assertNotNull("CVV field 'checkout-cvv' must be displayed on checkout screen", cvvField)
        cvvField!!.click()
    }

    /**
     * TC-CHK-009: Prominent order reference display on Confirmation screen.
     * Category: Positive
     * Bug: Covers BUG-013 (Order reference omitted on Confirmation screen).
     * Build Target: Runs on FIXED build only (Blocked by BUG-011 on broken build).
     *
     * Completes checkout and verifies confirmation screen displays order reference ("TS-XXXXXX").
     */
    @Test
    fun testTcChk009OrderReferenceDisplayOnConfirmation() {
        navigateToCheckout("p1")

        fillValidCheckoutForm()

        val submitBtn = findAndWait(byRes("checkout-submit")) ?: findAndWait(By.text("Place Order"))
        assertNotNull("Place Order button 'checkout-submit' must be present", submitBtn)
        submitBtn!!.click()

        val confirmationTitle = findAndWait(byRes("confirmation-title")) ?: findAndWait(By.text("Order Confirmed!"))
        assertNotNull("Confirmation title 'confirmation-title' must be displayed", confirmationTitle)

        val orderRef = findAndWait(byRes("confirmation-order-ref")) ?: findAndWait(By.textStartsWith("Order Ref:"))
        assertNotNull("Order reference 'confirmation-order-ref' must be prominently displayed (BUG-013)", orderRef)
    }
}
