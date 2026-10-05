# Project notes

## Architecture
- `ui`: JavaFX screens and user interaction.
- `service`: authentication and banking business rules.
- `data`: SQLite connection and schema initialization.
- `model`: immutable records.

## Database
- `users`: customer profile, password hash, account number, balance.
- `transactions`: account activity linked to the owning user.

## Before publishing
- Run the application and create a test account.
- Test deposits, withdrawals, transfers, and insufficient-balance handling.
- Capture screenshots of Login, Dashboard, and Transaction History.
- Add screenshots to a `screenshots/` folder in your GitHub repository.
- Never commit the generated SQLite database.
