// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.editor.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField appId = new JBTextField();
    private final JBTextField appKey = new JBTextField();
//    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("IntelliJ IDEA user");

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("App ID:"), appId, 1, false)
                .addLabeledComponent(new JBLabel("App Key:"), appKey, 2, false)
                .addComponent(new JBLabel("Alibaba 大模型通义千问应用 appId 和 appKey，获取地址：https://bailian.console.aliyun.com/#/app-center"), 3)
//                .addComponent(myIdeaUserStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return appId;
    }

    @NotNull
    public String getAppId() {
        return appId.getText();
    }

    public void setAppId(@NotNull String newText) {
        appId.setText(newText);
    }

    @NotNull
    public String getAppKey() {
        return appKey.getText();
    }

    public void setAppKey(@NotNull String newText) {
        appKey.setText(newText);
    }

}