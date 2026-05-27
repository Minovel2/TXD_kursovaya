package com.carpool.util;

import com.carpool.entity.*;
import com.carpool.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Инициализация тестовых данных.
 * Создаёт записи в БД, если они ещё не существуют.
 */
public class SchemaInitializer {

    private static final Logger log = LoggerFactory.getLogger(SchemaInitializer.class);

    public static void initialize() {
        // ======== ШАГ 1: СОЗДАНИЕ СХЕМЫ (с транзакцией) ========
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                // Проверяем, существует ли схема carpool
                boolean schemaExists = false;
                try {
                    em.createNativeQuery("SELECT 1 FROM information_schema.schemata WHERE schema_name = 'carpool'")
                            .getSingleResult();
                    schemaExists = true;
                } catch (Exception e) {
                    // Схема не найдена — продолжаем
                }

                if (!schemaExists) {
                    log.info("Схема carpool не найдена. Создаём...");
                    em.createNativeQuery("CREATE SCHEMA carpool").executeUpdate();
                    log.info("Схема carpool успешно создана.");
                } else {
                    log.info("Схема carpool уже существует.");
                }

                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                log.error("Ошибка при создании схемы carpool: {}", e.getMessage());
                throw e;
            }
        }

        // ======== ШАГ 2: ЗАПОЛНЕНИЕ ДАННЫМИ (новая транзакция) ========
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Проверка: если водители уже есть, ничего не делаем
            Long driverCount = em.createQuery("SELECT COUNT(d) FROM Driver d", Long.class).getSingleResult();
            if (driverCount > 0) {
                tx.commit();
                log.info("Данные уже существуют. Инициализация пропущена.");
                return;
            }

            // 1. Водители
            Driver d1 = new Driver("Зевахин", "Дмитрий", "Сергеевич");
            Driver d2 = new Driver("Петров", "Пётр", "Петрович");
            Driver d3 = new Driver("Сидоров", "Сидор", "Сидорович");
            Driver d4 = new Driver("Козлов", "Алексей", "Дмитриевич");
            Driver d5 = new Driver("Смирнова", "Ольга", "Владимировна");
            List.of(d1, d2, d3, d4, d5).forEach(em::persist);

            // 2. Пассажиры
            Passenger p1 = new Passenger("Новикова", "Елена", "Андреевна");
            Passenger p2 = new Passenger("Морозов", "Денис", "Сергеевич");
            Passenger p3 = new Passenger("Волкова", "Татьяна", "Игоревна");
            Passenger p4 = new Passenger("Зевахин", "Павел", "Александрович");
            Passenger p5 = new Passenger("Кузнецов", "Артём", "Николаевич");
            List.of(p1, p2, p3, p4, p5).forEach(em::persist);

            // 3. Города
            City moscow = new City("Москва");
            City spb = new City("Санкт-Петербург");
            City tver = new City("Тверь");
            City novgorod = new City("Новгород");
            City kazan = new City("Казань");
            List.of(moscow, spb, tver, novgorod, kazan).forEach(em::persist);

            em.flush(); // Чтобы получить id

            // 4. Поездки
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(3));

            Trip trip1 = new Trip(d1, "Москва", "Санкт-Петербург",
                    now.plusDays(1).withHour(9), "Toyota Camry", (short) 3, (short) 0);
            em.persist(trip1);

            Trip trip2 = new Trip(d2, "Тверь", "Казань",
                    now.plusDays(2).withHour(10), "Hyundai Solaris", (short) 2, (short) 0);
            em.persist(trip2);

            Trip trip3 = new Trip(d3, "Москва", "Новгород",
                    now.plusDays(3).withHour(8), "Kia Rio", (short) 4, (short) 0);
            em.persist(trip3);

            // 5. Промежуточные города
            em.persist(new TripCity(trip1, tver,
                    now.plusDays(1).withHour(12),
                    now.plusDays(1).withHour(12).plusMinutes(30),
                    new BigDecimal("500.00"), 1));

            em.persist(new TripCity(trip1, novgorod,
                    now.plusDays(1).withHour(15),
                    now.plusDays(1).withHour(15).plusMinutes(20),
                    new BigDecimal("800.00"), 2));

            em.persist(new TripCity(trip2, moscow,
                    now.plusDays(2).withHour(14),
                    now.plusDays(2).withHour(14).plusMinutes(30),
                    new BigDecimal("1200.00"), 1));

            em.persist(new TripCity(trip2, novgorod,
                    now.plusDays(2).withHour(18),
                    now.plusDays(2).withHour(18).plusMinutes(20),
                    new BigDecimal("600.00"), 2));

            em.persist(new TripCity(trip3, tver,
                    now.plusDays(3).withHour(11),
                    now.plusDays(3).withHour(11).plusMinutes(30),
                    new BigDecimal("300.00"), 1));

            // 6. Бронирования
            em.persist(new Booking(p1, trip1, "Москва", "Тверь"));
            em.persist(new Booking(p2, trip1, "Москва", "Санкт-Петербург"));
            em.persist(new Booking(p4, trip3, "Москва", "Новгород"));

            // Обновляем места в поездках (транзакционно, вручную — для примера)
            trip1.setAvailableSeats((short) (trip1.getAvailableSeats() - 2));
            trip3.setAvailableSeats((short) (trip3.getAvailableSeats() - 1));
            em.merge(trip1);
            em.merge(trip3);

            // 7. Отзывы
            em.persist(new Review(p1, d1, "Отличный водитель, ехали комфортно", (short) 5));
            em.persist(new Review(p2, d2, "Немного опоздали, но в целом нормально", (short) 4));
            em.persist(new Review(p3, d3, "Машина чистая, вежливый водитель", (short) 5));

            tx.commit();
            log.info("Тестовые данные успешно добавлены.");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Ошибка при инициализации данных: {}", e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }
}