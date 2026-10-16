
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditLogDao {

    public void recordAction(String username, String action)
            throws SQLException {

        String sql = """
            INSERT INTO audit_logs (username, action)
            VALUES (?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, action);
            ps.executeUpdate();
        }
    }

    public void listLogs() throws SQLException {

        String sql = """
            SELECT id, username, action, log_time
            FROM audit_logs
            ORDER BY id DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== AUDIT LOGS =====");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id")
                                + " | User: " + rs.getString("username")
                                + " | Action: " + rs.getString("action")
                                + " | Time: " + rs.getString("log_time")
                );
            }
        }
    }
}
