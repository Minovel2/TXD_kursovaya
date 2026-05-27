package com.carpool.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cities", schema = "carpool")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    protected City() {}

    public City(String name) {
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City c)) return false;
        return Objects.equals(name, c.name);
    }

    @Override
    public int hashCode() { return Objects.hashCode(name); }

    @Override
    public String toString() {
        return String.format("City{id=%d, name='%s'}", id, name);
    }
}