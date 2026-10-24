
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
        System.out.println("==================================");
        System.out.println(" GYM MEMBERSHIP AND FITNESS SYSTEM");
        System.out.println("==================================");
        System.out.println("1. Login");
        System.out.println("2. Register Account");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void login() throws SQLException {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        System.out.println("Welcome, " + user.getUsername() + "!");
        System.out.println("Role: " + user.getRole());

        // The existing ConsoleMenu still handles gym features.
        new ConsoleMenu().start();
    }

    private void register() throws SQLException {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();

        System.out.print("Choose a password: ");
        String password = scanner.nextLine();

        System.out.println("Select role:");
        System.out.println("1. Administrator");
        System.out.println("2. Staff");
        System.out.println("3. Manager");
        System.out.print("Role: ");

        String choice = scanner.nextLine();

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

        if (userDao.addUser(username, password, role)) {
            System.out.println(
                    "Account registered successfully. Status: PENDING"
            );
        } else {
            System.out.println("Registration failed.");
        }
    }
}
