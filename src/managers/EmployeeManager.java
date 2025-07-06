package managers;

import models.Employee;
import models.EmployeeDocument;
import database.DatabaseDAO;
import database.MySQLDatabaseDAO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Employee Manager for managing employee operations with database persistence
 */
public class EmployeeManager {
    private DatabaseDAO databaseDAO;
    private MySQLDatabaseDAO mySQLDAO;
    
    public EmployeeManager() {
        databaseDAO = new MySQLDatabaseDAO();
        mySQLDAO = (MySQLDatabaseDAO) databaseDAO;
        // Don't pre-calculate nextEmployeeId, calculate it fresh each time
        // Don't initialize sample data - let the DatabaseConnection handle it
    }
    
    private int getNextEmployeeId() {
        List<Employee> employees = databaseDAO.getAllEmployeesForIdCheck(); // Check ALL employees for ID conflicts
        System.out.println("Found " + employees.size() + " existing employees in database (including inactive):");
        int maxId = 0;
        for (Employee emp : employees) {
            System.out.println("  - Employee ID: " + emp.getEmployeeId() + " (" + emp.getFullName() + ")");
            if (emp.getEmployeeId() > maxId) {
                maxId = emp.getEmployeeId();
            }
        }
        int nextId = maxId + 1;
        System.out.println("Next available ID: " + nextId);
        return nextId;
    }
    
    private String generateEmployeeStringId(int numericId) {
        return String.format("EMP%03d", numericId);
    }
    
    /**
     * Generate a comprehensive employee ID based on department and hire date
     * Format: DEPT-YYYYMM-NNN (e.g., IT-202501-001, HR-202501-002)
     * @param department The employee's department
     * @param hireDate The employee's hire date
     * @param sequenceNumber The sequence number for this department/month combination
     * @return Formatted employee ID string
     */
    private String generateComprehensiveEmployeeId(String department, LocalDate hireDate, int sequenceNumber) {
        // Generate department code (max 3 characters)
        String deptCode = getDepartmentCode(department);
        
        // Format hire date as YYYYMM
        String dateCode = hireDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        // Format sequence number with leading zeros
        String seqCode = String.format("%03d", sequenceNumber);
        
        return String.format("%s-%s-%s", deptCode, dateCode, seqCode);
    }
    
    /**
     * Generate department code from department name
     * @param department Full department name
     * @return 2-3 character department code
     */
    private String getDepartmentCode(String department) {
        if (department == null || department.trim().isEmpty()) {
            return "GEN"; // General
        }
        
        String dept = department.trim().toUpperCase();
        
        // Common department mappings
        switch (dept) {
            case "INFORMATION TECHNOLOGY":
            case "IT":
                return "IT";
            case "HUMAN RESOURCES":
            case "HR":
                return "HR";
            case "FINANCE":
            case "ACCOUNTING":
                return "FIN";
            case "MARKETING":
                return "MKT";
            case "SALES":
                return "SAL";
            case "OPERATIONS":
                return "OPS";
            case "RESEARCH AND DEVELOPMENT":
            case "R&D":
                return "RND";
            case "CUSTOMER SERVICE":
                return "CS";
            case "ADMINISTRATION":
            case "ADMIN":
                return "ADM";
            case "LEGAL":
                return "LEG";
            case "PROCUREMENT":
                return "PRC";
            case "QUALITY ASSURANCE":
            case "QA":
                return "QA";
            case "ENGINEERING":
                return "ENG";
            case "PRODUCTION":
                return "PRD";
            case "LOGISTICS":
                return "LOG";
            default:
                // Generate code from first letters of words
                String[] words = dept.split("\\s+");
                if (words.length >= 2) {
                    return (words[0].charAt(0) + "" + words[1].charAt(0)).toUpperCase();
                } else if (words.length == 1 && words[0].length() >= 3) {
                    return words[0].substring(0, 3).toUpperCase();
                } else {
                    return "GEN";
                }
        }
    }
    
    public void addEmployee(Employee employee) {
        // Set a proper ID for database storage
        employee.setEmployeeId(getNextEmployeeId());
        databaseDAO.insertEmployee(employee);
    }
    
    public Employee createEmployee(String firstName, String lastName, String email, 
                                 String department, String position, double baseSalary, LocalDate hireDate) {
        // Generate comprehensive employee ID
        String comprehensiveId = generateNextComprehensiveEmployeeId(department, hireDate);
        
        Employee employee = new Employee(getNextEmployeeId(), firstName, lastName, email, 
                                       department, position, baseSalary, hireDate);
        
        // Store the comprehensive ID as a custom property
        employee.setComprehensiveEmployeeId(comprehensiveId);
        
        if (databaseDAO.insertEmployee(employee)) {
            return employee;
        }
        return null;
    }
    
    public Employee createEmployee(String firstName, String lastName, String email, String phone,
                                 String department, String position, double baseSalary, LocalDate hireDate) {
        System.out.println("Creating employee with phone: " + phone);
        
        // Generate comprehensive employee ID
        String comprehensiveId = generateNextComprehensiveEmployeeId(department, hireDate);
        
        Employee employee = new Employee(getNextEmployeeId(), firstName, lastName, email, 
                                       department, position, baseSalary, hireDate);
        employee.setPhone(phone);
        
        // Store the comprehensive ID as a custom property
        employee.setComprehensiveEmployeeId(comprehensiveId);
        
        System.out.println("Employee created with ID: " + employee.getEmployeeId() + " (" + comprehensiveId + ")");
        if (databaseDAO.insertEmployee(employee)) {
            System.out.println("Employee successfully inserted");
            return employee;
        }
        System.out.println("Failed to insert employee");
        return null;
    }
    
    public Employee createEmployee(String firstName, String lastName, String email, String phone,
                                 String department, String position, String jobTitle, String manager,
                                 double baseSalary, LocalDate hireDate) {
        System.out.println("Creating employee with employment details - Phone: " + phone + ", Job Title: " + jobTitle + ", Manager: " + manager);
        
        // Generate comprehensive employee ID
        String comprehensiveId = generateNextComprehensiveEmployeeId(department, hireDate);
        
        Employee employee = new Employee(getNextEmployeeId(), firstName, lastName, email, 
                                       department, position, baseSalary, hireDate);
        employee.setPhone(phone);
        employee.setJobTitle(jobTitle);
        employee.setManager(manager);
        
        // Store the comprehensive ID as a custom property
        employee.setComprehensiveEmployeeId(comprehensiveId);
        
        System.out.println("Employee created with ID: " + employee.getEmployeeId() + " (" + comprehensiveId + ")");
        if (databaseDAO.insertEmployee(employee)) {
            System.out.println("Employee successfully inserted");
            return employee;
        }
        System.out.println("Failed to insert employee");
        return null;
    }
    
    /**
     * Generate the next available comprehensive employee ID for a department/hire date combination
     * @param department The employee's department
     * @param hireDate The employee's hire date
     * @return The next available comprehensive employee ID
     */
    private String generateNextComprehensiveEmployeeId(String department, LocalDate hireDate) {
        List<Employee> employees = databaseDAO.getAllEmployeesForIdCheck(); // Check ALL employees for ID conflicts
        String deptCode = getDepartmentCode(department);
        String dateCode = hireDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String prefix = deptCode + "-" + dateCode + "-";
        
        int maxSequence = 0;
        for (Employee emp : employees) {
            String empId = emp.getComprehensiveEmployeeId();
            if (empId != null && empId.startsWith(prefix)) {
                try {
                    String seqPart = empId.substring(prefix.length());
                    int sequence = Integer.parseInt(seqPart);
                    maxSequence = Math.max(maxSequence, sequence);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    // Ignore malformed IDs
                }
            }
        }
        
        return generateComprehensiveEmployeeId(department, hireDate, maxSequence + 1);
    }
    
    public Employee getEmployee(int employeeId) {
        String stringId = generateEmployeeStringId(employeeId);
        return databaseDAO.getEmployeeById(stringId);
    }
    
    public List<Employee> getAllEmployees() {
        return databaseDAO.getAllEmployees();
    }
    
    public List<Employee> getActiveEmployees() {
        return databaseDAO.getAllEmployees().stream()
                .filter(Employee::isActive)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public boolean updateEmployee(int employeeId, String firstName, String lastName, String email, 
                                String phone, String department, String position, double baseSalary, LocalDate hireDate) {
        System.out.println("EmployeeManager: Updating employee ID " + employeeId + " with email: " + email + " and hire date: " + hireDate);
        String stringId = generateEmployeeStringId(employeeId);
        Employee employee = databaseDAO.getEmployeeById(stringId);
        if (employee != null) {
            System.out.println("Found employee: " + employee.getFullName() + " (current email: " + employee.getEmail() + ", current hire date: " + employee.getHireDate() + ")");
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhone(phone);
            employee.setDepartment(department);
            employee.setPosition(position);
            employee.setBaseSalary(baseSalary);
            employee.setHireDate(hireDate);
            return databaseDAO.updateEmployee(employee);
        } else {
            System.err.println("Employee not found for ID: " + stringId);
        }
        return false;
    }
    
    public boolean updateEmployee(int employeeId, String firstName, String lastName, String email, 
                                String phone, String department, String position, String jobTitle, String manager,
                                double baseSalary, LocalDate hireDate) {
        System.out.println("EmployeeManager: Updating employee ID " + employeeId + " with employment details - Job Title: " + jobTitle + ", Manager: " + manager);
        String stringId = generateEmployeeStringId(employeeId);
        Employee employee = databaseDAO.getEmployeeById(stringId);
        if (employee != null) {
            System.out.println("Found employee: " + employee.getFullName() + " (current email: " + employee.getEmail() + ", current hire date: " + employee.getHireDate() + ")");
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhone(phone);
            employee.setDepartment(department);
            employee.setPosition(position);
            employee.setJobTitle(jobTitle);
            employee.setManager(manager);
            employee.setBaseSalary(baseSalary);
            employee.setHireDate(hireDate);
            return databaseDAO.updateEmployee(employee);
        } else {
            System.err.println("Employee not found for ID: " + stringId);
        }
        return false;
    }
    
    public boolean deleteEmployee(int employeeId) {
        String stringId = generateEmployeeStringId(employeeId);
        return databaseDAO.deleteEmployee(stringId);
    }
    
    public List<Employee> searchEmployees(String keyword) {
        return databaseDAO.getAllEmployees().stream()
                .filter(emp -> emp.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                              emp.getDepartment().toLowerCase().contains(keyword.toLowerCase()) ||
                              emp.getPosition().toLowerCase().contains(keyword.toLowerCase()) ||
                              emp.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                              (emp.getPhone() != null && emp.getPhone().toLowerCase().contains(keyword.toLowerCase())) ||
                              String.valueOf(emp.getEmployeeId()).contains(keyword) ||
                              generateEmployeeStringId(emp.getEmployeeId()).toLowerCase().contains(keyword.toLowerCase()) ||
                              (emp.getComprehensiveEmployeeId() != null && emp.getComprehensiveEmployeeId().toLowerCase().contains(keyword.toLowerCase())))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Check if an email is already used by another employee
     * @param email The email to check
     * @param excludeEmployeeId The employee ID to exclude from the check (for updates)
     * @return true if email is available, false if already in use
     */
    public boolean isEmailAvailable(String email, int excludeEmployeeId) {
        List<Employee> employees = databaseDAO.getAllEmployees();
        for (Employee emp : employees) {
            if (emp.getEmail().equalsIgnoreCase(email) && emp.getEmployeeId() != excludeEmployeeId) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Generate a formatted employee ID string from numeric ID
     * @param numericId The numeric employee ID
     * @return Formatted employee ID string (e.g., "EMP001")
     */
    public String getFormattedEmployeeId(int numericId) {
        return generateEmployeeStringId(numericId);
    }
    
    /**
     * Get the next available employee ID that will be assigned
     * @return The next available employee ID
     */
    public int getNextAvailableEmployeeId() {
        return getNextEmployeeId();
    }
    
    /**
     * Check if an employee ID is available
     * @param employeeId The numeric employee ID to check
     * @return true if the ID is available, false if already in use
     */
    public boolean isEmployeeIdAvailable(int employeeId) {
        String stringId = generateEmployeeStringId(employeeId);
        Employee existing = databaseDAO.getEmployeeById(stringId);
        return existing == null;
    }
    
    /**
     * Get employee by formatted ID string (e.g., "EMP001")
     * @param formattedId The formatted employee ID string
     * @return The employee if found, null otherwise
     */
    public Employee getEmployeeByFormattedId(String formattedId) {
        return databaseDAO.getEmployeeById(formattedId);
    }
    
    /**
     * Parse numeric ID from formatted employee ID string
     * @param formattedId The formatted employee ID (e.g., "EMP001", "IT-202501-001")
     * @return The numeric ID, or -1 if invalid format
     */
    public int parseEmployeeId(String formattedId) {
        if (formattedId == null || formattedId.trim().isEmpty()) {
            return -1;
        }
        
        String id = formattedId.trim();
        
        // Handle old format (EMP001)
        if (id.startsWith("EMP")) {
            try {
                return Integer.parseInt(id.substring(3));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        
        // Handle comprehensive format (IT-202501-001)
        if (id.matches("^[A-Z]{2,3}-\\d{6}-\\d{3}$")) {
            // Find employee by comprehensive ID
            List<Employee> employees = databaseDAO.getAllEmployees();
            for (Employee emp : employees) {
                if (id.equals(emp.getComprehensiveEmployeeId())) {
                    return emp.getEmployeeId();
                }
            }
            return -1;
        }
        
        // Handle plain numeric format
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // Enhanced employee management with contact info and documents
    public boolean saveEmployeeWithContactInfo(Employee employee) {
        boolean success = false;
        
        // First save basic employee info
        if (employee.getEmployeeId() == 0) {
            employee.setEmployeeId(getNextEmployeeId());
            success = databaseDAO.insertEmployee(employee);
        } else {
            success = databaseDAO.updateEmployee(employee);
        }
        
        // If basic info saved successfully, try to save contact info
        if (success) {
            try {
                System.out.println("Attempting to save contact info for employee: " + employee.getFormattedEmployeeId());
                boolean contactInfoSaved = mySQLDAO.insertEmployeeContactInfo(employee);
                
                if (!contactInfoSaved) {
                    System.err.println("Warning: Contact information could not be saved for employee: " + employee.getFormattedEmployeeId());
                } else {
                    System.out.println("Contact information saved successfully for employee: " + employee.getFormattedEmployeeId());
                }
                
                // Save documents
                if (employee.getDocuments() != null && !employee.getDocuments().isEmpty()) {
                    System.out.println("Attempting to save " + employee.getDocuments().size() + " documents for employee: " + employee.getFormattedEmployeeId());
                    int savedDocuments = 0;
                    for (EmployeeDocument document : employee.getDocuments()) {
                        if (document.getDocumentId() == 0) { // New document
                            document.setEmployeeId(employee.getEmployeeId());
                            System.out.println("Saving document: " + document.getFileName() + " for employee ID: " + employee.getEmployeeId());
                            boolean documentSaved = mySQLDAO.insertEmployeeDocument(document);
                            if (documentSaved) {
                                savedDocuments++;
                                System.out.println("Document saved successfully: " + document.getFileName());
                            } else {
                                System.err.println("Failed to save document: " + document.getFileName());
                            }
                        }
                    }
                    System.out.println("Successfully saved " + savedDocuments + " out of " + employee.getDocuments().size() + " documents");
                } else {
                    System.out.println("No documents to save for employee: " + employee.getFormattedEmployeeId());
                }
            } catch (Exception e) {
                System.err.println("Error saving contact info/documents: " + e.getMessage());
                e.printStackTrace();
                // Don't fail the whole operation if contact info can't be saved
            }
        }
        
        return success;
    }
    
    public void loadEmployeeContactInfo(Employee employee) {
        try {
            mySQLDAO.loadEmployeeContactInfo(employee);
            
            // Load documents
            List<EmployeeDocument> documents = mySQLDAO.getEmployeeDocuments(employee);
            employee.setDocuments(documents);
        } catch (Exception e) {
            // Handle gracefully if tables don't exist yet
            System.err.println("Note: Contact info tables not available yet. " + e.getMessage());
            // Initialize empty contact info
            employee.setPersonalEmail("");
            employee.setWorkPhone("");
            employee.setEmergencyContact("");
            employee.setEmergencyPhone("");
            employee.setStreetAddress("");
            employee.setCity("");
            employee.setProvinceState("");
            employee.setZipCode("");
            employee.setCountry("");
            employee.setBirthDate(null);
            employee.setSocialSecurityNumber("");
            employee.setNationality("");
            employee.setMaritalStatus("");
            employee.setDocuments(new ArrayList<>());
        }
    }
    
    public boolean deleteEmployeeDocument(int documentId) {
        return mySQLDAO.deleteEmployeeDocument(documentId);
    }
    
    public boolean saveEmployeeDocument(EmployeeDocument document) {
        return mySQLDAO.insertEmployeeDocument(document);
    }
    
    // Enhanced getAllEmployees method to include contact info and documents
    public List<Employee> getAllEmployeesWithContactInfo() {
        List<Employee> employees = databaseDAO.getAllEmployees();
        
        // Load contact info and documents for each employee
        for (Employee employee : employees) {
            loadEmployeeContactInfo(employee);
        }
        
        return employees;
    }
}
