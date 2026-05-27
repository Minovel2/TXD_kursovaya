package com.carpool;

import com.carpool.service.BusinessQueryService;
import com.carpool.service.CrudDemoService;
import com.carpool.util.HibernateUtil;
import com.carpool.util.SchemaInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            log.info("=== ЗАПУСК HIBERNATE CARPOOL ===");

            // Шаг 1: Выполняем schema.sql (создаёт схему и таблицы)
            SchemaInitializer.executeSchema();

            // Шаг 2: Инициализируем Hibernate (он будет работать с существующими таблицами)
            HibernateUtil.getEntityManagerFactory();

            // Шаг 3: Заполняем данными
            SchemaInitializer.seedData();

            log.info("\n=== ДЕМОНСТРАЦИЯ CRUD ===");
            CrudDemoService crudDemo = new CrudDemoService();
            try {
                crudDemo.runAll();
            } catch (Exception e) {
                // CRUD уже выполнялся, ничего не делаем
            }

            log.info("\n=== БИЗНЕС-ЗАПРОСЫ (JPQL) ===");
            BusinessQueryService queryService = new BusinessQueryService();
            queryService.runAll();

        } catch (Exception e) {
            log.error("Ошибка при выполнении программы", e);
        } finally {
            HibernateUtil.close();
            log.info("Hibernate закрыт. Готово.");
        }
    }
}