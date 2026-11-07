
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

        String sql = """
            INSERT INTO users (username, password, role, status)
            VALUES (?, ?, ?, 'PENDING')
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role.toUpperCase());

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
            WHERE UPPER(status) = ?
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

        String sql = """
            UPDATE users
            SET role = ?, status = ?
            WHERE id = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newRole.toUpperCase());
            ps.setString(2, newStatus.toUpperCase());
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
