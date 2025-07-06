package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Payroll class representing a payroll record
 */
public class Payroll {
    private int payrollId;
    private int employeeId;
    private String payPeriod;
    private double basePay;
    private double overtime;
    private double bonuses;
    private double deductions;
    private double grossPay;
    private double taxes;
    private double netPay;
    private LocalDate payDate;
    
    // Additional fields for database compatibility
    private int id;
    private java.util.Date payPeriodStart;
    private java.util.Date payPeriodEnd;
    private double basicSalary;
    private double overtimeHours;
    private double overtimeRate;
    private double bonus;
    private double taxDeduction;
    private String status;
    
    public Payroll(int payrollId, int employeeId, String payPeriod, double basePay, 
                  double overtime, double bonuses, double deductions, LocalDate payDate) {
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.payPeriod = payPeriod;
        this.basePay = basePay;
        this.overtime = overtime;
        this.bonuses = bonuses;
        this.deductions = deductions;
        this.payDate = payDate;
        this.status = "PENDING";
        calculatePay();
    }
    
    // Constructor for database operations
    public Payroll(String employeeId, java.util.Date payPeriodStart, java.util.Date payPeriodEnd, 
                  double basicSalary, double overtimeHours, double overtimeRate, double bonus, double deductions) {
        this.employeeId = Integer.parseInt(employeeId);
        this.payPeriodStart = payPeriodStart;
        this.payPeriodEnd = payPeriodEnd;
        this.basicSalary = basicSalary;
        this.basePay = basicSalary;
        this.overtimeHours = overtimeHours;
        this.overtimeRate = overtimeRate;
        this.overtime = overtimeHours * overtimeRate;
        this.bonus = bonus;
        this.bonuses = bonus;
        this.deductions = deductions;
        this.status = "PENDING";
        calculatePay();
    }
    
    private void calculatePay() {
        this.grossPay = basePay + overtime + bonuses - deductions;
        this.taxes = grossPay * 0.2; // 20% tax rate
        this.taxDeduction = taxes;
        this.netPay = grossPay - taxes;
    }
    
    // Getters and setters
    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }
    public double getBasePay() { return basePay; }
    public void setBasePay(double basePay) { this.basePay = basePay; calculatePay(); }
    public double getOvertime() { return overtime; }
    
    // Database compatibility getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public java.util.Date getPayPeriodStart() { return payPeriodStart; }
    public void setPayPeriodStart(java.util.Date payPeriodStart) { this.payPeriodStart = payPeriodStart; }
    public java.util.Date getPayPeriodEnd() { return payPeriodEnd; }
    public void setPayPeriodEnd(java.util.Date payPeriodEnd) { this.payPeriodEnd = payPeriodEnd; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }
    public double getOvertimeRate() { return overtimeRate; }
    public void setOvertimeRate(double overtimeRate) { this.overtimeRate = overtimeRate; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public double getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(double taxDeduction) { this.taxDeduction = taxDeduction; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setOvertime(double overtime) { this.overtime = overtime; calculatePay(); }
    public double getBonuses() { return bonuses; }
    public void setBonuses(double bonuses) { this.bonuses = bonuses; calculatePay(); }
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; calculatePay(); }
    public double getGrossPay() { return grossPay; }
    public double getTaxes() { return taxes; }
    public double getNetPay() { return netPay; }
    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }
    
    @Override
    public String toString() {
        return String.format("Payroll ID: %d | Employee ID: %d | Period: %s | Gross: $%.2f | Taxes: $%.2f | Net: $%.2f | Date: %s",
                payrollId, employeeId, payPeriod, grossPay, taxes, netPay, 
                payDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
