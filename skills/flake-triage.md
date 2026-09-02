# Skill: Mobile Test Flake Triage Standard

## Purpose & Scope
This skill defines the structured decision framework for triaging mobile test failures to definitively classify whether a failure is a **REAL BUG** (application defect) or a **FLAKY TEST** (test automation / environment noise).

Whenever a test fails during local execution or CI, follow the 5-step workflow below before filing defects or altering application code.

---

## The 5-Step Triage Workflow

### Step 1: Consistency Check
- **Evaluate Repeatability**: Does the test fail 100% deterministically on repeated runs, or only intermittently?
- **Mandatory Re-run Rule**: If the test has only failed once or consistency is uncertain, **immediately re-run the test 3–5 consecutive times** (or request a re-run).
  - Always fails with identical stack trace/state $\rightarrow$ likely **REAL BUG** (or deterministic script error).
  - Passes on retry or fails inconsistently $\rightarrow$ **FLAKY TEST**.

### Step 2: Root Cause Analysis (App vs. Mobile Noise)
Determine whether the failure originated from faulty business logic or typical mobile platform noise:
- Did the application produce an invalid state, wrong value, or crash?
- OR did the test trip over mobile environment artifacts:
  - Animation/transition in flight during tap?
  - Soft keyboard covering the target input or submit button?
  - Interaction attempted before the node was attached to the hierarchy?
  - Locator matched multiple elements or an off-screen element?
  - Leaked state from a previously executed test?

### Step 3: Artifact Evidence Inspection
Examine the failure artifact (Maestro video, Appium screenshot, Espresso test report, UI Automator dump):
- **Genuinely Broken App**: The artifact shows the screen fully rendered with incorrect data, an unexpected error message, missing components, or an ANR/crash dialog.
- **Looked Too Early / Blocked**: The artifact shows a loading spinner, a mid-transition screen, an open keyboard covering the target, or the element appearing a fraction of a second later.

### Step 4: Classification
Assign a definitive verdict:
- **`REAL BUG`**: The application code violates requirements under valid test conditions.
- **`FLAKY TEST`**: The application behaves correctly, but the test script failed due to synchronization, hierarchy, or environmental timing issues.

### Step 5: Actionable Recommendation
- **If `REAL BUG`**:
  - Follow [`skills/bug-reporting.md`](./bug-reporting.md) to log a developer-ready bug report with repro steps, environment, and evidence.
  - Tag with a `BUG-XXX` identifier and track regression.
- **If `FLAKY TEST`**:
  - **DO NOT file a bug report against the application.**
  - Fix the test script using the appropriate stabilization pattern (see below).

---

## Common Mobile Flake Sources & Standard Fixes

| Flake Source | Root Cause | Standard Remedy |
| :--- | :--- | :--- |
| **Soft Keyboard Obstruction** | Keyboard covers inputs or submit buttons, blocking click events. | Explicitly dismiss keyboard before interacting (`hideKeyboard()`, `pressBack()`, or framework equivalent). |
| **Premature Tap / Render Race** | Action dispatched before UI component completes composition/layout. | Replace fixed delays with explicit wait-for-visibility / wait-for-existence (`extendedWaitUntil`, `WebDriverWait`, `Until.findObject`). |
| **Transition & Motion Animations** | Test attempts tap while view is translating/fading. | Synchronize on destination screen anchor element or disable system animations on test device. |
| **Fragile / Ambiguous Locators** | Locating by position, index, or generic text matching multiple views. | Migrate locators to unique, stable `resource-id` / `Modifier.testTag` / `testID`. |
| **Shared State & Order Dependency** | Leftover session, populated cart, or cached token from prior test. | Enforce test isolation: launch fresh per test with cleared state (`clearState: true`, fresh driver session, explicit cache wipe). |
| **Async / Network Latency** | Assertion executes before background network/IO request finishes. | Wait explicitly for specific outcome/value or use Espresso `IdlingResource`. |

---

## Triage Output Summary Format

When reporting the results of a failure triage, use the following concise structure:

```markdown
### Triage Summary: [Test Name / ID]

- **Classification**: REAL BUG | FLAKY TEST
- **Consistency**: [e.g., Failed 5/5 runs | Failed 1/5 runs (intermittent)]
- **Failure Symptom**: [Brief description of assertion failure or timeout]
- **Artifact Evidence**: [What the screenshot, recording, or log revealed]
- **Root Cause**: [Specific mobile noise factor OR application defect]
- **Action / Fix**:
  - *If Real Bug*: File bug report via `skills/bug-reporting.md` (Assigned ID / blocked cases).
  - *If Flaky Test*: [Specific script fix applied: e.g., added explicit wait on `catalog-title`, added `hideKeyboard()`].
```
