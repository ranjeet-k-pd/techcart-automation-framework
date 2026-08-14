# QA Automation Framework (Self-Contained)

![CI](https://github.com/ranjeet-k-pd/techcart-automation-framework/actions/workflows/ci.yml/badge.svg)

**Live demo app:** https://ranjeet-k-pd.github.io/techcart-automation-framework/

A Selenium WebDriver + TestNG automation framework built using the **Page Object Model (POM)** — testing a small demo web app (**TechCart**) that is bundled directly inside this repository and served locally during the test run.

**No external website dependency.** Unlike frameworks that point at a public demo site, this suite spins up its own tiny local web server before the tests run, so it never breaks due to a third-party site changing, going down, or blocking automated browsers — a common source of CI flakiness.

## What this demonstrates

- **Page Object Model** — page interactions (`LoginPage`, `ProductsPage`) are separated from test logic
- **Data-driven testing** — `@DataProvider` runs the same test against multiple invalid-login combinations
- **Self-contained test environment** — `LocalServer` (built on the JDK's own `HttpServer`, no extra dependency) serves the bundled demo app from `src/test/resources/webapp`
- **Reusable driver management** — a single `DriverFactory` class handles browser setup/teardown and headless mode
- **CI-ready** — a GitHub Actions workflow (`.github/workflows/ci.yml`) runs the full suite automatically on every push, with no network dependency on the app under test
- **Explicit waits** — `WebDriverWait` used throughout instead of hardcoded sleeps

## Tech stack

| Layer | Tool |
|---|---|
| Language | Java 11 |
| Automation | Selenium WebDriver 4 |
| Test framework | TestNG |
| Build tool | Maven |
| Local server | JDK built-in `com.sun.net.httpserver.HttpServer` |
| Driver management | WebDriverManager |
| CI | GitHub Actions |

## Project structure

```
qa-automation-framework/
├── src/main/java/pages/         # Page Object classes
├── src/main/java/utils/         # DriverFactory + LocalServer
├── src/test/java/tests/         # Test classes
├── src/test/resources/webapp/   # The demo app under test (bundled)
├── src/test/resources/          # testng.xml suite file
├── .github/workflows/ci.yml     # CI pipeline
└── pom.xml
```

## Test scenarios covered

1. Valid login lands the user on the Products page
2. Locked account shows the correct error message
3. Invalid username/password combinations are rejected (data-driven)
4. Adding a product updates the cart badge count
5. Adding multiple products increments the cart badge correctly

## How to run locally

**Prerequisites:** Java 11+, Maven, Google Chrome installed.

```bash
git clone https://github.com/<your-username>/qa-automation-framework.git
cd qa-automation-framework
mvn clean test
```

The demo app spins up automatically on `http://localhost:8089` for the duration of the test run — nothing else to configure.

Test results are generated in `target/surefire-reports/`.
