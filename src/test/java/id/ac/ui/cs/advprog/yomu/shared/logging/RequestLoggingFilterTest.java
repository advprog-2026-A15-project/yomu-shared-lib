package id.ac.ui.cs.advprog.yomu.shared.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    @InjectMocks
    private RequestLoggingFilter filter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        ReflectionTestUtils.setField(filter, "serviceName", "test-service");
    }

    @Test
    void testActuatorEndpointNotLogged() throws Exception {
        request.setRequestURI("/actuator/health");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testFaviconNotLogged() throws Exception {
        request.setRequestURI("/favicon.ico");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testSuccessfulGetRequestIsLogged() throws Exception {
        request.setMethod("GET");
        request.setRequestURI("/api/resource");
        request.setQueryString("id=123");
        response.setStatus(200);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testFailedPostRequestIsLogged() throws Exception {
        request.setMethod("POST");
        request.setRequestURI("/api/resource");
        response.setStatus(500);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testClientErrorPutRequestIsLogged() throws Exception {
        request.setMethod("PUT");
        request.setRequestURI("/api/resource");
        response.setStatus(404);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDeleteRequestIsLogged() throws Exception {
        request.setMethod("DELETE");
        request.setRequestURI("/api/resource");
        response.setStatus(204);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
