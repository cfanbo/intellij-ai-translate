package org.intellij.sdk.editor.provider;

import com.intellij.openapi.application.ApplicationManager;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
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
            HttpUriRequestBase httpRequest = this.getHttpRequest(); //getHttpGetRequest
            if (!config.streamStatus) {
                httpclient.execute(httpRequest, response -> this.responseHandler(response));
                Helper.printFinished();
            } else {
                // stream output
                try (CloseableHttpResponse httpResponse = httpclient.execute(httpRequest)) {
                    this.streamResponseHandler(httpResponse);
                } finally {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Helper.printFinished();
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}