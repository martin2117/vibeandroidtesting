# Skill: Test Case Design for Mobile (TechShop)

## Purpose & Scope
This skill defines the instructions and standard operating procedure for designing complete, grounded mobile test cases for TechShop features (Login, Product Catalog, Shopping Cart, Checkout).

Whenever tasked with designing test cases or a test matrix, follow the instructions below to produce an accurate, testable, and actionable test matrix.

---

## Inputs & Sources
Always reference the following project sources rather than making generic assumptions:
1. **Requirements Specification** ([techshop/requirements.md](../techshop/requirements.md)): The source of truth for expected behavior, business logic, constraints, and validation rules.
2. **Exploration Notes** ([exploration-notes.md](../exploration-notes.md)): The source of truth for actual UI hierarchy, available `resource-id` / `testTag` locators, observed anomalies, and known bugs (`BUG-XXX`).

---

## Core Requirements

### 1. Mandatory Coverage Categories
For every feature under test, include test cases across all three categories:
- **Positive (Happy Path)**: Valid end-to-end user workflows and expected successful operations.
- **Negative (Error Handling)**: Invalid inputs, missing required fields, authentication failures, and rejected operations.
- **Edge (Boundaries & Limits)**: Boundary values, numerical limits (e.g., minimum quantity = 1, minimum order value = $10.00), formatting constraints (e.g., MM/YY expiry dates, 16-digit cards, 3-digit CVVs, long text truncation), and edge interactions.

### 2. Standardized Per-Case Structure
Every test case in the matrix must specify:
- **ID**: Structured identifier prefixed by feature (e.g., `TC-LOGIN-001`, `TC-CAT-001`, `TC-CART-001`, `TC-CHK-001`).
- **Behavioural Title**: Concise, action-oriented summary of the behavior being verified.
- **Category**: `Positive`, `Negative`, or `Edge`.
- **Preconditions**: Exact app state, screen, authentication status, and cart contents required before execution.
- **Steps**: Concrete, sequential actions performed by the user.
- **Expected Result**: Verifiable outcome asserted against the specification in `techshop/requirements.md`.
- **Locator & Testability**: Target element identifier (`resource-id` / Compose `testTag` / React Native `testID` or visible text fallback).
  - **Testability Defect Flag**: If an interactive control lacks a stable `resource-id` / `testTag` (e.g., Login button missing id), explicitly flag it as `[Testability Defect: Missing ID]`.
- **Regression & Blockers**:
  - **Known Bug / Regression**: If the test covers a known defect identified during exploration, tag it with its `BUG-XXX` identifier and document the expected vs. observed behavior.
  - **Blocked Status**: If a test case cannot be executed due to a prerequisite blocker (e.g., blocked by `BUG-011`), explicitly mark it as `[Blocked by BUG-XXX]`.

---

## Test Matrix Output Format

When generating the test matrix (e.g., into `test-cases.md`), format the output as a clean, standardized Markdown table per feature:

| ID | Behavioural Title | Category | Preconditions | Steps | Expected Result | Locators / Testability | Bug / Regression Tag |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `TC-<FEATURE>-001` | [Action & outcome] | Positive / Negative / Edge | [Initial app state] | 1. [Step 1]<br>2. [Step 2] | [Expected specification behavior] | `element-id` or Text `"..."`<br>*(Flag if missing)* | `BUG-XXX` (or None / Blocked by `BUG-YYY`) |

---

## Agent Execution Checklist

Before finalizing any test matrix:
1. **Cross-Check Specs**: Are all functional requirements and acceptance criteria from `techshop/requirements.md` covered?
2. **Verify Locators**: Are locators exact matches with `exploration-notes.md`?
3. **Audit Balance**: Does every feature have Positive, Negative, and Edge test cases?
4. **Flag Deficiencies**: Are all missing test IDs flagged as testability defects?
5. **Trace Defects**: Are all known exploration bugs (`BUG-001` through `BUG-016`) mapped to planned regression test cases?
