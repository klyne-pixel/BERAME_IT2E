
package model;

public class AuditLog {

    private int id;
    private String username;
    private String action;
    private String logTime;

    public AuditLog(int id, String username, String action, String logTime) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.logTime = logTime;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getLogTime() {
        return logTime;
    }
}
