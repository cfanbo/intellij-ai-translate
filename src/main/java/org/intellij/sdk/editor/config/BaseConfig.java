package org.intellij.sdk.editor.config;

public class BaseConfig {
    public String provider = "";
    public String baseUrl = "";
    public String apiKey = "";
    public String model = "";
    public String prompt = "";

    public BaseConfig() {

    }

    public BaseConfig(String provider, String baseUrl, String apiKey, String model, String prompt) {
        this.provider = provider;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.prompt = prompt;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
//    abstract void callAgentApp(String text) throws IOException;
}
