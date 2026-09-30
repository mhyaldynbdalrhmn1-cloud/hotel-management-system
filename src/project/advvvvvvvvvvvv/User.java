package project.advvvvvvvvvvvv;

// User.java
// System user (for login). Extends Person to reuse fields.

public class User extends Person implements java.io.Serializable {

    private String username;
    private String password;
    private String role; // e.g. "Manager"

    // Constructor for login user
    public User(String username, String password, String role) {
        // We can use username as name, other fields empty
        super(username, "", "");
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Implementation of abstract getId() from Person
    @Override
    public String getId() {
        return username;
    }

    // Getters only (no setters needed for now)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // toString for debugging if needed
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
