import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QueryBuilder {
    private final HikariDataSource dataSource;

    public QueryBuilder(HikariDataSource hikariDataSource) {
        this.dataSource = hikariDataSource;
    }

    private Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper) {

        try (Connection con = getConnection();
             var stmt = con.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            var results = new ArrayList<T>();

            while (rs.next()) {
                results.add(mapper.map(rs));
            }

            return List.copyOf(results);

        } catch (SQLException e) {
            throw new IllegalStateException("Database query failed", e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection con = getConnection();
             var stmt = con.prepareStatement(sql)) {
            final int PARAMS_LENGTH = params.length;
            for (int i = 0; i < PARAMS_LENGTH; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (var rs = stmt.executeQuery()) {
                var results = new ArrayList<T>();
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
                return List.copyOf(results);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Database query failed", e);
        }
    }
}

