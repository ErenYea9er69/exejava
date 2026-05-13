package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a watering/fertilization/pruning schedule entry.
 * Inherits from Entity (OOP: inheritance).
 */
public class WateringSchedule extends Entity {

    private int plantId;
    private String plantName; // joined from plants table (display only)
    private String action;    // water, fertilize, prune
    private LocalDate scheduledDate;
    private boolean completed;
    private String notes;

    // ── Constructors ──────────────────────────────
    public WateringSchedule() {
        super();
        this.completed = false;
    }

    public WateringSchedule(int id, int plantId, String plantName, String action,
                            LocalDate scheduledDate, boolean completed, String notes,
                            LocalDateTime createdAt) {
        super(id, createdAt);
        this.plantId = plantId;
        this.plantName = plantName;
        this.action = action;
        this.scheduledDate = scheduledDate;
        this.completed = completed;
        this.notes = notes;
    }

    public WateringSchedule(int plantId, String action, LocalDate scheduledDate, String notes) {
        super();
        this.plantId = plantId;
        this.action = action;
        this.scheduledDate = scheduledDate;
        this.completed = false;
        this.notes = notes;
    }

    // ── Polymorphism ──────────────────────────────
    @Override
    public String getSummary() {
        return "Schedule: " + action + " on " + scheduledDate;
    }

    // ── Getters & Setters ─────────────────────────
    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
