"""TechShop Android - Checkout Test Suite (Appium).

Covers all CHECKOUT test cases (TC-CHK-001 through TC-CHK-009) defined in test-cases.md against com.techshop.android.

BLOCKER DEFECT NOTICE:
All test cases in this suite depend on transitioning from the Cart screen to the
Checkout screen. On the broken build, the "Proceed to Checkout" button is a no-op
due to BUG-011 (empty onClick lambda), which gates / blocks reaching the Checkout
screen via the UI. Full end-to-end verification of these tests succeeds on the fixed build.
"""

import pytest
from flows import navigate_to_checkout_flow
from pages.checkout_page import CheckoutPage


def test_tc_chk_001_e2e_successful_checkout(driver):
    """TC-CHK-001: End-to-end successful checkout submission (covers BUG-013).

    Blocked by BUG-011 on broken build; verify on fixed build.
    Fills all checkout fields with valid customer & payment details, submits order,
    and asserts transition to Confirmation screen with confirmation title and order reference.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="jane.doe@example.com",
        phone="5551234567",
        card="4111222233334444",
        expiry="12/28",
        cvv="123",
    )
    checkout_page.click_place_order()

    assert checkout_page.is_confirmation_screen_displayed(), (
        "Expected Confirmation screen after placing valid order."
    )


def test_tc_chk_002_empty_form_submission_rejection(driver):
    """TC-CHK-002: Rejection of empty checkout form submission (covers BUG-012).

    Blocked by BUG-011 on broken build; verify on fixed build.
    Leaves all checkout fields blank, clicks 'Place Order', and asserts submission
    is rejected with an error displayed in 'checkout-error'.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.click_place_order()

    assert checkout_page.is_error_displayed(), (
        "Expected validation error (checkout-error) for empty form submission (BUG-012)."
    )
    assert not checkout_page.is_confirmation_screen_displayed(), (
        "Expected order submission to be blocked when fields are empty."
    )


def test_tc_chk_003_expired_card_rejection(driver):
    """TC-CHK-003: Rejection of expired credit card date (covers BUG-009).

    Blocked by BUG-011 on broken build; verify on fixed build.
    Fills valid details with an expired date ('01/20'), submits order, and asserts
    past expiry date is rejected with an error displayed in 'checkout-error'.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="jane.doe@example.com",
        phone="5551234567",
        card="4111222233334444",
        expiry="01/20",  # Expired date
        cvv="123",
    )
    checkout_page.click_place_order()

    assert checkout_page.is_error_displayed(), (
        "Expected validation error (checkout-error) for expired card date (BUG-009)."
    )


def test_tc_chk_004_card_number_length_constraint(driver):
    """TC-CHK-004: Card number 16-digit length constraint.

    Blocked by BUG-011 on broken build; verify on fixed build.
    Attempts submission with 15 digits (rejected), then 16 digits (accepted).
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="jane.doe@example.com",
        phone="5551234567",
        card="411122223333444",  # 15 digits
        expiry="12/28",
        cvv="123",
    )
    checkout_page.click_place_order()

    assert checkout_page.is_error_displayed(), (
        "Expected validation error for 15-digit card number."
    )


def test_tc_chk_005_phone_number_length_constraint(driver):
    """TC-CHK-005: Phone number 10-digit length constraint.

    Blocked by BUG-011 on broken build; verify on fixed build.
    Attempts submission with 9 digits (rejected), then 10 digits (accepted).
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="jane.doe@example.com",
        phone="555123456",  # 9 digits
        card="4111222233334444",
        expiry="12/28",
        cvv="123",
    )
    checkout_page.click_place_order()

    assert checkout_page.is_error_displayed(), (
        "Expected validation error for 9-digit phone number."
    )


def test_tc_chk_006_cvv_numeric_and_length_constraint(driver):
    """TC-CHK-006: CVV numeric keypad type and 3-digit constraint (covers BUG-010).

    Blocked by BUG-011 on broken build; verify on fixed build.
    Attempts submission with invalid 2-digit CVV and verifies CVV requires exactly 3 digits.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="jane.doe@example.com",
        phone="5551234567",
        card="4111222233334444",
        expiry="12/28",
        cvv="12",  # 2 digits
    )
    checkout_page.click_place_order()

    assert checkout_page.is_error_displayed(), (
        "Expected validation error for 2-digit CVV (BUG-010)."
    )


def test_tc_chk_007_invalid_email_format_rejection(driver):
    """TC-CHK-007: Rejection of invalid email format on checkout.

    Blocked by BUG-011 on broken build; verify on fixed build.
    Enters malformed email on checkout form and asserts validation error is displayed.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="invalid-email-address",
        phone="5551234567",
        card="4111222233334444",
        expiry="12/28",
        cvv="123",
    )
    checkout_page.click_place_order()

    assert checkout_page.is_error_displayed(), (
        "Expected validation error for invalid email format."
    )


def test_tc_chk_008_ime_padding_cvv_visibility(driver):
    """TC-CHK-008: IME padding and field visibility above soft keyboard (covers BUG-017).

    Blocked by BUG-011 on broken build; verify on fixed build.
    Taps CVV field to open soft keyboard and verifies field remains accessible and visible.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    cvv_element = checkout_page.get_cvv_element()
    cvv_element.click()

    assert checkout_page.is_visible(checkout_page.CVV_INPUT), (
        "Expected CVV field to remain visible above keyboard with IME insets (BUG-017)."
    )


def test_tc_chk_009_order_reference_display_on_confirmation(driver):
    """TC-CHK-009: Prominent order reference display on Confirmation screen (covers BUG-013).

    Blocked by BUG-011 on broken build; verify on fixed build.
    Completes valid order submission and verifies order reference (TS-XXXXXX) is displayed.
    """
    checkout_page = navigate_to_checkout_flow(driver)
    assert checkout_page.is_on_checkout_screen(), (
        "Expected to reach Checkout screen (blocked by BUG-011 on broken build)."
    )

    checkout_page.fill_checkout_form(
        first_name="Jane",
        last_name="Doe",
        email="jane.doe@example.com",
        phone="5551234567",
        card="4111222233334444",
        expiry="12/28",
        cvv="123",
    )
    checkout_page.click_place_order()

    assert checkout_page.is_confirmation_screen_displayed(), (
        "Expected Confirmation screen after valid order."
    )
    # Order reference must start with TS-
    order_ref_visible = (
        checkout_page.is_visible(checkout_page.ORDER_REF_FALLBACK, timeout=3)
        or "TS-" in driver.page_source
    )
    assert order_ref_visible, (
        "Expected prominent order reference 'TS-XXXXXX' on confirmation screen (BUG-013)."
    )
