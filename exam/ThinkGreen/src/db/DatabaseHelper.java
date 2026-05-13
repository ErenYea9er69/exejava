package db;

import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC database helper for ThinkGreen.
 * Handles all database operations: connection, CRUD for all entities.
 */
public class DatabaseHelper {

    // ── Connection Config ─────────────────────────
    private static final String URL  = "jdbc:mysql://localhost:3306/thinkgreen";
    private static final String USER = "root";
    private static final String PASS = "";

    // ══════════════════════════════════════════════
    //  CONNECTION
    // ══════════════════════════════════════════════

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Tests if the database connection is working.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("DB Connection failed: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  USER OPERATIONS
    // ══════════════════════════════════════════════

    /**
     * Authenticate a user by username and password.
     * Returns the User object if credentials are valid, null otherwise.
     */
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Auth error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Register a new user. Returns true if successful.
     */
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Register error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all users from the database.
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get users error: " + e.getMessage());
        }
        return users;
    }

    /**
     * Delete a user by ID.
     */
    public static boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete user error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  PLANT OPERATIONS
    // ══════════════════════════════════════════════

    /**
     * Get all plants.
     */
    public static List<Plant> getAllPlants() {
        List<Plant> plants = new ArrayList<>();
        String sql = "SELECT * FROM plants ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                plants.add(extractPlant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get plants error: " + e.getMessage());
        }
        return plants;
    }

    /**
     * Get plants belonging to a specific user.
     */
    public static List<Plant> getPlantsByUser(int userId) {
        List<Plant> plants = new ArrayList<>();
        String sql = "SELECT * FROM plants WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                plants.add(extractPlant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get plants by user error: " + e.getMessage());
        }
        return plants;
    }

    /**
     * Add a new plant.
     */
    public static boolean addPlant(Plant plant) {
        String sql = "INSERT INTO plants (name, species, location, planted_date, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plant.getName());
            stmt.setString(2, plant.getSpecies());
            stmt.setString(3, plant.getLocation());
            stmt.setDate(4, Date.valueOf(plant.getPlantedDate()));
            stmt.setString(5, plant.getStatus());
            stmt.setInt(6, plant.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add plant error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing plant.
     */
    public static boolean updatePlant(Plant plant) {
        String sql = "UPDATE plants SET name=?, species=?, location=?, planted_date=?, status=?, user_id=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plant.getName());
            stmt.setString(2, plant.getSpecies());
            stmt.setString(3, plant.getLocation());
            stmt.setDate(4, Date.valueOf(plant.getPlantedDate()));
            stmt.setString(5, plant.getStatus());
            stmt.setInt(6, plant.getUserId());
            stmt.setInt(7, plant.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update plant error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a plant by ID.
     */
    public static boolean deletePlant(int id) {
        String sql = "DELETE FROM plants WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete plant error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  WEATHER ALERT OPERATIONS
    // ══════════════════════════════════════════════

    /**
     * Get all weather alerts.
     */
    public static List<WeatherAlert> getAllAlerts() {
        List<WeatherAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM weather_alerts ORDER BY alert_date DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                alerts.add(extractAlert(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get alerts error: " + e.getMessage());
        }
        return alerts;
    }

    /**
     * Get recent alerts (last 5).
     */
    public static List<WeatherAlert> getRecentAlerts() {
        List<WeatherAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM weather_alerts ORDER BY alert_date DESC LIMIT 5";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                alerts.add(extractAlert(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get recent alerts error: " + e.getMessage());
        }
        return alerts;
    }

    /**
     * Add a new weather alert.
     */
    public static boolean addAlert(WeatherAlert alert) {
        String sql = "INSERT INTO weather_alerts (alert_type, severity, message, alert_date, region) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, alert.getAlertType());
            stmt.setString(2, alert.getSeverity());
            stmt.setString(3, alert.getMessage());
            stmt.setDate(4, Date.valueOf(alert.getAlertDate()));
            stmt.setString(5, alert.getRegion());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add alert error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a weather alert by ID.
     */
    public static boolean deleteAlert(int id) {
        String sql = "DELETE FROM weather_alerts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete alert error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  WATERING SCHEDULE OPERATIONS
    // ══════════════════════════════════════════════

    /**
     * Get all schedules with plant name (JOIN).
     */
    public static List<WateringSchedule> getAllSchedules() {
        List<WateringSchedule> schedules = new ArrayList<>();
        String sql = "SELECT ws.*, p.name AS plant_name FROM watering_schedule ws " +
                     "JOIN plants p ON ws.plant_id = p.id ORDER BY ws.scheduled_date ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                schedules.add(extractSchedule(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get schedules error: " + e.getMessage());
        }
        return schedules;
    }

    /**
     * Get schedules for a specific plant.
     */
    public static List<WateringSchedule> getSchedulesByPlant(int plantId) {
        List<WateringSchedule> schedules = new ArrayList<>();
        String sql = "SELECT ws.*, p.name AS plant_name FROM watering_schedule ws " +
                     "JOIN plants p ON ws.plant_id = p.id WHERE ws.plant_id = ? ORDER BY ws.scheduled_date ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, plantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                schedules.add(extractSchedule(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get schedules by plant error: " + e.getMessage());
        }
        return schedules;
    }

    /**
     * Add a new schedule entry.
     */
    public static boolean addSchedule(WateringSchedule schedule) {
        String sql = "INSERT INTO watering_schedule (plant_id, action, scheduled_date, completed, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, schedule.getPlantId());
            stmt.setString(2, schedule.getAction());
            stmt.setDate(3, Date.valueOf(schedule.getScheduledDate()));
            stmt.setBoolean(4, schedule.isCompleted());
            stmt.setString(5, schedule.getNotes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add schedule error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Toggle the completed status of a schedule.
     */
    public static boolean toggleScheduleComplete(int id) {
        String sql = "UPDATE watering_schedule SET completed = NOT completed WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Toggle schedule error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a schedule entry by ID.
     */
    public static boolean deleteSchedule(int id) {
        String sql = "DELETE FROM watering_schedule WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete schedule error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  DASHBOARD STATISTICS
    // ══════════════════════════════════════════════

    /**
     * Returns dashboard statistics as a Map:
     *  - totalPlants, healthyPlants, sickPlants, needsWaterPlants
     *  - totalUsers, totalAlerts, pendingSchedules
     */
    public static Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Total plants
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM plants");
            rs.next();
            stats.put("totalPlants", rs.getInt(1));

            // Healthy plants
            rs = stmt.executeQuery("SELECT COUNT(*) FROM plants WHERE status = 'healthy'");
            rs.next();
            stats.put("healthyPlants", rs.getInt(1));

            // Sick plants
            rs = stmt.executeQuery("SELECT COUNT(*) FROM plants WHERE status = 'sick'");
            rs.next();
            stats.put("sickPlants", rs.getInt(1));

            // Needs water
            rs = stmt.executeQuery("SELECT COUNT(*) FROM plants WHERE status = 'needs_water'");
            rs.next();
            stats.put("needsWaterPlants", rs.getInt(1));

            // Total users
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            stats.put("totalUsers", rs.getInt(1));

            // Total alerts
            rs = stmt.executeQuery("SELECT COUNT(*) FROM weather_alerts");
            rs.next();
            stats.put("totalAlerts", rs.getInt(1));

            // Pending schedules (not completed)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM watering_schedule WHERE completed = FALSE");
            rs.next();
            stats.put("pendingSchedules", rs.getInt(1));

        } catch (SQLException e) {
            System.err.println("Dashboard stats error: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Returns plant count grouped by status (for PieChart).
     */
    public static Map<String, Integer> getPlantStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as cnt FROM plants GROUP BY status";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.err.println("Plant status counts error: " + e.getMessage());
        }
        return counts;
    }

    /**
     * Returns plant count grouped by user (for BarChart).
     */
    public static Map<String, Integer> getPlantsPerUser() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT u.username, COUNT(p.id) as cnt FROM users u " +
                     "LEFT JOIN plants p ON u.id = p.user_id GROUP BY u.username";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                counts.put(rs.getString("username"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.err.println("Plants per user error: " + e.getMessage());
        }
        return counts;
    }

    // ══════════════════════════════════════════════
    //  HELPER: ResultSet → Model extraction
    // ══════════════════════════════════════════════

    private static User extractUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private static Plant extractPlant(ResultSet rs) throws SQLException {
        return new Plant(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("species"),
            rs.getString("location"),
            rs.getDate("planted_date").toLocalDate(),
            rs.getString("status"),
            rs.getInt("user_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private static WeatherAlert extractAlert(ResultSet rs) throws SQLException {
        return new WeatherAlert(
            rs.getInt("id"),
            rs.getString("alert_type"),
            rs.getString("severity"),
            rs.getString("message"),
            rs.getDate("alert_date").toLocalDate(),
            rs.getString("region"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private static WateringSchedule extractSchedule(ResultSet rs) throws SQLException {
        return new WateringSchedule(
            rs.getInt("id"),
            rs.getInt("plant_id"),
            rs.getString("plant_name"),
            rs.getString("action"),
            rs.getDate("scheduled_date").toLocalDate(),
            rs.getBoolean("completed"),
            rs.getString("notes"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
