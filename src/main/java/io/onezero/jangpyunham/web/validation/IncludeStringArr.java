package io.onezero.jangpyunham.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IncludeStringArrValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeStringArr {

    String[] checkStrings();
    String message() default "Not include array";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
