package io.onezero.jangpyunham.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IncludeStringArrValidator implements ConstraintValidator<IncludeStringArr, String> {

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    @Override
    public void initialize(IncludeStringArr constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.toLowerCase().equals(ASC) || value.toLowerCase().equals(DESC);
    }
}
