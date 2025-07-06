package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Employee Document class for storing employee documents
 */
public class EmployeeDocument {
    private int documentId;
    private int employeeId;
    private String documentType;
    private String fileName;
    private String filePath;
    private byte[] fileData;
    private String description;
    private LocalDateTime uploadDate;
    private String uploadedBy;
    private long fileSize;
    private String mimeType;
    
    public EmployeeDocument() {
        this.uploadDate = LocalDateTime.now();
        this.uploadedBy = "System";
    }
    
    public EmployeeDocument(int employeeId, String documentType, String fileName, 
                           String filePath, byte[] fileData, String description) {
        this();
        this.employeeId = employeeId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileData = fileData;
        this.description = description;
        this.fileSize = fileData != null ? fileData.length : 0;
        this.mimeType = determineMimeType(fileName);
    }
    
    public EmployeeDocument(int documentId, int employeeId, String documentType, 
                           String fileName, String filePath, byte[] fileData, 
                           String description, LocalDateTime uploadDate, String uploadedBy) {
        this.documentId = documentId;
        this.employeeId = employeeId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileData = fileData;
        this.description = description;
        this.uploadDate = uploadDate;
        this.uploadedBy = uploadedBy;
        this.fileSize = fileData != null ? fileData.length : 0;
        this.mimeType = determineMimeType(fileName);
    }
    
    // Getters and Setters
    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { 
        this.fileName = fileName;
        this.mimeType = determineMimeType(fileName);
    }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { 
        this.fileData = fileData;
        this.fileSize = fileData != null ? fileData.length : 0;
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    // Utility methods
    private String determineMimeType(String fileName) {
        if (fileName == null) return "application/octet-stream";
        
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt": return "text/plain";
            case "jpg": 
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default: return "application/octet-stream";
        }
    }
    
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
    
    public String getFormattedUploadDate() {
        return uploadDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public boolean isImageFile() {
        return mimeType.startsWith("image/");
    }
    
    public boolean isPdfFile() {
        return "application/pdf".equals(mimeType);
    }
    
    public boolean isWordFile() {
        return mimeType.contains("word") || mimeType.contains("document");
    }
    
    public boolean isExcelFile() {
        return mimeType.contains("excel") || mimeType.contains("spreadsheet");
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s) - %s", 
                documentType, fileName, getFormattedFileSize(), getFormattedUploadDate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmployeeDocument that = (EmployeeDocument) obj;
        return documentId == that.documentId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(documentId);
    }
}
