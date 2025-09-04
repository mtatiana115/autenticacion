package co.com.bancolombia.api.exception;

import co.com.bancolombia.model.user.exception.EmailAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.security.SignatureException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleDuplicateEmailException(EmailAlreadyExistsException ex) {
        log.warn("Duplicate email detected -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(409);
        problem.setTitle("Duplicate Email");
        problem.setDetail(ex.getMessage());
        log.info("Returning 409 Conflict for duplicate email");
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(problem));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleValidationException(ValidationException ex) {
        log.warn("⚠DTO validation error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        log.info("Returning 400 Bad Request for DTO validation error");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Validation error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        log.info("Returning 400 Bad Request for IllegalArgumentException");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }

    @ExceptionHandler(DecodingException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleDecodingException(DecodingException ex) {
        log.error("Request deserialization error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail("Invalid request format");
        log.info("Returning 400 Bad Request for DecodingException");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleResponseStatusException(ResponseStatusException ex) {
        if (ex.getStatusCode().value() == 404) {
            log.error("Resource not found -> {}", ex.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(404);
            problem.setTitle("Not Found");
            problem.setDetail("The requested resource does not exist");
            log.info("Returning 404 Not Found");
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem));
        }

        log.error("Response status exception -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setTitle("Error");
        problem.setDetail("An unexpected error occurred while processing the request");
        log.info("Returning {} ResponseStatusException", ex.getStatusCode().value());
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(problem));
    }

    @ExceptionHandler(R2dbcException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleR2dbcBadGrammarException(R2dbcException ex) {
        log.error("Database error occurred -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("A server error occurred");
        log.info("Returning 500 Internal Server Error for R2dbcException");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem));
    }

    @ExceptionHandler(ConnectException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleConnectException(ConnectException ex) {
        log.error("Database connection failed -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("A server error occurred");
        log.info("Returning 500 Internal Server Error for ConnectException");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred -> {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred");
        log.info("Returning 500 Internal Server Error for generic exception");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem));
    }

    @ExceptionHandler({
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            SignatureException.class
    })
    public Mono<ResponseEntity<ProblemDetail>> handleJwtExceptions(Exception ex) {
        log.warn("JWT validation error -> {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Unauthorized");
        problem.setDetail("Invalid or expired JWT token");
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem));
    }
}