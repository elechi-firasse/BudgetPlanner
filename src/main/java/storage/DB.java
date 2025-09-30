package storage;

import java.nio.file.*;
import java.sql.*;

public final class DB {
    private static final String URL = "jdbc:sqlite:" + Paths.get("data", "budget.db").toString();

    static {
        try { Files.createDirectories(Paths.get("data")); } catch (Exception ignored) {}
        init();
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void init() {
        String sql = """
            PRAGMA journal_mode=WAL;
            CREATE TABLE IF NOT EXISTS transactions (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              account_id INTEGER NOT NULL DEFAULT 1,
              posted_date TEXT NOT NULL,
              amount_cents INTEGER NOT NULL,
              currency TEXT NOT NULL DEFAULT 'EUR',
              payee TEXT,
              memo TEXT,
              category_id INTEGER NULL,
              cleared INTEGER NOT NULL DEFAULT 0
            );
            CREATE INDEX IF NOT EXISTS idx_tx_date ON transactions(posted_date);
            """;
        try (var c = get(); var st = c.createStatement()) {
            for (String stmt : sql.split(";\\s*\\n")) {
                if (!stmt.isBlank()) st.execute(stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB init failed", e);
        }
    }
}
