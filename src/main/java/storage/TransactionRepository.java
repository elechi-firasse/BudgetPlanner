package storage;

import core.model.Money;
import core.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class TransactionRepository {

    public void insert(Transaction tx) {
        String sql = """
            INSERT INTO transactions(account_id, posted_date, amount_cents, currency, payee, memo, category_id, cleared)
            VALUES (?,?,?,?,?,?,?,?)
            """;
        try (var c = DB.get(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, tx.accountId());
            ps.setString(2, tx.postedDate().toString());
            ps.setLong(3, tx.amount().cents());
            ps.setString(4, tx.amount().currency());
            ps.setString(5, tx.payee());
            ps.setString(6, tx.memo());
            if (tx.categoryId() == null) ps.setNull(7, Types.BIGINT); else ps.setLong(7, tx.categoryId());
            ps.setInt(8, tx.cleared() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Transaction> listAll() {
        String sql = "SELECT id, account_id, posted_date, amount_cents, currency, payee, memo, category_id, cleared FROM transactions ORDER BY posted_date DESC";
        var out = new ArrayList<Transaction>();
        try (var c = DB.get(); var ps = c.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Transaction(
                        rs.getLong("id"),
                        rs.getLong("account_id"),
                        LocalDate.parse(rs.getString("posted_date")),
                        new Money(rs.getLong("amount_cents"), rs.getString("currency")),
                        rs.getString("payee"),
                        rs.getString("memo"),
                        rs.getObject("category_id") == null ? null : rs.getLong("category_id"),
                        rs.getInt("cleared") == 1
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}

