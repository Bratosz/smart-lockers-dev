package pl.bratosz.smartlockers.exception;

public class ClothNotExistException extends Exception {

    public ClothNotExistException() {

    }

    public ClothNotExistException(String message) {
        super(message);
    }
}
