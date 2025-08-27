package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;

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
            operation = @operation(
                    operationId = "guardarUsuario",
                    tags = {"Usuarios"},
                    summary = "Guarda un nuevo usuario",
                    requestBody = @RequestBody(
                            description = "Información del usuario a guardar",
                            required = true,
                            content = @Content(
                                    schema = @Schema(implementation = CrearUsuarioDTO.class)
                            )
                    ),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Usuario guardado exitosamente",
                                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Solicitud inválida",
                                    content = @Content(
                                            schema = @Schema(implementation = ErrorValidacion.class),
                                            examples = @ExampleObject(
                                                    name = "Ejemplo de respuesta de error 400",
                                                    value = "{\"status\": 400,\"message\": \"Correo no esta disponible\",\"errors\": [\"correoElectronico\"]}"
                                            )
                                    )
                            )
                    }
            )
    )

    public RouterFunction<ServerResponse> usuarioRouterFunction() {
    return RouterFunctions.route(POST(PATH).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::guardarUsuario);
    }
}
