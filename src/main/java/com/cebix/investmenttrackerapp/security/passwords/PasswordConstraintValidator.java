package com.cebix.investmenttrackerapp.security.passwords;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    private final Properties props = new Properties();

    public PasswordConstraintValidator() {
        props.put("ILLEGAL_WHITESPACE", "Password can not contain whitespace");
        props.put("TOO_SHORT", "Password must contain at least %1$s characters");
        props.put("TOO_LONG", "Password must contain at most %2$s characters");
        props.put("INSUFFICIENT_UPPERCASE", "Password must contains at least %1$s uppercase character");
        props.put("INSUFFICIENT_LOWERCASE", "Password must contains at least %1$s lowercase character");
        props.put("INSUFFICIENT_DIGIT", "Password must contains at least %1$s digit character");
        props.put("INSUFFICIENT_SPECIAL", "Password must contains at least %1$s special character");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Password cannot be null").addConstraintViolation();
            }
            return false;
        }

        PasswordValidator validator = getPasswordValidator();
        RuleResult result = validator.validate(new PasswordData(value));

        if (result.isValid()) {
            return true;
        }

        if (context != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.join(", ", validator.getMessages(result)))
                    .addConstraintViolation();
        }
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
