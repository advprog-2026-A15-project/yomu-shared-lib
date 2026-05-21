package id.ac.ui.cs.advprog.yomu.shared.security.servlet;

import id.ac.ui.cs.advprog.yomu.shared.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Check for headers from API Gateway first
        String headerUserId = request.getHeader("X-User-Id");
        String headerUsername = request.getHeader("X-User-Username");
        String headerRole = request.getHeader("X-User-Role");

        if (headerUserId != null && headerUsername != null && headerRole != null) {
            setAuthentication(headerUserId, headerUsername, headerRole, request);
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Fallback to JWT token if headers not present (e.g. direct service call)
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            if (jwtService.isAccessTokenValid(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtService.extractUsername(jwt);
                String role = jwtService.extractRole(jwt); 
                String userId = jwtService.extractUserId(jwt);

                if (username != null && role != null && userId != null) {
                    setAuthentication(userId, username, role, request);
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String userId, String username, String role, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userId, // principal is userId (UUID string)
                userId, // credentials is userId (changed from username to fix controller expectations)
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);     
    }}
