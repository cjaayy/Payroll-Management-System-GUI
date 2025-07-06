package managers;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

/**
 * Address Manager for handling country, province/state, city, and barangay data
 */
public class AddressManager {
    
    /**
     * Get all countries
     */
    public static List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        String query = "SELECT country_name FROM countries WHERE is_active = TRUE ORDER BY country_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                countries.add(rs.getString("country_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching countries: " + e.getMessage());
            // Fallback data
            countries.addAll(Arrays.asList(
                "Philippines", "United States", "Canada", "Australia", 
                "United Kingdom", "Singapore", "Malaysia", "Thailand"
            ));
        }
        
        return countries;
    }
    
    /**
     * Get provinces/states for a specific country
     */
    public static List<String> getProvincesStates(String countryName) {
        List<String> provincesStates = new ArrayList<>();
        String query = "SELECT ps.province_state_name " +
                      "FROM provinces_states ps " +
                      "JOIN countries c ON ps.country_code = c.country_code " +
                      "WHERE c.country_name = ? AND ps.is_active = TRUE " +
                      "ORDER BY ps.province_state_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, countryName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                provincesStates.add(rs.getString("province_state_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching provinces/states: " + e.getMessage());
            // Fallback data for Philippines
            if ("Philippines".equals(countryName)) {
                provincesStates.addAll(Arrays.asList(
                    "National Capital Region (Metro Manila)", "Central Luzon", "CALABARZON",
                    "Central Visayas", "Western Visayas", "Davao Region"
                ));
            } else if ("United States".equals(countryName)) {
                provincesStates.addAll(Arrays.asList(
                    "California", "New York", "Texas", "Florida", "Illinois"
                ));
            }
        }
        
        return provincesStates;
    }
    
    /**
     * Get cities for a specific province/state
     */
    public static List<String> getCities(String countryName, String provinceStateName) {
        List<String> cities = new ArrayList<>();
        String query = "SELECT ct.city_name " +
                      "FROM cities ct " +
                      "JOIN countries c ON ct.country_code = c.country_code " +
                      "JOIN provinces_states ps ON ct.province_state_code = ps.province_state_code " +
                      "WHERE c.country_name = ? AND ps.province_state_name = ? AND ct.is_active = TRUE " +
                      "ORDER BY ct.city_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, countryName);
            stmt.setString(2, provinceStateName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                cities.add(rs.getString("city_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cities: " + e.getMessage());
            // Fallback data
            if ("Philippines".equals(countryName) && "National Capital Region (Metro Manila)".equals(provinceStateName)) {
                cities.addAll(Arrays.asList(
                    "Manila", "Quezon City", "Makati", "Pasig", "Taguig", 
                    "Mandaluyong", "Marikina", "Caloocan", "Las Piñas", "Muntinlupa"
                ));
            }
        }
        
        return cities;
    }
    
    /**
     * Get barangays for a specific city
     */
    public static List<String> getBarangays(String countryName, String provinceStateName, String cityName) {
        List<String> barangays = new ArrayList<>();
        String query = "SELECT b.barangay_name " +
                      "FROM barangays b " +
                      "JOIN countries c ON b.country_code = c.country_code " +
                      "JOIN provinces_states ps ON b.province_state_code = ps.province_state_code " +
                      "JOIN cities ct ON b.city_code = ct.city_code " +
                      "WHERE c.country_name = ? AND ps.province_state_name = ? AND ct.city_name = ? AND b.is_active = TRUE " +
                      "ORDER BY b.barangay_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, countryName);
            stmt.setString(2, provinceStateName);
            stmt.setString(3, cityName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                barangays.add(rs.getString("barangay_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching barangays: " + e.getMessage());
            // Fallback data
            if ("Makati".equals(cityName)) {
                barangays.addAll(Arrays.asList(
                    "Bel-Air", "Forbes Park", "Legaspi Village", "Salcedo Village", 
                    "San Lorenzo", "Poblacion", "Magallanes", "Rockwell"
                ));
            } else if ("Quezon City".equals(cityName)) {
                barangays.addAll(Arrays.asList(
                    "Bagong Pag-asa", "Batasan Hills", "Commonwealth", "Cubao", 
                    "Diliman", "Fairview", "Libis", "Novaliches", "UP Campus"
                ));
            }
        }
        
        return barangays;
    }
    
    /**
     * Get country code from country name
     */
    public static String getCountryCode(String countryName) {
        String query = "SELECT country_code FROM countries WHERE country_name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, countryName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("country_code");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching country code: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get country statistics for debugging
     */
    public static void printAddressStatistics() {
        String query = "SELECT " +
                      "(SELECT COUNT(*) FROM countries WHERE is_active = TRUE) as countries, " +
                      "(SELECT COUNT(*) FROM provinces_states WHERE is_active = TRUE) as provinces, " +
                      "(SELECT COUNT(*) FROM cities WHERE is_active = TRUE) as cities, " +
                      "(SELECT COUNT(*) FROM barangays WHERE is_active = TRUE) as barangays";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                System.out.println("=== Address Database Statistics ===");
                System.out.println("Countries: " + rs.getInt("countries"));
                System.out.println("Provinces/States: " + rs.getInt("provinces"));
                System.out.println("Cities: " + rs.getInt("cities"));
                System.out.println("Barangays: " + rs.getInt("barangays"));
                System.out.println("====================================");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching address statistics: " + e.getMessage());
        }
    }
    
    /**
     * Test address loading for a specific country
     */
    public static void testAddressLoading(String countryName) {
        System.out.println("Testing address loading for: " + countryName);
        
        List<String> provinces = getProvincesStates(countryName);
        System.out.println("Provinces/States found: " + provinces.size());
        
        if (!provinces.isEmpty()) {
            String firstProvince = provinces.get(0);
            System.out.println("Testing province: " + firstProvince);
            
            List<String> cities = getCities(countryName, firstProvince);
            System.out.println("Cities found: " + cities.size());
            
            if (!cities.isEmpty()) {
                String firstCity = cities.get(0);
                System.out.println("Testing city: " + firstCity);
                
                List<String> barangays = getBarangays(countryName, firstProvince, firstCity);
                System.out.println("Barangays found: " + barangays.size());
            }
        }
    }
}
