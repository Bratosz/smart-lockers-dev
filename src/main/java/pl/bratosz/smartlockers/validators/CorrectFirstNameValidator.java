package pl.bratosz.smartlockers.validators;

import pl.bratosz.smartlockers.validators.annotations.CorrectFirstName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CorrectFirstNameValidator implements ConstraintValidator<
        CorrectFirstName, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return (s != null) && (StringValidator.isStringContainLettersAndSpacesOnly(s));
    }
}

