from typing import List, Optional, Tuple
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException


def by_id(resource_id: str) -> Tuple[str, str]:
    """Helper creating a robust locator for resource-ids across Compose and React Native.

    Matches exact resourceId or package-prefixed resource-id (e.g. 'login-email'
    or 'com.techshop.android:id/login-email').
    """
    return (
        AppiumBy.ANDROID_UIAUTOMATOR,
        f'new UiSelector().resourceIdMatches(".*(:id/)?{resource_id}$")'
    )


def by_text(text: str) -> Tuple[str, str]:
    """Helper creating a locator for exact visible text."""
    return (
        AppiumBy.ANDROID_UIAUTOMATOR,
        f'new UiSelector().text("{text}")'
    )


class BasePage:
    """Base Page Object containing reusable locate and assert helpers for mobile testing."""

    def __init__(self, driver: WebDriver, timeout: int = 10):
        self.driver = driver
        self.timeout = timeout

    def find(self, locator: Tuple[str, str], timeout: Optional[int] = None) -> WebElement:
        """Finds a visible element on screen within timeout."""
        t = timeout if timeout is not None else self.timeout
        return WebDriverWait(self.driver, t).until(
            EC.visibility_of_element_located(locator),
            message=f"Element {locator} not visible after {t}s"
        )

    def find_present(self, locator: Tuple[str, str], timeout: Optional[int] = None) -> WebElement:
        """Finds an element present in the DOM/hierarchy within timeout."""
        t = timeout if timeout is not None else self.timeout
        return WebDriverWait(self.driver, t).until(
            EC.presence_of_element_located(locator),
            message=f"Element {locator} not present after {t}s"
        )

    def find_all(self, locator: Tuple[str, str], timeout: Optional[int] = None) -> List[WebElement]:
        """Finds all matching elements."""
        t = timeout if timeout is not None else self.timeout
        try:
            WebDriverWait(self.driver, t).until(EC.presence_of_element_located(locator))
            return self.driver.find_elements(*locator)
        except TimeoutException:
            return []

    def click(self, locator: Tuple[str, str], timeout: Optional[int] = None) -> None:
        """Waits for an element to be clickable and clicks it."""
        t = timeout if timeout is not None else self.timeout
        element = WebDriverWait(self.driver, t).until(
            EC.element_to_be_clickable(locator),
            message=f"Element {locator} not clickable after {t}s"
        )
        element.click()

    def type_text(self, locator: Tuple[str, str], text: str, clear: bool = True, timeout: Optional[int] = None) -> None:
        """Types text into an input field, optionally clearing existing content first."""
        element = self.find(locator, timeout=timeout)
        if clear:
            element.clear()
        element.send_keys(text)

    def get_text(self, locator: Tuple[str, str], timeout: Optional[int] = None) -> str:
        """Retrieves visible text from an element."""
        return self.find(locator, timeout=timeout).text

    def get_attribute(self, locator: Tuple[str, str], attribute: str, timeout: Optional[int] = None) -> Optional[str]:
        """Retrieves a specific attribute value from an element."""
        element = self.find_present(locator, timeout=timeout)
        return element.get_attribute(attribute)

    def is_visible(self, locator: Tuple[str, str], timeout: int = 3) -> bool:
        """Checks if an element is currently visible on screen."""
        try:
            self.find(locator, timeout=timeout)
            return True
        except (TimeoutException, NoSuchElementException):
            return False

    def is_present(self, locator: Tuple[str, str], timeout: int = 3) -> bool:
        """Checks if an element exists in the view hierarchy."""
        try:
            self.find_present(locator, timeout=timeout)
            return True
        except (TimeoutException, NoSuchElementException):
            return False

    def wait_until_invisible(self, locator: Tuple[str, str], timeout: Optional[int] = None) -> bool:
        """Waits until an element becomes invisible or absent."""
        t = timeout if timeout is not None else self.timeout
        return WebDriverWait(self.driver, t).until(
            EC.invisibility_of_element_located(locator)
        )

    def hide_keyboard(self) -> None:
        """Safely dismisses the soft keyboard if present to prevent UI obstruction."""
        try:
            self.driver.hide_keyboard()
        except Exception:
            pass

    # Assertion Helpers
    def assert_visible(self, locator: Tuple[str, str], message: Optional[str] = None, timeout: Optional[int] = None) -> None:
        """Asserts that the element is visible on screen."""
        assert self.is_visible(locator, timeout=timeout if timeout is not None else self.timeout), (
            message or f"Expected element {locator} to be visible on screen."
        )

    def assert_not_visible(self, locator: Tuple[str, str], message: Optional[str] = None, timeout: int = 3) -> None:
        """Asserts that the element is not visible on screen."""
        assert not self.is_visible(locator, timeout=timeout), (
            message or f"Expected element {locator} to NOT be visible on screen."
        )

    def assert_text(self, locator: Tuple[str, str], expected_text: str, message: Optional[str] = None) -> None:
        """Asserts that the element's text matches the expected value."""
        actual_text = self.get_text(locator)
        assert actual_text == expected_text, (
            message or f"Expected text '{expected_text}' for {locator}, but got '{actual_text}'."
        )

    def assert_attribute(self, locator: Tuple[str, str], attribute: str, expected_val: str, message: Optional[str] = None) -> None:
        """Asserts that a given attribute on the element matches the expected value."""
        actual_val = self.get_attribute(locator, attribute)
        assert actual_val == expected_val, (
            message or f"Expected attribute '{attribute}' of {locator} to be '{expected_val}', but got '{actual_val}'."
        )
