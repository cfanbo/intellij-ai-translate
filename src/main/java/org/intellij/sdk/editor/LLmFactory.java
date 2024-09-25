package org.intellij.sdk.editor;


import org.intellij.sdk.editor.settings.AppSettings;

import org.intellij.sdk.editor.provider.*;

public class LLmFactory{
    public static LLmService getInstance(AppSettings.State config) throws Exception {
        try {
            switch (config.provider) {
                case "bailian":
                    return new BailianLLM(config);
                case "coze":
                    return new CozeLLM(config);
                default:
                    throw new Exception("unknow Provider");
            }
        } catch(ConfigurationException ex) {
            System.err.println("ConfigurationException caught: " + ex.getMessage());
            throw ex;
        } catch(Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
            throw e;
        }
    }
}
