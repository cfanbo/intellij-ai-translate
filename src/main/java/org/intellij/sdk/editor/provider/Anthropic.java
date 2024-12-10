package org.intellij.sdk.editor.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.LLmService;
import org.intellij.sdk.editor.settings.AppSettings;
import org.intellij.sdk.editor.util.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class Anthropic extends Base implements LLmService {

    public Anthropic(AppSettings.State conf) throws ConfigurationException {
        super(conf);
    }

    @Override
    public void callAgentApp(String text) throws IOException {
        this.setText(text);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpUriRequestBase httpRequest = this.getHttpRequest(); //getHttpGetRequest

            httpRequest.setUri(URI.create(config.llmConfig.baseUrl + "/v1/messages"));
            httpRequest.removeHeaders("Authorization");
            httpRequest.addHeader("anthropic-version", "2023-06-01");
            httpRequest.addHeader("x-api-key", baseConfig.apiKey);

            if (!config.streamStatus) {
                httpclient.execute(httpRequest, (HttpClientResponseHandler<Void>) response -> {
                    // 处理响应
                    responseHandler(response);
                    Helper.printFinished();
                    return null;
                });
            } else {
                // stream output
                httpclient.execute(httpRequest, (HttpClientResponseHandler<Void>) response -> {
                    try {
                        // 处理流式响应
//                            streamResponseHandler(response);
                        // 将 ClassicHttpResponse 转换为 CloseableHttpResponse
                        if (response instanceof CloseableHttpResponse) {
                            streamResponseHandler((CloseableHttpResponse) response);
                        } else {
                            throw new IllegalStateException("Response is not an instance of CloseableHttpResponse");
                        }
                    } finally {
                        ApplicationManager.getApplication().invokeLater(Helper::printFinished);
                    }
                    return null;
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected HttpClientResponseHandler responseHandler(ClassicHttpResponse response) throws IOException, ParseException {
        /*

// https://docs.anthropic.com/en/api/messages
{
  "id" : "msg_01JUq5kbRPAnjkHkfiSnFafR",
  "type" : "message",
  "role" : "assistant",
  "model" : "claude-3-haiku-20240307",
  "content" : [ {
    "type" : "text",
    "text" : "動的に"
  } ],
  "stop_reason" : "end_turn",
  "stop_sequence" : null,
  "usage" : {
    "input_tokens" : 88,
    "output_tokens" : 8
  }
}
         */
        int statusCode = response.getCode();
        if (statusCode == 200) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.containsKey("content")) {
                JSONObject choice = jsonObject.getJSONArray("content").getJSONObject(0);
                String content = choice.getString("text");
                Helper.printToConsole(content);
            } else {
                throw new RuntimeException("Data parsing Error");
            }
        } else {
            throw new RuntimeException("Unexpected response code: " + statusCode);
        }


        return null;
    }

    @Override
    protected void streamResponseHandler(CloseableHttpResponse httpResponse) throws IOException {
// https://docs.anthropic.com/en/api/messages-streaming
/*

Event types
    - Ping events
    - Error events
    - Other events

Each Stream Event flow:
    - message_start
    - content_block_start -> content_block_delta -> content_block_stop
    - message_delta ...
    - message_stop

// example:

event: message_start
data: {"type": "message_start", "message": {"id": "msg_1nZdL29xx5MUA1yADyHTEsnR8uuvGzszyY", "type": "message", "role": "assistant", "content": [], "model": "claude-3-5-sonnet-20240620", "stop_reason": null, "stop_sequence": null, "usage": {"input_tokens": 25, "output_tokens": 1}}}

event: content_block_start
data: {"type": "content_block_start", "index": 0, "content_block": {"type": "text", "text": ""}}

event: ping
data: {"type": "ping"}

event: content_block_delta
data: {"type": "content_block_delta", "index": 0, "delta": {"type": "text_delta", "text": "Hello"}}

event: content_block_delta
data: {"type": "content_block_delta", "index": 0, "delta": {"type": "text_delta", "text": "!"}}

event: content_block_stop
data: {"type": "content_block_stop", "index": 0}

event: message_delta
data: {"type": "message_delta", "delta": {"stop_reason": "end_turn", "stop_sequence":null}, "usage": {"output_tokens": 15}}

event: message_stop
data: {"type": "message_stop"}
*/
        int statusCode = httpResponse.getCode();
        if (statusCode >= 200 && statusCode < 300) {
            InputStream responseStream = null;
            responseStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if ("event: content_block_delta".equals(line)) {
                    line = reader.readLine().trim();
                    line = line.substring("data:".length());

                    JSONObject jsonObject = JSON.parseObject(line);
                    String content = jsonObject.getJSONObject("delta").getString("text");
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Helper.printToConsole(content);
                    });

                } else {
                    reader.readLine();
                }
            }
        } else {
            throw new RuntimeException("Unexpected response code: " + statusCode);
        }
    }


}
