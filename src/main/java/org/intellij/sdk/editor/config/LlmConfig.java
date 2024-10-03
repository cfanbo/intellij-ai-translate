package org.intellij.sdk.editor.config;

import java.util.Objects;

public class LlmConfig {
    public String name = "";

    public String provider = "";
    public String baseUrl = "";
    public String apiKey = "";
    public String model = "";
    public String prompt = "";
    public int maxTokens = 1024;
    public double temperature = 0;

    public LlmConfig() {
    }

    public LlmConfig(String provider, String baseUrl, String apiKey, String model, String prompt, int maxTokens,
                     double temperature) {
        this.provider = provider;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.prompt = prompt;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.name = provider + "-" + model;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LlmConfig o = (LlmConfig) obj;
        return Objects.equals(provider, o.provider) &&
                Objects.equals(baseUrl, o.baseUrl) &&
                Objects.equals(apiKey, o.apiKey) &&
                Objects.equals(model, o.model) &&
                Objects.equals(prompt, o.prompt) &&
                maxTokens == o.maxTokens &&
                temperature == o.temperature;
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, baseUrl, apiKey, model, prompt, maxTokens, temperature);
    }
}