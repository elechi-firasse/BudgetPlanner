package core.model;

import java.time.LocalDate;

public record Transaction(long id,
                          long accountId,
                          LocalDate postedDate,
                          Money amount,
                          String payee,
                          String memo,
                          Long categoryId,
                          boolean cleared
) {
}
