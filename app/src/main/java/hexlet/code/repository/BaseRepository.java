package hexlet.code.repository;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Базовый класс репозиториев, хранящий соединение с базой данных.
 */
public class BaseRepository {
    public static HikariDataSource dataSource;
}
