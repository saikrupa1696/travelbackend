-- Migration V2: Add real location coordinates to trips and rich place photo/details to itinerary_activities
-- Safe backward-compatible schema expansion preserving all existing records

ALTER TABLE trips
    ADD COLUMN destination_latitude DOUBLE NULL AFTER destination,
    ADD COLUMN destination_longitude DOUBLE NULL AFTER destination_latitude,
    ADD COLUMN destination_place_id VARCHAR(255) NULL AFTER destination_longitude;

ALTER TABLE itinerary_activities
    ADD COLUMN photo_url VARCHAR(500) NULL AFTER place_reference,
    ADD COLUMN address VARCHAR(255) NULL AFTER photo_url,
    ADD COLUMN rating DOUBLE NULL AFTER address,
    ADD COLUMN opening_hours VARCHAR(100) NULL AFTER rating;
