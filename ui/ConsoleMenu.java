
package ui;

import dao.*;
import model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final MemberDao memberDao;
    private final MembershipDao membershipDao;
    private final FitnessDao fitnessDao;
    private final ReportDao reportDao;
    private final AuditLogDao auditLogDao;

    private final User currentUser;

    public ConsoleMenu(UserDao userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentUser = currentUser;

        this.memberDao = new MemberDao();
        this.membershipDao = new MembershipDao();
        this.fitnessDao = new FitnessDao();
        this.reportDao = new ReportDao();
        this.auditLogDao = new AuditLogDao();
    }

    // MAIN MENU
    public void start() {

        boolean running = true;

        recordAction("Logged in");

        while (running) {

            displayMenu();

            int choice = readIntInput("Choose an option: ");

            try {
                switch (choice) {

                    case 1 -> addMember();

                    case 2 -> viewMembers();

                    case 3 -> searchMember();

                    case 4 -> updateMember();

                    case 5 -> {
                        if (isAdministrator()) {
                            deactivateMember();
                        } else {
                            accessDenied();
                        }
                    }

                    case 6 -> addMembership();

                    case 7 -> viewMemberships();

                    case 8 -> addFitnessInformation();

                    case 9 -> viewFitnessInformation();

                    case 10 -> {
                        if (canViewReports()) {
                            dailySalesReport();
                        } else {
                            accessDenied();
                        }
                    }

                    case 11 -> {
                        if (canViewReports()) {
                            monthlyIncomeSummary();
                        } else {
                            accessDenied();
                        }
                    }

                    case 12 -> {
                        if (isAdministrator()) {
                            viewAuditLogs();
                        } else {
                            accessDenied();
                        }
                    }

                    case 13 -> {
                        if (isAdministrator()) {
                            new AdminMenu(userDao, scanner, currentUser).start();
                        } else {
                            accessDenied();
                        }
                    }

                    case 0 -> {
                        recordAction("Logged out");
                        System.out.println("Logging out. Goodbye, "
                                + currentUser.getUsername() + "!");
                        running = false;
                    }

                    default -> System.out.println("Invalid option. Try again.");
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    // DISPLAY MENU BASED ON ROLE
    private void displayMenu() {

        String role = currentUser.getRole().toUpperCase();

        System.out.println("\n======================================");
        System.out.println("     GYM MEMBERSHIP AND FITNESS");
        System.out.println("======================================");
        System.out.println("Logged in as: " + currentUser.getUsername());
        System.out.println("Role: " + currentUser.getRole());
        System.out.println("--------------------------------------");

        System.out.println("1. Register Member");
        System.out.println("2. View Members");
        System.out.println("3. Search Member");
        System.out.println("4. Update Member");

        if (role.equals("ADMINISTRATOR")) {
            System.out.println("5. Deactivate Member");
        }

        System.out.println("6. Add Membership");
        System.out.println("7. View Memberships");
        System.out.println("8. Add Fitness Information");
        System.out.println("9. View Fitness Information");

        if (canViewReports()) {
            System.out.println("10. Daily Sales Report");
            System.out.println("11. Monthly Income Summary");
        }

        if (role.equals("ADMINISTRATOR")) {
            System.out.println("12. View Audit Logs");
            System.out.println("13. Manage User Accounts");
        }

        System.out.println("0. Logout");
        System.out.println("======================================");
    }

    // REGISTER MEMBER
    private void addMember() throws SQLException {

        System.out.println("\n========== REGISTER MEMBER ==========");

        System.out.print("Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Age: ");
        int age = readIntInput("");

        System.out.print("Gender: ");
        String gender = scanner.nextLine();

        System.out.print("Contact Number: ");
        String phone = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        memberDao.addMember(name, age, gender, phone, email);

        recordAction("Registered a new member: " + name);

        System.out.println("Member registered successfully!");
    }

    // VIEW MEMBERS
    private void viewMembers() throws SQLException {

        System.out.println("\n========== MEMBER LIST ==========");

        memberDao.listMembers();
    }

    // SEARCH MEMBER
    private void searchMember() throws SQLException {

        System.out.println("\n========== SEARCH MEMBER ==========");

        System.out.print("Enter member name or keyword: ");
        String keyword = scanner.nextLine();

        memberDao.searchMember(keyword);
    }

    // UPDATE MEMBER
    private void updateMember() throws SQLException {

        System.out.println("\n========== UPDATE MEMBER ==========");

        int id = readIntInput("Enter Member ID: ");

        System.out.print("New Full Name: ");
        String name = scanner.nextLine();

        int age = readIntInput("New Age: ");

        System.out.print("New Gender: ");
        String gender = scanner.nextLine();

        System.out.print("New Contact Number: ");
        String phone = scanner.nextLine();

        System.out.print("New Email: ");
        String email = scanner.nextLine();

        memberDao.updateMember(id, name, age, gender, phone, email);

        recordAction("Updated member ID: " + id);

        System.out.println("Member information updated.");
    }

    // DEACTIVATE MEMBER - ADMINISTRATOR ONLY
    private void deactivateMember() throws SQLException {

        System.out.println("\n========== DEACTIVATE MEMBER ==========");

        int id = readIntInput("Enter Member ID to deactivate: ");

        memberDao.deactivateMember(id);

        recordAction("Deactivated member ID: " + id);

        System.out.println("Member deactivation request completed.");
    }

    // ADD MEMBERSHIP
    private void addMembership() throws SQLException {

        System.out.println("\n========== ADD MEMBERSHIP ==========");

        int memberId = readIntInput("Enter Member ID: ");

        System.out.print("Membership Type: ");
        String plan = scanner.nextLine();

        System.out.print("Start Date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();

        System.out.print("End Date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();

        double fee = readDoubleInput("Membership Fee: ");

        membershipDao.addMembership(
                memberId, plan, startDate, endDate, fee
        );

        recordAction("Added membership for member ID: " + memberId);

        System.out.println("Membership added successfully!");
    }

    // VIEW MEMBERSHIPS
    private void viewMemberships() throws SQLException {

        System.out.println("\n========== MEMBERSHIP LIST ==========");

        membershipDao.listMemberships();
    }

    // ADD FITNESS INFORMATION
    private void addFitnessInformation() throws SQLException {

        System.out.println("\n========== ADD FITNESS INFORMATION ==========");

        int memberId = readIntInput("Enter Member ID: ");

        double weight = readDoubleInput("Weight (kg): ");

        double height = readDoubleInput("Height (cm): ");

        System.out.print("Fitness Goal: ");
        String goal = scanner.nextLine();

        System.out.print("Fitness Level: ");
        String fitnessLevel = scanner.nextLine();

        fitnessDao.addFitnessInformation(
                memberId, weight, height, goal, fitnessLevel
        );

        recordAction("Added fitness information for member ID: " + memberId);

        System.out.println("Fitness information saved successfully!");
    }

    // VIEW FITNESS INFORMATION
    private void viewFitnessInformation() throws SQLException {

        System.out.println("\n========== FITNESS INFORMATION ==========");

        fitnessDao.listFitnessInformation();
    }

    // DAILY SALES REPORT - MANAGER AND ADMINISTRATOR
    private void dailySalesReport() throws SQLException {

        System.out.println("\n========== DAILY SALES REPORT ==========");

        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        reportDao.dailySalesReport(date);

        recordAction("Viewed Daily Sales Report for " + date);
    }

    // MONTHLY INCOME SUMMARY - MANAGER AND ADMINISTRATOR
    private void monthlyIncomeSummary() throws SQLException {

        System.out.println("\n========== MONTHLY INCOME SUMMARY ==========");

        System.out.print("Enter Month (YYYY-MM): ");
        String month = scanner.nextLine();

        reportDao.monthlyIncomeSummary(month);

        recordAction("Viewed Monthly Income Summary for " + month);
    }

    // VIEW AUDIT LOGS - ADMINISTRATOR ONLY
    private void viewAuditLogs() throws SQLException {

        System.out.println("\n========== SYSTEM AUDIT LOGS ==========");

        auditLogDao.listLogs();

        recordAction("Viewed system audit logs");
    }

    // RECORD USER ACTION
    private void recordAction(String action) {

        try {
            auditLogDao.recordAction(
                    currentUser.getUsername(),
                    action
            );
        } catch (SQLException e) {
            System.out.println("Unable to record audit log: "
                    + e.getMessage());
        }
    }

    // ROLE CHECKS
    private boolean isAdministrator() {
        return currentUser.getRole()
                .equalsIgnoreCase("ADMINISTRATOR");
    }

    private boolean canViewReports() {

        String role = currentUser.getRole();

        return role.equalsIgnoreCase("ADMINISTRATOR")
                || role.equalsIgnoreCase("MANAGER");
    }

    private void accessDenied() {
        System.out.println("Access denied. You do not have permission.");
    }

    // INTEGER INPUT VALIDATION
    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a valid number."
                );
            }
        }
    }

    // DECIMAL INPUT VALIDATION
    private double readDoubleInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a valid amount or number."
                );
            }
        }
    }
}
