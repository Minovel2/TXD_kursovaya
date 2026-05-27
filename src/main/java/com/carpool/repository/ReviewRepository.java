package com.carpool.repository;

import com.carpool.entity.Review;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ReviewRepository extends GenericRepository<Review, Integer> {

    public ReviewRepository() { super(Review.class); }

    /**
     * Находит все отзывы, оставленные конкретному водителю.
     */
    public List<Review> findByDriverId(int driverId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM Review r JOIN FETCH r.passenger WHERE r.driver.id = :driverId ORDER BY r.id DESC",
                            Review.class)
                    .setParameter("driverId", driverId)
                    .getResultList();
        }
    }

    /**
     * Находит все отзывы, оставленные конкретным пассажиром.
     */
    public List<Review> findByPassengerId(int passengerId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM Review r JOIN FETCH r.driver WHERE r.passenger.id = :passengerId ORDER BY r.id DESC",
                            Review.class)
                    .setParameter("passengerId", passengerId)
                    .getResultList();
        }
    }

    /**
     * Проверяет, оставлял ли пассажир отзыв этому водителю (чтобы избежать дубликатов).
     */
    public boolean existsByPassengerAndDriver(int passengerId, int driverId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(r) FROM Review r WHERE r.passenger.id = :passengerId AND r.driver.id = :driverId",
                            Long.class)
                    .setParameter("passengerId", passengerId)
                    .setParameter("driverId", driverId)
                    .getSingleResult();
            return count > 0;
        }
    }
}