package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlsController;
import hexlet.code.dto.BasePage;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
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
            // Подключаем шаблонизатор Jte для рендеринга страниц.
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
            // Раздаём статические файлы (например, собранный css) из каталога static.
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "static";
            });
            // Корневой маршрут, который выводит главную страницу с формой.
            config.routes.get("/", ctx -> {
                var page = new BasePage();
                page.setFlash(ctx.consumeSessionAttribute("flash"));
                page.setFlashType(ctx.consumeSessionAttribute("flashType"));
                ctx.render("index.jte", Map.of("page", page));
            });
            // Маршруты для работы с url.
            config.routes.get(NamedRoutes.urlsPath(), UrlsController::index);
            config.routes.post(NamedRoutes.urlsPath(), UrlsController::create);
            config.routes.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        });
        return app;
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        // Порт берём из переменной окружения PORT — так приложение можно
        // развернуть на Render.com, который сам задаёт PORT извне.
        app.start(getPort());
    }

    private static TemplateEngine createTemplateEngine() {
        // Шаблоны лежат в src/main/resources/templates. Явно указываем их
        // расположение через ResourceCodeResolver, чтобы шаблоны подгружались
        // из нужного места и при запуске автотестов.
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static void initDataBase() throws IOException, SQLException {
        var databaseUrl = getDatabaseUrl();
        var hikariConfig = new HikariConfig();
        // В продакшене адрес базы задаётся снаружи через переменную окружения
        // JDBC_DATABASE_URL, при локальной разработке используется H2 в памяти.
        hikariConfig.setJdbcUrl(databaseUrl);
        // Драйвер задаём явно: в fat-jar из нескольких драйверов через DriverManager
        // регистрируется только один, и без этой строки не находится PostgreSQL.
        if (databaseUrl.startsWith("jdbc:postgresql")) {
            hikariConfig.setDriverClassName("org.postgresql.Driver");
        } else {
            hikariConfig.setDriverClassName("org.h2.Driver");
        }
        configureCredentials(hikariConfig);
        var dataSource = new HikariDataSource(hikariConfig);

        var sql = readResourceFile("schema.sql");
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;
    }

    private static String getDatabaseUrl() {
        var databaseUrl = System.getenv().getOrDefault(
                "JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        // Render выдаёт ссылку на базу в формате postgres://..., а JDBC требует
        // jdbc:postgresql://.... Нормализуем её и добавляем sslmode для TLS.
        if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
            databaseUrl = "jdbc:postgresql://" + databaseUrl.substring(databaseUrl.indexOf("://") + 3);
            if (!databaseUrl.contains("?")) {
                databaseUrl += "?sslmode=require";
            }
        }
        return databaseUrl;
    }

    private static void configureCredentials(HikariConfig hikariConfig) {
        // JDBC-драйвер PostgreSQL не умеет разбирать user:password@ в URL,
        // поэтому выносим их в отдельные поля Hikari, убирая из адреса.
        var jdbcUrl = hikariConfig.getJdbcUrl();
        int schemeEnd = jdbcUrl.indexOf("://");
        if (schemeEnd == -1) {
            return;
        }
        var rest = jdbcUrl.substring(schemeEnd + 3);
        int at = rest.lastIndexOf('@');
        if (at == -1) {
            return;
        }
        var userInfo = rest.substring(0, at);
        int colon = userInfo.indexOf(':');
        if (colon == -1) {
            hikariConfig.setUsername(userInfo);
        } else {
            hikariConfig.setUsername(userInfo.substring(0, colon));
            hikariConfig.setPassword(userInfo.substring(colon + 1));
        }
        hikariConfig.setJdbcUrl(jdbcUrl.substring(0, schemeEnd + 3) + rest.substring(at + 1));
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
