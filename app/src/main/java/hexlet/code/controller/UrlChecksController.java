package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.sql.SQLException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;

public final class UrlChecksController {

    public static void create(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + urlId + " not found"));

        HttpResponse<String> response;
        try {
            response = Unirest.get(url.getName())
                    .connectTimeout(5000)
                    .socketTimeout(10000)
                    .asString();
        } catch (UnirestException e) {
            failCheck(ctx, urlId);
            return;
        }

        if (response.getStatus() >= 400) {
            failCheck(ctx, urlId);
            return;
        }

        var check = new UrlCheck();
        check.setUrlId(urlId);
        check.setStatusCode(response.getStatus());

        var document = Jsoup.parse(response.getBody());
        check.setTitle(document.title());
        var h1 = document.select("h1").first();
        check.setH1(h1 == null ? "" : h1.text());
        var description = document.select("meta[name=description]").first();
        check.setDescription(description == null ? "" : description.attr("content"));

        UrlCheckRepository.save(check);

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }

    private static void failCheck(Context ctx, Long urlId) {
        ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
        ctx.sessionAttribute("flashType", "error");
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
