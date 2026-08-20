package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

/**
 * Обработчики маршрутов для работы с url.
 */
public final class UrlsController {

    /**
     * Выводит список всех добавленных url.
     *
     * @param ctx контекст запроса
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/index.jte", Map.of("page", page));
    }

    /**
     * Выводит информацию об одном url.
     *
     * @param ctx контекст запроса
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        var page = new UrlPage(url);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/show.jte", Map.of("page", page));
    }

    /**
     * Добавляет новый url в базу данных.
     *
     * @param ctx контекст запроса
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static void create(Context ctx) throws SQLException {
        var input = ctx.formParam("url");
        var name = normalize(input);
        if (name == null) {
            var page = new BasePage();
            page.setFlash("Некорректный URL");
            page.setFlashType("error");
            ctx.status(422);
            ctx.render("index.jte", Map.of("page", page));
            return;
        }

        var existingUrl = UrlRepository.findByName(name);
        if (existingUrl.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect(NamedRoutes.urlPath(existingUrl.get().getId()));
            return;
        }

        var url = new Url(name);
        UrlRepository.save(url);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
    }

    private static String normalize(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            URL url = new URI(input.trim()).toURL();
            return url.getProtocol() + "://" + url.getAuthority();
        } catch (Exception e) {
            return null;
        }
    }
}
