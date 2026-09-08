package hennovo_backend.shared.logging;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogHubLoggingFilter extends OncePerRequestFilter {

    private final LogHubClient logHubClient;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {

            filterChain.doFilter(request, response);

        } finally {

            long duration = System.currentTimeMillis() - start;

            int statusCode = response.getStatus();

            String logLevel = determineLogLevel(statusCode);

            String message = request.getMethod()
                    + " "
                    + request.getRequestURI();

            logHubClient.sendLog(
                    message,
                    logLevel,
                    statusCode,
                    duration
            );
        }
    }

    private String determineLogLevel(int statusCode) {

        if (statusCode >= 500) {
            return "ERROR";
        }

        if (statusCode >= 400) {
            return "WARNING";
        }

        return "INFO";
    }
}