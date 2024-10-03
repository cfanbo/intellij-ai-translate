package org.intellij.sdk.editor.config;

public class Options {
    public Double temperature;
    public Integer maxTokens;
    public Double topP;
    public Integer n;
    public String stop;
    public Boolean stream;

    public Options() {
        temperature = (double) 0;
        maxTokens = 1024;
        topP = 1.0;
        stream = false;
    }

    public Options(Double temperature, Integer maxTokens, Double topP, Integer n, String stop, Boolean stream) {
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.topP = topP;
        this.n = n;
        this.stop = stop;
        this.stream = stream;
        System.out.println("Options: " + temperature + ", " + maxTokens + ", " + topP + ", " + n + ", " + stop + ", " + stream);
    }

    public Options setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    public Options setTemperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public Options setTopP(Double topP) {
        this.topP = topP;
        return this;
    }

    public Options setN(Integer n) {
        this.n = n;
        return this;
    }

    public Options setStop(String stop) {
        this.stop = stop;
        return this;
    }

    public Options setStream(Boolean stream) {
        this.stream = stream;
        return this;
    }

    public static Options fromConfig(org.intellij.sdk.editor.settings.AppSettings.State config) {
        return new Options(config.llmConfig.temperature / 100.0, config.llmConfig.maxTokens, 1.0, 1, "", true);
    }
}
