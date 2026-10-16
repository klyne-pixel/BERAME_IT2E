
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDao {

    public boolean addMember(String name, int age, String gender,
                             String phone, String email)
            throws SQLException {

        String sql = """
            INSERT INTO members(name, age, gender, phone, email)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, email);

            return ps.executeUpdate() > 0;
        }
    }

    public void listMembers() throws SQLException {

        String sql = "SELECT * FROM members ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== GYM MEMBERS =====");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id")
                                + " | Name: " + rs.getString("name")
                                + " | Age: " + rs.getInt("age")
                                + " | Gender: " + rs.getString("gender")
                                + " | Phone: " + rs.getString("phone")
                                + " | Email: " + rs.getString("email")
                                + " | Status: " + rs.getString("status")
                );
            }
        }
    }

    public void searchMember(String keyword) throws SQLException {

        String sql = """
            SELECT * FROM members
            WHERE name LIKE ? OR phone LIKE ? OR email LIKE ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println(
                            "ID: " + rs.getInt("id")
                                    + " | Name: " + rs.getString("name")
                                    + " | Phone: " + rs.getString("phone")
                                    + " | Email: " + rs.getString("email")
                                    + " | Status: " + rs.getString("status")
                    );
                }
            }
        }
    }

    public boolean updateMember(int id, String name, int age,
                                String gender, String phone,
                                String email) throws SQLException {

        String sql = """
            UPDATE members
            SET name = ?, age = ?, gender = ?, phone = ?, email = ?
            WHERE id = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deactivateMember(int id) throws SQLException {

        String sql = """
            UPDATE members SET status = 'Inactive'
            WHERE id = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
