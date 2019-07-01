package za.co.banking.zacobanking.exception;

public class MaxOverdraftViolationException extends Exception {
    public MaxOverdraftViolationException(String message) {
        super(message);
    }
}
