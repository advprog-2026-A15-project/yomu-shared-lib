package id.ac.ui.cs.advprog.yomu.shared.communication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(CommunicationConfig.class);

    @Test
    void testRestClientBuilderBeanExists() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestClient.Builder.class);
        });
    }
}
