from typing import Optional, Tuple
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.remote.webelement import WebElement
from .base_page import BasePage, by_id


class CatalogPage(BasePage):
    """Page Object for TechShop Catalog / Products Screen."""

    # Locators
    CATALOG_TITLE = by_id("catalog-title")
    TAB_PRODUCTS = by_id("tab-products")
    TAB_CART = by_id("tab-cart")

    def __init__(self, driver: WebDriver, timeout: int = 10):
        super().__init__(driver, timeout)

    @staticmethod
    def product_name_locator(product_id: str) -> Tuple[str, str]:
        """Returns the locator for a product name by product ID (e.g. 'p1')."""
        return by_id(f"name-{product_id}")

    @staticmethod
    def add_button_locator(product_id: str) -> Tuple[str, str]:
        """Returns the locator for a product Add button by product ID."""
        return by_id(f"add-{product_id}")

    @staticmethod
    def stock_badge_locator(product_id: str) -> Tuple[str, str]:
        """Returns the locator for a product stock badge (e.g. 'stock-p4')."""
        return by_id(f"stock-{product_id}")

    def is_on_catalog_screen(self, timeout: Optional[int] = None) -> bool:
        """Verifies if the catalog screen header is visible."""
        return self.is_visible(self.CATALOG_TITLE, timeout=timeout or self.timeout)

    def get_title_text(self) -> str:
        """Retrieves the text of the catalog header."""
        return self.get_text(self.CATALOG_TITLE)

    def add_product_to_cart(self, product_id: str = "p1") -> "CatalogPage":
        """Clicks the Add button for a specific product."""
        self.click(self.add_button_locator(product_id))
        return self

    def click_cart_tab(self) -> None:
        """Navigates to the Cart screen via the bottom navigation tab."""
        self.click(self.TAB_CART)

    def click_products_tab(self) -> None:
        """Navigates to the Products/Catalog screen via the bottom navigation tab."""
        self.click(self.TAB_PRODUCTS)

    def is_product_displayed(self, product_id: str) -> bool:
        """Checks if a product is displayed in the catalog."""
        return self.is_visible(self.product_name_locator(product_id))

    def get_product_name_text(self, product_id: str) -> str:
        """Retrieves text of product title."""
        return self.get_text(self.product_name_locator(product_id))

    def get_product_name_element(self, product_id: str) -> WebElement:
        """Returns the WebElement for a product name."""
        return self.find(self.product_name_locator(product_id))

    def get_stock_badge_element(self, product_id: str) -> WebElement:
        """Returns the WebElement for a product stock badge."""
        return self.find(self.stock_badge_locator(product_id))

    def get_stock_badge_text(self, product_id: str) -> str:
        """Retrieves the stock badge text."""
        return self.get_text(self.stock_badge_locator(product_id))

    def is_add_button_enabled(self, product_id: str) -> bool:
        """Checks if Add button is enabled."""
        element = self.find(self.add_button_locator(product_id))
        attr_enabled = element.get_attribute("enabled")
        if attr_enabled is not None:
            return attr_enabled.lower() == "true"
        return element.is_enabled()
