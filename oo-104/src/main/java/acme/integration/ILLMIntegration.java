package acme.integration;

import java.io.IOException;

public interface ILLMIntegration {
    String askAquestion(String question) throws IOException, InterruptedException;
}
