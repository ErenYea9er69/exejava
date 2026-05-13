package models;

import java.time.LocalDateTime;

/**
 * Abstract base class for all ThinkGreen entities.
 * Demonstrates OOP: abstraction + encapsulation.
 */
public abstract class Entity {

    private int id;
    private LocalDateTime createdAt;

    // ── Constructors ──────────────────────────────
    public Entity() {
        this.createdAt = LocalDateTime.now();
    }

    public Entity(int id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    // ── Abstract method (polymorphism) ────────────
    /**
     * Returns a human-readable summary of this entity.
     * Each subclass provides its own implementation.
     */
    public abstract String getSummary();

    // ── Getters & Setters (encapsulation) ─────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
