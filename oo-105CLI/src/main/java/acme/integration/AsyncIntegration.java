package acme.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AsyncIntegration implements ILLMIntegration{
    private final JmsTemplate jmsTemplate;

    // Injects the queue name from application.properties @Value("${app.queue.name}")
    @Value("${llm.queue.name}")
    private String destination;

    public AsyncIntegration(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    @Override
    public String askAquestion(String message) throws IOException, InterruptedException {
        System.out.println(destination+" ...sending... " + message);
        jmsTemplate.convertAndSend(destination, message);
        return "OK";
    }
}
