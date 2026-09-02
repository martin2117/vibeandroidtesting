from typing import Optional, Tuple
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.remote.webelement import WebElement
from .base_page import BasePage, by_id, by_text


class CheckoutPage(BasePage):
    """Page Object for TechShop Checkout and Order Confirmation Screens."""

    # Checkout Form Locators
    FIRST_NAME_INPUT = by_id("checkout-firstName")
    LAST_NAME_INPUT = by_id("checkout-lastName")
    EMAIL_INPUT = by_id("checkout-email")
    PHONE_INPUT = by_id("checkout-phone")
    CARD_INPUT = by_id("checkout-card")
    EXPIRY_INPUT = by_id("checkout-expiry")
    CVV_INPUT = by_id("checkout-cvv")
    SUBMIT_BUTTON = by_id("checkout-submit")
    ERROR_TEXT = by_id("checkout-error")

    # Confirmation Screen Locators
    CONFIRMATION_TITLE = by_id("confirmation-title")
    DONE_BUTTON = by_text("Done")
    ORDER_REF_FALLBACK = by_text("TS-")

    def __init__(self, driver: WebDriver, timeout: int = 10):
        super().__init__(driver, timeout)

    def is_on_checkout_screen(self, timeout: Optional[int] = None) -> bool:
        """Verifies if the user is on the Checkout screen."""
        return self.is_visible(self.FIRST_NAME_INPUT, timeout=timeout or self.timeout)

    def enter_first_name(self, first_name: str) -> "CheckoutPage":
        """Enters First Name."""
        self.type_text(self.FIRST_NAME_INPUT, first_name)
        return self

    def enter_last_name(self, last_name: str) -> "CheckoutPage":
        """Enters Last Name."""
        self.type_text(self.LAST_NAME_INPUT, last_name)
        return self

    def enter_email(self, email: str) -> "CheckoutPage":
        """Enters Email address."""
        self.type_text(self.EMAIL_INPUT, email)
        return self

    def enter_phone(self, phone: str) -> "CheckoutPage":
        """Enters Phone number."""
        self.type_text(self.PHONE_INPUT, phone)
        return self

    def enter_card(self, card: str) -> "CheckoutPage":
        """Enters Card Number."""
        self.type_text(self.CARD_INPUT, card)
        return self

    def enter_expiry(self, expiry: str) -> "CheckoutPage":
        """Enters Expiry Date (MM/YY)."""
        self.type_text(self.EXPIRY_INPUT, expiry)
        return self

    def enter_cvv(self, cvv: str) -> "CheckoutPage":
        """Enters CVV."""
        self.type_text(self.CVV_INPUT, cvv)
        return self

    def fill_checkout_form(
        self,
        first_name: str = "Jane",
        last_name: str = "Doe",
        email: str = "jane.doe@example.com",
        phone: str = "5551234567",
        card: str = "4111222233334444",
        expiry: str = "12/28",
        cvv: str = "123",
    ) -> "CheckoutPage":
        """Fills all fields in the checkout form."""
        if first_name:
            self.enter_first_name(first_name)
        if last_name:
            self.enter_last_name(last_name)
        if email:
            self.enter_email(email)
        if phone:
            self.enter_phone(phone)
        if card:
            self.enter_card(card)
        if expiry:
            self.enter_expiry(expiry)
        if cvv:
            self.enter_cvv(cvv)
        self.hide_keyboard()
        return self

    def click_place_order(self) -> None:
        """Clicks 'Place Order' submit button."""
        self.hide_keyboard()
        self.click(self.SUBMIT_BUTTON)

    def get_error_message(self) -> str:
        """Retrieves checkout error text."""
        return self.get_text(self.ERROR_TEXT)

    def is_error_displayed(self) -> bool:
        """Checks if checkout error text is displayed."""
        return self.is_visible(self.ERROR_TEXT, timeout=3)

    def is_confirmation_screen_displayed(self) -> bool:
        """Checks if Confirmation screen title is visible."""
        return self.is_visible(self.CONFIRMATION_TITLE, timeout=5)

    def get_confirmation_title_text(self) -> str:
        """Retrieves confirmation title text."""
        return self.get_text(self.CONFIRMATION_TITLE)

    def get_cvv_element(self) -> WebElement:
        """Returns the CVV input element."""
        return self.find(self.CVV_INPUT)

    def click_done(self) -> None:
        """Clicks 'Done' button on Confirmation screen."""
        self.click(self.DONE_BUTTON)
