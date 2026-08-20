package hexlet.code.util;

public final class TextTruncator {

    private static final int MAX_LENGTH = 200;

    private TextTruncator() {
    }

    public static String truncate(String value) {
        if (value == null || value.length() <= MAX_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LENGTH) + "...";
    }
}
