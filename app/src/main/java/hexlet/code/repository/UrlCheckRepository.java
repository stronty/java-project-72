package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link UrlCheck}.
 */
public class UrlCheckRepository extends BaseRepository {

    /**
     * Сохраняет новую проверку в базу данных и проставляет ей сгенерированный id.
     *
     * @param check сохраняемая проверка
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = dataSource.getConnection();
                var preparedStatement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, check.getUrlId());
            preparedStatement.setInt(2, check.getStatusCode());
            preparedStatement.setString(3, check.getTitle());
            preparedStatement.setString(4, check.getH1());
            preparedStatement.setString(5, check.getDescription());
            preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                check.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Возвращает все проверки url, отсортированные по дате создания по убыванию.
     *
     * @param urlId идентификатор url
     * @return список проверок
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC, id DESC";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                result.add(toUrlCheck(resultSet));
            }
            return result;
        }
    }

    /**
     * Возвращает последнюю проверку url.
     *
     * @param urlId идентификатор url
     * @return последняя проверка или пустой Optional
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static Optional<UrlCheck> findLastByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC, id DESC LIMIT 1";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUrlCheck(resultSet));
            }
            return Optional.empty();
        }
    }

    /**
     * Удаляет все проверки из базы данных. Используется в тестах.
     *
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static void removeAll() throws SQLException {
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM url_checks");
        }
    }

    private static UrlCheck toUrlCheck(ResultSet resultSet) throws SQLException {
        var check = new UrlCheck();
        check.setId(resultSet.getLong("id"));
        check.setUrlId(resultSet.getLong("url_id"));
        check.setStatusCode(resultSet.getInt("status_code"));
        check.setTitle(resultSet.getString("title"));
        check.setH1(resultSet.getString("h1"));
        check.setDescription(resultSet.getString("description"));
        check.setCreatedAt(resultSet.getTimestamp("created_at"));
        return check;
    }
}
