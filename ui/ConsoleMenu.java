
package ui;

import dao.*;
import model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner;
    private final UserDao userDao;
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

    // MAIN MENU - NO SECOND LOGIN
    public void start() {

        System.out.println("\n==================================");
        System.out.println(" GYM MEMBERSHIP AND FITNESS SYSTEM");
        System.out.println("==================================");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("Role: " + currentUser.getRole());

        boolean running = true;

        while (running) {
            showMenu();

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> addMember();
                    case "2" -> memberDao.listMembers();
                    case "3" -> searchMember();
                    case "4" -> updateMember();
                    case "5" -> deactivateMember();
                    case "6" -> addMembership();
                    case "7" -> membershipDao.listMemberships();
                    case "8" -> addFitnessInformation();
                    case "9" -> fitnessDao.listFitnessInformation();
                    case "10" -> dailySalesReport();
                    case "11" -> monthlyIncomeSummary();
                    case "12" -> auditLogDao.listLogs();
                    case "13" -> {
                        if (isAdministrator()) {
                            new AdminMenu(userDao, scanner).start();
                        } else {
                            System.out.println("Access denied. Administrator only.");
                        }
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());

            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }

        System.out.println("You have logged out successfully.");
    }

    // ROLE CHECK
    private boolean isAdministrator() {
        return "Administrator".equalsIgnoreCase(currentUser.getRole());
    }

    private boolean isManager() {
        return "Manager".equalsIgnoreCase(currentUser.getRole());
    }

    // DISPLAY MENU BASED ON ROLE
    private void showMenu() {

        System.out.println("\n========== MAIN MENU ==========");

        System.out.println("1. Register Member");
        System.out.println("2. View Members");
        System.out.println("3. Search Member");
        System.out.println("4. Update Member");

        if (isAdministrator()) {
            System.out.println("5. Deactivate Member");
        }

        System.out.println("6. Add Membership");
        System.out.println("7. View Memberships");
        System.out.println("8. Add Fitness Information");
        System.out.println("9. View Fitness Information");

        if (isAdministrator() || isManager()) {
            System.out.println("10. Daily Sales Report");
            System.out.println("11. Monthly Income Summary");
        }

        if (isAdministrator()) {
            System.out.println("12. View Audit Logs");
            System.out.println("13. Manage User Accounts");
        }

        System.out.println("0. Logout");
    }

    // REGISTER MEMBER
    private void addMember() throws SQLException {

        System.out.println("\n===== REGISTER MEMBER =====");

        System.out.print("Member name: ");
        String name = scanner.nextLine();

        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.print("Gender: ");
        String gender = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        if (memberDao.addMember(name, age, gender, phone, email)) {
            auditLogDao.recordAction(
                    currentUser.getUsername(),
                    "Registered member: " + name
            );

            System.out.println("Member registered successfully!");
        } else {
            System.out.println("Unable to register member.");
        }
    }

    // SEARCH MEMBER
    private void searchMember() throws SQLException {

        System.out.print("\nEnter name, phone, or email: ");
        String keyword = scanner.nextLine();

        memberDao.searchMember(keyword);
    }

    // UPDATE MEMBER
    private void updateMember() throws SQLException {

        System.out.println("\n===== UPDATE MEMBER =====");

        System.out.print("Member ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("New name: ");
        String name = scanner.nextLine();

        System.out.print("New age: ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.print("New gender: ");
        String gender = scanner.nextLine();

        System.out.print("New phone: ");
        String phone = scanner.nextLine();

        System.out.print("New email: ");
        String email = scanner.nextLine();

        if (memberDao.updateMember(id, name, age, gender, phone, email)) {

            auditLogDao.recordAction(
                    currentUser.getUsername(),
                    "Updated member ID: " + id
            );

            System.out.println("Member updated successfully!");

        } else {
            System.out.println("Member not found.");
        }
    }

    // DEACTIVATE MEMBER
    private void deactivateMember() throws SQLException {

        if (!isAdministrator()) {
            System.out.println("Access denied.");
            return;
        }

        System.out.print("\nEnter Member ID to deactivate: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (memberDao.deactivateMember(id)) {

            auditLogDao.recordAction(
                    currentUser.getUsername(),
                    "Deactivated member ID: " + id
            );

            System.out.println("Member deactivated successfully!");

        } else {
            System.out.println("Member not found.");
        }
    }

    // ADD MEMBERSHIP
    private void addMembership() throws SQLException {

        System.out.println("\n===== ADD MEMBERSHIP =====");

        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine());

        System.out.print("Membership plan: ");
        String plan = scanner.nextLine();

        System.out.print("Start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();

        System.out.print("End date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();

        System.out.print("Membership fee (PHP): ");
        double fee = Double.parseDouble(scanner.nextLine());

        if (membershipDao.addMembership(
                memberId, plan, startDate, endDate, fee)) {

            auditLogDao.recordAction(
                    currentUser.getUsername(),
                    "Added membership for member ID: " + memberId
            );

            System.out.println("Membership added successfully!");

        } else {
            System.out.println("Unable to add membership.");
        }
    }

    // ADD FITNESS INFORMATION
    private void addFitnessInformation() throws SQLException {

        System.out.println("\n===== ADD FITNESS INFORMATION =====");

        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine());

        System.out.print("Weight (kg): ");
        double weight = Double.parseDouble(scanner.nextLine());

        System.out.print("Height (cm): ");
        double height = Double.parseDouble(scanner.nextLine());

        System.out.print("Fitness goal: ");
        String goal = scanner.nextLine();

        System.out.print("Notes: ");
        String notes = scanner.nextLine();

        if (fitnessDao.addFitnessInformation(
                memberId, weight, height, goal, notes)) {

            auditLogDao.recordAction(
                    currentUser.getUsername(),
                    "Added fitness information for member ID: " + memberId
            );

            System.out.println("Fitness information saved!");

        } else {
            System.out.println("Unable to save fitness information.");
        }
    }

    // DAILY SALES REPORT
    private void dailySalesReport() throws SQLException {

        if (!isAdministrator() && !isManager()) {
            System.out.println("Access denied.");
            return;
        }

        System.out.print("\nDate (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        reportDao.dailySalesReport(date);
    }

    // MONTHLY INCOME SUMMARY
    private void monthlyIncomeSummary() throws SQLException {

        if (!isAdministrator() && !isManager()) {
            System.out.println("Access denied.");
            return;
        }

        System.out.print("\nMonth (YYYY-MM): ");
        String month = scanner.nextLine();

        reportDao.monthlyIncomeSummary(month);
    }
}
