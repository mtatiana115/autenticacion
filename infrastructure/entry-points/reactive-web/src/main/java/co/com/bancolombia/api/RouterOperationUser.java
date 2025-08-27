package co.com.bancolombia.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@RequiredArgsConstructor
public class RouterOperationUser {

    public static final String PATH = "/api/v1/users";
    private final Handler handler;

    @Bean
    @RouterOperation(
            path = PATH,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = org.springframework.web.bind.annotation.RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "guardarUsuario",
            operation = @Operation(
                    operationId = "guardarUsuario",
                    tags = {"Usuarios"},
                    summary = "Guarda un nuevo usuario",
                    requestBody = @RequestBody(
                            description = "Información del usuario a guardar",
                            required = true // ,
                            // comentado porque no tienes CrearUsuarioDTO
                            // content = @Content(
                            //         schema = @Schema(implementation = CrearUsuarioDTO.class)
                            // )
                    ),
                    // comentado porque no tienes UsuarioDTO
                    responses = {
                            //@ApiResponse(
                            //        responseCode = "200",
                            //        description = "Usuario guardado exitosamente",
                            //        content = @Content(schema = @Schema(implementation = UsuarioDTO.class))
                            //),
                            // comentado porque no tienes ErrorValidacion
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Solicitud inválida" // ,
                                    // content = @Content(
                                    //         schema = @Schema(implementation = ErrorValidacion.class),
                                    //         examples = @ExampleObject(
                                    //                 name = "Ejemplo de respuesta de error 400",
                                    //                 value = "{\"status\": 400,\"message\": \"Correo no esta disponible\",\"errors\": [\"correoElectronico\"]}"
                                    //         )
                                    // )
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> userRouter() {
        final RequestPredicate postAction = POST(PATH)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.
                route(
                        postAction,
                        handler::saveUseCase
                );
    }
}
