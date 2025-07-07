package managers;

import models.AuditTrail;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Audit Trail Manager for logging all user actions
 */
public class AuditTrailManager {
    
    public AuditTrailManager() {
        // Constructor - no database dependency needed as we use DatabaseConnection directly
    }
    
    /**
     * Logs an action to the audit trail
     */
    public void logAction(String username, String action, String tableName, String recordId, String details) {
        AuditTrail audit = new AuditTrail(username, action, tableName, recordId);
        audit.setNewValues(details);
        audit.setSuccess(true);
        insertAuditTrail(audit);
    }
    
    /**
     * Logs a failed action to the audit trail
     */
    public void logFailedAction(String username, String action, String tableName, String recordId, String errorMessage) {
        AuditTrail audit = new AuditTrail(username, action, tableName, recordId);
        audit.setErrorMessage(errorMessage);
        audit.setSuccess(false);
        insertAuditTrail(audit);
    }
    
    /**
     * Inserts audit trail record
     */
    private void insertAuditTrail(AuditTrail audit) {
        String sql = "INSERT INTO audit_trail (username, action, table_name, record_id, old_values, " +
                    "new_values, ip_address, user_agent, timestamp, is_success, error_message) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, audit.getUsername());
            pstmt.setString(2, audit.getAction());
            pstmt.setString(3, audit.getTableName());
            pstmt.setString(4, audit.getRecordId());
            pstmt.setString(5, audit.getOldValues());
            pstmt.setString(6, audit.getNewValues());
            pstmt.setString(7, audit.getIpAddress());
            pstmt.setString(8, audit.getUserAgent());
            pstmt.setTimestamp(9, Timestamp.valueOf(audit.getTimestamp()));
            pstmt.setBoolean(10, audit.isSuccess());
            pstmt.setString(11, audit.getErrorMessage());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            // Log to console if database logging fails
            System.err.println("Failed to log audit trail: " + e.getMessage());
            System.err.println("Audit details: " + audit.toString());
        }
    }
    
    /**
     * Gets audit trail records for a specific user
     */
    public List<AuditTrail> getAuditTrailByUser(String username, int limit) {
        List<AuditTrail> auditTrails = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE username = ? ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setInt(2, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return auditTrails;
    }
    
    /**
     * Gets recent audit trail records
     */
    public List<AuditTrail> getRecentAuditTrail(int limit) {
        List<AuditTrail> auditTrails = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return auditTrails;
    }
    
    /**
     * Gets audit trail records for a specific action
     */
    public List<AuditTrail> getAuditTrailByAction(String action, int limit) {
        List<AuditTrail> auditTrails = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE action = ? ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, action);
            pstmt.setInt(2, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return auditTrails;
    }
    
    /**
     * Gets audit trail records for a specific user and action
     */
    public List<AuditTrail> getAuditTrailByUserAndAction(String username, String action, int limit) {
        List<AuditTrail> auditTrails = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE username = ? AND action = ? ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, action);
            pstmt.setInt(3, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return auditTrails;
    }
    
    /**
     * Gets failed login attempts for security monitoring
     */
    public List<AuditTrail> getFailedLoginAttempts(int hours) {
        List<AuditTrail> auditTrails = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE action = 'LOGIN_FAILED' AND timestamp > ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusHours(hours)));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    auditTrails.add(mapResultSetToAuditTrail(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return auditTrails;
    }
    
    private AuditTrail mapResultSetToAuditTrail(ResultSet rs) throws SQLException {
        AuditTrail audit = new AuditTrail();
        audit.setAuditId(rs.getInt("audit_id"));
        audit.setUsername(rs.getString("username"));
        audit.setAction(rs.getString("action"));
        audit.setTableName(rs.getString("table_name"));
        audit.setRecordId(rs.getString("record_id"));
        audit.setOldValues(rs.getString("old_values"));
        audit.setNewValues(rs.getString("new_values"));
        audit.setIpAddress(rs.getString("ip_address"));
        audit.setUserAgent(rs.getString("user_agent"));
        
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            audit.setTimestamp(timestamp.toLocalDateTime());
        }
        
        audit.setSuccess(rs.getBoolean("is_success"));
        audit.setErrorMessage(rs.getString("error_message"));
        
        return audit;
    }
}
