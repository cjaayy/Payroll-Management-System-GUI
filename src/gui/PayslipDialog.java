package gui;

import models.Employee;
import models.Payroll;
import managers.EmployeeManager;
import managers.PayslipGenerator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.util.Map;

/**
 * Dialog for displaying and printing Philippine payslips
 */
public class PayslipDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient PayslipGenerator payslipGenerator;
    private transient EmployeeManager employeeManager;
    private JTextArea payslipTextArea;
    private JButton printButton;
    private JButton saveButton;
    private JButton closeButton;
    private String payslipContent;
    
    @SuppressWarnings("this-escape")
    public PayslipDialog(Window parent, EmployeeManager employeeManager, PayslipGenerator payslipGenerator) {
        super(parent, "Philippine Payslip", ModalityType.APPLICATION_MODAL);
        this.employeeManager = employeeManager;
        this.payslipGenerator = payslipGenerator;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        setSize(800, 900);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        payslipTextArea = new JTextArea();
        payslipTextArea.setEditable(false);
        payslipTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        payslipTextArea.setBackground(Color.WHITE);
        payslipTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        printButton = new JButton("Print Payslip");
        saveButton = new JButton("Save as Text");
        closeButton = new JButton("Close");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        
        printButton.setFont(buttonFont);
        printButton.setBackground(new Color(70, 130, 180));
        printButton.setForeground(Color.WHITE);
        
        saveButton.setFont(buttonFont);
        saveButton.setBackground(new Color(32, 178, 170));
        saveButton.setForeground(Color.WHITE);
        
        closeButton.setFont(buttonFont);
        closeButton.setBackground(new Color(128, 128, 128));
        closeButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Philippine Payslip", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Payslip content
        JScrollPane scrollPane = new JScrollPane(payslipTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printPayslip();
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePayslip();
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Display payslip from payroll record
     */
    public void displayPayslip(Payroll payroll) {
        if (payroll == null) {
            JOptionPane.showMessageDialog(this, "No payroll record provided.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        payslipContent = payslipGenerator.generatePayslipFromPayroll(payroll);
        payslipTextArea.setText(payslipContent);
        payslipTextArea.setCaretPosition(0);
        
        Employee employee = employeeManager.getEmployee(payroll.getEmployeeId());
        if (employee != null) {
            setTitle("Philippine Payslip - " + employee.getFullName());
        }
    }
    
    /**
     * Display custom payslip
     */
    public void displayCustomPayslip(int employeeId, String payPeriod, double basicSalary, 
                                   double overtimeHours, double nightDiffHours, double holidayDays,
                                   boolean isRegularHoliday, boolean workedOnHoliday,
                                   Map<String, Double> allowances, Map<String, Double> deductions,
                                   boolean include13thMonth) {
        
        Employee employee = employeeManager.getEmployee(employeeId);
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        payslipContent = payslipGenerator.generatePayslip(employeeId, payPeriod, basicSalary, 
                                                        overtimeHours, nightDiffHours, holidayDays,
                                                        isRegularHoliday, workedOnHoliday,
                                                        allowances, deductions, include13thMonth);
        payslipTextArea.setText(payslipContent);
        payslipTextArea.setCaretPosition(0);
        
        setTitle("Philippine Payslip - " + employee.getFullName());
    }
    
    /**
     * Print the payslip
     */
    private void printPayslip() {
        if (payslipContent == null || payslipContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No payslip to print.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean printed = payslipTextArea.print();
            if (printed) {
                JOptionPane.showMessageDialog(this, "Payslip printed successfully!", 
                                            "Print Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Printing was cancelled.", 
                                            "Print Cancelled", JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Error printing payslip: " + ex.getMessage(), 
                                        "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Save payslip as text file
     */
    private void savePayslip() {
        if (payslipContent == null || payslipContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No payslip to save.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Payslip");
        fileChooser.setSelectedFile(new java.io.File("payslip.txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(), payslipContent.getBytes());
                JOptionPane.showMessageDialog(this, "Payslip saved successfully to: " + file.getAbsolutePath(), 
                                            "Save Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving payslip: " + ex.getMessage(), 
                                            "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Static method to show payslip dialog
     */
    public static void showPayslipDialog(Window parent, EmployeeManager employeeManager, 
                                       PayslipGenerator payslipGenerator, Payroll payroll) {
        PayslipDialog dialog = new PayslipDialog(parent, employeeManager, payslipGenerator);
        dialog.displayPayslip(payroll);
        dialog.setVisible(true);
    }
    
    /**
     * Static method to show custom payslip dialog
     */
    public static void showCustomPayslipDialog(Window parent, EmployeeManager employeeManager, 
                                             PayslipGenerator payslipGenerator, int employeeId,
                                             String payPeriod, double basicSalary, 
                                             double overtimeHours, double nightDiffHours, double holidayDays,
                                             boolean isRegularHoliday, boolean workedOnHoliday,
                                             Map<String, Double> allowances, Map<String, Double> deductions,
                                             boolean include13thMonth) {
        PayslipDialog dialog = new PayslipDialog(parent, employeeManager, payslipGenerator);
        dialog.displayCustomPayslip(employeeId, payPeriod, basicSalary, overtimeHours, nightDiffHours, 
                                  holidayDays, isRegularHoliday, workedOnHoliday, allowances, deductions, include13thMonth);
        dialog.setVisible(true);
    }
}
