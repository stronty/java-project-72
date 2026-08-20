package hexlet.code.dto;

import io.javalin.http.Context;

/**
 * Базовый класс страниц, хранящий flash-сообщение для вывода пользователю.
 */
public class BasePage {
    private String flash;
    private String flashType = "success";

    /**
     * Забирает flash-сообщение из сессии и кладёт его на страницу.
     *
     * @param ctx контекст запроса
     */
    public final void setFlashFromSession(Context ctx) {
        this.flash = ctx.consumeSessionAttribute("flash");
        this.flashType = ctx.consumeSessionAttribute("flashType");
    }

    public final String getFlash() {
        return flash;
    }

    public final void setFlash(String flash) {
        this.flash = flash;
    }

    public final String getFlashType() {
        return flashType;
    }

    public final void setFlashType(String flashType) {
        this.flashType = flashType;
    }
}
