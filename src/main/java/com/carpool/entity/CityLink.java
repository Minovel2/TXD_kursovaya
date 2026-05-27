package com.carpool.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "city_links", schema = "carpool")
public class CityLink {

    @EmbeddedId
    private CityLinkId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("prevCityId")
    @JoinColumn(name = "prev_city_id")
    private City prevCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("nextCityId")
    @JoinColumn(name = "next_city_id")
    private City nextCity;

    protected CityLink() {}

    public CityLink(City prevCity, City nextCity) {
        this.id = new CityLinkId(prevCity.getId(), nextCity.getId());
        this.prevCity = prevCity;
        this.nextCity = nextCity;
    }

    public CityLinkId getId() { return id; }
    public void setId(CityLinkId id) { this.id = id; }
    public City getPrevCity() { return prevCity; }
    public void setPrevCity(City prevCity) { this.prevCity = prevCity; }
    public City getNextCity() { return nextCity; }
    public void setNextCity(City nextCity) { this.nextCity = nextCity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CityLink that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return String.format("CityLink{id=%s, %s → %s}", id, prevCity, nextCity);
    }
}