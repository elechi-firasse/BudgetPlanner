package io;

import core.model.Money;
import core.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class CsvImporter {
    // Adjust for your bank format
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<Transaction> load(Path path, long accountId) {
        try (Reader r = Files.newBufferedReader(path)) {
            Iterable<CSVRecord> rows = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(r);
            var out = new ArrayList<Transaction>();
            for (CSVRecord rec : rows) {
                LocalDate date = LocalDate.parse(rec.get("Date"), DMY);    // e.g., "31.08.2025"
                String payee = rec.get("Payee");
                String memo  = rec.get("Memo");
                // Amount like "-12,34" or "-12.34" → normalize:
                String raw = rec.get("Amount").replace(",", ".");
                long cents = Math.round(Double.parseDouble(raw) * 100.0);
                out.add(new Transaction(0L, accountId, date, new Money(cents, "EUR"), payee, memo, null, false));
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("CSV parse failed: " + e.getMessage(), e);
        }
    }
}
