package hexlet.code.repository;

import hexlet.code.model.Url;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Url}.
 */
public class UrlRepository extends BaseRepository {

    /**
     * Сохраняет новый url в базу данных и проставляет ему сгенерированный id.
     *
     * @param url сохраняемый url
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var connection = dataSource.getConnection();
                var preparedStatement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Возвращает url по его id.
     *
     * @param id идентификатор url
     * @return найденный url или пустой Optional
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static Optional<Url> find(long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUrl(resultSet));
            }
            return Optional.empty();
        }
    }

    /**
     * Возвращает url по его имени.
     *
     * @param name имя url
     * @return найденный url или пустой Optional
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUrl(resultSet));
            }
            return Optional.empty();
        }
    }

    /**
     * Возвращает все url, отсортированные по дате создания по убыванию.
     *
     * @return список url
     * @throws SQLException при ошибке обращения к базе данных
     */
    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls ORDER BY created_at DESC, id DESC";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                result.add(toUrl(resultSet));
            }
            return result;
        }
    }

    private static Url toUrl(ResultSet resultSet) throws SQLException {
        var url = new Url(resultSet.getString("name"));
        url.setId(resultSet.getLong("id"));
        url.setCreatedAt(resultSet.getTimestamp("created_at"));
        return url;
    }
}
