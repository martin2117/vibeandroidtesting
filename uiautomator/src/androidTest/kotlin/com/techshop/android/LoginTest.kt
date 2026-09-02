package com.techshop.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Automator Login Test Suite for TechShop Android app.
 *
 * Implements the LOGIN test cases defined in test-cases.md against com.techshop.android.
 * Elements are located by resource-id using By.res(PACKAGE_NAME, ...), falling back to
 * visible text only where specified by testability defects.
 */
@RunWith(AndroidJUnit4::class)
class LoginTest : BaseUiAutomatorTest() {

    /**
     * TC-LOGIN-001: Successful authentication with valid credentials.
     * Category: Positive
     *
     * Enters valid credentials, clicks "Log In", and asserts successful navigation
     * to the Product Catalog screen where the catalog header and products are displayed.
     */
    @Test
    fun testTcLogin001SuccessfulAuthentication() {
        val emailInput = findAndWait(byRes("login-email"))
        assertNotNull("Email input field 'login-email' must be present", emailInput)
        emailInput!!.text = testEmail

        val passwordInput = findAndWait(byRes("login-password"))
        assertNotNull("Password input field 'login-password' must be present", passwordInput)
        passwordInput!!.text = testPassword

        val loginBtn = findAndWait(By.text("Log In"))
        assertNotNull("Log In button must be present", loginBtn)
        loginBtn!!.click()

        val catalogTitle = findAndWait(byRes("catalog-title"))
        assertNotNull("Catalog title 'catalog-title' must be displayed after login", catalogTitle)

        val firstProduct = findAndWait(byRes("name-p1"))
        assertNotNull("Product list item 'name-p1' must be displayed on catalog screen", firstProduct)
    }

    /**
     * TC-LOGIN-002: Rejection of empty credentials submission.
     * Category: Negative
     * Bug: BUG-002 (Auth bypass on empty fields)
     *
     * Leaves Email and Password fields blank, clicks "Log In", and asserts that
     * submission is rejected with an inline error (login-error) and user remains on Login screen.
     */
    @Test
    fun testTcLogin002EmptyCredentialsRejection() {
        val loginBtn = findAndWait(By.text("Log In"))
        assertNotNull("Log In button must be present", loginBtn)
        loginBtn!!.click()

        val loginError = findAndWait(byRes("login-error"), 2000L)
        assertNotNull("Inline error 'login-error' must be displayed on empty submission", loginError)

        val catalogTitle = findAndWait(byRes("catalog-title"), 1000L)
        assertNull("Catalog screen must not be reached on empty credentials", catalogTitle)
    }

    /**
     * TC-LOGIN-003: Rejection of invalid password credentials.
     * Category: Negative
     * Bug: BUG-003 (Auth bypass on wrong password)
     *
     * Enters a valid email with an incorrect password, clicks "Log In", and asserts
     * that authentication fails with an inline error message (login-error) on the Login screen.
     */
    @Test
    fun testTcLogin003InvalidPasswordRejection() {
        val emailInput = findAndWait(byRes("login-email"))
        assertNotNull("Email input field 'login-email' must be present", emailInput)
        emailInput!!.text = testEmail

        val passwordInput = findAndWait(byRes("login-password"))
        assertNotNull("Password input field 'login-password' must be present", passwordInput)
        passwordInput!!.text = "wrongpassword123"

        val loginBtn = findAndWait(By.text("Log In"))
        assertNotNull("Log In button must be present", loginBtn)
        loginBtn!!.click()

        val loginError = findAndWait(byRes("login-error"), 2000L)
        assertNotNull("Inline error 'login-error' must be displayed on wrong password", loginError)

        val catalogTitle = findAndWait(byRes("catalog-title"), 1000L)
        assertNull("Catalog screen must not be reached on invalid credentials", catalogTitle)
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
        val emailInput = findAndWait(byRes("login-email"))
        assertNotNull("Email input field 'login-email' must be present", emailInput)
        emailInput!!.text = "invalid-email-format"

        val passwordInput = findAndWait(byRes("login-password"))
        assertNotNull("Password input field 'login-password' must be present", passwordInput)
        passwordInput!!.text = testPassword

        val loginBtn = findAndWait(By.text("Log In"))
        assertNotNull("Log In button must be present", loginBtn)
        loginBtn!!.click()

        val loginError = findAndWait(byRes("login-error"), 2000L)
        assertNotNull("Inline error 'login-error' must be displayed on invalid email format", loginError)

        val catalogTitle = findAndWait(byRes("catalog-title"), 1000L)
        assertNull("Catalog screen must not be reached on invalid email format", catalogTitle)
    }

    /**
     * TC-LOGIN-005: Password input masking verification.
     * Category: Edge
     * Bug: BUG-001 (Plaintext password entry)
     * Target Framework: Espresso / Appium (Deferred from UI Automator)
     *
     * NOTE: As documented in skills/test-authoring.md, UI Automator is a black-box testing
     * framework that cannot inspect Compose semantics (PasswordVisualTransformation) or
     * view-layer inputType attributes. Verification of password masking is therefore deferred
     * to Espresso (SemanticsProperties.Password) and Appium (get_attribute("password")).
     *
     * This test documents the black-box limitation and validates presence of the password field.
     */
    @Test
    fun testTcLogin005PasswordInputMasking() {
        val passwordInput = findAndWait(byRes("login-password"))
        assertNotNull(
            "Password input field 'login-password' must exist. Note: PasswordVisualTransformation " +
                "(BUG-001) attribute inspection is deferred to Espresso / Appium.",
            passwordInput
        )
    }

    /**
     * TC-LOGIN-006: Unauthenticated navigation tab bar gating.
     * Category: Edge
     * Bug: BUG-015 (Ungated bottom navigation tabs before login)
     *
     * Inspects screen layout on launch before authentication and asserts that bottom
     * navigation tabs (Products, Cart) are hidden until the user authenticates.
     */
    @Test
    fun testTcLogin006UnauthenticatedTabBarGating() {
        val productsTab = findAndWait(byRes("tab-products"), 1500L)
        val cartTab = findAndWait(byRes("tab-cart"), 1500L)

        assertNull("Bottom navigation tab 'tab-products' must NOT be accessible before login (BUG-015)", productsTab)
        assertNull("Bottom navigation tab 'tab-cart' must NOT be accessible before login (BUG-015)", cartTab)
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

        val catalogTitle = findAndWait(byRes("catalog-title"))
        assertNotNull("Catalog screen should be displayed after successful login", catalogTitle)

        // Switch to Cart tab
        val cartTab = findAndWait(byRes("tab-cart"))
        assertNotNull("Cart tab 'tab-cart' must be accessible when authenticated", cartTab)
        cartTab!!.click()

        val emptyCart = findAndWait(byRes("cart-empty"))
        assertNotNull("Cart empty state 'cart-empty' must be displayed", emptyCart)

        // Switch back to Products tab
        val productsTab = findAndWait(byRes("tab-products"))
        assertNotNull("Products tab 'tab-products' must be accessible", productsTab)
        productsTab!!.click()

        val catalogTitlePersisted = findAndWait(byRes("catalog-title"))
        assertNotNull("Catalog title should persist without re-prompting login", catalogTitlePersisted)

        val emailInput = findAndWait(byRes("login-email"), 1000L)
        assertNull("Login screen should not reappear after tab navigation", emailInput)
    }

    /**
     * TC-LOGIN-008: Primary login submit button testability identifier.
     * Category: Edge
     * Bug: BUG-016 (Missing testTag on Log In button)
     *
     * Black-box tools require stable identifiers. This test specifically asserts that
     * the Log In button exposes a dedicated resource-id / testTag ("login-submit").
     * On the broken build, the button lacks an identifier and this test will fail, catching BUG-016.
     */
    @Test
    fun testTcLogin008LoginButtonTestabilityIdentifier() {
        val submitButtonById = findAndWait(byRes("login-submit"), 2000L)
        assertNotNull(
            "Primary login button must expose a stable resource-id 'login-submit' (BUG-016)",
            submitButtonById
        )
    }
}
