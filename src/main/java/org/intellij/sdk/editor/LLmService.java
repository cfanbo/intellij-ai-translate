package org.intellij.sdk.editor;

import org.intellij.sdk.editor.settings.AppSettings;

public interface LLmService {
    String callAgentApp(String input) throws Exception;
}
