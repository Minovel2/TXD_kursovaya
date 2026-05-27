package com.carpool.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CityLinkId implements Serializable {

    private Integer prevCityId;
    private Integer nextCityId;

    protected CityLinkId() {}

    public CityLinkId(Integer prevCityId, Integer nextCityId) {
        this.prevCityId = prevCityId;
        this.nextCityId = nextCityId;
    }

    public Integer getPrevCityId() { return prevCityId; }
    public void setPrevCityId(Integer prevCityId) { this.prevCityId = prevCityId; }
    public Integer getNextCityId() { return nextCityId; }
    public void setNextCityId(Integer nextCityId) { this.nextCityId = nextCityId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CityLinkId that)) return false;
        return Objects.equals(prevCityId, that.prevCityId) && Objects.equals(nextCityId, that.nextCityId);
    }

    @Override
    public int hashCode() { return Objects.hash(prevCityId, nextCityId); }

    @Override
    public String toString() {
        return String.format("CityLinkId{prev=%d, next=%d}", prevCityId, nextCityId);
    }
}