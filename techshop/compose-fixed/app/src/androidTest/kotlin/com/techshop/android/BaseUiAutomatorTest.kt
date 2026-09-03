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

        // Force stop to guarantee completely clean state between tests
        try {
            device.executeShellCommand("am force-stop $PACKAGE_NAME")
        } catch (e: Exception) {
            // Ignore
        }

        // Launch app by package name with clean task flags
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        } ?: throw IllegalStateException("Could not find launch intent for package: $PACKAGE_NAME")

        context.startActivity(intent)

        // Wait for the application to appear on screen and render initial screen
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)
        device.wait(Until.hasObject(byRes("login-email")), LAUNCH_TIMEOUT)
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
     * Helper to construct a BySelector for a resource-id across Compose testTags and XML IDs.
     */
    protected fun byRes(resourceId: String): BySelector {
        return By.res(java.util.regex.Pattern.compile(".*(:id/)?$resourceId$"))
    }

    fun scrollDown() {
        val width = device.displayWidth
        val height = device.displayHeight
        device.swipe(width / 2, (height * 0.8).toInt(), width / 2, (height * 0.2).toInt(), 40)
    }

    fun scrollUp() {
        val width = device.displayWidth
        val height = device.displayHeight
        device.swipe(width / 2, (height * 0.2).toInt(), width / 2, (height * 0.8).toInt(), 40)
    }

    /**
     * Finds a UI element using an explicit wait with Until before returning the UiObject2,
     * with automatic scrolling retry if not immediately in viewport.
     *
     * @param selector The BySelector to locate the element.
     * @param timeout The maximum duration in milliseconds to wait for the element.
     * @return The found UiObject2, or null if the element is not found within the timeout.
     */
    protected fun findAndWait(selector: BySelector, timeout: Long = DEFAULT_TIMEOUT): UiObject2? {
        val found = device.wait(Until.hasObject(selector), minOf(timeout, 2000L))
        if (found) {
            return device.findObject(selector)
        }
        try {
            // Try scrolling down to reveal elements below the fold
            scrollDown()
            if (device.wait(Until.hasObject(selector), minOf(timeout, 2000L))) {
                return device.findObject(selector)
            }
            // Try scrolling back up
            scrollUp()
            scrollUp()
            if (device.wait(Until.hasObject(selector), minOf(timeout, 2000L))) {
                return device.findObject(selector)
            }
        } catch (e: Exception) {
            // Ignore swipe failures
        }
        return if (device.wait(Until.hasObject(selector), timeout)) {
            device.findObject(selector)
        } else {
            null
        }
    }

    /**
     * Shared helper to perform the standard login flow.
     * Enters email and password, dismisses soft keyboard, and locates the login button.
     */
    fun login(email: String = testEmail, password: String = testPassword) {
        val emailField = findAndWait(byRes("login-email"))
            ?: throw AssertionError("Email input field 'login-email' not found")
        emailField.text = email

        val passwordField = findAndWait(byRes("login-password"))
            ?: throw AssertionError("Password input field 'login-password' not found")
        passwordField.text = password

        // Locate login button by stable resource-id or visible text "Log In"
        val loginButton = findAndWait(byRes("login-submit")) ?: findAndWait(By.text("Log In"))
            ?: throw AssertionError("Login button 'login-submit' / 'Log In' not found")
        loginButton.click()

        // Explicitly wait for destination Catalog screen to settle
        findAndWait(byRes("catalog-title"))
            ?: throw AssertionError("Catalog title 'catalog-title' not displayed after login")
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

        // Explicitly wait for Cart screen anchor to settle
        device.wait(Until.hasObject(byRes("order-total")), DEFAULT_TIMEOUT)
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

        // Explicitly wait for Checkout screen anchor
        device.wait(Until.hasObject(byRes("checkout-firstName")), DEFAULT_TIMEOUT)
    }
}
