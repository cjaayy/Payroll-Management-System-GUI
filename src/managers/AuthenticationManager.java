package managers;

import models.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Manager for user login/logout
 */
public class AuthenticationManager {
    private Map<String, User> users;
    private User currentUser;
    
    public AuthenticationManager() {
        users = new HashMap<>();
        // Initialize default users
        users.put("admin", new User("admin", "admin123", "admin"));
        users.put("hr", new User("hr", "hr123", "hr"));
    }
    
    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean hasAdminRole() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
}
