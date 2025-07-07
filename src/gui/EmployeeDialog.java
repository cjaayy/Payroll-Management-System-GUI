package gui;

import models.Employee;
import models.EmployeeDocument;
import managers.EmployeeManager;
import managers.DepartmentPositionManager;
import managers.AddressManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for adding/editing employees with enhanced features
 */
public class EmployeeDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private transient EmployeeManager employeeManager;
    private transient Employee employee;
    private boolean confirmed = false;
    
    // Tabbed pane
    private JTabbedPane tabbedPane;
    
    // Basic Info Tab
    private JLabel employeeIdLabel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> positionComboBox;
    private JTextField jobTitleField;
    private JTextField managerField;
    private JTextField salaryField;
    private JTextField hireDateField;
    
    // Contact Information Tab
    private JTextField personalEmailField;
    private JTextField workPhoneField;
    private JTextField emergencyContactField;
    private JTextField emergencyPhoneField;
    private JTextField streetAddressField;
    private JComboBox<String> countryComboBox;
    private JComboBox<String> provinceStateComboBox;
    private JComboBox<String> cityComboBox;
    private JComboBox<String> barangayComboBox;
    private JTextField zipCodeField;
    private JTextField birthDateField;
    private JTextField ssnField;
    private JTextField nationalityField;
    private JComboBox<String> maritalStatusComboBox;
    
    // Employment Details Tab
    private JComboBox<String> employmentStatusComboBox;
    private JTextField joiningDateField;
    private JTextField probationEndDateField;
    private JTextField exitDateField;
    private JTextField exitReasonField;
    
    // Bank and Payment Details Tab
    private JTextField bankNameField;
    private JTextField accountNumberField;
    private JTextField accountHolderNameField;
    private JTextField bankBranchField;
    private JTextField routingNumberField;
    private JComboBox<String> paymentMethodComboBox;
    private JComboBox<String> paymentFrequencyComboBox;
    
    // Documents Tab
    private JTable documentsTable;
    private DefaultTableModel documentsTableModel;
    private JButton addDocumentButton;
    private JButton removeDocumentButton;
    private JButton viewDocumentButton;
    
    // Buttons
    private JButton saveButton;
    private JButton cancelButton;
    
    public EmployeeDialog(Window parent, EmployeeManager employeeManager, Employee employee) {
        super(parent, employee == null ? "Add Employee" : "Edit Employee", ModalityType.APPLICATION_MODAL);
        this.employeeManager = employeeManager;
        this.employee = employee;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (employee != null) {
            populateFields();
        }
        
        setSize(800, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Initialize basic info components
        initializeBasicInfoComponents();
        
        // Initialize contact info components
        initializeContactInfoComponents();
        
        // Initialize employment details components
        initializeEmploymentDetailsComponents();
        
        // Initialize bank and payment details components
        initializeBankPaymentComponents();
        
        // Initialize documents components
        initializeDocumentsComponents();
        
        // Initialize buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
    }
    
    private void initializeBasicInfoComponents() {
        employeeIdLabel = new JLabel();
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        
        // Initialize department combo box with all departments
        departmentComboBox = new JComboBox<>(DepartmentPositionManager.getAllDepartments());
        departmentComboBox.setEditable(false);
        departmentComboBox.addActionListener(e -> updatePositionComboBox());
        
        // Initialize position combo box (will be populated based on department selection)
        positionComboBox = new JComboBox<>(new String[0]);
        positionComboBox.setEditable(false);
        
        jobTitleField = new JTextField(20);
        managerField = new JTextField(20);
        salaryField = new JTextField(20);
        hireDateField = new JTextField(20);
        
        // Add placeholder text for date field
        hireDateField.setToolTipText("Format: YYYY-MM-DD");
        
        // Add tooltip for salary field
        salaryField.setToolTipText("Maximum salary: $99,999,999.99");
        
        // Set employee ID label
        if (employee != null) {
            String displayId = employee.getFormattedEmployeeId();
            employeeIdLabel.setText("Employee ID: " + displayId);
            employeeIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
            employeeIdLabel.setForeground(new Color(70, 130, 180));
        } else {
            employeeIdLabel.setText("Employee ID: (Auto-generated based on department and hire date)");
            employeeIdLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            employeeIdLabel.setForeground(new Color(100, 100, 100));
        }
    }
    
    private void initializeContactInfoComponents() {
        personalEmailField = new JTextField(20);
        workPhoneField = new JTextField(20);
        emergencyContactField = new JTextField(20);
        emergencyPhoneField = new JTextField(20);
        streetAddressField = new JTextField(20);
        zipCodeField = new JTextField(20);
        birthDateField = new JTextField(20);
        ssnField = new JTextField(20);
        nationalityField = new JTextField(20);
        
        // Initialize address dropdowns
        countryComboBox = new JComboBox<>();
        provinceStateComboBox = new JComboBox<>();
        cityComboBox = new JComboBox<>();
        barangayComboBox = new JComboBox<>();
        
        // Initialize marital status dropdown
        maritalStatusComboBox = new JComboBox<>(new String[]{
            "", "Single", "Married", "Divorced", "Widowed", "Separated"
        });
        
        // Load countries initially
        loadCountries();
        
        // Set up cascading dropdown listeners
        setupAddressDropdownListeners();
        
        // Add tooltips
        birthDateField.setToolTipText("Format: YYYY-MM-DD");
        ssnField.setToolTipText("Social Security Number or equivalent");
    }
    
    private void initializeEmploymentDetailsComponents() {
        // Employment status dropdown
        employmentStatusComboBox = new JComboBox<>(new String[]{
            "ACTIVE", "INACTIVE", "RESIGNED", "TERMINATED", "ON_LEAVE"
        });
        
        // Date fields
        joiningDateField = new JTextField(20);
        probationEndDateField = new JTextField(20);
        exitDateField = new JTextField(20);
        exitReasonField = new JTextField(20);
        
        // Add tooltips
        joiningDateField.setToolTipText("Format: YYYY-MM-DD (Usually same as hire date)");
        probationEndDateField.setToolTipText("Format: YYYY-MM-DD (End of probation period)");
        exitDateField.setToolTipText("Format: YYYY-MM-DD (Last working day)");
        exitReasonField.setToolTipText("Reason for leaving (if applicable)");
    }
    
    private void initializeBankPaymentComponents() {
        bankNameField = new JTextField(20);
        accountNumberField = new JTextField(20);
        accountHolderNameField = new JTextField(20);
        bankBranchField = new JTextField(20);
        routingNumberField = new JTextField(20);
        
        // Payment method dropdown
        paymentMethodComboBox = new JComboBox<>(new String[]{
            "BANK_TRANSFER", "CASH", "CHECK", "DIGITAL_WALLET"
        });
        
        // Payment frequency dropdown
        paymentFrequencyComboBox = new JComboBox<>(new String[]{
            "WEEKLY", "BIWEEKLY", "MONTHLY", "QUARTERLY", "ANNUALLY"
        });
        
        // Add tooltips
        accountNumberField.setToolTipText("Bank account number");
        routingNumberField.setToolTipText("Bank routing/sort code");
        bankBranchField.setToolTipText("Bank branch name or code");
    }
    
    private void initializeDocumentsComponents() {
        // Create table model
        documentsTableModel = new DefaultTableModel(new Object[]{
            "Document Type", "File Name", "Category", "Upload Date", "File Size", "Description"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        documentsTable = new JTable(documentsTableModel);
        documentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Create buttons
        addDocumentButton = new JButton("Add Document");
        removeDocumentButton = new JButton("Remove Document");
        viewDocumentButton = new JButton("View Document");
        
        // Add double-click listener to table for quick document viewing
        documentsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewSelectedDocument();
                }
            }
        });
    }
    
    private void setupLayout() {
        // Create panels for each tab
        JPanel basicInfoPanel = createBasicInfoPanel();
        JPanel contactInfoPanel = createContactInfoPanel();
        JPanel employmentDetailsPanel = createEmploymentDetailsPanel();
        JPanel bankPaymentPanel = createBankPaymentPanel();
        JPanel documentsPanel = createDocumentsPanel();
        
        // Add tabs
        tabbedPane.addTab("Basic Info", basicInfoPanel);
        tabbedPane.addTab("Contact Info", contactInfoPanel);
        tabbedPane.addTab("Employment Details", employmentDetailsPanel);
        tabbedPane.addTab("Bank & Payment", bankPaymentPanel);
        tabbedPane.addTab("Documents", documentsPanel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Layout main dialog
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Employee ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(employeeIdLabel, gbc);
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(departmentComboBox, gbc);
        
        // Position
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(positionComboBox, gbc);
        
        // Job Title
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Job Title:"), gbc);
        gbc.gridx = 1;
        panel.add(jobTitleField, gbc);
        
        // Manager
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Manager:"), gbc);
        gbc.gridx = 1;
        panel.add(managerField, gbc);
        
        // Salary
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        panel.add(salaryField, gbc);
        
        // Hire Date
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("Hire Date:"), gbc);
        gbc.gridx = 1;
        panel.add(hireDateField, gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Personal Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Personal Email:"), gbc);
        gbc.gridx = 1;
        panel.add(personalEmailField, gbc);
        row++;
        
        // Work Phone
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Work Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(workPhoneField, gbc);
        row++;
        
        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(emergencyContactField, gbc);
        row++;
        
        // Emergency Phone
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Emergency Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(emergencyPhoneField, gbc);
        row++;
        
        // Street Address
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Street Address:"), gbc);
        gbc.gridx = 1;
        panel.add(streetAddressField, gbc);
        row++;
        
        // Country
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Country:"), gbc);
        gbc.gridx = 1;
        panel.add(countryComboBox, gbc);
        row++;
        
        // Province/State
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Province/State:"), gbc);
        gbc.gridx = 1;
        panel.add(provinceStateComboBox, gbc);
        row++;
        
        // City
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        panel.add(cityComboBox, gbc);
        row++;
        
        // Barangay
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Barangay:"), gbc);
        gbc.gridx = 1;
        panel.add(barangayComboBox, gbc);
        row++;
        
        // Zip Code
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Zip Code:"), gbc);
        gbc.gridx = 1;
        panel.add(zipCodeField, gbc);
        row++;
        
        // Birth Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Birth Date:"), gbc);
        gbc.gridx = 1;
        panel.add(birthDateField, gbc);
        row++;
        
        // SSN
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("SSN:"), gbc);
        gbc.gridx = 1;
        panel.add(ssnField, gbc);
        row++;
        
        // Nationality
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        panel.add(nationalityField, gbc);
        row++;
        
        // Marital Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Marital Status:"), gbc);
        gbc.gridx = 1;
        panel.add(maritalStatusComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createEmploymentDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Employment Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Employment Status:"), gbc);
        gbc.gridx = 1;
        panel.add(employmentStatusComboBox, gbc);
        row++;
        
        // Joining Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Joining Date:"), gbc);
        gbc.gridx = 1;
        panel.add(joiningDateField, gbc);
        row++;
        
        // Probation End Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Probation End Date:"), gbc);
        gbc.gridx = 1;
        panel.add(probationEndDateField, gbc);
        row++;
        
        // Exit Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Exit Date:"), gbc);
        gbc.gridx = 1;
        panel.add(exitDateField, gbc);
        row++;
        
        // Exit Reason
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Exit Reason:"), gbc);
        gbc.gridx = 1;
        panel.add(exitReasonField, gbc);
        
        return panel;
    }
    
    private JPanel createBankPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Bank Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Bank Name:"), gbc);
        gbc.gridx = 1;
        panel.add(bankNameField, gbc);
        row++;
        
        // Account Number
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        panel.add(accountNumberField, gbc);
        row++;
        
        // Account Holder Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Account Holder Name:"), gbc);
        gbc.gridx = 1;
        panel.add(accountHolderNameField, gbc);
        row++;
        
        // Bank Branch
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Bank Branch:"), gbc);
        gbc.gridx = 1;
        panel.add(bankBranchField, gbc);
        row++;
        
        // Routing Number
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Routing Number:"), gbc);
        gbc.gridx = 1;
        panel.add(routingNumberField, gbc);
        row++;
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        panel.add(paymentMethodComboBox, gbc);
        row++;
        
        // Payment Frequency
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Payment Frequency:"), gbc);
        gbc.gridx = 1;
        panel.add(paymentFrequencyComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(documentsTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addDocumentButton);
        buttonPanel.add(removeDocumentButton);
        buttonPanel.add(viewDocumentButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Document buttons
        addDocumentButton.addActionListener(e -> addDocument());
        removeDocumentButton.addActionListener(e -> removeSelectedDocument());
        viewDocumentButton.addActionListener(e -> viewSelectedDocument());
        
        // Save and Cancel buttons
        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void updatePositionComboBox() {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        if (selectedDepartment != null && !selectedDepartment.isEmpty()) {
            String[] positions = DepartmentPositionManager.getPositionsForDepartment(selectedDepartment);
            positionComboBox.setModel(new DefaultComboBoxModel<>(positions));
        } else {
            positionComboBox.setModel(new DefaultComboBoxModel<>(new String[0]));
        }
    }
    
    private void loadCountries() {
        countryComboBox.removeAllItems();
        countryComboBox.addItem(""); // Empty option
        
        List<String> countries = AddressManager.getAllCountries();
        for (String country : countries) {
            countryComboBox.addItem(country);
        }
    }
    
    private void setupAddressDropdownListeners() {
        countryComboBox.addActionListener(e -> {
            String selectedCountry = (String) countryComboBox.getSelectedItem();
            loadProvinceStates(selectedCountry);
        });
        
        provinceStateComboBox.addActionListener(e -> {
            String selectedCountry = (String) countryComboBox.getSelectedItem();
            String selectedProvince = (String) provinceStateComboBox.getSelectedItem();
            loadCities(selectedCountry, selectedProvince);
        });
        
        cityComboBox.addActionListener(e -> {
            String selectedCountry = (String) countryComboBox.getSelectedItem();
            String selectedProvince = (String) provinceStateComboBox.getSelectedItem();
            String selectedCity = (String) cityComboBox.getSelectedItem();
            loadBarangays(selectedCountry, selectedProvince, selectedCity);
        });
    }
    
    private void loadProvinceStates(String country) {
        provinceStateComboBox.removeAllItems();
        cityComboBox.removeAllItems();
        barangayComboBox.removeAllItems();
        
        if (country != null && !country.isEmpty()) {
            provinceStateComboBox.addItem(""); // Empty option
            List<String> provinces = AddressManager.getProvincesStates(country);
            for (String province : provinces) {
                provinceStateComboBox.addItem(province);
            }
        }
    }
    
    private void loadCities(String country, String province) {
        cityComboBox.removeAllItems();
        barangayComboBox.removeAllItems();
        
        if (country != null && !country.isEmpty() && province != null && !province.isEmpty()) {
            cityComboBox.addItem(""); // Empty option
            List<String> cities = AddressManager.getCities(country, province);
            for (String city : cities) {
                cityComboBox.addItem(city);
            }
        }
    }
    
    private void loadBarangays(String country, String province, String city) {
        barangayComboBox.removeAllItems();
        
        if (country != null && !country.isEmpty() && province != null && !province.isEmpty() && 
            city != null && !city.isEmpty()) {
            barangayComboBox.addItem(""); // Empty option
            List<String> barangays = AddressManager.getBarangays(country, province, city);
            for (String barangay : barangays) {
                barangayComboBox.addItem(barangay);
            }
        }
    }
    
    private void populateFields() {
        // Basic info
        String selectedDepartment = employee.getDepartment();
        departmentComboBox.setSelectedItem(selectedDepartment);
        updatePositionComboBox();
        
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone() != null ? employee.getPhone() : "");
        positionComboBox.setSelectedItem(employee.getPosition());
        jobTitleField.setText(employee.getJobTitle() != null ? employee.getJobTitle() : "");
        managerField.setText(employee.getManager() != null ? employee.getManager() : "");
        salaryField.setText(String.valueOf(employee.getSalary()));
        hireDateField.setText(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Contact info
        populateContactInfo();
        
        // Employment details
        populateEmploymentDetails();
        
        // Bank and payment details
        populateBankPaymentDetails();
        
        // Documents
        refreshDocumentsTable();
    }
    
    private void populateContactInfo() {
        personalEmailField.setText(employee.getPersonalEmail() != null ? employee.getPersonalEmail() : "");
        workPhoneField.setText(employee.getWorkPhone() != null ? employee.getWorkPhone() : "");
        emergencyContactField.setText(employee.getEmergencyContact() != null ? employee.getEmergencyContact() : "");
        emergencyPhoneField.setText(employee.getEmergencyPhone() != null ? employee.getEmergencyPhone() : "");
        streetAddressField.setText(employee.getStreetAddress() != null ? employee.getStreetAddress() : "");
        zipCodeField.setText(employee.getZipCode() != null ? employee.getZipCode() : "");
        
        if (employee.getBirthDate() != null) {
            birthDateField.setText(employee.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        ssnField.setText(employee.getSocialSecurityNumber() != null ? employee.getSocialSecurityNumber() : "");
        nationalityField.setText(employee.getNationality() != null ? employee.getNationality() : "");
        maritalStatusComboBox.setSelectedItem(employee.getMaritalStatus() != null ? employee.getMaritalStatus() : "");
        
        // Address cascade
        String country = employee.getCountry();
        if (country != null && !country.isEmpty()) {
            countryComboBox.setSelectedItem(country);
            loadProvinceStates(country);
            
            String provinceState = employee.getProvinceState();
            if (provinceState != null && !provinceState.isEmpty()) {
                provinceStateComboBox.setSelectedItem(provinceState);
                loadCities(country, provinceState);
                
                String city = employee.getCity();
                if (city != null && !city.isEmpty()) {
                    cityComboBox.setSelectedItem(city);
                    loadBarangays(country, provinceState, city);
                    
                    String barangay = employee.getBarangay();
                    if (barangay != null && !barangay.isEmpty()) {
                        barangayComboBox.setSelectedItem(barangay);
                    }
                }
            }
        }
    }
    
    private void populateEmploymentDetails() {
        employmentStatusComboBox.setSelectedItem(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus() : "ACTIVE");
        
        if (employee.getJoiningDate() != null) {
            joiningDateField.setText(employee.getJoiningDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        if (employee.getProbationEndDate() != null) {
            probationEndDateField.setText(employee.getProbationEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        if (employee.getExitDate() != null) {
            exitDateField.setText(employee.getExitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        exitReasonField.setText(employee.getExitReason() != null ? employee.getExitReason() : "");
    }
    
    private void populateBankPaymentDetails() {
        bankNameField.setText(employee.getBankName() != null ? employee.getBankName() : "");
        accountNumberField.setText(employee.getAccountNumber() != null ? employee.getAccountNumber() : "");
        accountHolderNameField.setText(employee.getAccountHolderName() != null ? employee.getAccountHolderName() : "");
        bankBranchField.setText(employee.getBankBranch() != null ? employee.getBankBranch() : "");
        routingNumberField.setText(employee.getRoutingNumber() != null ? employee.getRoutingNumber() : "");
        
        paymentMethodComboBox.setSelectedItem(employee.getPaymentMethod() != null ? employee.getPaymentMethod() : "BANK_TRANSFER");
        paymentFrequencyComboBox.setSelectedItem(employee.getPaymentFrequency() != null ? employee.getPaymentFrequency() : "MONTHLY");
    }
    
    private void refreshDocumentsTable() {
        documentsTableModel.setRowCount(0);
        
        if (employee != null && employee.getDocuments() != null) {
            for (EmployeeDocument document : employee.getDocuments()) {
                String category = getDocumentCategory(document.getDocumentType());
                Object[] rowData = {
                    document.getDocumentType(),
                    document.getFileName(),
                    category != null ? category : "Other",
                    document.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    formatFileSize(document.getFileSize()),
                    document.getDescription() != null ? document.getDescription() : ""
                };
                documentsTableModel.addRow(rowData);
            }
        }
    }
    
    private String getDocumentCategory(String documentType) {
        if (documentType == null) return "Other";
        
        String type = documentType.toLowerCase();
        if (type.contains("id") || type.contains("license") || type.contains("passport")) {
            return "Identification";
        } else if (type.contains("contract") || type.contains("offer")) {
            return "Employment";
        } else if (type.contains("resume") || type.contains("cv")) {
            return "Resume";
        } else if (type.contains("certificate") || type.contains("diploma")) {
            return "Education";
        } else if (type.contains("photo") || type.contains("image")) {
            return "Photo";
        } else {
            return "Other";
        }
    }
    
    private String formatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
    
    private void saveEmployee() {
        try {
            // Validate required fields
            if (firstNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (lastNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Last name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create or update employee
            if (employee == null) {
                employee = createNewEmployee();
            } else {
                updateExistingEmployee();
            }
            
            // Save to database (use the enhanced save method that handles documents)
            boolean success = employeeManager.saveEmployeeWithContactInfo(employee);
            
            if (success) {
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private Employee createNewEmployee() {
        LocalDate hireDate = parseDate(hireDateField.getText());
        String department = (String) departmentComboBox.getSelectedItem();
        String position = (String) positionComboBox.getSelectedItem();
        double salary = Double.parseDouble(salaryField.getText());
        
        Employee newEmployee = new Employee(0, firstNameField.getText().trim(), lastNameField.getText().trim(),
                                          emailField.getText().trim(), department, position, salary, hireDate);
        
        updateEmployeeFromFields(newEmployee);
        return newEmployee;
    }
    
    private void updateExistingEmployee() {
        employee.setFirstName(firstNameField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setDepartment((String) departmentComboBox.getSelectedItem());
        employee.setPosition((String) positionComboBox.getSelectedItem());
        employee.setSalary(Double.parseDouble(salaryField.getText()));
        employee.setHireDate(parseDate(hireDateField.getText()));
        
        updateEmployeeFromFields(employee);
    }
    
    private void updateEmployeeFromFields(Employee emp) {
        // Basic info
        emp.setPhone(phoneField.getText().trim());
        emp.setJobTitle(jobTitleField.getText().trim());
        emp.setManager(managerField.getText().trim());
        
        // Contact info
        emp.setPersonalEmail(personalEmailField.getText().trim());
        emp.setWorkPhone(workPhoneField.getText().trim());
        emp.setEmergencyContact(emergencyContactField.getText().trim());
        emp.setEmergencyPhone(emergencyPhoneField.getText().trim());
        emp.setStreetAddress(streetAddressField.getText().trim());
        emp.setCountry((String) countryComboBox.getSelectedItem());
        emp.setProvinceState((String) provinceStateComboBox.getSelectedItem());
        emp.setCity((String) cityComboBox.getSelectedItem());
        emp.setBarangay((String) barangayComboBox.getSelectedItem());
        emp.setZipCode(zipCodeField.getText().trim());
        emp.setBirthDate(parseDate(birthDateField.getText()));
        emp.setSocialSecurityNumber(ssnField.getText().trim());
        emp.setNationality(nationalityField.getText().trim());
        emp.setMaritalStatus((String) maritalStatusComboBox.getSelectedItem());
        
        // Employment details
        emp.setEmploymentStatus((String) employmentStatusComboBox.getSelectedItem());
        emp.setJoiningDate(parseDate(joiningDateField.getText()));
        emp.setProbationEndDate(parseDate(probationEndDateField.getText()));
        emp.setExitDate(parseDate(exitDateField.getText()));
        emp.setExitReason(exitReasonField.getText().trim());
        
        // Bank and payment details
        emp.setBankName(bankNameField.getText().trim());
        emp.setAccountNumber(accountNumberField.getText().trim());
        emp.setAccountHolderName(accountHolderNameField.getText().trim());
        emp.setBankBranch(bankBranchField.getText().trim());
        emp.setRoutingNumber(routingNumberField.getText().trim());
        emp.setPaymentMethod((String) paymentMethodComboBox.getSelectedItem());
        emp.setPaymentFrequency((String) paymentFrequencyComboBox.getSelectedItem());
    }
    
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateString.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    private void addDocument() {
        try {
            // Document type selection
            String[] documentTypes = {
                "ID Card/Passport", "Resume/CV", "Diploma/Certificate", "Birth Certificate",
                "Marriage Certificate", "Medical Certificate", "Background Check", 
                "Employment Contract", "Tax Documents", "Bank Details", "Other"
            };
            
            String documentType = (String) JOptionPane.showInputDialog(this,
                "Select document type:", "Document Type",
                JOptionPane.QUESTION_MESSAGE, null, documentTypes, documentTypes[0]);
            
            if (documentType == null) return;
            
            // File selection
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Select Document File");
            
            // Set file filters
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Document Files", "pdf", "doc", "docx", "txt", "jpg", "jpeg", "png", "gif", "bmp"));
            
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;
            
            java.io.File selectedFile = fileChooser.getSelectedFile();
            
            // Check file size (limit to 10MB)
            long maxSize = 10 * 1024 * 1024; // 10MB in bytes
            if (selectedFile.length() > maxSize) {
                JOptionPane.showMessageDialog(this, 
                    "File size exceeds 10MB limit. Please select a smaller file.", 
                    "File Too Large", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Description input
            String description = JOptionPane.showInputDialog(this, 
                "Enter document description (optional):", "Document Description", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (description == null) description = "";
            
            // Read file data
            byte[] fileData = java.nio.file.Files.readAllBytes(selectedFile.toPath());
            
            // Create employee document
            EmployeeDocument document = new EmployeeDocument(
                employee != null ? employee.getEmployeeId() : 0,
                documentType,
                selectedFile.getName(),
                selectedFile.getAbsolutePath(),
                fileData,
                description
            );
            
            // Add to employee's document list
            if (employee != null) {
                employee.addDocument(document);
                refreshDocumentsTable();
                JOptionPane.showMessageDialog(this, 
                    "Document added successfully!\n" +
                    "File: " + selectedFile.getName() + "\n" +
                    "Size: " + formatFileSize(selectedFile.length()) + "\n" +
                    "Type: " + documentType, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding document: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void removeSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a document to remove.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String fileName = (String) documentsTableModel.getValueAt(selectedRow, 1);
        String documentType = (String) documentsTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this document?\n" +
            "Type: " + documentType + "\n" +
            "File: " + fileName,
            "Confirm Removal", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (employee != null && selectedRow < employee.getDocuments().size()) {
                    employee.getDocuments().remove(selectedRow);
                    refreshDocumentsTable();
                    JOptionPane.showMessageDialog(this, 
                        "Document removed successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error removing document: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a document to view.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (employee != null && selectedRow < employee.getDocuments().size()) {
                EmployeeDocument document = employee.getDocuments().get(selectedRow);
                
                // Show document details
                StringBuilder details = new StringBuilder();
                details.append("Document Details\n");
                details.append("================\n\n");
                details.append("Type: ").append(document.getDocumentType()).append("\n");
                details.append("File Name: ").append(document.getFileName()).append("\n");
                details.append("File Size: ").append(formatFileSize(document.getFileSize())).append("\n");
                details.append("MIME Type: ").append(document.getMimeType()).append("\n");
                details.append("Upload Date: ").append(document.getUploadDate().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
                details.append("Uploaded By: ").append(document.getUploadedBy()).append("\n");
                details.append("Description: ").append(document.getDescription() != null ? 
                    document.getDescription() : "No description").append("\n");
                
                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                // Add options: Close, Save File, Preview File
                Object[] options = {"Close", "Save File", "Preview File"};
                int option = JOptionPane.showOptionDialog(this, scrollPane, 
                    "Document Details", JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                
                if (option == 1) { // Save File
                    saveDocumentFile(document);
                } else if (option == 2) { // Preview File
                    previewDocumentFile(document);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error viewing document: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void previewDocumentFile(EmployeeDocument document) {
        try {
            // Create a temporary file for preview
            String fileName = document.getFileName();
            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }
            
            // Create temp file
            java.io.File tempFile = java.io.File.createTempFile("preview_", extension);
            tempFile.deleteOnExit(); // Clean up on exit
            
            // Write document data to temp file
            java.nio.file.Files.write(tempFile.toPath(), document.getFileData());
            
            // Try to open the file with the default system application
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(tempFile);
                    JOptionPane.showMessageDialog(this, 
                        "Document opened in default application.\n" +
                        "File: " + fileName + "\n" +
                        "Note: This is a temporary preview file.", 
                        "Preview Opened", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Fallback: offer to save the file
                    int option = JOptionPane.showConfirmDialog(this, 
                        "Cannot open file for preview on this system.\n" +
                        "Would you like to save the file to view it manually?", 
                        "Preview Not Available", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        saveDocumentFile(document);
                    }
                }
            } else {
                // Desktop not supported, offer to save
                int option = JOptionPane.showConfirmDialog(this, 
                    "File preview not supported on this system.\n" +
                    "Would you like to save the file to view it manually?", 
                    "Preview Not Available", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    saveDocumentFile(document);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error previewing document: " + e.getMessage() + "\n" +
                "You can try saving the file instead.", 
                "Preview Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveDocumentFile(EmployeeDocument document) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(document.getFileName()));
            fileChooser.setDialogTitle("Save Document As");
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File outputFile = fileChooser.getSelectedFile();
                java.nio.file.Files.write(outputFile.toPath(), document.getFileData());
                
                JOptionPane.showMessageDialog(this, 
                    "Document saved successfully to:\n" + outputFile.getAbsolutePath(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving document: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Employee getEmployee() {
        return employee;
    }
}
