from typing import Optional, Tuple
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.remote.webelement import WebElement
from .base_page import BasePage, by_id, by_text


class LoginPage(BasePage):
    """Page Object for TechShop Login Screen."""

    # Locators
    EMAIL_INPUT = by_id("login-email")
    PASSWORD_INPUT = by_id("login-password")
    ERROR_TEXT = by_id("login-error")
    # Per instructions and TC-LOGIN-001/TC-LOGIN-008: locate login button by text "Log In"
    LOGIN_BUTTON = by_text("Log In")
    LOGIN_SUBMIT_ID = by_id("login-submit")
    TAB_PRODUCTS = by_id("tab-products")
    TAB_CART = by_id("tab-cart")
    SUBTITLE = by_text("Sign in to continue")

    def __init__(self, driver: WebDriver, timeout: int = 10):
        super().__init__(driver, timeout)

    def is_on_login_screen(self) -> bool:
        """Verifies if the user is on the Login Screen."""
        return self.is_visible(self.EMAIL_INPUT, timeout=5) or self.is_visible(self.SUBTITLE, timeout=5)

    def enter_email(self, email: str) -> "LoginPage":
        """Types the email address into the email input field."""
        self.type_text(self.EMAIL_INPUT, email)
        return self

    def enter_password(self, password: str) -> "LoginPage":
        """Types the password into the password input field."""
        self.type_text(self.PASSWORD_INPUT, password)
        return self

    def click_login(self) -> None:
        """Clicks the 'Log In' submit button located by text 'Log In'."""
        self.hide_keyboard()
        self.click(self.LOGIN_BUTTON)

    def login(self, email: str, password: str) -> None:
        """Performs full login sequence by filling credentials and clicking Log In."""
        if email:
            self.enter_email(email)
        if password:
            self.enter_password(password)
        self.hide_keyboard()
        self.click_login()

    def get_error_message(self) -> str:
        """Retrieves the text displayed in the login error message box."""
        return self.get_text(self.ERROR_TEXT)

    def is_error_displayed(self) -> bool:
        """Checks whether the login error text is visible."""
        return self.is_visible(self.ERROR_TEXT, timeout=3)

    def is_tab_products_visible(self) -> bool:
        """Checks if the Products tab is visible."""
        return self.is_visible(self.TAB_PRODUCTS, timeout=3)

    def is_tab_cart_visible(self) -> bool:
        """Checks if the Cart tab is visible."""
        return self.is_visible(self.TAB_CART, timeout=3)

    def get_password_element(self) -> WebElement:
        """Returns the password input WebElement for direct inspection."""
        return self.find(self.PASSWORD_INPUT)

    def get_login_button_element(self) -> WebElement:
        """Returns the login button WebElement located by text 'Log In'."""
        return self.find(self.LOGIN_BUTTON)
