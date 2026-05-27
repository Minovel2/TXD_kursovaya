package com.carpool.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "trips", schema = "carpool")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "departure_point", nullable = false, length = 255)
    private String departurePoint;

    @Column(name = "arrival_point", nullable = false, length = 255)
    private String arrivalPoint;

    @Column(name = "departure_datetime")
    private OffsetDateTime departureDateTime;

    @Column(name = "car_model", nullable = false, length = 255)
    private String carModel;

    @Column(name = "available_seats", nullable = false)
    private Short availableSeats;

    @Column(nullable = false)
    private Short status; // 0 - активна, 1 - завершена

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TripCity> tripCities = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    protected Trip() {}

    // Конструктор
    public Trip(Driver driver, String departurePoint, String arrivalPoint, OffsetDateTime departureDateTime, String carModel, Short availableSeats, Short status) {
        this.driver = driver;
        this.departurePoint = departurePoint;
        this.arrivalPoint = arrivalPoint;
        this.departureDateTime = departureDateTime;
        this.carModel = carModel;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    // Геттеры/Сеттеры, equals, hashCode...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public String getDeparturePoint() { return departurePoint; }
    public void setDeparturePoint(String departurePoint) { this.departurePoint = departurePoint; }
    public String getArrivalPoint() { return arrivalPoint; }
    public void setArrivalPoint(String arrivalPoint) { this.arrivalPoint = arrivalPoint; }
    public OffsetDateTime getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(OffsetDateTime departureDateTime) { this.departureDateTime = departureDateTime; }
    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    public Short getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Short availableSeats) { this.availableSeats = availableSeats; }
    public Short getStatus() { return status; }
    public void setStatus(Short status) { this.status = status; }
    public List<TripCity> getTripCities() { return tripCities; }
    public List<Booking> getBookings() { return bookings; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip t)) return false;
        return Objects.equals(id, t.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return String.format("Trip{id=%d, from=%s to=%s, driver=%s}", id, departurePoint, arrivalPoint, driver);
    }
}