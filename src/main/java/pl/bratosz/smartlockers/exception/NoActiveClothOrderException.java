package pl.bratosz.smartlockers.exception;

public class NoActiveClothOrderException extends ClothOrderException {
    public NoActiveClothOrderException(String message) {
        super(message);
    }
}
