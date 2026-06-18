package com.carpool;

import com.carpool.service.BusinessQueryService;
import com.carpool.service.CrudDemoService;
import com.carpool.util.HibernateUtil;
import com.carpool.util.SchemaInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final CrudDemoService crudDemo = new CrudDemoService();
    private static final BusinessQueryService bizQuery = new BusinessQueryService();

    public static void main(String[] args) {
        System.out.println("=== Hibernate Carpool Demo (Java 21 · PostgreSQL 17) ===\n");

        try {
            // Инициализация (схема + таблицы + данные)
            SchemaInitializer.executeSchema();
            HibernateUtil.getEntityManagerFactory();
            SchemaInitializer.seedData();
            System.out.println("БД готова.\n");
        } catch (Exception e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("""
                    [1] CRUD  [2] Запросы  [3] Всё  [0] Выход
                    > """);

            try {
                switch (scanner.nextLine().trim()) {
                    case "1" -> runCrudMenu(scanner);
                    case "2" -> runBusinessMenu(scanner);
                    case "3" -> runAllDemo();
                    case "0" -> running = false;
                    default -> System.out.println("Неверный выбор.");
                }
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        System.out.println("До свидания!");
        HibernateUtil.close();
    }

    // ── CRUD МЕНЮ ─────────────────────────────────────────────────────────────

    private static void runCrudMenu(Scanner scanner) {
        while (true) {
            System.out.print("""
                    [1] Create  [2] Read  [3] Update  [4] Delete
                    [5] Транзакция (Бронь)  [6] Всё  [0] Назад
                    > """);

            switch (scanner.nextLine().trim()) {
                case "1" -> crudDemo.demoCreate();
                case "2" -> crudDemo.demoRead();
                case "3" -> crudDemo.demoUpdate();
                case "4" -> crudDemo.demoDelete();
                case "5" -> crudDemo.demoTransaction();
                case "6" -> runAllCrud();
                case "0" -> { return; }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void runAllCrud() {
        crudDemo.demoCreate();
        crudDemo.demoRead();
        crudDemo.demoUpdate();
        crudDemo.demoDelete();
        crudDemo.demoTransaction();
    }

    // ── БИЗНЕС-ЗАПРОСЫ МЕНЮ ──────────────────────────────────────────────────

    private static void runBusinessMenu(Scanner scanner) {
        while (true) {
            System.out.print("""
                    [1] Популярные маршруты
                    [2] Рейтинг водителей
                    [3] Поиск поездок (Москва -> Тверь)
                    [4] Активные водители
                    [5] Пассажиры без броней
                    [6] Средняя цена по направлениям
                    [7] Топ-3 пассажира по тратам
                    [8] Самые длинные маршруты
                    [9] Статистика по городам
                    [10] Водители без отзывов
                    [11] Всё
                    [0] Назад
                    > """);

            switch (scanner.nextLine().trim()) {
                case "1" -> bizQuery.popularRoutes();
                case "2" -> bizQuery.driverRatings();
                case "3" -> bizQuery.searchTrips();
                case "4" -> bizQuery.activeDrivers();
                case "5" -> bizQuery.passengersWithoutBookings();
                case "6" -> bizQuery.averagePriceByDirection();
                case "7" -> bizQuery.topSpenders();
                case "8" -> bizQuery.longestRoutes();
                case "9" -> bizQuery.cityStatistics();
                case "10" -> bizQuery.driversWithoutReviews();
                case "11" -> runAllBusinessQueries();
                case "0" -> { return; }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void runAllBusinessQueries() {
        bizQuery.popularRoutes();
        bizQuery.driverRatings();
        bizQuery.searchTrips();
        bizQuery.activeDrivers();
        bizQuery.passengersWithoutBookings();
        bizQuery.averagePriceByDirection();
        bizQuery.topSpenders();
        bizQuery.longestRoutes();
        bizQuery.cityStatistics();
        bizQuery.driversWithoutReviews();
    }

    // ── ЗАПУСТИТЬ ВСЁ ─────────────────────────────────────────────────────────

    private static void runAllDemo() {
        System.out.println("\n--- CRUD ---");
        runAllCrud();
        System.out.println("\n--- Бизнес-запросы ---");
        runAllBusinessQueries();
        System.out.println("\nГотово.");
    }
}