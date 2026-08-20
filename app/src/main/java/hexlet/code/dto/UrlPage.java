package hexlet.code.dto;

import hexlet.code.model.Url;

/**
 * Страница с информацией об одном url.
 */
public final class UrlPage extends BasePage {
    private final Url url;

    public UrlPage(Url url) {
        this.url = url;
    }

    public Url getUrl() {
        return url;
    }
}
