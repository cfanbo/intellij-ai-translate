// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.intellij.sdk.editor.settings;

import com.intellij.openapi.options.Configurable;
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
                !appSettingsComponent.getAppId().equals(state.appId) ||
                !appSettingsComponent.getAppKey().equals(state.appKey) ||
                !appSettingsComponent.getCozeToken().equals(state.cozeToken) ||
                !appSettingsComponent.getCozeBotID().equals(state.cozeBotID);
    }

    @Override
    public void apply() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        state.provider = appSettingsComponent.getProvider();
        state.appId = appSettingsComponent.getAppId();
        state.appKey = appSettingsComponent.getAppKey();

        state.cozeBotID = appSettingsComponent.getCozeBotID();
        state.cozeToken = appSettingsComponent.getCozeToken();
    }

    @Override
    public void reset() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        appSettingsComponent.setProvider(state.provider);
        appSettingsComponent.setAppId(state.appId);
        appSettingsComponent.setAppKey(state.appKey);

        appSettingsComponent.setCozeBotID(state.cozeBotID);
        appSettingsComponent.setCozeToken(state.cozeToken);
    }

    @Override
    public void disposeUIResources() {
        appSettingsComponent = null;
    }

}