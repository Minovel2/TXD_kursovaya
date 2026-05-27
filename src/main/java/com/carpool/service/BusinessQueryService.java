package com.carpool.service;

import com.carpool.entity.Trip;
import com.carpool.entity.Driver;
import com.carpool.entity.Review;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;

/**
 * 10 бизнес-запросов для системы «Карпулинг».
 * Каждый запрос — это реальная задача, которую решает приложение.
 */
public class BusinessQueryService {

    // --- ЗАПРОС 1: Популярные направления (топ-5 маршрутов) ---
    public void popularRoutes() {
        printHeader("1. Популярные направления (топ-5 маршрутов по бронированиям)");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT b.departureCity, b.arrivalCity, COUNT(b)
                    FROM Booking b
                    GROUP BY b.departureCity, b.arrivalCity
                    ORDER BY COUNT(b) DESC
                    """, Object[].class)
                    .setMaxResults(5)
                    .getResultList();

            System.out.printf("     %-20s %-20s %-10s%n", "Откуда", "Куда", "Бронирований");
            System.out.println("     " + "─".repeat(54));
            for (Object[] row : results) {
                System.out.printf("     %-20s %-20s %-10d%n", row[0], row[1], (long) row[2]);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 2: Рейтинг водителей (средняя оценка) ---
    public void driverRatings() {
        printHeader("2. Рейтинг водителей (средняя оценка)");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT d.id, CONCAT(d.lastName, ' ', d.firstName), AVG(r.rating)
                FROM Driver d
                LEFT JOIN Review r ON r.driver.id = d.id
                GROUP BY d.id, d.lastName, d.firstName
                ORDER BY AVG(r.rating) DESC
                """, Object[].class).getResultList();

            System.out.printf("     %-5s %-30s %-10s%n", "ID", "Водитель", "Рейтинг");
            System.out.println("     " + "─".repeat(49));
            for (Object[] row : results) {
                Double avg = row[2] != null ? (Double) row[2] : 0.0;
                System.out.printf("     %-5d %-30s %-10.2f%n", (int) row[0], row[1], avg);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 3: Поиск поездок с фильтрацией (подзапрос) ---
    public void searchTrips() {
        printHeader("3. Поиск поездок (Москва → Тверь)");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Trip> trips = em.createQuery("""
                    SELECT DISTINCT t
                    FROM Trip t
                    JOIN t.tripCities tc
                    WHERE t.departurePoint = :from
                      AND t.arrivalPoint = :to
                      AND t.availableSeats > 0
                      AND t.status = 0
                    ORDER BY t.departureDateTime
                    """, Trip.class)
                    .setParameter("from", "Москва")
                    .setParameter("to", "Тверь")
                    .getResultList();

            System.out.println("     Найдено поездок: " + trips.size());
            for (Trip t : trips) {
                System.out.printf("     ID: %d, Водитель: %s, Время: %s, Мест: %d%n",
                        t.getId(), t.getDriver().getFullName(), t.getDepartureDateTime(), t.getAvailableSeats());
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 4: Активные водители (есть активные поездки) ---
    public void activeDrivers() {
        printHeader("4. Активные водители (есть активные поездки)");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT d.id, CONCAT(d.lastName, ' ', d.firstName), COUNT(t)
                    FROM Driver d
                    JOIN d.trips t
                    WHERE t.status = 0
                    GROUP BY d.id, d.lastName, d.firstName
                    """, Object[].class).getResultList();

            System.out.printf("     %-5s %-30s %-10s%n", "ID", "Водитель", "Активных поездок");
            System.out.println("     " + "─".repeat(51));
            for (Object[] row : results) {
                System.out.printf("     %-5d %-30s %-10d%n", (int) row[0], row[1], (long) row[2]);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 5: Пассажиры, которые ни разу не бронировали ---
    public void passengersWithoutBookings() {
        printHeader("5. Пассажиры без бронирований");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT p.id, CONCAT(p.lastName, ' ', p.firstName)
                    FROM Passenger p
                    WHERE p NOT IN (SELECT b.passenger FROM Booking b)
                    """, Object[].class).getResultList();

            System.out.printf("     %-5s %-30s%n", "ID", "Пассажир");
            System.out.println("     " + "─".repeat(37));
            for (Object[] row : results) {
                System.out.printf("     %-5d %-30s%n", (int) row[0], row[1]);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 6: Средняя стоимость поездки по направлениям ---
    public void averagePriceByDirection() {
        printHeader("6. Средняя стоимость поездки по направлениям");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT b.departureCity, b.arrivalCity, AVG(tc.price)
                FROM Booking b
                JOIN b.trip t
                JOIN t.tripCities tc
                GROUP BY b.departureCity, b.arrivalCity
                """, Object[].class).getResultList();

            System.out.printf("     %-20s %-20s %-15s%n", "Откуда", "Куда", "Средняя цена");
            System.out.println("     " + "─".repeat(59));
            for (Object[] row : results) {
                String from = (String) row[0];
                String to = (String) row[1];
                Double avgPrice = (Double) row[2]; // <--- ВАЖНО: Double, а не BigDecimal
                System.out.printf("     %-20s %-20s %-15.2f%n", from, to, avgPrice);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 7: Топ-3 самых щедрых пассажиров (по сумме потраченной) ---
    public void topSpenders() {
        printHeader("7. Топ-3 пассажира по сумме потраченной");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                SELECT CONCAT(p.lastName, ' ', p.firstName), SUM(tc.price)
                FROM Booking b
                JOIN b.passenger p
                JOIN b.trip tr
                JOIN tr.tripCities tc
                GROUP BY p.id, p.lastName, p.firstName
                ORDER BY SUM(tc.price) DESC
                """, Object[].class)
                    .setMaxResults(3)
                    .getResultList();

            System.out.printf("     %-30s %-15s%n", "Пассажир", "Потрачено (₽)");
            System.out.println("     " + "─".repeat(47));
            for (Object[] row : results) {
                String passengerName = (String) row[0];
                BigDecimal totalSpent = (BigDecimal) row[1];
                System.out.printf("     %-30s %-15.2f%n", passengerName, totalSpent.doubleValue());
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 8: Поездки с самым длинным маршрутом (по количеству промежуточных городов) ---
    public void longestRoutes() {
        printHeader("8. Поездки с наибольшим количеством промежуточных городов");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT t.id, t.departurePoint, t.arrivalPoint, SIZE(t.tripCities)
                    FROM Trip t
                    ORDER BY SIZE(t.tripCities) DESC
                    """, Object[].class)
                    .setMaxResults(5)
                    .getResultList();

            System.out.printf("     %-5s %-15s %-15s %-15s%n", "ID", "Откуда", "Куда", "Промежуточных");
            System.out.println("     " + "─".repeat(54));
            for (Object[] row : results) {
                System.out.printf("     %-5d %-15s %-15s %-15d%n", (long) row[0], row[1], row[2], (int) row[3]);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 9: Статистика по городам (сколько раз город был точкой отправления/прибытия) ---
    public void cityStatistics() {
        printHeader("9. Статистика по городам");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT 'Отправление', b.departureCity, COUNT(b)
                    FROM Booking b
                    GROUP BY b.departureCity
                    UNION ALL
                    SELECT 'Прибытие', b.arrivalCity, COUNT(b)
                    FROM Booking b
                    GROUP BY b.arrivalCity
                    ORDER BY 1, 3 DESC
                    """, Object[].class).getResultList();

            System.out.printf("     %-12s %-20s %-10s%n", "Тип", "Город", "Количество");
            System.out.println("     " + "─".repeat(44));
            for (Object[] row : results) {
                System.out.printf("     %-12s %-20s %-10d%n", row[0], row[1], (long) row[2]);
            }
        }
        printDivider();
    }

    // --- ЗАПРОС 10: Водители, которые никогда не получали отзывы ---
    public void driversWithoutReviews() {
        printHeader("10. Водители без отзывов");
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT d.id, CONCAT(d.lastName, ' ', d.firstName)
                    FROM Driver d
                    WHERE d NOT IN (SELECT r.driver FROM Review r)
                    """, Object[].class).getResultList();

            System.out.printf("     %-5s %-30s%n", "ID", "Водитель");
            System.out.println("     " + "─".repeat(37));
            for (Object[] row : results) {
                System.out.printf("     %-5d %-30s%n", (int) row[0], row[1]);
            }
        }
        printDivider();
    }

    // --- ЗАПУСК ВСЕХ ЗАПРОСОВ ---
    public void runAll() {
        popularRoutes();
        driverRatings();
        searchTrips();
        activeDrivers();
        passengersWithoutBookings();
        averagePriceByDirection();
        topSpenders();
        longestRoutes();
        cityStatistics();
        driversWithoutReviews();
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("╔" + "═".repeat(title.length() + 4) + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + "═".repeat(title.length() + 4) + "╝");
    }

    private void printDivider() {
        System.out.println("─".repeat(80));
    }
}