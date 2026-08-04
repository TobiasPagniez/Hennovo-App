package hennovo_backend.shared.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final List<String> errors;

    public CustomException(
            String message,
            HttpStatus status,
            List<String> errors) {

        super(message);
        this.status = status;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }
}
