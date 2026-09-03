package hennovo_backend.shared.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoHtmlValidator implements ConstraintValidator<NoHtml, String> {

    // Detecta cualquier apertura de tag tipo <algo> o cierre </algo>
    private static final Pattern TAG_PATTERN = Pattern.compile("<\\s*/?\\s*[a-zA-Z!]");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true; // Dejamos que @NotBlank/@NotNull manejen ese caso aparte
        }

        return !TAG_PATTERN.matcher(value).find();
    }
}
