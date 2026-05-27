package com.carpool.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bookings", schema = "carpool")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "departure_city", nullable = false, length = 255)
    private String departureCity;

    @Column(name = "arrival_city", nullable = false, length = 255)
    private String arrivalCity;

    protected Booking() {}

    public Booking(Passenger passenger, Trip trip, String departureCity, String arrivalCity) {
        this.passenger = passenger;
        this.trip = trip;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking b)) return false;
        return Objects.equals(id, b.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}