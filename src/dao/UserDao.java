
package dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // LOGIN
    public User login(String username, String password) throws SQLException {

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

    // REGISTER USER
    public boolean addUser(String username, String password, String role)
            throws SQLException {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()
                || role == null || role.isBlank()) {
            return false;
        }

        String normalizedRole = role.toUpperCase();

        if (!normalizedRole.equals("ADMINISTRATOR")
                && !normalizedRole.equals("STAFF")
                && !normalizedRole.equals("MANAGER")) {
            return false;
        }

        String sql = """
                INSERT INTO users (username, password, role, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.setString(3, normalizedRole);

            return ps.executeUpdate() > 0;
        }
    }

    // GET ALL USERS
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

    // GET PENDING USERS
    public List<User> getPendingUsers() throws SQLException {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT id, username, role, status
                FROM users
                WHERE UPPER(status) = 'PENDING'
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

    // APPROVE USER
    public boolean approveUser(int userId) throws SQLException {

        String sql = """
                UPDATE users
                SET status = 'APPROVED'
                WHERE id = ? AND UPPER(status) = 'PENDING'
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;
        }
    }

    // UPDATE USER ROLE AND STATUS
    public boolean updateUserRoleAndStatus(
            int userId, String newRole, String newStatus)
            throws SQLException {

        if (newRole == null || newStatus == null) {
            return false;
        }

        String role = newRole.toUpperCase();
        String status = newStatus.toUpperCase();

        if (!role.equals("ADMINISTRATOR")
                && !role.equals("STAFF")
                && !role.equals("MANAGER")) {
            return false;
        }

        if (!status.equals("APPROVED")
                && !status.equals("PENDING")) {
            return false;
        }

        String sql = """
                UPDATE users
                SET role = ?, status = ?
                WHERE id = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setString(2, status);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
