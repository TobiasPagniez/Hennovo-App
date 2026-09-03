package hennovo_backend.shared.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_SECONDS = 60;
    private static final long INACTIVITY_SECONDS = WINDOW_SECONDS * 2;

    private final Map<String, RequestCounter> clients = new ConcurrentHashMap<>();

    public RateLimitDecision check(String key) {

        long now = Instant.now().getEpochSecond();

        RequestCounter counter = clients.computeIfAbsent(
                key,
                k -> new RequestCounter(now));

        synchronized (counter) {

            counter.lastAccess = now;

            if (now - counter.windowStart >= WINDOW_SECONDS) {
                counter.windowStart = now;
                counter.requests = 0;
            }

            if (counter.requests >= MAX_REQUESTS) {
                long retryAfter = WINDOW_SECONDS - (now - counter.windowStart);
                return new RateLimitDecision(false, Math.max(1, retryAfter));
            }

            counter.requests++;

            return new RateLimitDecision(true, 0);
        }
    }

    public boolean isAllowed(String key) {
        return check(key).allowed();
    }

    @Scheduled(fixedDelayString = "${rate-limit.cleanup-interval-ms:300000}")
    public void removeInactiveClients() {

        long now = Instant.now().getEpochSecond();

        clients.forEach((key, counter) -> {
            synchronized (counter) {
                if (now - counter.lastAccess >= INACTIVITY_SECONDS) {
                    clients.remove(key, counter);
                }
            }
        });
    }

    public record RateLimitDecision(boolean allowed, long retryAfterSeconds) {
    }

    private static class RequestCounter {

        private long windowStart;
        private long lastAccess;
        private int requests;

        public RequestCounter(long windowStart) {
            this.windowStart = windowStart;
            this.lastAccess = windowStart;
            this.requests = 0;
        }
    }
}
