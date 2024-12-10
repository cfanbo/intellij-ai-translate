package org.intellij.sdk.editor.provider;

import com.intellij.openapi.application.ApplicationManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.LLmService;
import org.intellij.sdk.editor.settings.AppSettings;
import org.intellij.sdk.editor.util.Helper;

import java.io.IOException;


public class OpenAI extends Base implements LLmService {

    public OpenAI(AppSettings.State conf) throws ConfigurationException, IOException {
        super(conf);
    }

    public void callAgentApp(String text) throws IOException {
        this.setText(text);

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // 获取请求对象
            ClassicHttpRequest httpRequest = this.getHttpRequest(); // 假设 getHttpRequest() 返回一个 ClassicHttpRequest

            if (!config.streamStatus) {
                // 非流式输出
                httpclient.execute(httpRequest, (HttpClientResponseHandler<Void>) response -> {
                    // 处理响应
                    responseHandler(response);
                    Helper.printFinished();
                    return null;
                });
            } else {
                // 流式输出
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

}