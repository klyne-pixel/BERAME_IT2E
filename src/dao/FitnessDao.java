
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FitnessDao {

    public boolean addFitnessInformation(int memberId, double weight,
                                         double height, String fitnessGoal,
                                         String notes)
            throws SQLException {

        String sql = """
            INSERT INTO fitness_information
            (member_id, weight, height, fitness_goal, notes)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setDouble(2, weight);
            ps.setDouble(3, height);
            ps.setString(4, fitnessGoal);
            ps.setString(5, notes);

            return ps.executeUpdate() > 0;
        }
    }

    public void listFitnessInformation() throws SQLException {

        String sql = """
            SELECT f.id, f.member_id, m.name,
                   f.weight, f.height, f.fitness_goal,
                   f.notes, f.date_recorded
            FROM fitness_information f
            JOIN members m ON f.member_id = m.id
            ORDER BY f.id
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== FITNESS INFORMATION =====");

            while (rs.next()) {
                System.out.println(
                        "Record ID: " + rs.getInt("id")
                                + " | Member: " + rs.getString("name")
                                + " | Weight: " + rs.getDouble("weight") + " kg"
                                + " | Height: " + rs.getDouble("height") + " cm"
                                + " | Goal: " + rs.getString("fitness_goal")
                                + " | Notes: " + rs.getString("notes")
                                + " | Date: " + rs.getString("date_recorded")
                );
            }
        }
    }
}
