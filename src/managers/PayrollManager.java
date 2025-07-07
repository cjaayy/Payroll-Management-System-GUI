package managers;

import models.Payroll;
import java.time.LocalDate;
import java.util.*;

/**
 * Payroll Manager for managing payroll operations
 */
public class PayrollManager {
    private Map<Integer, Payroll> payrolls;
    private int nextPayrollId;
    private SalaryComponentManager salaryComponentManager;
    
    public PayrollManager() {
        payrolls = new HashMap<>();
        nextPayrollId = 1;
        salaryComponentManager = new SalaryComponentManager();
    }
    
    /**
     * Set salary component manager (for dependency injection)
     */
    public void setSalaryComponentManager(SalaryComponentManager salaryComponentManager) {
        this.salaryComponentManager = salaryComponentManager;
    }
    
    /**
     * Get salary component manager
     */
    public SalaryComponentManager getSalaryComponentManager() {
        return salaryComponentManager;
    }
    
    public Payroll createPayroll(int employeeId, String payPeriod, double basePay, 
                               double overtime, double bonuses, double deductions, LocalDate payDate) {
        Payroll payroll = new Payroll(nextPayrollId++, employeeId, payPeriod, basePay, 
                                    overtime, bonuses, deductions, payDate);
        
        // Apply salary components if available
        if (salaryComponentManager != null) {
            Map<String, Double> breakdown = salaryComponentManager.getSalaryBreakdown(employeeId, basePay);
            payroll.setSalaryBreakdown(breakdown);
        }
        
        payrolls.put(payroll.getPayrollId(), payroll);
        return payroll;
    }
    
    /**
     * Create payroll with automatic salary component calculation
     */
    public Payroll createPayrollWithComponents(int employeeId, String payPeriod, double basePay, 
                                             double overtime, LocalDate payDate) {
        Payroll payroll = new Payroll(nextPayrollId++, employeeId, payPeriod, basePay, 
                                    overtime, 0.0, 0.0, payDate);
        
        // Apply salary components
        if (salaryComponentManager != null) {
            Map<String, Double> breakdown = salaryComponentManager.getSalaryBreakdown(employeeId, basePay);
            payroll.setSalaryBreakdown(breakdown);
        }
        
        payrolls.put(payroll.getPayrollId(), payroll);
        return payroll;
    }
    
    public Payroll getPayroll(int payrollId) {
        return payrolls.get(payrollId);
    }
    
    public List<Payroll> getAllPayrolls() {
        return new ArrayList<>(payrolls.values());
    }
    
    public List<Payroll> getPayrollsByEmployee(int employeeId) {
        return payrolls.values().stream()
                .filter(payroll -> payroll.getEmployeeId() == employeeId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public boolean updatePayroll(int payrollId, String payPeriod, double basePay, 
                               double overtime, double bonuses, double deductions, LocalDate payDate) {
        Payroll payroll = payrolls.get(payrollId);
        if (payroll != null) {
            payroll.setPayPeriod(payPeriod);
            payroll.setBasePay(basePay);
            payroll.setOvertime(overtime);
            payroll.setBonuses(bonuses);
            payroll.setDeductions(deductions);
            payroll.setPayDate(payDate);
            return true;
        }
        return false;
    }
    
    public boolean deletePayroll(int payrollId) {
        return payrolls.remove(payrollId) != null;
    }
}
