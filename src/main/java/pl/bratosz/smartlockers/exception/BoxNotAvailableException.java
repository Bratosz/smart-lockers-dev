package pl.bratosz.smartlockers.exception;

import pl.bratosz.smartlockers.repository.EmployeesRepository;

public class BoxNotAvailableException extends Exception {
    public BoxNotAvailableException() {
    }

    public BoxNotAvailableException(String message) {
        super(message);
    }
}
