package managers;

/**
 * Philippine Payroll Calculator utility class
 * Handles calculations specific to Philippine labor laws and regulations
 */
public class PhilippinePayrollCalculator {
    
    // Philippine minimum wage rates (as of 2024 - these should be updated regularly)
    public static final double NCR_MINIMUM_WAGE = 610.0; // Metro Manila daily minimum wage
    public static final double REGION_4A_MINIMUM_WAGE = 470.0; // Calabarzon daily minimum wage
    
    // Standard working hours
    public static final int STANDARD_WORKING_HOURS_PER_DAY = 8;
    public static final int STANDARD_WORKING_DAYS_PER_MONTH = 22;
    
    // Tax brackets (2024 TRAIN Law)
    public static final double TAX_EXEMPT_THRESHOLD = 250000.0; // Annual
    
    /**
     * Calculate overtime pay based on Philippine labor code
     * Regular overtime: 125% of hourly rate
     * Holiday overtime: varies by holiday type
     */
    public static double calculateOvertimePay(double basicSalary, double overtimeHours, boolean isHoliday, boolean isSpecialHoliday) {
        double hourlyRate = basicSalary / (STANDARD_WORKING_DAYS_PER_MONTH * STANDARD_WORKING_HOURS_PER_DAY);
        
        if (isHoliday) {
            if (isSpecialHoliday) {
                // Special holiday overtime: 130% of hourly rate
                return hourlyRate * 1.3 * overtimeHours;
            } else {
                // Regular holiday overtime: 200% of hourly rate
                return hourlyRate * 2.0 * overtimeHours;
            }
        } else {
            // Regular overtime: 125% of hourly rate
            return hourlyRate * 1.25 * overtimeHours;
        }
    }
    
    /**
     * Calculate holiday pay based on Philippine labor code
     */
    public static double calculateHolidayPay(double dailyRate, boolean isRegularHoliday, boolean workedOnHoliday) {
        if (isRegularHoliday) {
            if (workedOnHoliday) {
                // Worked on regular holiday: 200% of daily rate
                return dailyRate * 2.0;
            } else {
                // Regular holiday (no work): 100% of daily rate
                return dailyRate;
            }
        } else {
            // Special holiday (worked): 130% of daily rate
            if (workedOnHoliday) {
                return dailyRate * 1.3;
            }
        }
        return 0.0;
    }
    
    /**
     * Calculate night differential (minimum 10% of hourly rate)
     */
    public static double calculateNightDifferential(double basicSalary, double nightHours) {
        double hourlyRate = basicSalary / (STANDARD_WORKING_DAYS_PER_MONTH * STANDARD_WORKING_HOURS_PER_DAY);
        return hourlyRate * 0.10 * nightHours; // 10% minimum night differential
    }
    
    /**
     * Calculate SSS contribution based on 2024 rates
     */
    public static SSContribution calculateSSSContribution(double monthlySalary) {
        // SSS contribution table (simplified - should be updated with actual table)
        double totalContribution = 0.0;
        double employeeShare = 0.0;
        double employerShare = 0.0;
        
        if (monthlySalary <= 4000) {
            totalContribution = 180.0;
            employeeShare = 60.0;
            employerShare = 120.0;
        } else if (monthlySalary <= 4750) {
            totalContribution = 247.50;
            employeeShare = 82.50;
            employerShare = 165.0;
        } else if (monthlySalary <= 5500) {
            totalContribution = 315.0;
            employeeShare = 105.0;
            employerShare = 210.0;
        } else if (monthlySalary <= 6250) {
            totalContribution = 382.50;
            employeeShare = 127.50;
            employerShare = 255.0;
        } else if (monthlySalary <= 7000) {
            totalContribution = 450.0;
            employeeShare = 150.0;
            employerShare = 300.0;
        } else if (monthlySalary <= 7750) {
            totalContribution = 517.50;
            employeeShare = 172.50;
            employerShare = 345.0;
        } else if (monthlySalary <= 8500) {
            totalContribution = 585.0;
            employeeShare = 195.0;
            employerShare = 390.0;
        } else if (monthlySalary <= 9250) {
            totalContribution = 652.50;
            employeeShare = 217.50;
            employerShare = 435.0;
        } else if (monthlySalary <= 10000) {
            totalContribution = 720.0;
            employeeShare = 240.0;
            employerShare = 480.0;
        } else {
            // Maximum contribution for salaries above 30,000
            totalContribution = 1800.0;
            employeeShare = 600.0;
            employerShare = 1200.0;
        }
        
        return new SSContribution(totalContribution, employeeShare, employerShare);
    }
    
    /**
     * Calculate PhilHealth contribution based on 2024 rates
     */
    public static PhilHealthContribution calculatePhilHealthContribution(double monthlySalary) {
        // PhilHealth contribution: 5% of monthly salary (split equally)
        // Minimum: PHP 500, Maximum: PHP 5,000
        double contributionBase = Math.min(Math.max(monthlySalary, 10000), 100000);
        double totalContribution = contributionBase * 0.05;
        
        // Split equally between employer and employee
        double employeeShare = totalContribution / 2;
        double employerShare = totalContribution / 2;
        
        return new PhilHealthContribution(totalContribution, employeeShare, employerShare);
    }
    
    /**
     * Calculate Pag-IBIG contribution based on 2024 rates
     */
    public static PagIBIGContribution calculatePagIBIGContribution(double monthlySalary) {
        double employeeRate = (monthlySalary <= 1500) ? 0.01 : 0.02; // 1% or 2%
        double employeeShare = monthlySalary * employeeRate;
        
        // Employee share capped at PHP 100
        employeeShare = Math.min(employeeShare, 100.0);
        
        // Employer matches employee contribution
        double employerShare = employeeShare;
        double totalContribution = employeeShare + employerShare;
        
        return new PagIBIGContribution(totalContribution, employeeShare, employerShare);
    }
    
    /**
     * Calculate withholding tax based on TRAIN Law (2024)
     */
    public static double calculateWithholdingTax(double annualTaxableIncome) {
        if (annualTaxableIncome <= 250000) {
            return 0.0; // Tax exempt
        } else if (annualTaxableIncome <= 400000) {
            return (annualTaxableIncome - 250000) * 0.15; // 15%
        } else if (annualTaxableIncome <= 800000) {
            return 22500 + (annualTaxableIncome - 400000) * 0.20; // 20%
        } else if (annualTaxableIncome <= 2000000) {
            return 102500 + (annualTaxableIncome - 800000) * 0.25; // 25%
        } else if (annualTaxableIncome <= 8000000) {
            return 402500 + (annualTaxableIncome - 2000000) * 0.30; // 30%
        } else {
            return 2202500 + (annualTaxableIncome - 8000000) * 0.35; // 35%
        }
    }
    
    /**
     * Calculate monthly withholding tax
     */
    public static double calculateMonthlyWithholdingTax(double monthlyTaxableIncome) {
        double annualTaxableIncome = monthlyTaxableIncome * 12;
        double annualTax = calculateWithholdingTax(annualTaxableIncome);
        return annualTax / 12;
    }
    
    /**
     * Calculate 13th month pay
     */
    public static double calculate13thMonthPay(double totalBasicSalaryForYear) {
        return totalBasicSalaryForYear / 12;
    }
    
    /**
     * Calculate Service Incentive Leave monetization
     */
    public static double calculateSILMonetization(double dailyRate, int unusedLeaveDays) {
        return dailyRate * unusedLeaveDays;
    }
    
    /**
     * Check if salary meets minimum wage requirement
     */
    public static boolean meetsMinimumWage(double dailySalary, String region) {
        double minimumWage = NCR_MINIMUM_WAGE; // Default to NCR
        
        switch (region.toUpperCase()) {
            case "NCR":
            case "METRO MANILA":
                minimumWage = NCR_MINIMUM_WAGE;
                break;
            case "REGION IV-A":
            case "CALABARZON":
                minimumWage = REGION_4A_MINIMUM_WAGE;
                break;
            // Add more regions as needed
        }
        
        return dailySalary >= minimumWage;
    }
    
    // Helper classes for contribution calculations
    public static class SSContribution {
        public final double total;
        public final double employeeShare;
        public final double employerShare;
        
        public SSContribution(double total, double employeeShare, double employerShare) {
            this.total = total;
            this.employeeShare = employeeShare;
            this.employerShare = employerShare;
        }
    }
    
    public static class PhilHealthContribution {
        public final double total;
        public final double employeeShare;
        public final double employerShare;
        
        public PhilHealthContribution(double total, double employeeShare, double employerShare) {
            this.total = total;
            this.employeeShare = employeeShare;
            this.employerShare = employerShare;
        }
    }
    
    public static class PagIBIGContribution {
        public final double total;
        public final double employeeShare;
        public final double employerShare;
        
        public PagIBIGContribution(double total, double employeeShare, double employerShare) {
            this.total = total;
            this.employeeShare = employeeShare;
            this.employerShare = employerShare;
        }
    }
}
