package com.carpool.repository;

import com.carpool.entity.Passenger;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class PassengerRepository extends GenericRepository<Passenger, Integer> {

    public PassengerRepository() { super(Passenger.class); }

    public Optional<Passenger> findByFullName(String lastName, String firstName, String middleName) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM Passenger p WHERE p.lastName = :ln AND p.firstName = :fn AND p.middleName = :mn",
                            Passenger.class)
                    .setParameter("ln", lastName)
                    .setParameter("fn", firstName)
                    .setParameter("mn", middleName)
                    .getResultStream().findFirst();
        }
    }
}