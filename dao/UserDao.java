
package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // Login
    public User login(String username, String password)
            throws SQLException {

        String sql = """
            SELECT id, username, password, role, status
            FROM users
            WHERE username = ? AND password = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                }
            }
        }

        return null;
    }

    // Register a new user
    public boolean addUser(String username, String password, String role)
            throws SQLException {

        String sql = """
            INSERT INTO users(username, password, role)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            return ps.executeUpdate() > 0;
        }
    }

    // Get all registered users
    public List<User> getAllUsers() throws SQLException {

        List<User> users = new ArrayList<>();

        String sql = """
            SELECT id, username, role, status
            FROM users
            ORDER BY id
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "",
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        }

        return users;
    }

    // Get users waiting for approval
    public List<User> getPendingUsers() throws SQLException {

        List<User> users = new ArrayList<>();

        String sql = """
            SELECT id, username, role, status
            FROM users
            WHERE status = ?
            ORDER BY id
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "PENDING");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            "",
                            rs.getString("role"),
                            rs.getString("status")
                    ));
                }
            }
        }

        return users;
    }

    // Approve a pending user account
    public boolean approveUser(int userId) throws SQLException {

        String sql = """
            UPDATE users
            SET status = 'APPROVED'
            WHERE id = ?
            AND status = 'PENDING'
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
