import os
from typing import Optional
from selenium.webdriver.remote.webdriver import WebDriver
from pages.login_page import LoginPage
from pages.catalog_page import CatalogPage
from pages.cart_page import CartPage
from pages.checkout_page import CheckoutPage


def login_flow(
    driver: WebDriver,
    email: Optional[str] = None,
    password: Optional[str] = None,
    timeout: int = 10
) -> CatalogPage:
    """Executes a shared login flow navigating from Login to Catalog."""
    target_email = email or os.getenv("TEST_EMAIL", "demo@techshop.com")
    target_password = password or os.getenv("TEST_PASSWORD", "password123")

    login_page = LoginPage(driver, timeout=timeout)
    login_page.login(target_email, target_password)

    catalog_page = CatalogPage(driver, timeout=timeout)
    assert catalog_page.is_on_catalog_screen(), "Expected Catalog screen after successful login flow."
    return catalog_page


def add_item_and_open_cart(
    driver: WebDriver,
    product_id: str = "p1",
    email: Optional[str] = None,
    password: Optional[str] = None,
    timeout: int = 10
) -> CartPage:
    """Shared flow to authenticate, add a specified product to cart, and switch to Cart tab."""
    catalog_page = login_flow(driver, email=email, password=password, timeout=timeout)
    catalog_page.add_product_to_cart(product_id)
    catalog_page.click_cart_tab()
    cart_page = CartPage(driver, timeout=timeout)
    # Explicitly wait for cart screen anchor to ensure transition completes
    cart_page.find_present(cart_page.ORDER_TOTAL, timeout=timeout)
    return cart_page


# Alias for backward compatibility
add_to_cart_flow = add_item_and_open_cart


def navigate_to_checkout_flow(
    driver: WebDriver,
    product_id: str = "p1",
    email: Optional[str] = None,
    password: Optional[str] = None,
    timeout: int = 10
) -> CheckoutPage:
    """Shared flow to setup items in cart and attempt navigation to Checkout screen.

    Note: On the broken build, transition from Cart to Checkout is blocked by BUG-011
    (unresponsive 'Proceed to Checkout' button) and succeeds only on the fixed build.
    """
    cart_page = add_item_and_open_cart(driver, product_id=product_id, email=email, password=password, timeout=timeout)
    cart_page.click_proceed_to_checkout()
    return CheckoutPage(driver, timeout=timeout)
