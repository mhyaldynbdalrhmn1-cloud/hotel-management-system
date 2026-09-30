package project.advvvvvvvvvvvv;

public class AuthService {

    public AuthService() {}

    public void addUser(User user) {
        DatabaseConnection.registerUser(user);
    }

    public User authenticateUser(String username, String password) {
        return DatabaseConnection.authenticateUser(username, password);
    }

    public boolean register(String username, String password, String role) {
        return DatabaseConnection.registerUser(new User(username, password, role));
    }
}