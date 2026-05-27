package com.carpool.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reviews", schema = "carpool")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "review_text")
    private String reviewText;

    @Column
    private Short rating;

    protected Review() {}

    public Review(Passenger passenger, Driver driver, String reviewText, Short rating) {
        this.passenger = passenger;
        this.driver = driver;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    // Геттеры и сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public Short getRating() { return rating; }
    public void setRating(Short rating) { this.rating = rating; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review r)) return false;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return String.format("Review{id=%d, rating=%d, text='%s'}", id, rating, reviewText);
    }
}