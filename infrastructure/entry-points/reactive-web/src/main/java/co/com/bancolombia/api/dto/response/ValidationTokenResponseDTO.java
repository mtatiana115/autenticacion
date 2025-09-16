package co.com.bancolombia.api.dto.response;

public record ValidationTokenResponseDTO(
        String rolName,
        String email,
        String documentId,
        String name
) {
}
