package hennovo_backend.shared.logging;

public record LogHubRequest(
        String message,
        String logLevel,
        Long appId,
        Integer statusCode,
        long durationMs
) {
}