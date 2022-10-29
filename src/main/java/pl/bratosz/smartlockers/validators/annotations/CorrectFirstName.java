package pl.bratosz.smartlockers.validators.annotations;

import pl.bratosz.smartlockers.validators.CorrectFirstNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CorrectFirstNameValidator.class)
public @interface CorrectFirstName {
    String message() default "{pl.bratosz.smartlockers.CorrectFirstName" +
            ".message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
