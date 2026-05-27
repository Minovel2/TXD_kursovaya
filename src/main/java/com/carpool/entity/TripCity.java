package com.carpool.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "trip_cities", schema = "carpool")
public class TripCity {

    @EmbeddedId
    private TripCityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cityId")
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "arrival_time")
    private OffsetDateTime arrivalTime;

    @Column(name = "departure_time")
    private OffsetDateTime departureTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "city_order", nullable = false)
    private Integer cityOrder;

    protected TripCity() {}

    public TripCity(Trip trip, City city, OffsetDateTime arrivalTime, OffsetDateTime departureTime, BigDecimal price, Integer cityOrder) {
        this.id = new TripCityId(trip.getId(), city.getId());
        this.trip = trip;
        this.city = city;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.price = price;
        this.cityOrder = cityOrder;
    }

    public TripCityId getId() { return id; }
    public void setId(TripCityId id) { this.id = id; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public OffsetDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(OffsetDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public OffsetDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(OffsetDateTime departureTime) { this.departureTime = departureTime; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getCityOrder() { return cityOrder; }
    public void setCityOrder(Integer cityOrder) { this.cityOrder = cityOrder; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripCity tc)) return false;
        return Objects.equals(id, tc.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}