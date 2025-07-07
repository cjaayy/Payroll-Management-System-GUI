package gui;

import models.Employee;
import models.Payroll;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reports Panel for generating various reports
 */
public class ReportsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private PayrollManagementSystemGUI mainApp;
    private JTextArea reportArea;
    private JButton employeeSummaryButton;
    private JButton payrollSummaryButton;
    private JButton departmentReportButton;
    private JButton monthlyReportButton;
    private JButton backButton;
    private JTextField monthField;
    
    public ReportsPanel(PayrollManagementSystemGUI mainApp) {
        this.mainApp = mainApp;
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        setBackground(new Color(240, 248, 255));
        
        // Text area for displaying reports
        reportArea = new JTextArea(20, 60);
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBackground(Color.WHITE);
        reportArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Buttons
        employeeSummaryButton = new JButton("Employee Summary");
        payrollSummaryButton = new JButton("Payroll Summary");
        departmentReportButton = new JButton("Department Report");
        monthlyReportButton = new JButton("Monthly Report");
        backButton = new JButton("Back to Main Menu");
        
        // Month field for monthly report
        monthField = new JTextField(10);
        monthField.setToolTipText("Format: YYYY-MM");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        Color buttonColor = new Color(70, 130, 180);
        
        employeeSummaryButton.setFont(buttonFont);
        employeeSummaryButton.setBackground(buttonColor);
        employeeSummaryButton.setForeground(Color.WHITE);
        
        payrollSummaryButton.setFont(buttonFont);
        payrollSummaryButton.setBackground(buttonColor);
        payrollSummaryButton.setForeground(Color.WHITE);
        
        departmentReportButton.setFont(buttonFont);
        departmentReportButton.setBackground(buttonColor);
        departmentReportButton.setForeground(Color.WHITE);
        
        monthlyReportButton.setFont(buttonFont);
        monthlyReportButton.setBackground(buttonColor);
        monthlyReportButton.setForeground(Color.WHITE);
        
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Report area in center
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(employeeSummaryButton);
        controlPanel.add(payrollSummaryButton);
        controlPanel.add(departmentReportButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(monthField);
        controlPanel.add(monthlyReportButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(backButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        employeeSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateEmployeeSummaryReport();
            }
        });
        
        payrollSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePayrollSummaryReport();
            }
        });
        
        departmentReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateDepartmentReport();
            }
        });
        
        monthlyReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMonthlyReport();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showMainMenuPanel();
            }
        });
    }
    
    private void generateEmployeeSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("EMPLOYEE SUMMARY REPORT\n");
        report.append("=" .repeat(50)).append("\n\n");
        
        List<Employee> employees = mainApp.getEmployeeManager().getActiveEmployees();
        
        if (employees.isEmpty()) {
            report.append("No employees found.\n");
        } else {
            report.append("Total Active Employees: ").append(employees.size()).append("\n\n");
            
            // Department distribution
            Map<String, Integer> departmentCount = new HashMap<>();
            double totalSalary = 0;
            
            for (Employee emp : employees) {
                departmentCount.put(emp.getDepartment(), 
                                  departmentCount.getOrDefault(emp.getDepartment(), 0) + 1);
                totalSalary += emp.getBaseSalary();
            }
            
            report.append("Department Distribution:\n");
            for (Map.Entry<String, Integer> entry : departmentCount.entrySet()) {
                report.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" employees\n");
            }
            
            report.append("\nSalary Statistics:\n");
            report.append("  Average Salary: $").append(String.format("%.2f", totalSalary / employees.size())).append("\n");
            report.append("  Total Salary Budget: $").append(String.format("%.2f", totalSalary)).append("\n\n");
            
            report.append("Employee Details:\n");
            report.append("-".repeat(80)).append("\n");
            for (Employee emp : employees) {
                report.append(String.format("%-4d %-20s %-15s %-15s $%10.2f\n", 
                            emp.getEmployeeId(), emp.getFullName(), emp.getDepartment(), 
                            emp.getPosition(), emp.getBaseSalary()));
            }
        }
        
        reportArea.setText(report.toString());
        reportArea.setCaretPosition(0);
    }
    
    private void generatePayrollSummaryReport() {
        StringBuilder report = new StringBuilder();
        report.append("PAYROLL SUMMARY REPORT\n");
        report.append("=" .repeat(50)).append("\n\n");
        
        List<Payroll> payrolls = mainApp.getPayrollManager().getAllPayrolls();
        
        if (payrolls.isEmpty()) {
            report.append("No payroll records found.\n");
        } else {
            report.append("Total Payroll Records: ").append(payrolls.size()).append("\n\n");
            
            double totalGrossPay = 0;
            double totalNetPay = 0;
            double totalTaxes = 0;
            
            for (Payroll payroll : payrolls) {
                totalGrossPay += payroll.getGrossPay();
                totalNetPay += payroll.getNetPay();
                totalTaxes += payroll.getTaxes();
            }
            
            report.append("Financial Summary:\n");
            report.append("  Total Gross Pay: $").append(String.format("%.2f", totalGrossPay)).append("\n");
            report.append("  Total Net Pay: $").append(String.format("%.2f", totalNetPay)).append("\n");
            report.append("  Total Taxes: $").append(String.format("%.2f", totalTaxes)).append("\n");
            report.append("  Average Gross Pay: $").append(String.format("%.2f", totalGrossPay / payrolls.size())).append("\n");
            report.append("  Average Net Pay: $").append(String.format("%.2f", totalNetPay / payrolls.size())).append("\n\n");
            
            report.append("Payroll Details:\n");
            report.append("-".repeat(80)).append("\n");
            for (Payroll payroll : payrolls) {
                Employee employee = mainApp.getEmployeeManager().getEmployee(payroll.getEmployeeId());
                String employeeName = employee != null ? employee.getFullName() : "Unknown";
                
                report.append(String.format("%-4d %-20s %-10s $%8.2f $%8.2f $%8.2f\n", 
                            payroll.getPayrollId(), employeeName, payroll.getPayPeriod(),
                            payroll.getGrossPay(), payroll.getTaxes(), payroll.getNetPay()));
            }
        }
        
        reportArea.setText(report.toString());
        reportArea.setCaretPosition(0);
    }
    
    private void generateDepartmentReport() {
        StringBuilder report = new StringBuilder();
        report.append("DEPARTMENT WISE REPORT\n");
        report.append("=" .repeat(50)).append("\n\n");
        
        List<Employee> employees = mainApp.getEmployeeManager().getActiveEmployees();
        
        if (employees.isEmpty()) {
            report.append("No employees found.\n");
        } else {
            Map<String, java.util.List<Employee>> departmentEmployees = new HashMap<>();
            
            for (Employee emp : employees) {
                departmentEmployees.computeIfAbsent(emp.getDepartment(), k -> new java.util.ArrayList<>()).add(emp);
            }
            
            for (Map.Entry<String, java.util.List<Employee>> entry : departmentEmployees.entrySet()) {
                String department = entry.getKey();
                java.util.List<Employee> deptEmployees = entry.getValue();
                
                report.append(department.toUpperCase()).append(" DEPARTMENT\n");
                report.append("-".repeat(40)).append("\n");
                report.append("Total Employees: ").append(deptEmployees.size()).append("\n\n");
                
                double deptTotalSalary = 0;
                for (Employee emp : deptEmployees) {
                    report.append(String.format("  %-20s %-15s $%10.2f\n", 
                                emp.getFullName(), emp.getPosition(), emp.getBaseSalary()));
                    deptTotalSalary += emp.getBaseSalary();
                }
                
                report.append("\nDepartment Total Salary: $").append(String.format("%.2f", deptTotalSalary)).append("\n");
                report.append("Department Average Salary: $").append(String.format("%.2f", deptTotalSalary / deptEmployees.size())).append("\n\n");
            }
        }
        
        reportArea.setText(report.toString());
        reportArea.setCaretPosition(0);
    }
    
    private void generateMonthlyReport() {
        String month = monthField.getText().trim();
        
        if (month.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a month (YYYY-MM).", 
                                        "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("MONTHLY PAYROLL REPORT (").append(month).append(")\n");
        report.append("=" .repeat(50)).append("\n\n");
        
        List<Payroll> payrolls = mainApp.getPayrollManager().getAllPayrolls();
        java.util.List<Payroll> monthlyPayrolls = new java.util.ArrayList<>();
        
        for (Payroll payroll : payrolls) {
            if (payroll.getPayPeriod().equals(month)) {
                monthlyPayrolls.add(payroll);
            }
        }
        
        if (monthlyPayrolls.isEmpty()) {
            report.append("No payroll records found for ").append(month).append(".\n");
        } else {
            report.append("Total Records: ").append(monthlyPayrolls.size()).append("\n\n");
            
            double totalGrossPay = 0;
            double totalNetPay = 0;
            double totalTaxes = 0;
            
            report.append("Employee Details:\n");
            report.append("-".repeat(80)).append("\n");
            
            for (Payroll payroll : monthlyPayrolls) {
                Employee employee = mainApp.getEmployeeManager().getEmployee(payroll.getEmployeeId());
                String employeeName = employee != null ? employee.getFullName() : "Unknown";
                
                report.append(String.format("%-20s $%8.2f $%8.2f $%8.2f\n", 
                            employeeName, payroll.getGrossPay(), payroll.getTaxes(), payroll.getNetPay()));
                
                totalGrossPay += payroll.getGrossPay();
                totalNetPay += payroll.getNetPay();
                totalTaxes += payroll.getTaxes();
            }
            
            report.append("\n").append("=".repeat(80)).append("\n");
            report.append("Monthly Total Gross Pay: $").append(String.format("%.2f", totalGrossPay)).append("\n");
            report.append("Monthly Total Net Pay: $").append(String.format("%.2f", totalNetPay)).append("\n");
            report.append("Monthly Total Taxes: $").append(String.format("%.2f", totalTaxes)).append("\n");
        }
        
        reportArea.setText(report.toString());
        reportArea.setCaretPosition(0);
    }
}
