package com.portfolio.bank.data;
import java.sql.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:banking-system.db";
    private Database() {}
    public static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        return c;
    }
    public static void initialize() {
        String users = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password_hash TEXT NOT NULL, account_number TEXT NOT NULL UNIQUE, balance REAL NOT NULL DEFAULT 0, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        String transactions = "CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, type TEXT NOT NULL, amount REAL NOT NULL CHECK(amount > 0), description TEXT, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id))";
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(users); s.execute(transactions);
        } catch (SQLException e) { throw new RuntimeException("Could not initialize database", e); }
    }
}
