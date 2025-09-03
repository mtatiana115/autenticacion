package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.dto.request.SignInDTO;
import co.com.bancolombia.api.dto.response.UserRecordResponse;
import co.com.bancolombia.api.handler.AuthHandler;
import co.com.bancolombia.api.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
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
            )
    })
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route()
                .POST("/api/v1/users", userHandler::saveUseCase)
                .GET("/api/v1/users/email/{email}/exists", userHandler::existsUserByEmailUseCase).build();

    }
    @Bean
    @RouterOperation(
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
                                    content = @Content(schema = @Schema(implementation = SignInDTO.class))
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
    )

    public RouterFunction<ServerResponse> routerAuthFunction (AuthHandler authHandler){
        return route()
                .POST("/api/v1/login", authHandler::listenSignIn).build();
    }
}
