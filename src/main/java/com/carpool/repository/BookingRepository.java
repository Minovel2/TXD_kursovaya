package com.carpool.repository;

import com.carpool.entity.Booking;
import com.carpool.entity.Passenger;
import com.carpool.entity.Trip;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class BookingRepository extends GenericRepository<Booking, Integer> {

    public BookingRepository() { super(Booking.class); }

    public List<Booking> findByPassengerId(int passengerId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                    "FROM Booking b JOIN FETCH b.trip WHERE b.passenger.id = :pid",
                    Booking.class).setParameter("pid", passengerId).getResultList();
        }
    }

    /**
     * Создает бронирование и уменьшает количество мест в поездке (транзакционно)
     */
    public Booking createBooking(int passengerId, long tripId, String departureCity, String arrivalCity) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Passenger passenger = em.find(Passenger.class, passengerId);
            Trip trip = em.find(Trip.class, tripId);

            if (passenger == null || trip == null) {
                throw new IllegalArgumentException("Пассажир или поездка не найдены");
            }
            if (trip.getAvailableSeats() <= 0) {
                throw new IllegalStateException("Нет свободных мест");
            }

            // Уменьшаем количество мест
            trip.setAvailableSeats((short) (trip.getAvailableSeats() - 1));
            em.merge(trip);

            // Создаем бронирование
            Booking booking = new Booking(passenger, trip, departureCity, arrivalCity);
            em.persist(booking);

            tx.commit();
            return booking;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}