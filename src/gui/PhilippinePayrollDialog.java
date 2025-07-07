package gui;

import models.Employee;
import models.Payroll;
import managers.EmployeeManager;
import managers.PayrollManager;
import managers.SalaryComponentManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Philippine Payroll Dialog with specific calculations for Philippine labor laws
 */
public class PhilippinePayrollDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient PayrollManager payrollManager;
    private transient EmployeeManager employeeManager;
    private transient SalaryComponentManager salaryComponentManager;
    private transient Payroll payroll;
    private boolean confirmed = false;
    
    private JTextField employeeIdField;
    private JTextField payPeriodField;
    private JTextField basePayField;
    private JTextField overtimeHoursField;
    private JTextField nightDiffHoursField;
    private JTextField holidayDaysField;
    private JTextField payDateField;
    private JTextField netPayField;
    private JTextField totalEarningsField;
    private JTextField totalDeductionsField;
    
    // Philippine Allowances
    private JTextField riceAllowanceField;
    private JTextField transportAllowanceField;
    private JTextField mealAllowanceField;
    private JTextField connectivityAllowanceField;
    private JTextField medicalAllowanceField;
    private JTextField clothingAllowanceField;
    private JTextField educationAllowanceField;
    
    // Other Deductions
    private JTextField tardinessField;
    private JTextField absencesField;
    private JTextField cashAdvanceField;
    private JTextField loanPaymentField;
    
    private JCheckBox isRegularHolidayBox;
    private JCheckBox workedOnHolidayBox;
    private JCheckBox is13thMonthBox;
    private JCheckBox includeAllowancesBox;
    
    private JButton calculateButton;
    private JButton generatePayslipButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel employeeNameLabel;
    private JTextArea calculationSummaryArea;
    
    @SuppressWarnings("unused")
    private transient SalaryComponentManager.PayrollCalculationResult currentPayrollResult;
    
    @SuppressWarnings("this-escape")
    public PhilippinePayrollDialog(Window parent, PayrollManager payrollManager, 
                                 EmployeeManager employeeManager, SalaryComponentManager salaryComponentManager, Payroll payroll) {
        super(parent, payroll == null ? "Create Philippine Payroll - Automated HR System" : "Edit Philippine Payroll - Automated HR System", ModalityType.APPLICATION_MODAL);
        this.payrollManager = payrollManager;
        this.employeeManager = employeeManager;
        this.salaryComponentManager = salaryComponentManager;
        this.payroll = payroll;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (payroll != null) {
            populateFields();
        }
        
        setSize(1400, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        employeeIdField = new JTextField(8);
        employeeIdField.setToolTipText("Enter employee ID (e.g., EMP001)");
        payPeriodField = new JTextField(8);
        basePayField = new JTextField(8);
        overtimeHoursField = new JTextField(8);
        nightDiffHoursField = new JTextField(8);
        holidayDaysField = new JTextField(8);
        payDateField = new JTextField(8);
        netPayField = new JTextField(8);
        netPayField.setEditable(false);
        netPayField.setBackground(new Color(240, 240, 240));
        totalEarningsField = new JTextField(8);
        totalEarningsField.setEditable(false);
        totalEarningsField.setBackground(new Color(240, 240, 240));
        totalDeductionsField = new JTextField(8);
        totalDeductionsField.setEditable(false);
        totalDeductionsField.setBackground(new Color(240, 240, 240));
        
        // Philippine Allowances
        riceAllowanceField = new JTextField(8);
        transportAllowanceField = new JTextField(8);
        mealAllowanceField = new JTextField(8);
        connectivityAllowanceField = new JTextField(8);
        medicalAllowanceField = new JTextField(8);
        clothingAllowanceField = new JTextField(8);
        educationAllowanceField = new JTextField(8);
        
        // Other Deductions
        tardinessField = new JTextField(8);
        absencesField = new JTextField(8);
        cashAdvanceField = new JTextField(8);
        loanPaymentField = new JTextField(8);
        
        // Set default values
        riceAllowanceField.setText("2000.00");
        transportAllowanceField.setText("2000.00");
        mealAllowanceField.setText("1500.00");
        connectivityAllowanceField.setText("1000.00");
        medicalAllowanceField.setText("0.00");
        clothingAllowanceField.setText("0.00");
        educationAllowanceField.setText("0.00");
        
        tardinessField.setText("0.00");
        absencesField.setText("0.00");
        cashAdvanceField.setText("0.00");
        loanPaymentField.setText("0.00");
        
        isRegularHolidayBox = new JCheckBox("Regular Holiday");
        workedOnHolidayBox = new JCheckBox("Worked on Holiday");
        is13thMonthBox = new JCheckBox("Include 13th Month Pay");
        includeAllowancesBox = new JCheckBox("Include Standard Allowances", true);
        
        calculateButton = new JButton("Calculate Philippine Payroll");
        generatePayslipButton = new JButton("Generate Payslip");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        employeeNameLabel = new JLabel();
        
        calculationSummaryArea = new JTextArea(12, 30);
        calculationSummaryArea.setEditable(false);
        calculationSummaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        calculationSummaryArea.setBorder(BorderFactory.createTitledBorder("Philippine Payroll Calculation Summary"));
        
        // Style buttons
        calculateButton.setBackground(new Color(32, 178, 170));
        calculateButton.setForeground(Color.WHITE);
        generatePayslipButton.setBackground(new Color(255, 165, 0));
        generatePayslipButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        
        // Initially disable payslip and save buttons until calculation is done
        generatePayslipButton.setEnabled(false);
        saveButton.setEnabled(false);
        
        // Set default values
        overtimeHoursField.setText("0");
        nightDiffHoursField.setText("0");
        holidayDaysField.setText("0");
        payDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Add tooltips for better user experience
        payPeriodField.setToolTipText("Format: YYYY-MM (e.g., 2024-01)");
        basePayField.setToolTipText("Monthly basic salary in ₱ - Auto-populated when employee is selected");
        overtimeHoursField.setToolTipText("Total overtime hours worked - Auto-calculates on entry");
        nightDiffHoursField.setToolTipText("Total night shift hours (for 10% differential) - Auto-calculates on entry");
        holidayDaysField.setToolTipText("Number of holiday days - Auto-calculates on entry");
        payDateField.setToolTipText("Format: YYYY-MM-DD - Defaults to today");
        
        // Add tooltips for summary fields
        totalEarningsField.setToolTipText("Automatically calculated total earnings including all allowances and bonuses");
        totalDeductionsField.setToolTipText("Automatically calculated total deductions including SSS, PhilHealth, Pag-IBIG, and taxes");
        netPayField.setToolTipText("Automatically calculated net pay after all deductions");
        
        // Add tooltips for automated features
        calculateButton.setToolTipText("Click to manually calculate or values auto-calculate when fields are entered");
        generatePayslipButton.setToolTipText("Generate professional payslip (enabled after calculation)");
        saveButton.setToolTipText("Save payroll to database (enabled after calculation)");
        
        employeeNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        employeeNameLabel.setForeground(new Color(70, 130, 180));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel with horizontal layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create four main sections horizontally for better distribution
        JPanel leftPanel = createBasicInfoPanel();
        JPanel centerLeftPanel = createAllowancesPanel();
        JPanel centerRightPanel = createDeductionsPanel();
        JPanel rightPanel = createSummaryPanel();
        
        // Top panel for horizontal sections
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(leftPanel);
        topPanel.add(centerLeftPanel);
        topPanel.add(centerRightPanel);
        topPanel.add(rightPanel);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        
        // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(generatePayslipButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee & Basic Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(employeeIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Employee:"), gbc);
        gbc.gridx = 1;
        panel.add(employeeNameLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Pay Period:"), gbc);
        gbc.gridx = 1;
        panel.add(payPeriodField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Basic Salary (₱):"), gbc);
        gbc.gridx = 1;
        panel.add(basePayField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Overtime Hours:"), gbc);
        gbc.gridx = 1;
        panel.add(overtimeHoursField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Night Diff Hours:"), gbc);
        gbc.gridx = 1;
        panel.add(nightDiffHoursField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Holiday Days:"), gbc);
        gbc.gridx = 1;
        panel.add(holidayDaysField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Pay Date:"), gbc);
        gbc.gridx = 1;
        panel.add(payDateField, gbc);
        
        // Summary fields
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Total Earnings (₱):"), gbc);
        gbc.gridx = 1;
        panel.add(totalEarningsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Total Deductions (₱):"), gbc);
        gbc.gridx = 1;
        panel.add(totalDeductionsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Net Pay (₱):"), gbc);
        gbc.gridx = 1;
        panel.add(netPayField, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        
        // Checkboxes
        gbc.gridy = ++row;
        panel.add(includeAllowancesBox, gbc);
        
        gbc.gridy = ++row;
        panel.add(isRegularHolidayBox, gbc);
        
        gbc.gridy = ++row;
        panel.add(workedOnHolidayBox, gbc);
        
        gbc.gridy = ++row;
        panel.add(is13thMonthBox, gbc);
        
        return panel;
    }
    
    private JPanel createAllowancesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Philippine Allowances (₱)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Rice:"), gbc);
        gbc.gridx = 1;
        panel.add(riceAllowanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Transport:"), gbc);
        gbc.gridx = 1;
        panel.add(transportAllowanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Meal:"), gbc);
        gbc.gridx = 1;
        panel.add(mealAllowanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Connectivity:"), gbc);
        gbc.gridx = 1;
        panel.add(connectivityAllowanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Medical:"), gbc);
        gbc.gridx = 1;
        panel.add(medicalAllowanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Clothing:"), gbc);
        gbc.gridx = 1;
        panel.add(clothingAllowanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Education:"), gbc);
        gbc.gridx = 1;
        panel.add(educationAllowanceField, gbc);
        
        return panel;
    }
    
    private JPanel createDeductionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Other Deductions (₱)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tardiness:"), gbc);
        gbc.gridx = 1;
        panel.add(tardinessField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Absences:"), gbc);
        gbc.gridx = 1;
        panel.add(absencesField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Cash Advance:"), gbc);
        gbc.gridx = 1;
        panel.add(cashAdvanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        panel.add(new JLabel("Loan Payment:"), gbc);
        gbc.gridx = 1;
        panel.add(loanPaymentField, gbc);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Payroll Calculation Summary"));
        
        JScrollPane summaryScrollPane = new JScrollPane(calculationSummaryArea);
        summaryScrollPane.setPreferredSize(new Dimension(320, 300));
        panel.add(summaryScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Auto-calculate when employee ID is entered
        employeeIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeName();
                autoCalculateIfReady();
            }
        });
        
        // Auto-calculate when key fields change
        ActionListener autoCalculateListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoCalculateIfReady();
            }
        };
        
        basePayField.addActionListener(autoCalculateListener);
        overtimeHoursField.addActionListener(autoCalculateListener);
        nightDiffHoursField.addActionListener(autoCalculateListener);
        holidayDaysField.addActionListener(autoCalculateListener);
        
        // Auto-calculate when checkboxes change
        includeAllowancesBox.addActionListener(autoCalculateListener);
        isRegularHolidayBox.addActionListener(autoCalculateListener);
        workedOnHolidayBox.addActionListener(autoCalculateListener);
        is13thMonthBox.addActionListener(autoCalculateListener);
        
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePhilippinePayroll();
            }
        });
        
        generatePayslipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePayslip();
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePayroll();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void autoCalculateIfReady() {
        try {
            // Only auto-calculate if we have the basic required fields
            String employeeIdText = employeeIdField.getText().trim();
            String basePayText = basePayField.getText().trim();
            
            if (!employeeIdText.isEmpty() && !basePayText.isEmpty()) {
                Employee employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
                if (employee != null) {
                    // Perform automatic calculation
                    calculatePhilippinePayroll();
                }
            }
        } catch (Exception ex) {
            // Silent fail for auto-calculation
        }
    }
    
    private void updateEmployeeName() {
        try {
            String employeeIdText = employeeIdField.getText().trim();
            if (employeeIdText.isEmpty()) {
                employeeNameLabel.setText("");
                // Clear calculation fields
                totalEarningsField.setText("");
                totalDeductionsField.setText("");
                netPayField.setText("");
                calculationSummaryArea.setText("");
                generatePayslipButton.setEnabled(false);
                saveButton.setEnabled(false);
                return;
            }
            
            Employee employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
            if (employee != null) {
                employeeNameLabel.setText(employee.getFullName() + " - " + employee.getPosition());
                basePayField.setText(String.valueOf(employee.getBaseSalary()));
                employeeNameLabel.setForeground(new Color(0, 128, 0)); // Green for success
            } else {
                employeeNameLabel.setText("⚠ Employee not found");
                employeeNameLabel.setForeground(Color.RED);
                basePayField.setText("");
                // Clear calculation fields
                totalEarningsField.setText("");
                totalDeductionsField.setText("");
                netPayField.setText("");
                calculationSummaryArea.setText("");
                generatePayslipButton.setEnabled(false);
                saveButton.setEnabled(false);
            }
        } catch (Exception ex) {
            employeeNameLabel.setText("⚠ Error loading employee");
            employeeNameLabel.setForeground(Color.RED);
        }
    }
    
    private void calculatePhilippinePayroll() {
        // Validate data first
        if (!validatePayrollData()) {
            return;
        }
        
        try {
            // Show processing status
            calculationSummaryArea.setText("⏳ Processing payroll calculation...");
            calculateButton.setEnabled(false);
            
            // Get input values
            String employeeIdText = employeeIdField.getText().trim();
            double basicSalary = Double.parseDouble(basePayField.getText().trim());
            double overtimeHours = Double.parseDouble(overtimeHoursField.getText().trim());
            double nightDiffHours = Double.parseDouble(nightDiffHoursField.getText().trim());
            double holidayDays = Double.parseDouble(holidayDaysField.getText().trim());
            
            boolean isRegularHoliday = isRegularHolidayBox.isSelected();
            boolean workedOnHoliday = workedOnHolidayBox.isSelected();
            boolean include13thMonth = is13thMonthBox.isSelected();
            
            Employee employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid employee.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Use the new standard payroll calculation method
            SalaryComponentManager.PayrollCalculationResult result = 
                salaryComponentManager.calculateStandardPayroll(
                    employee.getEmployeeId(), 
                    basicSalary, 
                    overtimeHours, 
                    nightDiffHours, 
                    holidayDays, 
                    isRegularHoliday, 
                    workedOnHoliday, 
                    include13thMonth
                );
            
            // Get the formatted summary
            String summary = salaryComponentManager.getPayrollCalculationSummary(result);
            
            // Add employee details to the summary
            StringBuilder finalSummary = new StringBuilder();
            finalSummary.append("=== PHILIPPINE PAYROLL CALCULATION ===\n");
            finalSummary.append("Employee: ").append(employee.getFullName()).append("\n");
            finalSummary.append("Position: ").append(employee.getPosition()).append("\n");
            finalSummary.append("Pay Period: ").append(payPeriodField.getText()).append("\n\n");
            
            // Check minimum wage compliance
            boolean meetsMinWage = salaryComponentManager.isMinimumWageCompliant(result);
            finalSummary.append("Minimum Wage Compliance: ");
            finalSummary.append(meetsMinWage ? "✓ COMPLIANT" : "⚠ NOT COMPLIANT").append("\n\n");
            
            // Add the detailed calculation summary
            finalSummary.append(summary);
            
            // Display the summary
            calculationSummaryArea.setText(finalSummary.toString());
            calculationSummaryArea.setCaretPosition(0);
            
            // Update the summary fields automatically
            double totalEarnings = result.getTotalEarnings().doubleValue();
            totalEarningsField.setText(String.format("₱%.2f", totalEarnings));
            totalDeductionsField.setText(String.format("₱%.2f", result.getTotalDeductions().doubleValue()));
            netPayField.setText(String.format("₱%.2f", result.getNetPay().doubleValue()));
            
            // Store the result for payslip generation
            this.currentPayrollResult = result;
            
            // Enable payslip generation button
            generatePayslipButton.setEnabled(true);
            saveButton.setEnabled(true);
            calculateButton.setEnabled(true);
            
        } catch (NumberFormatException ex) {
            // Clear summary fields on error
            totalEarningsField.setText("");
            totalDeductionsField.setText("");
            netPayField.setText("");
            calculationSummaryArea.setText("⚠ Please enter valid numeric values for all fields.");
            generatePayslipButton.setEnabled(false);
            saveButton.setEnabled(false);
            calculateButton.setEnabled(true);
            
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for all fields.", 
                                        "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Clear summary fields on error
            totalEarningsField.setText("");
            totalDeductionsField.setText("");
            netPayField.setText("");
            calculationSummaryArea.setText("⚠ Error calculating payroll: " + ex.getMessage());
            generatePayslipButton.setEnabled(false);
            saveButton.setEnabled(false);
            calculateButton.setEnabled(true);
            
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error calculating payroll: " + ex.getMessage(), 
                                        "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generatePayslip() {
        try {
            // Get input values
            String employeeIdText = employeeIdField.getText().trim();
            double basicSalary = Double.parseDouble(basePayField.getText().trim());
            double overtimeHours = Double.parseDouble(overtimeHoursField.getText().trim());
            double nightDiffHours = Double.parseDouble(nightDiffHoursField.getText().trim());
            double holidayDays = Double.parseDouble(holidayDaysField.getText().trim());
            
            boolean isRegularHoliday = isRegularHolidayBox.isSelected();
            boolean workedOnHoliday = workedOnHolidayBox.isSelected();
            boolean include13thMonth = is13thMonthBox.isSelected();
            
            Employee employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid employee.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Prepare allowances and deductions
            java.util.Map<String, Double> allowances = new java.util.HashMap<>();
            java.util.Map<String, Double> deductions = new java.util.HashMap<>();
            
            if (includeAllowancesBox.isSelected()) {
                allowances.put("Rice Allowance", Double.parseDouble(riceAllowanceField.getText().trim()));
                allowances.put("Transportation Allowance", Double.parseDouble(transportAllowanceField.getText().trim()));
                allowances.put("Meal Allowance", Double.parseDouble(mealAllowanceField.getText().trim()));
                allowances.put("Connectivity Allowance", Double.parseDouble(connectivityAllowanceField.getText().trim()));
                allowances.put("Medical Allowance", Double.parseDouble(medicalAllowanceField.getText().trim()));
                allowances.put("Clothing Allowance", Double.parseDouble(clothingAllowanceField.getText().trim()));
                allowances.put("Education Allowance", Double.parseDouble(educationAllowanceField.getText().trim()));
            }
            
            // Add other deductions
            double tardiness = Double.parseDouble(tardinessField.getText().trim());
            double absences = Double.parseDouble(absencesField.getText().trim());
            double cashAdvance = Double.parseDouble(cashAdvanceField.getText().trim());
            double loanPayment = Double.parseDouble(loanPaymentField.getText().trim());
            
            if (tardiness > 0) deductions.put("Tardiness", tardiness);
            if (absences > 0) deductions.put("Absences", absences);
            if (cashAdvance > 0) deductions.put("Cash Advance", cashAdvance);
            if (loanPayment > 0) deductions.put("Loan Payment", loanPayment);
            
            // Create and show payslip
            managers.PayslipGenerator payslipGenerator = new managers.PayslipGenerator(employeeManager);
            PayslipDialog.showCustomPayslipDialog(this, employeeManager, payslipGenerator, 
                                                employee.getEmployeeId(), payPeriodField.getText().trim(),
                                                basicSalary, overtimeHours, nightDiffHours, holidayDays,
                                                isRegularHoliday, workedOnHoliday, allowances, deductions,
                                                include13thMonth);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", 
                                        "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating payslip: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateFields() {
        if (payroll != null) {
            Employee employee = employeeManager.getEmployee(payroll.getEmployeeId());
            if (employee != null) {
                employeeIdField.setText(employee.getFormattedEmployeeId());
                updateEmployeeName();
            }
            payPeriodField.setText(payroll.getPayPeriod());
            basePayField.setText(String.valueOf(payroll.getBasePay()));
            payDateField.setText(payroll.getPayDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            employeeIdField.setEnabled(false);
        }
    }
    
    private void savePayroll() {
        // Validate data first
        if (!validatePayrollData()) {
            return;
        }
        
        try {
            String employeeIdText = employeeIdField.getText().trim();
            String payPeriod = payPeriodField.getText().trim();
            double basePay = Double.parseDouble(basePayField.getText().trim());
            LocalDate payDate = LocalDate.parse(payDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            Employee employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid employee.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (payroll == null) {
                // Create new payroll with Philippine calculations
                payrollManager.createPayrollWithComponents(
                    employee.getEmployeeId(), payPeriod, basePay, 0.0, payDate);
                
                JOptionPane.showMessageDialog(this, "Philippine payroll created successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing payroll
                payrollManager.updatePayroll(payroll.getPayrollId(), payPeriod, basePay, 
                                           0.0, 0.0, 0.0, payDate);
                JOptionPane.showMessageDialog(this, "Philippine payroll updated successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving payroll: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Validates all payroll data before processing
     * Following HR best practices for data integrity
     */
    private boolean validatePayrollData() {
        StringBuilder errors = new StringBuilder();
        
        // Employee validation
        String employeeIdText = employeeIdField.getText().trim();
        if (employeeIdText.isEmpty()) {
            errors.append("• Employee ID is required\n");
        } else {
            Employee employee = employeeManager.getEmployeeByFormattedId(employeeIdText);
            if (employee == null) {
                errors.append("• Invalid Employee ID\n");
            }
        }
        
        // Pay period validation
        String payPeriod = payPeriodField.getText().trim();
        if (payPeriod.isEmpty()) {
            errors.append("• Pay period is required\n");
        } else if (!payPeriod.matches("\\d{4}-\\d{2}")) {
            errors.append("• Pay period must be in format YYYY-MM\n");
        }
        
        // Numeric field validation
        try {
            double basicSalary = Double.parseDouble(basePayField.getText().trim());
            if (basicSalary <= 0) {
                errors.append("• Basic salary must be greater than 0\n");
            }
        } catch (NumberFormatException ex) {
            errors.append("• Invalid basic salary amount\n");
        }
        
        try {
            double overtime = Double.parseDouble(overtimeHoursField.getText().trim());
            if (overtime < 0) {
                errors.append("• Overtime hours cannot be negative\n");
            }
        } catch (NumberFormatException ex) {
            errors.append("• Invalid overtime hours\n");
        }
        
        try {
            double nightDiff = Double.parseDouble(nightDiffHoursField.getText().trim());
            if (nightDiff < 0) {
                errors.append("• Night differential hours cannot be negative\n");
            }
        } catch (NumberFormatException ex) {
            errors.append("• Invalid night differential hours\n");
        }
        
        try {
            double holidays = Double.parseDouble(holidayDaysField.getText().trim());
            if (holidays < 0) {
                errors.append("• Holiday days cannot be negative\n");
            }
        } catch (NumberFormatException ex) {
            errors.append("• Invalid holiday days\n");
        }
        
        // Pay date validation
        try {
            LocalDate.parse(payDateField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ex) {
            errors.append("• Invalid pay date format (use YYYY-MM-DD)\n");
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Please correct the following errors:\n\n" + errors.toString(), 
                "Data Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
