-- ============================================================
-- Схема БД: Система карпулинга (carpool)
-- PostgreSQL 17
-- ============================================================

-- Создаём схему, если её нет
CREATE SCHEMA IF NOT EXISTS carpool;

-- Переключаемся на схему carpool
SET
search_path TO carpool;

-- ============================================================
-- Таблицы
-- ============================================================

-- Таблица "Водитель"
CREATE TABLE IF NOT EXISTS carpool.drivers
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    last_name   VARCHAR(100) NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    CONSTRAINT uq_drivers_name UNIQUE (last_name, first_name, middle_name)
);

-- Таблица "Пассажир"
CREATE TABLE IF NOT EXISTS carpool.passengers
(
    id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    last_name   VARCHAR(100) NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    CONSTRAINT uq_passengers_full_name UNIQUE (last_name, first_name, middle_name)
);

-- Таблица "Промежуточный город" (справочник городов)
CREATE TABLE IF NOT EXISTS carpool.cities
(
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_cities_name UNIQUE (name)
);

-- =============================================
-- Таблицы с FK на независимые
-- =============================================

-- Таблица "Предложение о поездке"
CREATE TABLE IF NOT EXISTS carpool.trips
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    driver_id          INT          NOT NULL,
    departure_point    VARCHAR(255) NOT NULL,
    arrival_point      VARCHAR(255) NOT NULL,
    departure_datetime TIMESTAMPTZ,
    car_model          VARCHAR(255) NOT NULL,
    available_seats    SMALLINT     NOT NULL,
    status             SMALLINT     NOT NULL, -- Статус поездки (например, 0 - активна, 1 - завершена)

    CONSTRAINT fk_trips_driver
        FOREIGN KEY (driver_id) REFERENCES carpool.drivers (id) ON DELETE RESTRICT,
    CONSTRAINT chk_trips_available_seats CHECK (available_seats >= 0)
);

-- Таблица "Промежуточный город"
CREATE TABLE IF NOT EXISTS carpool.trip_cities
(
    trip_id        BIGINT         NOT NULL,
    city_id        INT            NOT NULL,
    arrival_time   TIMESTAMPTZ,
    departure_time TIMESTAMPTZ,
    price          NUMERIC(10, 2) NOT NULL,
    city_order     INT            not null,

    CONSTRAINT pk_trip_cities PRIMARY KEY (trip_id, city_id),
    CONSTRAINT fk_trip_cities_trip
        FOREIGN KEY (trip_id) REFERENCES carpool.trips (id) ON DELETE CASCADE,
    CONSTRAINT fk_trip_cities_city
        FOREIGN KEY (city_id) REFERENCES carpool.cities (id) ON DELETE RESTRICT
);

-- Таблица "Отзыв"
CREATE TABLE IF NOT EXISTS carpool.reviews
(
    id           INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    passenger_id INT NOT NULL,
    driver_id    INT NOT NULL,
    review_text  TEXT,
    rating       SMALLINT,

    CONSTRAINT fk_reviews_passenger
        FOREIGN KEY (passenger_id) REFERENCES carpool.passengers (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_driver
        FOREIGN KEY (driver_id) REFERENCES carpool.drivers (id) ON DELETE CASCADE,
    CONSTRAINT chk_reviews_rating CHECK (rating >= 1 AND rating <= 5)
);

-- Таблица "Бронь"
CREATE TABLE IF NOT EXISTS carpool.bookings
(
    id             INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    passenger_id   INT          NOT NULL,
    trip_id        BIGINT       NOT NULL,
    departure_city VARCHAR(255) NOT NULL,
    arrival_city   VARCHAR(255) NOT NULL,

    CONSTRAINT fk_bookings_passenger
        FOREIGN KEY (passenger_id) REFERENCES carpool.passengers (id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_trip
        FOREIGN KEY (trip_id) REFERENCES carpool.trips (id) ON DELETE RESTRICT
);

-- Таблица "Ссылка"
CREATE TABLE IF NOT EXISTS carpool.city_links
(
    prev_city_id INT,
    next_city_id INT,

    CONSTRAINT pk_city_links PRIMARY KEY (prev_city_id, next_city_id),
    CONSTRAINT fk_city_links_prev
        FOREIGN KEY (prev_city_id) REFERENCES carpool.cities (id) ON DELETE CASCADE,
    CONSTRAINT fk_city_links_next
        FOREIGN KEY (next_city_id) REFERENCES carpool.cities (id) ON DELETE CASCADE
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_trips_driver ON trips(driver_id);
CREATE INDEX IF NOT EXISTS idx_trips_departure ON trips(departure_datetime);
CREATE INDEX IF NOT EXISTS idx_trip_cities_trip ON trip_cities(trip_id);
CREATE INDEX IF NOT EXISTS idx_bookings_passenger ON bookings(passenger_id);
CREATE INDEX IF NOT EXISTS idx_bookings_trip ON bookings(trip_id);
CREATE INDEX IF NOT EXISTS idx_reviews_driver ON reviews(driver_id);