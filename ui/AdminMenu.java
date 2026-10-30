package ui;

import dao.UserDao;
import model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n================================");
            System.out.println("       ADMIN USER MENU");
            System.out.println("================================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> viewAllUsers();
                    case "2" -> viewPendingUsers();
                    case "3" -> approveUser();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void viewAllUsers() throws SQLException {
        List<User> users = userDao.getAllUsers();

        System.out.println("\nALL SYSTEM USERS");
        displayUsers(users);
    }

    private void viewPendingUsers() throws SQLException {
        List<User> users = userDao.getPendingUsers();

        System.out.println("\nPENDING REGISTRATIONS");
        displayUsers(users);
    }

    private void approveUser() throws SQLException {
        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }

        System.out.println("\nPENDING USERS");
        displayUsers(pendingUsers);

        System.out.print("\nEnter User ID to approve (0 to cancel): ");
        String input = scanner.nextLine();

        if (input.equals("0")) {
            System.out.println("Approval cancelled.");
            return;
        }

        try {
            int userId = Integer.parseInt(input);

            boolean exists = pendingUsers.stream()
                    .anyMatch(user -> user.getId() == userId);

            if (!exists) {
                System.out.println("User ID not found in pending registrations.");
                return;
            }

            if (userDao.approveUser(userId)) {
                System.out.println("Account approved successfully!");
            } else {
                System.out.println("Account approval failed.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
        }
    }

    private void displayUsers(List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";

        System.out.println(border);
        System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n",
                "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf("| %-51s |%n", "No users found.");
        } else {
            for (User user : users) {
                System.out.printf("| %-5d | %-15s | %-12s | %-10s |%n",
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getStatus());
            }
        }

        System.out.println(border);
    }
}