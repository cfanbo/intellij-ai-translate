package org.intellij.sdk.editor;


import org.intellij.sdk.editor.config.LlmConfig;
import org.intellij.sdk.editor.provider.*;
import org.intellij.sdk.editor.settings.AppSettings;

public class LLmFactory {
    public static LLmService getInstance(AppSettings.State config) throws Exception {
        try {
            switch (config.provider) {
                case "bailian":
                    return new BailianLLM(config);
                case "coze":
                    return new CozeLLM(config);
            }

            // v1
            LlmConfig llmConfig = config.llmConfig;
            String provider = llmConfig.provider.toLowerCase();
            switch (provider) {
                case "bailian":
                    return new BailianLLM(config);
                case "coze":
                    return new CozeLLM(config);
//                case "azure":
//                    return new Anthropic(config);
                case "deepl":
                    return new DeepL(config);
                case "anthropic":
                    return new Anthropic(config);
                default:
                    return new OpenAI(config);
//                    throw new Exception("unknow Provider");
            }
        } catch (ConfigurationException ex) {
            System.err.println("ConfigurationException caught: " + ex.getMessage());
            throw ex;
        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
            throw e;
        }
    }
}
