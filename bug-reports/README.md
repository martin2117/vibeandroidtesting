# TechShop Android — Bug Reports Index

This directory contains formal developer-ready bug reports for all 17 application and testability defects discovered across the test automation suites, formatted according to [`skills/bug-reporting.md`](file:///Users/mac/vibeandroidtesting/skills/bug-reporting.md).

## Bug Reports Manifest

| Bug ID | Title | Area | Severity | Cross-Build (Compose & RN) | File Link |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **`BUG-001`** | Password Input Displayed in Plaintext Without Character Masking | Login | `Critical` | Both | [`BUG-001.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-001.md) |
| **`BUG-002`** | Authentication Bypass When Submitting Empty Credentials Form | Login | `Critical` | Both | [`BUG-002.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-002.md) |
| **`BUG-003`** | Authentication Bypass When Submitting Invalid Password | Login | `Critical` | Both | [`BUG-003.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-003.md) |
| **`BUG-004`** | Faulty Percentage Discount Calculation Divides by 1000.0 Instead of 100.0 | Cart | `Major` | Both | [`BUG-004.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-004.md) |
| **`BUG-005`** | Quantity Stepper Allows Decrementing Below 1 to Zero and Negative Values | Cart | `Major` | Both | [`BUG-005.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-005.md) |
| **`BUG-006`** | Displayed Order Total Fails to Update Dynamically Due to Unkeyed State Memory | Cart | `Critical` | Both | [`BUG-006.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-006.md) |
| **`BUG-007`** | Long Product Title Exceeds Single Line and Overflows Cell Bounds | Catalog | `Minor` | Both | [`BUG-007.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-007.md) |
| **`BUG-008`** | Out of Stock Badge Incorrectly Styled in Green Color | Catalog | `Minor` | Both | [`BUG-008.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-008.md) |
| **`BUG-009`** | Payment Form Accepts Expired Credit Card Date Without Validation | Checkout | `Major` | Both | [`BUG-009.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-009.md) |
| **`BUG-010`** | CVV Field Opens QWERTY Text Keyboard and Accepts Non-Numeric Characters | Checkout | `Major` | Both | [`BUG-010.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-010.md) |
| **`BUG-011`** | "Proceed to Checkout" Button is Unresponsive (No-Op Empty Lambda) | Checkout | `Blocker` | Both | [`BUG-011.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-011.md) |
| **`BUG-012`** | Empty Checkout Form Submission Bypasses Required Field Validation | Checkout | `Major` | Both | [`BUG-012.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-012.md) |
| **`BUG-013`** | Order Confirmation Screen Omits Generated Order Reference Number | Checkout | `Major` | Both | [`BUG-013.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-013.md) |
| **`BUG-014`** | Top Navigation Bar Title Displays "Untitled" Instead of "Products" | Catalog | `Minor` | Both | [`BUG-014.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-014.md) |
| **`BUG-015`** | Bottom Navigation Tab Bar is Visible and Interactive Before Authentication | Login | `Major` | Both | [`BUG-015.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-015.md) |
| **`BUG-016`** | Primary Login Submit Button Missing Test Identifier (testTag / resource-id) | Login | `Major` | Both | [`BUG-016.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-016.md) |
| **`BUG-017`** | Soft Keyboard Covers Focused CVV Field Without IME Inset Padding | Checkout | `Major` | Both | [`BUG-017.md`](file:///Users/mac/vibeandroidtesting/bug-reports/BUG-017.md) |
