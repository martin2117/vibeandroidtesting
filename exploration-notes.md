# Exploration Notes

## Login

### UI Elements & Locators

| Element | Resource-ID / Test Tag | Class / Type | Notes |
| :--- | :--- | :--- | :--- |
| Screen Title | *None* | `android.widget.TextView` | Text: `"TechShop"` |
| Subtitle | *None* | `android.widget.TextView` | Text: `"Sign in to continue"` |
| Email Input Field | `login-email` | `android.widget.EditText` | Placeholder / label: `"Email"` |
| Password Input Field | `login-password` | `android.widget.EditText` | Placeholder / label: `"Password"` |
| Error Message Box | `login-error` | `android.widget.TextView` | Conditional; only renders if error is non-empty |
| **Log In Button** | **NONE (Missing ID)** | `android.widget.Button` | **FLAGGED**: Missing `testTag` / `resource-id` (BUG-016). Only locatable via text `"Log In"` or coordinates |
| Products Bottom Tab | `tab-products` | `android.view.View` | Accessible in bottom navigation bar |
| Cart Bottom Tab | `tab-cart` | `android.view.View` | Accessible in bottom navigation bar |

---

### Flows Executed

1. **Empty Submit**:
   - **Input**: Both Email and Password fields left blank.
   - **Action**: Clicked "Log In" button.
   - **Actual Result**: Directly logged in and transitioned to the Catalog / Products screen (`catalog-title`). No error message appeared.

2. **Wrong Password**:
   - **Input**: Email = `demo@techshop.com`, Password = `wrongpassword123`.
   - **Action**: Clicked "Log In" button.
   - **Actual Result**: Password typed in plaintext. Clicked "Log In" and immediately transitioned to the Catalog / Products screen. No error message appeared.

3. **Valid Login**:
   - **Input**: Email = `demo@techshop.com`, Password = `password123`.
   - **Action**: Clicked "Log In" button.
   - **Actual Result**: Successfully transitioned to the Catalog / Products screen showing product list items (`name-p1`, `name-p2`, etc.).

---

### Anomalies & Discrepancies (Bugs vs. Expected Behavior)

1. **Plaintext Password Entry (BUG-001)**:
   - **Observed**: Password characters are rendered in plain readable text without any visual masking (`PasswordVisualTransformation` is missing).
   - **Expected**: Password should be masked (e.g., bullet dots/asterisks).

2. **Authentication Bypass on Empty Fields (BUG-002)**:
   - **Observed**: Submitting empty credentials immediately authenticates and advances to the catalog.
   - **Expected**: Form validation should block submission and display an error indicating email and password are required.

3. **Authentication Bypass on Invalid Credentials (BUG-003)**:
   - **Observed**: Supplying an incorrect password still authenticates and grants access.
   - **Expected**: App should reject invalid credentials, stay on the login screen, and display an error message (e.g., in `login-error`).

4. **Missing Automation Locator on Log In Button (BUG-016)**:
   - **Observed**: The primary "Log In" submit button has no `Modifier.testTag()` / resource-id assigned.
   - **Expected**: All primary interactive controls should expose stable test identifiers (e.g., `login-submit`).

5. **Ungated Bottom Navigation Bar (BUG-015)**:
   - **Observed**: The bottom navigation bar with "Products" (`tab-products`) and "Cart" (`tab-cart`) tabs is visible and clickable on the Login screen before signing in.
   - **Expected**: Navigation tabs should be hidden or gated behind authentication.

---

## Catalog

### UI Elements & Locators

| Element | Resource-ID / Test Tag | Class / Type | Notes |
| :--- | :--- | :--- | :--- |
| Catalog Header Title | `catalog-title` | `android.widget.TextView` | Displays `"Untitled"` instead of `"Products"` |
| Product 1 Name (Headphones) | `name-p1` | `android.widget.TextView` | Text: `"Wireless Headphones"` |
| Product 1 Add Button | `add-p1` | `android.view.View` / `Button` | Child text: `"Add"`, enabled |
| Product 2 Name (Keyboard) | `name-p2` | `android.widget.TextView` | Text: `"Mechanical Keyboard"` |
| Product 2 Add Button | `add-p2` | `android.view.View` / `Button` | Child text: `"Add"`, enabled |
| Product 3 Name (Monitor) | `name-p3` | `android.widget.TextView` | Long text: `"Ultra-Wide Curved 49-inch Professional Gaming Monitor with HDR"` |
| Product 3 Add Button | `add-p3` | `android.view.View` / `Button` | Child text: `"Add"`, enabled |
| Product 4 Name (USB Hub) | `name-p4` | `android.widget.TextView` | Text: `"USB-C Hub"` |
| Product 4 Stock Badge | `stock-p4` | `android.widget.TextView` | Text: `"Out of Stock"` (renders with green text) |
| Product 4 Add Button | `add-p4` | `android.view.View` / `Button` | Disabled button (`enabled = false`) |
| Product Icons / Price Texts | *None* | `android.widget.TextView` | Emojis (🎧, ⌨️, 🖥️, 🔌) and prices ($60, $90, $700, $40) have no explicit IDs |

---

### Flows Executed

1. **Browse Catalog**:
   - Inspected catalog items (`p1` through `p4`).
   - Verified that item `p4` (USB-C Hub) has `add-p4` disabled due to out-of-stock status.

2. **Add Items to Cart**:
   - Clicked `add-p1` (Wireless Headphones, $60).
   - Clicked `add-p2` (Mechanical Keyboard, $90).
   - Verified that items are added to ViewModel cart state and accessible when switching tabs.

---

### Anomalies & Discrepancies (Bugs vs. Expected Behavior)

1. **Incorrect Screen Title (BUG-014)**:
   - **Observed**: Top header reads `"Untitled"` instead of `"Products"`.
   - **Expected**: Top header should clearly identify the screen as `"Products"`.

2. **Unconstrained Text Overflow on Long Product Names (BUG-007)**:
   - **Observed**: `name-p3` ("Ultra-Wide Curved 49-inch Professional Gaming Monitor with HDR") does not restrict `maxLines` or apply text ellipsis, overflowing and stretching the layout cell.
   - **Expected**: Long product names should be constrained with `maxLines` and ellipsis formatting.

3. **Incorrect Badge Styling for Out-of-Stock Items (BUG-008)**:
   - **Observed**: The `"Out of Stock"` badge on `stock-p4` is rendered in **Green** (`Color.Green`).
   - **Expected**: Out-of-stock warnings should be styled in Red or a warning color to avoid misleading users.

---

## Cart

### UI Elements & Locators

| Element | Resource-ID / Test Tag | Class / Type | Notes |
| :--- | :--- | :--- | :--- |
| Cart Title | *None* | `android.widget.TextView` | Text: `"Cart"` |
| Empty Cart State | `cart-empty` | `android.widget.TextView` | Text: `"Your cart is empty"` (when 0 items) |
| Decrement Quantity Button | `qty-dec-$id` (e.g. `qty-dec-p1`) | `android.view.View` / `Button` | Button text: `"−"` |
| Quantity Value Label | `qty-$id` (e.g. `qty-p1`) | `android.widget.TextView` | Number string (e.g. `"1"`, `"2"`) |
| Increment Quantity Button | `qty-inc-$id` (e.g. `qty-inc-p1`) | `android.view.View` / `Button` | Button text: `"+"` |
| Remove Item Button | `remove-$id` (e.g. `remove-p1`) | `android.view.View` / `Button` | Button text: `"Remove"` |
| Discount Code Input | `discount-input` | `android.widget.EditText` | Placeholder: `"Discount code"` |
| Apply Discount Button | `apply-discount` | `android.view.View` / `Button` | Button text: `"Apply"` |
| Order Total Display | `order-total` | `android.widget.TextView` | Text formatted as `"Total: $XX.XX"` |
| Min Order Error Banner | `min-order-error` | `android.widget.TextView` | Text: `"Minimum order value is $10.00"` |
| **Proceed to Checkout Button** | `proceed-checkout` | `android.view.View` / `Button` | Button text: `"Proceed to Checkout"` |

---

### Flows Executed

1. **Quantity Modification**:
   - **Increment (`qty-inc-p1`)**: Tapped `+`. Quantity displayed changed from `1` to `2`. However, `order-total` remained frozen at `"Total: $60.00"` instead of updating to `$120.00`.
   - **Decrement (`qty-dec-p1`)**: Tapped `−` three times from quantity `2`. Quantity decremented to `1`, then `0`, and then `-1`.

2. **Apply Discount Code**:
   - Entered code `"SAVE10"` into `discount-input` and tapped `apply-discount`.
   - Subtotal was $120.00. With 10% discount, the discount should be $12.00 (Total = $108.00).
   - Due to the division bug, discount was calculated as `$1.20` instead of `$12.00`. Furthermore, `order-total` failed to re-render the updated total on screen.

3. **Proceed to Checkout**:
   - Tapped `proceed-checkout` ("Proceed to Checkout").
   - **ACTUAL BEHAVIOR**: **The button did nothing when tapped.** The screen remained on `CartScreen`.

---

### Anomalies & Discrepancies (Bugs vs. Expected Behavior)

1. **Displayed Total Not Updating on Quantity / Cart Change (BUG-006)**:
   - **Observed**: `displayedTotal` is wrapped in `remember { vm.total }` without keys, capturing the total once upon initial composition. Modifying item quantities or discounts never updates the displayed order total.
   - **Expected**: Total should reactively reflect live cart state (`vm.total`).

2. **Negative Quantity Allowed (BUG-005)**:
   - **Observed**: Decrementing quantity past `1` allows values of `0`, `-1`, `-2`, etc.
   - **Expected**: Minimum quantity should be clamped to `1` (or prompt item removal when reaching zero).

3. **Faulty Discount Percentage Calculation (BUG-004)**:
   - **Observed**: Discount calculation divides by `1000.0` instead of `100.0` (`subtotal * discountPercent / 1000.0`), applying only 10% of the actual discount (e.g. 1% instead of 10%).
   - **Expected**: 10% discount (`SAVE10`) should subtract `subtotal * 10 / 100.0`.

4. **Proceed to Checkout Button is a No-Op / Blocker (BUG-011)**:
   - **Observed**: Tapping the `proceed-checkout` button performs no operation whatsoever because the `onProceed` lambda in `AppRoot` is empty (`{}`). Checkout is **completely unreachable via UI navigation** in this build.
   - **Expected**: Tapping `proceed-checkout` should transition navigation to the Checkout screen.

---

## Checkout & Purchase (Screen & Code Analysis)

> **Note**: Because **BUG-011** completely blocks navigation to Checkout from the Cart screen, the Checkout and Confirmation flows are gated by this blocker. Analysis below captures the planted bugs and locators present in the broken build's Checkout implementation (`CheckoutScreen` and `ConfirmationScreen`).

### UI Elements & Locators

| Element | Resource-ID / Test Tag | Class / Type | Notes |
| :--- | :--- | :--- | :--- |
| Checkout Header | *None* | `android.widget.TextView` | Text: `"Checkout"` |
| First Name Input | `checkout-firstName` | `android.widget.EditText` | Label: `"First Name"` |
| Last Name Input | `checkout-lastName` | `android.widget.EditText` | Label: `"Last Name"` |
| Email Input | `checkout-email` | `android.widget.EditText` | Label: `"Email"` |
| Phone Input | `checkout-phone` | `android.widget.EditText` | Label: `"Phone"` (KeyboardType: Number) |
| Card Number Input | `checkout-card` | `android.widget.EditText` | Label: `"Card Number"` (KeyboardType: Number) |
| Expiry Input | `checkout-expiry` | `android.widget.EditText` | Label: `"Expiry (MM/YY)"` (KeyboardType: Text) |
| CVV Input | `checkout-cvv` | `android.widget.EditText` | Label: `"CVV"` (KeyboardType: Text) |
| Checkout Error Text | `checkout-error` | `android.widget.TextView` | Conditional error banner |
| Place Order Button | `checkout-submit` | `android.widget.Button` | Button text: `"Place Order"` |
| Confirmation Title | `confirmation-title` | `android.widget.TextView` | Text: `"Order Confirmed"` |
| Confirmation Order Ref | *None (Missing ID)* | `android.widget.TextView` | **FLAGGED**: Order reference number is not displayed (BUG-013) |
| Confirmation Done Button | *None (Missing ID)* | `android.widget.Button` | Button text: `"Done"` |

---

### Anomalies & Discrepancies (Bugs vs. Expected Behavior)

1. **Checkout Screen Unreachable via UI (BUG-011 - Blocker)**:
   - **Observed**: Cannot navigate to Checkout from Cart because `proceed-checkout` has an empty click handler.
   - **Expected**: Clicking "Proceed to Checkout" should display `CheckoutScreen`.

2. **Empty Checkout Form Submission Allowed (BUG-012)**:
   - **Observed**: In `CheckoutScreen`, clicking `checkout-submit` immediately generates an order reference (`TS-XXXXXX`) and completes the purchase even if all customer and payment fields are blank.
   - **Expected**: Mandatory field validation should require name, email, address/phone, and payment details before allowing order placement.

3. **Past Expiry Date Accepted (BUG-009)**:
   - **Observed**: No date validation exists on `checkout-expiry`; expired credit card dates (e.g., `01/20`) are accepted without validation errors.
   - **Expected**: App should parse `MM/YY` and reject dates prior to the current month/year.

4. **CVV Keyboard Type is Not Numeric (BUG-010)**:
   - **Observed**: The `checkout-cvv` field uses `KeyboardType.Text` instead of `KeyboardType.Number`, opening a standard QWERTY keyboard instead of a numeric keypad and accepting non-numeric characters.
   - **Expected**: CVV field should use `KeyboardType.Number` with numeric-only validation.

5. **Keyboard Covers CVV Field Without IME Padding (BUG-017)**:
   - **Observed**: The checkout form Column lacks `Modifier.imePadding()`. When the soft keyboard opens on lower fields (such as CVV), the keyboard covers the field and it cannot be scrolled into view.
   - **Expected**: Screen layout should include IME insets padding so focused input fields remain visible above the soft keyboard.

6. **Order Reference Missing on Confirmation Screen (BUG-013)**:
   - **Observed**: `ConfirmationScreen` receives `orderRef` parameter but never renders it to the screen (only displays static text `"Thank you for your purchase."`).
   - **Expected**: The generated order reference number should be prominently displayed (e.g. tagged as `confirmation-order-ref`).

---

## Requirements Gap Analysis (Sprint 1 Spec vs. Observed Build)

### 1. Requirements Observed & Working as Expected

| Area | Requirement | Observation in App |
| :--- | :--- | :--- |
| **Login** | Valid credentials (`demo@techshop.com` / `password123`) log in | Successfully authenticates and navigates to the catalog. |
| **Login** | Session persists for app session | Remains authenticated during tab navigation. |
| **Catalog** | Scrollable product catalog list | List with products (`p1`–`p4`), names, prices, and icons is displayed. |
| **Catalog** | Out-of-stock items have disabled Add button | `add-p4` (USB-C Hub) has `enabled = false` and cannot be added. |
| **Cart** | Add products from catalog to cart | Clicking `add-p1` / `add-p2` adds the items into the cart state. |
| **Cart** | Cart displays item details & empty message | Displays item name, unit price, quantity, and `"Your cart is empty"` (`cart-empty`) when empty. |
| **Cart** | Remove individual items | `remove-$id` controls exist to remove products from cart state. |

---

### 2. Behaviors That Contradict the Specification (Bugs / Defects)

| Area | Specification Requirement | Observed Broken Behavior | Bug ID |
| :--- | :--- | :--- | :--- |
| **Login** | Password field must mask input | Password is displayed in **plaintext** (`PasswordVisualTransformation` missing). | `BUG-001` |
| **Login** | Empty fields must be rejected with error | Empty login submission **bypasses authentication** and navigates to catalog. | `BUG-002` |
| **Login** | Failed login shows error, stays on login | Wrong password **bypasses authentication**; no error shown. | `BUG-003` |
| **Login** | Tab bar hidden until user authenticated | Bottom navigation tabs (`tab-products`, `tab-cart`) are visible on launch. | `BUG-015` |
| **Login** | Every interactive element must have resource-id | Primary **"Log In" button has no resource-id / testTag**. | `BUG-016` |
| **Catalog** | Navigation title: `"Products"` | Header displays **`"Untitled"`**. | `BUG-014` |
| **Catalog** | Long names must truncate cleanly | `name-p3` ("Ultra-Wide Curved 49-inch...") **overflows without truncation**. | `BUG-007` |
| **Catalog** | Out-of-stock badge must be **red** | Out-of-stock badge on `stock-p4` is rendered in **Green**. | `BUG-008` |
| **Cart** | Quantity stepper: minimum 1 | Stepper allows quantity to decrement to **0, -1, and negative values**. | `BUG-005` |
| **Cart** | Order total updates immediately on qty change | Displayed total is frozen at initial render (`remember { vm.total }`) and **never updates**. | `BUG-006` |
| **Cart** | Discount code applies percentage (e.g. SAVE10 = 10%) | Discount calculation divides by `1000.0` instead of `100.0`, applying **1/10th of discount**. | `BUG-004` |
| **Cart** | Accessible via "Proceed to Checkout" button | **Proceed button is a no-op** (empty handler); checkout is completely unreachable via UI. | `BUG-011` |
| **Checkout** | All fields required (empty rejected) | Empty checkout form submits and creates order without validation. | `BUG-012` |
| **Checkout** | Expiry date: not in past | Expired card dates (e.g. past `MM/YY`) are accepted. | `BUG-009` |
| **Checkout** | CVV: numeric keypad only | CVV uses `KeyboardType.Text` (QWERTY) and accepts letters. | `BUG-010` |
| **Checkout** | Keyboard must not permanently cover field | Missing `imePadding()`; soft keyboard **covers CVV field**. | `BUG-017` |
| **Checkout** | Confirmation screen shows order reference | Confirmation screen **does not display the generated order reference**. | `BUG-013` |

---

### 3. Requirements Not Exercised / Blocked from Live UI Testing

1. **Specific Field Format Validations (Checkout)**:
   - *Card Number exactly 16 digits*: Verified keypad is numeric, but exact 16-digit length validation is not enforced or verified in live test.
   - *Phone Number exactly 10 digits*: Verified keypad is numeric, but 10-digit format rule is not enforced or verified in live test.
   - *Email format with `@` and domain* on Login & Checkout: App currently lacks format regex validation.
2. **Minimum Order Value Enforcement ($10.00)**:
   - The UI banner element (`min-order-error`) appears when subtotal < $10, but blocking checkout submission at < $10 could not be exercised via UI due to the `proceed-checkout` blocker (`BUG-011`).
3. **End-to-End Live UI Checkout & Purchase Flow**:
   - Live end-to-end checkout execution on the emulator was blocked by `BUG-011` ("Proceed to Checkout" no-op). Full automated testing of checkout screens requires testing against the fixed build or patching the proceed handler.

