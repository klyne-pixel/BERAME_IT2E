
package model;

public class User {

    private int id;
    private String username;
    private String password;
    private String role;
    private String status;

    public User(int id, String username, String password, String role) {
        this(id, username, password, role, "PENDING");
    }

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
