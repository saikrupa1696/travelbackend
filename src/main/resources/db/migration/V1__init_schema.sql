-- TravelMate AI Database Schema Migration V1
-- Creates normalized relational tables with constraints and indexes

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    destination VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    duration_days INT NOT NULL,
    travel_type VARCHAR(50) NOT NULL,
    traveller_count INT NOT NULL DEFAULT 1,
    budget_amount DECIMAL(12,2) NOT NULL,
    budget_mode VARCHAR(20) NOT NULL DEFAULT 'TOTAL',
    interests TEXT,
    travel_pace VARCHAR(50) DEFAULT 'MODERATE',
    accommodation_pref VARCHAR(100) DEFAULT 'STANDARD',
    food_pref VARCHAR(100) DEFAULT 'ANY',
    additional_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_trips_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_trips_user_id (user_id),
    INDEX idx_trips_destination (destination),
    INDEX idx_trips_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS itineraries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    generation_source VARCHAR(50) NOT NULL DEFAULT 'RULE_BASED',
    total_estimated_cost DECIMAL(12,2) DEFAULT 0.00,
    remaining_budget DECIMAL(12,2) DEFAULT 0.00,
    budget_status VARCHAR(50) DEFAULT 'WITHIN_BUDGET',
    itinerary_data LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_itineraries_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    INDEX idx_itineraries_trip_id (trip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS itinerary_days (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    itinerary_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    date DATE,
    theme VARCHAR(255),
    daily_budget DECIMAL(10,2) DEFAULT 0.00,
    daily_estimated_cost DECIMAL(10,2) DEFAULT 0.00,
    CONSTRAINT fk_days_itinerary FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE,
    INDEX idx_days_itinerary_id (itinerary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS itinerary_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    itinerary_day_id BIGINT NOT NULL,
    place_name VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    description TEXT,
    start_time VARCHAR(20),
    end_time VARCHAR(20),
    duration_minutes INT DEFAULT 60,
    estimated_cost DECIMAL(10,2) DEFAULT 0.00,
    cost_type VARCHAR(50) DEFAULT 'ESTIMATED',
    place_reference VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    sequence_number INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_activities_day FOREIGN KEY (itinerary_day_id) REFERENCES itinerary_days(id) ON DELETE CASCADE,
    INDEX idx_activities_day_id (itinerary_day_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS shared_trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    share_token VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shared_trips_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    INDEX idx_shared_trips_token (share_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
