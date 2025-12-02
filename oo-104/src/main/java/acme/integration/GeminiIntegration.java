package acme.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GeminiIntegration implements ILLMIntegration{

    @Value( "${model.name}")
    private String MODEL_NAME;
    @Value( "${api.key}")
    private String API_KEY;

    @Override
    public String askAquestion(String question) throws IOException, InterruptedException {
        String prompt="{'contents':[{'parts':[{'text':'"+question+"'}]}]}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = buildHttpRequest(prompt);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(response.body());
        ContentResponse content = getResponse(response.body());
        //System.out.println("====>"+content.getGeneratedContent());
        return content.getGeneratedContent();
    }

    private HttpRequest buildHttpRequest(String jsonBody) {
        String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent";
        return HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    private ContentResponse getResponse(String rawJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(rawJson);
        JsonNode node = rootNode
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0);
        ContentResponse obj = mapper.treeToValue(node, ContentResponse.class);
        return obj;
    }

}
