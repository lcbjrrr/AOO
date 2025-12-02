package acme.mq;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentResponse {

    @JsonProperty("text")
    private String generatedContent;

    public String getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(String generatedContent) {
        this.generatedContent = generatedContent;
    }
}
