package org.intellij.sdk.editor.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.config.BaseConfig;
import org.intellij.sdk.editor.config.LlmConfig;
import org.intellij.sdk.editor.config.Options;
import org.intellij.sdk.editor.config.Template;
import org.intellij.sdk.editor.settings.AppSettings;
import org.intellij.sdk.editor.util.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

abstract public class Base {
    protected final AppSettings.State config;

    protected final BaseConfig baseConfig;
    protected final Template defaultTemplate = new Template();
    protected final Options options = new Options();

    protected String targetLanguage = "Chinese";
    private String text = "";

    protected HttpUriRequestBase httpRequest;

    public Base(AppSettings.State conf) throws ConfigurationException {
        this.config = conf;

        LlmConfig config = this.config.llmConfig;

        // validate config
        if (config.baseUrl.isEmpty()) {
            throw new ConfigurationException("endpoint cannot be empty");
        }

        if (!config.provider.equals("Ollama") && config.apiKey.isEmpty()) {
            throw new ConfigurationException("apiKey cannot be empty");
        }

        if (!config.provider.equals("DeepL") && config.model.isEmpty()) {
            throw new ConfigurationException("LLM model cannot be empty");
        }

        // set target language
        if (!conf.targetLanguage.isEmpty()) {
            this.setTargetLanguage(conf.targetLanguage);
        }

        // base config
        this.baseConfig = new BaseConfig(
                config.provider,
                config.baseUrl,
                config.apiKey,
                config.model,
                config.prompt.isEmpty() ? this.defaultTemplate.prompt : config.prompt
        );

        if (config.maxTokens > 0) {
            options.maxTokens = config.maxTokens;
        }
        options.temperature = config.temperature;
    }

    protected HttpUriRequestBase getHttpRequest() throws JsonProcessingException, URISyntaxException {
        this.httpRequest = new HttpUriRequestBase("POST", URI.create(this.baseConfig.baseUrl + "/chat/completions"));
        this.withHttpRequestHeaderAndBody(this.httpRequest);
        return this.httpRequest;
    }

    protected HttpUriRequestBase getHttpGetRequest() {
        this.httpRequest = new HttpUriRequestBase("GET", URI.create(this.baseConfig.baseUrl + "/chat/completions"));
        return this.httpRequest;
    }

    protected void withHttpRequestHeaderAndBody(HttpUriRequestBase httpRequest) throws JsonProcessingException, URISyntaxException {
        this.withHttpRequestHeader(httpRequest);
        this.withHttpRequestBody(httpRequest);
    }

    private void withHttpRequestHeader(HttpUriRequestBase httpRequest) {
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.addHeader("Authorization", "Bearer " + this.baseConfig.apiKey);
    }

    private void withHttpRequestBody(HttpUriRequestBase httpRequest) throws JsonProcessingException, URISyntaxException {
        // request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.llmConfig.model);

        String prompt = this.getPrompt();
        String systemPrompmt = this.getSystemPrompt();

        if (baseConfig.provider.equalsIgnoreCase("anthropic")) {
            requestBody.put("system", systemPrompmt);
            requestBody.put("messages", new Object[]{
                    new HashMap<String, String>() {{
                        put("role", "user");
                        put("content", prompt);
                    }}
            });
        } else {
            requestBody.put("messages", new Object[]{
                    new HashMap<String, String>() {{
                        put("role", "system");
                        put("content", systemPrompmt); // claude 不兼容
                    }},
                    new HashMap<String, String>() {{
                        put("role", "user");
                        put("content", prompt);
                    }}
            });
        }

        if (config.streamStatus) {
            requestBody.put("stream", true);
        } else {
            requestBody.put("stream", false);
        }

        requestBody.put("max_tokens", options.maxTokens);
        requestBody.put("temperature", options.temperature);


        // 将请求体转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        HttpEntity requestEntity = new StringEntity(jsonRequestBody, ContentType.APPLICATION_JSON);
        httpRequest.setEntity(requestEntity);
    }

    final protected void setText(String text) {
        this.text = text;
    }

    final protected void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    protected HttpClientResponseHandler responseHandler(ClassicHttpResponse response) throws IOException, ParseException {
        /*

// https://platform.openai.com/docs/api-reference/making-requests
{
    "id": "chatcmpl-abc123",
    "object": "chat.completion",
    "created": 1677858242,
    "model": "gpt-4o-mini",
    "usage": {
        "prompt_tokens": 13,
        "completion_tokens": 7,
        "total_tokens": 20,
        "completion_tokens_details": {
            "reasoning_tokens": 0
        }
    },
    "choices": [
        {
            "message": {
                "role": "assistant",
                "content": "\n\nThis is a test!"
            },
            "logprobs": null,
            "finish_reason": "stop",
            "index": 0
        }
    ]
}
         */
        int statusCode = response.getCode();
        if (statusCode == 200) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.containsKey("choices")) {
                JSONObject choice = jsonObject.getJSONArray("choices").getJSONObject(0);
                String content = choice.getJSONObject("message").getString("content");
                Helper.printToConsole(content);
            } else {
                throw new RuntimeException("Data parsing Error");
            }
        } else {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println(responseBody);
            throw new RuntimeException("Unexpected response code: " + statusCode);
        }


        return null;
    }

    protected void streamResponseHandler(CloseableHttpResponse httpResponse) throws IOException {
// https://platform.openai.com/docs/api-reference/chat/streaming
/*
data: {"id":"chatcmpl-123","object":"chat.completion.chunk","created":1694268190,"model":"gpt-4o-mini", "system_fingerprint": "fp_44709d6fcb", "choices":[{"index":0,"delta":{"role":"assistant","content":""},"logprobs":null,"finish_reason":null}]}

data: {"id":"chatcmpl-123","object":"chat.completion.chunk","created":1694268190,"model":"gpt-4o-mini", "system_fingerprint": "fp_44709d6fcb", "choices":[{"index":0,"delta":{"content":"Hello"},"logprobs":null,"finish_reason":null}]}

data: [DONE]

data: {
	"id": "chatcmpl-123",
	"object": "chat.completion.chunk",
	"created": 1694268190,
	"model": "gpt-4o-mini",
	"system_fingerprint": "fp_44709d6fcb",
	"choices": [{
		"index": 0,
		"delta": {
			"role": "assistant",
			"content": ""
		},
		"logprobs": null,
		"finish_reason": null
	}]
}
....

{"id":"chatcmpl-123","object":"chat.completion.chunk","created":1694268190,"model":"gpt-4o-mini", "system_fingerprint": "fp_44709d6fcb", "choices":[{"index":0,"delta":{},"logprobs":null,"finish_reason":"stop"}]}

         */
        int statusCode = httpResponse.getCode();
        if (statusCode >= 200 && statusCode < 300) {
            InputStream responseStream = null;
            responseStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String responseBody = line.trim();
                if ("data: [DONE]".equals(responseBody)) {
                    continue;
                }

                responseBody = responseBody.replaceFirst("data: ", "");
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (jsonObject.containsKey("choices")) {
                    JSONObject choice = jsonObject.getJSONArray("choices").getJSONObject(0);
                    String content = choice.getJSONObject("delta").getString("content");
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Helper.printToConsole(content);
                    });
                }
            }
        } else {
            throw new RuntimeException("Unexpected response code: " + statusCode);
        }
    }


//    protected void streamResponseHandler(CloseableHttpResponse httpResponse) throws IOException {
//        int statusCode = httpResponse.getCode();
//        if (statusCode >= 200 && statusCode < 300) {
//            InputStream responseStream = null;
//            responseStream = httpResponse.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if ("".equals(line)) {
//                    continue;
//                }
//
//                String responseBody = line.trim();
//
//                JSONObject jsonObject = JSON.parseObject(responseBody);
//                if (jsonObject.containsKey("choices")) {
//                    JSONObject choice = jsonObject.getJSONArray("choices").getJSONObject(0);
//                    String content = choice.getJSONObject("message").getString("content");
//                    ApplicationManager.getApplication().invokeLater(() -> {
//                        Helper.printToConsole(content);
//                    });
//                }
//            }
//        } else {
//            throw new RuntimeException("Unexpected response code: " + statusCode);
//        }
//    }

    protected String getPrompt() {
        String prompt = this.defaultTemplate != null ? this.defaultTemplate.prompt : "";
        prompt = prompt.replaceAll("\\{\\{to\\}\\}", this.targetLanguage);
        prompt = prompt != null ? prompt.replaceAll("\\{\\{text\\}\\}", this.text) : "";

        return prompt;
    }

    protected String getSystemPrompt() {
        String prompt = this.defaultTemplate != null ? this.defaultTemplate.systemPrompt : "";
        prompt = prompt.replaceAll("\\{\\{to\\}\\}", this.getTargetLanguage());

        return prompt;
    }
}
