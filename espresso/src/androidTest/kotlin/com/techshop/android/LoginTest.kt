package com.techshop.android

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Login test suite for TechShop Android app (Espresso / Compose Test).
 *
 * Implements the LOGIN test cases defined in test-cases.md against com.techshop.android.
 * Covers positive, negative, and edge test cases including planted bug regressions.
 */
@RunWith(AndroidJUnit4::class)
class LoginTest : BaseEspressoTest() {

    /**
     * TC-LOGIN-001: Successful authentication with valid credentials.
     * Category: Positive
     *
     * Enters valid credentials, clicks "Log In", and asserts successful navigation
     * to the Product Catalog screen where the catalog header and products are displayed.
     */
    @Test
    fun testTcLogin001SuccessfulAuthentication() {
        composeTestRule.onNodeWithTag("login-email").performTextReplacement(testEmail)
        composeTestRule.onNodeWithTag("login-password").performTextReplacement(testPassword)
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithTag("catalog-title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("name-p1").assertIsDisplayed()
    }

    /**
     * TC-LOGIN-002: Rejection of empty credentials submission.
     * Category: Negative
     * Bug: BUG-002 (Empty credentials bypass authentication)
     *
     * Leaves email and password empty, clicks "Log In", and asserts that submission
     * is rejected with an inline error (login-error) and user remains on Login screen.
     */
    @Test
    fun testTcLogin002EmptyCredentialsRejection() {
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithTag("login-error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("catalog-title").assertDoesNotExist()
    }

    /**
     * TC-LOGIN-003: Rejection of invalid password credentials.
     * Category: Negative
     * Bug: BUG-003 (Invalid password bypasses authentication)
     *
     * Enters valid email with an incorrect password, clicks "Log In", and asserts
     * that authentication fails with an error message (login-error) displayed on Login screen.
     */
    @Test
    fun testTcLogin003InvalidPasswordRejection() {
        composeTestRule.onNodeWithTag("login-email").performTextReplacement(testEmail)
        composeTestRule.onNodeWithTag("login-password").performTextReplacement("wrongpassword123")
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithTag("login-error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("catalog-title").assertDoesNotExist()
    }

    /**
     * TC-LOGIN-004: Rejection of invalid email format.
     * Category: Negative
     *
     * Enters a malformed email format and valid password, clicks "Log In", and asserts
     * that format validation fails, displaying an error message (login-error) on Login screen.
     */
    @Test
    fun testTcLogin004InvalidEmailFormatRejection() {
        composeTestRule.onNodeWithTag("login-email").performTextReplacement("invalid-email-format")
        composeTestRule.onNodeWithTag("login-password").performTextReplacement(testPassword)
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithTag("login-error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("catalog-title").assertDoesNotExist()
    }

    /**
     * TC-LOGIN-005: Password input masking verification.
     * Category: Edge
     * Bug: BUG-001 (Plaintext password entry)
     * Target: Espresso / Appium (Attribute-aware inspection)
     *
     * Enters password text into the password field and asserts that the node config
     * defines the Password semantics property (PasswordVisualTransformation), ensuring
     * characters are masked rather than displayed in plaintext.
     */
    @Test
    fun testTcLogin005PasswordInputMasking() {
        composeTestRule.onNodeWithTag("login-password").performTextReplacement(testPassword)
        composeTestRule.onNodeWithTag("login-password")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
    }

    /**
     * TC-LOGIN-006: Unauthenticated navigation tab bar gating.
     * Category: Edge
     * Bug: BUG-015 (Ungated bottom navigation tabs before login)
     *
     * Inspects screen layout on app launch before authentication and asserts that
     * bottom navigation tabs (Products, Cart) are hidden until the user authenticates.
     */
    @Test
    fun testTcLogin006UnauthenticatedTabBarGating() {
        composeTestRule.onNodeWithTag("tab-products").assertDoesNotExist()
        composeTestRule.onNodeWithTag("tab-cart").assertDoesNotExist()
    }

    /**
     * TC-LOGIN-007: Session persistence across in-app navigation.
     * Category: Positive
     *
     * Authenticates successfully, navigates to the Cart tab, navigates back to
     * the Products tab, and asserts that the session persists without re-prompting for login.
     */
    @Test
    fun testTcLogin007SessionPersistence() {
        login()
        composeTestRule.onNodeWithTag("catalog-title").assertIsDisplayed()

        // Switch to Cart tab
        composeTestRule.onNodeWithTag("tab-cart").performClick()
        composeTestRule.onNodeWithTag("cart-empty").assertIsDisplayed()

        // Switch back to Products tab
        composeTestRule.onNodeWithTag("tab-products").performClick()
        composeTestRule.onNodeWithTag("catalog-title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login-email").assertDoesNotExist()
    }

    /**
     * TC-LOGIN-008: Primary login submit button testability identifier.
     * Category: Edge
     * Bug: BUG-016 (Missing testTag on Log In button)
     *
     * Inspects the view hierarchy to assert that the "Log In" button exposes
     * a dedicated testTag / resource-id ("login-submit").
     */
    @Test
    fun testTcLogin008LoginButtonTestabilityIdentifier() {
        composeTestRule.onNodeWithTag("login-submit")
            .assertExists()
            .assertIsDisplayed()
    }
}
