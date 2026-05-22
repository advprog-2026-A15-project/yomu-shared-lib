package id.ac.ui.cs.advprog.yomu.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {"spring.main.web-application-type=servlet", "yomu.security.bypass=false", "grpc.server.port=-1"})
class SharedSecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSecurityFilterChainBeanExists() {
        SecurityFilterChain chain = applicationContext.getBean("sharedSecurityFilterChain", SecurityFilterChain.class);
        assertNotNull(chain);
    }
}
