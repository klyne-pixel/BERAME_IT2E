
import dao.DatabaseInitializer;
import dao.UserDao;
import ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Step 1: Initialize the database
        DatabaseInitializer.initialize();

        // Step 2: Create shared objects
        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();

        // Step 3: Start authentication
        Authentication authentication =
                new Authentication(userDao, scanner);

        authentication.start();
    }
}
