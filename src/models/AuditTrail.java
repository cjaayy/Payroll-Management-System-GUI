package models;

import java.time.LocalDateTime;

/**
 * Audit Trail model to track all user actions for security and compliance
 */
public class AuditTrail {
    private int auditId;
    private String username;
    private String action;
    private String tableName;
    private String recordId;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private boolean isSuccess;
    private String errorMessage;
    
    // Constructors
    public AuditTrail() {
        this.timestamp = LocalDateTime.now();
        this.isSuccess = true;
    }
    
    public AuditTrail(String username, String action, String tableName, String recordId) {
        this();
        this.username = username;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
    }
    
    // Getters and Setters
    public int getAuditId() {
        return auditId;
    }
    
    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getRecordId() {
        return recordId;
    }
    
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    public String getOldValues() {
        return oldValues;
    }
    
    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }
    
    public String getNewValues() {
        return newValues;
    }
    
    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return isSuccess;
    }
    
    public void setSuccess(boolean success) {
        isSuccess = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s %s %s at %s", 
                           username, action, tableName, recordId, timestamp);
    }
}
