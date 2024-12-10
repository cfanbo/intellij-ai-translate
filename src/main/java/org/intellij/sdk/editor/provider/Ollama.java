package org.intellij.sdk.editor.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class Ollama extends Base implements LLmService {

    public Ollama(AppSettings.State conf) throws ConfigurationException, IOException {
        super(conf);
    }

    @Override
    protected HttpUriRequestBase getHttpRequest() throws JsonProcessingException, URISyntaxException {
        this.httpRequest = new HttpUriRequestBase("POST", URI.create(this.baseConfig.baseUrl + "/api/chat"));
        this.withHttpRequestHeaderAndBody(this.httpRequest);
        return this.httpRequest;
    }

    public void callAgentApp(String text) throws IOException {
        this.setText(text);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpUriRequestBase httpRequest = this.getHttpRequest(); //getHttpGetRequest
            httpRequest.removeHeaders("Authorization");

            if (!config.streamStatus) {
                // 非流式输出
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
        int statusCode = response.getCode();
        if (statusCode == 200) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(responseBody);
            String content = jsonObject.getJSONObject("message").getString("content");
            ApplicationManager.getApplication().invokeLater(() -> {
                Helper.printToConsole(content);
            });
        } else {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println(responseBody);
            throw new RuntimeException("Unexpected response code: " + statusCode);
        }


        return null;
    }

    /**

     */
    @Override
    protected void streamResponseHandler(CloseableHttpResponse httpResponse) throws IOException {
//
/*
     {
         "model" : "qwen2.5:0.5b-instruct",
         "created_at" : "2024-10-16T08:54:26.044297Z",
         "message" : {
             "role" : "assistant",
             "content" : "未"
         },
         "done" : false
     }
         */
        int statusCode = httpResponse.getCode();
        if (statusCode >= 200 && statusCode < 300) {
            InputStream responseStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String responseBody = line.trim();

                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (jsonObject.containsKey("done")) {
                    boolean done = jsonObject.getBoolean("done");
                    if (!done) {
                        String content = jsonObject.getJSONObject("message").getString("content");
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Helper.printToConsole(content);
                        });
                    }
                }
            }
        } else {
            throw new RuntimeException("Unexpected response code: " + statusCode);
        }
    }


    public static String getRequest(String url) {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 HttpGet 请求
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public List<String> getModelList(String apiURL) {
        String jsonString = Ollama.getRequest(apiURL + "/api/tags");;
        JSONObject jsonObject = JSON.parseObject(jsonString);
        JSONArray modelsArray = jsonObject.getJSONArray("models");

        // 提取 name 字段
        List<String> names = new ArrayList<>();
        for (int i = 0; i < modelsArray.size(); i++) {
            JSONObject model = modelsArray.getJSONObject(i);
            String name = model.getString("name");
            names.add(name);
        }
        return names;
    }

}