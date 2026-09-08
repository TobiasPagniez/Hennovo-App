package hennovo_backend.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import hennovo_backend.shared.logging.LogHubClient;

@Configuration
public class LogHubConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public LogHubClient logHubClient(RestClient.Builder restClientBuilder,
                                     @Value("${loghub.url}") String url,
                                     @Value("${loghub.api-key:}") String apiKey,
                                     @Value("${loghub.app-id:}") String appId) {
        return new LogHubClient(restClientBuilder, url, apiKey, appId);
    }
}