
package model;

public class User {

    private final int id;
    private final String username;
    private final String password;
    private final String role;
    private final String status;

    // Keep the original constructor for existing code
    public User(int id, String username, String password, String role) {
        this(id, username, password, role, "PENDING");
    }

    // New constructor including account status
    public User(int id, String username, String password,
                String role, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
