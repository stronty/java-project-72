package hexlet.code.util;

/**
 * Утилита для обрезки длинных строк при выводе в таблицах.
 */
public final class TextTruncator {

    private static final int MAX_LENGTH = 200;

    private TextTruncator() {
    }

    /**
     * Обрезает строку до 200 символов, добавляя в конец троеточие.
     *
     * @param value исходная строка
     * @return обрезанная строка или null, если входная строка null
     */
    public static String truncate(String value) {
        if (value == null || value.length() <= MAX_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LENGTH) + "...";
    }
}
