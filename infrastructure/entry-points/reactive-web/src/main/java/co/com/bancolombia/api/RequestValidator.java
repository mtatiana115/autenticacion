package co.com.bancolombia.api;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validateUser(T dto) {
        return Mono.fromCallable(() -> {
            var errors = new BeanPropertyBindingResult(dto, dto.getClass().getName());
            validator.validate(dto, errors);
            if (errors.hasErrors()) {
                throw new ValidationException(errors.toString());
            }
            return dto;
        });
    }

}
