# NOVA Bank — Desktop Banking System

A JavaFX desktop banking simulation designed as a software engineering portfolio project.

## Features
- Account registration and sign-in
- BCrypt password hashing
- Account balance dashboard
- Deposits, withdrawals, and account-to-account transfers
- Transaction history
- SQLite persistence, JDBC prepared statements, and transaction rollback
- Input validation and clear success/error messages

## Technology
Java 21 · JavaFX 21 · SQLite · JDBC · BCrypt · Maven

## Run in Apache NetBeans
1. Install JDK 21 or newer and Apache NetBeans with Maven support.
2. Extract the ZIP archive.
3. In NetBeans select **File → Open Project** and choose the `BankingSystem` folder.
4. Wait for Maven to download dependencies.
5. Run the Maven goal `javafx:run`, or execute `mvn clean javafx:run` in the project terminal.

The SQLite database is created automatically on first launch. Choose **Create a new account** to register, then sign in. Passwords must be at least 8 characters.

## Project structure
`data` contains database initialization; `model` contains records; `service` contains business logic; `ui` contains JavaFX screens.

## Security and scope
This is an educational banking simulation, not a production banking platform. It does not connect to real banks or payment networks. Do not use real financial credentials or customer data.

## LinkedIn project summary
Built NOVA Bank, a JavaFX desktop banking simulation featuring password hashing, SQLite persistence, customer registration, deposits, withdrawals, account transfers, and transaction history. Applied object-oriented programming, JDBC prepared statements, and database transaction handling.

Suggested GitHub topics: `java`, `javafx`, `sqlite`, `jdbc`, `banking-system`, `desktop-application`, `portfolio-project`.
