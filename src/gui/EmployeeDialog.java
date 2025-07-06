package gui;

import models.Employee;
import models.EmployeeDocument;
import managers.EmployeeManager;
import managers.DepartmentPositionManager;
import managers.AddressManager;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for adding/editing employees with tabbed interface
 */
public class EmployeeDialog extends JDialog {
    private EmployeeManager employeeManager;
    private Employee employee;
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
        
        setSize(700, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Initialize basic info components
        initializeBasicInfoComponents();
        
        // Initialize contact info components
        initializeContactInfoComponents();
        
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
        
        if (country != null && !country.isEmpty() && province != null && !province.isEmpty() 
            && city != null && !city.isEmpty()) {
            barangayComboBox.addItem(""); // Empty option
            List<String> barangays = AddressManager.getBarangays(country, province, city);
            for (String barangay : barangays) {
                barangayComboBox.addItem(barangay);
            }
        }
    }
    
    /**
     * Update the position combo box based on selected department
     */
    private void updatePositionComboBox() {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        if (selectedDepartment != null) {
            String[] positions = DepartmentPositionManager.getPositionsForDepartment(selectedDepartment);
            positionComboBox.removeAllItems();
            for (String position : positions) {
                positionComboBox.addItem(position);
            }
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create and add tabs
        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        tabbedPane.addTab("Contact Information", createContactInfoPanel());
        tabbedPane.addTab("Documents", createDocumentsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employee ID - spans across both columns
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(employeeIdLabel, gbc);
        
        // Reset gridwidth for other components
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(departmentComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(positionComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Job Title:"), gbc);
        gbc.gridx = 1;
        panel.add(jobTitleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Manager:"), gbc);
        gbc.gridx = 1;
        panel.add(managerField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Base Salary:"), gbc);
        gbc.gridx = 1;
        panel.add(salaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("Hire Date:"), gbc);
        gbc.gridx = 1;
        panel.add(hireDateField, gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Personal Information Section
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 10, 5);
        JLabel personalLabel = new JLabel("Personal Information");
        personalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        personalLabel.setForeground(new Color(70, 130, 180));
        panel.add(personalLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Personal Email:"), gbc);
        gbc.gridx = 1;
        panel.add(personalEmailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Work Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(workPhoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Birth Date:"), gbc);
        gbc.gridx = 1;
        panel.add(birthDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("SSN/ID Number:"), gbc);
        gbc.gridx = 1;
        panel.add(ssnField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        panel.add(nationalityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Marital Status:"), gbc);
        gbc.gridx = 1;
        panel.add(maritalStatusComboBox, gbc);
        
        // Emergency Contact Section
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 10, 5);
        JLabel emergencyLabel = new JLabel("Emergency Contact");
        emergencyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emergencyLabel.setForeground(new Color(70, 130, 180));
        panel.add(emergencyLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(emergencyContactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Emergency Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(emergencyPhoneField, gbc);
        
        // Address Section
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 10, 5);
        JLabel addressLabel = new JLabel("Address Information");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressLabel.setForeground(new Color(70, 130, 180));
        panel.add(addressLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 11;
        panel.add(new JLabel("Street Address:"), gbc);
        gbc.gridx = 1;
        panel.add(streetAddressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 12;
        panel.add(new JLabel("Country:"), gbc);
        gbc.gridx = 1;
        panel.add(countryComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 13;
        panel.add(new JLabel("Province/State:"), gbc);
        gbc.gridx = 1;
        panel.add(provinceStateComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 14;
        panel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        panel.add(cityComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 15;
        panel.add(new JLabel("Barangay:"), gbc);
        gbc.gridx = 1;
        panel.add(barangayComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 16;
        panel.add(new JLabel("ZIP Code:"), gbc);
        gbc.gridx = 1;
        panel.add(zipCodeField, gbc);
        
        // Add panel to scroll pane for better navigation
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void initializeDocumentsComponents() {
        // Initialize documents table with enhanced columns
        String[] columnNames = {"Document Type", "File Name", "Category", "Upload Date", "File Size", "Description"};
        documentsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        documentsTable = new JTable(documentsTableModel);
        documentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentsTable.getTableHeader().setReorderingAllowed(false);
        documentsTable.setRowHeight(25);
        
        // Set column widths
        documentsTable.getColumnModel().getColumn(0).setPreferredWidth(130); // Document Type
        documentsTable.getColumnModel().getColumn(1).setPreferredWidth(180); // File Name
        documentsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Category
        documentsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Upload Date
        documentsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // File Size
        documentsTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Description
        
        // Initialize buttons with enhanced styling and tooltips
        addDocumentButton = new JButton("📁 Add Document");
        removeDocumentButton = new JButton("🗑️ Remove Document");
        viewDocumentButton = new JButton("👁️ View Details");
        
        // Enhanced button styling
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        Color primaryColor = new Color(70, 130, 180);
        Color successColor = new Color(40, 167, 69);
        Color dangerColor = new Color(220, 53, 69);
        
        addDocumentButton.setFont(buttonFont);
        addDocumentButton.setBackground(successColor);
        addDocumentButton.setForeground(Color.WHITE);
        addDocumentButton.setToolTipText("Upload a new document (PDF, DOC, Images, etc.)");
        addDocumentButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        removeDocumentButton.setFont(buttonFont);
        removeDocumentButton.setBackground(dangerColor);
        removeDocumentButton.setForeground(Color.WHITE);
        removeDocumentButton.setToolTipText("Remove the selected document permanently");
        removeDocumentButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        viewDocumentButton.setFont(buttonFont);
        viewDocumentButton.setBackground(primaryColor);
        viewDocumentButton.setForeground(Color.WHITE);
        viewDocumentButton.setToolTipText("View document details, preview content, and export");
        viewDocumentButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Add button listeners
        addDocumentButton.addActionListener(e -> addDocument());
        removeDocumentButton.addActionListener(e -> removeSelectedDocument());
        viewDocumentButton.addActionListener(e -> viewSelectedDocument());
    }
    
    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEmployee();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void populateFields() {
        // Populate basic info
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone() == null ? "" : employee.getPhone());
        
        // Set department and update positions
        departmentComboBox.setSelectedItem(employee.getDepartment());
        updatePositionComboBox();
        positionComboBox.setSelectedItem(employee.getPosition());
        
        jobTitleField.setText(employee.getJobTitle() != null ? employee.getJobTitle() : "");
        managerField.setText(employee.getManager() != null ? employee.getManager() : "");
        
        salaryField.setText(String.valueOf(employee.getBaseSalary()));
        hireDateField.setText(employee.getHireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Populate contact info
        populateContactInfo();
        
        // Populate documents
        refreshDocumentsTable();
    }
    
    private void populateContactInfo() {
        // Personal email
        personalEmailField.setText(employee.getPersonalEmail() != null ? employee.getPersonalEmail() : "");
        
        // Work phone
        workPhoneField.setText(employee.getWorkPhone() != null ? employee.getWorkPhone() : "");
        
        // Emergency contact
        emergencyContactField.setText(employee.getEmergencyContact() != null ? employee.getEmergencyContact() : "");
        emergencyPhoneField.setText(employee.getEmergencyPhone() != null ? employee.getEmergencyPhone() : "");
        
        // Address information
        streetAddressField.setText(employee.getStreetAddress() != null ? employee.getStreetAddress() : "");
        
        // Set country and trigger cascade loading
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
        
        zipCodeField.setText(employee.getZipCode() != null ? employee.getZipCode() : "");
        
        // Personal information
        if (employee.getBirthDate() != null) {
            birthDateField.setText(employee.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        ssnField.setText(employee.getSocialSecurityNumber() != null ? employee.getSocialSecurityNumber() : "");
        nationalityField.setText(employee.getNationality() != null ? employee.getNationality() : "");
        
        String maritalStatus = employee.getMaritalStatus();
        if (maritalStatus != null && !maritalStatus.isEmpty()) {
            maritalStatusComboBox.setSelectedItem(maritalStatus);
        }
    }
    
    private void saveEmployee() {
        try {
            // Get basic information
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String department = (String) departmentComboBox.getSelectedItem();
            String position = (String) positionComboBox.getSelectedItem();
            String jobTitle = jobTitleField.getText().trim();
            String manager = managerField.getText().trim();
            String salaryText = salaryField.getText().trim();
            String hireDateText = hireDateField.getText().trim();
            
            // Get contact information
            String personalEmail = personalEmailField.getText().trim();
            String workPhone = workPhoneField.getText().trim();
            String emergencyContact = emergencyContactField.getText().trim();
            String emergencyPhone = emergencyPhoneField.getText().trim();
            String streetAddress = streetAddressField.getText().trim();
            String country = (String) countryComboBox.getSelectedItem();
            String provinceState = (String) provinceStateComboBox.getSelectedItem();
            String city = (String) cityComboBox.getSelectedItem();
            String barangay = (String) barangayComboBox.getSelectedItem();
            String zipCode = zipCodeField.getText().trim();
            String birthDateText = birthDateField.getText().trim();
            String ssn = ssnField.getText().trim();
            String nationality = nationalityField.getText().trim();
            String maritalStatus = (String) maritalStatusComboBox.getSelectedItem();
            
            // Validate required fields (job title and manager are optional)
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
                department == null || department.isEmpty() || position == null || position.isEmpty() || 
                salaryText.isEmpty() || hireDateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.\nNote: Phone, Job Title, and Manager are optional.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate email format
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate personal email if provided
            if (!personalEmail.isEmpty() && !isValidEmail(personalEmail)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid personal email address.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check for duplicate email (for updates, exclude current employee)
            int excludeId = (employee != null) ? employee.getEmployeeId() : -1;
            if (!employeeManager.isEmailAvailable(email, excludeId)) {
                JOptionPane.showMessageDialog(this, "This email address is already in use by another employee.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate phone format (optional field)
            if (!phone.isEmpty() && !isValidPhone(phone)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid phone number (e.g., 123-456-7890).", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse salary
            double salary;
            try {
                salary = Double.parseDouble(salaryText);
                System.out.println("Parsed salary: " + salary);
                if (salary < 0) {
                    JOptionPane.showMessageDialog(this, "Salary must be a positive number.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Check if salary exceeds database limit (DECIMAL(10,2) = max 99,999,999.99)
                if (salary > 99999999.99) {
                    JOptionPane.showMessageDialog(this, "Salary cannot exceed $99,999,999.99.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid salary format. Please enter a valid number.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse hire date
            LocalDate hireDate;
            try {
                hireDate = LocalDate.parse(hireDateText);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid hire date format. Please use YYYY-MM-DD.", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse birth date (optional)
            LocalDate birthDate = null;
            if (!birthDateText.isEmpty()) {
                try {
                    birthDate = LocalDate.parse(birthDateText);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid birth date format. Please use YYYY-MM-DD.", 
                                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Create or update employee
            if (employee == null || employee.getEmployeeId() == 0) {
                // Create new employee
                List<EmployeeDocument> tempDocuments = null;
                if (employee != null && employee.getDocuments() != null) {
                    tempDocuments = new ArrayList<>(employee.getDocuments());
                    System.out.println("Preserving " + tempDocuments.size() + " uploaded documents for new employee");
                }
                
                employee = employeeManager.createEmployee(firstName, lastName, email, phone, 
                    department, position, jobTitle, manager, salary, hireDate);
                
                if (employee != null) {
                    // Restore documents to the new employee object
                    if (tempDocuments != null && !tempDocuments.isEmpty()) {
                        for (EmployeeDocument doc : tempDocuments) {
                            employee.addDocument(doc);
                        }
                        System.out.println("Restored " + tempDocuments.size() + " documents to new employee");
                    }
                    
                    // Set contact information
                    setEmployeeContactInfo(employee, personalEmail, workPhone, emergencyContact, emergencyPhone,
                                         streetAddress, country, provinceState, city, barangay, zipCode,
                                         birthDate, ssn, nationality, maritalStatus);
                    
                    // Save with contact info
                    if (employeeManager.saveEmployeeWithContactInfo(employee)) {
                        confirmed = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save employee contact information. Please try again.", 
                                                    "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add employee. Please try again.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing employee
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setEmail(email);
                employee.setPhone(phone.isEmpty() ? null : phone);
                employee.setDepartment(department);
                employee.setPosition(position);
                employee.setJobTitle(jobTitle.isEmpty() ? null : jobTitle);
                employee.setManager(manager.isEmpty() ? null : manager);
                employee.setBaseSalary(salary);
                employee.setHireDate(hireDate);
                
                // Set contact information
                setEmployeeContactInfo(employee, personalEmail, workPhone, emergencyContact, emergencyPhone,
                                     streetAddress, country, provinceState, city, barangay, zipCode,
                                     birthDate, ssn, nationality, maritalStatus);
                
                // Save with contact info
                if (employeeManager.saveEmployeeWithContactInfo(employee)) {
                    confirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update employee. Please check for duplicate email addresses.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the employee: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setEmployeeContactInfo(Employee emp, String personalEmail, String workPhone, 
                                       String emergencyContact, String emergencyPhone, String streetAddress,
                                       String country, String provinceState, String city, String barangay,
                                       String zipCode, LocalDate birthDate, String ssn, String nationality,
                                       String maritalStatus) {
        // Set contact information
        emp.setPersonalEmail(personalEmail.isEmpty() ? null : personalEmail);
        emp.setWorkPhone(workPhone.isEmpty() ? null : workPhone);
        emp.setEmergencyContact(emergencyContact.isEmpty() ? null : emergencyContact);
        emp.setEmergencyPhone(emergencyPhone.isEmpty() ? null : emergencyPhone);
        
        // Set address information
        emp.setStreetAddress(streetAddress.isEmpty() ? null : streetAddress);
        emp.setCountry(country != null && !country.isEmpty() ? country : null);
        emp.setProvinceState(provinceState != null && !provinceState.isEmpty() ? provinceState : null);
        emp.setCity(city != null && !city.isEmpty() ? city : null);
        emp.setBarangay(barangay != null && !barangay.isEmpty() ? barangay : null);
        emp.setZipCode(zipCode.isEmpty() ? null : zipCode);
        
        // Set personal information
        emp.setBirthDate(birthDate);
        emp.setSocialSecurityNumber(ssn.isEmpty() ? null : ssn);
        emp.setNationality(nationality.isEmpty() ? null : nationality);
        emp.setMaritalStatus(maritalStatus != null && !maritalStatus.isEmpty() ? maritalStatus : null);
    }
    
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }
    
    private boolean isValidPhone(String phone) {
        // Allow various phone formats: 123-456-7890, (123) 456-7890, 123.456.7890, 1234567890
        String phonePattern = "^[\\+]?[1-9]?[0-9]{0,2}[-\\s\\.]?\\(?[0-9]{3}\\)?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4}$";
        return phone.matches(phonePattern);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Employee getEmployee() {
        return employee;
    }

    private JPanel createDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header label
        JLabel headerLabel = new JLabel("Employee Documents");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(new Color(70, 130, 180));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Documents table with scroll pane
        JScrollPane scrollPane = new JScrollPane(documentsTable);
        scrollPane.setPreferredSize(new Dimension(550, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Document List"));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(addDocumentButton);
        buttonPanel.add(viewDocumentButton);
        buttonPanel.add(removeDocumentButton);
        
        // Info panel with comprehensive file type information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Upload Guidelines"));
        
        JLabel infoLabel1 = new JLabel("<html><center><b>Supported Document Types:</b></center></html>");
        infoLabel1.setFont(new Font("Arial", Font.BOLD, 12));
        infoLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel2 = new JLabel("<html><center>" +
            "<b>Documents:</b> PDF, DOC, DOCX, ODT, RTF, TXT<br/>" +
            "<b>Images:</b> JPG, PNG, GIF, BMP, TIFF<br/>" +
            "<b>Spreadsheets:</b> XLS, XLSX, ODS, CSV<br/>" +
            "<b>Presentations:</b> PPT, PPTX, ODP<br/>" +
            "<b>Archives:</b> ZIP, RAR, 7Z" +
            "</center></html>");
        infoLabel2.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel2.setForeground(new Color(70, 70, 70));
        infoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel3 = new JLabel("<html><center>" +
            "<b>Maximum file size:</b> 25 MB per document<br/>" +
            "<b>Document Categories:</b> Personal, Employment, ID, Educational, Financial, Medical" +
            "</center></html>");
        infoLabel3.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel3.setForeground(Color.GRAY);
        infoLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(infoLabel1);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(infoLabel2);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(infoLabel3);
        infoPanel.add(Box.createVerticalStrut(5));
        
        // Create a bottom panel to hold both buttons and info
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        
        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addDocument() {
        // Create enhanced file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Upload Employee Document");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        
        // Add multiple file filters for different document categories
        FileNameExtensionFilter allSupportedFilter = new FileNameExtensionFilter(
            "All Supported Documents", 
            "pdf", "doc", "docx", "odt", "rtf", "txt", 
            "jpg", "jpeg", "png", "gif", "bmp", "tiff", 
            "xls", "xlsx", "ods", "csv",
            "ppt", "pptx", "odp",
            "zip", "rar", "7z"
        );
        
        FileNameExtensionFilter documentFilter = new FileNameExtensionFilter(
            "Documents (PDF, DOC, DOCX, ODT, RTF, TXT)", 
            "pdf", "doc", "docx", "odt", "rtf", "txt"
        );
        
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
            "Images (JPG, PNG, GIF, BMP, TIFF)", 
            "jpg", "jpeg", "png", "gif", "bmp", "tiff"
        );
        
        FileNameExtensionFilter spreadsheetFilter = new FileNameExtensionFilter(
            "Spreadsheets (XLS, XLSX, ODS, CSV)", 
            "xls", "xlsx", "ods", "csv"
        );
        
        FileNameExtensionFilter presentationFilter = new FileNameExtensionFilter(
            "Presentations (PPT, PPTX, ODP)", 
            "ppt", "pptx", "odp"
        );
        
        FileNameExtensionFilter archiveFilter = new FileNameExtensionFilter(
            "Archives (ZIP, RAR, 7Z)", 
            "zip", "rar", "7z"
        );
        
        // Set the default filter and add all filters
        fileChooser.setFileFilter(allSupportedFilter);
        fileChooser.addChoosableFileFilter(documentFilter);
        fileChooser.addChoosableFileFilter(imageFilter);
        fileChooser.addChoosableFileFilter(spreadsheetFilter);
        fileChooser.addChoosableFileFilter(presentationFilter);
        fileChooser.addChoosableFileFilter(archiveFilter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Validate file extension
            String fileName = selectedFile.getName().toLowerCase();
            String[] supportedExtensions = {
                "pdf", "doc", "docx", "odt", "rtf", "txt",
                "jpg", "jpeg", "png", "gif", "bmp", "tiff",
                "xls", "xlsx", "ods", "csv",
                "ppt", "pptx", "odp",
                "zip", "rar", "7z"
            };
            
            boolean isSupported = false;
            for (String ext : supportedExtensions) {
                if (fileName.endsWith("." + ext)) {
                    isSupported = true;
                    break;
                }
            }
            
            if (!isSupported) {
                JOptionPane.showMessageDialog(this, 
                    "Unsupported file type. Please select a supported document format.",
                    "Invalid File Type", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check file size (25 MB limit - increased from 16 MB)
            long fileSizeInMB = selectedFile.length() / (1024 * 1024);
            if (selectedFile.length() > 25 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, 
                    "File size (" + fileSizeInMB + " MB) exceeds the 25 MB limit. Please select a smaller file.",
                    "File Too Large", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Enhanced document type selection with more categories
            String[] documentTypes = {
                "Personal Documents",
                "• Resume/CV",
                "• Cover Letter", 
                "• Portfolio",
                "Employment Documents",
                "• Employment Contract",
                "• Job Offer Letter",
                "• Non-Disclosure Agreement",
                "• Employee Handbook Acknowledgment",
                "Identification Documents", 
                "• Government ID",
                "• Driver's License",
                "• Passport",
                "• Social Security Card",
                "• Birth Certificate",
                "Educational Documents",
                "• Diploma/Degree",
                "• Transcript of Records",
                "• Professional Certificate",
                "• Training Certificate",
                "• License/Certification",
                "Financial Documents",
                "• Tax Documents",
                "• Bank Information",
                "• Salary Certificate",
                "Medical Documents",
                "• Medical Certificate",
                "• Health Insurance",
                "• Vaccination Records",
                "Other Documents",
                "• Reference Letter",
                "• Performance Review",
                "• Emergency Contact Form",
                "• Other"
            };
            
            String documentType = (String) JOptionPane.showInputDialog(
                this,
                "Select the document type:",
                "Document Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                documentTypes,
                documentTypes[1]); // Default to Resume/CV
            
            if (documentType != null && !documentType.startsWith("•") && !documentType.endsWith("Documents")) {
                // If a category header was selected, default to the first item in that category
                documentType = "Other";
            } else if (documentType != null && documentType.startsWith("• ")) {
                // Remove the bullet point prefix
                documentType = documentType.substring(2);
            }
            
            if (documentType != null) {
                // Enhanced description dialog with examples
                String description = JOptionPane.showInputDialog(
                    this,
                    "<html>Enter a description for this document (optional):<br/>" +
                    "<i>Examples: \"University Degree in Computer Science\", \"Updated Resume 2025\", " +
                    "\"Medical Clearance for Employment\"</i></html>",
                    "Document Description",
                    JOptionPane.QUESTION_MESSAGE);
                
                if (description == null) description = ""; // Handle cancel
                
                try {
                    // Read file data
                    byte[] fileData = Files.readAllBytes(selectedFile.toPath());
                    
                    // Determine MIME type based on file extension
                    String mimeType = getMimeType(fileName);
                    
                    // Create document object
                    EmployeeDocument document = new EmployeeDocument(
                        employee != null ? employee.getEmployeeId() : 0,
                        documentType,
                        selectedFile.getName(),
                        selectedFile.getAbsolutePath(),
                        fileData,
                        description
                    );
                    
                    // Set additional metadata
                    document.setMimeType(mimeType);
                    document.setUploadedBy("System User"); // In a real system, this would be the current user
                    
                    // Add to employee's document list
                    if (employee == null) {
                        // For new employees, we'll create a temporary employee object to hold documents
                        employee = new Employee(0, "", "", "", "", "", 0, LocalDate.now());
                        System.out.println("Created temporary employee object for document storage");
                    }
                    employee.addDocument(document);
                    System.out.println("Added document to employee: " + document.getFileName() + " (Employee has " + employee.getDocuments().size() + " documents)");
                    
                    // Update table
                    refreshDocumentsTable();
                    
                    JOptionPane.showMessageDialog(this, 
                        "<html>Document uploaded successfully!<br/>" +
                        "<b>File:</b> " + selectedFile.getName() + "<br/>" +
                        "<b>Type:</b> " + documentType + "<br/>" +
                        "<b>Size:</b> " + formatFileSize(selectedFile.length()) + "</html>",
                        "Upload Successful", JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error reading file: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
                } catch (OutOfMemoryError e) {
                    JOptionPane.showMessageDialog(this, 
                        "File is too large to process. Please select a smaller file.",
                        "Memory Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Get MIME type based on file extension
     */
    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "odt": return "application/vnd.oasis.opendocument.text";
            case "rtf": return "application/rtf";
            case "txt": return "text/plain";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            case "tiff": return "image/tiff";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ods": return "application/vnd.oasis.opendocument.spreadsheet";
            case "csv": return "text/csv";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "odp": return "application/vnd.oasis.opendocument.presentation";
            case "zip": return "application/zip";
            case "rar": return "application/x-rar-compressed";
            case "7z": return "application/x-7z-compressed";
            default: return "application/octet-stream";
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
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove the document: " + fileName + "?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (employee != null && employee.getDocuments() != null && 
                selectedRow < employee.getDocuments().size()) {
                
                // Get the document from the employee's list that matches the selected row
                EmployeeDocument document = employee.getDocuments().get(selectedRow);
                
                // Double-check that we have the right document by matching filename
                if (!fileName.equals(document.getFileName())) {
                    System.err.println("WARNING: Table row filename doesn't match document list filename!");
                    System.err.println("Table filename: " + fileName);
                    System.err.println("Document filename: " + document.getFileName());
                    
                    // Try to find the document by filename
                    EmployeeDocument foundDocument = null;
                    for (EmployeeDocument doc : employee.getDocuments()) {
                        if (fileName.equals(doc.getFileName())) {
                            foundDocument = doc;
                            break;
                        }
                    }
                    
                    if (foundDocument != null) {
                        document = foundDocument;
                        System.out.println("Found document by filename match: " + document.getFileName());
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Error: Could not find the selected document. Please try again.",
                            "Document Not Found", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                System.out.println("Removing document: " + document.getFileName() + " (ID: " + document.getDocumentId() + ")");
                
                // If document is already saved to database, delete it immediately
                if (document.getDocumentId() > 0) {
                    System.out.println("Deleting document from database: " + document.getFileName() + " (ID: " + document.getDocumentId() + ")");
                    boolean deleted = employeeManager.deleteEmployeeDocument(document.getDocumentId());
                    if (!deleted) {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to delete document from database. Please try again.",
                            "Delete Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    System.out.println("Document successfully deleted from database");
                }
                
                // Remove from employee's document list
                employee.removeDocument(document);
                refreshDocumentsTable();
                
                System.out.println("Document removed from employee's list. Remaining documents: " + employee.getDocuments().size());
                
                JOptionPane.showMessageDialog(this, 
                    "Document removed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
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
        
        if (employee != null && employee.getDocuments() != null && 
            selectedRow < employee.getDocuments().size()) {
            
            EmployeeDocument document = employee.getDocuments().get(selectedRow);
            
            // Create enhanced information dialog
            StringBuilder info = new StringBuilder();
            info.append("Document Information\n");
            info.append("===================\n\n");
            info.append("File Name: ").append(document.getFileName()).append("\n");
            info.append("Document Type: ").append(document.getDocumentType()).append("\n");
            info.append("File Size: ").append(formatFileSize(document.getFileSize())).append("\n");
            info.append("Upload Date: ").append(document.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            info.append("Uploaded By: ").append(document.getUploadedBy()).append("\n");
            
            if (document.getMimeType() != null) {
                info.append("File Type: ").append(document.getMimeType()).append("\n");
            }
            
            if (document.getDescription() != null && !document.getDescription().trim().isEmpty()) {
                info.append("Description: ").append(document.getDescription()).append("\n");
            }
            
            // Add file path information
            if (document.getFilePath() != null && !document.getFilePath().trim().isEmpty()) {
                info.append("Original Path: ").append(document.getFilePath()).append("\n");
            }
            
            // Add document category information
            String category = getDocumentCategory(document.getDocumentType());
            if (category != null) {
                info.append("Category: ").append(category).append("\n");
            }
            
            JTextArea textArea = new JTextArea(info.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 350));
            
            // Create a panel with additional options
            JPanel viewPanel = new JPanel(new BorderLayout());
            viewPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Add buttons for additional actions
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton exportButton = new JButton("Export File");
            JButton previewButton = new JButton("Preview");
            
            exportButton.addActionListener(e -> exportDocument(document));
            previewButton.addActionListener(e -> previewDocument(document));
            
            // Only enable preview for certain file types
            String fileName = document.getFileName().toLowerCase();
            boolean canPreview = fileName.endsWith(".txt") || fileName.endsWith(".csv") || 
                               fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                               fileName.endsWith(".png") || fileName.endsWith(".gif") || 
                               fileName.endsWith(".bmp");
            previewButton.setEnabled(canPreview);
            
            buttonPanel.add(exportButton);
            buttonPanel.add(previewButton);
            viewPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(this, viewPanel, 
                "Document Details - " + document.getFileName(), JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Get document category based on document type
     */
    private String getDocumentCategory(String documentType) {
        if (documentType == null) return null;
        
        String type = documentType.toLowerCase();
        if (type.contains("resume") || type.contains("cv") || type.contains("cover letter") || type.contains("portfolio")) {
            return "Personal Documents";
        } else if (type.contains("contract") || type.contains("offer") || type.contains("agreement") || type.contains("handbook")) {
            return "Employment Documents";
        } else if (type.contains("id") || type.contains("license") || type.contains("passport") || type.contains("birth certificate")) {
            return "Identification Documents";
        } else if (type.contains("diploma") || type.contains("degree") || type.contains("transcript") || type.contains("certificate") || type.contains("training")) {
            return "Educational Documents";
        } else if (type.contains("tax") || type.contains("bank") || type.contains("salary")) {
            return "Financial Documents";
        } else if (type.contains("medical") || type.contains("health") || type.contains("vaccination")) {
            return "Medical Documents";
        }
        return "Other Documents";
    }
    
    /**
     * Export document to file system
     */
    private void exportDocument(EmployeeDocument document) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Document");
        fileChooser.setSelectedFile(new File(document.getFileName()));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try {
                Files.write(saveFile.toPath(), document.getFileData());
                JOptionPane.showMessageDialog(this, 
                    "Document exported successfully to:\n" + saveFile.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting document: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Preview document content (for supported file types)
     */
    private void previewDocument(EmployeeDocument document) {
        String fileName = document.getFileName().toLowerCase();
        byte[] fileData = document.getFileData();
        
        if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
            // Preview text files
            try {
                String content = new String(fileData, "UTF-8");
                if (content.length() > 10000) {
                    content = content.substring(0, 10000) + "\n\n... (Content truncated - showing first 10,000 characters)";
                }
                
                JTextArea textArea = new JTextArea(content);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, 
                    "Preview - " + document.getFileName(), JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error previewing text file: " + e.getMessage(),
                    "Preview Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                   fileName.endsWith(".png") || fileName.endsWith(".gif") || 
                   fileName.endsWith(".bmp")) {
            // Preview image files
            try {
                ImageIcon imageIcon = new ImageIcon(fileData);
                
                // Scale image if too large
                if (imageIcon.getIconWidth() > 800 || imageIcon.getIconHeight() > 600) {
                    Image img = imageIcon.getImage();
                    Image scaledImg = img.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaledImg);
                }
                
                JLabel imageLabel = new JLabel(imageIcon);
                JScrollPane scrollPane = new JScrollPane(imageLabel);
                scrollPane.setPreferredSize(new Dimension(Math.min(imageIcon.getIconWidth() + 50, 850), 
                                                         Math.min(imageIcon.getIconHeight() + 50, 650)));
                
                JOptionPane.showMessageDialog(this, scrollPane, 
                    "Preview - " + document.getFileName(), JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error previewing image file: " + e.getMessage(),
                    "Preview Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshDocumentsTable() {
        documentsTableModel.setRowCount(0);
        
        if (employee != null && employee.getDocuments() != null) {
            System.out.println("Refreshing documents table with " + employee.getDocuments().size() + " documents");
            int rowIndex = 0;
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
                System.out.println("Added to table [" + rowIndex + "]: " + document.getFileName() + " (ID: " + document.getDocumentId() + ")");
                rowIndex++;
            }
        } else {
            System.out.println("No documents to refresh (employee or documents list is null)");
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
}
