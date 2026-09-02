from typing import Optional, Tuple
from selenium.webdriver.remote.webdriver import WebDriver
from .base_page import BasePage, by_id, by_text


class CartPage(BasePage):
    """Page Object for TechShop Cart Screen."""

    # Locators
    CART_EMPTY = by_id("cart-empty")
    ORDER_TOTAL = by_id("order-total")
    MIN_ORDER_ERROR = by_id("min-order-error")
    DISCOUNT_INPUT = by_id("discount-input")
    APPLY_DISCOUNT_BUTTON = by_id("apply-discount")
    PROCEED_CHECKOUT_BUTTON = by_id("proceed-checkout")
    CHECKOUT_FIRST_NAME = by_id("checkout-firstName")
    TAB_PRODUCTS = by_id("tab-products")
    TAB_CART = by_id("tab-cart")

    def __init__(self, driver: WebDriver, timeout: int = 10):
        super().__init__(driver, timeout)

    @staticmethod
    def item_name_locator(product_id: str) -> Tuple[str, str]:
        """Returns locator for product name in cart/catalog."""
        return by_id(f"name-{product_id}")

    @staticmethod
    def qty_locator(product_id: str) -> Tuple[str, str]:
        """Returns locator for item quantity text."""
        return by_id(f"qty-{product_id}")

    @staticmethod
    def qty_inc_locator(product_id: str) -> Tuple[str, str]:
        """Returns locator for quantity increment '+' button."""
        return by_id(f"qty-inc-{product_id}")

    @staticmethod
    def qty_dec_locator(product_id: str) -> Tuple[str, str]:
        """Returns locator for quantity decrement '−' button."""
        return by_id(f"qty-dec-{product_id}")

    @staticmethod
    def remove_locator(product_id: str) -> Tuple[str, str]:
        """Returns locator for item 'Remove' button."""
        return by_id(f"remove-{product_id}")

    def is_cart_empty_displayed(self) -> bool:
        """Checks if the empty cart placeholder message is displayed."""
        return self.is_visible(self.CART_EMPTY, timeout=5)

    def get_cart_empty_text(self) -> str:
        """Retrieves the empty cart placeholder text."""
        return self.get_text(self.CART_EMPTY)

    def get_order_total_text(self) -> str:
        """Retrieves the order total text (e.g. 'Total: $60.00')."""
        return self.get_text(self.ORDER_TOTAL)

    def get_item_quantity(self, product_id: str) -> str:
        """Retrieves quantity string for specified product."""
        return self.get_text(self.qty_locator(product_id))

    def increment_quantity(self, product_id: str) -> "CartPage":
        """Clicks '+' button to increment quantity."""
        self.click(self.qty_inc_locator(product_id))
        return self

    def decrement_quantity(self, product_id: str) -> "CartPage":
        """Clicks '−' button to decrement quantity."""
        self.click(self.qty_dec_locator(product_id))
        return self

    def remove_item(self, product_id: str) -> "CartPage":
        """Clicks 'Remove' button for specified product."""
        self.click(self.remove_locator(product_id))
        return self

    def apply_discount(self, code: str) -> "CartPage":
        """Fills discount code input and clicks Apply."""
        self.type_text(self.DISCOUNT_INPUT, code)
        self.hide_keyboard()
        self.click(self.APPLY_DISCOUNT_BUTTON)
        return self

    def click_proceed_to_checkout(self) -> None:
        """Clicks 'Proceed to Checkout' button."""
        self.click(self.PROCEED_CHECKOUT_BUTTON)

    def is_min_order_error_displayed(self) -> bool:
        """Checks if minimum order value error banner is displayed."""
        return self.is_visible(self.MIN_ORDER_ERROR, timeout=3)

    def is_item_present(self, product_id: str) -> bool:
        """Checks if a product's row/name is present in the cart."""
        return self.is_present(self.qty_locator(product_id), timeout=3)

    def is_checkout_screen_reached(self) -> bool:
        """Checks if navigation reached Checkout screen (e.g., checkout-firstName)."""
        return self.is_visible(self.CHECKOUT_FIRST_NAME, timeout=3)
