package hexlet.code.util;

/**
 * Класс с именованными маршрутами приложения.
 */
public final class NamedRoutes {

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String checksPath(Long id) {
        return checksPath(String.valueOf(id));
    }

    public static String checksPath(String id) {
        return "/urls/" + id + "/checks";
    }
}
