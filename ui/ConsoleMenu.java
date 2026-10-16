
package ui;

import dao.*;
import model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final UserDao userDao = new UserDao();
    private final MemberDao memberDao = new MemberDao();
    private final MembershipDao membershipDao = new MembershipDao();
    private final FitnessDao fitnessDao = new FitnessDao();
    private final ReportDao reportDao = new ReportDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    private User currentUser;

    public void start() {
        try {
            System.out.println("==================================");
            System.out.println(" GYM MEMBERSHIP AND FITNESS SYSTEM");
            System.out.println("==================================");

            if (!login()) {
                System.out.println("Invalid login. Goodbye!");
                return;
            }

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
                        case "0" -> running = false;
                        default -> System.out.println("Invalid option.");
                    }
                } catch (SQLException e) {
                    System.out.println("Database error: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            System.out.println("Thank you for using the system!");

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    private boolean login() throws SQLException {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = userDao.login(username, password);

        if (currentUser != null) {
            auditLogDao.recordAction(username, "Logged in");
            System.out.println("Welcome, " + username + "!");
            return true;
        }

        return false;
    }

    private void showMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Register Member");
        System.out.println("2. View Members");
        System.out.println("3. Search Member");
        System.out.println("4. Update Member");
        System.out.println("5. Deactivate Member");
        System.out.println("6. Add Membership");
        System.out.println("7. View Memberships");
        System.out.println("8. Add Fitness Information");
        System.out.println("9. View Fitness Information");
        System.out.println("10. Daily Sales Report");
        System.out.println("11. Monthly Income Summary");
        System.out.println("12. View Audit Logs");
        System.out.println("0. Exit");
    }

    private void addMember() throws SQLException {
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
                    currentUser.getUsername(), "Registered member: " + name
            );
            System.out.println("Member registered successfully!");
        }
    }

    private void searchMember() throws SQLException {
        System.out.print("Enter name, phone, or email to search: ");
        memberDao.searchMember(scanner.nextLine());
    }

    private void updateMember() throws SQLException {
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
                    currentUser.getUsername(), "Updated member ID: " + id
            );
            System.out.println("Member updated successfully!");
        } else {
            System.out.println("Member not found.");
        }
    }

    private void deactivateMember() throws SQLException {
        System.out.print("Member ID to deactivate: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (memberDao.deactivateMember(id)) {
            auditLogDao.recordAction(
                    currentUser.getUsername(), "Deactivated member ID: " + id
            );
            System.out.println("Member deactivated.");
        } else {
            System.out.println("Member not found.");
        }
    }

    private void addMembership() throws SQLException {
        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine());

        System.out.print("Plan name: ");
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
        }
    }

    private void addFitnessInformation() throws SQLException {
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
        }
    }

    private void dailySalesReport() throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        reportDao.dailySalesReport(scanner.nextLine());
    }

    private void monthlyIncomeSummary() throws SQLException {
        System.out.print("Month (YYYY-MM): ");
        reportDao.monthlyIncomeSummary(scanner.nextLine());
    }
}
