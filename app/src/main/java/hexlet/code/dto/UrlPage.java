package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.util.List;

public final class UrlPage extends BasePage {
    private final Url url;
    private final List<UrlCheck> checks;

    public UrlPage(Url url, List<UrlCheck> checks) {
        this.url = url;
        this.checks = checks;
    }

    public Url getUrl() {
        return url;
    }

    public List<UrlCheck> getChecks() {
        return checks;
    }
}
