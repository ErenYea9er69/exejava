package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a meteorological alert for garden regions.
 * Inherits from Entity (OOP: inheritance).
 */
public class WeatherAlert extends Entity {

    private String alertType;  // frost, heatwave, storm, drought
    private String severity;   // low, medium, high, critical
    private String message;
    private LocalDate alertDate;
    private String region;

    // ── Constructors ──────────────────────────────
    public WeatherAlert() {
        super();
    }

    public WeatherAlert(int id, String alertType, String severity, String message,
                        LocalDate alertDate, String region, LocalDateTime createdAt) {
        super(id, createdAt);
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.alertDate = alertDate;
        this.region = region;
    }

    public WeatherAlert(String alertType, String severity, String message,
                        LocalDate alertDate, String region) {
        super();
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.alertDate = alertDate;
        this.region = region;
    }

    // ── Polymorphism ──────────────────────────────
    @Override
    public String getSummary() {
        return "⚠ " + alertType + " (" + severity + ") — " + region;
    }

    // ── Getters & Setters ─────────────────────────
    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(LocalDate alertDate) {
        this.alertDate = alertDate;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
