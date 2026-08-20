package hexlet.code.dto;

/**
 * Базовый класс страниц, хранящий flash-сообщение для вывода пользователю.
 */
public class BasePage {
    private String flash;

    public final String getFlash() {
        return flash;
    }

    public final void setFlash(String flash) {
        this.flash = flash;
    }
}
