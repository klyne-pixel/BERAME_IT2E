
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditLogDao {

    public void recordAction(String username, String action)
            throws SQLException {

        String sql = """
            INSERT INTO audit_logs (user_id, action, details)
            SELECT id, ?, ?
            FROM users
            WHERE username = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, action);
            ps.setString(2, action);
            ps.setString(3, username);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted == 0) {
                throw new SQLException("User not found: " + username);
            }
        }
    }

    public void listLogs() throws SQLException {

        String sql = """
            SELECT
                audit_logs.log_id,
                users.username,
                audit_logs.action,
                audit_logs.details,
                audit_logs.created_at
            FROM audit_logs
            LEFT JOIN users
                ON audit_logs.user_id = users.id
            ORDER BY audit_logs.log_id DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n========== AUDIT LOGS ==========");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("log_id")
                                + " | User: " + rs.getString("username")
                                + " | Action: " + rs.getString("action")
                                + " | Details: " + rs.getString("details")
                                + " | Time: " + rs.getString("created_at")
                );
            }

            if (!found) {
                System.out.println("No audit logs found.");
            }
        }
    }
}
