# Skill: Mobile Bug Reporting Standard

## Purpose & Scope
This skill defines the standard instructions for transforming a failing mobile test and its execution artifacts into a clear, actionable, developer-ready bug report.

Whenever a test fails due to an application defect (or when filing a defect during exploratory testing), follow the structure and standards defined below.

---

## Required Bug Report Structure

Every bug report must contain the following discrete sections:

### 1. Title
- Format: `[<Feature / Area>] <Specific Behavioural Summary>`
- Must describe the exact faulty behavior and circumstance, not generic text like "test failed" or "login broken".

### 2. Environment
- **Build / Architecture**: Native Jetpack Compose or React Native
- **Build Version / Variant**: Broken (e.g., `v1.0.0-broken`) vs Fixed (e.g., `v1.0.0-fixed`)
- **OS Version**: Android Version & API Level (e.g., Android 14 / API 34)
- **Device / Emulator**: Device model, screen resolution/DPI (e.g., Pixel 7 Emulator)
- **Detection Framework**: Maestro / Appium / Espresso / UI Automator

### 3. Target Element & Locator Details
- **Resource ID / Test Tag**: The exact `resource-id`, `Modifier.testTag`, or `testID` of the affected control.
- **Missing Identifier Flag**: If the element lacks a stable ID, explicitly state: `None (Missing ID / Testability Defect)`.

### 4. Steps to Reproduce
- Numbered, deterministic steps starting from a freshly launched app on a clean emulator.
- Include exact input values, button taps, and screen transitions.

### 5. Expected vs. Actual Results
- **Expected Result**: What the app should do according to the requirements specification.
- **Actual Result**: What the app actually did (with exact error messages, unexpected transitions, or invalid UI states).

### 6. Severity & Impact
- **Severity**: `Blocker` | `Critical` | `Major` | `Minor`
- **Reason**: Exactly one concise line explaining the business, user experience, or testability impact.

### 7. Cross-Build & Test Blocker Analysis
- **Cross-Build Reproducibility**: Note whether this issue reproduces on Compose, React Native, or both.
- **Blocked Test Cases**: List any downstream test cases or user flows blocked from executing due to this bug (e.g., `Blocks TC-CHK-001 through TC-CHK-005`).

### 8. Evidence & Artifacts
- Direct reference or path to test execution artifacts:
  - **Maestro**: Screen recording (`.mp4`) or flow execution log.
  - **Appium**: Failure screenshot (`.png`) or page source XML dump.
  - **Espresso**: Gradle test report (`index.html`), logcat snippet, or assertion failure trace.
  - **UI Automator**: Logcat error dump, hierarchy dump, or screenshot.

---

## Standard Bug Report Template

```markdown
# [BUG-XXX] [<Feature>] <Clear Behavioural Title>

## Environment
- **Build**: Jetpack Compose / React Native (Broken / Fixed build)
- **Device/OS**: Android [Version] (API [Level]) on [Emulator/Device Model]
- **Detected By**: [Maestro / Appium / Espresso / UI Automator]
- **Target Element**: `resource-id` or *None (Missing ID)*

## Steps to Reproduce
1. Launch app with fresh state.
2. [Step 2 with exact input]
3. [Step 3 action]

## Expected Result
[Specific outcome according to specification]

## Actual Result
[Observed faulty behavior, error message, or unexpected transition]

## Severity & Impact
- **Severity**: [Blocker / Critical / Major / Minor]
- **Rationale**: [One-line explanation of user/system impact]

## Scope & Blockers
- **Cross-Build Behavior**: [Reproduces on Compose / React Native / Both]
- **Blocks**: [None / List of blocked test cases or flows]

## Evidence
- [Artifact Path / Link to Screenshot, Video Recording, or Test Report]
```

---

## Quality Self-Check (Mandatory)

Before submitting or filing any bug report, perform this final verification:

> **"Could a developer reproduce this from the steps alone on a clean emulator?"**

If the answer is **NO** (e.g., missing preconditions, ambiguous inputs, unstated build variant, or unclear steps), **the bug report is not done**. Refine the steps until it is 100% reproducible in isolation.
