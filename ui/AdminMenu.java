
package ui;

import dao.UserDao;
import model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentAdmin;

    public AdminMenu(UserDao userDao, Scanner scanner, User currentAdmin) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Users");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("0. Back");

            int choice = readIntInput("Choose an option (0 to cancel): ");

            try {
                switch (choice) {

                    case 1 -> viewAllUsers();

                    case 2 -> viewPendingUsers();

                    case 3 -> approveUser();

                    case 4 -> updateUser();

                    case 0 -> running = false;

                    default -> System.out.println("Invalid option.");
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    // VIEW ALL USERS
    private void viewAllUsers() throws SQLException {

        List<User> users = userDao.getAllUsers();

        System.out.println("\n========== ALL USERS ==========");

        displayUsers(users);
    }

    // VIEW PENDING USERS
    private void viewPendingUsers() throws SQLException {

        List<User> users = userDao.getPendingUsers();

        System.out.println("\n========== PENDING USERS ==========");

        displayUsers(users);
    }

    // DISPLAY USER TABLE
    private void displayUsers(List<User> users) {

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("+-----+-----------------+----------------+------------+");
        System.out.printf("| %-3s | %-15s | %-14s | %-10s |%n",
                "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("+-----+-----------------+----------------+------------+");

        for (User user : users) {
            System.out.printf("| %-3d | %-15s | %-14s | %-10s |%n",
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getStatus());
        }

        System.out.println("+-----+-----------------+----------------+------------+");
    }

    // APPROVE USER
    private void approveUser() throws SQLException {

        List<User> pendingUsers = userDao.getPendingUsers();

        System.out.println("\n========== PENDING USERS ==========");
        displayUsers(pendingUsers);

        if (pendingUsers.isEmpty()) {
            return;
        }

        int userId = readIntInput("Enter User ID to approve (0 to cancel): ");

        if (userId == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        boolean exists = pendingUsers.stream()
                .anyMatch(user -> user.getId() == userId);

        if (!exists) {
            System.out.println("User ID not found in pending users.");
            return;
        }

        if (userDao.approveUser(userId)) {
            System.out.println("User account approved successfully!");
        } else {
            System.out.println("Approval failed.");
        }
    }

    // UPDATE USER
    private void updateUser() throws SQLException {

        List<User> users = userDao.getAllUsers();

        System.out.println("\n========== UPDATE USER ==========");
        displayUsers(users);

        if (users.isEmpty()) {
            return;
        }

        int userId = readIntInput("Enter User ID to update (0 to cancel): ");

        if (userId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        User targetUser = null;

        for (User user : users) {
            if (user.getId() == userId) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            System.out.println("User ID not found.");
            return;
        }

        // SELF-EDIT RESTRICTION
        if (targetUser.getId() == currentAdmin.getId()) {
            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted."
            );
            return;
        }

        System.out.println("Selected User: " + targetUser.getUsername());

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                userId, newRole, newStatus
        );

        if (updated) {
            System.out.println("User role and status updated successfully!");
        } else {
            System.out.println("Update failed. User may no longer exist.");
        }
    }

    // VALIDATE INTEGER INPUT
    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
    }

    // VALIDATE ROLE
    private String readValidRole() {

        while (true) {

            System.out.println("\nAvailable Roles:");
            System.out.println("1. ADMINISTRATOR");
            System.out.println("2. STAFF");
            System.out.println("3. MANAGER");
            System.out.println("0. Cancel");

            int choice = readIntInput("Choose new role: ");

            switch (choice) {

                case 1:
                    return "ADMINISTRATOR";

                case 2:
                    return "STAFF";

                case 3:
                    return "MANAGER";

                case 0:
                    return null;

                default:
                    System.out.println("Invalid role. Try again.");
            }
        }
    }

    // VALIDATE STATUS
    private String readValidStatus() {

        while (true) {

            System.out.println("\nAvailable Status:");
            System.out.println("1. APPROVED");
            System.out.println("2. PENDING");
            System.out.println("0. Cancel");

            int choice = readIntInput("Choose new status: ");

            switch (choice) {

                case 1:
                    return "APPROVED";

                case 2:
                    return "PENDING";

                case 0:
                    return null;

                default:
                    System.out.println("Invalid status. Try again.");
            }
        }
    }
}
