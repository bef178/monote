package moo.diarystorage.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SqliteJdbcAnnoConfig(dbPath = "/tmp/diary.db")
public class SqliteHelper {

    static final Logger LOGGER = LoggerFactory.getLogger(SqliteHelper.class);

    static final String URI;

    static {
        SqliteJdbcAnnoConfig annoConfig = SqliteHelper.class
                .getAnnotation(SqliteJdbcAnnoConfig.class);
        URI = SqliteJdbcAnnoConfig.uriPrefix + annoConfig.dbPath();
    }

    static boolean execute(Connection conn, String sql) {
        try (Statement statement = conn.createStatement()) {
            return statement.execute(sql);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    static Connection getConnection() {
        // TODO thread pool
        try {
            return DriverManager.getConnection(URI);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
