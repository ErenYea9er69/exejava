-- ============================================
-- ThinkGreen - Jardinage Urbain Collaboratif
-- MySQL Database Schema + Seed Data
-- Run this in phpMyAdmin (XAMPP)
-- ============================================

CREATE DATABASE IF NOT EXISTS thinkgreen;
USE thinkgreen;

-- ============================================
-- Table: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- Table: plants
-- ============================================
CREATE TABLE IF NOT EXISTS plants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    planted_date DATE NOT NULL,
    status ENUM('healthy', 'needs_water', 'sick', 'harvested') NOT NULL DEFAULT 'healthy',
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- Table: weather_alerts
-- ============================================
CREATE TABLE IF NOT EXISTS weather_alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alert_type ENUM('frost', 'heatwave', 'storm', 'drought') NOT NULL,
    severity ENUM('low', 'medium', 'high', 'critical') NOT NULL,
    message TEXT NOT NULL,
    alert_date DATE NOT NULL,
    region VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- Table: watering_schedule
-- ============================================
CREATE TABLE IF NOT EXISTS watering_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plant_id INT NOT NULL,
    action ENUM('water', 'fertilize', 'prune') NOT NULL,
    scheduled_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- SEED DATA
-- ============================================

-- Users (passwords are plain text for demo — in production, hash them)
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@thinkgreen.com', 'admin123', 'admin'),
('fatima', 'fatima@email.com', 'fatima123', 'user'),
('youssef', 'youssef@email.com', 'youssef123', 'user');

-- Plants
INSERT INTO plants (name, species, location, planted_date, status, user_id) VALUES
('Tomate Cerise', 'Solanum lycopersicum', 'Jardin Nord', '2026-03-15', 'healthy', 2),
('Basilic', 'Ocimum basilicum', 'Jardin Nord', '2026-04-01', 'healthy', 2),
('Menthe', 'Mentha spicata', 'Jardin Sud', '2026-03-20', 'needs_water', 3),
('Rose', 'Rosa gallica', 'Jardin Est', '2026-02-10', 'healthy', 3),
('Lavande', 'Lavandula angustifolia', 'Jardin Sud', '2026-01-15', 'healthy', 2),
('Courgette', 'Cucurbita pepo', 'Jardin Nord', '2026-04-10', 'sick', 3),
('Persil', 'Petroselinum crispum', 'Jardin Est', '2026-03-25', 'harvested', 2),
('Fraisier', 'Fragaria × ananassa', 'Jardin Sud', '2026-02-28', 'healthy', 3);

-- Weather Alerts
INSERT INTO weather_alerts (alert_type, severity, message, alert_date, region) VALUES
('frost', 'high', 'Gel attendu cette nuit. Protégez vos plantes sensibles.', '2026-05-14', 'Casablanca'),
('heatwave', 'critical', 'Canicule prévue 3 jours. Arrosage renforcé recommandé.', '2026-05-15', 'Marrakech'),
('storm', 'medium', 'Orages possibles en soirée. Rentrez les pots.', '2026-05-13', 'Rabat'),
('drought', 'low', 'Période sèche prolongée. Surveillez l humidité du sol.', '2026-05-16', 'Fès'),
('heatwave', 'high', 'Températures élevées prévues. Ombrez les jeunes pousses.', '2026-05-17', 'Casablanca');

-- Watering Schedule
INSERT INTO watering_schedule (plant_id, action, scheduled_date, completed, notes) VALUES
(1, 'water', '2026-05-13', FALSE, 'Arrosage matin et soir'),
(2, 'fertilize', '2026-05-14', FALSE, 'Engrais bio liquide'),
(3, 'water', '2026-05-13', TRUE, 'Fait ce matin'),
(4, 'prune', '2026-05-15', FALSE, 'Tailler les branches mortes'),
(5, 'water', '2026-05-14', FALSE, 'Arrosage léger'),
(6, 'fertilize', '2026-05-16', FALSE, 'Traitement anti-parasitaire');
