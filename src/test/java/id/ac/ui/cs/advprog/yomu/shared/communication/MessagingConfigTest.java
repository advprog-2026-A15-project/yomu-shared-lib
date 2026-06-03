package id.ac.ui.cs.advprog.yomu.shared.communication;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.TopicExchange;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class MessagingConfigTest {

    @Test
    @SuppressWarnings({"deprecation", "removal", "java:S5738"})
    void testBeans() {
        MessagingConfig config = new MessagingConfig();
        
        Jackson2JsonMessageConverter converter = config.messageConverter();
        assertNotNull(converter);
        
        TopicExchange exchange = config.eventExchange();
        assertNotNull(exchange);
        
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        assertNotNull(template);
    }
}
