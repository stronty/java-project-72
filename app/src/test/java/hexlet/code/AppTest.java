package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.TestConfig;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.sql.SQLException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {

    private static final TestConfig FOLLOW_REDIRECTS = new TestConfig(
            true, true, HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                    .build());

    private static final String HTML = """
            <html>
              <head>
                <title>Example title</title>
                <meta name="description" content="Example description">
              </head>
              <body><h1>Example h1</h1></body>
            </html>
            """;

    private static MockWebServer mockWebServer;

    private Javalin app;

    @BeforeAll
    public static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    public static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
        UrlCheckRepository.removeAll();
        UrlRepository.removeAll();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("data-test=\"urls\"");
        });
    }

    @Test
    public void testCreateUrl() throws Exception {
        JavalinTest.test(app, FOLLOW_REDIRECTS, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.com");

            var url = UrlRepository.findByName("https://example.com").orElseThrow();
            var pageResponse = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(pageResponse.code()).isEqualTo(200);
            assertThat(pageResponse.body().string()).contains("https://example.com");
        });

        assertThat(UrlRepository.findByName("https://example.com")).isPresent();
        assertThat(UrlRepository.getEntities()).hasSize(1);
    }

    @Test
    public void testCreateUrlRedirect() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            assertThat(response.code()).isEqualTo(302);
        });
    }

    @Test
    public void testCreateUrlExisting() throws Exception {
        JavalinTest.test(app, FOLLOW_REDIRECTS, (server, client) -> {
            var first = client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            assertThat(first.code()).isEqualTo(200);

            var second = client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            assertThat(second.code()).isEqualTo(200);
            assertThat(second.body().string()).contains("Страница уже существует");
        });
        assertThat(UrlRepository.getEntities()).hasSize(1);
    }

    @Test
    public void testCreateUrlInvalid() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), "url=invalid");
            assertThat(response.code()).isEqualTo(422);
            assertThat(response.body().string()).contains("Некорректный URL");
        });
    }

    @Test
    public void testUrlPage() throws Exception {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        var check = new UrlCheck();
        check.setUrlId(url.getId());
        check.setStatusCode(200);
        check.setTitle("Example title");
        check.setH1("Example h1");
        check.setDescription("Example description");
        UrlCheckRepository.save(check);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://example.com")
                    .contains("action=\"/urls/" + url.getId() + "/checks\"")
                    .contains("data-test=\"checks\"")
                    .contains("Example title");
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(999999L));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testCreateCheck() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(HTML));
        var url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, FOLLOW_REDIRECTS, (server, client) -> {
            var response = client.post(NamedRoutes.checksPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("Страница успешно проверена")
                    .contains("data-test=\"checks\"")
                    .contains("Example title")
                    .contains("Example h1")
                    .contains("Example description")
                    .contains(">200<");
        });

        var checks = UrlCheckRepository.findByUrlId(url.getId());
        assertThat(checks).hasSize(1);
        var check = checks.get(0);
        assertThat(check.getStatusCode()).isEqualTo(200);
        assertThat(check.getTitle()).isEqualTo("Example title");
        assertThat(check.getH1()).isEqualTo("Example h1");
        assertThat(check.getDescription()).isEqualTo("Example description");
    }

    @Test
    public void testCreateCheckError() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        var url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, FOLLOW_REDIRECTS, (server, client) -> {
            var response = client.post(NamedRoutes.checksPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Произошла ошибка при проверке");
        });

        assertThat(UrlCheckRepository.findByUrlId(url.getId())).isEmpty();
    }

    @Test
    public void testCreateCheckTruncation() throws Exception {
        var longValue = "a".repeat(300);
        var html = "<html><head><title>" + longValue + "</title>"
                + "<meta name=\"description\" content=\"" + longValue + "\"></head>"
                + "<body><h1>" + longValue + "</h1></body></html>";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(html));
        var url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, FOLLOW_REDIRECTS, (server, client) -> {
            var response = client.post(NamedRoutes.checksPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            var body = response.body().string();
            assertThat(body).contains("a".repeat(200) + "...");
            assertThat(body).doesNotContain(longValue);
        });
    }

    @Test
    public void testUrlsPageLastCheck() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(HTML));
        var url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, FOLLOW_REDIRECTS, (server, client) -> {
            client.post(NamedRoutes.checksPath(url.getId()));
            var check = UrlCheckRepository.findByUrlId(url.getId()).get(0);
            var lastCheckDate = check.getCreatedAt().toLocalDateTime().toLocalDate().toString();

            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("Дата последней проверки")
                    .contains("200")
                    .contains(lastCheckDate);
        });
    }
}
