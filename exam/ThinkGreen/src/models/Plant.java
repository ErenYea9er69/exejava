package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a plant tracked in a shared urban garden.
 * Inherits from Entity (OOP: inheritance).
 */
public class Plant extends Entity {

    private String name;
    private String species;
    private String location;
    private LocalDate plantedDate;
    private String status; // healthy, needs_water, sick, harvested
    private int userId;

    // ── Constructors ──────────────────────────────
    public Plant() {
        super();
        this.status = "healthy";
    }

    public Plant(int id, String name, String species, String location,
                 LocalDate plantedDate, String status, int userId, LocalDateTime createdAt) {
        super(id, createdAt);
        this.name = name;
        this.species = species;
        this.location = location;
        this.plantedDate = plantedDate;
        this.status = status;
        this.userId = userId;
    }

    public Plant(String name, String species, String location,
                 LocalDate plantedDate, String status, int userId) {
        super();
        this.name = name;
        this.species = species;
        this.location = location;
        this.plantedDate = plantedDate;
        this.status = status;
        this.userId = userId;
    }

    // ── Polymorphism ──────────────────────────────
    @Override
    public String getSummary() {
        return "Plant: " + name + " — " + status;
    }

    // ── Getters & Setters ─────────────────────────
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(LocalDate plantedDate) {
        this.plantedDate = plantedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
