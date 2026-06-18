package com.carpool.service;

import com.carpool.repository.*;
import com.carpool.entity.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class CrudDemoService {

    private final DriverRepository driverRepo = new DriverRepository();
    private final PassengerRepository passengerRepo = new PassengerRepository();
    private final CityRepository cityRepo = new CityRepository();
    private final TripRepository tripRepo = new TripRepository();
    private final BookingRepository bookingRepo = new BookingRepository();
    private final ReviewRepository reviewRepo = new ReviewRepository();
    private final GenericRepository<City, Integer> cityGenericRepo = new GenericRepository<>(City.class);
    private final GenericRepository<Review, Integer> reviewGenericRepo = new GenericRepository<>(Review.class);

    // --- CREATE ---
    public void demoCreate() {
        printHeader("CREATE — Создание записей");

        // 1. Создаём нового водителя
        Driver driver = driverRepo.save(new Driver("Иванов", "Иван", "Иванович"));
        System.out.printf("Создан водитель: id=%d, %s%n", driver.getId(), driver.getFullName());

        // 2. Создаём город
        City city = cityRepo.save(new City("Рязань"));
        System.out.printf("Создан город: id=%d, '%s'%n", city.getId(), city.getName());

        // 3. Создаём поездку (связываем водителя, города)
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(3));
        Trip trip = new Trip(
                driver,
                "Москва",
                "Рязань",
                now.plusDays(1).withHour(10),
                "Lada Vesta",
                (short) 3,
                (short) 0
        );
        trip = tripRepo.save(trip);
        System.out.printf("Создана поездка: id=%d, %s → %s%n", trip.getId(), trip.getDeparturePoint(), trip.getArrivalPoint());

        // 4. Создаём пассажира
        Passenger passenger = passengerRepo.save(new Passenger("Петров", "Петр", "Петрович"));
        System.out.printf("Создан пассажир: id=%d, %s%n", passenger.getId(), passenger.getFullName());

        // 5. Создаём бронирование (через репозиторий, который уменьшает места)
        Booking booking = bookingRepo.createBooking(passenger.getId(), trip.getId(), "Москва", "Рязань");
        System.out.printf("Создано бронирование: id=%d, пассажир=%s%n", booking.getId(), booking.getPassenger().getFullName());

        // 6. Создаём отзыв
        Review review = reviewRepo.save(new Review(passenger, driver, "Отличная поездка!", (short) 5));
        System.out.printf("Создан отзыв: id=%d, оценка=%d%n", review.getId(), review.getRating());

        printDivider();
    }

    // --- READ ---
    public void demoRead() {
        printHeader("READ — Чтение данных");

        System.out.println("Все водители:");
        List<Driver> drivers = driverRepo.findAll();
        System.out.printf("     %-5s %-30s%n", "ID", "ФИО");
        System.out.println("     " + "─".repeat(37));
        for (Driver d : drivers) {
            System.out.printf("     %-5d %-30s%n", d.getId(), d.getFullName());
        }

        System.out.println("\nВсе поездки с промежуточными городами:");
        List<Trip> trips = tripRepo.findAllWithCities();
        System.out.printf("     %-5s %-15s %-15s %-10s%n", "ID", "Откуда", "Куда", "Мест");
        System.out.println("     " + "─".repeat(49));
        for (Trip t : trips) {
            System.out.printf("     %-5d %-15s %-15s %-10d%n",
                    t.getId(), t.getDeparturePoint(), t.getArrivalPoint(), t.getAvailableSeats());
        }

        System.out.println("\nПоиск пассажира по id=1:");
        passengerRepo.findById(1).ifPresentOrElse(
                p -> System.out.println("     " + p),
                () -> System.out.println("     Не найден")
        );

        System.out.println("\nПоиск отзывов для водителя id=1:");
        List<Review> reviews = reviewRepo.findByDriverId(1);
        if (reviews.isEmpty()) {
            System.out.println("     Отзывов пока нет");
        } else {
            for (Review r : reviews) {
                System.out.printf("     Пассажир: %s, Оценка: %d, Текст: %s%n",
                        r.getPassenger().getFullName(), r.getRating(), r.getReviewText());
            }
        }

        printDivider();
    }

    // --- UPDATE ---
    public void demoUpdate() {
        printHeader("UPDATE — Обновление данных");

        // Для примера обновим название города
        cityRepo.findByName("Рязань").ifPresent(city -> {
            String oldName = city.getName();
            city.setName("Рязань (обновлена)");
            City updated = cityRepo.update(city);
            System.out.printf("  Обновлено название города: '%s' → '%s'%n", oldName, updated.getName());
            // Возвращаем обратно
            city.setName(oldName);
            cityRepo.update(city);
        });

        printDivider();
    }

    // --- DELETE ---
    public void demoDelete() {
        printHeader("DELETE — Удаление данных");

        // Создаём временного пассажира для удаления
        Passenger temp = passengerRepo.save(new Passenger("Удали", "Меня", ""));
        System.out.printf("Создан временный пассажир: id=%d%n", temp.getId());

        boolean deleted = passengerRepo.deleteById(temp.getId());
        System.out.printf("  Удалён id=%d (успех=%b)%n", temp.getId(), deleted);

        boolean notFound = passengerRepo.deleteById(99999);
        System.out.printf("  Удаление несуществующего id=99999 (успех=%b)%n", notFound);

        printDivider();
    }

    // --- TRANSACTION (Бронирование) ---
    public void demoTransaction() {
        printHeader("TRANSACTION — Бронирование места");


        System.out.println("Бронирование: пассажир=2, поездка=1");
        try {
            Booking booking = bookingRepo.createBooking(2, 1L, "Москва", "Тверь");
            System.out.printf("Бронирование создано! id=%d%n", booking.getId());

            System.out.println("\nПопытка забронировать место в той же поездке, но места закончились...");
            // Этот вызов упадёт, если мест нет. В DataSeeder мест было 3, мы одно заняли.

        } catch (IllegalStateException e) {
            System.out.printf("Ожидаемая ошибка: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("Ошибка: %s%n", e.getMessage());
        }

        printDivider();
    }

    public void runAll() {
        demoRead();
        demoCreate();
        demoUpdate();
        demoDelete();
        demoTransaction();
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("╔" + "═".repeat(title.length() + 4) + "╗");
        System.out.println("║  " + title + "  ║");
        System.out.println("╚" + "═".repeat(title.length() + 4) + "╝");
    }

    public static void printDivider() {
        System.out.println("─".repeat(80));
    }
}