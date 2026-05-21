package id.ac.ui.cs.advprog.yomu.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    // ANSI Colors
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BOLD = "\u001B[1m";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (request.getRequestURI().contains("/actuator") || request.getRequestURI().contains("/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? uri + "?" + queryString : uri;

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            String statusColor = status >= 500 ? RED : (status >= 400 ? YELLOW : GREEN);
            String methodColor = getMethodColor(method);

            log.info("{} {}[{}] {}{}{} {}{}{} {}{}{}ms{} - {}{}{}",
                    PURPLE + BOLD + "[" + serviceName.toUpperCase() + "]" + RESET,
                    methodColor + BOLD, method, RESET,
                    CYAN, fullPath, RESET,
                    statusColor + BOLD, status, RESET,
                    YELLOW, duration, RESET,
                    status >= 400 ? RED + " (FAILED)" + RESET : ""
            );
        }
    }

    private String getMethodColor(String method) {
        return switch (method) {
            case "GET" -> GREEN;
            case "POST" -> CYAN;
            case "PUT", "PATCH" -> YELLOW;
            case "DELETE" -> RED;
            default -> RESET;
        };
    }
}
