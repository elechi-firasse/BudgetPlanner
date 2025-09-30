package core.model;

public record Money(long cents, String currency) {
    public static Money eur(double amount) {
        return new Money(Math.round(amount * 100.0), "EUR");
    }
    public String format() {
        return String.format("%s %.2f", currency, cents / 100.0);
    }
}
