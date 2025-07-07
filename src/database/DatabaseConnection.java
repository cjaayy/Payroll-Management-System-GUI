package database;

import java.sql.*;

/**
 * Database Connection Manager for Payroll Management System
 */
public class DatabaseConnection {
    private static Connection connection;
    private static boolean initialized = false;
    
    /**
     * Initialize the database connection and create tables if they don't exist
     */
    public static void initializeDatabase() throws SQLException {
        if (initialized) {
            return;
        }
        
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First, create the database if it doesn't exist
            createDatabaseIfNotExists();
            
            // Then connect to the specific database
            connection = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD
            );
            
            // Create tables if they don't exist
            createTables();
            
            initialized = true;
            System.out.println("Database connection initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initializeDatabase();
        }
        
        // Check if connection is still valid
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD
            );
        }
        
        return connection;
    }
    
    /**
     * Create the database if it doesn't exist
     */
    private static void createDatabaseIfNotExists() throws SQLException {
        String baseUrl = "jdbc:mysql://" + DatabaseConfig.DB_HOST + ":" + DatabaseConfig.DB_PORT + "/";
        
        try (Connection conn = DriverManager.getConnection(
                baseUrl,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(DatabaseConfig.CREATE_DATABASE);
            System.out.println("Database created or already exists: " + DatabaseConfig.DB_NAME);
        }
    }
    
    /**
     * Create necessary tables if they don't exist
     */
    private static void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            
            // Check if enhanced users table already exists
            boolean usersTableExists = false;
            boolean hasNewSchema = false;
            
            try {
                ResultSet tables = stmt.executeQuery("SHOW TABLES LIKE 'users'");
                if (tables.next()) {
                    usersTableExists = true;
                    tables.close();
                    
                    // Check if it has the new schema
                    ResultSet columns = stmt.executeQuery("SHOW COLUMNS FROM users LIKE 'user_id'");
                    if (columns.next()) {
                        hasNewSchema = true;
                    }
                    columns.close();
                }
            } catch (SQLException e) {
                // Table doesn't exist, we'll create it
                usersTableExists = false;
            }
            
            if (usersTableExists && !hasNewSchema) {
                // Old schema exists, need to backup and migrate
                System.out.println("Migrating users table to new schema...");
                try {
                    stmt.executeUpdate("CREATE TABLE users_backup AS SELECT * FROM users");
                    stmt.executeUpdate("DROP TABLE users");
                } catch (SQLException e) {
                    System.err.println("Migration warning: " + e.getMessage());
                }
            }
            
            // Create users table with enhanced security features (only if needed)
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "salt VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100) UNIQUE, " +
                "full_name VARCHAR(100), " +
                "role ENUM('ADMIN', 'HR_OFFICER', 'PAYROLL_OFFICER', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE', " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "failed_login_attempts INT DEFAULT 0, " +
                "lockout_until TIMESTAMP NULL, " +
                "last_login TIMESTAMP NULL, " +
                "last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "created_by VARCHAR(50), " +
                "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "last_modified_by VARCHAR(50), " +
                "INDEX idx_username (username), " +
                "INDEX idx_email (email), " +
                "INDEX idx_role (role), " +
                "INDEX idx_active (is_active)" +
                ")";
            stmt.executeUpdate(createUsersTable);
            
            // Create audit trail table
            String createAuditTable = "CREATE TABLE IF NOT EXISTS audit_trail (" +
                "audit_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "action VARCHAR(50) NOT NULL, " +
                "table_name VARCHAR(50), " +
                "record_id VARCHAR(50), " +
                "old_values TEXT, " +
                "new_values TEXT, " +
                "ip_address VARCHAR(45), " +
                "user_agent TEXT, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "is_success BOOLEAN DEFAULT TRUE, " +
                "error_message TEXT, " +
                "INDEX idx_username (username), " +
                "INDEX idx_action (action), " +
                "INDEX idx_timestamp (timestamp), " +
                "INDEX idx_table_record (table_name, record_id)" +
                ")";
            stmt.executeUpdate(createAuditTable);
            
            // Create employees table
            String createEmployeesTable = "CREATE TABLE IF NOT EXISTS employees (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_id VARCHAR(20) UNIQUE NOT NULL, " +
                "comprehensive_employee_id VARCHAR(50), " +
                "first_name VARCHAR(50) NOT NULL, " +
                "last_name VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "phone VARCHAR(20), " +
                "department VARCHAR(50), " +
                "position VARCHAR(50), " +
                "job_title VARCHAR(100), " +
                "manager VARCHAR(100), " +
                "hire_date DATE, " +
                "salary DECIMAL(10,2), " +
                "status VARCHAR(20) DEFAULT 'ACTIVE', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
            stmt.executeUpdate(createEmployeesTable);
            
            // Create payroll table
            String createPayrollTable = "CREATE TABLE IF NOT EXISTS payroll (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_id VARCHAR(20) NOT NULL, " +
                "pay_period_start DATE NOT NULL, " +
                "pay_period_end DATE NOT NULL, " +
                "basic_salary DECIMAL(10,2) NOT NULL, " +
                "overtime_hours DECIMAL(5,2) DEFAULT 0, " +
                "overtime_rate DECIMAL(5,2) DEFAULT 0, " +
                "bonus DECIMAL(10,2) DEFAULT 0, " +
                "deductions DECIMAL(10,2) DEFAULT 0, " +
                "gross_pay DECIMAL(10,2) NOT NULL, " +
                "tax_deduction DECIMAL(10,2) DEFAULT 0, " +
                "net_pay DECIMAL(10,2) NOT NULL, " +
                "status VARCHAR(20) DEFAULT 'DRAFT', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (employee_id) REFERENCES employees(employee_id)" +
                ")";
            stmt.executeUpdate(createPayrollTable);
            
            // Create employee contact information table
            String createContactInfoTable = "CREATE TABLE IF NOT EXISTS employee_contact_info (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_id VARCHAR(20) NOT NULL, " +
                "personal_email VARCHAR(100), " +
                "work_phone VARCHAR(20), " +
                "emergency_contact VARCHAR(100), " +
                "emergency_phone VARCHAR(20), " +
                "street_address TEXT, " +
                "barangay VARCHAR(100), " +
                "city VARCHAR(50), " +
                "province_state VARCHAR(50), " +
                "country VARCHAR(50), " +
                "zip_code VARCHAR(10), " +
                "birth_date DATE, " +
                "social_security_number VARCHAR(20), " +
                "nationality VARCHAR(50), " +
                "marital_status VARCHAR(20), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                ")";
            stmt.executeUpdate(createContactInfoTable);
            
            // Create address reference tables
            String createCountriesTable = "CREATE TABLE IF NOT EXISTS countries (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "country_code VARCHAR(3) UNIQUE NOT NULL, " +
                "country_name VARCHAR(100) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE" +
                ")";
            stmt.executeUpdate(createCountriesTable);
            
            String createProvincesStatesTable = "CREATE TABLE IF NOT EXISTS provinces_states (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "country_code VARCHAR(3) NOT NULL, " +
                "province_state_code VARCHAR(10) NOT NULL, " +
                "province_state_name VARCHAR(100) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "FOREIGN KEY (country_code) REFERENCES countries(country_code), " +
                "UNIQUE KEY unique_province_state (country_code, province_state_code)" +
                ")";
            stmt.executeUpdate(createProvincesStatesTable);
            
            String createCitiesTable = "CREATE TABLE IF NOT EXISTS cities (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "country_code VARCHAR(3) NOT NULL, " +
                "province_state_code VARCHAR(10) NOT NULL, " +
                "city_code VARCHAR(10) NOT NULL, " +
                "city_name VARCHAR(100) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "FOREIGN KEY (country_code) REFERENCES countries(country_code), " +
                "UNIQUE KEY unique_city (country_code, province_state_code, city_code)" +
                ")";
            stmt.executeUpdate(createCitiesTable);
            
            String createBarangaysTable = "CREATE TABLE IF NOT EXISTS barangays (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "country_code VARCHAR(3) NOT NULL, " +
                "province_state_code VARCHAR(10) NOT NULL, " +
                "city_code VARCHAR(10) NOT NULL, " +
                "barangay_code VARCHAR(10) NOT NULL, " +
                "barangay_name VARCHAR(100) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "FOREIGN KEY (country_code) REFERENCES countries(country_code), " +
                "UNIQUE KEY unique_barangay (country_code, province_state_code, city_code, barangay_code)" +
                ")";
            stmt.executeUpdate(createBarangaysTable);
            
            // Create employee documents table
            String createDocumentsTable = "CREATE TABLE IF NOT EXISTS employee_documents (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_id VARCHAR(20) NOT NULL, " +
                "document_type VARCHAR(50) NOT NULL, " +
                "file_name VARCHAR(255) NOT NULL, " +
                "file_path VARCHAR(500), " +
                "file_data LONGBLOB, " +
                "file_size BIGINT, " +
                "mime_type VARCHAR(100), " +
                "description TEXT, " +
                "uploaded_by VARCHAR(50), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                ")";
            stmt.executeUpdate(createDocumentsTable);
            
            // Drop and recreate salary components table to ensure correct schema
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS employee_salary_components");
                stmt.executeUpdate("DROP TABLE IF EXISTS salary_components");
                System.out.println("Dropped existing salary components tables for recreation...");
            } catch (SQLException e) {
                System.out.println("Tables didn't exist or couldn't be dropped: " + e.getMessage());
            }
            
            // Create salary components table
            String createSalaryComponentsTable = "CREATE TABLE IF NOT EXISTS salary_components (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "type VARCHAR(20) NOT NULL, " +
                "amount DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                "is_percentage BOOLEAN DEFAULT FALSE, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "created_date DATE DEFAULT (CURRENT_DATE), " +
                "last_modified DATE DEFAULT (CURRENT_DATE), " +
                "created_by VARCHAR(50), " +
                "modified_by VARCHAR(50), " +
                "UNIQUE KEY unique_component_name (name, type)" +
                ")";
            stmt.executeUpdate(createSalaryComponentsTable);
            
            // Create employee salary components table
            String createEmployeeSalaryComponentsTable = "CREATE TABLE IF NOT EXISTS employee_salary_components (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_id VARCHAR(20) NOT NULL, " +
                "salary_component_id INT NOT NULL, " +
                "custom_amount DECIMAL(10,2) NOT NULL, " +
                "is_percentage BOOLEAN DEFAULT FALSE, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "effective_date DATE NOT NULL, " +
                "end_date DATE NULL, " +
                "created_date DATE DEFAULT (CURRENT_DATE), " +
                "created_by VARCHAR(50), " +
                "remarks TEXT, " +
                "FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (salary_component_id) REFERENCES salary_components(id) ON DELETE CASCADE, " +
                "UNIQUE KEY unique_employee_component (employee_id, salary_component_id, effective_date)" +
                ")";
            stmt.executeUpdate(createEmployeeSalaryComponentsTable);
            
            // Note: Default users are now created by UserManager.initializeDefaultAdmin()
            // This ensures proper password hashing and security
            
            System.out.println("Database tables created successfully!");
        }
    }
    
    /**
     * Test if the database connection is working
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                // Test with a simple query
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                initialized = false;
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
