# Skill: Mobile Test Authoring Standards (TechShop)

## Purpose & Scope
This skill defines the instructions and coding standards for authoring mobile automated tests across all supported frameworks (**Maestro**, **Appium**, **Espresso**, and **UI Automator**) against the TechShop Android app (`com.techshop.android`).

Whenever tasked with writing, refactoring, or generating automated test suites from test cases, follow the universal rules and framework-specific standards defined below.

---

## Universal Rules (All Frameworks)

1. **Locators & Hierarchy**:
   - **Primary**: Always prefer stable `resource-id`s. On Jetpack Compose, these derive from `Modifier.testTag(...)` exposed via `semantics { testTagsAsResourceId = true }`. On React Native, these derive from `testID`. Both surface as standard Android resource-ids.
   - **Fallback**: Use visible text only when no ID exists in the build under test (e.g., the Login button in the broken build lacks an ID due to `BUG-016`, so locate it by text `"Log In"`).
   - **Forbidden**: Never locate elements by position, index, or fragile absolute hierarchy paths.
   - **Defect Flagging**: If a critical interactive element lacks an ID, note it as a testability defect and recommend adding a test tag.

2. **Cross-Build Compatibility**:
   - Tests must run interchangeably against both Native Jetpack Compose and React Native builds using the single package identifier `com.techshop.android`.

3. **Rigorous Assertions**:
   - Every test must assert verifiable outcomes: element visibility, text/value accuracy, component state (enabled/disabled), or specific element attributes.
   - Never write shallow assertions that only check "the app didn't crash".

4. **Test Isolation & State**:
   - Every test must launch fresh and execute independently in any arbitrary order.
   - Clear app state / session data on startup to prevent cross-test contamination.

5. **Secrets & Credentials**:
   - Never hardcode credentials in test files. Load credentials from environment variables (`TEST_EMAIL`, `TEST_PASSWORD`) or test runners/configs.

6. **Timing & Synchronization**:
   - **Zero Manual Sleeps**: Never use arbitrary `sleep`, `time.sleep()`, or `Thread.sleep()`.
   - Always rely on framework-native explicit waits, conditions, or UI thread synchronization.

7. **Modularity & Reusability**:
   - Extract common multi-step setup flows (e.g., standard login, adding a product to cart, navigating to checkout) into reusable helpers, subflows, page actions, or base test classes.

8. **Technical Honesty & Framework Delegation**:
   - **Acknowledge Limitations**: Black-box tools (Maestro, UI Automator) cannot inspect hidden view attributes such as masked password input types (`BUG-001`) or UI render colors (`BUG-008`).
   - Rather than writing weak or false assertions, explicitly document the limitation and defer attribute-level validation cases to **Appium** or **Espresso**.

---

## Framework Best Practices

### 1. Maestro (YAML Flows)
*Declarative mobile UI workflows located under `maestro/`*

- **File Structure**:
  - Main test flows: `maestro/flows/<feature>_<test_name>.yaml`
  - Reusable subflows: `maestro/subflows/<action>.yaml` (invoked via `runFlow: ../subflows/<action>.yaml`)
- **App Configuration**:
  - Always set `appId: com.techshop.android` at the root of the flow.
  - Launch with state reset:
    ```yaml
    - launchApp:
        clearState: true
    ```
- **Locators & Actions**:
  - Locate by resource-id: `id: "login-email"` or visible text: `"Log In"`.
  - Assertions: Use `assertVisible: { id: "catalog-title" }`, `assertNotVisible: ...`, or regex matches `text: ".*120.*"`.
- **Parameters & Waits**:
  - Inject credentials via CLI parameters: `${EMAIL}` / `${PASSWORD}` (passed with `maestro test -e EMAIL=... -e PASSWORD=...`).
  - Use `extendedWaitUntil: { visible: "...", timeout: 10000 }` instead of fixed delays.
  - Keep flows purely declarative without complex logic. If a case requires attribute inspection (e.g., masked input type), hand it off to Appium or Espresso.

---

### 2. Appium (Python + pytest + UiAutomator2)
*Page Object Model (POM) testing located under `appium/`*

- **Directory Layout**:
  - `appium/conftest.py`: Driver setup/teardown fixture with capabilities (`automationName: "UiAutomator2"`, `appPackage: "com.techshop.android"`, `appActivity`), reading credentials from `os.environ`.
  - `appium/pages/`: Dedicated Page Object classes per screen (e.g., `login_page.py`, `catalog_page.py`, `cart_page.py`, `checkout_page.py`).
  - `appium/flows.py` / `helpers.py`: Common multi-screen flows (e.g., `login_as_demo_user()`).
  - `appium/tests/`: Pytest test modules (e.g., `test_login.py`, `test_cart.py`).
  - `appium/pytest.ini` & `appium/requirements.txt`: Configuration and dependencies.
- **Locators & Inspection**:
  - Use `AppiumBy.ID` for resource-ids, `AppiumBy.ACCESSIBILITY_ID` for content descriptions, and `AppiumBy.ANDROID_UIAUTOMATOR` for text matching.
  - Inspect element attributes directly when required:
    ```python
    assert password_field.get_attribute("password") == "true"  # Verifies BUG-001
    ```
- **Structure & Synchronization**:
  - Keep Page Objects thin: encapsulate element locators and user interactions; keep assertions inside test functions.
  - Use `WebDriverWait(driver, timeout).until(EC.visibility_of_element_located(...))` — never `time.sleep()`.

---

### 3. Espresso (Kotlin + AndroidX Test)
*In-app / white-box instrumented tests located under `espresso/`*

- **Architecture & Source Set**:
  - Place instrumented tests in the `androidTest` source set under `espresso/src/androidTest/kotlin/...`.
  - Use a common base class (`BaseEspressoTest`) that initializes the `ActivityScenarioRule` or Compose `createAndroidComposeRule`, retrieves credentials from `BuildConfig` / environment, and houses shared helpers (`login()`, `addItemAndOpenCart()`).
  - Create one test class per feature area (`LoginTest.kt`, `CartTest.kt`, `CheckoutTest.kt`).
- **Classic Views vs. Jetpack Compose**:
  - **View Matchers**: `onView(withId(R.id.login_email)).perform(typeText(...))` and `.check(matches(isDisplayed()))`.
  - **Compose Semantics**: `composeTestRule.onNodeWithTag("login-email").performTextInput(...)`.
  - **Attribute & Masking Validation**: Validate password masking via input type matchers (`withInputType(...)`) or Compose semantics properties (`SemanticsProperties.PasswordVisualTransformation`).
- **Synchronization & Execution**:
  - Rely on Espresso's native UI thread idle synchronization. For background async operations, register an `IdlingResource` (do not write loop polls).
  - Execute suites via Gradle: `./gradlew connectedAndroidTest`.
  - Treat Espresso Test Recorder output as a draft: refactor fragile position-based locators into robust test tags.

---

### 4. UI Automator (Kotlin + AndroidX Test)
*Black-box and cross-app instrumented tests located under `uiautomator/`*

- **Architecture**:
  - Place tests under `uiautomator/src/androidTest/kotlin/...`.
  - Use a base class (`BaseUiAutomatorTest`) obtaining `UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())`, providing helper methods like `findAndWait(BySelector, timeout)`, `login()`, and `addItemAndOpenCart()`.
  - Launch app by package name (`com.techshop.android`) via explicit Intent with `FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK` before each test.
- **Locators & Waits**:
  - Locate with `By.res("com.techshop.android", "login-email")`, `By.desc("...")`, or `By.text("...")`. Never locate by screen coordinates or child indices.
  - Always synchronize before interacting: `device.wait(Until.hasObject(By.res(...)), timeout)` or `device.wait(Until.findObject(...), timeout)`.
- **Scope & Cross-App Scenarios**:
  - Handle black-box limits: Defer masked text / color attribute assertions to Appium/Espresso.
  - Leverage UI Automator's unique ability to step outside the application: use it for OS dialogs, permissions, Home screen transitions, recents navigation, and system notifications.
