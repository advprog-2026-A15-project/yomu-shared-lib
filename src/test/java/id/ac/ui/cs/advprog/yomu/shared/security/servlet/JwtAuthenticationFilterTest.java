package id.ac.ui.cs.advprog.yomu.shared.security.servlet;

import id.ac.ui.cs.advprog.yomu.shared.security.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFilterWithGatewayHeaders() throws Exception {
        request.addHeader("X-User-Id", "user123");
        request.addHeader("X-User-Username", "testuser");
        request.addHeader("X-User-Role", "USER");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user123", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void testFilterWithoutTokenAndHeaders() throws Exception {
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterWithValidJwt() throws Exception {
        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.isAccessTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("testuser");
        when(jwtService.extractRole("valid-token")).thenReturn("USER");
        when(jwtService.extractUserId("valid-token")).thenReturn("user123");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user123", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void testFilterWithInvalidJwt() throws Exception {
        request.addHeader("Authorization", "Bearer invalid-token");

        when(jwtService.isAccessTokenValid("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterWithJwtServiceException() throws Exception {
        request.addHeader("Authorization", "Bearer bad-token");

        when(jwtService.isAccessTokenValid("bad-token")).thenThrow(new RuntimeException("Error parsing token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterWithPartialGatewayHeaders() throws Exception {
        request.addHeader("X-User-Id", "user123");
        // headerUsername and headerRole absent → condition false → falls to JWT path → no JWT → no auth

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterWithNonBearerAuthHeader() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilterWithValidJwtButAlreadyAuthenticated() throws Exception {
        Authentication existingAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.isAccessTokenValid("valid-token")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource(value = {
            "null, USER, user123",
            "testuser, null, user123",
            "testuser, USER, null"
    }, nullValues = "null")
    void testFilterWithValidJwtButMissingClaims(String username, String role, String userId) throws Exception {
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.isAccessTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn(username);
        when(jwtService.extractRole("valid-token")).thenReturn(role);
        when(jwtService.extractUserId("valid-token")).thenReturn(userId);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
