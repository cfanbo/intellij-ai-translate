// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.intellij.sdk.editor.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import org.intellij.sdk.editor.config.Store;
import org.intellij.sdk.editor.config.StoreRecord;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * Provides controller functionality for application settings.
 */
final class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent appSettingsComponent;

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AiTranslate";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return appSettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        appSettingsComponent = new AppSettingsComponent();
        return appSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        return !appSettingsComponent.getProvider().equals(state.provider) ||
                appSettingsComponent.getStreamStatus() != state.streamStatus ||
                !appSettingsComponent.getAppId().equals(state.appId) ||
                !appSettingsComponent.getAppKey().equals(state.appKey) ||
                !appSettingsComponent.getTargetLang().equals(state.targetLanguage) ||
                !appSettingsComponent.getCozeToken().equals(state.cozeToken) ||
                !appSettingsComponent.getCozeBotID().equals(state.cozeBotID) ||
                !appSettingsComponent.getLlmConfig().equals(state.llmConfig);
    }

    @Override
    public void apply() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        state.provider = appSettingsComponent.getProvider();
        state.streamStatus = appSettingsComponent.getStreamStatus();
        state.appId = appSettingsComponent.getAppId();
        state.appKey = appSettingsComponent.getAppKey();

        state.cozeBotID = appSettingsComponent.getCozeBotID();
        state.cozeToken = appSettingsComponent.getCozeToken();

        state.targetLanguage = appSettingsComponent.getTargetLang();
        state.llmConfig = appSettingsComponent.getLlmConfig();

        state.storeString = appSettingsComponent.getStoreString();

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue("store", state.storeString);
    }

    @Override
    public void reset() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        appSettingsComponent.setProvider(state.provider);
        appSettingsComponent.setStreamStatus(state.streamStatus);
        appSettingsComponent.setAppId(state.appId);
        appSettingsComponent.setAppKey(state.appKey);

        appSettingsComponent.setCozeBotID(state.cozeBotID);
        appSettingsComponent.setCozeToken(state.cozeToken);

        appSettingsComponent.setTargetLang(state.targetLanguage);
        appSettingsComponent.setLlmConfig(state.llmConfig);
    }

    @Override
    public void disposeUIResources() {
        appSettingsComponent = null;
    }

}