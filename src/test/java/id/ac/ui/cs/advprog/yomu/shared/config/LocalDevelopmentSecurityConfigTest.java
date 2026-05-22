package id.ac.ui.cs.advprog.yomu.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {"spring.main.web-application-type=servlet", "spring.profiles.active=local", "yomu.security.bypass=true", "grpc.server.port=-1"})
class LocalDevelopmentSecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSecurityFilterChainBeanExists() {
        SecurityFilterChain chain = applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
    }
}
