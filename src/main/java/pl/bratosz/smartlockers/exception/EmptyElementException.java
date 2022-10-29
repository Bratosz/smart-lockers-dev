package pl.bratosz.smartlockers.exception;

import java.util.function.Supplier;

public class EmptyElementException extends Exception {
    public EmptyElementException(String message) {
        super(message);
    }
}
