package co.com.bancolombia.api.exception;

import co.com.bancolombia.model.user.exception.EmailAlreadyExistsException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleDuplicateEmailException(EmailAlreadyExistsException ex) {
        log.warn("Duplicate email detected -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(409);
        problem.setTitle("Duplicate Email");
        problem.setDetail(ex.getMessage());
        log.info("Returning 409 Conflict for duplicate email");
        return problem;
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        log.warn("⚠DTO validation error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        log.info("Returning 400 Bad Request for DTO validation error");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Validation error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        log.info("Returning 400 Bad Request for IllegalArgumentException");
        return problem;
    }

    @ExceptionHandler(DecodingException.class)
    public ProblemDetail handleDecodingException(DecodingException ex) {
        log.error("Request deserialization error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail("Invalid request format");
        log.info("Returning 400 Bad Request for DecodingException");
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        if (ex.getStatusCode().value() == 404) {
            log.error("Resource not found -> {}", ex.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(404);
            problem.setTitle("Not Found");
            problem.setDetail("The requested resource does not exist");
            log.info("Returning 404 Not Found");
            return problem;
        }
        log.error("Response status exception -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setTitle("Error");
        problem.setDetail("An unexpected error occurred while processing the request");
        log.info("Returning {} ResponseStatusException", ex.getStatusCode().value());
        return problem;
    }

    @ExceptionHandler(R2dbcException.class)
    public ProblemDetail handleR2dbcBadGrammarException(R2dbcException ex) {
        log.error("Database error occurred -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("A server error occurred");
        log.info("Returning 500 Internal Server Error for R2dbcException");
        return problem;
    }

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException ex) {
        log.error("Database connection failed -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("A server error occurred");
        log.info("Returning 500 Internal Server Error for ConnectException");
        return problem;
    }
}
