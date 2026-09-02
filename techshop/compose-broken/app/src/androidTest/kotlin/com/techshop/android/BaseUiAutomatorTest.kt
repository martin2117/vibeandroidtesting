package com.techshop.android

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Before

/**
 * Base UI Automator test class for TechShop Android black-box testing.
 *
 * Obtains the UiDevice instance via InstrumentationRegistry, launches the target app
 * by package name (com.techshop.android) with a clean task state, reads credentials
 * from the environment or instrumentation arguments, and provides reusable synchronization
 * and interaction helpers.
 */
open class BaseUiAutomatorTest {

    protected lateinit var device: UiDevice

    val testEmail: String = getCredential("TEST_EMAIL", "demo@techshop.com")
    val testPassword: String = getCredential("TEST_PASSWORD", "password123")

    companion object {
        const val PACKAGE_NAME = "com.techshop.android"
        const val DEFAULT_TIMEOUT = 5000L
        const val LAUNCH_TIMEOUT = 8000L
    }

    @Before
    open fun setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Launch app by package name with clean task flags
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        } ?: throw IllegalStateException("Could not find launch intent for package: $PACKAGE_NAME")

        context.startActivity(intent)

        // Wait for the application to appear on screen
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)
    }

    /**
     * Reads a test credential from system environment variables or instrumentation arguments,
     * falling back to a default value if not set.
     */
    protected fun getCredential(key: String, fallback: String): String {
        return System.getenv(key)
            ?: InstrumentationRegistry.getArguments().getString(key)
            ?: fallback
    }

    /**
     * Helper to construct a BySelector for a resource-id in the TechShop package.
     */
    protected fun byRes(resourceId: String): BySelector {
        return By.res(PACKAGE_NAME, resourceId)
    }

    /**
     * Finds a UI element using an explicit wait with Until before returning the UiObject2.
     *
     * @param selector The BySelector to locate the element.
     * @param timeout The maximum duration in milliseconds to wait for the element.
     * @return The found UiObject2, or null if the element is not found within the timeout.
     */
    protected fun findAndWait(selector: BySelector, timeout: Long = DEFAULT_TIMEOUT): UiObject2? {
        val found = device.wait(Until.hasObject(selector), timeout)
        return if (found) {
            device.findObject(selector)
        } else {
            null
        }
    }

    /**
     * Shared helper to perform the standard login flow.
     * Enters email and password, and locates the login button by visible text "Log In".
     */
    fun login(email: String = testEmail, password: String = testPassword) {
        val emailField = findAndWait(byRes("login-email"))
            ?: throw AssertionError("Email input field 'login-email' not found")
        emailField.text = email

        val passwordField = findAndWait(byRes("login-password"))
            ?: throw AssertionError("Password input field 'login-password' not found")
        passwordField.text = password

        // Locate login button by visible text "Log In" as defined in test standards
        val loginButton = findAndWait(By.text("Log In"))
            ?: throw AssertionError("Login button with text 'Log In' not found")
        loginButton.click()
    }

    /**
     * Shared helper to add a product to the cart and navigate to the Cart screen.
     */
    fun addItemAndOpenCart(productId: String = "p1") {
        val addButton = findAndWait(byRes("add-$productId"))
            ?: throw AssertionError("Add button for product '$productId' not found")
        addButton.click()

        val cartTab = findAndWait(byRes("tab-cart"))
            ?: throw AssertionError("Bottom navigation tab 'tab-cart' not found")
        cartTab.click()
    }

    /**
     * Shared helper to navigate directly to the Checkout screen via standard UI flow.
     * Note: Blocked on the broken build by BUG-011 (Proceed to Checkout button is a no-op).
     */
    fun navigateToCheckout(productId: String = "p1") {
        login()
        addItemAndOpenCart(productId)
        val proceedCheckout = findAndWait(byRes("proceed-checkout"))
            ?: throw AssertionError("Proceed to Checkout button 'proceed-checkout' not found")
        proceedCheckout.click()
    }
}
