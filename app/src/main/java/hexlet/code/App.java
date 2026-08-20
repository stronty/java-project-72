package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

public final class App {

    // Метод выставлен наружу (public static), чтобы тесты и внешний код могли
    // получить полностью настроенное javalin-приложение и запустить его сами.
    public static Javalin getApp() throws IOException, SQLException {
        initDataBase();

        var app = Javalin.create(config -> {
            // Логирование запросов в режиме разработки — удобно смотреть,
            // какие маршруты вызываются и с каким статусом отвечают.
            config.bundledPlugins.enableDevLogging();
            // Корневой маршрут, который отдаёт строку "Hello World" на странице.
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });
        return app;
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        // Порт берём из переменной окружения PORT — так приложение можно
        // развернуть на Render.com, который сам задаёт PORT извне.
        app.start(getPort());
    }

    private static void initDataBase() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        // В продакшене адрес базы задаётся снаружи через переменную окружения
        // JDBC_DATABASE_URL, при локальной разработке используется H2 в памяти.
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        var dataSource = new HikariDataSource(hikariConfig);

        var sql = readResourceFile("schema.sql");
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;
    }

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault(
                "JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
