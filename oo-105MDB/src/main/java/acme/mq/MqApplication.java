package acme.mq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MqApplication  implements CommandLineRunner {
//    private LLMMessageListener llmListener;
//    private EmailMessageListener emailListener;
//
//    public MqApplication(LLMMessageListener llmListener,EmailMessageListener emailListener) {
//        this.llmListener = llmListener;
//        this.emailListener = emailListener;
//    }
    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("******** Starting Application *********");
    }



}
