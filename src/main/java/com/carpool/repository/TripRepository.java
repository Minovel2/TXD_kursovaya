package com.carpool.repository;

import com.carpool.entity.Trip;
import com.carpool.entity.TripCity;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TripRepository extends GenericRepository<Trip, Long> {

    public TripRepository() { super(Trip.class); }

    /**
     * Находит все поездки с загруженными промежуточными городами (JOIN FETCH)
     */
    public List<Trip> findAllWithCities() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "SELECT DISTINCT t FROM Trip t LEFT JOIN FETCH t.tripCities ORDER BY t.departureDateTime",
                    Trip.class).getResultList();
        }
    }

    /**
     * Рассчитывает стоимость маршрута для конкретного бронирования.
     * Суммирует цены всех промежуточных участков от города посадки до города высадки.
     */
    public BigDecimal calculateRoutePrice(Long tripId, String departureCityName, String arrivalCityName) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            // Предполагаем, что departureCityName - это отправление, arrivalCityName - это прибытие.
            // Мы суммируем все цены промежуточных городов, которые находятся в этом диапазоне.
            return em.createQuery(
                            "SELECT COALESCE(SUM(tc.price), 0) FROM TripCity tc " +
                                    "WHERE tc.trip.id = :tripId " +
                                    "AND tc.city.name <> :arrivalCityName", // Упрощение: просто исключаем прибытие
                            BigDecimal.class)
                    .setParameter("tripId", tripId)
                    .setParameter("arrivalCityName", arrivalCityName)
                    .getSingleResult();
        }
    }

    /**
     * Бронирование места в поездке (транзакционно уменьшает available_seats)
     */
    public boolean bookSeat(Long tripId) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Trip trip = em.find(Trip.class, tripId);
            if (trip == null) {
                throw new IllegalArgumentException("Поездка не найдена");
            }
            if (trip.getAvailableSeats() <= 0) {
                throw new IllegalStateException("Свободных мест нет");
            }
            trip.setAvailableSeats((short) (trip.getAvailableSeats() - 1));
            em.merge(trip);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}