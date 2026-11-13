
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDao {

    // REGISTER MEMBER
    public boolean addMember(String name, int age, String gender,
                             String phone, String email)
            throws SQLException {

        String sql = """
            INSERT INTO members (full_name, contact_number, address)
            VALUES (?, ?, ?)
        """;

        String address = "Age: " + age
                + ", Gender: " + gender
                + ", Email: " + email;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, address);

            return ps.executeUpdate() > 0;
        }
    }

    // VIEW MEMBERS
    public void listMembers() throws SQLException {

        String sql = """
            SELECT * FROM members
            ORDER BY member_id
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== GYM MEMBERS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("member_id")
                                + " | Name: " + rs.getString("full_name")
                                + " | Phone: " + rs.getString("contact_number")
                                + " | Address/Details: " + rs.getString("address")
                                + " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No members registered yet.");
            }
        }
    }

    // SEARCH MEMBER
    public void searchMember(String keyword) throws SQLException {

        String sql = """
            SELECT * FROM members
            WHERE full_name LIKE ?
               OR contact_number LIKE ?
               OR address LIKE ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\n===== SEARCH RESULTS =====");

                while (rs.next()) {
                    found = true;

                    System.out.println(
                            "ID: " + rs.getInt("member_id")
                                    + " | Name: " + rs.getString("full_name")
                                    + " | Phone: " + rs.getString("contact_number")
                                    + " | Details: " + rs.getString("address")
                                    + " | Status: " + rs.getString("status")
                    );
                }

                if (!found) {
                    System.out.println("No matching members found.");
                }
            }
        }
    }

    // UPDATE MEMBER
    public boolean updateMember(int id, String name, int age,
                                String gender, String phone,
                                String email) throws SQLException {

        String sql = """
            UPDATE members
            SET full_name = ?,
                contact_number = ?,
                address = ?
            WHERE member_id = ?
        """;

        String address = "Age: " + age
                + ", Gender: " + gender
                + ", Email: " + email;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    // DEACTIVATE MEMBER
    public boolean deactivateMember(int id) throws SQLException {

        String sql = """
            UPDATE members
            SET status = 'INACTIVE'
            WHERE member_id = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
