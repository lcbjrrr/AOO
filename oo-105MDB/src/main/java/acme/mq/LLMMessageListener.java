
package acme.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;


import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import jakarta.mail.PasswordAuthentication;

import java.util.Properties;

@Component
public class LLMMessageListener {
    @Value( "${model.name}")
    private String MODEL_NAME;
    @Value( "${api.key}")
    private String API_KEY;
    @Value( "${jdbc.url}")
    private String JDBC_URL;

    public LLMMessageListener(){}

    @JmsListener(destination = "${llm.queue.name}")
    public void receiveMessage(String msg) {
        try {
            String[] part = msg.split(",");
            String id = part[0];
            String question = part[1];
            String prompt = "{'contents':[{'parts':[{'text':'" + question + "'}]}]}";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = buildHttpRequest(prompt);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            ContentResponse content = getResponse(response.body());
            System.out.println("====>"+content.getGeneratedContent());
            //return content.getGeneratedContent();
            String sql = "UPDATE papers SET keywords = ? WHERE paper_id = ?";
            Connection conn = DriverManager.getConnection(JDBC_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, content.getGeneratedContent());
            pstmt.setInt(2, Integer.parseInt(id));
            int rows = pstmt.executeUpdate();
            System.out.println("%" + rows+" id "+id);
        } catch (Exception e) {
            System.out.println("####" + e.getMessage());
        }

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


