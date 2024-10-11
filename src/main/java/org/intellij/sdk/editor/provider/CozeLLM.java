package org.intellij.sdk.editor.provider;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.message.StatusLine;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.LLmService;
import org.intellij.sdk.editor.config.LlmConfig;
import org.intellij.sdk.editor.config.Store;
import org.intellij.sdk.editor.config.StoreRecord;
import org.intellij.sdk.editor.http.response.ChatApiResponse;
import org.intellij.sdk.editor.http.response.MessageListApiResponse;
import org.intellij.sdk.editor.http.response.RetrieveApiResponse;
import org.intellij.sdk.editor.http.response.StreamChatApiResponse;
import org.intellij.sdk.editor.settings.AppSettings;
import org.intellij.sdk.editor.util.Helper;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CozeLLM implements LLmService {
    private AppSettings.State config;
    private CozeLLM llm;

    private String providerName = "Coze";
    private StoreRecord record;

    private String botId = "";
    private String token = "";

    public CozeLLM(AppSettings.State config) throws ConfigurationException {
        this.record = Store.getInstance().getRecord(providerName);
        if (record.models.isEmpty()) {
            throw new ConfigurationException("Coze BotID is empty");
        }
        if (record.apiKey.isEmpty()) {
            throw new ConfigurationException("Coze Token is empty");
        }

        // bot and token
        this.botId = record.models.get(0);
        this.token = record.apiKey;

        this.config = config;

        this.llm = this;
    }

    public void callAgentApp(String prompt)
            throws ApiException, NoApiKeyException, InputRequiredException {

        try {
            llm.createChat(prompt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createChat(String prompt) {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost("https://api.coze.cn/v3/chat");
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Content-Type", "application/json");

//            System.out.println("Executing request " + httpPost.getMethod() + " " + httpPost.getUri());

            // 准备JSON数据
            // 创建 ObjectMapper 对象
            ObjectMapper objectMapper = new ObjectMapper();

            // 创建一个包含所有数据的 Map
            Map<String, Object> data = new HashMap<>();
            data.put("bot_id", botId);
            data.put("user_id", "ai-translate");
            data.put("stream", config.streamStatus);
            data.put("auto_save_history", true);

            // 创建 additional_messages 对象
            Map<String, Object> additionalMessage = new HashMap<>();
            additionalMessage.put("role", "user");
            additionalMessage.put("content", prompt);
            additionalMessage.put("content_type", "text");

            // 将 additional_messages 放入数组中
            Map<String, Object>[] additionalMessages = new Map[]{additionalMessage};

            // 将数组放入主数据对象中
            data.put("additional_messages", Arrays.asList(additionalMessages));

            // 转换为 JSON 字符串
            String json = objectMapper.writeValueAsString(data);

            HttpEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            // 根据流式输出标志进行处理
            if (!config.streamStatus) {
                // 非流式输出
                final ChatResult result = httpclient.execute(httpPost, response -> {
                    System.out.println("----------------------------------------");
                    System.out.println(httpPost + "->" + new StatusLine(response));

                    String responseBody = EntityUtils.toString(response.getEntity());
                    ChatApiResponse apiResponse = JSON.parseObject(responseBody, ChatApiResponse.class);

                    return new ChatResult(response.getCode(), apiResponse);
                });


                String conversationId = result.apiResponse.getData().getConversationId();
                String chatId = result.apiResponse.getData().getId();

                // block bot completecd
                if ("in_progress".equals(result.apiResponse.getData().getStatus())) {
                    llm.blockBotUntilCompleted(conversationId, chatId);
                }

                String text = llm.getBotReply(conversationId, chatId);
                Helper.printToConsole(text);

                Helper.printFinished();
            } else {
                // 流式输出
                InputStream responseStream = null;
                try (CloseableHttpResponse httpResponse = httpclient.execute(httpPost)) {
                    int statusCode = httpResponse.getCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        responseStream = httpResponse.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                continue;
                            }

                            String eventStr = line.trim();
                            if (eventStr.startsWith("event:")) {
                                String event = eventStr.substring("event:".length());

                                if ("conversation.message.delta".equals(event)) {
                                    // 获取数据
                                    String dataStr = reader.readLine().trim();
                                    System.out.println(event);
                                    System.out.println(dataStr);
                                    StreamChatApiResponse apiResponse = JSON.parseObject(dataStr.substring("data:".length()), StreamChatApiResponse.class);
                                    if (apiResponse != null && apiResponse.getContent() != null) {
                                        System.out.println(apiResponse.getContent());

                                        ApplicationManager.getApplication().invokeLater(() -> {
                                            Helper.printToConsole(apiResponse.getContent());
                                        });
                                    }
                                }
                            }
                        }
                    } else {
                        throw new RuntimeException("Unexpected response code: " + statusCode);
                    }
                } finally {
                    if (responseStream != null) {
                        responseStream.close();
                    }

                    ApplicationManager.getApplication().invokeLater(() -> {
                        Helper.printFinished();
                    });

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void blockBotUntilCompleted(String conversation_id, String chat_id) {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            URI uri = new URIBuilder("https://api.coze.cn/v3/chat/retrieve")
                    .addParameter("conversation_id", conversation_id)
                    .addParameter("chat_id", chat_id)
                    .build();

            final HttpGet httpget = new HttpGet(uri);
            httpget.setHeader("Authorization", "Bearer " + token);
            httpget.setHeader("Content-Type", "application/json");

            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getUri());

            while(true) {
                Boolean next_loop = httpclient.execute(httpget, response -> {
                    System.out.println("----------------------------------------");
                    System.out.println(httpget + "->" + new StatusLine(response));

                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("\nResponse body:");
                    System.out.println(responseBody);

                    RetrieveApiResponse apiResponse = JSON.parseObject(responseBody, RetrieveApiResponse.class);
                    return apiResponse.getData().getStatus().equals("in_progress");
                });

                if (!next_loop) {
                    break;
                }

                Thread.sleep(1000);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getBotReply(String conversation_id, String chat_id) {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            URI uri = new URIBuilder("https://api.coze.cn/v3/chat/message/list")
                    .addParameter("conversation_id", conversation_id)
                    .addParameter("chat_id", chat_id)
                    .build();

            final HttpGet httpget = new HttpGet(uri);
            httpget.setHeader("Authorization", "Bearer " + token);
            httpget.setHeader("Content-Type", "application/json");

            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getUri());

            return httpclient.execute(httpget, response -> {
                System.out.println("----------------------------------------");
                System.out.println(httpget + "->" + new StatusLine(response));

                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("\nResponse body:");
                System.out.println(responseBody);

                MessageListApiResponse apiResponse = JSON.parseObject(responseBody, MessageListApiResponse.class);
                List<MessageListApiResponse.Data> dataList = apiResponse.getData();

                for (MessageListApiResponse.Data data : dataList) {
                    if ("assistant".equals(data.getRole()) && "answer".equals(data.getType())) {
                        return data.getContent();
                    }
                }

                return "";
            });

        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static class ChatResult {
        final int status;
        final ChatApiResponse apiResponse;

        ChatResult(final int status, final ChatApiResponse apiResponse) {
            this.status = status;
            this.apiResponse = apiResponse;
        }
    }

    static class RetrieveResult {
        final int status;
        final RetrieveApiResponse apiResponse;

        RetrieveResult(final int status, final RetrieveApiResponse apiResponse) {
            this.status = status;
            this.apiResponse = apiResponse;
        }
    }
}
