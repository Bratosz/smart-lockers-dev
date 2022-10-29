package pl.bratosz.smartlockers.utils;

import org.junit.jupiter.api.Test;
import pl.bratosz.smartlockers.utils.string.EmployeeNameAndGender;
import pl.bratosz.smartlockers.utils.string.NameExtractor;

class NameExtractorTest {

    @Test
    void shouldCreateEmployees() {
        NameExtractor instance = NameExtractor.getInstance();
        EmployeeNameAndGender e1 = instance.get("JAN NOWAK");
        EmployeeNameAndGender e2 = instance.get("ŁUKASZ NOWAK");
        System.out.println("cokowliek");

    }
}