package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.dto.request.SignInDTO;
import co.com.bancolombia.api.dto.request.ValidateTokenDTO;
import co.com.bancolombia.api.dto.response.AuthResponseDTO;
import co.com.bancolombia.api.dto.response.UserRecordResponse;
import co.com.bancolombia.api.handler.AuthHandler;
import co.com.bancolombia.api.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "saveUseCase",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Create a new user",
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            description = "Creates a new user and returns its representation",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateUserRecord.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User successfully created",
                                            content = @Content(schema = @Schema(implementation = UserRecordResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "409", description = "Conflict (duplicate resource)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/email/{email}",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "findUserByEmail",
                    operation = @Operation(
                            operationId = "findUserByEmail",
                            summary = "Find a user by email",
                            description = "Finds a user and returns its representation",
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            parameters = {
                                    @Parameter(name = "email", required = true, in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User successfully found",
                                            content = @Content(schema = @Schema(implementation = UserRecordResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "User not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route()
                .POST("/api/v1/users", userHandler::saveUseCase)
                .GET("/api/v1/users/email/{email}/exists", userHandler::existsUserByEmailUseCase)
                .GET("/api/v1/users/email/{email}", userHandler::findUserByEmail)
                .build();
    }

    @RouterOperations({@RouterOperation(
            path = "/api/v1/login",
            method = RequestMethod.POST,
            beanClass = AuthHandler.class,
            beanMethod = "listenSignIn",
            operation = @Operation(
                    operationId = "login",
                    summary = "User login",
                    description = "Authenticates a user and returns an authentication token",
                    requestBody = @RequestBody(
                            required = true,
                            description = "The user's credentials (email and password)",
                            content = @Content(schema = @Schema(implementation = SignInDTO.class))
                    ),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Login successful",
                                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
                            ),
                            @ApiResponse(
                                    responseCode = "401",
                                    description = "Unauthorized - Invalid credentials"
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Bad Request - Missing or invalid fields"
                            )
                    }
            )
    ),
            @RouterOperation(
                    path = "/api/v1/token",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "validateToken",
                    operation = @Operation(
                            operationId = "isValid",
                            summary = "Get information abouth token",
                            description = "Validate an authentication token",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "token",
                                    content = @Content(schema = @Schema(implementation = ValidateTokenDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "is valid successful",
                                            content = @Content(schema = @Schema(implementation = SignInDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Unauthorized - Invalid token"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Bad Request - Missing or invalid fields"
                                    )
                            }
                    )
            )})
    @Bean
    public RouterFunction<ServerResponse> routerAuthFunction(AuthHandler authHandler) {
        return route()
                .POST("/api/v1/login", authHandler::listenSignIn)
                .POST("/api/v1/token", authHandler::validateToken)
                .build();
    }
}
