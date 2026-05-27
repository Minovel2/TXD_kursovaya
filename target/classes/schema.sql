-- ============================================================
-- Схема БД: Система карпулинга (carpool)
-- PostgreSQL 17
-- ============================================================

-- Удаляем схему, если она существует (для пересоздания)
DROP SCHEMA IF EXISTS carpool CASCADE;
CREATE SCHEMA carpool;

-- Переключаемся на схему carpool
SET search_path TO carpool;

-- ============================================================
-- Таблицы
-- ============================================================

CREATE TABLE drivers (
                         id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         last_name VARCHAR(100) NOT NULL,
                         first_name VARCHAR(100) NOT NULL,
                         middle_name VARCHAR(100)
);

CREATE TABLE passengers (
                            id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                            last_name VARCHAR(100) NOT NULL,
                            first_name VARCHAR(100) NOT NULL,
                            middle_name VARCHAR(100)
);

CREATE TABLE cities (
                        id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE trips (
                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                       driver_id INT NOT NULL,
                       departure_point VARCHAR(255) NOT NULL,
                       arrival_point VARCHAR(255) NOT NULL,
                       departure_datetime TIMESTAMPTZ,
                       car_model VARCHAR(255) NOT NULL,
                       available_seats SMALLINT NOT NULL,
                       status SMALLINT NOT NULL,
                       FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

CREATE TABLE trip_cities (
                             trip_id BIGINT NOT NULL,
                             city_id INT NOT NULL,
                             arrival_time TIMESTAMPTZ,
                             departure_time TIMESTAMPTZ,
                             price NUMERIC(10,2) NOT NULL,
                             city_order INT NOT NULL,
                             PRIMARY KEY (trip_id, city_id),
                             FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
                             FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT
);

CREATE TABLE bookings (
                          id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                          passenger_id INT NOT NULL,
                          trip_id BIGINT NOT NULL,
                          departure_city VARCHAR(255) NOT NULL,
                          arrival_city VARCHAR(255) NOT NULL,
                          FOREIGN KEY (passenger_id) REFERENCES passengers(id),
                          FOREIGN KEY (trip_id) REFERENCES trips(id)
);

CREATE TABLE reviews (
                         id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         passenger_id INT NOT NULL,
                         driver_id INT NOT NULL,
                         review_text TEXT,
                         rating SMALLINT,
                         FOREIGN KEY (passenger_id) REFERENCES passengers(id) ON DELETE CASCADE,
                         FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE
);

CREATE TABLE city_links (
                            prev_city_id INT,
                            next_city_id INT,
                            PRIMARY KEY (prev_city_id, next_city_id),
                            FOREIGN KEY (prev_city_id) REFERENCES cities(id) ON DELETE CASCADE,
                            FOREIGN KEY (next_city_id) REFERENCES cities(id) ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_trips_driver ON trips(driver_id);
CREATE INDEX idx_trips_departure ON trips(departure_datetime);
CREATE INDEX idx_trip_cities_trip ON trip_cities(trip_id);
CREATE INDEX idx_bookings_passenger ON bookings(passenger_id);
CREATE INDEX idx_bookings_trip ON bookings(trip_id);
CREATE INDEX idx_reviews_driver ON reviews(driver_id);