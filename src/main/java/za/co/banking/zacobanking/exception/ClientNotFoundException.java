package za.co.banking.zacobanking.exception;

public class ClientNotFoundException extends Exception {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException() {
        super();
    }
}
