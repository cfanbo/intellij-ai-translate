package org.intellij.sdk.editor.provider;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.LLmService;
import org.intellij.sdk.editor.settings.AppSettings;

public class BailianLLM implements LLmService {
    private AppSettings.State config;

    public BailianLLM(AppSettings.State config) throws ConfigurationException {
        if (config.appId.isEmpty()) {
            throw new ConfigurationException("AppId is empty");
        }
        if (config.appKey.isEmpty()) {
            throw new ConfigurationException("AppKey is empty");
        }

        this.config = config;
    }

    public String callAgentApp(String prompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(config.appKey)
                .appId(config.appId)
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

//        System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
//                result.getRequestId(), result.getOutput().getText(), result.getOutput().getFinishReason());

        return result.getOutput().getText();
    }
}
