
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MembershipDao {

    public boolean addMembership(int memberId, String plan,
                                 String startDate, String endDate,
                                 double fee) throws SQLException {

        String sql = """
            INSERT INTO memberships
            (member_id, plan, start_date, end_date, fee)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setString(2, plan);
            ps.setString(3, startDate);
            ps.setString(4, endDate);
            ps.setDouble(5, fee);

            return ps.executeUpdate() > 0;
        }
    }

    public void listMemberships() throws SQLException {

        String sql = """
            SELECT m.id, m.member_id, members.name,
                   m.plan, m.start_date, m.end_date,
                   m.fee, m.status
            FROM memberships m
            JOIN members ON m.member_id = members.id
            ORDER BY m.id
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== MEMBERSHIPS =====");

            while (rs.next()) {
                System.out.println(
                        "Membership ID: " + rs.getInt("id")
                                + " | Member: " + rs.getString("name")
                                + " | Plan: " + rs.getString("plan")
                                + " | Start: " + rs.getString("start_date")
                                + " | End: " + rs.getString("end_date")
                                + " | Fee: " + rs.getDouble("fee")
                                + " | Status: " + rs.getString("status")
                );
            }
        }
    }

    public boolean updateMembershipStatus(int id, String status)
            throws SQLException {

        String sql = """
            UPDATE memberships
            SET status = ?
            WHERE id = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }
}
