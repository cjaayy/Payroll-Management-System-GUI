package models;

import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Represents a salary component (allowance, deduction, bonus, etc.)
 */
public class SalaryComponent {
    private int id;
    private String name;
    private String description;
    private String type; // EARNING, DEDUCTION, ALLOWANCE, BONUS
    private BigDecimal amount;
    private boolean isPercentage;
    private boolean isActive;
    private LocalDate createdDate;
    private LocalDate lastModified;
    private String createdBy;
    private String modifiedBy;
    
    // Constructors
    public SalaryComponent() {
        this.createdDate = LocalDate.now();
        this.lastModified = LocalDate.now();
        this.isActive = true;
        this.isPercentage = false;
        this.amount = BigDecimal.ZERO;
    }
    
    public SalaryComponent(String name, String type, BigDecimal amount, String description) {
        this();
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }
    
    public SalaryComponent(int id, String name, String type, BigDecimal amount, String description, 
                          boolean isPercentage, boolean isActive) {
        this(name, type, amount, description);
        this.id = id;
        this.isPercentage = isPercentage;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDate getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDate lastModified) {
        this.lastModified = lastModified;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getModifiedBy() {
        return modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    
    // Utility methods
    public String getFormattedAmount() {
        if (isPercentage) {
            return String.format("%.2f%%", amount.doubleValue());
        } else {
            return String.format("₱%.2f", amount.doubleValue());
        }
    }
    
    public boolean isEarning() {
        return "EARNING".equals(type) || "ALLOWANCE".equals(type) || "BONUS".equals(type);
    }
    
    public boolean isDeduction() {
        return "DEDUCTION".equals(type);
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SalaryComponent that = (SalaryComponent) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
