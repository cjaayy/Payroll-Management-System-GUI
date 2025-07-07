package managers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Password Security utility for secure password handling
 */
public class PasswordSecurity {
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    // Password strength patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    
    /**
     * Generates a random salt for password hashing
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashes a password with the given salt
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            
            // Add salt to password
            String saltedPassword = password + salt;
            
            // Hash the salted password
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            
            // Convert to base64 string
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing algorithm not available", e);
        }
    }
    
    /**
     * Verifies if a password matches the stored hash
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(storedHash);
    }
    
    /**
     * Validates password strength according to security policies
     */
    public static PasswordStrength validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return new PasswordStrength(false, "Password must be at least 8 characters long");
        }
        
        if (password.length() > 128) {
            return new PasswordStrength(false, "Password must be less than 128 characters long");
        }
        
        boolean hasUpper = UPPERCASE_PATTERN.matcher(password).matches();
        boolean hasLower = LOWERCASE_PATTERN.matcher(password).matches();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).matches();
        boolean hasSpecial = SPECIAL_CHAR_PATTERN.matcher(password).matches();
        
        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        
        if (score < 3) {
            return new PasswordStrength(false, 
                "Password must contain at least 3 of the following: uppercase letter, lowercase letter, digit, special character");
        }
        
        // Check for common patterns
        if (password.toLowerCase().contains("password") || 
            password.toLowerCase().contains("123456") ||
            password.toLowerCase().contains("admin")) {
            return new PasswordStrength(false, "Password contains common patterns and is not secure");
        }
        
        return new PasswordStrength(true, "Password meets security requirements");
    }
    
    /**
     * Generates a secure random password
     */
    public static String generateSecurePassword(int length) {
        if (length < 8) length = 8;
        if (length > 128) length = 128;
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upperCase + lowerCase + digits + special;
        
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(upperCase.charAt(secureRandom.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(secureRandom.nextInt(lowerCase.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        password.append(special.charAt(secureRandom.nextInt(special.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        for (int i = password.length() - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }
        
        return password.toString();
    }
    
    /**
     * Inner class to represent password strength validation result
     */
    public static class PasswordStrength {
        private final boolean isValid;
        private final String message;
        
        public PasswordStrength(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
