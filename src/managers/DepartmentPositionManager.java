package managers;

import java.util.*;

/**
 * Manages department and position mappings for the company
 */
public class DepartmentPositionManager {
    
    private static final Map<String, String[]> DEPARTMENT_POSITIONS = new HashMap<>();
    
    static {
        // Information Technology Department
        DEPARTMENT_POSITIONS.put("Information Technology", new String[]{
            "Software Developer", "Senior Software Developer", "Software Engineer", 
            "Senior Software Engineer", "Full Stack Developer", "Frontend Developer", 
            "Backend Developer", "DevOps Engineer", "System Administrator", 
            "Network Administrator", "Database Administrator", "IT Support Specialist", 
            "Cybersecurity Analyst", "IT Manager", "Technical Lead", "Solutions Architect"
        });
        
        // Human Resources Department
        DEPARTMENT_POSITIONS.put("Human Resources", new String[]{
            "HR Assistant", "HR Specialist", "HR Generalist", "Recruiter", 
            "Senior Recruiter", "Talent Acquisition Specialist", "HR Business Partner", 
            "Compensation Analyst", "Benefits Administrator", "Training Coordinator", 
            "Employee Relations Specialist", "HR Manager", "HR Director"
        });
        
        // Finance Department
        DEPARTMENT_POSITIONS.put("Finance", new String[]{
            "Accounting Clerk", "Bookkeeper", "Junior Accountant", "Staff Accountant", 
            "Senior Accountant", "Financial Analyst", "Budget Analyst", "Tax Specialist", 
            "Payroll Specialist", "Accounts Payable Specialist", "Accounts Receivable Specialist", 
            "Financial Controller", "Finance Manager", "CFO"
        });
        
        // Marketing Department
        DEPARTMENT_POSITIONS.put("Marketing", new String[]{
            "Marketing Assistant", "Marketing Coordinator", "Marketing Specialist", 
            "Digital Marketing Specialist", "Content Creator", "Social Media Manager", 
            "SEO Specialist", "Email Marketing Specialist", "Brand Manager", 
            "Product Marketing Manager", "Marketing Manager", "Marketing Director"
        });
        
        // Sales Department
        DEPARTMENT_POSITIONS.put("Sales", new String[]{
            "Sales Representative", "Inside Sales Representative", "Outside Sales Representative", 
            "Account Executive", "Senior Account Executive", "Sales Consultant", 
            "Business Development Representative", "Key Account Manager", "Regional Sales Manager", 
            "Sales Manager", "Sales Director", "VP of Sales"
        });
        
        // Operations Department
        DEPARTMENT_POSITIONS.put("Operations", new String[]{
            "Operations Assistant", "Operations Coordinator", "Operations Specialist", 
            "Process Improvement Specialist", "Supply Chain Analyst", "Logistics Coordinator", 
            "Inventory Manager", "Operations Supervisor", "Operations Manager", 
            "Operations Director", "COO"
        });
        
        // Research and Development
        DEPARTMENT_POSITIONS.put("Research and Development", new String[]{
            "Research Assistant", "Junior Researcher", "Research Scientist", 
            "Senior Research Scientist", "Product Developer", "R&D Engineer", 
            "Innovation Manager", "Research Manager", "R&D Director", "Chief Technology Officer"
        });
        
        // Customer Service
        DEPARTMENT_POSITIONS.put("Customer Service", new String[]{
            "Customer Service Representative", "Customer Support Specialist", 
            "Technical Support Representative", "Customer Success Specialist", 
            "Account Support Specialist", "Customer Service Supervisor", 
            "Customer Service Manager", "Customer Experience Manager"
        });
        
        // Administration
        DEPARTMENT_POSITIONS.put("Administration", new String[]{
            "Administrative Assistant", "Executive Assistant", "Office Manager", 
            "Data Entry Clerk", "Receptionist", "Office Coordinator", 
            "Administrative Specialist", "Executive Secretary", "Administrative Manager"
        });
        
        // Legal Department
        DEPARTMENT_POSITIONS.put("Legal", new String[]{
            "Legal Assistant", "Paralegal", "Legal Analyst", "Corporate Counsel", 
            "Senior Legal Counsel", "Contract Manager", "Compliance Officer", 
            "Legal Manager", "General Counsel", "Chief Legal Officer"
        });
        
        // Procurement Department
        DEPARTMENT_POSITIONS.put("Procurement", new String[]{
            "Purchasing Assistant", "Procurement Specialist", "Buyer", "Senior Buyer", 
            "Procurement Analyst", "Vendor Manager", "Strategic Sourcing Manager", 
            "Procurement Manager", "Chief Procurement Officer"
        });
        
        // Quality Assurance
        DEPARTMENT_POSITIONS.put("Quality Assurance", new String[]{
            "QA Tester", "QA Analyst", "Test Engineer", "Senior QA Engineer", 
            "Automation Engineer", "QA Lead", "Quality Manager", "QA Director"
        });
        
        // Engineering Department
        DEPARTMENT_POSITIONS.put("Engineering", new String[]{
            "Junior Engineer", "Engineer", "Senior Engineer", "Principal Engineer", 
            "Design Engineer", "Project Engineer", "Systems Engineer", 
            "Engineering Manager", "Engineering Director", "Chief Engineer"
        });
        
        // Production Department
        DEPARTMENT_POSITIONS.put("Production", new String[]{
            "Production Worker", "Machine Operator", "Production Supervisor", 
            "Quality Control Inspector", "Production Coordinator", "Shift Supervisor", 
            "Production Manager", "Plant Manager", "Production Director"
        });
        
        // Logistics Department
        DEPARTMENT_POSITIONS.put("Logistics", new String[]{
            "Logistics Coordinator", "Shipping Clerk", "Warehouse Associate", 
            "Logistics Specialist", "Transportation Coordinator", "Warehouse Supervisor", 
            "Logistics Manager", "Supply Chain Manager", "Logistics Director"
        });
    }
    
    /**
     * Get all available departments
     * @return Array of department names
     */
    public static String[] getAllDepartments() {
        return DEPARTMENT_POSITIONS.keySet().toArray(new String[0]);
    }
    
    /**
     * Get positions available in a specific department
     * @param department The department name
     * @return Array of position names for the department
     */
    public static String[] getPositionsForDepartment(String department) {
        return DEPARTMENT_POSITIONS.getOrDefault(department, new String[0]);
    }
    
    /**
     * Get department code for a given department name
     * @param department The department name
     * @return Department code (matches EmployeeManager logic)
     */
    public static String getDepartmentCode(String department) {
        if (department == null || department.trim().isEmpty()) {
            return "GEN";
        }
        
        String dept = department.trim().toUpperCase();
        
        switch (dept) {
            case "INFORMATION TECHNOLOGY":
                return "IT";
            case "HUMAN RESOURCES":
                return "HR";
            case "FINANCE":
                return "FIN";
            case "MARKETING":
                return "MKT";
            case "SALES":
                return "SAL";
            case "OPERATIONS":
                return "OPS";
            case "RESEARCH AND DEVELOPMENT":
                return "RND";
            case "CUSTOMER SERVICE":
                return "CS";
            case "ADMINISTRATION":
                return "ADM";
            case "LEGAL":
                return "LEG";
            case "PROCUREMENT":
                return "PRC";
            case "QUALITY ASSURANCE":
                return "QA";
            case "ENGINEERING":
                return "ENG";
            case "PRODUCTION":
                return "PRD";
            case "LOGISTICS":
                return "LOG";
            default:
                return "GEN";
        }
    }
    
    /**
     * Check if a department exists
     * @param department The department name to check
     * @return true if department exists
     */
    public static boolean isDepartmentValid(String department) {
        return DEPARTMENT_POSITIONS.containsKey(department);
    }
    
    /**
     * Check if a position exists in a department
     * @param department The department name
     * @param position The position name
     * @return true if position exists in the department
     */
    public static boolean isPositionValidForDepartment(String department, String position) {
        String[] positions = getPositionsForDepartment(department);
        return Arrays.asList(positions).contains(position);
    }
}
