package hennovo_backend.shared.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LogHubClient {

    private final RestClient restClient;
    private final String apiKey;
    private final Long appId;

    public LogHubClient(
            RestClient.Builder restClientBuilder,
            @Value("${loghub.url}") String url,
            @Value("${loghub.api-key:}") String apiKey,
            @Value("${loghub.app-id:}") String appId) {

        this.restClient = restClientBuilder
                .baseUrl(url)
                .build();

        this.apiKey = apiKey;

        Long resolvedAppId = null;
        if (apiKey != null && !apiKey.isBlank() && appId != null && !appId.isBlank()) {
            try {
                resolvedAppId = Long.valueOf(appId);
            } catch (NumberFormatException e) {
                resolvedAppId = null;
            }
        }
        this.appId = resolvedAppId;
    }

    public void sendLog(
            String message,
            String logLevel,
            int statusCode,
            long durationMs) {

        if (apiKey == null || apiKey.isBlank() || appId == null) {
            return;
        }

        try {

            LogHubRequest request = new LogHubRequest(
                    message,
                    logLevel,
                    appId,
                    statusCode,
                    durationMs
            );

            restClient.post()
                    .uri("/logs")
                    .header("X-API-KEY", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {

            System.err.println(
                    "No se pudo enviar el log a LogHub: "
                            + e.getMessage()
            );
        }
    }
}