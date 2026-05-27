package com.carpool.repository;

import com.carpool.entity.Driver;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class DriverRepository extends GenericRepository<Driver, Integer> {

    public DriverRepository() { super(Driver.class); }

    public Optional<Driver> findByFullName(String lastName, String firstName, String middleName) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM Driver d WHERE d.lastName = :ln AND d.firstName = :fn AND d.middleName = :mn",
                            Driver.class)
                    .setParameter("ln", lastName)
                    .setParameter("fn", firstName)
                    .setParameter("mn", middleName)
                    .getResultStream().findFirst();
        }
    }
}