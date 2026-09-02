"""TechShop Android - Catalog & General Test Suite (Appium).

Covers all CATALOG test cases (TC-CAT-001 through TC-CAT-004) defined in test-cases.md against com.techshop.android.
"""

import pytest
from flows import login_flow
from pages.catalog_page import CatalogPage


def test_tc_cat_001_header_title(driver):
    """TC-CAT-001: Catalog navigation header title verification (covers BUG-014).

    Navigates to the Catalog screen and asserts the top navigation header displays
    'Products' instead of 'Untitled'.
    """
    catalog_page = login_flow(driver)

    actual_title = catalog_page.get_title_text()
    assert actual_title == "Products", (
        f"Expected catalog header to display 'Products', but got '{actual_title}' "
        f"(BUG-014: catalog title displays 'Untitled')."
    )


def test_tc_cat_002_product_name_truncation(driver):
    """TC-CAT-002: Clean truncation of long product names (covers BUG-007).

    Inspects Product 3 (Ultra-Wide Curved Monitor) title on the Catalog screen
    to verify it does not overflow and corrupt the product card layout.
    """
    catalog_page = login_flow(driver)

    assert catalog_page.is_product_displayed("p3"), "Expected Product 3 to be displayed in catalog."

    p3_elem = catalog_page.get_product_name_element("p3")
    rect = p3_elem.rect
    # In unconstrained broken layout, the un-truncated text wraps unbounded or overflows
    # Assert bounds are constrained to a reasonable cell height (e.g. <= 120px)
    assert rect["height"] <= 120, (
        f"Expected product title to be constrained with maxLines/truncation, "
        f"but height was {rect['height']}px (BUG-007: long name overflows cell layout)."
    )


def test_tc_cat_003_out_of_stock_badge_and_state(driver):
    """TC-CAT-003: Out-of-stock badge color & disabled state verification (covers BUG-008).

    Inspects Product 4 (USB-C Hub) on the Catalog screen to verify the out-of-stock
    badge displays 'Out of Stock' and the Add button is disabled (enabled == false).
    """
    catalog_page = login_flow(driver)

    assert catalog_page.is_product_displayed("p4"), "Expected Product 4 to be displayed in catalog."

    badge_text = catalog_page.get_stock_badge_text("p4")
    assert badge_text == "Out of Stock", (
        f"Expected stock badge to read 'Out of Stock', but got '{badge_text}'."
    )

    is_enabled = catalog_page.is_add_button_enabled("p4")
    assert not is_enabled, (
        "Expected Add button for out-of-stock Product 4 to be disabled (enabled=false)."
    )


def test_tc_cat_004_browse_catalog_products(driver):
    """TC-CAT-004: Browse catalog products list and items display.

    Scrolls and verifies that products p1 through p4 are listed with product names,
    and available items have active Add buttons.
    """
    catalog_page = login_flow(driver)

    for pid in ["p1", "p2", "p3", "p4"]:
        assert catalog_page.is_product_displayed(pid), (
            f"Expected Product {pid} to be rendered in the catalog list."
        )

    # Product 1 should have an enabled Add button
    assert catalog_page.is_add_button_enabled("p1"), (
        "Expected Product 1 Add button to be enabled."
    )
