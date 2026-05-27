package com.carpool.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TripCityId implements Serializable {

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "city_id")
    private Integer cityId;

    protected TripCityId() {}

    public TripCityId(Long tripId, Integer cityId) {
        this.tripId = tripId;
        this.cityId = cityId;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripCityId that)) return false;
        return Objects.equals(tripId, that.tripId) && Objects.equals(cityId, that.cityId);
    }

    @Override
    public int hashCode() { return Objects.hash(tripId, cityId); }
}