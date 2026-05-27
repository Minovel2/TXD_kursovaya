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
            HibernateUtil.getEntityManagerFactory();

            // ЗАПОЛНЕНИЕ ТЕСТОВЫМИ ДАННЫМИ
            SchemaInitializer.initialize();

            log.info("\n=== ДЕМОНСТРАЦИЯ CRUD ===");
            CrudDemoService crudDemo = new CrudDemoService();
            crudDemo.runAll();

            log.info("\n=== БИЗНЕС-ЗАПРОСЫ (JPQL) ===");
            BusinessQueryService queryService = new BusinessQueryService();
            queryService.runAll();

        } catch (Exception e) {
            log.error("Ошибка при выполнении программы", e); // <-- Вся информация об ошибке попадёт в лог
        } finally {
            HibernateUtil.close();
            log.info("Hibernate закрыт. Готово.");
        }
    }
}