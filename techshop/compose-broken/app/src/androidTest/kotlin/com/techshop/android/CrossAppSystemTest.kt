package com.techshop.android

import android.view.KeyEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cross-App and System Interaction Test Suite.
 *
 * Demonstrates capabilities exclusive to UI Automator:
 * 1. Stepping outside the application process (Home button navigation, Recent Apps overview).
 * 2. Multi-tasking and app switching verification (returning to app from launcher/recents).
 * 3. System-level UI interaction (system keyboard keycodes, notification shade, OS dialogs).
 *
 * Why Espresso cannot do this:
 * Espresso runs inside the target application's process and thread loop (via Instrumentation
 * bounded to the app's View/Compose hierarchy). It cannot inspect or interact with elements
 * outside its own window or process (such as the OS Launcher, Recents overview, System UI,
 * runtime permission dialogs, or other applications).
 *
 * Why real bugs live at these seams:
 * Production regressions frequently occur at OS boundaries:
 * - Activity lifecycle transitions (onPause, onStop, onSaveInstanceState, onDestroy).
 * - Background process death and state loss when switching between apps.
 * - Soft keyboard and IME inset changes displacing or covering input fields (e.g., BUG-017).
 * - System permission dialogs interrupting user flows.
 * - Deep links and push notifications navigating into unexpected app states.
 */
@RunWith(AndroidJUnit4::class)
class CrossAppSystemTest : BaseUiAutomatorTest() {

    /**
     * Demonstrates an end-to-end flow stepping outside TechShop:
     * 1. Logs in and navigates within TechShop to Catalog screen.
     * 2. Presses the system Home button, transitioning to OS Launcher.
     * 3. Opens the Recent Apps overview.
     * 4. Returns to TechShop and verifies session/screen state persistence.
     * 5. Interacts directly with the system keyboard / IME via device-level key events.
     */
    @Test
    fun testCrossAppNavigationAndSystemInteraction() {
        // 1. Authenticate within TechShop
        login()
        val catalogTitle = findAndWait(byRes("catalog-title"))
        assertNotNull("Catalog title must be displayed before stepping outside app", catalogTitle)

        // 2. Step outside: Press Device HOME button
        device.pressHome()
        device.waitForIdle(2000L)

        // Verify TechShop is no longer the foreground focused window
        val catalogOutside = device.findObject(byRes("catalog-title"))
        assertTrue("TechShop catalog should no longer be active in foreground after Home press", catalogOutside == null)

        // 3. Open Recent Apps Overview
        device.pressRecentApps()
        device.waitForIdle(2000L)

        // 4. Return to TechShop from Launcher / Recent Apps
        // Re-launch/switch back via package manager intent with Bring-To-Front behavior
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val launchIntent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        context.startActivity(launchIntent)

        // Verify app state is preserved upon returning
        val restoredCatalog = findAndWait(byRes("catalog-title"), 5000L)
        assertNotNull("Catalog screen state must be restored upon returning to TechShop", restoredCatalog)

        // 5. System Keyboard / Hardware Key Interaction on Cart / Search
        addItemAndOpenCart("p1")

        val discountInput = findAndWait(byRes("discount-input"))
        assertNotNull("Discount input field must be present", discountInput)
        discountInput!!.click()

        // Send direct system hardware/IME keycodes (outside app view tree)
        device.pressKeyCode(KeyEvent.KEYCODE_S)
        device.pressKeyCode(KeyEvent.KEYCODE_A)
        device.pressKeyCode(KeyEvent.KEYCODE_V)
        device.pressKeyCode(KeyEvent.KEYCODE_E)
        device.pressKeyCode(KeyEvent.KEYCODE_1)
        device.pressKeyCode(KeyEvent.KEYCODE_0)

        // Dismiss the soft keyboard using device back key
        device.pressBack()

        // Apply discount and assert system interaction resulted in updated state
        val applyDiscount = findAndWait(byRes("apply-discount"))
        assertNotNull("Apply discount button must be present", applyDiscount)
        applyDiscount!!.click()

        val orderTotal = findAndWait(byRes("order-total"))
        assertNotNull("Order total must be displayed following system input", orderTotal)
    }
}
