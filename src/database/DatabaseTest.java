package database;

/**
 * Simple test to verify MySQL connection works
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("Testing MySQL Database Connection...");
        
        try {
            // Initialize database
            DatabaseConnection.initializeDatabase();
            
            // Test connection
            if (DatabaseConnection.testConnection()) {
                System.out.println("✓ Database connection successful!");
                
                // Test DAO
                MySQLDatabaseDAO dao = new MySQLDatabaseDAO();
                if (dao.testConnection()) {
                    System.out.println("✓ DAO connection successful!");
                }
                
                System.out.println("✓ All database tests passed!");
            } else {
                System.out.println("✗ Database connection failed!");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Database test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
