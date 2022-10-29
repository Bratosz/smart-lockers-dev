package pl.bratosz.smartlockers.utils.string;

import pl.bratosz.smartlockers.service.jgenderize.model.Gender;
import pl.bratosz.smartlockers.service.jgenderize.model.NameGender;

import java.util.List;

public class EmployeeNameAndGender {
    private final String firstName;
    private final String lastName;
    private final Gender gender;

    private EmployeeNameAndGender(String lastName, String firstName, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public static EmployeeNameAndGender createForGenderUnknown(List<NameGender> genders) {
        String lastName = genders.get(0).getName();
        String firstName = genders.get(1).getName();
        return new EmployeeNameAndGender(lastName, firstName, Gender.NULL);
    }

    public static EmployeeNameAndGender create(String lastName, String firstName, Gender gender) {
        return new EmployeeNameAndGender(lastName, firstName, gender);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }
}
