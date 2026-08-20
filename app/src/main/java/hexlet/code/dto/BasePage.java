package hexlet.code.dto;

/**
 * Базовый класс страниц, хранящий flash-сообщение для вывода пользователю.
 */
public class BasePage {
    private String flash;
    private String flashType = "success";

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
