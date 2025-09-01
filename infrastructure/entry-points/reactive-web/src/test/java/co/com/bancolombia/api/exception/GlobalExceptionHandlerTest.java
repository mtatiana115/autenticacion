package co.com.bancolombia.api.exception;

import co.com.bancolombia.model.user.exception.EmailAlreadyExistsException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

import java.net.ConnectException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleDuplicateEmailException_ShouldReturnConflict() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email already exists");

        StepVerifier.create(handler.handleDuplicateEmailException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(409);
                    assertThat(response.getBody().getTitle()).isEqualTo("Duplicate Email");
                    assertThat(response.getBody().getDetail())
                            .contains("Email already exists");
                })
                .verifyComplete();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException ex = new ValidationException("Invalid data");

        StepVerifier.create(handler.handleValidationException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(400);
                    assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
                    assertThat(response.getBody().getDetail()).isEqualTo("Invalid data");
                })
                .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad param");

        StepVerifier.create(handler.handleIllegalArgumentException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(400);
                    assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
                    assertThat(response.getBody().getDetail()).isEqualTo("Bad param");
                })
                .verifyComplete();
    }

    @Test
    void handleDecodingException_ShouldReturnBadRequest() {
        DecodingException ex = new DecodingException("Cannot decode");

        StepVerifier.create(handler.handleDecodingException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(400);
                    assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
                    assertThat(response.getBody().getDetail()).isEqualTo("Invalid request format");
                })
                .verifyComplete();
    }

    @Test
    void handleResponseStatusException_NotFound_ShouldReturnNotFound() {
        ResponseStatusException ex = new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found");

        StepVerifier.create(handler.handleResponseStatusException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(404);
                    assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
                    assertThat(response.getBody().getDetail()).isEqualTo("The requested resource does not exist");
                })
                .verifyComplete();
    }

    @Test
    void handleResponseStatusException_OtherStatus_ShouldReturnError() {
        ResponseStatusException ex = new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Bad request");

        StepVerifier.create(handler.handleResponseStatusException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(400);
                    assertThat(response.getBody().getTitle()).isEqualTo("Error");
                    assertThat(response.getBody().getDetail()).isEqualTo("An unexpected error occurred while processing the request");
                })
                .verifyComplete();
    }

    @Test
    void handleR2dbcBadGrammarException_ShouldReturnInternalServerError() {
        R2dbcException ex = mock(R2dbcException.class);

        StepVerifier.create(handler.handleR2dbcBadGrammarException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(500);
                    assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
                    assertThat(response.getBody().getDetail()).isEqualTo("A server error occurred");
                })
                .verifyComplete();
    }

    @Test
    void handleConnectException_ShouldReturnInternalServerError() {
        ConnectException ex = new ConnectException("Connection refused");

        StepVerifier.create(handler.handleConnectException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(500);
                    assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
                    assertThat(response.getBody().getDetail()).isEqualTo("A server error occurred");
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected");

        StepVerifier.create(handler.handleGenericException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(500);
                    assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
                    assertThat(response.getBody().getDetail()).isEqualTo("An unexpected error occurred");
                })
                .verifyComplete();
    }
}
