package models;

import java.time.LocalDateTime;

/**
 * Represents a user of the ThinkGreen platform.
 * Inherits from Entity (OOP: inheritance).
 */
public class User extends Entity {

    private String username;
    private String email;
    private String password;
    private String role; // "user" or "admin"

    // ── Constructors ──────────────────────────────
    public User() {
        super();
        this.role = "user";
    }

    public User(int id, String username, String email, String password, String role, LocalDateTime createdAt) {
        super(id, createdAt);
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "user";
    }

    // ── Polymorphism ──────────────────────────────
    @Override
    public String getSummary() {
        return "User: " + username + " (" + role + ")";
    }

    // ── Getters & Setters ─────────────────────────
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "admin".equals(this.role);
    }
}
