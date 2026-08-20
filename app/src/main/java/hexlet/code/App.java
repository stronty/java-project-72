package hexlet.code;

import io.javalin.Javalin;

public final class App {

    // Метод выставлен наружу (public static), чтобы тесты и внешний код могли
    // получить полностью настроенное javalin-приложение и запустить его сами.
    public static Javalin getApp() {
        var app = Javalin.create(config -> {
            // Логирование запросов в режиме разработки — удобно смотреть,
            // какие маршруты вызываются и с каким статусом отвечают.
            config.bundledPlugins.enableDevLogging();
            // Корневой маршрут, который отдаёт строку "Hello World" на странице.
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });
        return app;
    }

    public static void main(String[] args) {
        var app = getApp();
        // Порт берём из переменной окружения PORT — так приложение можно
        // развернуть на Render.com, который сам задаёт PORT извне.
        app.start(getPort());
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}
