package managers;

import models.SalaryComponent;
import models.EmployeeSalaryComponent;
import models.Employee;
import database.DatabaseDAO;
import database.MySQLDatabaseDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Manages salary components and employee salary component assignments
 */
public class SalaryComponentManager {
    private DatabaseDAO databaseDAO;
    
    public SalaryComponentManager() {
        this.databaseDAO = new MySQLDatabaseDAO();
    }
    
    public SalaryComponentManager(DatabaseDAO databaseDAO) {
        this.databaseDAO = databaseDAO;
    }
    
    // Salary Component Management
    public List<SalaryComponent> getAllSalaryComponents() {
        try {
            return databaseDAO.getAllSalaryComponents();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public SalaryComponent getSalaryComponent(int id) {
        try {
            return databaseDAO.getSalaryComponentById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean addSalaryComponent(SalaryComponent component) {
        try {
            return databaseDAO.insertSalaryComponent(component);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSalaryComponent(SalaryComponent component) {
        try {
            return databaseDAO.updateSalaryComponent(component);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteSalaryComponent(int id) {
        try {
            return databaseDAO.deleteSalaryComponent(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Employee Salary Component Management
    public List<EmployeeSalaryComponent> getEmployeeSalaryComponents(String employeeId) {
        try {
            return databaseDAO.getEmployeeSalaryComponents(employeeId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public boolean addEmployeeSalaryComponent(EmployeeSalaryComponent empComponent) {
        try {
            return databaseDAO.insertEmployeeSalaryComponent(empComponent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateEmployeeSalaryComponent(EmployeeSalaryComponent empComponent) {
        try {
            return databaseDAO.updateEmployeeSalaryComponent(empComponent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteEmployeeSalaryComponent(int id) {
        try {
            return databaseDAO.deleteEmployeeSalaryComponent(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Payroll Calculation Methods
    public PayrollCalculationResult calculatePayroll(Employee employee, BigDecimal baseSalary) {
        List<EmployeeSalaryComponent> components = getEmployeeSalaryComponents(employee.getEmployeeIdString());
        
        BigDecimal totalEarnings = baseSalary;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalAllowances = BigDecimal.ZERO;
        BigDecimal totalBonuses = BigDecimal.ZERO;
        
        List<ComponentCalculation> earningCalculations = new ArrayList<>();
        List<ComponentCalculation> deductionCalculations = new ArrayList<>();
        
        // Process each component
        for (EmployeeSalaryComponent empComponent : components) {
            if (!empComponent.isCurrentlyActive()) continue;
            
            SalaryComponent component = empComponent.getSalaryComponent();
            if (component == null) continue;
            
            BigDecimal calculatedAmount = calculateComponentAmount(empComponent, baseSalary);
            ComponentCalculation calc = new ComponentCalculation(component, calculatedAmount, empComponent.isPercentage());
            
            if (component.isEarning()) {
                totalEarnings = totalEarnings.add(calculatedAmount);
                earningCalculations.add(calc);
                
                if ("ALLOWANCE".equals(component.getType())) {
                    totalAllowances = totalAllowances.add(calculatedAmount);
                } else if ("BONUS".equals(component.getType())) {
                    totalBonuses = totalBonuses.add(calculatedAmount);
                }
            } else if (component.isDeduction()) {
                totalDeductions = totalDeductions.add(calculatedAmount);
                deductionCalculations.add(calc);
            }
        }
        
        BigDecimal netPay = totalEarnings.subtract(totalDeductions);
        
        return new PayrollCalculationResult(
            baseSalary, totalEarnings, totalDeductions, totalAllowances, 
            totalBonuses, netPay, earningCalculations, deductionCalculations
        );
    }
    
    private BigDecimal calculateComponentAmount(EmployeeSalaryComponent empComponent, BigDecimal baseSalary) {
        BigDecimal amount = empComponent.getCustomAmount();
        
        if (empComponent.isPercentage()) {
            // Calculate percentage of base salary
            return baseSalary.multiply(amount).divide(BigDecimal.valueOf(100));
        } else {
            // Fixed amount
            return amount;
        }
    }
    
    // Utility methods
    public List<SalaryComponent> getSalaryComponentsByType(String type) {
        return getAllSalaryComponents().stream()
            .filter(component -> type.equals(component.getType()))
            .collect(Collectors.toList());
    }
    
    public List<SalaryComponent> getActiveSalaryComponents() {
        return getAllSalaryComponents().stream()
            .filter(SalaryComponent::isActive)
            .collect(Collectors.toList());
    }
    
    // Additional methods for GUI compatibility
    public EmployeeSalaryComponent assignComponentToEmployee(int employeeId, int salaryComponentId, 
                                                           double amount, boolean isPercentage, 
                                                           java.time.LocalDate effectiveDate) {
        String employeeIdStr = String.format("EMP%03d", employeeId);
        EmployeeSalaryComponent empComponent = new EmployeeSalaryComponent(
            employeeIdStr, salaryComponentId, BigDecimal.valueOf(amount)
        );
        empComponent.setPercentage(isPercentage);
        empComponent.setEffectiveDate(effectiveDate);
        
        if (addEmployeeSalaryComponent(empComponent)) {
            return empComponent;
        }
        return null;
    }
    
    public boolean removeComponentFromEmployee(int employeeId, int salaryComponentId) {
        String employeeIdStr = String.format("EMP%03d", employeeId);
        List<EmployeeSalaryComponent> components = getEmployeeSalaryComponents(employeeIdStr);
        
        for (EmployeeSalaryComponent component : components) {
            if (component.getSalaryComponentId() == salaryComponentId) {
                return deleteEmployeeSalaryComponent(component.getId());
            }
        }
        return false;
    }
    
    public double calculateTotalAllowances(int employeeId, double baseSalary) {
        String employeeIdStr = String.format("EMP%03d", employeeId);
        List<EmployeeSalaryComponent> components = getEmployeeSalaryComponents(employeeIdStr);
        
        double total = 0.0;
        for (EmployeeSalaryComponent empComponent : components) {
            if (empComponent.isCurrentlyActive() && empComponent.getSalaryComponent() != null) {
                SalaryComponent component = empComponent.getSalaryComponent();
                if ("ALLOWANCE".equals(component.getType())) {
                    BigDecimal amount = calculateComponentAmount(empComponent, BigDecimal.valueOf(baseSalary));
                    total += amount.doubleValue();
                }
            }
        }
        return total;
    }
    
    public double calculateTotalDeductions(int employeeId, double baseSalary) {
        String employeeIdStr = String.format("EMP%03d", employeeId);
        List<EmployeeSalaryComponent> components = getEmployeeSalaryComponents(employeeIdStr);
        
        double total = 0.0;
        for (EmployeeSalaryComponent empComponent : components) {
            if (empComponent.isCurrentlyActive() && empComponent.getSalaryComponent() != null) {
                SalaryComponent component = empComponent.getSalaryComponent();
                if ("DEDUCTION".equals(component.getType())) {
                    BigDecimal amount = calculateComponentAmount(empComponent, BigDecimal.valueOf(baseSalary));
                    total += amount.doubleValue();
                }
            }
        }
        return total;
    }
    
    public double calculateTotalBonuses(int employeeId, double baseSalary) {
        String employeeIdStr = String.format("EMP%03d", employeeId);
        List<EmployeeSalaryComponent> components = getEmployeeSalaryComponents(employeeIdStr);
        
        double total = 0.0;
        for (EmployeeSalaryComponent empComponent : components) {
            if (empComponent.isCurrentlyActive() && empComponent.getSalaryComponent() != null) {
                SalaryComponent component = empComponent.getSalaryComponent();
                if ("BONUS".equals(component.getType())) {
                    BigDecimal amount = calculateComponentAmount(empComponent, BigDecimal.valueOf(baseSalary));
                    total += amount.doubleValue();
                }
            }
        }
        return total;
    }
    
    public java.util.Map<String, Double> getSalaryBreakdown(int employeeId, double baseSalary) {
        String employeeIdStr = String.format("EMP%03d", employeeId);
        List<EmployeeSalaryComponent> components = getEmployeeSalaryComponents(employeeIdStr);
        
        java.util.Map<String, Double> breakdown = new java.util.HashMap<>();
        breakdown.put("Base Salary", baseSalary);
        
        for (EmployeeSalaryComponent empComponent : components) {
            if (empComponent.isCurrentlyActive() && empComponent.getSalaryComponent() != null) {
                SalaryComponent component = empComponent.getSalaryComponent();
                BigDecimal amount = calculateComponentAmount(empComponent, BigDecimal.valueOf(baseSalary));
                breakdown.put(component.getName(), amount.doubleValue());
            }
        }
        
        return breakdown;
    }
    
    public SalaryComponent createSalaryComponent(String name, String type, double amount, 
                                               String description, boolean isPercentage) {
        SalaryComponent component = new SalaryComponent(name, type, BigDecimal.valueOf(amount), description);
        component.setPercentage(isPercentage);
        
        if (addSalaryComponent(component)) {
            return component;
        }
        return null;
    }
    
    public PayrollCalculationResult calculateStandardPayroll(int employeeId, double baseSalary, 
                                                           double overtimeHours, double overtimeRate, 
                                                           double bonusAmount, boolean applyTax, 
                                                           boolean applySSS, boolean applyPhilHealth) {
        // Create a dummy employee for calculation
        Employee employee = new Employee(employeeId, "Dummy", "Employee", "dummy@email.com", 
                                        "Unknown", "Unknown", baseSalary, java.time.LocalDate.now());
        
        return calculatePayroll(employee, BigDecimal.valueOf(baseSalary));
    }
    
    public String getPayrollCalculationSummary(PayrollCalculationResult result) {
        StringBuilder summary = new StringBuilder();
        summary.append("Payroll Calculation Summary\n");
        summary.append("==========================\n");
        summary.append(String.format("Base Salary: ₱%.2f\n", result.getBaseSalary().doubleValue()));
        summary.append(String.format("Total Earnings: ₱%.2f\n", result.getTotalEarnings().doubleValue()));
        summary.append(String.format("Total Deductions: ₱%.2f\n", result.getTotalDeductions().doubleValue()));
        summary.append(String.format("Net Pay: ₱%.2f\n", result.getNetPay().doubleValue()));
        
        return summary.toString();
    }
    
    public boolean isMinimumWageCompliant(PayrollCalculationResult result) {
        // Philippine minimum wage compliance check
        double minimumWage = 570.00; // Daily minimum wage in NCR (example)
        double dailyRate = result.getNetPay().doubleValue() / 22; // Assuming 22 working days
        return dailyRate >= minimumWage;
    }
    
    // Inner classes for calculation results
    public static class PayrollCalculationResult {
        private final BigDecimal baseSalary;
        private final BigDecimal totalEarnings;
        private final BigDecimal totalDeductions;
        private final BigDecimal totalAllowances;
        private final BigDecimal totalBonuses;
        private final BigDecimal netPay;
        private final List<ComponentCalculation> earningCalculations;
        private final List<ComponentCalculation> deductionCalculations;
        
        public PayrollCalculationResult(BigDecimal baseSalary, BigDecimal totalEarnings, 
                                       BigDecimal totalDeductions, BigDecimal totalAllowances,
                                       BigDecimal totalBonuses, BigDecimal netPay,
                                       List<ComponentCalculation> earningCalculations,
                                       List<ComponentCalculation> deductionCalculations) {
            this.baseSalary = baseSalary;
            this.totalEarnings = totalEarnings;
            this.totalDeductions = totalDeductions;
            this.totalAllowances = totalAllowances;
            this.totalBonuses = totalBonuses;
            this.netPay = netPay;
            this.earningCalculations = earningCalculations;
            this.deductionCalculations = deductionCalculations;
        }
        
        // Getters
        public BigDecimal getBaseSalary() { return baseSalary; }
        public BigDecimal getTotalEarnings() { return totalEarnings; }
        public BigDecimal getTotalDeductions() { return totalDeductions; }
        public BigDecimal getTotalAllowances() { return totalAllowances; }
        public BigDecimal getTotalBonuses() { return totalBonuses; }
        public BigDecimal getNetPay() { return netPay; }
        public List<ComponentCalculation> getEarningCalculations() { return earningCalculations; }
        public List<ComponentCalculation> getDeductionCalculations() { return deductionCalculations; }
    }
    
    public static class ComponentCalculation {
        private final SalaryComponent component;
        private final BigDecimal calculatedAmount;
        private final boolean isPercentage;
        
        public ComponentCalculation(SalaryComponent component, BigDecimal calculatedAmount, boolean isPercentage) {
            this.component = component;
            this.calculatedAmount = calculatedAmount;
            this.isPercentage = isPercentage;
        }
        
        // Getters
        public SalaryComponent getComponent() { return component; }
        public BigDecimal getCalculatedAmount() { return calculatedAmount; }
        public boolean isPercentage() { return isPercentage; }
        
        public String getFormattedAmount() {
            return String.format("₱%.2f", calculatedAmount.doubleValue());
        }
    }
}
