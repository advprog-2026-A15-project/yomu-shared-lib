package id.ac.ui.cs.advprog.yomu.shared.communication;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalServiceClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    private InternalServiceClient internalServiceClient;

    @BeforeEach
    void setUp() {
        internalServiceClient = new InternalServiceClient(restClientBuilder);
        when(restClientBuilder.clone()).thenReturn(restClientBuilder);
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(mock(RestClient.class));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGetClientWithoutToken() {
        RequestContextHolder.resetRequestAttributes();

        RestClient client = internalServiceClient.getClient("http://localhost:8080");

        assertNotNull(client);
        verify(restClientBuilder, never()).defaultHeader(anyString(), anyString());
    }

    @Test
    void testGetClientWithToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer my-secret-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(restClientBuilder.defaultHeader("Authorization", "Bearer my-secret-token")).thenReturn(restClientBuilder);

        RestClient client = internalServiceClient.getClient("http://localhost:8080");

        assertNotNull(client);
        verify(restClientBuilder).defaultHeader("Authorization", "Bearer my-secret-token");
    }

    @Test
    void testGetClientWithInvalidTokenFormat() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RestClient client = internalServiceClient.getClient("http://localhost:8080");

        assertNotNull(client);
        verify(restClientBuilder, never()).defaultHeader(anyString(), anyString());
    }
}
