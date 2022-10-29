package pl.bratosz.smartlockers.exception;

public class WrongIdException extends RuntimeException {
    public WrongIdException() {
    }

    public WrongIdException(String message) {
        super(message);
    }
}
