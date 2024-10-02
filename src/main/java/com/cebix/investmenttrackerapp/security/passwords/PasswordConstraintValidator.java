package com.cebix.investmenttrackerapp.security.passwords;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    private final Properties props = new Properties();

    public PasswordConstraintValidator() {
        props.put("ILLEGAL_WHITESPACE", "Password can not contain whitespace");
        props.put("TOO_SHORT", "Password must contains at least %1$s characters");
        props.put("TOO_LONG", "Password must contains at least %1$s characters");
        props.put("UPPER_CASE", "Password must contains at least 1 uppercase character");
        props.put("LOWER_CASE", "Password must contains at least 1 lowercase character");
        props.put("NUMERIC", "Password must contains at least 1 number");
        props.put("SPECIAL_CHAR", "Password must contains at least 1 special character");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        PasswordValidator validator = getPasswordValidator();

        RuleResult result = validator.validate(new PasswordData(value));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.join(", ", validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }

    private PasswordValidator getPasswordValidator() {
        MessageResolver resolver = new PropertiesMessageResolver(props);
        return new PasswordValidator(resolver,
                new LengthRule(8, 16),
                new WhitespaceRule(),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
                );
    }
}
