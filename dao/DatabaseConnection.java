
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:users.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() throws SQLException {

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS members (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    age INTEGER,
                    gender TEXT,
                    phone TEXT,
                    email TEXT,
                    status TEXT DEFAULT 'Active'
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS memberships (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_id INTEGER NOT NULL,
                    plan TEXT NOT NULL,
                    start_date TEXT,
                    end_date TEXT,
                    fee REAL,
                    status TEXT DEFAULT 'Active'
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS fitness_information (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_id INTEGER NOT NULL,
                    weight REAL,
                    height REAL,
                    fitness_goal TEXT,
                    notes TEXT,
                    date_recorded TEXT DEFAULT CURRENT_DATE
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT,
                    action TEXT,
                    log_time TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);

            st.execute("""
                INSERT OR IGNORE INTO users
                (username, password, role)
                VALUES ('admin', 'admin123', 'Administrator')
            """);
        }
    }
}

