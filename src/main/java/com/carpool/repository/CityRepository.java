package com.carpool.repository;

import com.carpool.entity.City;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class CityRepository extends GenericRepository<City, Integer> {

    public CityRepository() { super(City.class); }

    public Optional<City> findByName(String name) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery("FROM City c WHERE c.name = :name", City.class)
                    .setParameter("name", name)
                    .getResultStream().findFirst();
        }
    }
}