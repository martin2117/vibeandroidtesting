package com.techshop.android

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

/**
 * Base Espresso test class for TechShop Android instrumentation tests.
 *
 * Initializes createAndroidComposeRule for Jetpack Compose UI testing,
 * reads test credentials from environment variables or instrumentation args,
 * and provides shared navigation and interaction helpers across test classes.
 */
open class BaseEspressoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val testEmail: String = getCredential("TEST_EMAIL", "demo@techshop.com")
    val testPassword: String = getCredential("TEST_PASSWORD", "password123")

    private fun getCredential(key: String, fallback: String): String {
        return System.getenv(key)
            ?: InstrumentationRegistry.getArguments().getString(key)
            ?: fallback
    }

    /**
     * Shared helper to perform login with specified or default credentials.
     * Locates the submit button by visible text "Log In" as defined in test standards.
     */
    fun login(email: String = testEmail, password: String = testPassword) {
        composeTestRule.onNodeWithTag("login-email").performTextReplacement(email)
        composeTestRule.onNodeWithTag("login-password").performTextReplacement(password)
        composeTestRule.onNodeWithText("Log In").performClick()
        composeTestRule.onNodeWithTag("catalog-title").assertExists()
    }

    /**
     * Shared helper to add a product to the cart and navigate to the Cart screen.
     */
    fun addItemAndOpenCart(productId: String = "p1") {
        composeTestRule.onNodeWithTag("add-$productId").performClick()
        composeTestRule.onNodeWithTag("tab-cart").performClick()
        composeTestRule.waitForIdle()
    }

    /**
     * Shared helper to navigate directly to the Checkout screen via standard UI flow.
     * Note: Blocked on the broken build by BUG-011 (Proceed button is a no-op).
     */
    fun navigateToCheckout(productId: String = "p1") {
        login()
        addItemAndOpenCart(productId)
        composeTestRule.onNodeWithTag("proceed-checkout").performClick()
        composeTestRule.waitForIdle()
    }
}
