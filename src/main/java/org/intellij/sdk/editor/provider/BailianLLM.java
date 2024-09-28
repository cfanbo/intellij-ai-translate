package org.intellij.sdk.editor.provider;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.intellij.openapi.application.ApplicationManager;
import io.reactivex.Flowable;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.LLmService;
import org.intellij.sdk.editor.settings.AppSettings;
import org.intellij.sdk.editor.util.Helper;

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

    public void callAgentApp(String prompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(config.appKey)
                .appId(config.appId)
                .prompt(prompt)
                .incrementalOutput(config.streamStatus) // 控制在流式输出模式下是否开启增量输出，即后续输出内容是否包含已输出的内容。设置为True时(默认值），将开启增量输出模式。
                .build();

        Application application = new Application();
        if (config.streamStatus) {
            Flowable<ApplicationResult> result = application.streamCall(param);
            // 使用RxJava订阅流式输出
            result.subscribe(
                    appResult -> {
                        // 每次接收到新的数据块时执行的操作
                        System.out.println(appResult.getOutput().getText());
                        // 确保在EDT上执行UI操作
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Helper.printToConsole(appResult.getOutput().getText());
                        });
                    },
                    throwable -> {
                        // 处理订阅过程中可能发生的错误
                        System.err.println("Error processing stream output: " + throwable.getMessage());
                        throw new Exception(throwable.getMessage());
                    },
                    () -> {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Helper.printFinished();
                        });
                        // 当所有数据块都被成功处理后执行的操作
                        System.out.println("Stream completed.");
                    }
            );

            // 流式处理完
        } else {
            // 常规方式处理结果
            ApplicationResult result = application.call(param);
            System.out.println(result.getOutput().getText());
            Helper.printToConsole(result.getOutput().getText());
            Helper.printFinished();
        }
    }
}
