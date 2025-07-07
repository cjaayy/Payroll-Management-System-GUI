package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
     private void initializePersonalInfo() {
        this.personalEmail = "";
        this.workPhone = "";
        this.emergencyContact = "";
        this.emergencyPhone = "";
        this.streetAddress = "";
        this.barangay = "";
        this.city = "";
        this.provinceState = "";
        this.country = "";
        this.zipCode = "";
        this.birthDate = null;
        this.socialSecurityNumber = "";
        this.nationality = "";
        this.maritalStatus = "";
    }
    
    /**
     * Employee class representing an employee in the payroll system
     */
    public class Employee {
    private int employeeId;
    private String comprehensiveEmployeeId; // New field for comprehensive ID (e.g., IT-202501-001)
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private String jobTitle;        // More specific than position
    private String manager;         // Manager's name or ID
    private double baseSalary;
    private LocalDate hireDate;
    private boolean isActive;
    
    // Contact and Personal Information
    private String personalEmail;
    private String workPhone;
    private String emergencyContact;
    private String emergencyPhone;
    private String streetAddress;
    private String barangay;
    private String city;
    private String provinceState;
    private String country;
    private String zipCode;
    private LocalDate birthDate;
    private String socialSecurityNumber;
    private String nationality;
    private String maritalStatus;
    
    // Employment Status and Key Dates
    private String employmentStatus;  // ACTIVE, INACTIVE, RESIGNED, TERMINATED
    private LocalDate joiningDate;    // Same as hire date but more explicit
    private LocalDate probationEndDate;
    private LocalDate exitDate;
    private String exitReason;
    
    // Bank and Payment Details
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String bankBranch;
    private String routingNumber;
    private String paymentMethod;     // BANK_TRANSFER, CASH, CHECK
    private String paymentFrequency;  // WEEKLY, BIWEEKLY, MONTHLY
    
    // Document Storage
    private List<EmployeeDocument> documents;
    
    public Employee(int employeeId, String firstName, String lastName, String email, 
                   String department, String position, double baseSalary, LocalDate hireDate) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = null;
        this.department = department;
        this.position = position;
        this.jobTitle = position; // Default to position if not specified
        this.manager = null;
        this.baseSalary = baseSalary;
        this.hireDate = hireDate;
        this.isActive = true;
        this.comprehensiveEmployeeId = null; // Will be set by EmployeeManager
        this.documents = new ArrayList<>();
        initializePersonalInfo();
        initializeEmploymentInfo();
    }
    
    // Constructor for database operations
    public Employee(String employeeId, String firstName, String lastName, String email, 
                   String phone, String department, String position, java.util.Date hireDate, double baseSalary) {
        // Extract numeric part from string ID (e.g., "EMP001" -> 1)
        if (employeeId.startsWith("EMP")) {
            try {
                this.employeeId = Integer.parseInt(employeeId.substring(3));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing employee ID: " + employeeId);
                this.employeeId = 0; // Default value
            }
        } else {
            try {
                this.employeeId = Integer.parseInt(employeeId);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing employee ID: " + employeeId);
                this.employeeId = 0; // Default value
            }
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone != null ? phone : "";
        this.department = department;
        this.position = position;
        this.jobTitle = position; // Default to position if not specified
        this.manager = null; // Will be set separately if needed
        this.baseSalary = baseSalary;
        this.hireDate = new java.sql.Date(hireDate.getTime()).toLocalDate();
        this.isActive = true;
        this.comprehensiveEmployeeId = null; // Will be generated if needed
        this.documents = new ArrayList<>();
        initializePersonalInfo();
        initializeEmploymentInfo();
    }
    
    // Constructor for database operations with employment details
    public Employee(String employeeId, String firstName, String lastName, String email, 
                   String phone, String department, String position, String jobTitle, 
                   String manager, java.util.Date hireDate, double baseSalary) {
        // Extract numeric part from string ID (e.g., "EMP001" -> 1)
        if (employeeId.startsWith("EMP")) {
            try {
                this.employeeId = Integer.parseInt(employeeId.substring(3));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing employee ID: " + employeeId);
                this.employeeId = 0; // Default value
            }
        } else {
            try {
                this.employeeId = Integer.parseInt(employeeId);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing employee ID: " + employeeId);
                this.employeeId = 0; // Default value
            }
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone != null ? phone : "";
        this.department = department;
        this.position = position;
        this.jobTitle = jobTitle != null ? jobTitle : position; // Default to position if null
        this.manager = manager;
        this.baseSalary = baseSalary;
        this.hireDate = new java.sql.Date(hireDate.getTime()).toLocalDate();
        this.isActive = true;
        this.comprehensiveEmployeeId = null; // Will be generated if needed
        this.documents = new ArrayList<>();
        initializePersonalInfo();
        initializeEmploymentInfo();
    }
    
    private void initializePersonalInfo() {
        this.personalEmail = "";
        this.workPhone = "";
        this.emergencyContact = "";
        this.emergencyPhone = "";
        this.streetAddress = "";
        this.barangay = "";
        this.city = "";
        this.provinceState = "";
        this.country = "";
        this.zipCode = "";
        this.birthDate = null;
        this.socialSecurityNumber = "";
        this.nationality = "";
        this.maritalStatus = "";
    }
    
    private void initializeEmploymentInfo() {
        this.employmentStatus = "ACTIVE";
        this.joiningDate = this.hireDate; // Default joining date to hire date
        this.probationEndDate = null;
        this.exitDate = null;
        this.exitReason = "";
        this.bankName = "";
        this.accountNumber = "";
        this.accountHolderName = "";
        this.bankBranch = "";
        this.routingNumber = "";
        this.paymentMethod = "BANK_TRANSFER";
        this.paymentFrequency = "MONTHLY";
    }
    
    // Getters and setters
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeIdString() { 
        return String.format("EMP%03d", employeeId); 
    }
    
    public String getComprehensiveEmployeeId() { return comprehensiveEmployeeId; }
    public void setComprehensiveEmployeeId(String comprehensiveEmployeeId) { this.comprehensiveEmployeeId = comprehensiveEmployeeId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getSalary() { return baseSalary; } // Alias for database compatibility
    public void setSalary(double salary) { this.baseSalary = salary; } // Alias for database compatibility
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }
    
    public String getWorkPhone() { return workPhone; }
    public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    
    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getProvinceState() { return provinceState; }
    public void setProvinceState(String provinceState) { this.provinceState = provinceState; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public String getSocialSecurityNumber() { return socialSecurityNumber; }
    public void setSocialSecurityNumber(String socialSecurityNumber) { this.socialSecurityNumber = socialSecurityNumber; }
    
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    
    // Employment Status and Key Dates Getters and Setters
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    
    public LocalDate getProbationEndDate() { return probationEndDate; }
    public void setProbationEndDate(LocalDate probationEndDate) { this.probationEndDate = probationEndDate; }
    
    public LocalDate getExitDate() { return exitDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }
    
    public String getExitReason() { return exitReason; }
    public void setExitReason(String exitReason) { this.exitReason = exitReason; }
    
    // Bank and Payment Details Getters and Setters
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    
    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
    
    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentFrequency() { return paymentFrequency; }
    public void setPaymentFrequency(String paymentFrequency) { this.paymentFrequency = paymentFrequency; }
    
    // Document Management
    public List<EmployeeDocument> getDocuments() { return documents; }
    public void setDocuments(List<EmployeeDocument> documents) { this.documents = documents; }
    
    public void addDocument(EmployeeDocument document) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        this.documents.add(document);
    }
    
    public void removeDocument(EmployeeDocument document) {
        if (this.documents != null) {
            this.documents.remove(document);
        }
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Get formatted employee ID (e.g., "IT-202501-001" or "EMP001" as fallback)
     * @return Formatted employee ID string
     */
    public String getFormattedEmployeeId() {
        if (comprehensiveEmployeeId != null && !comprehensiveEmployeeId.trim().isEmpty()) {
            return comprehensiveEmployeeId;
        }
        return String.format("EMP%03d", employeeId);
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (streetAddress != null && !streetAddress.trim().isEmpty()) {
            sb.append(streetAddress);
        }
        if (barangay != null && !barangay.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(barangay);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (provinceState != null && !provinceState.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(provinceState);
        }
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(zipCode);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        String managerInfo = (manager != null && !manager.trim().isEmpty()) ? " | Manager: " + manager : "";
        return String.format("ID: %s | %s | %s | %s%s | $%.2f | %s", 
                getFormattedEmployeeId(), getFullName(), department, position, managerInfo, baseSalary, 
                hireDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    // Backward compatibility methods for old field names
    @Deprecated
    public String getAddress() { return streetAddress; }
    
    @Deprecated
    public void setAddress(String address) { this.streetAddress = address; }
    
    @Deprecated
    public String getState() { return provinceState; }
    
    @Deprecated
    public void setState(String state) { this.provinceState = state; }
}
