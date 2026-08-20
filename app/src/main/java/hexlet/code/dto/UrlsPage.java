package hexlet.code.dto;

import hexlet.code.model.Url;
import java.util.List;

/**
 * Страница со списком всех url.
 */
public final class UrlsPage extends BasePage {
    private final List<Url> urls;

    public UrlsPage(List<Url> urls) {
        this.urls = urls;
    }

    public List<Url> getUrls() {
        return urls;
    }
}
