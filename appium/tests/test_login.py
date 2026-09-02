"""TechShop Android - Login Test Suite (Appium).

Covers all LOGIN test cases defined in test-cases.md against com.techshop.android.
"""

import pytest
from pages.login_page import LoginPage
from pages.catalog_page import CatalogPage


def test_tc_login_001_valid_credentials(driver, credentials):
    """TC-LOGIN-001: Successful authentication with valid credentials.

    Enters valid demo credentials, clicks 'Log In', and asserts navigation to
    the Product Catalog screen with catalog header visible.
    """
    login_page = LoginPage(driver)
    catalog_page = CatalogPage(driver)

    login_page.enter_email(credentials["email"])
    login_page.enter_password(credentials["password"])
    login_page.click_login()

    assert catalog_page.is_on_catalog_screen(), (
        "Expected navigation to Product Catalog screen after valid login."
    )


def test_tc_login_002_empty_credentials_rejection(driver):
    """TC-LOGIN-002: Rejection of empty credentials submission (covers BUG-002).

    Leaves credentials empty, clicks 'Log In', and asserts form submission
    is rejected with an inline error displayed on the Login screen.
    """
    login_page = LoginPage(driver)
    catalog_page = CatalogPage(driver)

    login_page.click_login()

    assert login_page.is_error_displayed(), (
        "Expected inline error message (login-error) when submitting empty credentials."
    )
    assert not catalog_page.is_on_catalog_screen(timeout=2), (
        "Expected app to remain on Login screen, but navigated to Catalog (BUG-002)."
    )


def test_tc_login_003_invalid_password_rejection(driver, credentials):
    """TC-LOGIN-003: Rejection of invalid password credentials (covers BUG-003).

    Enters valid email with wrong password, clicks 'Log In', and asserts authentication
    fails with an error message displayed on the Login screen.
    """
    login_page = LoginPage(driver)
    catalog_page = CatalogPage(driver)

    login_page.enter_email(credentials["email"])
    login_page.enter_password("wrongpassword123")
    login_page.click_login()

    assert login_page.is_error_displayed(), (
        "Expected error message (login-error) when submitting invalid password."
    )
    assert not catalog_page.is_on_catalog_screen(timeout=2), (
        "Expected app to remain on Login screen, but navigated to Catalog (BUG-003)."
    )


def test_tc_login_004_invalid_email_format_rejection(driver, credentials):
    """TC-LOGIN-004: Rejection of invalid email format.

    Enters malformed email address and valid password, clicks 'Log In', and asserts
    format validation error is displayed and user remains on Login screen.
    """
    login_page = LoginPage(driver)
    catalog_page = CatalogPage(driver)

    login_page.enter_email("invalid-email-format")
    login_page.enter_password(credentials["password"])
    login_page.click_login()

    assert login_page.is_error_displayed(), (
        "Expected format validation error (login-error) for invalid email format."
    )
    assert not catalog_page.is_on_catalog_screen(timeout=2), (
        "Expected user to remain on Login screen."
    )


def test_tc_login_005_password_input_masking(driver, credentials):
    """TC-LOGIN-005: Password input masking verification (covers BUG-001).

    Inspects the 'password' attribute of the password input element to ensure
    characters are visually masked (password == 'true') via PasswordVisualTransformation.
    Deferred from Maestro because Maestro cannot inspect accessibility node attributes.
    """
    login_page = LoginPage(driver)

    login_page.enter_password(credentials["password"])
    password_element = login_page.get_password_element()

    # Direct attribute inspection via Appium / UiAutomator2
    is_masked = password_element.get_attribute("password")
    assert is_masked == "true", (
        f"Expected password field to have masked input attribute (password='true'), "
        f"but got password='{is_masked}' (BUG-001: plaintext password entry)."
    )


def test_tc_login_006_unauthenticated_tab_bar_gating(driver):
    """TC-LOGIN-006: Unauthenticated navigation tab bar gating (covers BUG-015).

    Inspects screen layout on launch before authentication to verify bottom
    navigation tabs (Products, Cart) are hidden or inaccessible until login.
    """
    login_page = LoginPage(driver)

    assert not login_page.is_tab_products_visible(), (
        "Expected 'tab-products' to be hidden on Login screen before authentication (BUG-015)."
    )
    assert not login_page.is_tab_cart_visible(), (
        "Expected 'tab-cart' to be hidden on Login screen before authentication (BUG-015)."
    )


def test_tc_login_007_session_persistence(driver, credentials):
    """TC-LOGIN-007: Session persistence across in-app navigation.

    Authenticates successfully, switches to 'Cart' tab, switches back to
    'Products' tab, and verifies session persists without re-prompting login.
    """
    login_page = LoginPage(driver)
    catalog_page = CatalogPage(driver)

    login_page.enter_email(credentials["email"])
    login_page.enter_password(credentials["password"])
    login_page.click_login()

    assert catalog_page.is_on_catalog_screen(), "Expected Catalog screen after login."

    catalog_page.click_cart_tab()
    catalog_page.click_products_tab()

    assert catalog_page.is_on_catalog_screen(), (
        "Expected Catalog screen to remain visible after tab navigation."
    )
    assert not login_page.is_on_login_screen(), (
        "Expected user session to persist without returning to Login screen."
    )


def test_tc_login_008_login_button_testability_identifier(driver):
    """TC-LOGIN-008: Primary login submit button testability identifier (covers BUG-016).

    Locates the 'Log In' button by visible text and inspects its 'resource-id' attribute
    to verify it exposes a dedicated testTag / resource-id (e.g., 'login-submit').
    """
    login_page = LoginPage(driver)

    # Locate the button by text and inspect its resource-id attribute
    login_button = login_page.get_login_button_element()
    resource_id = login_button.get_attribute("resource-id") or ""

    has_valid_id = "login-submit" in resource_id or login_page.is_present(login_page.LOGIN_SUBMIT_ID, timeout=2)
    assert has_valid_id, (
        f"Expected 'Log In' button to expose a dedicated resource-id / testTag ('login-submit'), "
        f"but got resource-id='{resource_id}' (BUG-016: missing testTag on Log In button)."
    )

