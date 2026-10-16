
import dao.DatabaseConnection;
import ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            new ConsoleMenu().start();
        } catch (Exception e) {
            System.out.println("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
