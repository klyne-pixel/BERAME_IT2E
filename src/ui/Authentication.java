
package ui;

import dao.UserDao;
import model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n==================================");
            System.out.println(" GYM MEMBERSHIP AND FITNESS SYSTEM");
            System.out.println("==================================");
            System.out.println("1. Login");
            System.out.println("2. Register Account");
            System.out.println("0. Exit");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1" -> login();

                    case "2" -> register();

                    case "0" -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }

                    default -> System.out.println("Invalid option.");
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    // LOGIN
    private void login() throws SQLException {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if (!"APPROVED".equalsIgnoreCase(user.getStatus())) {
            System.out.println(
                    "Your account is not approved yet. Please contact the administrator."
            );
            return;
        }

        System.out.println("\nWelcome, " + user.getUsername() + "!");
        System.out.println("Role: " + user.getRole());

        // Open the gym system menu
        new ConsoleMenu(userDao, scanner, user).start();
    }

    // REGISTER ACCOUNT
    private void register() throws SQLException {

        System.out.println("\n========== REGISTER ACCOUNT ==========");

        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        System.out.print("Choose a password: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.println("\nSelect Role:");
        System.out.println("1. Administrator");
        System.out.println("2. Staff");
        System.out.println("3. Manager");

        System.out.print("Choose role: ");
        String choice = scanner.nextLine().trim();

        String role = switch (choice) {
            case "1" -> "Administrator";
            case "2" -> "Staff";
            case "3" -> "Manager";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role.");
            return;
        }

        boolean success = userDao.addUser(username, password, role);

        if (success) {
            System.out.println(
                    "Account registered successfully. Status: PENDING"
            );
            System.out.println(
                    "Please wait for administrator approval before logging in."
            );
        } else {
            System.out.println(
                    "Registration failed. Username may already exist."
            );
        }
    }
}
