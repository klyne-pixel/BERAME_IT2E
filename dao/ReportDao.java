
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDao {

    public void dailySalesReport(String date) throws SQLException {

        String sql = """
            SELECT COUNT(*) AS total_transactions,
                   COALESCE(SUM(fee), 0) AS total_sales
            FROM memberships
            WHERE start_date = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, date);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n===== DAILY SALES REPORT =====");
                System.out.println("Date: " + date);
                System.out.println(
                        "Transactions: " + rs.getInt("total_transactions")
                );
                System.out.printf(
                        "Total Sales: PHP %.2f%n",
                        rs.getDouble("total_sales")
                );
            }
        }
    }

    public void monthlyIncomeSummary(String month) throws SQLException {

        String sql = """
            SELECT COUNT(*) AS total_transactions,
                   COALESCE(SUM(fee), 0) AS total_income
            FROM memberships
            WHERE substr(start_date, 1, 7) = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, month);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n===== MONTHLY INCOME SUMMARY =====");
                System.out.println("Month: " + month);
                System.out.println(
                        "Transactions: " + rs.getInt("total_transactions")
                );
                System.out.printf(
                        "Total Income: PHP %.2f%n",
                        rs.getDouble("total_income")
                );
            }
        }
    }
}
