package pl.bratosz.smartlockers.exception;

import java.io.IOException;

public class WrongFileFormatException extends IOException {
    public WrongFileFormatException() {
    }

    public WrongFileFormatException(String message){
        super(message);
    }
}
