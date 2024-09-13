package org.intellij.sdk.editor;

import com.alibaba.dashscope.app.*;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.intellij.sdk.editor.settings.AppSettings;

public class LlmService {
    private String appId;
    private String appKey;

    // 私有化构造函数，防止外部实例化
    private LlmService(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    // 内部类持有单例实例
    private static class Holder {
        // 这里需要确保在应用初始化时，AppSettings 和其配置被正确加载
        private static final LlmService INSTANCE = createInstance();

        // 创建实例的方法
        private static LlmService createInstance() {
            AppSettings settings = AppSettings.getInstance();
            AppSettings.State config = settings.getState();
            return new LlmService(config.appId, config.appKey);
        }
    }

    // 提供公共的获取实例方法
    public static LlmService getInstance() {
        return Holder.INSTANCE;
    }

    public String callAgentApp(String prompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(appKey)
                .appId(appId)
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

//        System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
//                result.getRequestId(), result.getOutput().getText(), result.getOutput().getFinishReason());

        return result.getOutput().getText();
    }
}
