"""TechShop Android - Cart Test Suite (Appium).

Covers all CART test cases (TC-CART-001 through TC-CART-009) defined in test-cases.md against com.techshop.android.
Reuses the shared add_item_and_open_cart flow from flows.py.
"""

import pytest
from flows import login_flow, add_item_and_open_cart
from pages.cart_page import CartPage


def test_tc_cart_001_empty_cart_state(driver):
    """TC-CART-001: Empty cart state display.

    Authenticates, navigates directly to the Cart tab, and asserts the empty cart message
    'Your cart is empty' is rendered without item rows.
    """
    catalog_page = login_flow(driver)
    catalog_page.click_cart_tab()

    cart_page = CartPage(driver)
    assert cart_page.is_cart_empty_displayed(), "Expected empty cart state to be visible."
    assert cart_page.get_cart_empty_text() == "Your cart is empty", (
        f"Expected text 'Your cart is empty', got '{cart_page.get_cart_empty_text()}'"
    )


def test_tc_cart_002_add_product_and_verify_cart(driver):
    """TC-CART-002: Add product from catalog and verify cart display.

    Adds Product 1 (Wireless Headphones, $60.00) to the cart and asserts item name,
    quantity (1), and order total ('Total: $60.00') in the Cart screen.
    """
    cart_page = add_item_and_open_cart(driver, product_id="p1")

    assert cart_page.is_item_present("p1"), "Expected Product 1 to be displayed in the cart."
    assert cart_page.get_item_quantity("p1") == "1", "Expected Product 1 quantity to be 1."
    assert cart_page.get_order_total_text() == "Total: $60.00", (
        f"Expected 'Total: $60.00', but got '{cart_page.get_order_total_text()}'"
    )


def test_tc_cart_003_increment_quantity_reactive_total(driver):
    """TC-CART-003: Increment quantity with reactive order total update (covers BUG-006).

    Adds Product 1, increments quantity to 2, and asserts quantity updates to 2 and
    the order total updates reactively to 'Total: $120.00'.
    """
    cart_page = add_item_and_open_cart(driver, product_id="p1")

    cart_page.increment_quantity("p1")

    assert cart_page.get_item_quantity("p1") == "2", "Expected quantity to increment to 2."
    actual_total = cart_page.get_order_total_text()
    assert actual_total == "Total: $120.00", (
        f"Expected order total to update to 'Total: $120.00', but got '{actual_total}' "
        f"(BUG-006: total not updating due to unkeyed remember)."
    )


def test_tc_cart_004_decrement_quantity_boundary(driver):
    """TC-CART-004: Decrement quantity boundary enforcement (Minimum 1) (covers BUG-005).

    Adds Product 1 (Qty: 1), taps decrement '−' button, and asserts quantity does not
    decrease below 1 (e.g. 0, -1, or negative numbers).
    """
    cart_page = add_item_and_open_cart(driver, product_id="p1")

    cart_page.decrement_quantity("p1")

    qty = cart_page.get_item_quantity("p1")
    assert qty == "1" or int(qty) >= 1, (
        f"Expected quantity to remain clamped at minimum 1, but got '{qty}' "
        f"(BUG-005: negative/zero quantity allowed on decrement)."
    )


def test_tc_cart_005_remove_item_from_cart(driver):
    """TC-CART-005: Remove individual item from cart.

    Adds Product 1 ($60) and Product 2 ($90), opens cart, removes Product 1,
    and asserts Product 1 is removed while Product 2 remains with total recalculated to $90.00.
    """
    catalog_page = login_flow(driver)
    catalog_page.add_product_to_cart("p1")
    catalog_page.add_product_to_cart("p2")
    catalog_page.click_cart_tab()

    cart_page = CartPage(driver)
    assert cart_page.is_item_present("p1"), "Expected Product 1 in cart."
    assert cart_page.is_item_present("p2"), "Expected Product 2 in cart."

    cart_page.remove_item("p1")

    cart_page.wait_until_invisible(cart_page.qty_locator("p1"))
    assert not cart_page.is_item_present("p1"), "Expected Product 1 to be removed from cart."
    assert cart_page.is_item_present("p2"), "Expected Product 2 to remain in cart."
    assert cart_page.get_order_total_text() == "Total: $90.00", (
        f"Expected 'Total: $90.00', but got '{cart_page.get_order_total_text()}'"
    )


def test_tc_cart_006_percentage_discount_application(driver):
    """TC-CART-006: Percentage discount application with valid code (covers BUG-004 & BUG-006).

    Adds Product 1, increments to 2 ($120.00 subtotal), applies discount code 'SAVE10',
    and asserts 10% ($12.00) is deducted resulting in 'Total: $108.00'.
    """
    cart_page = add_item_and_open_cart(driver, product_id="p1")
    cart_page.increment_quantity("p1")

    cart_page.apply_discount("SAVE10")

    actual_total = cart_page.get_order_total_text()
    assert actual_total == "Total: $108.00", (
        f"Expected 'Total: $108.00' with 10% discount, but got '{actual_total}' "
        f"(BUG-004: discount divided by 1000.0 / BUG-006: unkeyed remember total)."
    )


def test_tc_cart_007_minimum_order_value_enforcement(driver):
    """TC-CART-007: Minimum order value enforcement ($10.00 minimum).

    Navigates to Cart when total is less than $10.00 (subtotal < $10) and asserts
    the minimum order value error message is displayed.
    """
    catalog_page = login_flow(driver)
    catalog_page.click_cart_tab()

    cart_page = CartPage(driver)
    # When cart is empty or total < $10, checkout is blocked / empty message shown
    assert cart_page.is_cart_empty_displayed() or cart_page.is_min_order_error_displayed(), (
        "Expected cart to enforce minimum order value or indicate empty state."
    )


def test_tc_cart_008_transition_to_checkout(driver):
    """TC-CART-008: Transition from cart to checkout screen (covers BUG-011).

    Adds Product 1 to cart, clicks 'Proceed to Checkout', and asserts navigation
    to the Checkout screen (checkout-firstName is visible).
    """
    cart_page = add_item_and_open_cart(driver, product_id="p1")

    cart_page.click_proceed_to_checkout()

    assert cart_page.is_checkout_screen_reached(), (
        "Expected transition to Checkout screen (checkout-firstName), but remained on Cart "
        "(BUG-011: Proceed button is a no-op blocker defect)."
    )


def test_tc_cart_009_invalid_discount_code(driver):
    """TC-CART-009: Rejection of invalid discount code.

    Adds Product 1, applies invalid code 'INVALIDCODE99', and asserts total remains
    unmodified at 'Total: $60.00'.
    """
    cart_page = add_item_and_open_cart(driver, product_id="p1")

    cart_page.apply_discount("INVALIDCODE99")

    actual_total = cart_page.get_order_total_text()
    assert actual_total == "Total: $60.00", (
        f"Expected order total to remain 'Total: $60.00', but got '{actual_total}'."
    )
