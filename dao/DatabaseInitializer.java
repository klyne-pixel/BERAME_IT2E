package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    full_name TEXT,
                    role TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS members (
                    member_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    contact_number TEXT,
                    address TEXT,
                    date_registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status TEXT DEFAULT 'ACTIVE'
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS memberships (
                    membership_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_id INTEGER NOT NULL,
                    membership_type TEXT NOT NULL,
                    start_date TEXT,
                    end_date TEXT,
                    fee REAL DEFAULT 0,
                    status TEXT DEFAULT 'ACTIVE',
                    FOREIGN KEY (member_id)
                        REFERENCES members(member_id)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS fitness_information (
                    fitness_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_id INTEGER NOT NULL,
                    fitness_goal TEXT,
                    height REAL,
                    weight REAL,
                    fitness_level TEXT,
                    date_recorded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (member_id)
                        REFERENCES members(member_id)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    action TEXT NOT NULL,
                    details TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id)
                        REFERENCES users(id)
                )
                """);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed: "
                    + e.getMessage());
            throw new IllegalStateException(
                    "Cannot start without database initialization.", e);
        }
    }
}