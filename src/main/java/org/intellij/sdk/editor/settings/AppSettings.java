// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.editor.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.intellij.sdk.editor.config.LlmConfig;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


@State(
        name = "org.intellij.sdk.editor.settings.AppSettings",
        storages = @Storage("SdkSettingsPlugin.xml")
)
public final class AppSettings
        implements PersistentStateComponent<AppSettings.State> {

    public static class State {

        @NonNls
        public String provider = "";
        public boolean streamStatus = true;
        public boolean automateClearConsoleStatus = false;
//        public String appId = "";
//        public String appKey = "";
//
//        public String cozeBotID = "";
//        public String cozeToken = "";

        public String targetLanguage = "";
        public LlmConfig llmConfig = new LlmConfig();

        // store
        public String storeString = "";
    }

    private State myState = new State();

    public static AppSettings getInstance() {
        return ApplicationManager.getApplication()
                .getService(AppSettings.class);
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

}