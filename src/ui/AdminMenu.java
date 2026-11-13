
package ui;

import dao.UserDao;
import dao.ActivityLogDao;
import model.User;
import model.ActivityLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;
    private final User currentAdmin;

    public AdminMenu(UserDao userDao, Scanner scanner, User currentAdmin) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
        this.activityLogDao = new ActivityLogDao();
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Users");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("5. View All Activity Logs");
            System.out.println("6. Filter Activity Logs by User ID");
            System.out.println("7. Clear Historical Activity Logs");
            System.out.println("0. Back");

            int choice = readIntInput("Choose an option (0 to cancel): ");

            try {
                switch (choice) {

                    case 1 -> viewAllUsers();

                    case 2 -> viewPendingUsers();

                    case 3 -> approveUser();

                    case 4 -> updateUser();

                    case 5 -> viewAllActivityLogs();

                    case 6 -> filterActivityLogs();

                    case 7 -> clearActivityLogs();

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

            logActivity(
                    "USER_APPROVED",
                    "Administrator approved user ID: " + userId
            );

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

            logActivity(
                    "USER_UPDATED",
                    "Updated user ID: " + userId
                            + ", Role: " + newRole
                            + ", Status: " + newStatus
            );

            System.out.println("User role and status updated successfully!");

        } else {
            System.out.println("Update failed. User may no longer exist.");
        }
    }

    // VIEW ALL ACTIVITY LOGS
    private void viewAllActivityLogs() {

        System.out.println("\n========== ALL ACTIVITY LOGS ==========");

        try {
            List<ActivityLog> logs = activityLogDao.getAllLogs();

            displayActivityLogs(logs);

            logActivity(
                    "ACTIVITY_LOGS_VIEWED",
                    "Administrator viewed all activity logs"
            );

        } catch (RuntimeException e) {
            System.out.println("Unable to retrieve activity logs: "
                    + e.getMessage());
        }
    }

    // FILTER ACTIVITY LOGS BY USER ID
    private void filterActivityLogs() {

        System.out.println("\n========== FILTER ACTIVITY LOGS ==========");

        int userId = readIntInput("Enter User ID to filter (0 to cancel): ");

        if (userId == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            List<ActivityLog> logs = activityLogDao.getLogsByUserId(userId);

            displayActivityLogs(logs);

            logActivity(
                    "ACTIVITY_LOGS_FILTERED",
                    "Administrator filtered activity logs for User ID: " + userId
            );

        } catch (RuntimeException e) {
            System.out.println("Unable to retrieve activity logs: "
                    + e.getMessage());
        }
    }

    // DISPLAY ACTIVITY LOGS
    private void displayActivityLogs(List<ActivityLog> logs) {

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "USER ID",
                "ACTION TYPE",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (ActivityLog log : logs) {

            System.out.printf(
                    "%-5d | %-8d | %-18s | %-35s | %-20s%n",
                    log.getId(),
                    log.getUserId(),
                    log.getActionType(),
                    log.getDescription(),
                    log.getTimestamp()
            );
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------");
    }

    // CLEAR HISTORICAL ACTIVITY LOGS
    private void clearActivityLogs() {

        System.out.println("\n========== CLEAR ACTIVITY LOGS ==========");
        System.out.println("WARNING: This will permanently delete all historical activity logs.");

        System.out.print("Type CLEAR to confirm: ");

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equals("CLEAR")) {
            System.out.println("Operation cancelled. No logs were deleted.");
            return;
        }

        try {

            activityLogDao.clearAllLogs();

            System.out.println("Historical activity logs cleared successfully.");

            // Record the clearing action after deleting old logs
            logActivity(
                    "ACTIVITY_LOGS_CLEARED",
                    "Administrator cleared historical activity logs"
            );

        } catch (RuntimeException e) {
            System.out.println("Unable to clear activity logs: "
                    + e.getMessage());
        }
    }

    // RECORD ADMIN ACTIVITY
    private void logActivity(String actionType, String description) {

        try {
            activityLogDao.logAction(
                    currentAdmin.getId(),
                    actionType,
                    description
            );

        } catch (RuntimeException e) {
            System.out.println("Unable to record activity: "
                    + e.getMessage());
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
