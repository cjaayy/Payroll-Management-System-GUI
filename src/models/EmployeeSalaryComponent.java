package models;

import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Represents an employee's specific salary component assignment
 */
public class EmployeeSalaryComponent {
    private int id;
    private String employeeId;
    private int salaryComponentId;
    private SalaryComponent salaryComponent;
    private BigDecimal customAmount;
    private boolean isPercentage;
    private boolean isActive;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private LocalDate createdDate;
    private String createdBy;
    private String remarks;
    
    // Constructors
    public EmployeeSalaryComponent() {
        this.createdDate = LocalDate.now();
        this.isActive = true;
        this.isPercentage = false;
        this.customAmount = BigDecimal.ZERO;
    }
    
    public EmployeeSalaryComponent(String employeeId, int salaryComponentId, BigDecimal customAmount) {
        this();
        this.employeeId = employeeId;
        this.salaryComponentId = salaryComponentId;
        this.customAmount = customAmount;
        this.effectiveDate = LocalDate.now();
    }
    
    public EmployeeSalaryComponent(int id, String employeeId, int salaryComponentId, 
                                  BigDecimal customAmount, boolean isPercentage, 
                                  boolean isActive, LocalDate effectiveDate) {
        this(employeeId, salaryComponentId, customAmount);
        this.id = id;
        this.isPercentage = isPercentage;
        this.isActive = isActive;
        this.effectiveDate = effectiveDate;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public int getSalaryComponentId() {
        return salaryComponentId;
    }
    
    public void setSalaryComponentId(int salaryComponentId) {
        this.salaryComponentId = salaryComponentId;
    }
    
    public SalaryComponent getSalaryComponent() {
        return salaryComponent;
    }
    
    public void setSalaryComponent(SalaryComponent salaryComponent) {
        this.salaryComponent = salaryComponent;
        if (salaryComponent != null) {
            this.salaryComponentId = salaryComponent.getId();
        }
    }
    
    public BigDecimal getCustomAmount() {
        return customAmount;
    }
    
    public void setCustomAmount(BigDecimal customAmount) {
        this.customAmount = customAmount;
    }
    
    public boolean isPercentage() {
        return isPercentage;
    }
    
    public void setPercentage(boolean percentage) {
        isPercentage = percentage;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    // Utility methods
    public String getFormattedAmount() {
        if (isPercentage) {
            return String.format("%.2f%%", customAmount.doubleValue());
        } else {
            return String.format("₱%.2f", customAmount.doubleValue());
        }
    }
    
    public boolean isCurrentlyActive() {
        if (!isActive) return false;
        
        LocalDate today = LocalDate.now();
        if (effectiveDate != null && effectiveDate.isAfter(today)) {
            return false;
        }
        
        if (endDate != null && endDate.isBefore(today)) {
            return false;
        }
        
        return true;
    }
    
    public boolean isCurrentlyEffective() {
        return isCurrentlyActive();
    }
    
    public String getComponentName() {
        return salaryComponent != null ? salaryComponent.getName() : "Unknown Component";
    }
    
    public String getComponentType() {
        return salaryComponent != null ? salaryComponent.getType() : "Unknown Type";
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s", getComponentName(), getFormattedAmount());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        EmployeeSalaryComponent that = (EmployeeSalaryComponent) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
