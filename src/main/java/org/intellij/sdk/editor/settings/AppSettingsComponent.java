// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.editor.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField appId = new JBTextField();
    private final JBTextField appKey = new JBTextField();
//    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("IntelliJ IDEA user");

    public AppSettingsComponent() {
        // 创建标题分隔符
        TitledSeparator titledSeparator = new TitledSeparator("通义千问应用");

        // 创建可点击的超链接
        JEditorPane linkPane = createHyperlinkPane();


        // 创建表单内容
        JPanel formContent = FormBuilder.createFormBuilder()
                .addComponent(titledSeparator)
                .addLabeledComponent(new JBLabel("App ID:"), appId, 1, false)
                .addLabeledComponent(new JBLabel("App Key:"), appKey, 2, false)
                .addComponent(linkPane, 3)
//                .addComponent(new JBLabel("通义千问应用 appId 和 appKey 获取地址：https://bailian.console.aliyun.com/#/app-center"), 3)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        // 创建主面板
        myMainPanel = new JPanel(new BorderLayout());
        myMainPanel.add(formContent, BorderLayout.CENTER);
    }

    private JEditorPane createHyperlinkPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setText("<html>获取通义千问应用 appId 和 appKey 地址：<a href=\"https://bailian.console.aliyun.com/#/app-center\">https://bailian.console.aliyun.com/#/app-center</a></html>");

        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return editorPane;
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