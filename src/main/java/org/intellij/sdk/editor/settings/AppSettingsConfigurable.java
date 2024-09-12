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

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        return !mySettingsComponent.getAppId().equals(state.appId) ||
                mySettingsComponent.getAppKey().equals(state.appKey);
    }

    @Override
    public void apply() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        state.appId = mySettingsComponent.getAppId();
        state.appKey = mySettingsComponent.getAppKey();
    }

    @Override
    public void reset() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        mySettingsComponent.setAppId(state.appId);
        mySettingsComponent.setAppKey(state.appKey);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}