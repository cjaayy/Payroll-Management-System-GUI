package managers;

import models.Employee;
import models.Payroll;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Philippine Payslip Generator
 * Generates comprehensive payslips following Philippine payroll standards
 */
public class PayslipGenerator {
    private static final String COMPANY_NAME = "Philippine Payroll Management System";
    private static final String COMPANY_ADDRESS = "123 Business District, Makati City, Metro Manila, Philippines";
    private static final String COMPANY_LOGO = "[COMPANY LOGO]";
    
    private EmployeeManager employeeManager;
    
    public PayslipGenerator(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }
    
    /**
     * Generate a comprehensive Philippine payslip
     */
    public String generatePayslip(int employeeId, String payPeriod, double basicSalary, 
                                 double overtimeHours, double nightDiffHours, double holidayDays,
                                 boolean isRegularHoliday, boolean workedOnHoliday, 
                                 Map<String, Double> customAllowances, Map<String, Double> customDeductions,
                                 boolean include13thMonth) {
        
        Employee employee = employeeManager.getEmployee(employeeId);
        if (employee == null) {
            return "Employee not found";
        }
        
        StringBuilder payslip = new StringBuilder();
        
        // Header with company details
        generateHeader(payslip);
        
        // Employee details
        generateEmployeeDetails(payslip, employee);
        
        // Pay period information
        generatePayPeriodInfo(payslip, payPeriod);
        
        // Earnings section
        double totalEarnings = generateEarningsSection(payslip, employee, basicSalary, overtimeHours, 
                                                     nightDiffHours, holidayDays, isRegularHoliday, 
                                                     workedOnHoliday, customAllowances, include13thMonth);
        
        // Deductions section
        double totalDeductions = generateDeductionsSection(payslip, employee, basicSalary, customDeductions);
        
        // Net pay calculation
        double netPay = totalEarnings - totalDeductions;
        generateNetPaySection(payslip, totalEarnings, totalDeductions, netPay);
        
        // Signature section
        generateSignatureSection(payslip);
        
        return payslip.toString();
    }
    
    /**
     * Generate payslip header with company information
     */
    private void generateHeader(StringBuilder payslip) {
        payslip.append("═══════════════════════════════════════════════════════════════════════════════\n");
        payslip.append("                                  ").append(COMPANY_LOGO).append("\n");
        payslip.append("                          ").append(COMPANY_NAME).append("\n");
        payslip.append("                          ").append(COMPANY_ADDRESS).append("\n");
        payslip.append("═══════════════════════════════════════════════════════════════════════════════\n");
        payslip.append("                                    PAYSLIP\n");
        payslip.append("═══════════════════════════════════════════════════════════════════════════════\n\n");
    }
    
    /**
     * Generate employee details section
     */
    private void generateEmployeeDetails(StringBuilder payslip, Employee employee) {
        payslip.append("EMPLOYEE DETAILS:\n");
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append(String.format("%-25s : %s\n", "Employee Name", employee.getFullName()));
        payslip.append(String.format("%-25s : %s\n", "Employee ID", employee.getFormattedEmployeeId()));
        payslip.append(String.format("%-25s : %s\n", "Department", employee.getDepartment()));
        payslip.append(String.format("%-25s : %s\n", "Designation", employee.getPosition()));
        payslip.append(String.format("%-25s : %s\n", "Job Title", employee.getJobTitle()));
        
        // TIN (Tax Identification Number) - would be stored in employee profile
        String tin = getTIN(employee);
        if (!tin.isEmpty()) {
            payslip.append(String.format("%-25s : %s\n", "TIN", tin));
        }
        
        payslip.append(String.format("%-25s : %s\n", "Hire Date", employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        payslip.append("\n");
    }
    
    /**
     * Generate pay period information
     */
    private void generatePayPeriodInfo(StringBuilder payslip, String payPeriod) {
        payslip.append("PAY PERIOD INFORMATION:\n");
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append(String.format("%-25s : %s\n", "Pay Period", payPeriod));
        payslip.append(String.format("%-25s : %s\n", "Pay Date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        payslip.append(String.format("%-25s : %s\n", "Payment Method", "Bank Transfer"));
        payslip.append("\n");
    }
    
    /**
     * Generate earnings section
     */
    private double generateEarningsSection(StringBuilder payslip, Employee employee, double basicSalary,
                                         double overtimeHours, double nightDiffHours, double holidayDays,
                                         boolean isRegularHoliday, boolean workedOnHoliday,
                                         Map<String, Double> customAllowances, boolean include13thMonth) {
        
        payslip.append("EARNINGS:\n");
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        
        double totalEarnings = 0.0;
        
        // Basic Salary
        payslip.append(String.format("%-50s : ₱ %,10.2f\n", "Basic Salary", basicSalary));
        totalEarnings += basicSalary;
        
        // Overtime Pay
        if (overtimeHours > 0) {
            double overtimePay = PhilippinePayrollCalculator.calculateOvertimePay(
                basicSalary, overtimeHours, workedOnHoliday && isRegularHoliday, false);
            payslip.append(String.format("%-50s : ₱ %,10.2f\n", 
                String.format("Overtime Pay (%.1f hrs)", overtimeHours), overtimePay));
            totalEarnings += overtimePay;
        }
        
        // Night Differential
        if (nightDiffHours > 0) {
            double nightDiff = PhilippinePayrollCalculator.calculateNightDifferential(basicSalary, nightDiffHours);
            payslip.append(String.format("%-50s : ₱ %,10.2f\n", 
                String.format("Night Differential (%.1f hrs)", nightDiffHours), nightDiff));
            totalEarnings += nightDiff;
        }
        
        // Holiday Pay
        if (holidayDays > 0) {
            double dailyRate = basicSalary / 22; // Assuming 22 working days
            double holidayPay = PhilippinePayrollCalculator.calculateHolidayPay(
                dailyRate, isRegularHoliday, workedOnHoliday) * holidayDays;
            String holidayType = isRegularHoliday ? "Regular Holiday" : "Special Holiday";
            payslip.append(String.format("%-50s : ? %,10.2f\n", 
                String.format("%s Pay (%.1f days)", holidayType, holidayDays), holidayPay));
            totalEarnings += holidayPay;
        }
        
        // Philippine Allowances
        payslip.append("\nALLOWANCES:\n");
        Map<String, Double> allowances = getDefaultAllowances();
        if (customAllowances != null) {
            allowances.putAll(customAllowances);
        }
        
        double totalAllowances = 0.0;
        for (Map.Entry<String, Double> entry : allowances.entrySet()) {
            if (entry.getValue() > 0) {
                payslip.append(String.format("%-50s : ? %,10.2f\n", entry.getKey(), entry.getValue()));
                totalAllowances += entry.getValue();
            }
        }
        
        if (totalAllowances > 0) {
            payslip.append(String.format("%-50s : ? %,10.2f\n", "Total Allowances", totalAllowances));
            totalEarnings += totalAllowances;
        }
        
        // 13th Month Pay
        if (include13thMonth) {
            double thirteenthMonthPay = PhilippinePayrollCalculator.calculate13thMonthPay(basicSalary * 12) / 12;
            payslip.append(String.format("%-50s : ? %,10.2f\n", "13th Month Pay (Monthly)", thirteenthMonthPay));
            totalEarnings += thirteenthMonthPay;
        }
        
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append(String.format("%-50s : ? %,10.2f\n", "GROSS PAY", totalEarnings));
        payslip.append("\n");
        
        return totalEarnings;
    }
    
    /**
     * Generate deductions section
     */
    private double generateDeductionsSection(StringBuilder payslip, Employee employee, double basicSalary,
                                           Map<String, Double> customDeductions) {
        
        payslip.append("DEDUCTIONS:\n");
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        
        double totalDeductions = 0.0;
        
        // Government Contributions
        payslip.append("GOVERNMENT CONTRIBUTIONS:\n");
        
        // SSS Contribution
        PhilippinePayrollCalculator.SSContribution sssContrib = 
            PhilippinePayrollCalculator.calculateSSSContribution(basicSalary);
        payslip.append(String.format("%-50s : ? %,10.2f\n", "SSS Contribution", sssContrib.employeeShare));
        totalDeductions += sssContrib.employeeShare;
        
        // PhilHealth Contribution
        PhilippinePayrollCalculator.PhilHealthContribution philHealthContrib = 
            PhilippinePayrollCalculator.calculatePhilHealthContribution(basicSalary);
        payslip.append(String.format("%-50s : ? %,10.2f\n", "PhilHealth Contribution", philHealthContrib.employeeShare));
        totalDeductions += philHealthContrib.employeeShare;
        
        // Pag-IBIG Contribution
        PhilippinePayrollCalculator.PagIBIGContribution pagibigContrib = 
            PhilippinePayrollCalculator.calculatePagIBIGContribution(basicSalary);
        payslip.append(String.format("%-50s : ? %,10.2f\n", "Pag-IBIG Contribution", pagibigContrib.employeeShare));
        totalDeductions += pagibigContrib.employeeShare;
        
        // Withholding Tax
        double monthlyTax = PhilippinePayrollCalculator.calculateMonthlyWithholdingTax(basicSalary);
        payslip.append(String.format("%-50s : ? %,10.2f\n", "Withholding Tax", monthlyTax));
        totalDeductions += monthlyTax;
        
        // Other Deductions
        if (customDeductions != null && !customDeductions.isEmpty()) {
            payslip.append("\nOTHER DEDUCTIONS:\n");
            for (Map.Entry<String, Double> entry : customDeductions.entrySet()) {
                if (entry.getValue() > 0) {
                    payslip.append(String.format("%-50s : ? %,10.2f\n", entry.getKey(), entry.getValue()));
                    totalDeductions += entry.getValue();
                }
            }
        }
        
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append(String.format("%-50s : ? %,10.2f\n", "TOTAL DEDUCTIONS", totalDeductions));
        payslip.append("\n");
        
        return totalDeductions;
    }
    
    /**
     * Generate net pay section
     */
    private void generateNetPaySection(StringBuilder payslip, double totalEarnings, double totalDeductions, double netPay) {
        payslip.append("NET PAY CALCULATION:\n");
        payslip.append("═══════════════════════════════════════════════════════════════════════════════\n");
        payslip.append(String.format("%-50s : ? %,10.2f\n", "Gross Pay", totalEarnings));
        payslip.append(String.format("%-50s : ? %,10.2f\n", "Total Deductions", totalDeductions));
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append(String.format("%-50s : ? %,10.2f\n", "NET PAY", netPay));
        payslip.append("═══════════════════════════════════════════════════════════════════════════════\n\n");
    }
    
    /**
     * Generate signature section
     */
    private void generateSignatureSection(StringBuilder payslip) {
        payslip.append("SIGNATURES:\n");
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append("Employee Signature: ________________________    Date: ___________\n\n");
        payslip.append("Authorized Representative: __________________    Date: ___________\n");
        payslip.append("Name: ______________________________________\n");
        payslip.append("Position: HR Manager / Payroll Officer\n\n");
        
        payslip.append("───────────────────────────────────────────────────────────────────────────────\n");
        payslip.append("This payslip is computer-generated and does not require a signature.\n");
        payslip.append("For questions or concerns, please contact the HR Department.\n");
        payslip.append("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        payslip.append("═══════════════════════════════════════════════════════════════════════════════\n");
    }
    
    /**
     * Get default Philippine allowances
     */
    private Map<String, Double> getDefaultAllowances() {
        Map<String, Double> allowances = new LinkedHashMap<>();
        allowances.put("Rice Allowance", 2000.0);
        allowances.put("Transportation Allowance", 2000.0);
        allowances.put("Meal Allowance", 1500.0);
        allowances.put("Connectivity Allowance", 1000.0);
        allowances.put("Medical Allowance", 0.0);
        allowances.put("Clothing Allowance", 0.0);
        allowances.put("Education Allowance", 0.0);
        return allowances;
    }
    
    /**
     * Get employee TIN (Tax Identification Number)
     * This would typically be stored in the employee profile
     */
    private String getTIN(Employee employee) {
        // This would be retrieved from employee profile
        // For now, generate a sample TIN format
        return "123-456-789-000";
    }
    
    /**
     * Generate payslip from existing payroll record
     */
    public String generatePayslipFromPayroll(Payroll payroll) {
        Employee employee = employeeManager.getEmployee(payroll.getEmployeeId());
        if (employee == null) {
            return "Employee not found";
        }
        
        // Extract data from payroll record
        String payPeriod = payroll.getPayPeriod();
        double basicSalary = payroll.getBasePay();
        
        // Create default maps for allowances and deductions
        Map<String, Double> allowances = getDefaultAllowances();
        Map<String, Double> deductions = new HashMap<>();
        
        // Add any custom deductions from payroll
        if (payroll.getDeductions() > 0) {
            deductions.put("Other Deductions", payroll.getDeductions());
        }
        
        return generatePayslip(employee.getEmployeeId(), payPeriod, basicSalary, 
                              payroll.getOvertimeHours(), 0, 0, false, false, 
                              allowances, deductions, false);
    }
    
    /**
     * Generate payslip summary for reports
     */
    public String generatePayslipSummary(int employeeId, String payPeriod, double netPay) {
        Employee employee = employeeManager.getEmployee(employeeId);
        if (employee == null) {
            return "Employee not found";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("PAYSLIP SUMMARY\n");
        summary.append("Employee: ").append(employee.getFullName()).append("\n");
        summary.append("ID: ").append(employee.getFormattedEmployeeId()).append("\n");
        summary.append("Period: ").append(payPeriod).append("\n");
        summary.append("Net Pay: ? ").append(String.format("%,.2f", netPay)).append("\n");
        
        return summary.toString();
    }
}
