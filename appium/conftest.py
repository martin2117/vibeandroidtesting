import os
from pathlib import Path
import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options
from dotenv import load_dotenv

# Load environment variables from .env in project root or local directory
env_path = Path(__file__).resolve().parent.parent / ".env"
if env_path.exists():
    load_dotenv(dotenv_path=env_path)
else:
    load_dotenv()

APPIUM_SERVER_URL = os.getenv("APPIUM_SERVER_URL", "http://127.0.0.1:4723")
APP_PACKAGE = os.getenv("APP_PACKAGE", "com.techshop.android")
APP_ACTIVITY = os.getenv("APP_ACTIVITY", ".MainActivity")
DEFAULT_EMAIL = os.getenv("TEST_EMAIL", "demo@techshop.com")
DEFAULT_PASSWORD = os.getenv("TEST_PASSWORD", "password123")


@pytest.fixture(scope="session")
def credentials():
    """Returns the default test credentials loaded from environment variables."""
    return {
        "email": os.getenv("TEST_EMAIL", DEFAULT_EMAIL),
        "password": os.getenv("TEST_PASSWORD", DEFAULT_PASSWORD),
    }


@pytest.fixture(scope="function")
def driver():
    """Initializes and returns an Appium UiAutomator2 driver with a fresh launch per test."""
    options = UiAutomator2Options()
    options.platform_name = "Android"
    options.automation_name = "UiAutomator2"
    options.app_package = APP_PACKAGE
    options.app_activity = APP_ACTIVITY
    options.no_reset = False
    options.new_command_timeout = 60
    options.set_capability("autoGrantPermissions", True)

    driver = webdriver.Remote(APPIUM_SERVER_URL, options=options)
    driver.implicitly_wait(0)  # Rely on explicit waits in BasePage

    yield driver

    try:
        driver.terminate_app(APP_PACKAGE)
    except Exception:
        pass
    finally:
        driver.quit()
